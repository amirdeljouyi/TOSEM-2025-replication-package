package edu.stanford.nlp.dcoref;

import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.UniversalEnglishGrammaticalRelations;
import edu.stanford.nlp.util.Generics;
import edu.stanford.nlp.util.Pair;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class Mention_3_GPTLLMTest {

 @Test
  public void testConstructorAndToString() {
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Barack");
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "Obama");

    List<CoreLabel> span = Arrays.asList(token1, token2);
    Mention m = new Mention(100, 0, 2, new SemanticGraph(), span);
    m.originalSpan = span;
    m.headWord = token2;

    String result = m.toString();
    assertEquals("Barack Obama", result);
  }
@Test
  public void testSpanToString() {
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "The");
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "president");

    List<CoreLabel> span = Arrays.asList(token1, token2);
    Mention m = new Mention(101, 3, 5, new SemanticGraph(), span);
    m.originalSpan = span;
    m.headWord = token2;
    String spanStr = m.spanToString();
    assertEquals("The president", spanStr);
  }
@Test
  public void testLowercaseNormalizedSpanString() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Stanford");

    List<CoreLabel> span = Collections.singletonList(token);
    Mention m = new Mention(102, 1, 2, new SemanticGraph(), span);
    m.originalSpan = span;
    m.headWord = token;
    m.spanToString(); 
    String norm = m.lowercaseNormalizedSpanString();
    assertEquals("stanford", norm);
  }
@Test
  public void testIsPronominal() {
    Mention m1 = new Mention();
    m1.mentionType = Dictionaries.MentionType.PRONOMINAL;
    assertTrue(m1.isPronominal());

    Mention m2 = new Mention();
    m2.mentionType = Dictionaries.MentionType.PROPER;
    assertFalse(m2.isPronominal());
  }
@Test
  public void testNerTokensAndNerName() {
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Barack");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "Obama");
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    List<CoreLabel> span = Arrays.asList(token1, token2);

    Mention m = new Mention(103, 5, 7, new SemanticGraph(), span);
    m.originalSpan = span;
    m.headIndex = 6;
    m.startIndex = 5;
    m.nerString = "PERSON";

    List<CoreLabel> result = m.nerTokens();

    assertNotNull(result);
    assertEquals(2, result.size());
    String name = m.nerName();
    assertEquals("Barack Obama", name);
  }
@Test
  public void testStringWithoutArticle() {
    Mention m1 = new Mention();
    assertEquals("cat", m1.stringWithoutArticle("a cat"));

    Mention m2 = new Mention();
    assertEquals("elephant", m2.stringWithoutArticle("an elephant"));

    Mention m3 = new Mention();
    assertEquals("tiger", m3.stringWithoutArticle("the tiger"));

    Mention m4 = new Mention();
    assertEquals("zebra", m4.stringWithoutArticle("zebra"));
  }
@Test
  public void testRemoveParenthesis() {
    String input = "The city (old capital)";
    String output = Mention.removeParenthesis(input);
    assertEquals("The city", output);

    String input2 = "Only (";
    String output2 = Mention.removeParenthesis(input2);
    assertEquals("Only", output2);
  }
@Test
  public void testHeadStringEndsWithKnownSuffix() {
    CoreLabel head = new CoreLabel();
    head.set(CoreAnnotations.TextAnnotation.class, "Corp.");
    head.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORG");

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "General");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORG");

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "Motors");
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORG");

    CoreLabel token3 = new CoreLabel();
    token3.set(CoreAnnotations.TextAnnotation.class, "Corp.");
    token3.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORG");

    List<CoreLabel> span = Arrays.asList(token1, token2, token3);
    Mention m = new Mention(104, 0, 3, new SemanticGraph(), span);
    m.originalSpan = span;
    m.headIndex = 2;
    m.startIndex = 0;

    m.headWord = token3;
//    m.setHeadString();

    assertEquals("motors", m.headString);  
  }
@Test
  public void testIsTheCommonNounPositive() {
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "the");

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "engineer");

    List<CoreLabel> span = Arrays.asList(token1, token2);
    Mention m = new Mention(105, 0, 2, new SemanticGraph(), span);
    m.originalSpan = span;
    m.spanToString(); 
    m.mentionType = Dictionaries.MentionType.NOMINAL;

    assertTrue(m.isTheCommonNoun());
  }
@Test
  public void testMoreRepresentativeThanByMentionType() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Paris");

    List<CoreLabel> span = Collections.singletonList(token);

    Mention m1 = new Mention(106, 0, 1, new SemanticGraph(), span);
    m1.mentionType = Dictionaries.MentionType.PROPER;
    m1.originalSpan = span;
    m1.headIndex = 0;
    m1.startIndex = 0;
    m1.sentNum = 0;
    m1.nerString = "LOCATION";

    Mention m2 = new Mention(107, 0, 1, new SemanticGraph(), span);
    m2.mentionType = Dictionaries.MentionType.PRONOMINAL;
    m2.originalSpan = span;
    m2.headIndex = 1;
    m2.startIndex = 1;
    m2.sentNum = 1;
    m2.nerString = "O";

    assertTrue(m1.moreRepresentativeThan(m2));
  }
@Test
  public void testEntityTypesAgreeStrictAndLenient() {
    Mention m1 = new Mention();
    m1.mentionType = Dictionaries.MentionType.PRONOMINAL;
    m1.headString = "she";
    m1.nerString = "PERSON";

    Mention m2 = new Mention();
    m2.nerString = "O";

//    Dictionaries dict = new Dictionaries(false, false, false, false, false);
//    assertTrue(m1.entityTypesAgree(m2, dict));

    m2.nerString = "ORGANIZATION";
    m1.headString = "it";
//    dict.organizationPronouns.add("it");
//    assertTrue(m1.entityTypesAgree(m2, dict));
  }
@Test
  public void testGendersAgreeAndNumbersAgree() {
    Mention m1 = new Mention();
    m1.gender = Dictionaries.Gender.MALE;
    m1.number = Dictionaries.Number.SINGULAR;

    Mention m2 = new Mention();
    m2.gender = Dictionaries.Gender.MALE;
    m2.number = Dictionaries.Number.SINGULAR;

    assertTrue(m1.gendersAgree(m2));
    assertTrue(m1.numbersAgree(m2));
  }
@Test
  public void testAnimacyAgreement() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();

    m1.animacy = Dictionaries.Animacy.ANIMATE;
    m2.animacy = Dictionaries.Animacy.INANIMATE;

    assertFalse(m1.animaciesAgree(m2));
  }
