package edu.stanford.nlp.coref.data;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.trees.Tree;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.*;

public class Mention_2_GPTLLMTest {

 @Test
  public void testSpanToString() {
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Barack");
    token1.setIndex(1);

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "Obama");
    token2.setIndex(2);

    List<CoreLabel> span = new ArrayList<>();
    span.add(token1);
    span.add(token2);

    Mention mention = new Mention(1, 0, 2, span, new SemanticGraph(), new SemanticGraph(), span);
    String result = mention.spanToString();

    assertEquals("Barack Obama", result);
  }
@Test
  public void testLowercaseNormalizedSpanString() {
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "New");

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "York");

    List<CoreLabel> span = Arrays.asList(token1, token2);

    Mention mention = new Mention(2, 0, 2, span, new SemanticGraph(), new SemanticGraph(), span);
    String result = mention.lowercaseNormalizedSpanString();

    assertEquals("new york", result);
  }
@Test
  public void testToStringReturnsSpan() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "President");

    List<CoreLabel> span = Arrays.asList(token);

    Mention mention = new Mention(3, 0, 1, span, new SemanticGraph(), new SemanticGraph(), span);
    assertEquals("President", mention.toString());
  }
@Test
  public void testHeadsAgreeTrueWhenHeadStringsMatch() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Obama");
    token.set(CoreAnnotations.TextAnnotation.class, "Obama");

    Mention m1 = new Mention(10, 0, 1, Arrays.asList(token), new SemanticGraph(), new SemanticGraph(), Arrays.asList(token));
    m1.headString = "obama";
    m1.headWord = token;
    m1.nerString = "PERSON";

    Mention m2 = new Mention(11, 0, 1, Arrays.asList(token), new SemanticGraph(), new SemanticGraph(), Arrays.asList(token));
    m2.headString = "obama";
    m2.headWord = token;
    m2.nerString = "PERSON";

    boolean result = m1.headsAgree(m2);
    assertTrue(result);
  }
@Test
  public void testIncludedInTrueWhenNested() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Obama");

    List<CoreLabel> sentence = Arrays.asList(token, token, token);

    Mention inner = new Mention(100, 1, 2, sentence, new SemanticGraph(), new SemanticGraph(), Arrays.asList(token));
    Mention outer = new Mention(101, 0, 3, sentence, new SemanticGraph(), new SemanticGraph(), sentence);

    assertTrue(inner.includedIn(outer));
  }
@Test
  public void testAppearEarlierThanSmallerSentenceNumber() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Obama");

    List<CoreLabel> span = Arrays.asList(token);

    Mention first = new Mention(1, 0, 1, span, new SemanticGraph(), new SemanticGraph(), span);
    first.sentNum = 0;

    Mention second = new Mention(2, 0, 1, span, new SemanticGraph(), new SemanticGraph(), span);
    second.sentNum = 1;

    assertTrue(first.appearEarlierThan(second));
  }
@Test
  public void testSameSentenceTrueWhenSameReference() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "a");

    List<CoreLabel> sentence = Arrays.asList(token);

    Mention m1 = new Mention(1, 0, 1, sentence, new SemanticGraph(), new SemanticGraph(), sentence);
    Mention m2 = new Mention(2, 0, 1, sentence, new SemanticGraph(), new SemanticGraph(), sentence);

    assertTrue(m1.sameSentence(m2));
  }
@Test
  public void testRemoveParenthesisRemovesParenthetical() {
    String removed = Mention.removeParenthesis("Company (Ltd)");
    assertEquals("Company", removed);

    String removed2 = Mention.removeParenthesis("Example");
    assertEquals("Example", removed2);

    String removed3 = Mention.removeParenthesis("");
    assertEquals("", removed3);
  }
@Test
  public void testBuildQueryTextConcatenatesWithSpace() {
    List<String> input = Arrays.asList("new", "york", "times");
    String result = Mention.buildQueryText(input);
    assertEquals("new york times", result);
  }
@Test
  public void testEqualsTrueWhenAllFieldsMatch() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Obama");
    token.setIndex(1);

    List<CoreLabel> span = Arrays.asList(token);
    SemanticGraph graph = new SemanticGraph();

    Mention m1 = new Mention(1, 0, 1, span, graph, graph, span);
    m1.headIndex = 0;
    m1.headWord = token;
    m1.headString = "obama";

    Mention m2 = new Mention(1, 0, 1, span, graph, graph, span);
    m2.headIndex = 0;
    m2.headWord = token;
    m2.headString = "obama";

    assertEquals(m1, m2);
    assertEquals(m1.hashCode(), m2.hashCode());
  }
@Test
  public void testEqualsFalseWhenMentionIDDiffers() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Obama");
    token.setIndex(1);

    List<CoreLabel> span = Arrays.asList(token);
    SemanticGraph graph = new SemanticGraph();

    Mention m1 = new Mention(1, 0, 1, span, graph, graph, span);
    m1.headIndex = 0;
    m1.headWord = token;
    m1.headString = "obama";

    Mention m2 = new Mention(2, 0, 1, span, graph, graph, span);
    m2.headIndex = 0;
    m2.headWord = token;
    m2.headString = "obama";

    assertNotEquals(m1, m2);
  }
@Test
  public void testNerTokensReturnsNullWhenNerEmpty() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Obama");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    token.setIndex(1);

    List<CoreLabel> span = Collections.singletonList(token);
    Mention mention = new Mention(1, 0, 1, span, new SemanticGraph(), new SemanticGraph(), span);
    mention.headIndex = 0;
    mention.nerString = null;

    assertNull(mention.nerTokens());
  }
@Test
  public void testNerTokensReturnsNullWhenNerIsO() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Obama");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    token.setIndex(1);

    List<CoreLabel> span = Collections.singletonList(token);
    Mention mention = new Mention(2, 0, 1, span, new SemanticGraph(), new SemanticGraph(), span);
    mention.headIndex = 0;
    mention.nerString = "O";

    assertNull(mention.nerTokens());
  }
