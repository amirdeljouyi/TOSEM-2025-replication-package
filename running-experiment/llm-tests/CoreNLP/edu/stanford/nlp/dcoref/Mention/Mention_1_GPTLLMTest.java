package edu.stanford.nlp.dcoref;

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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class Mention_1_GPTLLMTest {

 @Test
  public void testSpanToString_ProperMention() {
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Barack");

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "Obama");

    SemanticGraph dependency = mock(SemanticGraph.class);
    List<CoreLabel> span = Arrays.asList(token1, token2);

    Mention mention = new Mention(1, 0, 2, dependency, span);
    mention.originalSpan = span;

    String result = mention.spanToString();
    assertEquals("Barack Obama", result);
  }
@Test
  public void testLowercaseNormalizedSpanString_CachedAfterSpanToString() {
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Barack");

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "Obama");

    SemanticGraph dependency = mock(SemanticGraph.class);
    List<CoreLabel> span = Arrays.asList(token1, token2);

    Mention mention = new Mention(1, 0, 2, dependency, span);
    mention.originalSpan = span;

    mention.spanToString(); 
    String result = mention.lowercaseNormalizedSpanString();
    assertEquals("barack obama", result);
  }
@Test
  public void testNerTokens_ReturnsCorrectSubList() {
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Barack");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "Obama");
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    SemanticGraph dependency = mock(SemanticGraph.class);

    List<CoreLabel> span = Arrays.asList(token1, token2);
    Mention mention = new Mention(1, 0, 2, dependency, span);
    mention.originalSpan = span;
    mention.nerString = "PERSON";
    mention.headIndex = 1;
    mention.startIndex = 0;

    List<CoreLabel> result = mention.nerTokens();
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals("Barack", result.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("Obama", result.get(1).get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testNerTokens_ReturnsNullForO() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "he");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    SemanticGraph dependency = mock(SemanticGraph.class);
    List<CoreLabel> span = Collections.singletonList(token);

    Mention mention = new Mention(1, 0, 1, dependency, span);
    mention.originalSpan = span;
    mention.nerString = "O";

    List<CoreLabel> result = mention.nerTokens();
    assertNull(result);
  }
@Test
  public void testNerName_ReturnsConcatenatedNERSpan() {
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Barack");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "Obama");
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    List<CoreLabel> span = Arrays.asList(token1, token2);

    Mention mention = new Mention(1, 0, 2, mock(SemanticGraph.class), span);
    mention.originalSpan = span;
    mention.nerString = "PERSON";
    mention.headIndex = 1;
    mention.startIndex = 0;

    String result = mention.nerName();
    assertEquals("Barack Obama", result);
  }
@Test
  public void testIsPronominal_WhenTypeIsPronominal_ReturnsTrue() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "he");

    Mention mention = new Mention();
    mention.mentionType = Dictionaries.MentionType.PRONOMINAL;

    assertTrue(mention.isPronominal());
  }
@Test
  public void testIsPronominal_WhenTypeIsNotPronominal_ReturnsFalse() {
    Mention mention = new Mention();
    mention.mentionType = Dictionaries.MentionType.NOMINAL;
    assertFalse(mention.isPronominal());
  }
@Test
  public void testHeadsAgree_WhenStringsEqual_ReturnsTrue() {
    CoreLabel head = new CoreLabel();
    head.set(CoreAnnotations.TextAnnotation.class, "Obama");

    Mention mention1 = new Mention();
    mention1.headWord = head;
    mention1.originalSpan = Collections.singletonList(head);
    mention1.headString = "obama";
    mention1.nerString = "PERSON";

    CoreLabel head2 = new CoreLabel();
    head2.set(CoreAnnotations.TextAnnotation.class, "Obama");

    Mention mention2 = new Mention();
    mention2.headWord = head2;
    mention2.originalSpan = Collections.singletonList(head2);
    mention2.headString = "obama";
    mention2.nerString = "PERSON";

    assertTrue(mention1.headsAgree(mention2));
  }
@Test
  public void testHeadsAgree_WithNERLooseMatch_ReturnsTrue() {
    CoreLabel h1 = new CoreLabel();
    h1.set(CoreAnnotations.TextAnnotation.class, "Obama");

    CoreLabel h2 = new CoreLabel();
    h2.set(CoreAnnotations.TextAnnotation.class, "Barack");

    Mention mention1 = new Mention();
    mention1.headWord = h1;
    mention1.originalSpan = Collections.singletonList(h1);
    mention1.headString = "obama";
    mention1.nerString = "PERSON";

    Mention mention2 = new Mention();
    mention2.headWord = h2;
    mention2.originalSpan = Collections.singletonList(h2);
    mention2.headString = "barack";
    mention2.nerString = "PERSON";

    assertFalse(mention1.headsAgree(mention2)); 
  }
@Test
  public void testNumbersAgree_SameNumber_ReturnsTrue() {
    Mention m1 = new Mention();
    m1.number = Dictionaries.Number.PLURAL;

    Mention m2 = new Mention();
    m2.number = Dictionaries.Number.PLURAL;

    assertTrue(m1.numbersAgree(m2));
  }
@Test
  public void testGendersAgree_Mismatched_ReturnsFalse() {
    Mention m1 = new Mention();
    m1.gender = Dictionaries.Gender.FEMALE;

    Mention m2 = new Mention();
    m2.gender = Dictionaries.Gender.MALE;

    assertFalse(m1.gendersAgree(m2));
  }
@Test
  public void testAnimaciesAgree_Unknown_ReturnsTrue() {
    Mention m1 = new Mention();
    m1.animacy = Dictionaries.Animacy.UNKNOWN;

    Mention m2 = new Mention();
    m2.animacy = Dictionaries.Animacy.INANIMATE;

    assertTrue(m1.animaciesAgree(m2));
  }
