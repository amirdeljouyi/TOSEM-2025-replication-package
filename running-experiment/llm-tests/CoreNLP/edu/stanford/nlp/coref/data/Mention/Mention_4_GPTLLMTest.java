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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class Mention_4_GPTLLMTest {

 @Test
  public void testToStringReturnsSpan() {
    CoreLabel tok = new CoreLabel();
    tok.setWord("Obama");
    tok.set(CoreAnnotations.TextAnnotation.class, "Obama");
    tok.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    List<CoreLabel> span = Arrays.asList(tok);
    SemanticGraph graph = new SemanticGraph();
    Mention mention = new Mention(1, 0, 1, span, graph, graph, span);
    mention.headWord = tok;
    String result = mention.toString();
    assertEquals("Obama", result);
  }
@Test
  public void testLowercaseNormalizedSpanString() {
    CoreLabel tok = new CoreLabel();
    tok.setWord("Obama");
    tok.set(CoreAnnotations.TextAnnotation.class, "Obama");
    tok.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    List<CoreLabel> span = Arrays.asList(tok);
    SemanticGraph graph = new SemanticGraph();
    Mention mention = new Mention(1, 0, 1, span, graph, graph, span);
    mention.headWord = tok;
    String result = mention.lowercaseNormalizedSpanString();
    assertEquals("obama", result);
  }
@Test
  public void testSpanToStringReturnsCachedValue() {
    CoreLabel tok = new CoreLabel();
    tok.setWord("Stanford");
    tok.set(CoreAnnotations.TextAnnotation.class, "Stanford");
    tok.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    List<CoreLabel> span = Arrays.asList(tok);
    SemanticGraph graph = new SemanticGraph();

    Mention mention = new Mention(1, 0, 1, span, graph, graph, span);
    mention.headWord = tok;

    String s1 = mention.spanToString();
    String s2 = mention.spanToString();

    assertSame(s1, s2);
    assertEquals("Stanford", s1);
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
    mention.mentionType = Dictionaries.MentionType.PROPER;
    assertFalse(mention.isPronominal());
  }
@Test
  public void testNerTokensReturnsCorrectSubspan() {
    CoreLabel c1 = new CoreLabel();
    c1.setWord("Barack");
    c1.setNER("PERSON");
    c1.setIndex(1);
    c1.set(CoreAnnotations.TextAnnotation.class, "Barack");

    CoreLabel c2 = new CoreLabel();
    c2.setWord("Obama");
    c2.setNER("PERSON");
    c2.setIndex(2);
    c2.set(CoreAnnotations.TextAnnotation.class, "Obama");

    CoreLabel c3 = new CoreLabel();
    c3.setWord("visited");
    c3.setNER("O");
    c3.setIndex(3);
    c3.set(CoreAnnotations.TextAnnotation.class, "visited");

    List<CoreLabel> span = Arrays.asList(c1, c2, c3);
    SemanticGraph graph = new SemanticGraph();

    Mention mention = new Mention(1, 0, 3, span, graph, graph, span);
    mention.headIndex = 1;
    mention.startIndex = 0;
    mention.nerString = "PERSON";

    List<CoreLabel> result = mention.nerTokens();
    assertEquals(2, result.size());
    assertEquals("Barack", result.get(0).word());
    assertEquals("Obama", result.get(1).word());
  }
@Test
  public void testNerTokensReturnsNullWhenNotNamedEntity() {
    CoreLabel c1 = new CoreLabel();
    c1.setWord("city");
    c1.setNER("O");
    c1.set(CoreAnnotations.TextAnnotation.class, "city");

    List<CoreLabel> span = Arrays.asList(c1);
    SemanticGraph graph = new SemanticGraph();

    Mention mention = new Mention(1, 0, 1, span, graph, graph, span);
    mention.headIndex = 0;
    mention.startIndex = 0;
    mention.nerString = "O";

    List<CoreLabel> result = mention.nerTokens();
    assertNull(result);
  }
@Test
  public void testNerNameReturnsJoinedName() {
    CoreLabel tok = new CoreLabel();
    tok.setWord("Obama");
    tok.setNER("PERSON");
    tok.set(CoreAnnotations.TextAnnotation.class, "Obama");
    tok.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");

    List<CoreLabel> span = Arrays.asList(tok);
    SemanticGraph graph = new SemanticGraph();

    Mention mention = new Mention(1, 0, 1, span, graph, graph, span);
    mention.startIndex = 0;
    mention.headIndex = 0;
    mention.nerString = "PERSON";

    String name = mention.nerName();
    assertEquals("Obama", name);
  }
@Test
  public void testHeadsAgreeExactMatch() {
    Mention m1 = new Mention();
    m1.headString = "obama";
    m1.nerString = "PERSON";

    Mention m2 = new Mention();
    m2.headString = "obama";
    m2.nerString = "PERSON";

    assertTrue(m1.headsAgree(m2));
  }
@Test
  public void testHeadsAgreeIncludedMatch() {
    CoreLabel c1 = new CoreLabel();
    c1.setWord("Bush");
    c1.setTag("NNP");

    Mention a1 = new Mention();
    a1.nerString = "PERSON";
    a1.originalSpan = Collections.singletonList(c1);
    a1.headWord = c1;

    CoreLabel c2 = new CoreLabel();
    c2.setWord("George");
    c2.setTag("NNP");

    Mention a2 = new Mention();
    a2.nerString = "PERSON";
    a2.originalSpan = Arrays.asList(c2, c1);
    a2.headWord = c1;

    assertTrue(a2.headsAgree(a1) || a1.headsAgree(a2));
  }
@Test
  public void testAttributesAgreeWhenAllMatch() throws IOException, ClassNotFoundException {
    Mention m1 = new Mention();
    m1.animacy = Dictionaries.Animacy.ANIMATE;
    m1.gender = Dictionaries.Gender.MALE;
    m1.number = Dictionaries.Number.SINGULAR;
    m1.mentionType = Dictionaries.MentionType.PROPER;
    m1.nerString = "PERSON";
    m1.headString = "obama";

    Mention m2 = new Mention();
    m2.animacy = Dictionaries.Animacy.ANIMATE;
    m2.gender = Dictionaries.Gender.MALE;
    m2.number = Dictionaries.Number.SINGULAR;
    m2.mentionType = Dictionaries.MentionType.PROPER;
    m2.nerString = "PERSON";
    m2.headString = "obama";

    Dictionaries dict = new Dictionaries();

    assertTrue(m1.attributesAgree(m2, dict));
  }
@Test
  public void testAttributesAgreeFailsOnGenderMismatch() throws IOException, ClassNotFoundException {
    Mention m1 = new Mention();
    m1.animacy = Dictionaries.Animacy.ANIMATE;
    m1.gender = Dictionaries.Gender.MALE;
    m1.number = Dictionaries.Number.SINGULAR;
    m1.mentionType = Dictionaries.MentionType.PROPER;
    m1.nerString = "PERSON";
    m1.headString = "obama";

    Mention m2 = new Mention();
    m2.animacy = Dictionaries.Animacy.ANIMATE;
    m2.gender = Dictionaries.Gender.FEMALE;
    m2.number = Dictionaries.Number.SINGULAR;
    m2.mentionType = Dictionaries.MentionType.PROPER;
    m2.nerString = "PERSON";
    m2.headString = "obama";

    Dictionaries dict = new Dictionaries();

    assertFalse(m1.attributesAgree(m2, dict));
  }
@Test
  public void testBuildQueryText() {
    List<String> words = Arrays.asList("barack", "obama");
    String result = Mention.buildQueryText(words);
    assertEquals("barack obama", result);
  }
@Test
  public void testRemoveParenthesisReturnsPrefix() {
    String result = Mention.removeParenthesis("California (USA)");
    assertEquals("California", result);
  }
@Test
  public void testRemoveParenthesisReturnsEmptyIfNoLeft() {
    String result = Mention.removeParenthesis("()");
    assertEquals("", result);
  }
@Test
  public void testGetPositionFirst() {
    CoreLabel c1 = new CoreLabel(); c1.setWord("The");
    CoreLabel c2 = new CoreLabel(); c2.setWord("dog");
    CoreLabel c3 = new CoreLabel(); c3.setWord("barks");
    List<CoreLabel> tokens = Arrays.asList(c1, c2, c3);
    SemanticGraph g = new SemanticGraph();

    Mention mention = new Mention(1, 0, 1, tokens, g, g);
    mention.headIndex = 0;
    mention.sentenceWords = tokens;

    String pos = mention.getPosition();
    assertEquals("first", pos);
  }
@Test
  public void testGetPositionLast() {
    CoreLabel c1 = new CoreLabel(); c1.setWord("The");
    CoreLabel c2 = new CoreLabel(); c2.setWord("dog");
    CoreLabel c3 = new CoreLabel(); c3.setWord("barks");
    List<CoreLabel> tokens = Arrays.asList(c1, c2, c3);
    SemanticGraph g = new SemanticGraph();

    Mention mention = new Mention(1, 0, 1, tokens, g, g);
    mention.headIndex = 2;
    mention.sentenceWords = tokens;

    String pos = mention.getPosition();
    assertEquals("last", pos);
  }
@Test
  public void testGetPositionMiddle() {
    CoreLabel c1 = new CoreLabel(); c1.setWord("The");
    CoreLabel c2 = new CoreLabel(); c2.setWord("dog");
    CoreLabel c3 = new CoreLabel(); c3.setWord("barks");
    List<CoreLabel> tokens = Arrays.asList(c1, c2, c3);
    SemanticGraph g = new SemanticGraph();

    Mention mention = new Mention(1, 0, 1, tokens, g, g);
    mention.headIndex = 1;
    mention.sentenceWords = tokens;

    String pos = mention.getPosition();
    assertEquals("middle", pos);
  }
@Test
public void testNumbersAgreeWithUnknown() {
  Mention m1 = new Mention();
  m1.number = Dictionaries.Number.UNKNOWN;
  Mention m2 = new Mention();
  m2.number = Dictionaries.Number.PLURAL;
  boolean result = m1.numbersAgree(m2);
  assertTrue(result);
}
@Test
public void testGendersAgreeWithUnknown() {
  Mention m1 = new Mention();
  m1.gender = Dictionaries.Gender.UNKNOWN;
  Mention m2 = new Mention();
  m2.gender = Dictionaries.Gender.FEMALE;
  boolean result = m1.gendersAgree(m2);
  assertTrue(result);
}
@Test
public void testAnimaciesAgreeWithUnknown() {
  Mention m1 = new Mention();
  m1.animacy = Dictionaries.Animacy.UNKNOWN;
  Mention m2 = new Mention();
  m2.animacy = Dictionaries.Animacy.INANIMATE;
  boolean result = m1.animaciesAgree(m2);
  assertTrue(result);
}
@Test
public void testSameSentenceTrue() {
  CoreLabel tok1 = new CoreLabel();
  tok1.setWord("foo");
  tok1.set(CoreAnnotations.TextAnnotation.class, "foo");
  List<CoreLabel> sentence = Collections.singletonList(tok1);
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
public void testIsListMemberOfTrue() {
  Mention list = new Mention();
  list.mentionType = Dictionaries.MentionType.LIST;
  list.startIndex = 0;
  list.endIndex = 3;
  list.sentenceWords = Collections.singletonList(new CoreLabel());

  Mention item = new Mention();
  item.mentionType = Dictionaries.MentionType.NOMINAL;
  item.startIndex = 1;
  item.endIndex = 2;
  item.sentenceWords = list.sentenceWords;

  assertTrue(item.isListMemberOf(list));
}
@Test
public void testIsListMemberOfFalse_SameMention() {
  Mention m = new Mention();
  m.mentionType = Dictionaries.MentionType.LIST;
  assertFalse(m.isListMemberOf(m));
}
@Test
public void testIsListMemberOfFalse_NonList() {
  Mention outer = new Mention();
  outer.mentionType = Dictionaries.MentionType.PROPER;
  outer.startIndex = 0;
  outer.endIndex = 2;
  outer.sentenceWords = Collections.singletonList(new CoreLabel());

  Mention inner = new Mention();
  inner.mentionType = Dictionaries.MentionType.NOMINAL;
  inner.startIndex = 0;
  inner.endIndex = 2;
  inner.sentenceWords = outer.sentenceWords;

  assertFalse(inner.isListMemberOf(outer));
}
@Test
public void testAppearEarlierThanDifferentSentences() {
  Mention m1 = new Mention();
  m1.sentNum = 0;
  Mention m2 = new Mention();
  m2.sentNum = 1;
  assertTrue(m1.appearEarlierThan(m2));
  assertFalse(m2.appearEarlierThan(m1));
}
@Test
public void testAppearEarlierThanSameSentence() {
  Mention m1 = new Mention();
  m1.sentNum = 2;
  m1.startIndex = 1;
  m1.endIndex = 3;

  Mention m2 = new Mention();
  m2.sentNum = 2;
  m2.startIndex = 2;
  m2.endIndex = 4;

  assertTrue(m1.appearEarlierThan(m2));
}
@Test
public void testEqualsWithNull() {
  Mention mention = new Mention();
  assertFalse(mention.equals(null));
}
@Test
public void testEqualsWithDifferentClass() {
  Mention mention = new Mention();
  assertFalse(mention.equals("not a Mention"));
}
@Test
public void testEqualsSameReference() {
  Mention mention = new Mention();
  assertTrue(mention.equals(mention));
}
@Test
public void testHashCodeIsConsistent() {
  Mention a = new Mention();
  a.startIndex = 2;
  a.endIndex = 4;

  Mention b = new Mention();
  b.startIndex = 2;
  b.endIndex = 4;

  assertEquals(a.hashCode(), b.hashCode());
}
@Test
public void testIsDemonymStateMatch() throws IOException, ClassNotFoundException {
//  Mention m1 = new Mention();
//  m1.spanToString = "CA";
//
//  Mention m2 = new Mention();
//  m2.spanToString = "California";

  Dictionaries dict = new Dictionaries();
  dict.statesAbbreviation.put("CA", "California");
  dict.statesAbbreviation.put("California", "California");

//  assertTrue(m1.isDemonym(m2, dict) || m2.isDemonym(m1, dict));
}
//@Test
//public void testIsDemonymPronounMatch() throws IOException, ClassNotFoundException {
//  Mention m1 = new Mention();
//  m1.spanToString = "American";
//
//  Mention m2 = new Mention();
//  m2.spanToString = "USA";

//  Dictionaries dict = new Dictionaries();
//  dict.demonyms.put("american", new HashSet<>(Collections.singletonList("usa")));
//  dict.demonyms.put("usa", new HashSet<>(Collections.singletonList("american")));
//
//  assertTrue(m1.isDemonym(m2, dict));
//}
@Test
public void testIsDemonymFalse() throws IOException, ClassNotFoundException {
  Mention m1 = new Mention();
//  m1.spanToString = "apple";

  Mention m2 = new Mention();
//  m2.spanToString = "banana";

  Dictionaries dict = new Dictionaries();

  assertFalse(m1.isDemonym(m2, dict));
}
@Test
public void testStringWithoutArticle_a() {
  Mention m = new Mention();
//  m.spanString = "a man";
  String result = m.stringWithoutArticle(null);
  assertEquals("man", result);
}
@Test
public void testStringWithoutArticle_an() {
  Mention m = new Mention();
//  m.spanString = "an elephant";
  String result = m.stringWithoutArticle(null);
  assertEquals("elephant", result);
}
@Test
public void testStringWithoutArticle_the() {
  Mention m = new Mention();
//  m.spanString = "the president";
  String result = m.stringWithoutArticle(null);
  assertEquals("president", result);
}
@Test
public void testStringWithoutArticle_noArticle() {
  Mention m = new Mention();
//  m.spanString = "president";
  String result = m.stringWithoutArticle(null);
  assertEquals("president", result);
}
@Test
public void testSetAnimacyUnknownFallback() throws Exception {
  CoreLabel tok = new CoreLabel();
  tok.setWord("object");
  tok.set(CoreAnnotations.TextAnnotation.class, "object");

  List<CoreLabel> span = Arrays.asList(tok);
  SemanticGraph graph = new SemanticGraph();

  Mention mention = new Mention(1, 0, 1, span, graph, graph, span);
  mention.mentionType = Dictionaries.MentionType.NOMINAL;
  mention.headWord = tok;
  mention.nerString = "UNKNOWN";
  mention.headString = "object";

  Dictionaries dict = new Dictionaries();
//  mention.setAnimacy(dict);
  assertEquals(Dictionaries.Animacy.UNKNOWN, mention.animacy);
}
@Test
public void testLowercaseNormalizedSpanStringCachesValue() {
  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "House");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
  List<CoreLabel> span = Arrays.asList(token);
  SemanticGraph graph = new SemanticGraph();

  Mention mention = new Mention(2, 0, 1, span, graph, graph, span);
  mention.headWord = token;
  String s1 = mention.lowercaseNormalizedSpanString();
  String s2 = mention.lowercaseNormalizedSpanString();
  assertSame(s1, s2);
  assertEquals("house", s1);
}
@Test
public void testNerTokensReturnsNullWhenNerStringIsNull() {
  Mention mention = new Mention();
  mention.nerString = null;
  List<CoreLabel> result = mention.nerTokens();
  assertNull(result);
}
@Test
public void testGetTypeReturnsMentionClass() {
  Mention mention = new Mention();
  assertEquals(Mention.class, mention.getType());
}
@Test
public void testEntityTypesAgreeStrictFalseAgreeOnO() throws IOException, ClassNotFoundException {
  Mention m1 = new Mention();
  m1.nerString = "O";
  Mention m2 = new Mention();
  m2.nerString = "PERSON";

  Dictionaries dict = new Dictionaries();
  assertTrue(m1.entityTypesAgree(m2, dict));
}
@Test
public void testEntityTypesAgreeStrictTrueMismatch() throws IOException, ClassNotFoundException {
  Mention m1 = new Mention();
  m1.nerString = "DATE";
  Mention m2 = new Mention();
  m2.nerString = "TIME";

  Dictionaries dict = new Dictionaries();
  assertFalse(m1.entityTypesAgree(m2, dict, true));
}
@Test
public void testInsideInReturnsTrueOnExactMatch() {
  Mention outer = new Mention();
  outer.sentNum = 3;
  outer.startIndex = 1;
  outer.endIndex = 4;

  Mention inner = new Mention();
  inner.sentNum = 3;
  inner.startIndex = 1;
  inner.endIndex = 4;

  assertTrue(inner.insideIn(outer));
}
@Test
public void testInsideInReturnsFalseOnOverlappingSpan() {
  Mention outer = new Mention();
  outer.sentNum = 5;
  outer.startIndex = 1;
  outer.endIndex = 2;

  Mention inner = new Mention();
  inner.sentNum = 5;
  inner.startIndex = 1;
  inner.endIndex = 4;

  assertFalse(inner.insideIn(outer));
}
@Test
public void testMoreRepresentativeThanReturnsTrueWhenNERMoreInformative() {
  CoreLabel tokA = new CoreLabel();
  tokA.set(CoreAnnotations.TextAnnotation.class, "Obama");
  tokA.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
  tokA.setTag("NNP");
  List<CoreLabel> span = Arrays.asList(tokA);
  SemanticGraph graph = new SemanticGraph();

  Mention m1 = new Mention(1, 0, 1, span, graph, graph, span);
  m1.headWord = tokA;
  m1.headIndex = 1;
  m1.startIndex = 0;
  m1.mentionType = Dictionaries.MentionType.PROPER;
  m1.nerString = "PERSON";
  m1.sentNum = 3;
  m1.originalSpan = span;

  Mention m2 = new Mention(2, 0, 1, span, graph, graph, span);
  m2.headWord = tokA;
  m2.headIndex = 1;
  m2.startIndex = 0;
  m2.mentionType = Dictionaries.MentionType.PROPER;
  m2.nerString = "O";
  m2.sentNum = 3;
  m2.originalSpan = span;

  assertTrue(m1.moreRepresentativeThan(m2));
}
@Test
public void testMoreRepresentativeThanReturnsFalseWhenSentNumIsHigher() {
  CoreLabel tok = new CoreLabel();
  tok.set(CoreAnnotations.TextAnnotation.class, "president");
  tok.setTag("NNP");
  List<CoreLabel> span = Arrays.asList(tok);
  SemanticGraph graph = new SemanticGraph();

  Mention m1 = new Mention(1, 0, 1, span, graph, graph, span);
  m1.headIndex = 1;
  m1.startIndex = 0;
  m1.mentionType = Dictionaries.MentionType.PROPER;
  m1.nerString = "PERSON";
  m1.sentNum = 3;
  m1.originalSpan = span;
  m1.headWord = tok;

  Mention m2 = new Mention(2, 0, 1, span, graph, graph, span);
  m2.headIndex = 1;
  m2.startIndex = 0;
  m2.mentionType = Dictionaries.MentionType.PROPER;
  m2.nerString = "PERSON";
  m2.sentNum = 2;
  m2.originalSpan = span;
  m2.headWord = tok;

  assertFalse(m1.moreRepresentativeThan(m2));
}
@Test
public void testIsCoordinatedReturnsTrueWhenConjunctionChildExists() {
  CoreLabel h = new CoreLabel();
  h.setIndex(1);
  h.set(CoreAnnotations.TextAnnotation.class, "dogs");
  IndexedWord head = new IndexedWord(h);

  CoreLabel cc = new CoreLabel();
  cc.set(CoreAnnotations.TextAnnotation.class, "and");
  cc.setIndex(2);
  IndexedWord conj = new IndexedWord(cc);

  SemanticGraph graph = new SemanticGraph();
  graph.addVertex(head);
  graph.addVertex(conj);
  graph.addEdge(head, conj, UniversalEnglishGrammaticalRelations.COORDINATION, 0, false);

  Mention mention = new Mention(0, 0, 2, Arrays.asList(h, cc), graph, graph, Arrays.asList(h, cc));
  mention.headIndexedWord = head;

  assertTrue(mention.isCoordinated());
}
@Test
public void testGetPatternReturnsBasicPattern() {
  CoreLabel h = new CoreLabel();
  h.setWord("dog");
  h.set(CoreAnnotations.TextAnnotation.class, "dog");
  h.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
  h.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
  h.setIndex(1);
  List<CoreLabel> span = Arrays.asList(h);
  SemanticGraph g = new SemanticGraph();

  Mention m = new Mention(1, 0, 1, span, g, g, span);
  m.headWord = h;
  m.headIndexedWord = new IndexedWord(h);

  String result = m.getPattern();
  assertTrue(result.contains("dog"));
}
@Test
public void testSetNumberSetsListPlural() throws IOException, ClassNotFoundException {
  CoreLabel h = new CoreLabel();
  h.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
  h.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
  h.set(CoreAnnotations.TextAnnotation.class, "cats");

  Mention m = new Mention();
  m.mentionType = Dictionaries.MentionType.LIST;
  m.headWord = h;
  m.nerString = "O";

  Dictionaries dict = new Dictionaries();
  m.setNumber(dict);
  assertEquals(Dictionaries.Number.PLURAL, m.number);
}
@Test
public void testSetGenderIgnoresPronounsForProperListMention() throws IOException, ClassNotFoundException {
  CoreLabel h = new CoreLabel();
  h.set(CoreAnnotations.TextAnnotation.class, "engineer");

  Mention m = new Mention();
  m.mentionType = Dictionaries.MentionType.LIST;
  m.headWord = h;
  m.headString = "engineer";
  m.nerString = "PERSON";
  m.number = Dictionaries.Number.SINGULAR;
  m.gender = Dictionaries.Gender.UNKNOWN;

  Dictionaries dict = new Dictionaries();
//  m.setGender(dict, null);
//  assertEquals(Gender.UNKNOWN, m.gender);
}
@Test
public void testIsTheCommonNounTrue() {
  Mention mention = new Mention();
  mention.mentionType = Dictionaries.MentionType.NOMINAL;
//  mention.spanString = "the cancer";
  List<CoreLabel> span = new ArrayList<>();

  CoreLabel t1 = new CoreLabel();
  t1.set(CoreAnnotations.TextAnnotation.class, "the");
  CoreLabel t2 = new CoreLabel();
  t2.set(CoreAnnotations.TextAnnotation.class, "cancer");

  span.add(t1);
  span.add(t2);

  mention.originalSpan = span;
  assertTrue(mention.isTheCommonNoun());
}
@Test
public void testIsTheCommonNounFalseWhenLongerSpan() {
  Mention mention = new Mention();
  mention.mentionType = Dictionaries.MentionType.NOMINAL;
//  mention.spanString = "the cancer cells divide";
  List<CoreLabel> span = new ArrayList<>();

  CoreLabel t1 = new CoreLabel();
  t1.set(CoreAnnotations.TextAnnotation.class, "the");
  CoreLabel t2 = new CoreLabel();
  t2.set(CoreAnnotations.TextAnnotation.class, "cancer");
  CoreLabel t3 = new CoreLabel();
  t3.set(CoreAnnotations.TextAnnotation.class, "cells");
  CoreLabel t4 = new CoreLabel();
  t4.set(CoreAnnotations.TextAnnotation.class, "divide");

  span.add(t1);
  span.add(t2);
  span.add(t3);
  span.add(t4);

  mention.originalSpan = span;

  assertFalse(mention.isTheCommonNoun());
}
@Test
public void testIsPredicateNominativesReturnsFalseWhenCollectionIsNull() {
  Mention a = new Mention();
  Mention b = new Mention();
  assertFalse(a.isPredicateNominatives(b));
}
@Test
public void testIsRelativePronounReturnsFalseWhenCollectionIsNull() {
  Mention a = new Mention();
  Mention b = new Mention();
  assertFalse(a.isRelativePronoun(b));
}
@Test
public void testIsMemberOfSameListWithOverlappingLists() {
  Mention listA = new Mention();
  Mention listB = new Mention();
  Set<Mention> list = new HashSet<>();
  list.add(listA);

  Mention m1 = new Mention();
  m1.belongToLists = list;

  Mention m2 = new Mention();
  m2.belongToLists = new HashSet<>();
  m2.belongToLists.add(listA);

  assertTrue(m1.isMemberOfSameList(m2));
}
@Test
public void testIsMemberOfSameListReturnsFalseWhenNoOverlap() {
  Mention listA = new Mention();
  Mention listB = new Mention();

  Mention m1 = new Mention();
  m1.belongToLists = new HashSet<>();
  m1.belongToLists.add(listA);

  Mention m2 = new Mention();
  m2.belongToLists = new HashSet<>();
  m2.belongToLists.add(listB);

  assertFalse(m1.isMemberOfSameList(m2));
}
@Test
public void testRemovePhraseAfterHeadWithComma() {
  CoreLabel w1 = new CoreLabel();
  w1.set(CoreAnnotations.TextAnnotation.class, "John");
  w1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");

  CoreLabel w2 = new CoreLabel();
  w2.set(CoreAnnotations.TextAnnotation.class, ",");
  w2.set(CoreAnnotations.PartOfSpeechAnnotation.class, ",");

  List<CoreLabel> span = new ArrayList<>();
  span.add(w1);
  span.add(w2);

  Mention mention = new Mention();
  mention.originalSpan = span;
  mention.headIndex = 0;
  mention.startIndex = 0;
  String result = mention.removePhraseAfterHead();
  assertEquals("John", result);
}
@Test
public void testRemovePhraseAfterHeadWithWH() {
  CoreLabel w1 = new CoreLabel();
  w1.set(CoreAnnotations.TextAnnotation.class, "students");
  w1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNS");

  CoreLabel w2 = new CoreLabel();
  w2.set(CoreAnnotations.TextAnnotation.class, "who");
  w2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "WP");

  List<CoreLabel> span = new ArrayList<>();
  span.add(w1);
  span.add(w2);

  Mention mention = new Mention();
  mention.originalSpan = span;
  mention.headIndex = 0;
  mention.startIndex = 0;

  String result = mention.removePhraseAfterHead();
  assertEquals("students", result);
}
@Test
public void testRemovePhraseAfterHeadWithNoCommaOrWH() {
  CoreLabel w1 = new CoreLabel();
  w1.set(CoreAnnotations.TextAnnotation.class, "students");
  w1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNS");

  List<CoreLabel> span = new ArrayList<>();
  span.add(w1);

  Mention mention = new Mention();
  mention.originalSpan = span;
  mention.headIndex = 0;
  mention.startIndex = 0;

//  mention.spanString = "students";
  String result = mention.removePhraseAfterHead();
  assertEquals("students", result);
}
@Test
public void testIsRoleAppositiveFailsDifferentSentence() throws IOException, ClassNotFoundException {
  Mention role = new Mention();
  Mention person = new Mention();
  role.sentenceWords = Collections.singletonList(new CoreLabel());
  person.sentenceWords = Collections.singletonList(new CoreLabel());

  Dictionaries dict = new Dictionaries();

  assertFalse(role.isRoleAppositive(person, dict));
}
@Test
public void testIsRoleAppositiveFailsOnDifferentStart() throws IOException, ClassNotFoundException {
  Mention role = new Mention();
  Mention person = new Mention();

//  role.spanString = "President Joe";
//  person.spanString = "Joe President";
  role.nerString = "PERSON";
  person.nerString = "PERSON";
  role.generic = false;
  person.generic = false;
  role.sentenceWords = Collections.singletonList(new CoreLabel());
  person.sentenceWords = role.sentenceWords;

  Dictionaries dict = new Dictionaries();

  assertFalse(role.isRoleAppositive(person, dict));
}
@Test
public void testGetPremodifiersReturnsEmptyWhenNullHead() {
  Mention m = new Mention();
  m.headIndexedWord = null;
  List<?> result = m.getPremodifiers();
  assertTrue(result.isEmpty());
}
@Test
public void testGetPostmodifiersReturnsEmptyWhenNullHead() {
  Mention m = new Mention();
  m.headIndexedWord = null;
  List<?> result = m.getPostmodifiers();
  assertTrue(result.isEmpty());
}
@Test
public void testGetQuantificationReturnsDefiniteWithDeterminer() throws IOException, ClassNotFoundException {
  CoreLabel word = new CoreLabel();
  word.setLemma("the");
  word.setIndex(1);

  IndexedWord head = new IndexedWord(word);
  SemanticGraph g = new SemanticGraph();
  g.addVertex(head);
  g.addVertex(head);
  g.addEdge(head, head, UniversalEnglishGrammaticalRelations.DETERMINER, 1.0, false);

  Mention m = new Mention();
  m.headIndexedWord = head;
  m.nerString = "O";
  m.basicDependency = g;
  m.enhancedDependency = g;

  Dictionaries dict = new Dictionaries();
  dict.determiners.add("the");

  String result = m.getQuantification(dict);
  assertEquals("definite", result);
}
@Test
public void testGetNegationWithNegSibling() throws IOException, ClassNotFoundException {
  CoreLabel tok = new CoreLabel(); tok.setLemma("not");
  IndexedWord head = new IndexedWord(new CoreLabel()); head.setIndex(1);
  IndexedWord neg = new IndexedWord(tok); neg.setIndex(2);

  SemanticGraph g = new SemanticGraph();
  g.addVertex(head);
  g.addVertex(neg);
  g.addEdge(head, neg, UniversalEnglishGrammaticalRelations.NEGATION_MODIFIER, 0, false);

  Mention m = new Mention();
  m.headIndexedWord = head;
  m.basicDependency = g;
  m.enhancedDependency = g;

  Dictionaries dict = new Dictionaries();
  dict.negations.add("not");

  int result = m.getNegation(dict);
  assertEquals(1, result);
}
@Test
public void testGetModifiersReturnsCountForAdjMod() throws IOException, ClassNotFoundException {
  CoreLabel headToken = new CoreLabel();
  headToken.setIndex(1);
  IndexedWord head = new IndexedWord(headToken);

  CoreLabel modToken = new CoreLabel();
  modToken.setLemma("great");
  modToken.setIndex(2);
  IndexedWord mod = new IndexedWord(modToken);

  SemanticGraph g = new SemanticGraph();
  g.addVertex(head);
  g.addVertex(mod);
  g.addEdge(head, mod, UniversalEnglishGrammaticalRelations.ADJECTIVAL_MODIFIER, 1.0, false);

  Mention mention = new Mention();
  mention.headIndexedWord = head;
  mention.basicDependency = g;
  mention.enhancedDependency = g;

  Dictionaries dict = new Dictionaries();

  int result = mention.getModifiers(dict);
  assertEquals(1, result);
}
@Test
public void testGetModalReturns1IfContainsModalWord() throws IOException, ClassNotFoundException {
  CoreLabel modal = new CoreLabel();
  modal.setLemma("must");
  IndexedWord w = new IndexedWord(modal);
  IndexedWord head = new IndexedWord(new CoreLabel());

  SemanticGraph g = new SemanticGraph();
  g.addVertex(head);
  g.addVertex(w);
  g.addEdge(head, w, UniversalEnglishGrammaticalRelations.AUX_MODIFIER, 0, false);

  Mention mention = new Mention();
  mention.headIndexedWord = head;
  mention.enhancedDependency = g;

  Dictionaries dict = new Dictionaries();
  dict.modals.add("must");

  int result = mention.getModal(dict);
  assertEquals(1, result);
}
@Test
public void testEqualsFailsOnDifferentMentionType() {
  Mention m1 = new Mention();
  m1.mentionType = Dictionaries.MentionType.PROPER;
  Mention m2 = new Mention();
  m2.mentionType = Dictionaries.MentionType.NOMINAL;
  assertFalse(m1.equals(m2));
}
@Test
public void testEqualsFailsOnDifferentStartIndex() {
  Mention m1 = new Mention();
  m1.startIndex = 1;
  Mention m2 = new Mention();
  m2.startIndex = 2;
  assertFalse(m1.equals(m2));
}
@Test
public void testEqualsFailsOnDifferentSentenceWords() {
  CoreLabel t1 = new CoreLabel();
  t1.setWord("X");
  List<CoreLabel> a = Collections.singletonList(t1);
  List<CoreLabel> b = Collections.singletonList(new CoreLabel());

  Mention m1 = new Mention();
  m1.sentenceWords = a;
  Mention m2 = new Mention();
  m2.sentenceWords = b;
  assertFalse(m1.equals(m2));
}
@Test
public void testIncludedInReturnsFalseWhenSentenceMismatch() {
  Mention outer = new Mention();
  outer.sentNum = 1;
  outer.startIndex = 0;
  outer.endIndex = 5;

  Mention inner = new Mention();
  inner.sentNum = 2;
  inner.startIndex = 2;
  inner.endIndex = 3;

  assertFalse(inner.includedIn(outer));
}
@Test
public void testIncludedInReturnsFalseWhenOutsideSpan() {
  Mention m1 = new Mention();
  m1.sentNum = 0;
  m1.startIndex = 0;
  m1.endIndex = 4;

  Mention m2 = new Mention();
  m2.sentNum = 0;
  m2.startIndex = 1;
  m2.endIndex = 5;

  assertFalse(m2.includedIn(m1));
}
@Test
public void testGetSplitPatternReturnsComponents() {
  CoreLabel w = new CoreLabel();
  w.set(CoreAnnotations.TextAnnotation.class, "library");
  w.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
  w.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
  w.setIndex(1);

  Mention m = new Mention();
  m.headWord = w;
  m.nerString = "O";
  m.headIndexedWord = new IndexedWord(w);
  m.enhancedDependency = new SemanticGraph();

  String[] pattern = m.getSplitPattern();

  assertEquals(4, pattern.length);
  assertEquals("library", pattern[0]);
}
@Test
public void testGetPatternHandlesNERTransition() {
  CoreLabel a = new CoreLabel();
  a.set(CoreAnnotations.TextAnnotation.class, "John");
  a.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
  a.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");

  CoreLabel b = new CoreLabel();
  b.set(CoreAnnotations.TextAnnotation.class, "Apple");
  b.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORG");
  b.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");

  List<AbstractCoreLabel> toks = new ArrayList<>();
  toks.add(a);
  toks.add(b);

  Mention m = new Mention();
  String pattern = m.getPattern(toks);
  assertTrue(pattern.contains("<PERSON>"));
  assertTrue(pattern.contains("<ORG>"));
}
@Test
public void testSetAnimacyHandlesUnknownNERCase() throws IOException, ClassNotFoundException {
  CoreLabel head = new CoreLabel();
  head.set(CoreAnnotations.TextAnnotation.class, "cloud");
  head.set(CoreAnnotations.NamedEntityTagAnnotation.class, "???");

  Mention m = new Mention();
  m.headWord = head;
  m.mentionType = Dictionaries.MentionType.PROPER;
  m.nerString = "???";
  m.headString = "cloud";

  Dictionaries dict = new Dictionaries();
//  m.setAnimacy(dict);

  assertEquals(Dictionaries.Animacy.UNKNOWN, m.animacy);
}
@Test
public void testSetGenderAvoidsPlural() throws IOException, ClassNotFoundException {
  CoreLabel tok = new CoreLabel();
  tok.set(CoreAnnotations.TextAnnotation.class, "engineers");

  Mention m = new Mention();
  m.headWord = tok;
  m.headString = "engineers";
  m.mentionType = Dictionaries.MentionType.PROPER;
  m.nerString = "O";
  m.number = Dictionaries.Number.PLURAL;

  Dictionaries dict = new Dictionaries();
  dict.maleWords.add("engineers");
//  m.setGender(dict, Dictionaries.Gender.MALE);

  assertEquals(Dictionaries.Gender.UNKNOWN, m.gender);
}
@Test
public void testSetNERStringUsesEntityTypeWhenAvailable() {
  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.EntityTypeAnnotation.class, "NAM");
  token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

  Mention m = new Mention();
  m.headWord = token;