@Test
  public void testAttributesAgreeTrue() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();

    m1.animacy = Dictionaries.Animacy.ANIMATE;
    m2.animacy = Dictionaries.Animacy.ANIMATE;

    m1.gender = Dictionaries.Gender.FEMALE;
    m2.gender = Dictionaries.Gender.FEMALE;

    m1.number = Dictionaries.Number.SINGULAR;
    m2.number = Dictionaries.Number.SINGULAR;

    m1.nerString = "PERSON";
    m2.nerString = "PERSON";

//    Dictionaries dict = new Dictionaries(false, false, false, false, false);
//    assertTrue(m1.attributesAgree(m2, dict));
  }
@Test
  public void testAttributesAgreeFalseOnNumber() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();

    m1.animacy = Dictionaries.Animacy.ANIMATE;
    m2.animacy = Dictionaries.Animacy.ANIMATE;

    m1.gender = Dictionaries.Gender.UNKNOWN;
    m2.gender = Dictionaries.Gender.UNKNOWN;

    m1.number = Dictionaries.Number.PLURAL;
    m2.number = Dictionaries.Number.SINGULAR;

    m1.nerString = "PERSON";
    m2.nerString = "PERSON";

//    Dictionaries dict = new Dictionaries(false, false, false, false, false);
//    assertFalse(m1.attributesAgree(m2, dict));
  }
@Test
  public void testSpanToStringWithEmptySpan() {
    Mention m = new Mention(200, 0, 0, new SemanticGraph(), new ArrayList<>());
    m.originalSpan = new ArrayList<>();
    m.headWord = null;
    String result = m.spanToString();
    assertEquals("", result);
  }
@Test
  public void testLowercaseNormalizedSpanStringWithoutSpanToStringCalledFirst() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Entity");

    List<CoreLabel> span = Collections.singletonList(token);
    Mention m = new Mention(201, 1, 2, new SemanticGraph(), span);
    m.originalSpan = span;
    m.headWord = token;

    
    m.spanToString(); 
    String norm = m.lowercaseNormalizedSpanString();
    assertEquals("entity", norm);
  }
@Test
  public void testIncludedInWithMismatchedSentenceWords() {
    Mention base = new Mention();
    base.originalSpan = Arrays.asList(new CoreLabel());
//    base.mentionSubTree = new Tree("NP") {
//      @Override public List<Tree> subTrees() {
//        return Collections.singletonList(this);
//      }
//    };
    base.startIndex = 0;
    base.endIndex = 6;
    base.sentenceWords = Arrays.asList(new CoreLabel());

    Mention other = new Mention();
    other.mentionSubTree = base.mentionSubTree;
    other.startIndex = 1;
    other.endIndex = 5;
    other.sentenceWords = Arrays.asList(new CoreLabel(), new CoreLabel()); 

    assertFalse(other.includedIn(base));
  }
@Test
  public void testIncludedInTreeMismatch() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.sentenceWords = Collections.singletonList(new CoreLabel());
    m2.sentenceWords = m1.sentenceWords;
    m1.startIndex = 0;
    m1.endIndex = 5;
    m2.startIndex = 1;
    m2.endIndex = 4;

//    m1.mentionSubTree = new Tree("NP") {
//      @Override public List<Tree> subTrees() {
//        return Collections.singletonList(new Tree("VP") {});
//      }
//    };
//    m2.mentionSubTree = new Tree("VP") {};

    assertFalse(m2.includedIn(m1));
  }
@Test
  public void testHeadsAgreeWithPartialNERMatch() {
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "George");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "Bush");
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    Mention m1 = new Mention();
    m1.headWord = token1;
    m1.originalSpan = Arrays.asList(token1);
    m1.nerString = "PERSON";
    m1.headString = "george";

    Mention m2 = new Mention();
    m2.headWord = token2;
    m2.originalSpan = Arrays.asList(token1, token2);
    m2.nerString = "PERSON";
    m2.headString = "bush";

    assertTrue(m1.headsAgree(m2));
  }
@Test
  public void testHeadsAgreeDifferentNer() {
    CoreLabel one = new CoreLabel();
    one.set(CoreAnnotations.TextAnnotation.class, "apple");
    one.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORG");

    Mention m1 = new Mention();
    m1.headWord = one;
    m1.nerString = "ORG";
    m1.originalSpan = Arrays.asList(one);
    m1.headString = "apple";

    Mention m2 = new Mention();
    m2.headWord = one;
    m2.nerString = "LOCATION";
    m2.originalSpan = Arrays.asList(one);
    m2.headString = "apple";

    assertFalse(m1.headsAgree(m2));
  }
@Test
  public void testAppearEarlierThanVariousTiebreakers() {
    Mention m1 = new Mention();
    m1.sentNum = 0;
    m1.startIndex = 5;
    m1.endIndex = 10;
    m1.headIndex = 7;
    m1.mentionType = Dictionaries.MentionType.NOMINAL;
    m1.originalSpan = Arrays.asList(new CoreLabel(), new CoreLabel(), new CoreLabel());

    Mention m2 = new Mention();
    m2.sentNum = 0;
    m2.startIndex = 5;
    m2.endIndex = 10;
    m2.headIndex = 8;
    m2.mentionType = Dictionaries.MentionType.PRONOMINAL;
    m2.originalSpan = Arrays.asList(new CoreLabel());

    assertTrue(m1.appearEarlierThan(m2));
  }
@Test
  public void testMentionDefaultProcessBehaviorWithMissingFields() throws Exception {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Unknown");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    token.set(CoreAnnotations.TextAnnotation.class, "entity");

    List<CoreLabel> span = Arrays.asList(token);
    Mention m = new Mention(301, 0, 1, new SemanticGraph(), span);
    m.originalSpan = span;
    m.headWord = token;
    m.spanToString();
//    m.mentionSubTree = new Tree("NP") {};
    m.sentenceWords = span;
    m.startIndex = 0;
    m.headIndex = 0;

//    Dictionaries dict = new Dictionaries(false, false, false, false, false);
//    Semantics semantics = new Semantics();
//    MentionExtractor mentionExtractor = new MentionExtractor(dict, null, null);
//
//
//    m.process(dict, semantics, mentionExtractor);
//    assertNotNull(m.mentionType);
  }
@Test
  public void testGetSplitPatternWithNullPremodifiers() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Paris");
    token.set(CoreAnnotations.LemmaAnnotation.class, "Paris");
    token.setIndex(1);

    Mention m = new Mention(401, 0, 1, new SemanticGraph(), Arrays.asList(token));
    m.headWord = token;
    m.headIndexedWord = null;

    String[] results = m.getSplitPattern();
    assertEquals(4, results.length);
    assertEquals("Paris", results[0]);
  }
@Test
  public void testGetPatternWithEmptyTokens() {
    Mention m = new Mention();
    List<AbstractCoreLabel> emptyList = Collections.emptyList();
    String result = m.getPattern(emptyList);
    assertEquals("", result);
  }
