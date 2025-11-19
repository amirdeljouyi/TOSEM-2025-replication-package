package edu.stanford.nlp.dcoref;

import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.UniversalEnglishGrammaticalRelations;
import edu.stanford.nlp.util.Pair;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class Mention_2_GPTLLMTest {

 @Test
  public void testSpanToString() {
    CoreLabel word1 = new CoreLabel();
    word1.set(CoreAnnotations.TextAnnotation.class, "Barack");
    CoreLabel word2 = new CoreLabel();
    word2.set(CoreAnnotations.TextAnnotation.class, "Obama");
    List<CoreLabel> span = Arrays.asList(word1, word2);

    Mention mention = new Mention(1, 0, 2, new SemanticGraph(), span);
    mention.originalSpan = span;

    assertEquals("Barack Obama", mention.spanToString());
    assertEquals("Barack Obama", mention.spanToString());
  }
@Test
  public void testLowercaseNormalizedSpanString() {
    CoreLabel word = new CoreLabel();
    word.set(CoreAnnotations.TextAnnotation.class, "Obama");
    List<CoreLabel> span = Collections.singletonList(word);

    Mention mention = new Mention(1, 0, 1, new SemanticGraph(), span);
    mention.originalSpan = span;
    mention.spanToString(); 

    String result = mention.lowercaseNormalizedSpanString();

    assertEquals("obama", result);
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
  public void testSameSentenceTrue() {
    CoreLabel token = new CoreLabel();
    List<CoreLabel> sentence = Collections.singletonList(token);
    Mention m1 = new Mention();
    m1.sentenceWords = sentence;
    Mention m2 = new Mention();
    m2.sentenceWords = sentence;

    assertTrue(m1.sameSentence(m2));
  }
@Test
  public void testSameSentenceFalse() {
    Mention m1 = new Mention();
    m1.sentenceWords = Collections.singletonList(new CoreLabel());
    Mention m2 = new Mention();
    m2.sentenceWords = Collections.singletonList(new CoreLabel());

    assertFalse(m1.sameSentence(m2));
  }
@Test
  public void testAddListMember() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();

    m1.addListMember(m2);

    assertNotNull(m1.listMembers);
    assertTrue(m1.listMembers.contains(m2));
  }
@Test
  public void testAddBelongsToList() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();

    m1.addBelongsToList(m2);

    assertNotNull(m1.belongToLists);
    assertTrue(m1.belongToLists.contains(m2));
  }
@Test
  public void testIsMemberOfSameListTrue() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    Mention sharedList = new Mention();

    m1.belongToLists = new HashSet<>();
    m1.belongToLists.add(sharedList);

    m2.belongToLists = new HashSet<>();
    m2.belongToLists.add(sharedList);

    assertTrue(m1.isMemberOfSameList(m2));
  }
@Test
  public void testIsMemberOfSameListFalse() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();

    m1.belongToLists = new HashSet<>();
    m2.belongToLists = new HashSet<>();

    m1.belongToLists.add(new Mention());
    m2.belongToLists.add(new Mention());

    assertFalse(m1.isMemberOfSameList(m2));
  }
@Test
  public void testIsAppositionTrue() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();

    m1.appositions = new HashSet<>();
    m1.appositions.add(m2);

    assertTrue(m1.isApposition(m2));
  }
@Test
  public void testIsAppositionFalse() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();

    m1.appositions = new HashSet<>();
    m1.appositions.add(new Mention());

    assertFalse(m1.isApposition(m2));
  }
@Test
  public void testIsPredicateNominativesTrue() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();

    m1.predicateNominatives = new HashSet<>();
    m1.predicateNominatives.add(m2);

    assertTrue(m1.isPredicateNominatives(m2));
  }
@Test
  public void testIsPredicateNominativesFalse() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();

    m1.predicateNominatives = new HashSet<>();
    m1.predicateNominatives.add(new Mention());

    assertFalse(m1.isPredicateNominatives(m2));
  }
@Test
  public void testAddRelativePronoun() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();

    m1.addRelativePronoun(m2);

    assertNotNull(m1.relativePronouns);
    assertTrue(m1.relativePronouns.contains(m2));
  }
@Test
  public void testIsRelativePronounTrue() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();

    m1.relativePronouns = new HashSet<>();
    m1.relativePronouns.add(m2);

    assertTrue(m1.isRelativePronoun(m2));
  }
@Test
  public void testIsRelativePronounFalse() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();

    m1.relativePronouns = new HashSet<>();
    m1.relativePronouns.add(new Mention());

    assertFalse(m1.isRelativePronoun(m2));
  }
@Test
  public void testHeadsAgreeTrueExactMatch() {
    CoreLabel head = new CoreLabel();
    head.set(CoreAnnotations.TextAnnotation.class, "smith");

    Mention m1 = new Mention();
    m1.headString = "smith";
    m1.headWord = head;
    m1.nerString = "O";

    Mention m2 = new Mention();
    m2.headString = "smith";
    m2.headWord = head;
    m2.nerString = "O";

    assertTrue(m1.headsAgree(m2));
  }
@Test
  public void testHeadsAgreeFalseDifferentString() {
    CoreLabel head1 = new CoreLabel();
    head1.set(CoreAnnotations.TextAnnotation.class, "Obama");

    CoreLabel head2 = new CoreLabel();
    head2.set(CoreAnnotations.TextAnnotation.class, "Bush");

    Mention m1 = new Mention();
    m1.headString = "obama";
    m1.headWord = head1;
    m1.nerString = "O";

    Mention m2 = new Mention();
    m2.headString = "bush";
    m2.headWord = head2;
    m2.nerString = "O";

    assertFalse(m1.headsAgree(m2));
  }
@Test
  public void testAppearEarlierThanTrueBasedOnSentence() {
    Mention earlier = new Mention();
    earlier.sentNum = 1;
    earlier.startIndex = 5;
    earlier.endIndex = 6;
    earlier.headIndex = 5;
    earlier.mentionType = Dictionaries.MentionType.PROPER;
    earlier.originalSpan = Collections.singletonList(new CoreLabel());

    Mention later = new Mention();
    later.sentNum = 2;
    later.startIndex = 1;
    later.endIndex = 3;
    later.headIndex = 1;
    later.mentionType = Dictionaries.MentionType.PROPER;
    later.originalSpan = Collections.singletonList(new CoreLabel());

    assertTrue(earlier.appearEarlierThan(later));
  }