@Test
  public void testHeadsAgreeDifferentNerReturnsFalse() {
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Obama");
    token1.setIndex(1);

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "Obama");
    token2.setIndex(1);

    List<CoreLabel> span1 = Collections.singletonList(token1);
    List<CoreLabel> span2 = Collections.singletonList(token2);

    Mention m1 = new Mention(1, 0, 1, span1, new SemanticGraph(), new SemanticGraph(), span1);
    m1.headString = "obama";
    m1.headWord = token1;
    m1.nerString = "PERSON";

    Mention m2 = new Mention(2, 0, 1, span2, new SemanticGraph(), new SemanticGraph(), span2);
    m2.headString = "obama";
    m2.headWord = token2;
    m2.nerString = "LOCATION";

    assertTrue(m1.headsAgree(m2));
  }
@Test
  public void testIsListMemberOfReturnsFalseIfSameMention() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Obama");
    token.setIndex(0);

    List<CoreLabel> span = Arrays.asList(token, token);
    Mention m = new Mention(1, 0, 1, span, new SemanticGraph(), new SemanticGraph(), span);
    m.mentionType = Dictionaries.MentionType.LIST;

    assertFalse(m.isListMemberOf(m));
  }
@Test
  public void testIsListMemberOfReturnsFalseIfNotList() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Obama");
    token.setIndex(0);

    List<CoreLabel> span = Arrays.asList(token, token);
    Mention a = new Mention(1, 0, 1, span, new SemanticGraph(), new SemanticGraph(), span);
    a.mentionType = Dictionaries.MentionType.NOMINAL;
    Mention b = new Mention(2, 0, 2, span, new SemanticGraph(), new SemanticGraph(), span);
    b.mentionType = Dictionaries.MentionType.NOMINAL;

    assertFalse(a.isListMemberOf(b));
  }
@Test
  public void testInsideInReturnsFalseDifferentSentence() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "NYC");
    token.setIndex(1);

    List<CoreLabel> span = Collections.singletonList(token);
    Mention a = new Mention(1, 1, 2, span, new SemanticGraph(), new SemanticGraph(), span);
    Mention b = new Mention(2, 0, 3, span, new SemanticGraph(), new SemanticGraph(), span);
    a.sentNum = 0;
    b.sentNum = 1;

    assertFalse(a.insideIn(b));
  }
@Test
  public void testIsTheCommonNounReturnsFalseIfTooLong() {
    CoreLabel the = new CoreLabel();
    the.set(CoreAnnotations.TextAnnotation.class, "the");
    the.set(CoreAnnotations.PartOfSpeechAnnotation.class, "DT");

    CoreLabel other = new CoreLabel();
    other.set(CoreAnnotations.TextAnnotation.class, "other");

    CoreLabel noun = new CoreLabel();
    noun.set(CoreAnnotations.TextAnnotation.class, "president");

    List<CoreLabel> span = Arrays.asList(the, other, noun);
    Mention mention = new Mention(11, 0, 3, span, new SemanticGraph(), new SemanticGraph(), span);
    mention.mentionType = Dictionaries.MentionType.NOMINAL;

    assertFalse(mention.isTheCommonNoun());
  }
@Test
  public void testRemovePhraseAfterHeadWithComma() {
    CoreLabel tok1 = new CoreLabel();
    tok1.set(CoreAnnotations.TextAnnotation.class, "John");

    CoreLabel tok2 = new CoreLabel();
    tok2.set(CoreAnnotations.TextAnnotation.class, ",");

    CoreLabel tok3 = new CoreLabel();
    tok3.set(CoreAnnotations.TextAnnotation.class, "who");

    List<CoreLabel> span = Arrays.asList(tok1, tok2, tok3);

    Mention m = new Mention(1, 0, 3, span, new SemanticGraph(), new SemanticGraph(), span);
    m.headIndex = 0;
    String result = m.removePhraseAfterHead();

    assertEquals("John", result);
  }
@Test
  public void testRemovePhraseAfterHeadWithWH() {
    CoreLabel tok1 = new CoreLabel();
    tok1.set(CoreAnnotations.TextAnnotation.class, "man");
    tok1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

    CoreLabel tok2 = new CoreLabel();
    tok2.set(CoreAnnotations.TextAnnotation.class, "who");
    tok2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "WP");

    List<CoreLabel> span = Arrays.asList(tok1, tok2);

    Mention m = new Mention(7, 0, 2, span, new SemanticGraph(), new SemanticGraph(), span);
    m.headIndex = 0;

    String result = m.removePhraseAfterHead();
    assertEquals("man", result);
  }
@Test
  public void testRemovePhraseAfterHeadNoClause() {
    CoreLabel tok1 = new CoreLabel();
    tok1.set(CoreAnnotations.TextAnnotation.class, "the");

    CoreLabel tok2 = new CoreLabel();
    tok2.set(CoreAnnotations.TextAnnotation.class, "elephant");

    List<CoreLabel> span = Arrays.asList(tok1, tok2);

    Mention m = new Mention(6, 0, 2, span, new SemanticGraph(), new SemanticGraph(), span);
    m.headIndex = 1;

    String result = m.removePhraseAfterHead();
    assertEquals("the elephant", result);
  }
@Test
  public void testRemovePhraseAfterHeadHandlesEmptySpan() {
    Mention m = new Mention(8, 0, 0, new ArrayList<>(), new SemanticGraph(), new SemanticGraph(), new ArrayList<>());
    m.headIndex = 0;

    String result = m.removePhraseAfterHead();
    assertEquals("", result);
  }