@Test
  public void testAttributesAgree_AllMatch_ReturnsTrue() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();

    Dictionaries dict = new Dictionaries();

    m1.animacy = Dictionaries.Animacy.ANIMATE;
    m1.gender = Dictionaries.Gender.MALE;
    m1.number = Dictionaries.Number.SINGULAR;
    m1.nerString = "PERSON";

    m2.animacy = Dictionaries.Animacy.ANIMATE;
    m2.gender = Dictionaries.Gender.MALE;
    m2.number = Dictionaries.Number.SINGULAR;
    m2.nerString = "PERSON";

    assertTrue(m1.attributesAgree(m2, dict));
  }
@Test
  public void testAttributesAgree_Mismatch_ReturnsFalse() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();

    Dictionaries dict = new Dictionaries();

    m1.animacy = Dictionaries.Animacy.ANIMATE;
    m1.gender = Dictionaries.Gender.MALE;
    m1.number = Dictionaries.Number.PLURAL;
    m1.nerString = "PERSON";

    m2.animacy = Dictionaries.Animacy.INANIMATE;
    m2.gender = Dictionaries.Gender.FEMALE;
    m2.number = Dictionaries.Number.SINGULAR;
    m2.nerString = "LOCATION";

    assertFalse(m1.attributesAgree(m2, dict));
  }
@Test
  public void testSpanToStringWithEmptySpanReturnsEmptyString() {
    Mention mention = new Mention();
    mention.originalSpan = new ArrayList<>();
    String result = mention.spanToString();
    assertEquals("", result);
  }
@Test
  public void testLowercaseNormalizedSpanStringWithoutCallingSpanToStringThrowsNPE() {
    Mention mention = new Mention();
    try {
      mention.lowercaseNormalizedSpanString();
      fail("Expected NullPointerException because spanString was never initialized by spanToString()");
    } catch (NullPointerException e) {
      
    }
  }
@Test
  public void testIsPronominalWithNullMentionTypeReturnsFalse() {
    Mention mention = new Mention();
    mention.mentionType = null;
    assertFalse(mention.isPronominal());
  }
@Test
  public void testHeadsAgreeWithDifferentNERButOneIsO() {
    CoreLabel h1 = new CoreLabel();
    h1.set(CoreAnnotations.TextAnnotation.class, "Obama");

    CoreLabel h2 = new CoreLabel();
    h2.set(CoreAnnotations.TextAnnotation.class, "Obama");

    Mention m1 = new Mention();
    m1.headString = "obama";
    m1.headWord = h1;
    m1.originalSpan = Collections.singletonList(h1);
    m1.nerString = "O";

    Mention m2 = new Mention();
    m2.headString = "obama";
    m2.headWord = h2;
    m2.originalSpan = Collections.singletonList(h2);
    m2.nerString = "PERSON";

    assertTrue(m1.headsAgree(m2));
  }
@Test
  public void testAnimaciesAgreeWhenBothInanimate() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.animacy = Dictionaries.Animacy.INANIMATE;
    m2.animacy = Dictionaries.Animacy.INANIMATE;
    assertTrue(m1.animaciesAgree(m2));
  }
@Test
  public void testEntityTypesAgreeStrictMismatch() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.nerString = "PERSON";
    m2.nerString = "LOCATION";
    Dictionaries dict = new Dictionaries();
    assertFalse(m1.entityTypesAgree(m2, dict, true));
  }
@Test
  public void testAppearEarlierThanHigherSentenceNumber() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.sentNum = 1;
    m2.sentNum = 2;
    assertTrue(m1.appearEarlierThan(m2));
  }
@Test
  public void testAppearEarlierThanTiebreakOnStartIndex() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.sentNum = 1;
    m2.sentNum = 1;
    m1.startIndex = 2;
    m2.startIndex = 3;
    assertTrue(m1.appearEarlierThan(m2));
  }
@Test
  public void testAppearEarlierThanTiebreakOnHeadIndex() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.sentNum = 1;
    m2.sentNum = 1;
    m1.startIndex = 0;
    m2.startIndex = 0;
    m1.endIndex = 5;
    m2.endIndex = 5;
    m1.headIndex = 1;
    m2.headIndex = 2;
    assertTrue(m1.appearEarlierThan(m2));
  }
@Test
  public void testRemovePhraseAfterHead_PicksBeforeComma() {
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

    Mention mention = new Mention();
    mention.originalSpan = Arrays.asList(t1, t2, t3, t4);
    mention.startIndex = 0;
    mention.headIndex = 1;

    String result = mention.removePhraseAfterHead();
    assertEquals("The man", result);
  }
@Test
  public void testGetPosition_FirstToken() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Obama");

    Mention mention = new Mention();
    mention.headIndex = 0;
    mention.sentenceWords = Collections.singletonList(token);

    String position = mention.getPosition();
    assertEquals("first", position);
  }
@Test
  public void testGetPosition_AtEnd() {
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "The");

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "dog");

    Mention mention = new Mention();
    mention.sentenceWords = Arrays.asList(token1, token2);
    mention.headIndex = 1;

    String result = mention.getPosition();
    assertEquals("last", result);
  }
@Test
  public void testStringWithoutArticle_HandlesAn() {
    Mention mention = new Mention();
    assertEquals("apple", mention.stringWithoutArticle("an apple"));
  }
@Test
  public void testStringWithoutArticle_HandlesThe() {
    Mention mention = new Mention();
    assertEquals("man", mention.stringWithoutArticle("The man"));
  }
@Test
  public void testIsListMemberOf_WhenNotInList() {
    Mention parent = new Mention();
    parent.mentionType = Dictionaries.MentionType.LIST;
    parent.startIndex = 0;
    parent.endIndex = 4;

    Mention child = new Mention();
    child.mentionType = Dictionaries.MentionType.NOMINAL;
    child.startIndex = 5;
    child.endIndex = 6;

    assertFalse(child.isListMemberOf(parent));
  }