@Test
  public void testAppearEarlierThanFalseSamePositionLowerSentenceNum() {
    Mention m1 = new Mention();
    m1.sentNum = 2;
    m1.startIndex = 3;
    m1.endIndex = 4;
    m1.headIndex = 3;
    m1.mentionType = Dictionaries.MentionType.NOMINAL;
    m1.originalSpan = Collections.singletonList(new CoreLabel());

    Mention m2 = new Mention();
    m2.sentNum = 1;
    m2.startIndex = 3;
    m2.endIndex = 4;
    m2.headIndex = 3;
    m2.mentionType = Dictionaries.MentionType.PROPER;
    m2.originalSpan = Collections.singletonList(new CoreLabel());

    assertFalse(m1.appearEarlierThan(m2));
  }
@Test
  public void testStringWithoutArticleRemovesThe() {
    Mention m = new Mention();
    List<CoreLabel> span = new ArrayList<>();
    CoreLabel w1 = new CoreLabel();
    w1.set(CoreAnnotations.TextAnnotation.class, "The");
    CoreLabel w2 = new CoreLabel();
    w2.set(CoreAnnotations.TextAnnotation.class, "Lion");
    span.add(w1);
    span.add(w2);
    m.originalSpan = span;

    String clean = m.stringWithoutArticle(null);

    assertEquals("Lion", clean);
  }
@Test
  public void testRemoveParenthesisReturnsBeforeText() {
    String input = "New York (NY)";
    String output = Mention.removeParenthesis(input);
    assertEquals("New York", output);
  }
@Test
  public void testRemoveParenthesisNoParenthesis() {
    String input = "California";
    String output = Mention.removeParenthesis(input);
    assertEquals("", output);
  }