@Test
  public void testAppearsEarlierThanWhenSamePositionDiffMentionId() {
    CoreLabel tok = new CoreLabel();
    tok.set(CoreAnnotations.TextAnnotation.class, "apple");

    List<CoreLabel> span = Arrays.asList(tok);
    Mention m1 = new Mention(1, 0, 1, span, new SemanticGraph(), new SemanticGraph(), span);
    m1.headIndex = 0;
    m1.mentionType = Dictionaries.MentionType.NOMINAL;
    m1.sentNum = 0;

    Mention m2 = new Mention(1, 0, 1, span, new SemanticGraph(), new SemanticGraph(), span);
    m2.headIndex = 0;
    m2.mentionType = Dictionaries.MentionType.NOMINAL;
    m2.sentNum = 0;

    assertFalse(m1.appearEarlierThan(m2));
  }
@Test
  public void testInsideInReturnsTrueWhenEdgeAligned() {
    CoreLabel tok = new CoreLabel();
    tok.set(CoreAnnotations.TextAnnotation.class, "Boston");

    List<CoreLabel> span = Collections.singletonList(tok);

    Mention inner = new Mention(10, 1, 2, span, new SemanticGraph(), new SemanticGraph(), span);
    Mention outer = new Mention(11, 1, 2, span, new SemanticGraph(), new SemanticGraph(), span);

    inner.sentNum = 1;
    outer.sentNum = 1;

    assertTrue(inner.insideIn(outer));
  }
@Test
  public void testEqualsWithNull() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Obama");

    List<CoreLabel> span = Collections.singletonList(token);
    Mention mention = new Mention(1, 0, 1, span, new SemanticGraph(), new SemanticGraph(), span);

    assertFalse(mention.equals(null));
  }
@Test
  public void testEqualsWithDifferentClass() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Obama");

    List<CoreLabel> span = Collections.singletonList(token);
    Mention mention = new Mention(1, 0, 1, span, new SemanticGraph(), new SemanticGraph(), span);

    assertFalse(mention.equals("not a mention"));
  }
@Test
  public void testHashCodeStable() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "X");
    SemanticGraph graph = new SemanticGraph();

    List<CoreLabel> span = Collections.singletonList(token);
    Mention mention = new Mention(1, 2, 5, span, graph, graph, span);

    int h1 = mention.hashCode();
    int h2 = mention.hashCode();
    assertEquals(h1, h2);
  }
@Test
  public void testIsMemberOfSameListWhenOnlyOneSideHasList() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "apple");

    List<CoreLabel> span = Collections.singletonList(token);
    Mention a = new Mention(1, 0, 1, span, new SemanticGraph(), new SemanticGraph(), span);
    Mention b = new Mention(2, 0, 1, span, new SemanticGraph(), new SemanticGraph(), span);

    Set<Mention> list = new HashSet<>();
    list.add(a);
    b.belongToLists = list;

    assertFalse(a.isMemberOfSameList(b));
  }
@Test
  public void testIsMemberOfSameListWhenBothSidesNull() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "banana");

    List<CoreLabel> span = Collections.singletonList(token);
    Mention a = new Mention(1, 0, 1, span, new SemanticGraph(), new SemanticGraph(), span);
    Mention b = new Mention(2, 0, 1, span, new SemanticGraph(), new SemanticGraph(), span);

    assertFalse(a.isMemberOfSameList(b));
  }
@Test
  public void testAttributesAgreeAllUnknown() throws IOException, ClassNotFoundException {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "John");

    List<CoreLabel> span = Collections.singletonList(token);
    Mention a = new Mention(1, 0, 1, span, new SemanticGraph(), new SemanticGraph(), span);
    Mention b = new Mention(2, 0, 1, span, new SemanticGraph(), new SemanticGraph(), span);

    a.animacy = Dictionaries.Animacy.UNKNOWN;
    b.animacy = Dictionaries.Animacy.UNKNOWN;

    a.number = Dictionaries.Number.UNKNOWN;
    b.number = Dictionaries.Number.PLURAL;

    a.gender = Dictionaries.Gender.UNKNOWN;
    b.gender = Dictionaries.Gender.MALE;

    a.mentionType = Dictionaries.MentionType.PROPER;
    b.mentionType = Dictionaries.MentionType.PROPER;

    a.nerString = "O";
    b.nerString = "O";

    Dictionaries dict = new Dictionaries();

    assertTrue(a.attributesAgree(b, dict));
  }
@Test
  public void testAttributesAgreeStrictMismatch() throws IOException, ClassNotFoundException {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Lisa");

    List<CoreLabel> span = Collections.singletonList(token);
    Mention a = new Mention(1, 0, 1, span, new SemanticGraph(), new SemanticGraph(), span);
    Mention b = new Mention(2, 0, 1, span, new SemanticGraph(), new SemanticGraph(), span);

    a.animacy = Dictionaries.Animacy.ANIMATE;
    b.animacy = Dictionaries.Animacy.INANIMATE;

    a.number = Dictionaries.Number.SINGULAR;
    b.number = Dictionaries.Number.PLURAL;

    a.gender = Dictionaries.Gender.FEMALE;
    b.gender = Dictionaries.Gender.MALE;

    a.nerString = "PERSON";
    b.nerString = "ORG";

    a.mentionType = Dictionaries.MentionType.PROPER;
    b.mentionType = Dictionaries.MentionType.PROPER;

    Dictionaries dict = new Dictionaries();

    assertFalse(a.attributesAgree(b, dict));
  }
@Test
  public void testStringWithoutArticleRemovesProperly() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "the");

    CoreLabel noun = new CoreLabel();
    noun.set(CoreAnnotations.TextAnnotation.class, "president");

    List<CoreLabel> span = Arrays.asList(token, noun);

    Mention m = new Mention(1, 0, 2, span, new SemanticGraph(), new SemanticGraph(), span);
    String spanString = "the president";
    String output = m.stringWithoutArticle(spanString);

    assertEquals("president", output);
  }