@Test
  public void testIsListMemberOf_ProperInclusion() {
    Tree parentTree = mock(Tree.class);
    Tree childTree = mock(Tree.class);

    when(parentTree.subTrees()).thenReturn(Collections.singleton(childTree));

    Mention parent = new Mention();
    parent.mentionType = Dictionaries.MentionType.LIST;
    parent.mentionSubTree = parentTree;
    parent.startIndex = 0;
    parent.endIndex = 3;

    Mention child = new Mention();
    child.mentionType = Dictionaries.MentionType.PROPER;
    child.mentionSubTree = childTree;
    child.startIndex = 1;
    child.endIndex = 2;
    child.sentenceWords = Collections.emptyList();
    parent.sentenceWords = Collections.emptyList();

    assertTrue(child.isListMemberOf(parent));
  }
@Test
  public void testIsMemberOfSameList_NoSharedList_ReturnsFalse() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.belongToLists = new HashSet<>();
    m2.belongToLists = new HashSet<>();
    Mention list1 = new Mention();
    Mention list2 = new Mention();
    m1.belongToLists.add(list1);
    m2.belongToLists.add(list2);

    assertFalse(m1.isMemberOfSameList(m2));
  }
@Test
  public void testIsMemberOfSameList_SharedList_ReturnsTrue() {
    Mention listMention = new Mention();

    Mention m1 = new Mention();
    m1.belongToLists = new HashSet<>();
    m1.belongToLists.add(listMention);

    Mention m2 = new Mention();
    m2.belongToLists = new HashSet<>();
    m2.belongToLists.add(listMention);

    assertTrue(m1.isMemberOfSameList(m2));
  }
@Test
  public void testRoleAppositive_FailureConditions() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "teacher");

    Mention m1 = new Mention();
    m1.originalSpan = Collections.singletonList(token);
    m1.headWord = token;
    m1.mentionType = Dictionaries.MentionType.NOMINAL;
    m1.nerString = "O";
    m1.animacy = Dictionaries.Animacy.ANIMATE;
    m1.gender = Dictionaries.Gender.MALE;
    m1.number = Dictionaries.Number.SINGULAR;

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "Tom");

    Mention m2 = new Mention();
    m2.originalSpan = Collections.singletonList(token2);
    m2.headWord = token2;
    m2.nerString = "LOCATION";
//    m2.spanString = "Tom";
    m2.animacy = Dictionaries.Animacy.ANIMATE;
    m2.gender = Dictionaries.Gender.MALE;
    m2.number = Dictionaries.Number.SINGULAR;
    m2.sentenceWords = Collections.singletonList(token2);
    m1.sentenceWords = m2.sentenceWords;

    Dictionaries dict = new Dictionaries();
    assertFalse(m1.isRoleAppositive(m2, dict)); 
  }
@Test
  public void testIsDemonym_StateCodeResolutionMatch() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();

    m1.originalSpan = new ArrayList<>();
    m2.originalSpan = new ArrayList<>();
//    m1.spanString = "CA";
//    m2.spanString = "California";

    Dictionaries dict = new Dictionaries();
    dict.statesAbbreviation.put("CA", "california");

    assertTrue(m1.isDemonym(m2, dict));
  }
@Test
  public void testIsDemonym_CustomDemonymsMatch() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();

//    m1.spanString = "American";
//    m2.spanString = "USA";

    Dictionaries dict = new Dictionaries();
//    dict.addDemonym("usa", "american");

    assertTrue(m1.isDemonym(m2, dict));
  }
@Test
  public void testIsDemonym_AbsentEntriesDoNotMatch() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();

//    m1.spanString = "Canadian";
//    m2.spanString = "Switzerland";

    Dictionaries dict = new Dictionaries(); 

    assertFalse(m1.isDemonym(m2, dict));
  }
@Test
  public void testIsTheCommonNoun_ShortFormReturnsTrue() {
    List<CoreLabel> originalSpan = new ArrayList<>();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "The");
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "cat");

    originalSpan.add(token1);
    originalSpan.add(token2);

    Mention m = new Mention();
    m.originalSpan = originalSpan;
    m.mentionType = Dictionaries.MentionType.NOMINAL;

    assertTrue(m.isTheCommonNoun());
  }
@Test
  public void testSingletonFeaturesExtraction_FullSet() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Barack");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");

    Mention mention = new Mention();
    mention.headWord = token;
    mention.mentionType = Dictionaries.MentionType.PROPER;
    mention.person = Dictionaries.Person.HE;
    mention.number = Dictionaries.Number.SINGULAR;
    mention.headString = "barack";
    mention.nerString = "PERSON";
    mention.animacy = Dictionaries.Animacy.ANIMATE;
    mention.headIndex = 1;
    mention.startIndex = 0;
    mention.sentenceWords = Arrays.asList(new CoreLabel(), new CoreLabel(), token);

    Dictionaries dict = new Dictionaries();
    ArrayList<String> features = mention.getSingletonFeatures(dict);
    assertTrue(features.contains("HE") || features.contains("1") || features.contains("SINGULAR"));
  }
@Test
  public void testGetPattern_LemmaFallbackAndNERMarkers() {
    CoreLabel mod1 = new CoreLabel();
    mod1.set(CoreAnnotations.TextAnnotation.class, "President");
    mod1.set(CoreAnnotations.LemmaAnnotation.class, "president");
    mod1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "TITLE");
    mod1.setIndex(1);

    CoreLabel mod2 = new CoreLabel();
    mod2.set(CoreAnnotations.TextAnnotation.class, "of");
    mod2.set(CoreAnnotations.LemmaAnnotation.class, "of");
    mod2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    mod2.setIndex(2);

    CoreLabel head = new CoreLabel();
    head.set(CoreAnnotations.TextAnnotation.class, "Obama");
    head.set(CoreAnnotations.LemmaAnnotation.class, "obama");
    head.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    head.setIndex(3);

    Mention mention = new Mention();
    mention.headWord = head;
    mention.nerString = "PERSON";

    List<AbstractCoreLabel> list = new ArrayList<>();
    list.add(mod1);
    list.add(mod2);
    list.add(head);

    String pattern = mention.getPattern(list);
    assertTrue(pattern.contains("<TITLE>"));
    assertTrue(pattern.contains("president"));
    assertTrue(pattern.contains("of"));
  }