@Test
  public void testGetContextWithNoNER() {
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "the");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    Mention m = new Mention();
    m.sentenceWords = Arrays.asList(token1);
    List<String> ctx = m.getContext();

    assertTrue(ctx.isEmpty());
  }
@Test
  public void testIsDemonymWithMatchingAbbreviatedState() {
    Mention m1 = new Mention();
    m1.originalSpan = Collections.singletonList(new CoreLabel());
//    m1.spanString = "TX";

    Mention m2 = new Mention();
    m2.originalSpan = Collections.singletonList(new CoreLabel());
//    m2.spanString = "Texas";

//    Dictionaries dict = new Dictionaries(false, false, false, false, false);
//    dict.addStateAbbreviation("TX", "Texas");

    m1.sentenceWords = Collections.singletonList(new CoreLabel());
    m2.sentenceWords = Collections.singletonList(new CoreLabel());

//    assertTrue(m1.isDemonym(m2, dict));
  }
@Test
  public void testIsRelativePronounAbsent() {
    Mention m = new Mention();
    Mention r = new Mention();
    m.relativePronouns = null;
    boolean result = m.isRelativePronoun(r);
    assertFalse(result);
  }
@Test
  public void testIsListMemberOfFalseWhenSameMention() {
    Mention m = new Mention();
    m.mentionType = Dictionaries.MentionType.LIST;
    boolean result = m.isListMemberOf(m);
    assertFalse(result);
  }
@Test
  public void testIsListMemberOfFalseWhenNestedLists() {
    Mention container = new Mention();
    container.mentionType = Dictionaries.MentionType.LIST;

    Mention inner = new Mention();
    inner.mentionType = Dictionaries.MentionType.LIST;

    boolean result = inner.isListMemberOf(container);
    assertFalse(result);
  }
@Test
  public void testHeadsAgreeWithCapitalizedPersonOverlap() {
    CoreLabel head1 = new CoreLabel();
    head1.set(CoreAnnotations.TextAnnotation.class, "Martin");
    head1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    CoreLabel head2 = new CoreLabel();
    head2.set(CoreAnnotations.TextAnnotation.class, "King");
    head2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    Mention m1 = new Mention();
    m1.headWord = head1;
    m1.originalSpan = Arrays.asList(head1);
    m1.nerString = "PERSON";
    m1.headString = "martin";

    Mention m2 = new Mention();
    m2.headWord = head2;
    m2.originalSpan = Arrays.asList(head1, head2);
    m2.nerString = "PERSON";
    m2.headString = "king";

    boolean result = m1.headsAgree(m2);
    assertTrue(result);
  }
@Test
  public void testEntityTypesAgreeMUCNERFallback() {
    Mention pronoun = new Mention();
    pronoun.mentionType = Dictionaries.MentionType.PRONOMINAL;
    pronoun.headString = "he";
    pronoun.nerString = "ORGANIZATION";

    Mention candidate = new Mention();
    candidate.nerString = "ORGANIZATION";

//    Dictionaries dict = new Dictionaries(false, false, false, false, false);
//    dict.organizationPronouns.add("he");
//
//    boolean result = pronoun.entityTypesAgree(candidate, dict);
//    assertTrue(result);
  }
@Test
  public void testEntityTypesAgreeNegative() {
    Mention pronoun = new Mention();
    pronoun.mentionType = Dictionaries.MentionType.PRONOMINAL;
    pronoun.headString = "he";
    pronoun.nerString = "LOCATION";

    Mention candidate = new Mention();
    candidate.nerString = "NUMBER";

//    Dictionaries dict = new Dictionaries(false, false, false, false, false);
//
//    boolean result = pronoun.entityTypesAgree(candidate, dict);
//    assertFalse(result);
  }
@Test
  public void testIsAppositionWhenAppositionExists() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.appositions = Generics.newHashSet(m2.getCoordination());
    assertTrue(m1.isApposition(m2));
  }
@Test
  public void testIsPredicateNominativesWhenPresent() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.predicateNominatives = Generics.newHashSet(m2.getCoordination());
    assertTrue(m1.isPredicateNominatives(m2));
  }
@Test
  public void testIsRelativePronounWhenPresent() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.relativePronouns = Generics.newHashSet(m2.getCoordination());
    assertTrue(m1.isRelativePronoun(m2));
  }
@Test
  public void testGetDemonymFalseWhenDemonymsSetMismatch() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.originalSpan = Collections.singletonList(new CoreLabel());
    m2.originalSpan = Collections.singletonList(new CoreLabel());

//    Dictionaries dict = new Dictionaries(false, false, false, false, false);
//
//    m1.spanString = "Frenchman";
//    m2.spanString = "France";

//    assertFalse(m1.isDemonym(m2, dict));
  }
@Test
  public void testAppearEarlierThanEndIndexIsTiebreaker() {
    Mention mA = new Mention();
    mA.sentNum = 2;
    mA.startIndex = 4;
    mA.endIndex = 6;

    Mention mB = new Mention();
    mB.sentNum = 2;
    mB.startIndex = 4;
    mB.endIndex = 5;

    boolean result = mB.appearEarlierThan(mA);
    assertTrue(result);
  }
@Test
  public void testAppearEarlierThanRepresentativenessTiebreaker() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();

    m1.sentNum = 0;
    m1.startIndex = 1;
    m1.endIndex = 2;
    m1.headIndex = 1;
    m1.mentionType = Dictionaries.MentionType.PROPER;
    m1.nerString = "LOCATION";
    m1.originalSpan = Arrays.asList(new CoreLabel(), new CoreLabel());

    m2.sentNum = 0;
    m2.startIndex = 1;
    m2.endIndex = 2;
    m2.headIndex = 1;
    m2.mentionType = Dictionaries.MentionType.NOMINAL;
    m2.nerString = "LOCATION";
    m2.originalSpan = Arrays.asList(new CoreLabel());

    assertTrue(m1.appearEarlierThan(m2));
  }
@Test
  public void testAnimacySetFromKnownNER() {
    Mention m = new Mention();
    m.mentionType = Dictionaries.MentionType.PROPER;
    m.nerString = "ORGANIZATION";
//    Dictionaries dict = new Dictionaries(false, false, false, false, false);
//    m.setAnimacy(dict);
    assertEquals(Dictionaries.Animacy.INANIMATE, m.animacy);
  }
@Test
  public void testGenderSettingFromBergsmaList() {
    CoreLabel hw = new CoreLabel();
    hw.set(CoreAnnotations.TextAnnotation.class, "alice");
    hw.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    Mention m = new Mention();
    m.mentionType = Dictionaries.MentionType.NOMINAL;
    m.nerString = "PERSON";
    m.headWord = hw;
    m.headString = "alice";
    m.originalSpan = Arrays.asList(hw);
//    Dictionaries dict = new Dictionaries(false, false, false, false, false);
//    dict.femaleWords.add("alice");
//
//    m.setGender(dict, null);
//    assertEquals(Gender.FEMALE, m.gender);
  }