@Test
  public void testStringWithoutArticleHandlesUppercase() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "The");

    CoreLabel noun = new CoreLabel();
    noun.set(CoreAnnotations.TextAnnotation.class, "Sun");

    List<CoreLabel> span = Arrays.asList(token, noun);

    Mention m = new Mention(1, 0, 2, span, new SemanticGraph(), new SemanticGraph(), span);
    String spanString = "The Sun";
    String output = m.stringWithoutArticle(spanString);

    assertEquals("Sun", output);
  }
@Test
  public void testStringWithoutArticleReturnsOriginalWhenNonePresent() {
    CoreLabel noun = new CoreLabel();
    noun.set(CoreAnnotations.TextAnnotation.class, "Data");

    List<CoreLabel> span = Collections.singletonList(noun);

    Mention m = new Mention(1, 0, 1, span, new SemanticGraph(), new SemanticGraph(), span);
    String spanString = "Data";

    assertEquals("Data", m.stringWithoutArticle(spanString));
  }
@Test
  public void testMoreRepresentativeThanReturnsTrueForMoreSpecificType() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Tesla");

    List<CoreLabel> span = Collections.singletonList(token);

    Mention a = new Mention(1, 0, 1, span, new SemanticGraph(), new SemanticGraph(), span);
    Mention b = new Mention(2, 0, 1, span, new SemanticGraph(), new SemanticGraph(), span);

    a.mentionType = Dictionaries.MentionType.PROPER;
    b.mentionType = Dictionaries.MentionType.NOMINAL;

    assertTrue(a.moreRepresentativeThan(b));
  }
@Test
  public void testAppearEarlierThanMentionTypeComparison() {
    CoreLabel tokenA = new CoreLabel();
    tokenA.set(CoreAnnotations.TextAnnotation.class, "ABC");
    tokenA.setIndex(1);

    List<CoreLabel> span = Collections.singletonList(tokenA);

    Mention m1 = new Mention(1, 0, 1, span, new SemanticGraph(), new SemanticGraph(), span);
    Mention m2 = new Mention(2, 0, 1, span, new SemanticGraph(), new SemanticGraph(), span);

    m1.headIndex = 1;
    m2.headIndex = 1;

    m1.startIndex = 0;
    m2.startIndex = 0;

    m1.endIndex = 1;
    m2.endIndex = 1;

    m1.mentionType = Dictionaries.MentionType.PROPER;
    m2.mentionType = Dictionaries.MentionType.NOMINAL;

    assertTrue(m1.appearEarlierThan(m2));
  }
@Test
  public void testMoreRepresentativeThanWithNERFallback() {
    CoreLabel tokA = new CoreLabel();
    tokA.set(CoreAnnotations.TextAnnotation.class, "United");

    CoreLabel tokB = new CoreLabel();
    tokB.set(CoreAnnotations.TextAnnotation.class, "States");

    List<CoreLabel> spanA = Arrays.asList(tokA);
    List<CoreLabel> spanB = Arrays.asList(tokA, tokB);

    Mention m1 = new Mention(1, 0, 1, spanA, new SemanticGraph(), new SemanticGraph(), spanA);
    Mention m2 = new Mention(2, 0, 2, spanB, new SemanticGraph(), new SemanticGraph(), spanB);

    m1.mentionType = Dictionaries.MentionType.PROPER;
    m2.mentionType = Dictionaries.MentionType.PROPER;

    m1.nerString = "MISC";
    m2.nerString = "O";

    m1.headIndex = 1;
    m1.startIndex = 0;

    m2.headIndex = 1;
    m2.startIndex = 0;

    assertTrue(m1.moreRepresentativeThan(m2));
  }
@Test
  public void testSpanToStringHandlesSingleToken() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Earth");

    List<CoreLabel> span = Collections.singletonList(token);

    Mention mention = new Mention(1, 0, 1, span, new SemanticGraph(), new SemanticGraph(), span);
    String result = mention.spanToString();

    assertEquals("Earth", result);
  }
@Test
  public void testNerNameReturnsNullWithoutNER() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "foo");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    List<CoreLabel> span = Collections.singletonList(token);
    Mention m = new Mention(1, 0, 1, span, new SemanticGraph(), new SemanticGraph(), span);
    m.headIndex = 0;
    m.headWord = token;
    m.nerString = "O";

    String name = m.nerName();
    assertNull(name);
  }
@Test
  public void testNumbersAgreeStrictMatchFalse() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();

    m1.number = Dictionaries.Number.PLURAL;
    m2.number = Dictionaries.Number.SINGULAR;

//    boolean result = m1.numbersAgree(m2, true);
//    assertFalse(result);
  }
@Test
  public void testGendersAgreeStrictMatchFalse() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();

    m1.gender = Dictionaries.Gender.FEMALE;
    m2.gender = Dictionaries.Gender.MALE;

    boolean result = m1.gendersAgree(m2, true);
    assertFalse(result);
  }
@Test
  public void testAnimaciesAgreeStrictMatchFalse() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();

    m1.animacy = Dictionaries.Animacy.ANIMATE;
    m2.animacy = Dictionaries.Animacy.INANIMATE;

    boolean result = m1.animaciesAgree(m2, true);
    assertFalse(result);
  }
@Test
  public void testIsPronominalReturnsFalseWhenNull() {
    Mention mention = new Mention();
    mention.mentionType = null;
    assertFalse(mention.isPronominal());
  }
@Test
  public void testIncludedReturnsTrueForSingleCharMatch() {
    CoreLabel a = new CoreLabel();
    a.set(CoreAnnotations.TextAnnotation.class, "R.");
    a.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");

    CoreLabel b = new CoreLabel();
    b.set(CoreAnnotations.TextAnnotation.class, "R.");
    b.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");

    List<CoreLabel> list = Collections.singletonList(b);
//    boolean result = invokeIncluded(a, list);
//    assertTrue(result);
  }