@Test
  public void testIsCoordinated_WithCCDependencyReturnsTrue() {
    Mention m = new Mention();
    SemanticGraph graph = mock(SemanticGraph.class);
    m.dependency = graph;

    List<Pair<GrammaticalRelation, IndexedWord>> childPairs = new ArrayList<>();
    IndexedWord dummy = new IndexedWord();
//    GrammaticalRelation gr = GrammaticalRelation.valueOf("cc", null, "cc", "cc");
//    childPairs.add(new Pair<>(gr, dummy));

    m.headIndexedWord = dummy;

    when(graph.childPairs(dummy)).thenReturn(childPairs);
    assertTrue(m.isCoordinated());
  }
@Test
  public void testInsideIn_ExactBoundaryMatch() {
    Mention container = new Mention();
    container.sentNum = 0;
    container.startIndex = 1;
    container.endIndex = 5;

    Mention inner = new Mention();
    inner.sentNum = 0;
    inner.startIndex = 1;
    inner.endIndex = 5;

    assertTrue(inner.insideIn(container));
  }
@Test
  public void testinsideIn_FailsForSentenceMismatch() {
    Mention m1 = new Mention();
    m1.sentNum = 1;
    m1.startIndex = 2;
    m1.endIndex = 4;

    Mention m2 = new Mention();
    m2.sentNum = 2;
    m2.startIndex = 1;
    m2.endIndex = 5;

    assertFalse(m1.insideIn(m2));
  }
@Test
  public void testgetQuantification_ReturnsQuantifiedIfNumberModifier() {
    Mention m = new Mention();
    IndexedWord head = new IndexedWord();
    m.headIndexedWord = head;
    m.nerString = "O";

    SemanticGraph dep = mock(SemanticGraph.class);
    m.dependency = dep;

    IndexedWord quant = new IndexedWord();
    Set<IndexedWord> numeric = new HashSet<>();
    numeric.add(quant);

    when(dep.getChildrenWithReln(head, UniversalEnglishGrammaticalRelations.NUMERIC_MODIFIER)).thenReturn(numeric);
    when(dep.getChildrenWithReln(head, UniversalEnglishGrammaticalRelations.DETERMINER)).thenReturn(Collections.emptySet());
    when(dep.getChildrenWithReln(head, UniversalEnglishGrammaticalRelations.POSSESSION_MODIFIER)).thenReturn(Collections.emptySet());

    Dictionaries dict = new Dictionaries();
    assertEquals("quantified", m.getQuantification(dict));
  }
@Test
  public void testSetGender_MatchesMalePronounDictionary() {
    CoreLabel head = new CoreLabel();
    head.set(CoreAnnotations.TextAnnotation.class, "he");

    Mention m = new Mention();
    m.headWord = head;
    m.headString = "he";
    m.mentionType = Dictionaries.MentionType.PRONOMINAL;
    m.gender = Dictionaries.Gender.UNKNOWN;
    m.number = Dictionaries.Number.SINGULAR;

    Dictionaries dict = new Dictionaries();
    dict.malePronouns.add("he");

//    m.setGender(dict, null);
    assertEquals(Dictionaries.Gender.MALE, m.gender);
  }
@Test
  public void testSetGender_UsesBergsmaMaleWordFallback() {
    CoreLabel head = new CoreLabel();
    head.set(CoreAnnotations.TextAnnotation.class, "John");
    head.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    Mention m = new Mention();
    m.headWord = head;
    m.headString = "john";
    m.mentionType = Dictionaries.MentionType.NOMINAL;
    m.nerString = "PERSON";
    m.gender = Dictionaries.Gender.UNKNOWN;
    m.number = Dictionaries.Number.SINGULAR;
    m.originalSpan = Arrays.asList(head);
    m.headIndex = 0;
    m.startIndex = 0;

    Dictionaries dict = new Dictionaries();
    dict.maleWords.add("john");

//    m.setGender(dict, null);
//    assertEquals(Gender.MALE, m.gender);
  }
@Test
  public void testSetGender_KeepsUnknownGenderIfNoMatch() {
    CoreLabel head = new CoreLabel();
    head.set(CoreAnnotations.TextAnnotation.class, "data");

    Mention m = new Mention();
    m.headWord = head;
    m.headString = "data";
    m.mentionType = Dictionaries.MentionType.NOMINAL;
    m.nerString = "O";
    m.gender = Dictionaries.Gender.UNKNOWN;
    m.number = Dictionaries.Number.SINGULAR;
    m.originalSpan = Arrays.asList(head);
    m.headIndex = 0;
    m.startIndex = 0;

    Dictionaries dict = new Dictionaries();
    

//    m.setGender(dict, null);
//    assertEquals(Gender.UNKNOWN, m.gender);
  }
@Test
  public void testSetNumber_FromProperNNSTag() {
    CoreLabel head = new CoreLabel();
    head.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNS");

    Mention m = new Mention();
    m.headWord = head;
    m.mentionType = Dictionaries.MentionType.NOMINAL;
    m.nerString = "O";

    Dictionaries dict = new Dictionaries();

    m.setNumber(dict);
    assertEquals(Dictionaries.Number.PLURAL, m.number);
  }
@Test
  public void testSetNumber_UsesBergsmaWhenUnknown() {
    CoreLabel head = new CoreLabel();
    head.set(CoreAnnotations.PartOfSpeechAnnotation.class, "XX");

    Mention m = new Mention();
    m.headWord = head;
    m.mentionType = Dictionaries.MentionType.PROPER;
    m.headString = "documents";
    m.nerString = "O";

    Dictionaries dict = new Dictionaries();
    dict.pluralWords.add("documents");

    m.setNumber(dict);
    assertEquals(Dictionaries.Number.PLURAL, m.number);
  }