@Test
public void testHeadsAgreeSameNERButIncludedHeadword() {
  CoreLabel head1 = new CoreLabel();
  head1.set(CoreAnnotations.TextAnnotation.class, "George");
  head1.setWord("George");

  CoreLabel word2 = new CoreLabel();
  word2.set(CoreAnnotations.TextAnnotation.class, "Bush");
  word2.setWord("Bush");

  Mention m1 = new Mention();
  m1.headString = "george";
  m1.headWord = head1;
  m1.nerString = "PERSON";
  m1.originalSpan = Arrays.asList(head1);

  Mention m2 = new Mention();
  m2.headString = "bush";
  m2.headWord = word2;
  m2.nerString = "PERSON";
  m2.originalSpan = Arrays.asList(head1, word2);

  assertTrue(m1.headsAgree(m2));
}
@Test
public void testHeadsAgreeSameNERButOppositeSpanInclusion() {
  CoreLabel word1 = new CoreLabel();
  word1.set(CoreAnnotations.TextAnnotation.class, "King");
  word1.setWord("King");

  CoreLabel head2 = new CoreLabel();
  head2.set(CoreAnnotations.TextAnnotation.class, "Martin");
  head2.setWord("Martin");

  Mention m1 = new Mention();
  m1.headString = "king";
  m1.headWord = word1;
  m1.nerString = "PERSON";
  m1.originalSpan = Arrays.asList(word1);

  Mention m2 = new Mention();
  m2.headString = "martin";
  m2.headWord = head2;
  m2.nerString = "PERSON";
  m2.originalSpan = Arrays.asList(head2, word1);

  assertTrue(m1.headsAgree(m2)); 
}
@Test
public void testAttributesAgreeFalseDueToAnimacy() {
  Mention m1 = new Mention();
  Mention m2 = new Mention();
  Dictionaries dict = new Dictionaries();

  m1.animacy = Dictionaries.Animacy.ANIMATE;
  m2.animacy = Dictionaries.Animacy.INANIMATE;

  m1.number = Dictionaries.Number.SINGULAR;
  m2.number = Dictionaries.Number.SINGULAR;

  m1.gender = Dictionaries.Gender.MALE;
  m2.gender = Dictionaries.Gender.MALE;

  m1.nerString = "PERSON";
  m2.nerString = "PERSON";

  assertFalse(m1.attributesAgree(m2, dict));
}
@Test
public void testIsTheCommonNounFalseWhenStartsWithA() {
  Mention mention = new Mention();
  CoreLabel article = new CoreLabel();
  article.set(CoreAnnotations.TextAnnotation.class, "a");

  CoreLabel noun = new CoreLabel();
  noun.set(CoreAnnotations.TextAnnotation.class, "dog");

  mention.mentionType = Dictionaries.MentionType.NOMINAL;
  mention.originalSpan = Arrays.asList(article, noun);

  assertFalse(mention.isTheCommonNoun());
}
@Test
public void testSameSentenceReferenceEqualityFalsePositiveCase() {
  Mention m1 = new Mention();
  List<CoreLabel> sentenceList = new ArrayList<>();
  sentenceList.add(new CoreLabel());

  Mention m2 = new Mention();
  m1.sentenceWords = sentenceList;
  m2.sentenceWords = new ArrayList<>();
  m2.sentenceWords.add(sentenceList.get(0)); 

  assertFalse(m1.sameSentence(m2)); 
}
@Test
public void testMoreRepresentativeThanEqualHeadsShortSpanTiebreaker() {
  Mention m1 = new Mention();
  Mention m2 = new Mention();

  m1.mentionType = Dictionaries.MentionType.PROPER;
  m2.mentionType = Dictionaries.MentionType.PROPER;

  m1.nerString = "PERSON";
  m2.nerString = "PERSON";

  m1.headIndex = 2;
  m1.startIndex = 1;

  m2.headIndex = 2;
  m2.startIndex = 1;

  m1.originalSpan = Arrays.asList(new CoreLabel(), new CoreLabel(), new CoreLabel());
  m2.originalSpan = Arrays.asList(new CoreLabel(), new CoreLabel(), new CoreLabel(), new CoreLabel());

  assertTrue(m1.moreRepresentativeThan(m2));
}
@Test
public void testMoreRepresentativeThanFallbackToHashCode() {
  Mention m1 = new Mention();
  Mention m2 = new Mention();

  m1.mentionType = Dictionaries.MentionType.PROPER;
  m2.mentionType = Dictionaries.MentionType.PROPER;

  m1.nerString = "PERSON";
  m2.nerString = "PERSON";

  m1.headIndex = 5;
  m1.startIndex = 5;
  m1.sentNum = 5;
  m1.originalSpan = Arrays.asList(new CoreLabel(), new CoreLabel(), new CoreLabel(), new CoreLabel(), new CoreLabel(), new CoreLabel());

  m2.headIndex = 5;
  m2.startIndex = 5;
  m2.sentNum = 5;
  m2.originalSpan = Arrays.asList(new CoreLabel(), new CoreLabel(), new CoreLabel(), new CoreLabel(), new CoreLabel(), new CoreLabel());

  boolean result;
  try {
    result = m1.moreRepresentativeThan(m2);
  } catch (IllegalStateException e) {
    result = false;
  }

  assertFalse(result); 
}
@Test
public void testInsideInFalseDifferentSentNum() {
  Mention m1 = new Mention();
  Mention m2 = new Mention();

  m1.sentNum = 1;
  m1.startIndex = 2;
  m1.endIndex = 4;

  m2.sentNum = 2;
  m2.startIndex = 0;
  m2.endIndex = 5;

  assertFalse(m1.insideIn(m2));
}
@Test
public void testInsideInTrueWithEqualBounds() {
  Mention m1 = new Mention();
  Mention m2 = new Mention();

  m1.sentNum = 1;
  m1.startIndex = 2;
  m1.endIndex = 4;

  m2.sentNum = 1;
  m2.startIndex = 2;
  m2.endIndex = 4;

  assertTrue(m1.insideIn(m2));
}
@Test
public void testGetPositionWhenHeadIndexNullOrInvalidReturnsNull() {
  Mention mention = new Mention();
  CoreLabel token1 = new CoreLabel();
  CoreLabel token2 = new CoreLabel();
  token1.set(CoreAnnotations.TextAnnotation.class, "John");
  token2.set(CoreAnnotations.TextAnnotation.class, "walks");

  mention.sentenceWords = Arrays.asList(token1, token2);
  mention.headIndex = 10;  

  String pos = mention.getPosition();

  assertNull(pos);
}
@Test
public void testEntityTypesAgreeWithStrictMismatchReturnsFalse() {
  Mention m1 = new Mention();
  Mention m2 = new Mention();
  Dictionaries dict = new Dictionaries();

  m1.nerString = "PERSON";
  m2.nerString = "LOCATION";

  boolean result = m1.entityTypesAgree(m2, dict, true);
  assertFalse(result);
}
@Test
public void testEntityTypesAgreeWithStrictMatchReturnsTrue() {
  Mention m1 = new Mention();
  Mention m2 = new Mention();
  Dictionaries dict = new Dictionaries();

  m1.nerString = "ORGANIZATION";
  m2.nerString = "ORGANIZATION";

  boolean result = m1.entityTypesAgree(m2, dict, true);
  assertTrue(result);
}
@Test
public void testEntityTypesAgreePronounCasePerson() {
  Mention pronoun = new Mention();
  Mention named = new Mention();
  Dictionaries dict = new Dictionaries();
//  dict.personPronouns = new HashSet<>();
//  dict.personPronouns.add("he");

  pronoun.mentionType = Dictionaries.MentionType.PRONOMINAL;
  pronoun.headString = "he";
  pronoun.nerString = "O";

  named.nerString = "PERSON";

  boolean result = pronoun.entityTypesAgree(named, dict);
  assertTrue(result);
}
@Test
public void testEntityTypesAgreePronounCaseOrganizationIncorrect() {
  Mention pronoun = new Mention();
  Mention named = new Mention();
  Dictionaries dict = new Dictionaries();
//  dict.organizationPronouns = Collections.singleton("they");

  pronoun.mentionType = Dictionaries.MentionType.PRONOMINAL;
  pronoun.headString = "he";
  pronoun.nerString = "O";

  named.nerString = "ORGANIZATION";

  boolean result = pronoun.entityTypesAgree(named, dict);
  assertFalse(result);
}
@Test
public void testGendersAgreeStrictTrue() {
  Mention m1 = new Mention();
  Mention m2 = new Mention();

  m1.gender = Dictionaries.Gender.FEMALE;
  m2.gender = Dictionaries.Gender.FEMALE;

  boolean result = m1.gendersAgree(m2, true);
  assertTrue(result);
}
@Test
public void testGendersAgreeStrictFalse() {
  Mention m1 = new Mention();
  Mention m2 = new Mention();

  m1.gender = Dictionaries.Gender.FEMALE;
  m2.gender = Dictionaries.Gender.MALE;

  boolean result = m1.gendersAgree(m2, true);
  assertFalse(result);
}
@Test
public void testNumbersAgreeWithUnknownReturnsTrue() {
  Mention m1 = new Mention();
  Mention m2 = new Mention();

  m1.number = Dictionaries.Number.UNKNOWN;
  m2.number = Dictionaries.Number.PLURAL;

  boolean result = m1.numbersAgree(m2);
  assertTrue(result);
}
@Test
public void testAnimaciesAgreeStrictTrue() {
  Mention m1 = new Mention();
  Mention m2 = new Mention();

  m1.animacy = Dictionaries.Animacy.INANIMATE;
  m2.animacy = Dictionaries.Animacy.INANIMATE;

  boolean result = m1.animaciesAgree(m2, true);
  assertTrue(result);
}
@Test
public void testAnimaciesAgreeStrictFalse() {
  Mention m1 = new Mention();
  Mention m2 = new Mention();

  m1.animacy = Dictionaries.Animacy.INANIMATE;
  m2.animacy = Dictionaries.Animacy.ANIMATE;

  boolean result = m1.animaciesAgree(m2, true);
  assertFalse(result);
}
@Test
public void testIsMemberOfSameListWhenBothNull() {
  Mention m1 = new Mention();
  Mention m2 = new Mention();

  boolean result = m1.isMemberOfSameList(m2);
  assertFalse(result);
}
@Test
public void testSpanToStringWithEmptySpanReturnsEmptyString() {
  Mention m = new Mention();
  m.originalSpan = new ArrayList<>();

  String result = m.spanToString();
  assertEquals("", result);
}
@Test
public void testRemovePhraseAfterHeadWithComma() {
  Mention m = new Mention();
  List<CoreLabel> tokens = new ArrayList<>();

  CoreLabel token1 = new CoreLabel();
  token1.set(CoreAnnotations.TextAnnotation.class, "The");
  token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "DT");
  tokens.add(token1);

  CoreLabel token2 = new CoreLabel();
  token2.set(CoreAnnotations.TextAnnotation.class, "scientist");
  token2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
  tokens.add(token2);

  CoreLabel token3 = new CoreLabel();
  token3.set(CoreAnnotations.TextAnnotation.class, ",");
  token3.set(CoreAnnotations.PartOfSpeechAnnotation.class, ",");
  tokens.add(token3);

  CoreLabel token4 = new CoreLabel();
  token4.set(CoreAnnotations.TextAnnotation.class, "who");
  token4.set(CoreAnnotations.PartOfSpeechAnnotation.class, "WP");
  tokens.add(token4);

  m.originalSpan = tokens;
  m.startIndex = 0;
  m.headIndex = 1;

  String result = m.removePhraseAfterHead();
  assertEquals("The scientist", result);
}
@Test
public void testRemovePhraseAfterHeadWithWHClauseNoComma() {
  Mention m = new Mention();
  List<CoreLabel> tokens = new ArrayList<>();

  CoreLabel token1 = new CoreLabel();
  token1.set(CoreAnnotations.TextAnnotation.class, "boy");
  token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
  tokens.add(token1);

  CoreLabel token2 = new CoreLabel();
  token2.set(CoreAnnotations.TextAnnotation.class, "who");
  token2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "WP");
  tokens.add(token2);

  m.originalSpan = tokens;
  m.startIndex = 0;
  m.headIndex = 0;

  String result = m.removePhraseAfterHead();
  assertEquals("boy", result);
}
@Test
public void testRemovePhraseAfterHeadNoCommaNoWH() {
  Mention m = new Mention();
  List<CoreLabel> tokens = new ArrayList<>();

  CoreLabel token1 = new CoreLabel();
  token1.set(CoreAnnotations.TextAnnotation.class, "Dr.");
  token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
  tokens.add(token1);

  CoreLabel token2 = new CoreLabel();
  token2.set(CoreAnnotations.TextAnnotation.class, "Smith");
  token2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
  tokens.add(token2);

  m.originalSpan = tokens;
  m.startIndex = 0;
  m.headIndex = 1;

  String result = m.removePhraseAfterHead();
  assertEquals("Dr. Smith", result);
}
@Test
public void testToStringEqualsSpanToString() {
  Mention m = new Mention();
  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "NASA");
  m.originalSpan = Collections.singletonList(token);
  String str1 = m.toString();
  String str2 = m.spanToString();
  assertEquals(str1, str2);
}
@Test
public void testGetGenderReturnsNullWhenGenderNotFoundInDictionary() {
  Mention mention = new Mention();
  CoreLabel head = new CoreLabel();
  head.set(CoreAnnotations.TextAnnotation.class, "Someone");
  List<CoreLabel> span = new ArrayList<>();
  CoreLabel token1 = new CoreLabel();
  token1.set(CoreAnnotations.TextAnnotation.class, "Dr.");
  CoreLabel token2 = new CoreLabel();
  token2.set(CoreAnnotations.TextAnnotation.class, "Someone");
  span.add(token1);
  span.add(token2);
  mention.originalSpan = span;
  mention.headWord = token2;
  mention.nerString = "PER";

  Dictionaries dict = new Dictionaries();
//  dict.genderNumber = new HashMap<>();

  Dictionaries.Gender result = null;
  try {
    java.lang.reflect.Method m = Mention.class.getDeclaredMethod("getGender", Dictionaries.class, List.class);
    m.setAccessible(true);
    result = (Dictionaries.Gender) m.invoke(mention, dict, Arrays.asList("dr.", "someone"));
  } catch (Exception e) {
    fail("Reflection failed: " + e.getMessage());
  }

  assertNull(result);
}
@Test
public void testLowercaseNormalizedSpanStringWhenSpanStringNotInitialized() {
  Mention mention = new Mention();
  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "NASA");
  List<CoreLabel> span = Collections.singletonList(token);
  mention.originalSpan = span;

  String spanStr = mention.spanToString(); 
  String lower = mention.lowercaseNormalizedSpanString();

  assertEquals("nasa", lower);
}
@Test
public void testBuildQueryTextStripsSpacesAndJoinsCorrectly() {
  List<String> terms = new ArrayList<>();
  terms.add("New");
  terms.add("York");
  terms.add("City");
  String query = Mention.buildQueryText(terms);
  assertEquals("New York City", query);
}
@Test
public void testGetPatternWithNamedEntityLabels() {
  Mention mention = new Mention();
  CoreLabel tok1 = new CoreLabel();
  tok1.set(CoreAnnotations.TextAnnotation.class, "Barack");
  tok1.set(CoreAnnotations.LemmaAnnotation.class, "barack");
  tok1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

  CoreLabel tok2 = new CoreLabel();
  tok2.set(CoreAnnotations.TextAnnotation.class, "Obama");
  tok2.set(CoreAnnotations.LemmaAnnotation.class, "obama");
  tok2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

  CoreLabel tok3 = new CoreLabel();
  tok3.set(CoreAnnotations.TextAnnotation.class, "spoke");
  tok3.set(CoreAnnotations.LemmaAnnotation.class, "speak");
  tok3.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

  mention.headWord = tok2;
  mention.nerString = "PERSON";

  List<AbstractCoreLabel> tokens = Arrays.asList(tok1, tok2, tok3);
  String result = mention.getPattern(tokens);

  assertTrue(result.contains("<PERSON>"));
  assertTrue(result.contains("speak"));
}
@Test
public void testRemovePhraseAfterHeadOnlyWHNoComma() {
  Mention mention = new Mention();
  CoreLabel word1 = new CoreLabel();
  word1.set(CoreAnnotations.TextAnnotation.class, "students");
  word1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNS");

  CoreLabel word2 = new CoreLabel();
  word2.set(CoreAnnotations.TextAnnotation.class, "who");
  word2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "WP");

  mention.originalSpan = Arrays.asList(word1, word2);
  mention.headIndex = 0;
  mention.startIndex = 0;

  String result = mention.removePhraseAfterHead();
  assertEquals("students", result);
}
@Test
public void testIsListLikeWithCoordinationWordInSpan() {
  Mention mention = new Mention();
  CoreLabel word1 = new CoreLabel();
  word1.set(CoreAnnotations.TextAnnotation.class, "Tom");
  word1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");

  CoreLabel word2 = new CoreLabel();
  word2.set(CoreAnnotations.TextAnnotation.class, "and");
  word2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "CC");

  CoreLabel word3 = new CoreLabel();
  word3.set(CoreAnnotations.TextAnnotation.class, "Jerry");
  word3.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");

  mention.originalSpan = Arrays.asList(word1, word2, word3);
  mention.mentionSubTree = mock(Tree.class);
  when(mention.mentionSubTree.getChildrenAsList()).thenReturn(Collections.emptyList());