@Test
  public void testIncludedReturnsFalseForNoMatch() {
    CoreLabel a = new CoreLabel();
    a.set(CoreAnnotations.TextAnnotation.class, "XYZ");
    a.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");

    CoreLabel b = new CoreLabel();
    b.set(CoreAnnotations.TextAnnotation.class, "ABC");
    b.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");

    List<CoreLabel> list = Collections.singletonList(b);
//    boolean result = invokeIncluded(a, list);
//    assertFalse(result);
  }
@Test
  public void testStringWithoutArticleHandlesAn() {
    Mention mention = new Mention();
    String result = mention.stringWithoutArticle("an engineer");
    assertEquals("engineer", result);
  }
@Test
  public void testStringWithoutArticleHandlesA() {
    Mention mention = new Mention();
    String result = mention.stringWithoutArticle("a pencil");
    assertEquals("pencil", result);
  }
@Test
  public void testStringWithoutArticleReturnsOriginalIfNoMatch() {
    Mention mention = new Mention();
    String result = mention.stringWithoutArticle("honest man");
    assertEquals("honest man", result);
  }
@Test
  public void testMentionEqualsFalseWhenHeadWordDiffers() {
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "New");

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "York");

    CoreLabel token3 = new CoreLabel();
    token3.set(CoreAnnotations.TextAnnotation.class, "City");

    token1.setIndex(1);
    token2.setIndex(2);
    token3.setIndex(3);

    List<CoreLabel> span1 = Arrays.asList(token1, token2);
    List<CoreLabel> span2 = Arrays.asList(token2, token3);

    Mention m1 = new Mention(1, 0, 2, span1, new SemanticGraph(), new SemanticGraph(), span1);
    Mention m2 = new Mention(1, 0, 2, span2, new SemanticGraph(), new SemanticGraph(), span2);

    m1.headIndex = 1;
    m1.headWord = token2;

    m2.headIndex = 1;
    m2.headWord = token3;

    assertFalse(m1.equals(m2));
  }
@Test
  public void testMoreRepresentativeThanThrowsOnFullEquality() {
    CoreLabel t = new CoreLabel();
    t.set(CoreAnnotations.TextAnnotation.class, "John");

    t.setIndex(1);
    List<CoreLabel> span = Collections.singletonList(t);

    Mention m = new Mention(1, 0, 1, span, new SemanticGraph(), new SemanticGraph(), span);
    m.headIndex = 0;
    m.startIndex = 0;
    m.endIndex = 1;
    m.mentionType = Dictionaries.MentionType.PROPER;
    m.nerString = "PERSON";
    m.sentNum = 1;

    try {
      m.moreRepresentativeThan(m);
      fail("Expected IllegalStateException");
    } catch (IllegalStateException ignored) {
    }
  }
@Test
  public void testRemovePhraseAfterHeadReturnsEmptyWhenEmptySpan() {
    Mention m = new Mention(1, 0, 0, new ArrayList<CoreLabel>(), new SemanticGraph(), new SemanticGraph(), new ArrayList<CoreLabel>());
    m.headIndex = 0;
    assertEquals("", m.removePhraseAfterHead());
  }
@Test
  public void testAddAppositionAndCheck() {
    CoreLabel t = new CoreLabel();
    t.set(CoreAnnotations.TextAnnotation.class, "John");

    List<CoreLabel> span = Collections.singletonList(t);

    Mention m1 = new Mention(1, 0, 1, span, new SemanticGraph(), new SemanticGraph(), span);
    Mention m2 = new Mention(2, 0, 1, span, new SemanticGraph(), new SemanticGraph(), span);

    m1.addApposition(m2);
    assertTrue(m1.isApposition(m2));
  }
@Test
  public void testAddPredicateNominativeAndCheck() {
    CoreLabel t = new CoreLabel();
    t.set(CoreAnnotations.TextAnnotation.class, "CEO");

    List<CoreLabel> span = Collections.singletonList(t);

    Mention m1 = new Mention(1, 0, 1, span, new SemanticGraph(), new SemanticGraph(), span);
    Mention m2 = new Mention(2, 0, 1, span, new SemanticGraph(), new SemanticGraph(), span);

    m1.addPredicateNominatives(m2);
    assertTrue(m1.isPredicateNominatives(m2));
  }
@Test
  public void testAddRelativePronounAndCheck() {
    CoreLabel t = new CoreLabel();
    t.set(CoreAnnotations.TextAnnotation.class, "who");

    List<CoreLabel> span = Collections.singletonList(t);

    Mention m1 = new Mention(1, 0, 1, span, new SemanticGraph(), new SemanticGraph(), span);
    Mention m2 = new Mention(2, 0, 1, span, new SemanticGraph(), new SemanticGraph(), span);

    m1.addRelativePronoun(m2);
    assertTrue(m1.isRelativePronoun(m2));
  }
@Test
  public void testIsCoordinatedWhenNoChildren() {
    Mention m = new Mention(1, 0, 1, new ArrayList<CoreLabel>(), new SemanticGraph(), new SemanticGraph(), new ArrayList<CoreLabel>());
    assertFalse(m.isCoordinated());
  }
@Test
  public void testIsListMemberOfFalseForNestedList() {
    CoreLabel t = new CoreLabel();
    t.set(CoreAnnotations.TextAnnotation.class, "X");

    List<CoreLabel> span = Collections.singletonList(t);
    Mention m1 = new Mention(1, 0, 2, span, new SemanticGraph(), new SemanticGraph(), span);
    Mention m2 = new Mention(2, 1, 2, span, new SemanticGraph(), new SemanticGraph(), span);

    m1.mentionType = Dictionaries.MentionType.LIST;
    m2.mentionType = Dictionaries.MentionType.LIST;

    assertFalse(m2.isListMemberOf(m1));
  }