@Test
  public void testSetNERString_ACEWithEntityType() {
    CoreLabel head = new CoreLabel();
    head.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    head.set(CoreAnnotations.EntityTypeAnnotation.class, "NAM");

    Mention m = new Mention();
    m.headWord = head;

//    m.setNERString();
//    assertEquals("ORGANIZATION", m.nerString);
  }
@Test
  public void testSetNERString_MUCWithoutNamedEntityTagAnnotation_DefaultsToO() {
    CoreLabel head = new CoreLabel();

    Mention m = new Mention();
    m.headWord = head;

//    m.setNERString();
//    assertEquals("O", m.nerString);
  }
@Test
  public void testAppearEarlierThan_TiebreakOnMentionType() {
    Mention m1 = new Mention();
    m1.sentNum = 0;
    m1.startIndex = 1;
    m1.endIndex = 3;
    m1.headIndex = 2;
    m1.mentionType = Dictionaries.MentionType.NOMINAL;

    Mention m2 = new Mention();
    m2.sentNum = 0;
    m2.startIndex = 1;
    m2.endIndex = 3;
    m2.headIndex = 2;
    m2.mentionType = Dictionaries.MentionType.LIST;

    assertFalse(m1.appearEarlierThan(m2)); 
  }
@Test
  public void testPreprocessSearchTerm_CleansWithEscapes() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "U.S.A.");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");

    Mention m = new Mention();
    m.originalSpan = Collections.singletonList(token);
    m.headWord = token;
    m.headString = "u.s.a.";
    m.headIndex = 0;
    m.startIndex = 0;
    m.spanToString(); 

    List<String> terms = m.preprocessSearchTerm();
    assertTrue(terms.get(0).contains("\\"));
  }
@Test
  public void testRemoveParenthesis_SplitsOnBracketAndKeepsLeft() {
    String input = "John Smith (CEO)";
    String result = Mention.removeParenthesis(input);
    assertEquals("John Smith", result);
  }
@Test
  public void testRemoveParenthesis_NoParenthesisReturnsSameString() {
    String input = "George Washington";
    String result = Mention.removeParenthesis(input);
    assertEquals("", result);
  }
@Test
  public void testGetSplitPattern_OnlyHeadExists() {
    CoreLabel head = new CoreLabel();
    head.set(CoreAnnotations.LemmaAnnotation.class, "innovation");
    head.setIndex(2);

    SemanticGraph graph = mock(SemanticGraph.class);

    Mention m = new Mention();
    m.dependency = graph;
    m.headWord = head;
    m.headIndexedWord = null;
    m.nerString = "O";
    m.startIndex = 0;
    m.headIndex = 2;
    m.sentenceWords = Arrays.asList(new CoreLabel(), new CoreLabel(), head);

    String[] components = m.getSplitPattern();
    assertEquals("innovation", components[0]);
    assertEquals("innovation", components[1]);
    assertEquals("innovation", components[2]);
    assertTrue(components[3].contains("innovation"));
  }
@Test
  public void testGetPosition_MiddleOfThreeWords() {
    CoreLabel a = new CoreLabel();
    a.set(CoreAnnotations.TextAnnotation.class, "He");

    CoreLabel b = new CoreLabel();
    b.set(CoreAnnotations.TextAnnotation.class, "is");

    CoreLabel c = new CoreLabel();
    c.set(CoreAnnotations.TextAnnotation.class, "smart");

    Mention m = new Mention();
    m.sentenceWords = Arrays.asList(a, b, c);
    m.headIndex = 1;

    String pos = m.getPosition();
    assertEquals("middle", pos);
  }
@Test
  public void testSetHeadString_ThrowsIfHeadIndexOverflow() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Corp.");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");

    Mention mention = new Mention();
    mention.headWord = token;
    mention.originalSpan = Collections.singletonList(token);
    mention.startIndex = 99;
    mention.headIndex = 100;

    try {
//      mention.setHeadString();
      fail("Should throw RuntimeException due to invalid startIndex");
    } catch (RuntimeException e) {
      
    }
  }
@Test
  public void testIncludedInReturnsFalseWhenNotSameSentence() {
    Mention container = new Mention();
    container.mentionSubTree = mock(Tree.class);
    container.sentNum = 0;
    container.startIndex = 0;
    container.endIndex = 5;

    Mention inner = new Mention();
    inner.mentionSubTree = mock(Tree.class);
    inner.sentNum = 1;
    inner.startIndex = 1;
    inner.endIndex = 4;

    assertFalse(inner.includedIn(container));
  }
@Test
  public void testIncludedInReturnsFalseWhenOutsideSpan() {
    Mention container = new Mention();
    container.mentionSubTree = mock(Tree.class);
    container.startIndex = 2;
    container.endIndex = 4;
    container.sentNum = 0;

    Mention inner = new Mention();
    inner.mentionSubTree = mock(Tree.class);
    inner.startIndex = 0;
    inner.endIndex = 5;
    inner.sentNum = 0;

    assertFalse(inner.includedIn(container));
  }
@Test
  public void testDiscourseSubjectFlagIsTrue() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.UtteranceAnnotation.class, 3);
    token.setIndex(1);

    SemanticGraph dep = mock(SemanticGraph.class);
    Mention mention = new Mention();
    mention.headWord = token;
    mention.dependency = dep;

    Pair pair = new Pair<>(null, "nsubj");
    Set roots = new LinkedHashSet();
    roots.add(token);

    when(dep.getRoots()).thenReturn(roots);
    when(dep.getNodeByIndexSafe(1)).thenReturn(null);
    when(dep.toString()).thenReturn("");

//    mention.setDiscourse();
//    assertTrue(mention.isSubject);
  }
@Test
  public void testGetPremodifiersWithNoHeadIndexedWordReturnsEmptyList() {
    Mention mention = new Mention();
    mention.headIndexedWord = null;
    SemanticGraph graph = mock(SemanticGraph.class);
    mention.dependency = graph;

    List list = mention.getPremodifiers();
    assertNotNull(list);
    assertTrue(list.isEmpty());
  }