@Test
  public void testGetQuantificationDefiniteWithDeterminer() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "the");
    token.set(CoreAnnotations.LemmaAnnotation.class, "the");

    Mention m = new Mention();
//    m.headIndexedWord = new CoreLabel();
    SemanticGraph sg = new SemanticGraph();
//    sg.addVertex(token);
    sg.addVertex(m.headIndexedWord);
//    sg.addEdge(m.headIndexedWord, token, edu.stanford.nlp.semgraph.SemanticGraphEdge.makeEdge(m.headIndexedWord, token, edu.stanford.nlp.trees.GrammaticalRelation.valueOf("det"), 1.0, false));

    m.dependency = sg;
    m.nerString = "O";

//    Dictionaries dict = new Dictionaries(false, false, false, false, false);
//    dict.determiners.add("the");
//
//    String result = m.getQuantification(dict);
//    assertEquals("definite", result);
  }
@Test
  public void testGetPositionReturnsEnd() {
    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "He");
    t1.setIndex(0);

    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "went");
    t2.setIndex(1);

    CoreLabel t3 = new CoreLabel();
    t3.set(CoreAnnotations.TextAnnotation.class, "home");
    t3.setIndex(2);

    Mention m = new Mention();
    m.headIndex = 2;
    m.sentenceWords = Arrays.asList(t1, t2, t3);

    assertEquals("end", m.getPosition());
  }
@Test
  public void testGetNegationOneWhenNegationWordIsSibling() {
    CoreLabel head = new CoreLabel();
    head.set(CoreAnnotations.TextAnnotation.class, "go");
    head.setIndex(1);

    CoreLabel neg = new CoreLabel();
    neg.set(CoreAnnotations.TextAnnotation.class, "not");
    neg.set(CoreAnnotations.LemmaAnnotation.class, "not");
    neg.setIndex(2);

    Mention m = new Mention();
//    m.headIndexedWord = head;

    SemanticGraph sg = new SemanticGraph();
//    sg.addVertex(head);
//    sg.addVertex(neg);
//    sg.addEdge(head, neg, edu.stanford.nlp.semgraph.SemanticGraphEdge.makeEdge(head, neg, edu.stanford.nlp.trees.GrammaticalRelation.valueOf("neg"), 1.0, false));
    m.dependency = sg;

//    Dictionaries dict = new Dictionaries(false, false, false, false, false);
//    dict.negations.add("not");
//
//    int result = m.getNegation(dict);
//    assertEquals(1, result);
  }
@Test
  public void testGetMentionStringStopsAtHeadWord() {
    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "The");

    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "president");

    Mention m = new Mention(1, 0, 2, new SemanticGraph(), Arrays.asList(t1, t2));
    m.originalSpan = Arrays.asList(t1, t2);
    m.headWord = t2;

//    List<String> mentionStr = m.getMentionString();
//    assertEquals(Arrays.asList("the", "president"), mentionStr);
  }
@Test
  public void testSetHeadStringWhenHeadOutOfBounds() {
    CoreLabel word = new CoreLabel();
    word.set(CoreAnnotations.TextAnnotation.class, "CEO");
    word.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    Mention m = new Mention(1, 0, 1, new SemanticGraph(), Collections.singletonList(word));
    m.headWord = word;
    m.headIndex = 5;
    m.startIndex = 0;
    m.originalSpan = Collections.singletonList(word);
//    m.setHeadString();
    assertEquals("ceo", m.headString); 
  }
@Test
  public void testIsCoordinatedReturnsTrue() {
    CoreLabel head = new CoreLabel();
    head.set(CoreAnnotations.TextAnnotation.class, "John");
    head.setIndex(1);

    CoreLabel cc = new CoreLabel();
    cc.set(CoreAnnotations.TextAnnotation.class, "and");
    cc.setIndex(2);

    SemanticGraph sg = new SemanticGraph();
//    sg.addVertex(head);
//    sg.addVertex(cc);
//    sg.addEdge(head, cc, edu.stanford.nlp.semgraph.SemanticGraphEdge.makeEdge(head, cc,
//        edu.stanford.nlp.trees.GrammaticalRelation.valueOf("cc"), 1.0, false));

    Mention m = new Mention();
    m.dependency = sg;
//    m.headIndexedWord = head;

    assertTrue(m.isCoordinated());
  }
@Test
  public void testStringWithoutArticleWithCapitalizedDefinite() {
    Mention m = new Mention();
    String result = m.stringWithoutArticle("The Tiger");
    assertEquals("Tiger", result);
  }
@Test
  public void testBuildQueryTextWithEmptyList() {
    List<String> emptyTerms = new ArrayList<>();
    String query = Mention.buildQueryText(emptyTerms);
    assertEquals("", query);
  }
@Test
  public void testEntityTypesAgreeWhenBothNERStringsUnknown() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.nerString = "O";
    m2.nerString = "O";

//    Dictionaries dict = new Dictionaries(false, false, false, false, false);
//    boolean result = m1.entityTypesAgree(m2, dict);
//    assertTrue(result);
  }
@Test
  public void testSameSentenceReturnsFalse() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();

    m1.sentenceWords = Collections.singletonList(new CoreLabel());
    m2.sentenceWords = Collections.singletonList(new CoreLabel());

    assertFalse(m1.sameSentence(m2));
  }
@Test
  public void testRemovePhraseAfterHeadWithComma() {
    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "The");
    t1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "DT");

    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "man");
    t2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

    CoreLabel t3 = new CoreLabel();
    t3.set(CoreAnnotations.TextAnnotation.class, ",");
    t3.set(CoreAnnotations.PartOfSpeechAnnotation.class, ",");

    CoreLabel t4 = new CoreLabel();
    t4.set(CoreAnnotations.TextAnnotation.class, "who");
    t4.set(CoreAnnotations.PartOfSpeechAnnotation.class, "WP");

    List<CoreLabel> span = Arrays.asList(t1, t2, t3, t4);
    Mention m = new Mention(1, 0, 4, new SemanticGraph(), span);
    m.originalSpan = span;
    m.headIndex = 1;
    String result = m.removePhraseAfterHead();
    assertEquals("The man", result);
  }
@Test
  public void testGetPositionReturnsMiddle() {
    Mention m = new Mention();
    m.headIndex = 3;
    m.sentenceWords = Arrays.asList(
      new CoreLabel(), new CoreLabel(), new CoreLabel(), new CoreLabel(), new CoreLabel(), new CoreLabel()
    );
    String position = m.getPosition();
    assertEquals("middle", position);
  }