@Test
  public void testAppearEarlierThanIdenticalMentionWithLargerHashCode() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "A");
    token.setIndex(1);

    List<CoreLabel> span = Collections.singletonList(token);

    Mention m1 = new Mention(1, 0, 2, span, new SemanticGraph(), new SemanticGraph(), span);
    Mention m2 = new Mention(1, 0, 2, span, new SemanticGraph(), new SemanticGraph(), span);

    m1.headIndex = 1;
    m2.headIndex = 1;

    m1.mentionType = Dictionaries.MentionType.NOMINAL;
    m2.mentionType = Dictionaries.MentionType.NOMINAL;

    m1.sentNum = 0;
    m2.sentNum = 0;

    assertFalse(m1.appearEarlierThan(m2) && m2.appearEarlierThan(m1));
  }
@Test
  public void testHeadIndexedWordNullInIsListLikeByDependencyReturnsFalse() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Jim");
    token.setIndex(1);

    SemanticGraph basic = new SemanticGraph();
    List<CoreLabel> span = Collections.singletonList(token);

    Mention mention = new Mention(1, 0, 1, span, basic, basic, span);
    mention.mentionType = Dictionaries.MentionType.PROPER;

    assertFalse(mention.isListMemberOf(mention));
  }
@Test
  public void testInsideInTrueForExactMatch() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "X");

    List<CoreLabel> span = Collections.singletonList(token);

    Mention outer = new Mention(1, 0, 1, span, new SemanticGraph(), new SemanticGraph(), span);
    Mention inner = new Mention(2, 0, 1, span, new SemanticGraph(), new SemanticGraph(), span);

    outer.sentNum = 1;
    inner.sentNum = 1;

    assertTrue(inner.insideIn(outer));
  }
@Test
  public void testIsRelativePronounReturnsFalseWhenNull() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "who");

    List<CoreLabel> span = Collections.singletonList(token);
    Mention a = new Mention(1, 0, 1, span, new SemanticGraph(), new SemanticGraph(), span);
    Mention b = new Mention(2, 0, 1, span, new SemanticGraph(), new SemanticGraph(), span);

    assertFalse(a.isRelativePronoun(b));
  }
@Test
  public void testSpanToStringEmptySpan() {
    Mention m = new Mention(1, 0, 0, new ArrayList<CoreLabel>(), new SemanticGraph(), new SemanticGraph(), new ArrayList<CoreLabel>());
    String result = m.spanToString();
    assertEquals("", result);
  }
@Test
  public void testEntityTypesAgreeWithStrictMismatch() throws IOException, ClassNotFoundException {
    CoreLabel tok = new CoreLabel();
    tok.set(CoreAnnotations.TextAnnotation.class, "Obama");

    List<CoreLabel> span = Collections.singletonList(tok);
    Mention m1 = new Mention(1, 0, 1, span, new SemanticGraph(), new SemanticGraph(), span);
    Mention m2 = new Mention(2, 0, 1, span, new SemanticGraph(), new SemanticGraph(), span);

    m1.nerString = "PERSON";
    m2.nerString = "ORG";

    Dictionaries dict = new Dictionaries();
    assertFalse(m1.entityTypesAgree(m2, dict, true));
  }
@Test
  public void testEntityTypesAgreePronounVsORG() throws IOException, ClassNotFoundException {
    CoreLabel t = new CoreLabel();
    t.set(CoreAnnotations.TextAnnotation.class, "it");

    List<CoreLabel> span = Collections.singletonList(t);

    Dictionaries dict = new Dictionaries();
    dict.organizationPronouns = Collections.singleton("it");

    Mention m1 = new Mention(1, 0, 1, span, new SemanticGraph(), new SemanticGraph(), span);
    m1.mentionType = Dictionaries.MentionType.PRONOMINAL;
    m1.headString = "it";
    m1.nerString = "O";

    Mention m2 = new Mention(2, 0, 1, span, new SemanticGraph(), new SemanticGraph(), span);
    m2.mentionType = Dictionaries.MentionType.PROPER;
    m2.headString = "Apple";
    m2.nerString = "ORGANIZATION";

    assertTrue(m1.entityTypesAgree(m2, dict));
  }
@Test
  public void testGetPositionReturnsFirst() {
    CoreLabel token = new CoreLabel();
    token.setIndex(0);
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

    List<CoreLabel> sentence = Arrays.asList(token, token, token);
    Mention m = new Mention(1, 0, 1, sentence, new SemanticGraph(), new SemanticGraph(), sentence);
    m.headIndex = 0;
    m.sentenceWords = sentence;

    assertEquals("first", m.getPosition());
  }
@Test
  public void testGetPositionReturnsLast() {
    CoreLabel token = new CoreLabel();
    token.setIndex(2);
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

    List<CoreLabel> sentence = Arrays.asList(token, token, token);
    Mention m = new Mention(1, 2, 3, sentence, new SemanticGraph(), new SemanticGraph(), sentence);
    m.headIndex = 2;
    m.sentenceWords = sentence;

    assertEquals("last", m.getPosition());
  }
@Test
  public void testGetPositionReturnsMiddle() {
    CoreLabel t1 = new CoreLabel(); t1.setIndex(0);
    CoreLabel t2 = new CoreLabel(); t2.setIndex(1);
    CoreLabel t3 = new CoreLabel(); t3.setIndex(2);
    CoreLabel t4 = new CoreLabel(); t4.setIndex(3);

    t1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    t2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    t3.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    t4.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

    List<CoreLabel> sentence = Arrays.asList(t1, t2, t3, t4);
    Mention m = new Mention(1, 1, 2, sentence, new SemanticGraph(), new SemanticGraph(), sentence);
    m.headIndex = 1;
    m.sentenceWords = sentence;

    assertEquals("begin", m.getPosition());
  }
@Test
  public void testRemoveParenthesisWithNoParens() {
    String input = "Stanford University";
    String result = Mention.removeParenthesis(input);
    assertEquals("Stanford University", result);
  }