//  m.setNERString();

  assertEquals("PERSON", m.nerString);
}
@Test
public void testSetNERStringDefaultsToOWhenMissing() {
  Mention m = new Mention();
  CoreLabel word = new CoreLabel();
  word.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
  m.headWord = word;
//  m.setNERString();
  assertEquals("O", m.nerString);
}
@Test
public void testEntityTypesAgreePersonPronounMatch() throws IOException, ClassNotFoundException {
  Mention m1 = new Mention();
  m1.headString = "he";
  m1.mentionType = Dictionaries.MentionType.PRONOMINAL;
  m1.nerString = "O";

  Mention m2 = new Mention();
  m2.nerString = "PERSON";

  Dictionaries dict = new Dictionaries();
  dict.personPronouns.add("he");

  boolean result = m1.entityTypesAgree(m2, dict);
  assertTrue(result);
}
@Test
public void testEntityTypesAgreeOrganizationPronounMatch() throws IOException, ClassNotFoundException {
  Mention m1 = new Mention();
  m1.headString = "they";
  m1.mentionType = Dictionaries.MentionType.PRONOMINAL;
  m1.nerString = "O";

  Mention m2 = new Mention();
  m2.nerString = "ORGANIZATION";

  Dictionaries dict = new Dictionaries();
  dict.organizationPronouns.add("they");

  boolean result = m1.entityTypesAgree(m2, dict);
  assertTrue(result);
}
@Test
public void testSetHeadStringSkipsKnownSuffix() {
  CoreLabel tok1 = new CoreLabel();
  tok1.set(CoreAnnotations.TextAnnotation.class, "John");
  CoreLabel tok2 = new CoreLabel();
  tok2.set(CoreAnnotations.TextAnnotation.class, "Corp.");
  tok2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORG");

  List<CoreLabel> span = Arrays.asList(tok1, tok2);

  SemanticGraph g = new SemanticGraph();
  Mention m = new Mention(1, 0, 2, span, g, g, span);
  m.headWord = tok2;
  m.headIndex = 1;
  m.mentionType = Dictionaries.MentionType.PROPER;

  tok2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORG");
//  m.setHeadString();
  assertEquals("john", m.headString);  
}
@Test
public void testGetContextRemovesDuplicateNamedEntities() {
  CoreLabel c1 = new CoreLabel();
  c1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
  c1.set(CoreAnnotations.TextAnnotation.class, "John");

  CoreLabel c2 = new CoreLabel();
  c2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
  c2.set(CoreAnnotations.TextAnnotation.class, "Doe");

  Mention m = new Mention();
  m.sentenceWords = Arrays.asList(c1, c2);
  List<String> context = m.getContext();

  assertEquals(1, context.size()); 
  assertTrue(context.get(0).contains("John") || context.get(0).contains("Doe"));
}
@Test
public void testHeadsAgreeNamedEntityInclusion() {
  CoreLabel w1 = new CoreLabel();
  w1.set(CoreAnnotations.TextAnnotation.class, "Obama");
  w1.setTag("NNP");

  CoreLabel w2 = new CoreLabel();
  w2.set(CoreAnnotations.TextAnnotation.class, "Barack");
  w2.setTag("NNP");

  Mention m1 = new Mention();
  m1.headString = "obama";
  m1.nerString = "PERSON";
  m1.originalSpan = Arrays.asList(w1);
  m1.headWord = w1;

  Mention m2 = new Mention();
  m2.headString = "obama";
  m2.nerString = "PERSON";
  m2.originalSpan = Arrays.asList(w2, w1); 
  m2.headWord = w1;

  assertTrue(m1.headsAgree(m2));
}
@Test
public void testHeadsAgreeFailsOnDifferentHead() {
  CoreLabel w1 = new CoreLabel();
  w1.set(CoreAnnotations.TextAnnotation.class, "Obama");
  w1.setTag("NNP");

  CoreLabel w2 = new CoreLabel();
  w2.set(CoreAnnotations.TextAnnotation.class, "Clinton");
  w2.setTag("NNP");

  Mention m1 = new Mention();
  m1.headString = "obama";
  m1.nerString = "PERSON";
  m1.originalSpan = Arrays.asList(w1);
  m1.headWord = w1;

  Mention m2 = new Mention();
  m2.headString = "clinton";
  m2.nerString = "PERSON";
  m2.originalSpan = Arrays.asList(w2);
  m2.headWord = w2;

  assertFalse(m1.headsAgree(m2));
}
@Test
public void testMoreRepresentativeThanBreakTieByHeadDistance() {
  CoreLabel h = new CoreLabel();
  h.set(CoreAnnotations.TextAnnotation.class, "Obama");
  h.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
  List<CoreLabel> span = Collections.singletonList(h);

  Mention m1 = new Mention(1, 0, 3, span, null, null, span);
  m1.mentionType = Dictionaries.MentionType.PROPER;
  m1.headIndex = 2;
  m1.startIndex = 0;
  m1.sentNum = 0;
  m1.nerString = "PERSON";
  m1.headWord = h;
  m1.originalSpan = span;

  Mention m2 = new Mention(2, 0, 3, span, null, null, span);
  m2.mentionType = Dictionaries.MentionType.PROPER;
  m2.headIndex = 1;
  m2.startIndex = 0;
  m2.sentNum = 0;
  m2.nerString = "PERSON";
  m2.headWord = h;
  m2.originalSpan = span;

  assertTrue(m1.moreRepresentativeThan(m2));
}
@Test
public void testMoreRepresentativeThanThrowsIllegalState() {
  CoreLabel h = new CoreLabel();
  h.set(CoreAnnotations.TextAnnotation.class, "Obama");
  h.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
  List<CoreLabel> span = Collections.singletonList(h);

  Mention m1 = new Mention(1, 0, 1, span, null, null, span);
  m1.mentionType = Dictionaries.MentionType.NOMINAL;
  m1.headIndex = 1;
  m1.startIndex = 0;
  m1.sentNum = 0;
  m1.nerString = "LOCATION";
  m1.headWord = h;
  m1.originalSpan = span;

  try {
    m1.moreRepresentativeThan(m1);
    fail("Expected IllegalStateException");
  } catch (IllegalStateException e) {
    assertTrue(e.getMessage().contains("Comparing a mention with itself"));
  }
}
@Test
public void testGetCoordinationReturnsOneWhenHeadHasConjChild() {
  CoreLabel root = new CoreLabel();
  root.setWord("company");
  root.setIndex(1);
  IndexedWord head = new IndexedWord(root);
  head.setTag("NN");

  CoreLabel conj = new CoreLabel();
  conj.setWord("and");
  conj.setIndex(2);
  IndexedWord dep = new IndexedWord(conj);

  SemanticGraph graph = new SemanticGraph();
  graph.addVertex(head);
  graph.addVertex(dep);
  graph.addEdge(head, dep, UniversalEnglishGrammaticalRelations.valueOf("conj:and"), 0, false);

  Mention m = new Mention();
  m.headIndexedWord = head;
  m.enhancedDependency = graph;

  assertEquals(1, m.getCoordination());
}
@Test
public void testGetCoordinationReturnsZeroForNoConj() {
  IndexedWord head = new IndexedWord();
  head.setIndex(1);
  SemanticGraph graph = new SemanticGraph();
  graph.addVertex(head);

  Mention m = new Mention();
  m.headIndexedWord = head;
  m.enhancedDependency = graph;

  assertEquals(0, m.getCoordination());
}
@Test
public void testGetPositionReturnsNullWhenIndexInvalid() {
  Mention m = new Mention();
  m.headIndex = -1;
  m.sentenceWords = Arrays.asList(new CoreLabel(), new CoreLabel(), new CoreLabel());
  assertNull(m.getPosition());
}
@Test
public void testSetDiscourseAssignsSubject() {
  CoreLabel word = new CoreLabel();
  word.setWord("Obama");
  word.setIndex(1);
  IndexedWord head = new IndexedWord(word);

  CoreLabel verb = new CoreLabel();
  verb.setWord("runs");
  verb.setIndex(2);
  IndexedWord v = new IndexedWord(verb);

  SemanticGraph graph = new SemanticGraph();
  graph.addVertex(head);
  graph.addVertex(v);
  graph.addEdge(v, head, UniversalEnglishGrammaticalRelations.NOMINAL_SUBJECT, 0, false);

  Mention m = new Mention();
  m.headWord = word;
  m.headIndex = 0;
  m.enhancedDependency = graph;
  m.basicDependency = graph;

//  m.setDiscourse();
  assertTrue(m.isSubject);
}
@Test
public void testSetGenderWithEmptyMentionString() throws IOException, ClassNotFoundException {
  CoreLabel tok = new CoreLabel();
  tok.set(CoreAnnotations.TextAnnotation.class, "Alice");

  Mention m = new Mention();
  m.headWord = tok;
  m.headString = "Alice";
  m.mentionType = Dictionaries.MentionType.NOMINAL;
  m.number = Dictionaries.Number.SINGULAR;

  Dictionaries dict = new Dictionaries();
//  m.setGender(dict, null);
  assertEquals(Dictionaries.Gender.UNKNOWN, m.gender);
}
@Test
public void testGetMentionStringHaltsAfterHeadWord() throws IOException, ClassNotFoundException {
  CoreLabel w1 = new CoreLabel();
  w1.set(CoreAnnotations.TextAnnotation.class, "Barack");
  CoreLabel w2 = new CoreLabel();
  w2.set(CoreAnnotations.TextAnnotation.class, "Obama");

  List<CoreLabel> span = Arrays.asList(w1, w2);

  Mention m = new Mention();
  m.originalSpan = span;
  m.headWord = w2;

  List<String> result = m.getSingletonFeatures(new Dictionaries());
  assertNotNull(result);
  assertFalse(result.isEmpty());
}
@Test
public void testProcessDoesNotThrowWhenSemanticsIsNull() throws Exception {
  CoreLabel tok = new CoreLabel();
  tok.set(CoreAnnotations.TextAnnotation.class, "Obama");
  tok.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
  tok.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
  tok.setWord("Obama");

  List<CoreLabel> span = Collections.singletonList(tok);
  SemanticGraph graph = new SemanticGraph();

  Mention m = new Mention(1, 0, 1, span, graph, graph, span);
  m.headWord = tok;
  m.headIndex = 0;
  m.mentionType = Dictionaries.MentionType.PROPER;
  m.headString = "obama";
  m.nerString = "PERSON";
  m.originalSpan = span;
  m.sentenceWords = span;

  Dictionaries dict = new Dictionaries();
  m.process(dict, null);
}
@Test
public void testSetSingletonMarksSingletonWhenScoreLow() throws IOException, ClassNotFoundException {
  CoreLabel head = new CoreLabel();
  head.set(CoreAnnotations.TextAnnotation.class, "thing");
  head.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

  Mention mention = new Mention();
  mention.mentionType = Dictionaries.MentionType.NOMINAL;
  mention.headWord = head;
  mention.headString = "thing";
  mention.nerString = "O";
  mention.animacy = Dictionaries.Animacy.UNKNOWN;
  mention.person = Dictionaries.Person.UNKNOWN;
  mention.number = Dictionaries.Number.PLURAL;

  Dictionaries dict = new Dictionaries();

//  LogisticClassifier<String, String> classifier = new LogisticClassifier<String, String>() {
//    @Override
//    public double probabilityOf(edu.stanford.nlp.ling.Datum<String, String> example) {
//      return 0.1;
//    }
//  };

//  mention.process(dict, null, classifier);
  assertTrue(mention.isSingleton);
}
@Test
public void testRemoveParenthesisMultipleOpenings() {
  String input = "Texas (USA) (South)";
  String result = Mention.removeParenthesis(input);
  assertEquals("Texas", result);
}
@Test
public void testIsAppositionReturnsFalseWhenAppositionsNull() {
  Mention m1 = new Mention();
  Mention m2 = new Mention();
  assertFalse(m1.isApposition(m2));
}
@Test
public void testGetPatternWithEmptyInput() {
  List<AbstractCoreLabel> tokens = new ArrayList<>();
  Mention m = new Mention();
  String pattern = m.getPattern(tokens);
  assertEquals("", pattern);
}
@Test
public void testSetNumberFallsBackToPluralFromDict() throws IOException, ClassNotFoundException {
  CoreLabel h = new CoreLabel();
  h.set(CoreAnnotations.TextAnnotation.class, "children");
  h.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
  h.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

  Mention m = new Mention();
  m.headWord = h;
  m.headString = "children";
  m.mentionType = Dictionaries.MentionType.PROPER;
  m.nerString = "O";

  Dictionaries dict = new Dictionaries();
  dict.pluralWords.add("children");

  m.setNumber(dict);
  assertEquals(Dictionaries.Number.PLURAL, m.number);
}
@Test
public void testSetPersonFallbackToThey() throws IOException, ClassNotFoundException {
  Mention m = new Mention();
  CoreLabel h = new CoreLabel();
  h.set(CoreAnnotations.TextAnnotation.class, "they");
  m.headWord = h;
  m.headString = "they";
  m.mentionType = Dictionaries.MentionType.PRONOMINAL;
  m.gender = Dictionaries.Gender.UNKNOWN;
  m.animacy = Dictionaries.Animacy.UNKNOWN;
  m.number = Dictionaries.Number.PLURAL;

  Dictionaries dict = new Dictionaries();
  dict.thirdPersonPronouns.add("they");

//  m.setPerson(dict);
  assertEquals(Dictionaries.Person.THEY, m.person);
}
@Test
public void testSetPersonHasNoMatchDefaultsToUnknown() throws IOException, ClassNotFoundException {
  Mention m = new Mention();
  CoreLabel h = new CoreLabel();
  h.set(CoreAnnotations.TextAnnotation.class, "xyz");
  m.headWord = h;
  m.headString = "xyz";
  m.mentionType = Dictionaries.MentionType.PRONOMINAL;
  m.number = Dictionaries.Number.SINGULAR;

  Dictionaries dict = new Dictionaries();

//  m.setPerson(dict);
  assertEquals(Dictionaries.Person.UNKNOWN, m.person);
}
@Test
public void testStringWithoutArticleNullFallback() {
  Mention m = new Mention();
//  m.spanString = null;
  String result = m.stringWithoutArticle(null);
  assertNull(result);
}
@Test
public void testStringWithoutArticleMatchesExactCase() {
  Mention m = new Mention();
//  m.spanString = "The cat";
  String result = m.stringWithoutArticle(null);
  assertEquals("cat", result);
}
@Test
public void testSpanToStringEmptyOriginalSpan() {
  Mention m = new Mention();
  m.originalSpan = new ArrayList<>();
  String span = m.spanToString();
  assertEquals("", span);
}
@Test
public void testSameSentenceReturnsTrueWithSelf() {
  Mention m = new Mention();
  m.sentenceWords = Collections.singletonList(new CoreLabel());
  assertTrue(m.sameSentence(m));
}
@Test
public void testEntityTypesAgreeAceTypePrefixFallback() throws IOException, ClassNotFoundException {
  Mention pronoun = new Mention();
  pronoun.mentionType = Dictionaries.MentionType.PRONOMINAL;
  pronoun.headString = "she";
  pronoun.nerString = "O";

  Mention ace = new Mention();
  ace.nerString = "PER-Identifier";

  Dictionaries dict = new Dictionaries();
  dict.personPronouns.add("she");

  assertTrue(pronoun.entityTypesAgree(ace, dict));
}
@Test
public void testGetCoordinationMultipleConjRelations() {
  CoreLabel root = new CoreLabel();
  root.setWord("President");
  root.setIndex(1);
  IndexedWord head = new IndexedWord(root);
  head.setTag("NNP");

  CoreLabel c1 = new CoreLabel();
  c1.setWord("and");
  c1.setIndex(2);
  IndexedWord w1 = new IndexedWord(c1);

  CoreLabel c2 = new CoreLabel();
  c2.setWord("or");
  c2.setIndex(3);
  IndexedWord w2 = new IndexedWord(c2);

  SemanticGraph g = new SemanticGraph();
  g.addVertex(head);
  g.addVertex(w1);
  g.addVertex(w2);
  g.addEdge(head, w1, UniversalEnglishGrammaticalRelations.valueOf("conj:and"), 0, false);
  g.addEdge(head, w2, UniversalEnglishGrammaticalRelations.valueOf("conj:or"), 0, false);

  Mention mention = new Mention();
  mention.headIndexedWord = head;
  mention.enhancedDependency = g;

  assertEquals(1, mention.getCoordination());
} 
}