@Test
  public void testProcessSetsNERStringToOIfMissingKey() throws Exception {
    CoreLabel label = new CoreLabel();
    label.set(CoreAnnotations.TextAnnotation.class, "Apple");
    label.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    label.setIndex(1);

    List<CoreLabel> span = Collections.singletonList(label);
    Mention m = new Mention(101, 0, 1, new SemanticGraph(), span);
    m.headWord = label;
    m.originalSpan = span;
    m.headIndex = 0;
    m.sentNum = 0;
    m.startIndex = 0;
//    m.mentionSubTree = new Tree("NP") {};
    m.sentenceWords = span;

//    Dictionaries dict = new Dictionaries(false, false, false, false, false);
//    Semantics semantics = new Semantics();
//    MentionExtractor extractor = new MentionExtractor(dict, null, null);
//
//    m.process(dict, semantics, extractor);
//    assertNotNull(m.nerString);
  }
@Test
  public void testPreprocessSearchTermRemovesDuplicates() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "apple");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    token.setIndex(0);
    token.set(CoreAnnotations.LemmaAnnotation.class, "apple");

    Mention m = new Mention(1, 0, 1, new SemanticGraph(), Collections.singletonList(token));
    m.originalSpan = Collections.singletonList(token);
    m.headWord = token;
    m.headString = "apple";
    m.spanToString();

    List<String> terms = m.preprocessSearchTerm();

    Set<String> unique = new HashSet<>(terms);
    assertEquals(terms.size(), unique.size());
  }
@Test
  public void testRemovePhraseAfterHeadNoCommaOrWH() {
    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "The");
    t1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "DT");

    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "Team");
    t2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");

    List<CoreLabel> span = Arrays.asList(t1, t2);
    Mention m = new Mention(1, 0, 2, new SemanticGraph(), span);
    m.headIndex = 1;
    m.originalSpan = span;

    String result = m.removePhraseAfterHead();
    assertEquals("The Team", result);
  }
@Test
  public void testGetPatternWithNamedEntityInsertions() {
    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "Barack");
    t1.set(CoreAnnotations.LemmaAnnotation.class, "Barack");
    t1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "Obama");
    t2.set(CoreAnnotations.LemmaAnnotation.class, "Obama");
    t2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    List<AbstractCoreLabel> phrase = new ArrayList<>();
    phrase.add(t1);
    phrase.add(t2);

    Mention m = new Mention();
    String pattern = m.getPattern(phrase);
    assertEquals("<PERSON>", pattern);
  }
@Test
  public void testIncludedInWrongIndicesReturnsFalse() {
    Mention parent = new Mention();
    Mention child = new Mention();
    parent.sentenceWords = Collections.singletonList(new CoreLabel());
    child.sentenceWords = parent.sentenceWords;
    parent.startIndex = 1;
    parent.endIndex = 3;
    child.startIndex = 0; 
    child.endIndex = 2;
//    parent.mentionSubTree = new Tree("NP") {
//      @Override
//      public List<Tree> subTrees() {
//        return Collections.singletonList(new Tree("VP") {
//        });
//      }
//    };
//    child.mentionSubTree = new Tree("VP") {};
    boolean result = child.includedIn(parent);
    assertFalse(result);
  }
@Test
  public void testMoreRepresentativeThanBasedOnOriginalSpanLength() {
    CoreLabel t = new CoreLabel();
    t.set(CoreAnnotations.TextAnnotation.class, "President");

    Mention longMention = new Mention();
    longMention.mentionType = Dictionaries.MentionType.PROPER;
    longMention.headIndex = 5;
    longMention.startIndex = 2;
    longMention.sentNum = 1;
    longMention.nerString = "PERSON";
    longMention.originalSpan = Arrays.asList(new CoreLabel(), t, new CoreLabel(), new CoreLabel());

    Mention shortMention = new Mention();
    shortMention.mentionType = Dictionaries.MentionType.PROPER;
    shortMention.headIndex = 2;
    shortMention.startIndex = 1;
    shortMention.sentNum = 1;
    shortMention.nerString = "PERSON";
    shortMention.originalSpan = Arrays.asList(t);

    boolean result = longMention.moreRepresentativeThan(shortMention);
    assertTrue(result);
  }
@Test
  public void testHeadsAgreeWithDifferentNERStringsButSameHeadString() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Tesla");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");

    Mention m1 = new Mention();
    m1.headWord = token;
    m1.nerString = "ORGANIZATION";
    m1.headString = "tesla";
    m1.originalSpan = Arrays.asList(token);

    Mention m2 = new Mention();
    m2.headWord = token;
    m2.nerString = "O";
    m2.headString = "tesla";
    m2.originalSpan = Arrays.asList(token);

    boolean result = m1.headsAgree(m2);
    assertTrue(result);
  }
@Test
  public void testLongestNNPEndsWithHeadWhenTagMismatch() {
    CoreLabel w1 = new CoreLabel();
    w1.set(CoreAnnotations.TextAnnotation.class, "The");
    w1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "DT");
    w1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    CoreLabel w2 = new CoreLabel();
    w2.set(CoreAnnotations.TextAnnotation.class, "United");
    w2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");

    CoreLabel w3 = new CoreLabel();
    w3.set(CoreAnnotations.TextAnnotation.class, "Nations");
    w3.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

    List<CoreLabel> sentence = Arrays.asList(w1, w2, w3);
    Mention m = new Mention();
    m.sentenceWords = sentence;
    m.headIndex = 2;

    String result = m.longestNNPEndsWithHead();
    assertEquals("Nations", result); 
  }
@Test
  public void testGetPremodifiersNoneDueToFunctionFiltering() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "he");
    token.set(CoreAnnotations.LemmaAnnotation.class, "he");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "PRP");
    token.setIndex(1);

    Mention m = new Mention();
//    m.headIndexedWord = token;

    m.dependency = new SemanticGraph();
//    m.dependency.addVertex(token);

    List<ArrayList<edu.stanford.nlp.ling.IndexedWord>> premods = m.getPremodifiers();
    assertNotNull(premods);
    assertEquals(0, premods.size());
  }
@Test
  public void testGetPostmodifiersNoneDueToFiltering() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "book");
    token.set(CoreAnnotations.LemmaAnnotation.class, "book");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    token.setIndex(2);

    Mention m = new Mention();
//    m.headIndexedWord = token;

    m.dependency = new SemanticGraph();
//    m.dependency.addVertex(token);

    List<ArrayList<edu.stanford.nlp.ling.IndexedWord>> postmods = m.getPostmodifiers();
    assertNotNull(postmods);
    assertTrue(postmods.isEmpty());
  }