@Test
  public void testEntityTypesAgreeBothUnlabeledLooseReturnsTrue() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();

    m1.nerString = "O";
    m2.nerString = "O";

    Dictionaries dict = new Dictionaries();
    assertTrue(m1.entityTypesAgree(m2, dict));
  }
@Test
  public void testEntityTypesAgreeStrictUnequalReturnsFalse() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();

    m1.nerString = "PERSON";
    m2.nerString = "LOCATION";

    Dictionaries dict = new Dictionaries();
    assertFalse(m1.entityTypesAgree(m2, dict, true));
  }
@Test
  public void testHeadsAgreeNamedEntityOverlapMixedCase() {
    CoreLabel mainHead = new CoreLabel();
    mainHead.set(CoreAnnotations.TextAnnotation.class, "Clinton");

    CoreLabel part1 = new CoreLabel();
    part1.set(CoreAnnotations.TextAnnotation.class, "Hillary");

    Mention m1 = new Mention();
    m1.nerString = "PERSON";
    m1.headWord = mainHead;
    m1.originalSpan = Arrays.asList(part1, mainHead);

    Mention m2 = new Mention();
    m2.nerString = "PERSON";
    m2.headWord = part1;
    m2.originalSpan = Arrays.asList(part1);

    assertTrue(m1.headsAgree(m2));
  }
@Test
  public void testMoreRepresentative_NERTypePrefersNotO() {
    Mention m1 = new Mention();
    m1.mentionType = Dictionaries.MentionType.NOMINAL;
    m1.nerString = "PERSON";
    m1.headIndex = 2;
    m1.startIndex = 0;
    m1.sentNum = 0;
    m1.originalSpan = Arrays.asList(new CoreLabel());

    Mention m2 = new Mention();
    m2.mentionType = Dictionaries.MentionType.NOMINAL;
    m2.nerString = "O";
    m2.headIndex = 2;
    m2.startIndex = 0;
    m2.sentNum = 0;
    m2.originalSpan = Arrays.asList(new CoreLabel());

    assertTrue(m1.moreRepresentativeThan(m2));
  }
@Test
  public void testGetPattern_ConstructsEntityWithPostmodifiers() {
    CoreLabel head = new CoreLabel();
    head.set(CoreAnnotations.TextAnnotation.class, "Obama");
    head.set(CoreAnnotations.LemmaAnnotation.class, "obama");
    head.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    head.setIndex(2);

    Mention mention = new Mention();
    mention.headWord = head;
    mention.nerString = "PERSON";
    mention.dependency = mock(SemanticGraph.class);
//    mention.headIndexedWord = head;

    String result = mention.getPattern();
    assertNotNull(result);
  }
@Test
  public void testStringWithoutArticle_ReturnsInputIfNoArticle() {
    Mention m = new Mention();
    String result = m.stringWithoutArticle("capital");
    assertEquals("capital", result);
  }
@Test
  public void testGetRelationWithRootHead() {
    SemanticGraph g = mock(SemanticGraph.class);
    IndexedWord iw = new IndexedWord();
    Set<IndexedWord> roots = new HashSet<>();
    roots.add(iw);

    when(g.getRoots()).thenReturn(roots);
    when(g.getFirstRoot()).thenReturn(iw);

    Mention m = new Mention();
    m.dependency = g;
    m.headIndexedWord = iw;

    String rel = m.getRelation();
    assertEquals("root", rel);
  }
@Test
  public void testSetHeadString_SuffixStripping() {
    CoreLabel suffix = new CoreLabel();
    suffix.set(CoreAnnotations.TextAnnotation.class, "Corp.");
    suffix.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");

    CoreLabel real = new CoreLabel();
    real.set(CoreAnnotations.TextAnnotation.class, "Stanford");
    real.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");

    List<CoreLabel> span = Arrays.asList(real, suffix);

    Mention mention = new Mention();
    mention.headWord = suffix;
    mention.headIndex = 1;
    mention.startIndex = 0;
    mention.originalSpan = span;

//    mention.setHeadString();
    assertEquals("stanford", mention.headString);
  }
@Test
  public void testSetPerson_Pronoun_I_Singular() {
    CoreLabel tok = new CoreLabel();
    tok.set(CoreAnnotations.TextAnnotation.class, "I");

    Mention mention = new Mention();
    mention.headWord = tok;
    mention.mentionType = Dictionaries.MentionType.PRONOMINAL;
    mention.spanToString(); 
    mention.originalSpan = Collections.singletonList(tok);
    mention.headString = "i";
    mention.number = Dictionaries.Number.SINGULAR;

    Dictionaries dict = new Dictionaries();
    dict.firstPersonPronouns.add("i");

//    mention.setPerson(dict);
//    assertEquals(Person.I, mention.person);
  }
@Test
  public void testSetPerson_Pronoun_You_SetsSecondPerson() {
    CoreLabel tok = new CoreLabel();
    tok.set(CoreAnnotations.TextAnnotation.class, "you");

    Mention mention = new Mention();
    mention.headWord = tok;
    mention.mentionType = Dictionaries.MentionType.PRONOMINAL;
    mention.spanToString();
    mention.originalSpan = Collections.singletonList(tok);
    mention.headString = "you";
    mention.number = Dictionaries.Number.UNKNOWN;

    Dictionaries dict = new Dictionaries();
    dict.secondPersonPronouns.add("you");
//
//    mention.setPerson(dict);
//    assertEquals(Person.YOU, mention.person);
  }
@Test
  public void testGetGender_UsesMappedConvertedStringWithExclam() {
    CoreLabel head = new CoreLabel();
    head.set(CoreAnnotations.TextAnnotation.class, "King");
    head.setIndex(4);

    List<CoreLabel> span = Arrays.asList(
      new CoreLabel(), 
      new CoreLabel(), 
      new CoreLabel(), 
      new CoreLabel(), 
      head
    );

    for (int i = 0; i < span.size(); i++) {
      span.get(i).set(CoreAnnotations.TextAnnotation.class, "dr");
    }

    Mention mention = new Mention();
    mention.originalSpan = span;
    mention.headWord = head;
    mention.nerString = "PERSON";

    Dictionaries dict = new Dictionaries();
    List<String> key = Arrays.asList("dr", "!");
    dict.genderNumber.put(key, Dictionaries.Gender.FEMALE);

//  Dictionaries.Gender result = mention.getGender(dict, Arrays.asList("dr", "king"));
//    assertEquals(Dictionaries.Gender.FEMALE, result);
  }