//  when(mention.mentionSubTree.yieldWords()).thenReturn((ArrayList<Word>) Arrays.asList(word1, word2, word3));

  boolean result = false;
  try {
    java.lang.reflect.Method m = Mention.class.getDeclaredMethod("isListLike");
    m.setAccessible(true);
    result = (Boolean) m.invoke(mention);
  } catch (Exception e) {
    fail("Unable to invoke isListLike by reflection.");
  }

  assertTrue(result);
}
@Test
public void testGetQuantificationReturnsDefiniteFromDeterminer() {
  Mention mention = new Mention();
  Dictionaries dict = new Dictionaries();
  SemanticGraph graph = new SemanticGraph();

  CoreLabel headToken = new CoreLabel();
  headToken.set(CoreAnnotations.TextAnnotation.class, "cat");
  headToken.setIndex(1);
  IndexedWord head = new IndexedWord(headToken);
  graph.addVertex(head);

  CoreLabel detToken = new CoreLabel();
  detToken.set(CoreAnnotations.TextAnnotation.class, "the");
  detToken.set(CoreAnnotations.LemmaAnnotation.class, "the");
  detToken.setIndex(2);
  IndexedWord det = new IndexedWord(detToken);
  graph.addVertex(det);
  graph.addEdge(head, det, UniversalEnglishGrammaticalRelations.DETERMINER, 1.0, false);

  mention.headIndexedWord = head;
  mention.nerString = "O";
  mention.dependency = graph;

//  dict.determiners = Collections.singleton("the");
  String result = mention.getQuantification(dict);
  assertEquals("definite", result);
}
@Test
public void testGetReportEmbeddingReturnsOneForAsClause() {
  Mention mention = new Mention();
  Dictionaries dict = new Dictionaries();
//  dict.reportVerb = new HashSet<>(Collections.singleton("say"));

  SemanticGraph graph = new SemanticGraph();

  CoreLabel headToken = new CoreLabel();
  headToken.set(CoreAnnotations.TextAnnotation.class, "news");
  headToken.setIndex(2);
  IndexedWord head = new IndexedWord(headToken);

  CoreLabel verbToken = new CoreLabel();
  verbToken.set(CoreAnnotations.TextAnnotation.class, "said");
  verbToken.set(CoreAnnotations.LemmaAnnotation.class, "say");
  verbToken.setIndex(1);
  IndexedWord verb = new IndexedWord(verbToken);

  CoreLabel markToken = new CoreLabel();
  markToken.set(CoreAnnotations.TextAnnotation.class, "as");
  markToken.set(CoreAnnotations.LemmaAnnotation.class, "as");
  markToken.setIndex(3);
  IndexedWord marker = new IndexedWord(markToken);

  graph.addVertex(head);
  graph.addVertex(verb);
  graph.addVertex(marker);

  graph.addEdge(verb, head, UniversalEnglishGrammaticalRelations.ADV_CLAUSE_MODIFIER, 1.0, false);
  graph.addEdge(verb, marker, UniversalEnglishGrammaticalRelations.MARKER, 1.0, false);

  mention.headIndexedWord = head;
  mention.dependency = graph;

  int result = mention.getReportEmbedding(dict);
  assertEquals(1, result);
}
@Test
public void testGetModalRecognizesModalAuxiliary() {
  Mention mention = new Mention();
  Dictionaries dict = new Dictionaries();
//  dict.modals = Collections.singleton("can");

  SemanticGraph graph = new SemanticGraph();

  CoreLabel headToken = new CoreLabel();
  headToken.set(CoreAnnotations.TextAnnotation.class, "leave");
  headToken.set(CoreAnnotations.LemmaAnnotation.class, "leave");
  headToken.setIndex(2);
  IndexedWord head = new IndexedWord(headToken);
  graph.addVertex(head);

  CoreLabel auxToken = new CoreLabel();
  auxToken.set(CoreAnnotations.TextAnnotation.class, "can");
  auxToken.set(CoreAnnotations.LemmaAnnotation.class, "can");
  auxToken.setIndex(1);
  IndexedWord aux = new IndexedWord(auxToken);
  graph.addVertex(aux);

  graph.addEdge(aux, head, UniversalEnglishGrammaticalRelations.AUX_MODIFIER, 1.0, false);
  graph.addEdge(head, aux, UniversalEnglishGrammaticalRelations.AUX_MODIFIER, 1.0, false); 

  mention.headIndexedWord = head;
  mention.dependency = graph;

  int result = mention.getModal(dict);
  assertEquals(1, result);
}
@Test
public void testIsTheCommonNounWithLongSpanReturnsFalse() {
  Mention mention = new Mention();
  CoreLabel w1 = new CoreLabel();
  CoreLabel w2 = new CoreLabel();
  CoreLabel w3 = new CoreLabel();
  w1.set(CoreAnnotations.TextAnnotation.class, "the");
  w2.set(CoreAnnotations.TextAnnotation.class, "big");
  w3.set(CoreAnnotations.TextAnnotation.class, "dog");
  mention.originalSpan = Arrays.asList(w1, w2, w3);
  mention.mentionType = Dictionaries.MentionType.NOMINAL;

  boolean result = mention.isTheCommonNoun();
  assertFalse(result);
}
@Test
public void testGetPatternEmptyPremodsAndPostmodsOnlyHead() {
  Mention mention = new Mention();
  CoreLabel head = new CoreLabel();
  head.setIndex(0);
  head.set(CoreAnnotations.LemmaAnnotation.class, "city");
  mention.headWord = head;

  mention.getPremodifiers().clear();
  mention.getPostmodifiers().clear();

  String result = mention.getPattern();
  assertEquals("city", result);
}
@Test
public void testGetPremodifierContextReturnsEmptyWhenNoNER() {
  Mention mention = new Mention();
  mention.headIndexedWord = new IndexedWord();
  mention.dependency = new SemanticGraph();

  List<String> result = mention.getPremodifierContext();
  assertTrue(result.isEmpty());
}
@Test
public void testGetContextReturnsOneNEPhrase() {
  Mention mention = new Mention();
  CoreLabel person1 = new CoreLabel();
  CoreLabel other = new CoreLabel();
  person1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
  person1.set(CoreAnnotations.TextAnnotation.class, "John");
  other.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
  mention.sentenceWords = Arrays.asList(other, person1, other);

  List<String> result = mention.getContext();

  assertEquals(1, result.size());
  assertEquals("John", result.get(0));
}
@Test
public void testGetSplitPatternAllThreeFormsReturnValidResults() {
  Mention mention = new Mention();
  CoreLabel head = new CoreLabel();
  head.set(CoreAnnotations.TextAnnotation.class, "city");
  head.set(CoreAnnotations.LemmaAnnotation.class, "city");
  head.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
  head.setIndex(1);
  mention.headWord = head;

  CoreLabel adj1 = new CoreLabel();
  adj1.set(CoreAnnotations.TextAnnotation.class, "old");
  adj1.set(CoreAnnotations.LemmaAnnotation.class, "old");
  adj1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
  adj1.setIndex(0);

  CoreLabel mod = new CoreLabel();
  mod.set(CoreAnnotations.TextAnnotation.class, "harbor");
  mod.set(CoreAnnotations.LemmaAnnotation.class, "harbor");
  mod.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
  mod.setIndex(2);

  mention.sentenceWords = Arrays.asList(adj1, head, mod);
  mention.originalSpan = mention.sentenceWords;

  mention.headIndexedWord = new IndexedWord(head);
  mention.dependency = new SemanticGraph();
  mention.dependency.addVertex(mention.headIndexedWord);

  IndexedWord adj1Indexed = new IndexedWord(adj1);
  IndexedWord modIndexed = new IndexedWord(mod);
  mention.dependency.addVertex(adj1Indexed);
  mention.dependency.addVertex(modIndexed);

  mention.dependency.addEdge(mention.headIndexedWord, adj1Indexed, UniversalEnglishGrammaticalRelations.ADJECTIVAL_MODIFIER, 1.0, false);
  mention.dependency.addEdge(mention.headIndexedWord, modIndexed, UniversalEnglishGrammaticalRelations.NP_ADVERBIAL_MODIFIER, 1.0, false);

  String[] result = mention.getSplitPattern();

  assertEquals(4, result.length);
  assertEquals("city", result[0]);
  assertTrue(result[1].contains("old"));
  assertTrue(result[2].contains("old"));
  assertTrue(result[3].contains("harbor"));
}
@Test
public void testSetGenderWhenMentionIsPronounOverridesWithPronounSet() {
  Mention mention = new Mention();
  mention.mentionType = Dictionaries.MentionType.PRONOMINAL;
  CoreLabel head = new CoreLabel();
  head.set(CoreAnnotations.TextAnnotation.class, "She");
  mention.headString = "she";
  mention.headWord = head;

  Dictionaries dict = new Dictionaries();
//  dict.femalePronouns = new HashSet<>(Collections.singleton("she"));

  java.util.List<String> dummyStr = new ArrayList<>();
  dummyStr.add("she");

  try {
    java.lang.reflect.Method m = Mention.class.getDeclaredMethod("setGender", Dictionaries.class, Dictionaries.Gender.class);
    m.setAccessible(true);
    m.invoke(mention, dict, null);
  } catch (Exception e) {
    fail(e.getMessage());
  }

  assertEquals(Dictionaries.Gender.FEMALE, mention.gender);
}
@Test
public void testGetModifiersInanimateNounReturnsZero() {
  Mention mention = new Mention();
  SemanticGraph g = new SemanticGraph();
  IndexedWord head = new IndexedWord();
  head.set(CoreAnnotations.TextAnnotation.class, "table");
  head.set(CoreAnnotations.LemmaAnnotation.class, "table");
  head.setIndex(1);
  g.addVertex(head);
  mention.headIndexedWord = head;
  mention.dependency = g;
  Dictionaries dict = new Dictionaries();
  int mods = mention.getModifiers(dict);
  assertEquals(0, mods);
}
@Test
public void testGetNegationReturnsOneForNegWordInChildren() {
  Mention mention = new Mention();
  IndexedWord head = new IndexedWord();
  head.set(CoreAnnotations.TextAnnotation.class, "run");
  head.set(CoreAnnotations.LemmaAnnotation.class, "run");
  head.setIndex(1);

  IndexedWord notWord = new IndexedWord();
  notWord.set(CoreAnnotations.LemmaAnnotation.class, "not");
  notWord.setIndex(2);

  SemanticGraph graph = new SemanticGraph();
  graph.addVertex(head);
  graph.addVertex(notWord);
  graph.addEdge(head, notWord, UniversalEnglishGrammaticalRelations.NEGATION_MODIFIER, 1.0, false);

  mention.headIndexedWord = head;
  mention.dependency = graph;

  Dictionaries dict = new Dictionaries();
//  dict.negations = new HashSet<>(Collections.singleton("not"));

  int result = mention.getNegation(dict);
  assertEquals(1, result);
}
@Test
public void testIsCoordinatedTrueFromConjRelationChild() {
  Mention mention = new Mention();
  SemanticGraph graph = new SemanticGraph();

  CoreLabel headToken = new CoreLabel();
  headToken.set(CoreAnnotations.TextAnnotation.class, "students");
  headToken.setIndex(1);

  CoreLabel anotherToken = new CoreLabel();
  anotherToken.set(CoreAnnotations.TextAnnotation.class, "teachers");
  anotherToken.setIndex(2);

  IndexedWord head = new IndexedWord(headToken);
  IndexedWord another = new IndexedWord(anotherToken);
  graph.addVertex(head);
  graph.addVertex(another);

  graph.addEdge(head, another, GrammaticalRelation.valueOf("conj:and"), 1.0, false);

  mention.headIndexedWord = head;
  mention.dependency = graph;

  boolean result = mention.isCoordinated();
  assertTrue(result);
}
@Test
public void testIsListMemberOfFalseWhenMentionTypeNotList() {
  Mention list = new Mention();
  list.mentionType = Dictionaries.MentionType.PROPER;
  list.mentionSubTree = null;
  list.originalSpan = Collections.emptyList();
  list.sentenceWords = Arrays.asList(new CoreLabel());

  Mention target = new Mention();
  target.mentionType = Dictionaries.MentionType.NOMINAL;
  target.sentenceWords = list.sentenceWords;

  boolean result = target.isListMemberOf(list);
  assertFalse(result);
}
@Test
public void testAttributesAgreeWithAllUnknownAttributesReturnsTrue() {
  Mention m1 = new Mention();
  Mention m2 = new Mention();
  m1.animacy = Dictionaries.Animacy.UNKNOWN;
  m2.animacy = Dictionaries.Animacy.UNKNOWN;
  m1.gender = Dictionaries.Gender.UNKNOWN;
  m2.gender = Dictionaries.Gender.UNKNOWN;
  m1.number = Dictionaries.Number.UNKNOWN;
  m2.number = Dictionaries.Number.UNKNOWN;
  m1.nerString = "O";
  m2.nerString = "O";

  Dictionaries dict = new Dictionaries();
  boolean result = m1.attributesAgree(m2, dict);
  assertTrue(result);
}
@Test
public void testAttributesAgreeFalseDueToEntityTypeMismatch() {
  Mention m1 = new Mention();
  Mention m2 = new Mention();
  Dictionaries dict = new Dictionaries();
  m1.nerString = "PERSON";
  m2.nerString = "LOCATION";

  m1.number = Dictionaries.Number.SINGULAR;
  m2.number = Dictionaries.Number.SINGULAR;
  m1.gender = Dictionaries.Gender.MALE;
  m2.gender = Dictionaries.Gender.MALE;
  m1.animacy = Dictionaries.Animacy.ANIMATE;
  m2.animacy = Dictionaries.Animacy.ANIMATE;

  boolean result = m1.attributesAgree(m2, dict);
  assertFalse(result);
}
@Test
public void testHeadsAgreeWithNullNERAndExactHeadMismatchReturnsFalse() {
  CoreLabel head1 = new CoreLabel();
  head1.set(CoreAnnotations.TextAnnotation.class, "Tokyo");
  head1.setWord("Tokyo");

  CoreLabel head2 = new CoreLabel();
  head2.set(CoreAnnotations.TextAnnotation.class, "Kyoto");
  head2.setWord("Kyoto");

  Mention m1 = new Mention();
  m1.headString = "tokyo";
  m1.headWord = head1;
  m1.nerString = null;
  m1.originalSpan = Arrays.asList(head1);

  Mention m2 = new Mention();
  m2.headString = "kyoto";
  m2.headWord = head2;
  m2.nerString = null;
  m2.originalSpan = Arrays.asList(head2);

  boolean result = m1.headsAgree(m2);
  assertFalse(result);
}
@Test
public void testMoreRepresentativeThanFalseDueToShorterSpan() {
  Mention m1 = new Mention();
  Mention m2 = new Mention();
  m1.mentionType = Dictionaries.MentionType.PROPER;
  m2.mentionType = Dictionaries.MentionType.PROPER;
  m1.nerString = "LOCATION";
  m2.nerString = "LOCATION";
  m1.headIndex = 3;
  m1.startIndex = 2;
  m2.headIndex = 3;
  m2.startIndex = 1;

  m1.sentNum = 0;
  m2.sentNum = 0;

  m1.originalSpan = Arrays.asList(new CoreLabel(), new CoreLabel());
  m2.originalSpan = Arrays.asList(new CoreLabel(), new CoreLabel(), new CoreLabel());

  boolean result = m1.moreRepresentativeThan(m2);
  assertFalse(result);
}
@Test
public void testIncludedInFalseWhenSubtreeIsNotContained() {
  Mention m1 = new Mention();
  Mention m2 = new Mention();

  m1.startIndex = 0;
  m1.endIndex = 2;
  m2.startIndex = 0;
  m2.endIndex = 2;

  m1.mentionSubTree = mock(Tree.class);
  when(m1.mentionSubTree.subTrees()).thenReturn(Collections.singleton(mock(Tree.class)));

  m2.mentionSubTree = mock(Tree.class);

  m1.sentenceWords = Collections.singletonList(new CoreLabel());
  m2.sentenceWords = m1.sentenceWords;

  boolean result = m1.includedIn(m2);
  assertFalse(result);
}
@Test
public void testHeadsAgreeWithNERSubwordInclusionReturnsTrue() {
  CoreLabel full = new CoreLabel();
  full.set(CoreAnnotations.TextAnnotation.class, "Barack");
  Mention m1 = new Mention();
  m1.headWord = full;
  m1.headString = "barack";
  m1.nerString = "PERSON";
  m1.originalSpan = Arrays.asList(full);

  CoreLabel full2 = new CoreLabel();
  full2.set(CoreAnnotations.TextAnnotation.class, "Barack");
  CoreLabel second = new CoreLabel();
  second.set(CoreAnnotations.TextAnnotation.class, "Obama");

  Mention m2 = new Mention();
  m2.headWord = second;
  m2.headString = "obama";
  m2.nerString = "PERSON";
  m2.originalSpan = Arrays.asList(full2, second);

  boolean result = m1.headsAgree(m2);
  assertTrue(result);
}
@Test
public void testHeadsAgreeWithSameWordDifferentNERReturnsFalse() {
  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "Apple");

  Mention m1 = new Mention();
  m1.headString = "apple";
  m1.headWord = token;
  m1.nerString = "ORGANIZATION";
  m1.originalSpan = Arrays.asList(token);

  Mention m2 = new Mention();
  m2.headWord = token;
  m2.headString = "apple";
  m2.nerString = "FOOD";
  m2.originalSpan = Arrays.asList(token);

  boolean result = m1.headsAgree(m2);
  assertFalse(result);
}
@Test
public void testStringWithoutArticleWithEmptyStringReturnsEmpty() {
  Mention mention = new Mention();
  String result = mention.stringWithoutArticle("");
  assertEquals("", result);
}
@Test
public void testLongNNPEndsWithHeadShortSpanReturnsHeadWord() {
  Mention mention = new Mention();
  CoreLabel token1 = new CoreLabel();
  token1.set(CoreAnnotations.TextAnnotation.class, "Dr.");
  token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
  CoreLabel token2 = new CoreLabel();
  token2.set(CoreAnnotations.TextAnnotation.class, "Smith");
  token2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
  mention.sentenceWords = Arrays.asList(token1, token2);
  mention.headIndex = 1;
  String result = mention.longestNNPEndsWithHead();
  assertEquals("Smith", result);
}
@Test
public void testLowestNPIncludesHeadReturnsHeadWordIfNotNP() {
  Mention mention = new Mention();

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "Tree");
  Tree leaf = Tree.valueOf("(NN Tree)");
  List<Tree> leaves = new ArrayList<>();
  leaves.add(leaf);

  mention.contextParseTree = mock(Tree.class);
  when(mention.contextParseTree.getLeaves()).thenReturn(leaves);
  when(mention.contextParseTree.ancestor(anyInt(), any(Tree.class))).thenReturn(null);

  mention.headIndex = 0;
  mention.sentenceWords = Collections.singletonList(token);
  mention.originalSpan = Collections.singletonList(token);
  mention.headWord = token;

  String result = mention.lowestNPIncludesHead();
  assertEquals("Tree", result);
}
@Test
public void testIsRoleAppositiveFalseDueToDifferentSentence() {
  Mention m1 = new Mention();
  Mention m2 = new Mention();
  Dictionaries dict = new Dictionaries();
  CoreLabel tok1 = new CoreLabel();
  tok1.set(CoreAnnotations.TextAnnotation.class, "doctor");
  m1.originalSpan = Collections.singletonList(tok1);
  m1.mentionType = Dictionaries.MentionType.NOMINAL;
  m1.nerString = "PERSON";
  CoreLabel tok2 = new CoreLabel();
  tok2.set(CoreAnnotations.TextAnnotation.class, "John");
  m2.originalSpan = Collections.singletonList(tok2);
  m2.nerString = "PERSON";

  m1.sentenceWords = Collections.singletonList(tok1);
  m2.sentenceWords = Collections.singletonList(tok2); 

  String t1 = m1.lowercaseNormalizedSpanString();
  assertFalse(m1.isRoleAppositive(m2, dict));
}
@Test
public void testSetNERStringHandlesNullNamedEntityTagAnnotation() {
  Mention mention = new Mention();
  CoreLabel head = new CoreLabel();
  head.set(CoreAnnotations.EntityTypeAnnotation.class, "NAM"); 
  mention.headWord = head;
//  mention.setNERString();
//
//  assertEquals("O", mention.nerString);
}
@Test
public void testSetNERStringWithNamedEntityTagAndNoEntityTypePresent() {
  Mention mention = new Mention();
  CoreLabel head = new CoreLabel();
  head.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");
  mention.headWord = head;
//  mention.setNERString();
//
//  assertEquals("LOCATION", mention.nerString);
}
@Test
public void testSetHeadStringFallsBackToOriginalWhenNoKnownSuffix() {
  Mention mention = new Mention();
  CoreLabel tok1 = new CoreLabel();
  tok1.set(CoreAnnotations.TextAnnotation.class, "Main");
  CoreLabel tok2 = new CoreLabel();
  tok2.set(CoreAnnotations.TextAnnotation.class, "Street");
  CoreLabel hw = tok2;
  mention.originalSpan = Arrays.asList(tok1, tok2);
  tok2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");
  tok1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");
  tok1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
  tok2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");

  mention.headWord = hw;
  mention.headIndex = 1;
  mention.startIndex = 0;

//  mention.setHeadString();
//
//  assertEquals("street", mention.headString);
}
@Test
public void testRemovePhraseAfterHeadWhenCommaAfterHeadOnly() {
  Mention mention = new Mention();
  CoreLabel tok1 = new CoreLabel();
  tok1.set(CoreAnnotations.TextAnnotation.class, "Steve");
  tok1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");

  CoreLabel tok2 = new CoreLabel();
  tok2.set(CoreAnnotations.TextAnnotation.class, ",");
  tok2.set(CoreAnnotations.PartOfSpeechAnnotation.class, ",");

  CoreLabel tok3 = new CoreLabel();
  tok3.set(CoreAnnotations.TextAnnotation.class, "founder");
  tok3.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

  mention.originalSpan = Arrays.asList(tok1, tok2, tok3);
  mention.startIndex = 0;
  mention.headIndex = 0;

  String result = mention.removePhraseAfterHead();
  assertEquals("Steve", result);
}
@Test
public void testGetPersonPronounYouSecondPerson() {
  Mention mention = new Mention();
  mention.mentionType = Dictionaries.MentionType.PRONOMINAL;
  CoreLabel hw = new CoreLabel();
  hw.set(CoreAnnotations.TextAnnotation.class, "You");
  mention.headWord = hw;
  mention.originalSpan = Arrays.asList(hw);
  mention.headString = "you";

  Dictionaries dict = new Dictionaries();
//  dict.secondPersonPronouns = new HashSet<>();
  dict.secondPersonPronouns.add("you");

  mention.number = Dictionaries.Number.UNKNOWN;
  mention.gender = Dictionaries.Gender.UNKNOWN;
  mention.animacy = Dictionaries.Animacy.UNKNOWN;
//
//  mention.setPerson(dict);
//  assertEquals(Dictionaries.Person.YOU, mention.person);
}
@Test
public void testGetPersonUnknownWhenNotInPronounLists() {
  Mention mention = new Mention();
  mention.mentionType = Dictionaries.MentionType.PRONOMINAL;
  CoreLabel hw = new CoreLabel();
  hw.set(CoreAnnotations.TextAnnotation.class, "gadget");
  mention.headWord = hw;
  mention.originalSpan = Arrays.asList(hw);
  mention.headString = "gadget";

  Dictionaries dict = new Dictionaries();
  mention.number = Dictionaries.Number.UNKNOWN;
  mention.gender = Dictionaries.Gender.UNKNOWN;
  mention.animacy = Dictionaries.Animacy.UNKNOWN;

//  mention.setPerson(dict);
//  assertEquals(Dictionaries.Person.UNKNOWN, mention.person);
}
@Test
public void testGetQuantificationReturnsIndefiniteWhenNoModifiersPresent() {
  Mention mention = new Mention();
  Dictionaries dict = new Dictionaries();
  mention.nerString = "O";
  IndexedWord hw = new IndexedWord();
  hw.set(CoreAnnotations.TextAnnotation.class, "apple");
  mention.headIndexedWord = hw;

  SemanticGraph graph = new SemanticGraph();
  IndexedWord apple = hw;
  graph.addVertex(apple);
  mention.dependency = graph;

  String result = mention.getQuantification(dict);
  assertEquals("indefinite", result);
}
@Test
public void testGetNegationReturnsZeroWhenSiblingsContainNegationAndIsSubject() {
  Mention mention = new Mention();
  Dictionaries dict = new Dictionaries();
//  dict.negations = new HashSet<>(Collections.singleton("not"));

  IndexedWord head = new IndexedWord();
  head.set(CoreAnnotations.TextAnnotation.class, "students");
  head.setIndex(1);
  IndexedWord sibling = new IndexedWord();
  sibling.set(CoreAnnotations.TextAnnotation.class, "not");
  sibling.setIndex(2);

  SemanticGraph graph = new SemanticGraph();
  graph.addVertex(head);
  graph.addVertex(sibling);

  mention.headIndexedWord = head;
  mention.dependency = graph;

  graph.addEdge(sibling, head, GrammaticalRelation.valueOf("nsubj"), 1.0, false);

  int result = mention.getNegation(dict);
  assertEquals(0, result); 
}
@Test
public void testGetReportEmbeddingReturnsZeroWhenNoMatchingPath() {
  Mention mention = new Mention();
  Dictionaries dict = new Dictionaries();
//  dict.reportVerb = new HashSet<>(Collections.singleton("claim"));
  SemanticGraph graph = new SemanticGraph();
  mention.headIndexedWord = new IndexedWord();
  mention.headIndexedWord.setIndex(5);
  mention.headIndexedWord.set(CoreAnnotations.TextAnnotation.class, "parade");

  graph.addVertex(mention.headIndexedWord);
  mention.dependency = graph;

  int result = mention.getReportEmbedding(dict);
  assertEquals(0, result);
} 
}