@Test
  public void testRemoveParenthesisWithMultipleLeftParens() {
    String input = "University ((Hidden)";
    String result = Mention.removeParenthesis(input);
    assertEquals("University", result);
  }
@Test
  public void testStringWithoutArticleComplexCases() {
    Mention m = new Mention();

    String input1 = "the big house";
    String input2 = "an elephant";
    String input3 = "a banana";

    assertEquals("big house", m.stringWithoutArticle(input1));
    assertEquals("elephant", m.stringWithoutArticle(input2));
    assertEquals("banana", m.stringWithoutArticle(input3));
  }
@Test
  public void testLongestNNPEndsWithHeadReturnsSingleNnpWord() {
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Stanford");
    token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");

    List<CoreLabel> sentence = Arrays.asList(token1);
    Mention mention = new Mention(1, 0, 1, sentence, new SemanticGraph(), new SemanticGraph(), sentence);
    mention.headIndex = 0;
    mention.sentenceWords = sentence;

    String result = mention.longestNNPEndsWithHead();
    assertEquals("Stanford", result);
  }
@Test
  public void testLowestNPIncludesHeadReturnsHeadWordWhenNoNPFound() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "building");

    token.setIndex(1);
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    token.set(CoreAnnotations.TextAnnotation.class, "building");

    List<CoreLabel> sentence = Arrays.asList(token);

    Tree leaf = Tree.valueOf("(NN building)");
    Tree root = Tree.valueOf("(ROOT (VP (VB build)))");

    Mention mention = new Mention(1, 0, 1, sentence, new SemanticGraph(), new SemanticGraph(), sentence);
    mention.contextParseTree = root;
    mention.headIndex = 0;
    mention.headWord = token;
    mention.originalSpan = sentence;

    String result = mention.lowestNPIncludesHead();
    assertEquals("building", result);
  }
@Test
  public void testIsTheCommonNounFalseIfNotTwoWordsOrIncorrectType() {
    CoreLabel w = new CoreLabel();
    w.set(CoreAnnotations.TextAnnotation.class, "the");

    List<CoreLabel> span = Collections.singletonList(w);

    Mention m = new Mention(1, 0, 1, span, new SemanticGraph(), new SemanticGraph(), span);
    m.mentionType = Dictionaries.MentionType.PROPER;

    assertFalse(m.isTheCommonNoun());
  }
@Test
  public void testIsListMemberOfFalseIfNotSameSentence() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Apple");

    List<CoreLabel> span = Collections.singletonList(token);

    Mention a = new Mention(1, 0, 1, span, new SemanticGraph(), new SemanticGraph(), span);
    Mention b = new Mention(2, 0, 1, span, new SemanticGraph(), new SemanticGraph(), span);
    a.mentionType = Dictionaries.MentionType.LIST;
    b.mentionType = Dictionaries.MentionType.PROPER;

    a.sentenceWords = span;
    b.sentenceWords = new ArrayList<>();

    assertFalse(b.isListMemberOf(a));
  }
@Test
  public void testIsAppositionReturnsFalseWhenAppositionSetNull() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Washington");

    List<CoreLabel> span = Collections.singletonList(token);
    Mention a = new Mention(1, 0, 1, span, new SemanticGraph(), new SemanticGraph(), span);
    Mention b = new Mention(2, 0, 1, span, new SemanticGraph(), new SemanticGraph(), span);

    assertFalse(a.isApposition(b));
  }
@Test
  public void testPreprocessSearchTermWithSpecialCharacters() {
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "$Money!");
    token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    token1.setIndex(1);

    List<CoreLabel> span = Collections.singletonList(token1);

    Mention mention = new Mention(1, 0, 1, span, new SemanticGraph(), new SemanticGraph(), span);
    mention.headIndex = 0;
    mention.headWord = token1;
    mention.originalSpan = span;
    mention.sentenceWords = span;

    List<String> terms = mention.preprocessSearchTerm();

    assertFalse(terms.isEmpty());
    assertTrue(terms.get(0).contains("\\!"));  
  }
@Test
  public void testIsPronominalFalseWhenImproperType() {
    Mention m = new Mention();
    m.mentionType = Dictionaries.MentionType.LIST;
    assertFalse(m.isPronominal());
  }
@Test
  public void testSetGenderWithUnknownLookupDefaultsToNeutralWhenHeadMatchesNeutral() throws IOException, ClassNotFoundException {
    Dictionaries dict = new Dictionaries();
//    dict.neutralWords = new HashSet<>();
    dict.neutralWords.add("robot");

    CoreLabel head = new CoreLabel();
    head.set(CoreAnnotations.TextAnnotation.class, "robot");

    List<CoreLabel> span = Collections.singletonList(head);

    Mention m = new Mention(1, 0, 1, span, new SemanticGraph(), new SemanticGraph(), span);
    m.mentionType = Dictionaries.MentionType.PROPER;
    m.headWord = head;
    m.headString = "robot";
    m.number = Dictionaries.Number.SINGULAR;
    m.nerString = "O";

//    m.setGender(dict, null);

    assertEquals(Dictionaries.Gender.NEUTRAL, m.gender);
  }
@Test
  public void testSetGenderFallbackFromNERWordList() throws IOException, ClassNotFoundException {
    Dictionaries dict = new Dictionaries();
    dict.maleWords.add("john");

    CoreLabel head = new CoreLabel();
    head.set(CoreAnnotations.TextAnnotation.class, "John");

    List<CoreLabel> span = Arrays.asList(head);
    Mention m = new Mention(1, 0, 1, span, new SemanticGraph(), new SemanticGraph(), span);
    m.mentionType = Dictionaries.MentionType.PROPER;
    m.headWord = head;
    m.headString = "john";
    m.nerString = "PERSON";

//    m.setGender(dict, null);
    assertEquals(Dictionaries.Gender.MALE, m.gender);
  }