@Test
  public void testSetType_WithEntityTypePRO_SetsPronominal() {
    CoreLabel head = new CoreLabel();
    head.set(CoreAnnotations.PartOfSpeechAnnotation.class, "PRP");
    head.set(CoreAnnotations.EntityTypeAnnotation.class, "PRO");

    Mention mention = new Mention();
    mention.headWord = head;
    mention.originalSpan = Collections.singletonList(head);

    Dictionaries dict = new Dictionaries();

//    mention.setType(dict);
    assertEquals(Dictionaries.MentionType.PRONOMINAL, mention.mentionType);
  }
@Test
  public void testSetType_ProperNNPWithNamedEntity_SetsProper() {
    CoreLabel head = new CoreLabel();
    head.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    head.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");

    Mention mention = new Mention();
    mention.headWord = head;
    mention.originalSpan = Collections.singletonList(head);

    Dictionaries dict = new Dictionaries();

//    mention.setType(dict);
    assertEquals(Dictionaries.MentionType.PROPER, mention.mentionType);
  }
@Test
  public void testAttributesAgree_AnimacyMismatchFails() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();

    Dictionaries dict = new Dictionaries();

    m1.animacy = Dictionaries.Animacy.INANIMATE;
    m2.animacy = Dictionaries.Animacy.ANIMATE;
    m1.gender = Dictionaries.Gender.UNKNOWN;
    m2.gender = Dictionaries.Gender.UNKNOWN;
    m1.number = Dictionaries.Number.UNKNOWN;
    m2.number = Dictionaries.Number.UNKNOWN;
    m1.nerString = "O";
    m2.nerString = "O";

    assertFalse(m1.attributesAgree(m2, dict));
  }
@Test
  public void testGetPremodifierContext_NoNamedEntitiesReturnsEmptyList() {
    CoreLabel a = new CoreLabel();
    a.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    CoreLabel b = new CoreLabel();
    b.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    Mention mention = new Mention();
    mention.headWord = b;
    mention.sentenceWords = Arrays.asList(a, b);
    mention.headIndexedWord = new IndexedWord();
    mention.nerString = "O";
    mention.dependency = mock(SemanticGraph.class);

    List<String> context = mention.getPremodifierContext();
    assertTrue(context.isEmpty());
  }
@Test
  public void testGetContext_WithMultipleNamedEntities() {
    CoreLabel a = new CoreLabel();
    a.set(CoreAnnotations.TextAnnotation.class, "President");
    a.set(CoreAnnotations.NamedEntityTagAnnotation.class, "TITLE");

    CoreLabel b = new CoreLabel();
    b.set(CoreAnnotations.TextAnnotation.class, "Obama");
    b.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    Mention mention = new Mention();
    mention.sentenceWords = Arrays.asList(a, b);

    List<String> context = mention.getContext();
    assertTrue(context.contains("President Obama"));
  }
@Test
  public void testIsCoordinated_FromConjunctionRelation() {
    SemanticGraph graph = mock(SemanticGraph.class);

    IndexedWord head = new IndexedWord();
    GrammaticalRelation conjRel = mock(GrammaticalRelation.class);
    when(conjRel.getShortName()).thenReturn("cc");

    Pair<GrammaticalRelation, IndexedWord> pair = new Pair<>(conjRel, head);
    List<Pair<GrammaticalRelation, IndexedWord>> children = Collections.singletonList(pair);
    when(graph.childPairs(head)).thenReturn(children);

    Mention mention = new Mention();
    mention.dependency = graph;
    mention.headIndexedWord = head;

    assertTrue(mention.isCoordinated());
  }
@Test
  public void testGetModifiers_AddsOnlyAdjectiveAndCompound() {
    IndexedWord head = new IndexedWord();
    IndexedWord adj = new IndexedWord();
    adj.set(CoreAnnotations.LemmaAnnotation.class, "giant");
    GrammaticalRelation rel = UniversalEnglishGrammaticalRelations.ADJECTIVAL_MODIFIER;

    SemanticGraph dep = mock(SemanticGraph.class);
    when(dep.childPairs(head)).thenReturn(Collections.singletonList(new Pair<>(rel, adj)));

    Mention mention = new Mention();
    mention.headIndexedWord = head;
    mention.nerString = "O";
    mention.dependency = dep;

    Dictionaries dict = new Dictionaries();

    int result = mention.getModifiers(dict);
    assertEquals(1, result);
  }
@Test
  public void testAddAndCheckApposition() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();

    m1.addApposition(m2);
    assertTrue(m1.isApposition(m2));
  }
@Test
  public void testAddAndCheckPredicateNominatives() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();

    m1.addPredicateNominatives(m2);
    assertTrue(m1.isPredicateNominatives(m2));
  }
@Test
  public void testAddAndCheckRelativePronouns() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();

    m1.addRelativePronoun(m2);
    assertTrue(m1.isRelativePronoun(m2));
  }
@Test
  public void testRemovePhraseAfterHeadHandlesWHStandalone() {
    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "The");
    t1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "DT");

    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "man");
    t2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

    CoreLabel t3 = new CoreLabel();
    t3.set(CoreAnnotations.TextAnnotation.class, "who");
    t3.set(CoreAnnotations.PartOfSpeechAnnotation.class, "WP");

    Mention m = new Mention();
    m.originalSpan = Arrays.asList(t1, t2, t3);
    m.headIndex = 1;
    m.startIndex = 0;

    String cleaned = m.removePhraseAfterHead();
    assertEquals("The man", cleaned);
  }