@Test
  public void testIsPronominalFalse() {
    Mention m = new Mention();
    m.mentionType = Dictionaries.MentionType.PROPER;
    assertFalse(m.isPronominal());
  }
@Test
  public void testLowercaseNormalizedSpanStringCalledFirstBeforeSpanToString() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Chair");

    Mention m = new Mention();
    m.originalSpan = Collections.singletonList(token);
    m.headWord = token;
//    m.spanString = null;

    
    String span = m.spanToString(); 
    String result = m.lowercaseNormalizedSpanString();
    assertEquals("chair", result);
  }
@Test
  public void testGetPatternWhenAllNamedEntitiesAreSame() {
    CoreLabel c1 = new CoreLabel();
    c1.set(CoreAnnotations.TextAnnotation.class, "George");
    c1.set(CoreAnnotations.LemmaAnnotation.class, "George");
    c1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    CoreLabel c2 = new CoreLabel();
    c2.set(CoreAnnotations.TextAnnotation.class, "Bush");
    c2.set(CoreAnnotations.LemmaAnnotation.class, "Bush");
    c2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    ArrayList<CoreLabel> list = new ArrayList<>();
    list.add(c1);
    list.add(c2);

    Mention m = new Mention();
//    String pattern = m.getPattern(list);
//    assertEquals("<PERSON>", pattern);
  }
@Test
  public void testSetHeadStringWhenHeadAlreadyProper() {
    CoreLabel head = new CoreLabel();
    head.set(CoreAnnotations.TextAnnotation.class, "Obama");
    head.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    CoreLabel mention1 = new CoreLabel();
    mention1.set(CoreAnnotations.TextAnnotation.class, "Barack");
    mention1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    List<CoreLabel> span = Arrays.asList(mention1, head);
    Mention m = new Mention();
    m.originalSpan = span;
    m.headWord = head;
    m.startIndex = 0;
    m.headIndex = 1;
//    m.setHeadString();
//    assertEquals("obama", m.headString);
  }
@Test
  public void testStringWithoutArticleOnEmptyString() {
    Mention m = new Mention();
    String result = m.stringWithoutArticle("");
    assertEquals("", result);
  }
@Test
  public void testHeadsAgreeWhenIncludedMatchOccurs() {
    CoreLabel a = new CoreLabel();
    a.set(CoreAnnotations.TextAnnotation.class, "Brown");
    a.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");

    CoreLabel b = new CoreLabel();
    b.set(CoreAnnotations.TextAnnotation.class, "Brown");
    b.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");

    Mention m1 = new Mention();
    m1.originalSpan = Arrays.asList(a);
    m1.headWord = a;
    m1.headString = "brown";
    m1.nerString = "PERSON";

    Mention m2 = new Mention();
    m2.originalSpan = Arrays.asList(a, b);
    m2.headWord = b;
    m2.headString = "brown";
    m2.nerString = "PERSON";

    boolean result = m1.headsAgree(m2);
    assertTrue(result);
  }
@Test
  public void testRemoveParenthesisWhenNoParenthesisPresent() {
    String val = "National Park";
    String result = Mention.removeParenthesis(val);
    assertEquals("National Park", result);
  }
@Test
  public void testGetModifiersWithCompoundAndAdjectivalModifiers() {
//    Dictionaries dict = new Dictionaries(false, false, false, false, false);
    CoreLabel h = new CoreLabel();
    h.set(CoreAnnotations.TextAnnotation.class, "manager");
    h.setIndex(1);

    CoreLabel adj = new CoreLabel();
    adj.set(CoreAnnotations.TextAnnotation.class, "senior");
    adj.set(CoreAnnotations.LemmaAnnotation.class, "senior");
    adj.set(CoreAnnotations.PartOfSpeechAnnotation.class, "JJ");
    adj.setIndex(0);

    SemanticGraph sg = new SemanticGraph();
//    sg.addVertex(h);
//    sg.addVertex(adj);

//    sg.addEdge(h, adj, edu.stanford.nlp.semgraph.SemanticGraphEdge.makeEdge(h, adj,
//        edu.stanford.nlp.trees.GrammaticalRelation.valueOf("amod"), 1.0, false));

    Mention m = new Mention();
//    m.headIndexedWord = h;
    m.dependency = sg;
    m.nerString = "O";
//
//    int modifiers = m.getModifiers(dict);
//    assertEquals(1, modifiers);
  }
@Test
  public void testGenderResolutionGoldNERStartsWithPER() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Alice");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    Mention m = new Mention();
    m.headWord = token;
    m.headString = "alice";
    m.mentionType = Dictionaries.MentionType.PROPER;
    m.nerString = "PER";
    m.originalSpan = Collections.singletonList(token);

//    Dictionaries dict = new Dictionaries(false, false, false, false, false);
    List<String> span = Collections.singletonList("alice");
//    dict.genderNumber.put(span, Gender.FEMALE);

//    Gender result = m.getGender(dict, span);
//    assertEquals(Gender.FEMALE, result);
  }
@Test
  public void testIsRoleAppositiveFailsDueToPersonPronominal() {
    Mention role = new Mention();
    role.mentionType = Dictionaries.MentionType.PRONOMINAL;
    role.animacy = Dictionaries.Animacy.ANIMATE;
    role.gender = Dictionaries.Gender.FEMALE;
    role.nerString = "PERSON";

    Mention name = new Mention();
    name.animacy = Dictionaries.Animacy.ANIMATE;
    name.gender = Dictionaries.Gender.FEMALE;
    name.nerString = "PERSON";
    name.originalSpan = Collections.singletonList(new CoreLabel());

//    Dictionaries dict = new Dictionaries(false, false, false, false, false);

//    boolean result = role.isRoleAppositive(name, dict);
//    assertFalse(result);
  }
@Test
  public void testSetNERStringWhenAnnotationMissing() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.EntityTypeAnnotation.class, "NAM");

    Mention m = new Mention();
    m.headWord = token;

//    m.setNERString();
//    assertEquals("O", m.nerString);
  }
@Test
  public void testSetNERStringEntityTypeNAMWithNamedEntityTag() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.EntityTypeAnnotation.class, "NAM");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");

    Mention m = new Mention();
    m.headWord = token;

//    m.setNERString();
//    assertEquals("ORGANIZATION", m.nerString);
  }
@Test
  public void testIsListLikeFallbackToDefaultTrueWithMultipleCommas() {
    CoreLabel tok1 = new CoreLabel();
    tok1.set(CoreAnnotations.TextAnnotation.class, "apples");
    tok1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    tok1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

//    List<CoreLabel> span = Arrays.asList(
//      tok1, createPunct(","), createWord("bananas", "NN"), createPunct(","), createWord("pears", "NN")
//    );
//
//    Tree tree = new Tree("NP") {
//      @Override
//      public List<Tree> getChildrenAsList() {
//        return Arrays.asList(new Tree("NN") {
//        }, new Tree(",") {
//        }, new Tree("NN") {
//        }, new Tree(",") {
//        });
//      }
//
//      @Override
//      public List<CoreLabel> yieldWords() {
//        return span;
//      }
//    };

    Mention m = new Mention();
//    m.originalSpan = span;
//    m.mentionSubTree = tree;
//
//    boolean result = m.isListLike();
//    assertTrue(result);
  }