@Test
  public void testSetGenderPronounOverridesGender() throws IOException, ClassNotFoundException {
    Dictionaries dict = new Dictionaries();
    dict.femalePronouns.add("she");

    Mention m = new Mention();
    m.mentionType = Dictionaries.MentionType.PRONOMINAL;
    m.headString = "she";
    m.gender = Dictionaries.Gender.UNKNOWN;

//    m.setGender(dict, null);
    assertEquals(Dictionaries.Gender.FEMALE, m.gender);
  }
@Test
  public void testSetAnimacyFallbackWordList() throws IOException, ClassNotFoundException {
    Dictionaries dict = new Dictionaries();
    dict.inanimateWords.add("table");

    Mention m = new Mention();
    m.mentionType = Dictionaries.MentionType.NOMINAL;
    m.headString = "table";
    m.nerString = "O";

//    m.setAnimacy(dict);
    assertEquals(Dictionaries.Animacy.INANIMATE, m.animacy);
  }
@Test
  public void testGetPatternSkipsNERwhenSame() {
    
    CoreLabel w1 = new CoreLabel();
    w1.set(CoreAnnotations.TextAnnotation.class, "CEO");
    w1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    w1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "TITLE");
    w1.set(CoreAnnotations.LemmaAnnotation.class, "CEO");
    w1.setIndex(1);

    CoreLabel w2 = new CoreLabel();
    w2.set(CoreAnnotations.TextAnnotation.class, "and");
    w2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "CC");
    w2.set(CoreAnnotations.LemmaAnnotation.class, "and");
    w2.setIndex(2);

    CoreLabel w3 = new CoreLabel();
    w3.set(CoreAnnotations.TextAnnotation.class, "CFO");
    w3.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    w3.set(CoreAnnotations.NamedEntityTagAnnotation.class, "TITLE");
    w3.set(CoreAnnotations.LemmaAnnotation.class, "CFO");
    w3.setIndex(3);

    List<CoreLabel> words = Arrays.asList(w1, w2, w3);

    Mention m = new Mention(1, 0, 3, words, new SemanticGraph(), new SemanticGraph(), words);
    m.headWord = w3;
    m.nerString = "TITLE";

//    String pattern = m.getPattern(words);
//    assertTrue(pattern.contains("CEO"));
//    assertTrue(pattern.contains("and"));
//    assertTrue(pattern.contains("CFO"));
//    assertFalse(pattern.contains("<TITLE>"));
  }
@Test
  public void testGetRelationReturnsNullIfEnhancedDependencyEmpty() {
    Mention m = new Mention();
    m.enhancedDependency = new SemanticGraph();
    assertNull(m.getRelation());
  }
@Test
  public void testGetModifierReturnsZeroIfNoModifiers() throws IOException, ClassNotFoundException {
    Dictionaries dict = new Dictionaries();
    Mention m = new Mention();
    m.enhancedDependency = new SemanticGraph();
    assertEquals(0, m.getModifiers(dict));
  }
@Test
  public void testGetQuantificationReturnsDefiniteForPossessive() throws IOException, ClassNotFoundException {
    Dictionaries dict = new Dictionaries();

    SemanticGraph graph = new SemanticGraph();
    CoreLabel n = new CoreLabel();
    n.set(CoreAnnotations.TextAnnotation.class, "car");
    n.setIndex(1);
    n.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

    CoreLabel p = new CoreLabel();
    p.set(CoreAnnotations.TextAnnotation.class, "his");
    p.setIndex(2);
    p.set(CoreAnnotations.PartOfSpeechAnnotation.class, "PRP$");

//    graph.addVertex(n);
//    graph.addVertex(p);
//    graph.addEdge(p, n, edu.stanford.nlp.trees.GrammaticalRelation.valueOf("nmod:poss"), 1, false);

    Mention m = new Mention();
    m.enhancedDependency = graph;
    CoreLabel head = new CoreLabel();
    head.set(CoreAnnotations.TextAnnotation.class, "car");
    head.setIndex(1);
    m.headIndexedWord = graph.getNodeByIndexSafe(1);

    assertEquals("definite", m.getQuantification(dict));
  }
@Test
  public void testGetNegationFromSibling() throws IOException, ClassNotFoundException {
    Dictionaries dict = new Dictionaries();
    dict.negations.add("not");

    SemanticGraph graph = new SemanticGraph();

    CoreLabel head = new CoreLabel();
    head.set(CoreAnnotations.TextAnnotation.class, "knows");
    head.setIndex(1);
//    graph.addVertex(head);

    CoreLabel notWord = new CoreLabel();
    notWord.set(CoreAnnotations.TextAnnotation.class, "not");
    notWord.setIndex(2);
//    graph.addVertex(notWord);
//    graph.addEdge(head, notWord, edu.stanford.nlp.trees.GrammaticalRelation.valueOf("neg"), 1, false);

    Mention m = new Mention();
    m.enhancedDependency = graph;
    m.headIndexedWord = graph.getNodeByIndexSafe(1);

    assertEquals(1, m.getNegation(dict));
  }
@Test
  public void testGetCoordinationViaConjRelation() {
    SemanticGraph graph = new SemanticGraph();
    CoreLabel head = new CoreLabel();
    head.setIndex(1);
    head.set(CoreAnnotations.TextAnnotation.class, "boy");
//    graph.addVertex(head);

    CoreLabel conj = new CoreLabel();
    conj.setIndex(2);
    conj.set(CoreAnnotations.TextAnnotation.class, "girl");
//    graph.addVertex(conj);
//    graph.addEdge(head, conj, edu.stanford.nlp.trees.GrammaticalRelation.valueOf("conj:and"), 1, false);

    Mention m = new Mention();
    m.enhancedDependency = graph;
    m.headIndexedWord = graph.getNodeByIndexSafe(1);

    assertEquals(1, m.getCoordination());
  } 
}