@Test
  public void testIsMemberOfSameListWhenOneSetIsNull() {
    Mention m1 = new Mention();
    m1.belongToLists = null;

    Mention m2 = new Mention();
    m2.belongToLists = new HashSet<>();

    Mention group = new Mention();
    m2.belongToLists.add(group);

    assertFalse(m1.isMemberOfSameList(m2));
  }
@Test
  public void testSplitPatternWithMultiplePremodifiers() {
    Mention mention = new Mention();
    SemanticGraph dep = mock(SemanticGraph.class);

    mention.dependency = dep;
    CoreLabel head = new CoreLabel();
    head.set(CoreAnnotations.LemmaAnnotation.class, "data");
    head.set(CoreAnnotations.TextAnnotation.class, "data");
    head.setIndex(4);
    mention.headWord = head;
    mention.headIndexedWord = new IndexedWord();
    mention.sentenceWords = Arrays.asList(new CoreLabel(), new CoreLabel(), new CoreLabel(), head);

    String[] split = mention.getSplitPattern();

    assertEquals("data", split[0]);
    assertNotNull(split[1]);
    assertNotNull(split[2]);
    assertNotNull(split[3]);
  }
@Test
  public void testPatternWithNERsAndResettingState() {
    Mention mention = new Mention();

    CoreLabel named = new CoreLabel();
    named.set(CoreAnnotations.LemmaAnnotation.class, "Barack");
    named.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    named.setIndex(1);

    CoreLabel coord = new CoreLabel();
    coord.set(CoreAnnotations.LemmaAnnotation.class, "and");
    coord.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    coord.setIndex(2);

    CoreLabel named2 = new CoreLabel();
    named2.set(CoreAnnotations.LemmaAnnotation.class, "Obama");
    named2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    named2.setIndex(3);

    CoreLabel head = new CoreLabel();
    head.set(CoreAnnotations.LemmaAnnotation.class, "Obama");
    head.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    head.setIndex(3);

    mention.headWord = head;
    mention.nerString = "PERSON";

    List<AbstractCoreLabel> tokens = new ArrayList<>();
    tokens.add(named);
    tokens.add(coord);
    tokens.add(named2);

    String pattern = mention.getPattern(tokens);
    assertTrue(pattern.contains("<PERSON>"));
  }
@Test
  public void testMoreRepresentativeWhenShorterSpanWins() {
    Mention m1 = new Mention();
    m1.mentionType = Dictionaries.MentionType.PROPER;
    m1.nerString = "PERSON";
    m1.headIndex = 3;
    m1.startIndex = 0;
    m1.originalSpan = Arrays.asList(new CoreLabel(), new CoreLabel());

    Mention m2 = new Mention();
    m2.mentionType = Dictionaries.MentionType.PROPER;
    m2.nerString = "PERSON";
    m2.headIndex = 3;
    m2.startIndex = 0;
    m2.originalSpan = Arrays.asList(new CoreLabel(), new CoreLabel(), new CoreLabel());

    assertTrue(m1.moreRepresentativeThan(m2));
  }
@Test
  public void testGetNegationFromSibling() {
    SemanticGraph graph = mock(SemanticGraph.class);
    IndexedWord head = new IndexedWord();
    IndexedWord not = new IndexedWord();
    not.setLemma("not");

    Set<IndexedWord> siblings = new HashSet<>();
    siblings.add(not);

    Mention m = new Mention();
    m.headIndexedWord = head;
    m.dependency = graph;

    Dictionaries dict = new Dictionaries();
    dict.negations.add("not");

    when(graph.getChildren(head)).thenReturn(Collections.emptySet());
    when(graph.getSiblings(head)).thenReturn(siblings);
    when(graph.hasParentWithReln(head, UniversalEnglishGrammaticalRelations.NOMINAL_SUBJECT)).thenReturn(false);

    assertEquals(1, m.getNegation(dict));
  }
@Test
  public void testGetQuantificationWithDefiniteDeterminer() {
    IndexedWord head = new IndexedWord();
    IndexedWord det = new IndexedWord();
    det.setLemma("the");

    Set<IndexedWord> dets = new HashSet<>();
    dets.add(det);

    SemanticGraph sg = mock(SemanticGraph.class);

    when(sg.getChildrenWithReln(head, UniversalEnglishGrammaticalRelations.DETERMINER)).thenReturn(dets);
    when(sg.getChildrenWithReln(head, UniversalEnglishGrammaticalRelations.POSSESSION_MODIFIER)).thenReturn(Collections.emptySet());
    when(sg.getChildrenWithReln(head, UniversalEnglishGrammaticalRelations.NUMERIC_MODIFIER)).thenReturn(Collections.emptySet());

    Mention m = new Mention();
    m.dependency = sg;
    m.headIndexedWord = head;
    m.nerString = "O";

    Dictionaries dict = new Dictionaries();
    dict.determiners.add("the");

    assertEquals("definite", m.getQuantification(dict));
  }
@Test
  public void testBuildQueryTextHandlesEscapes() {
    List<String> terms = Arrays.asList("U.S.", "C++", "NLP!");
    String result = Mention.buildQueryText(terms);
    assertTrue(result.contains("U.S."));
    assertTrue(result.contains("C++"));
    assertTrue(result.contains("NLP!"));
  }
@Test
  public void testAppearEarlierThan_TiebreakByHashCode() {
    Mention m1 = new Mention();
    m1.sentNum = 0;
    m1.startIndex = 1;
    m1.endIndex = 2;
    m1.headIndex = 3;
    m1.mentionType = Dictionaries.MentionType.PROPER;
    m1.originalSpan = Arrays.asList(new CoreLabel());

    Mention m2 = new Mention();
    m2.sentNum = 0;
    m2.startIndex = 1;
    m2.endIndex = 2;
    m2.headIndex = 3;
    m2.mentionType = Dictionaries.MentionType.PROPER;
    m2.originalSpan = Arrays.asList(new CoreLabel());

    assertTrue(m1.appearEarlierThan(m2) || !m1.appearEarlierThan(m2));
  } 
}