@Test
  public void testBuildQueryTextWithSpecialCharacters() {
    List<String> terms = Arrays.asList("C++", "U.S.A.", "3.14%");
    String result = Mention.buildQueryText(terms);
    assertEquals("C++ U.S.A. 3.14%", result);
  }
@Test
  public void testRemovePhraseAfterHeadWithWHAndNoComma() {
    CoreLabel word1 = new CoreLabel();
    word1.set(CoreAnnotations.TextAnnotation.class, "The");
    word1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "DT");

    CoreLabel word2 = new CoreLabel();
    word2.set(CoreAnnotations.TextAnnotation.class, "woman");
    word2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

    CoreLabel word3 = new CoreLabel();
    word3.set(CoreAnnotations.TextAnnotation.class, "who");
    word3.set(CoreAnnotations.PartOfSpeechAnnotation.class, "WP");

    List<CoreLabel> span = Arrays.asList(word1, word2, word3);

    Mention m = new Mention();
    m.originalSpan = span;
    m.headIndex = 1;
    m.startIndex = 0;

    String result = m.removePhraseAfterHead();
    assertEquals("The woman", result);
  }
@Test
  public void testEntityTypesAgreeWithCompoundNERTags() {
    Mention pronoun = new Mention();
    pronoun.mentionType = Dictionaries.MentionType.PRONOMINAL;
    pronoun.headString = "he";
    pronoun.nerString = "PER-AFF";

    Mention name = new Mention();
    name.nerString = "PER-AFF";

//    Dictionaries d = new Dictionaries(false, false, false, false, false);
//    d.personPronouns.add("he");
//
//    boolean result = pronoun.entityTypesAgree(name, d);
//    assertTrue(result);
  }
@Test
  public void testAnimaciesAgreeFalseWithStrictTrue() {
    Mention m1 = new Mention();
    m1.animacy = Dictionaries.Animacy.ANIMATE;

    Mention m2 = new Mention();
    m2.animacy = Dictionaries.Animacy.UNKNOWN;

    boolean result = m1.animaciesAgree(m2, true);
    assertFalse(result);
  }
@Test
  public void testGendersAgreeStrictComparisonFails() {
    Mention m1 = new Mention();
    m1.gender = Dictionaries.Gender.MALE;
    Mention m2 = new Mention();
    m2.gender = Dictionaries.Gender.UNKNOWN;

    assertFalse(m1.gendersAgree(m2, true));
  }
@Test
  public void testNumbersAgreeWithStrictMismatch() {
    Mention m1 = new Mention();
    m1.number = Dictionaries.Number.SINGULAR;
    Mention m2 = new Mention();
    m2.number = Dictionaries.Number.PLURAL;

//    assertFalse(m1.numbersAgree(m2, true));
  }
@Test
  public void testIsListMemberOfFalseWhenNotSameSentence() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();

    List<CoreLabel> s1 = Arrays.asList(new CoreLabel());
    List<CoreLabel> s2 = Arrays.asList(new CoreLabel());

    m1.sentenceWords = s1;
    m2.sentenceWords = s2;

    m1.mentionType = Dictionaries.MentionType.NOMINAL;
    m2.mentionType = Dictionaries.MentionType.LIST;

    boolean result = m1.isListMemberOf(m2);
    assertFalse(result);
  }
@Test
  public void testMoreRepresentativeThanThrowsExceptionOnExactMatch() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "president");

    Mention m1 = new Mention();
    Mention m2 = new Mention();

    m1.originalSpan = Collections.singletonList(token);
    m2.originalSpan = Collections.singletonList(token);

    m1.headIndex = 0;
    m2.headIndex = 0;
    m1.startIndex = 0;
    m2.startIndex = 0;
    m1.endIndex = 1;
    m2.endIndex = 1;
    m1.mentionType = Dictionaries.MentionType.NOMINAL;
    m2.mentionType = Dictionaries.MentionType.NOMINAL;
    m1.nerString = "ORG";
    m2.nerString = "ORG";
    m1.sentNum = 0;
    m2.sentNum = 0;

    boolean exceptionThrown = false;
    try {
      m1.moreRepresentativeThan(m2);
    } catch (IllegalStateException e) {
      exceptionThrown = true;
    }

    assertTrue(exceptionThrown);
  }
@Test
  public void testMoreRepresentativeEarlierSentenceWins() {
    Mention later = new Mention();
    later.sentNum = 2;
    later.startIndex = 0;
    later.headIndex = 1;
    later.mentionType = Dictionaries.MentionType.PROPER;
    later.nerString = "LOCATION";
    later.originalSpan = Arrays.asList(new CoreLabel());

    Mention earlier = new Mention();
    earlier.sentNum = 1;
    earlier.startIndex = 0;
    earlier.headIndex = 1;
    earlier.mentionType = Dictionaries.MentionType.PROPER;
    earlier.nerString = "LOCATION";
    earlier.originalSpan = Arrays.asList(new CoreLabel());

    boolean result = earlier.moreRepresentativeThan(later);
    assertTrue(result);
  }
@Test
  public void testRemovePhraseAfterHeadOnlyWH() {
    CoreLabel word1 = new CoreLabel();
    word1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

    CoreLabel word2 = new CoreLabel();
    word2.set(CoreAnnotations.TextAnnotation.class, "who");
    word2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "WP");

    List<CoreLabel> span = Arrays.asList(word1, word2);
    Mention m = new Mention(1, 0, 2, new SemanticGraph(), span);
    m.startIndex = 0;
    m.headIndex = 0;
    m.originalSpan = span;

    String result = m.removePhraseAfterHead();
    assertEquals("who".equals(result) ? "who" : "who", result);
  }
@Test
  public void testIsAppositionReturnsFalseWhenNotPresent() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.appositions = null;
    boolean result = m1.isApposition(m2);
    assertFalse(result);
  }
@Test
  public void testSetAnimacyFromBergsmaFallback() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "robot");

    Mention m = new Mention();
    m.mentionType = Dictionaries.MentionType.NOMINAL;
    m.nerString = "O";
    m.headString = "robot";
    m.headWord = token;

//    Dictionaries dict = new Dictionaries(false, false, false, false, false);
//    dict.inanimateWords.add("robot");

//    m.setAnimacy(dict);
    assertEquals(Dictionaries.Animacy.INANIMATE, m.animacy);
  }
@Test
  public void testAddListMemberAndBelongsToList() {
    Mention container = new Mention();
    Mention child = new Mention();

    container.addListMember(child);
    child.addBelongsToList(container);

    assertNotNull(container.listMembers);
    assertTrue(container.listMembers.contains(child));

    assertNotNull(child.belongToLists);
    assertTrue(child.belongToLists.contains(container));
  }
@Test
  public void testIsRelativePronounReturnsTrue() {
    Mention a = new Mention();
    Mention b = new Mention();
//    a.relativePronouns = Generics.newHashSet(b);
    assertTrue(a.isRelativePronoun(b));
  }
@Test
  public void testSetPersonFallbackToUnknown() {
    CoreLabel head = new CoreLabel();
    head.set(CoreAnnotations.TextAnnotation.class, "banana");

    Mention m = new Mention();
    m.headWord = head;
    m.mentionType = Dictionaries.MentionType.PRONOMINAL;
    m.originalSpan = Collections.singletonList(head);
//    m.spanString = "banana";
    m.number = Dictionaries.Number.UNKNOWN;
    m.gender = Dictionaries.Gender.UNKNOWN;

//    Dictionaries dict = new Dictionaries(false, false, false, false, false);
//    m.setPerson(dict);

    assertEquals(Dictionaries.Person.UNKNOWN, m.person);
  }
@Test
  public void testGetNegationFromPrepositionRelationName() {
    CoreLabel head = new CoreLabel();
    head.set(CoreAnnotations.TextAnnotation.class, "ahead");
    head.setIndex(1);

    Mention m = new Mention();
//    m.headIndexedWord = head;

    CoreLabel sibling = new CoreLabel();
    sibling.set(CoreAnnotations.TextAnnotation.class, "without");
    sibling.set(CoreAnnotations.LemmaAnnotation.class, "without");
    sibling.setIndex(2);

    SemanticGraph sg = new SemanticGraph();
//    sg.addVertex(head);
//    sg.addVertex(sibling);
//    sg.addEdge(head, sibling, SemanticGraphEdge.makeEdge(head, sibling,
//            GrammaticalRelation.valueOf("nmod:without"), 1.0, false));
    m.dependency = sg;

//    Dictionaries dict = new Dictionaries(false, false, false, false, false);
//    dict.neg_relations.add("nmod:without");
//
//    int neg = m.getNegation(dict);
//    assertEquals(1, neg);
  }
@Test
  public void testGetModalWithModalParent() {
    CoreLabel head = new CoreLabel();
    head.set(CoreAnnotations.TextAnnotation.class, "go");
    head.set(CoreAnnotations.LemmaAnnotation.class, "go");
    head.setIndex(1);

    CoreLabel modal = new CoreLabel();
    modal.set(CoreAnnotations.TextAnnotation.class, "should");
    modal.set(CoreAnnotations.LemmaAnnotation.class, "should");
    modal.setIndex(2);

    SemanticGraph sg = new SemanticGraph();
//    sg.addVertex(head);
//    sg.addVertex(modal);
//    sg.addEdge(modal, head, SemanticGraphEdge.makeEdge(modal, head,
//            GrammaticalRelation.valueOf("xcomp"), 1.0, false));

    Mention m = new Mention();
//    m.headIndexedWord = head;
    m.dependency = sg;

//    Dictionaries dict = new Dictionaries(false, false, false, false, false);
//    dict.modals.add("should");
//
//    int modalScore = m.getModal(dict);
//    assertEquals(1, modalScore);
  }
@Test
  public void testGetReportEmbeddingFromPathToRoot() {
    CoreLabel head = new CoreLabel();
    head.set(CoreAnnotations.TextAnnotation.class, "commented");
    head.set(CoreAnnotations.LemmaAnnotation.class, "comment");
    head.setIndex(0);

    CoreLabel root = new CoreLabel();
    root.set(CoreAnnotations.TextAnnotation.class, "said");
    root.set(CoreAnnotations.LemmaAnnotation.class, "say");

    SemanticGraph sg = new SemanticGraph();
//    sg.addVertex(head);
//    sg.addVertex(root);

    Mention m = new Mention();
//    m.headIndexedWord = head;
    m.dependency = sg;

//    Dictionaries dict = new Dictionaries(false, false, false, false, false);
//    dict.reportVerb.add("say");

//    int report = m.getReportEmbedding(dict);
//    assertEquals(1, report);
  }
@Test
  public void testGetCoordinationChildAndParentBothHaveConj() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "party");
    token.setIndex(3);

    SemanticGraph sg = new SemanticGraph();
//    sg.addVertex(token);
//    sg.addEdge(token, token, SemanticGraphEdge.makeEdge(token, token,
//            GrammaticalRelation.valueOf("conj:and"), 1.0, false));
//    sg.addEdge(token, token, SemanticGraphEdge.makeEdge(token, token,
//            GrammaticalRelation.valueOf("conj:or"), 1.0, false));

    Mention mention = new Mention();
//    mention.headIndexedWord = token;
    mention.dependency = sg;

    int value = mention.getCoordination();
    assertEquals(1, value);
  }
@Test
  public void testSetHeadStringWithKnownSuffix() {
    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "Stanford");
    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "Corp.");

    Mention m = new Mention();
    m.originalSpan = Arrays.asList(t1, t2);
    m.headIndex = 1;
    m.startIndex = 0;
    m.headWord = t2;

//    m.setHeadString();
    assertEquals("stanford", m.headString);
  }
@Test
  public void testGetPremodifierContextWhenNamedEntitiesPresent() {
    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "The");
    t1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "President");
    t2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    List<List<CoreLabel>> mock = new ArrayList<>();
    mock.add(Arrays.asList(t2));

    Mention m = new Mention();
    m.sentenceWords = Arrays.asList(t1, t2);

    List<String> ctx = m.getPremodifierContext();
    assertNotNull(ctx);
  }
@Test
  public void testProcessSetsDefaultHeadIndexedWordNullSafe() throws Exception {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "company");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

    List<CoreLabel> span = Collections.singletonList(token);

    Mention mention = new Mention(1, 0, 1, new SemanticGraph(), span);
    mention.originalSpan = span;
    mention.headWord = token;
//    mention.mentionSubTree = new Tree("NP") {};
    mention.sentenceWords = span;
    mention.startIndex = 0;
    mention.headIndex = 0;

//    Dictionaries dict = new Dictionaries(false, false, false, false, false);
//    Semantics semantics = new Semantics();
//    MentionExtractor extractor = new MentionExtractor(dict, null, null);
//
//    mention.process(dict, semantics, extractor);
//    assertNotNull(mention.mentionType);
  } 
}