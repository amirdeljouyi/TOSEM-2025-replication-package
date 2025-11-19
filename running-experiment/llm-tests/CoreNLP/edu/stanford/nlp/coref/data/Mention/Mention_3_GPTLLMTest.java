package edu.stanford.nlp.coref.data;

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

public class Mention_3_GPTLLMTest {

 @Test
  public void testSpanToString() {
    CoreLabel w1 = new CoreLabel();
    w1.set(CoreAnnotations.TextAnnotation.class, "The");
    CoreLabel w2 = new CoreLabel();
    w2.set(CoreAnnotations.TextAnnotation.class, "president");

    List<CoreLabel> span = new ArrayList<>();
    span.add(w1);
    span.add(w2);

    Mention mention = new Mention();
    mention.originalSpan = span;

    assertEquals("The president", mention.spanToString());
  }
@Test
  public void testLowercaseSpanString() {
    CoreLabel w1 = new CoreLabel();
    w1.set(CoreAnnotations.TextAnnotation.class, "Apple");
    CoreLabel w2 = new CoreLabel();
    w2.set(CoreAnnotations.TextAnnotation.class, "Pie");

    List<CoreLabel> span = new ArrayList<>();
    span.add(w1);
    span.add(w2);

    Mention mention = new Mention();
    mention.originalSpan = span;

    assertEquals("apple pie", mention.lowercaseNormalizedSpanString());
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
    List<CoreLabel> sentence = new ArrayList<>();
    sentence.add(new CoreLabel());

    Mention m1 = new Mention();
    m1.sentenceWords = sentence;

    Mention m2 = new Mention();
    m2.sentenceWords = sentence;

    assertTrue(m1.sameSentence(m2));
  }
@Test
  public void testSameSentenceFalse() {
    Mention m1 = new Mention();
    m1.sentenceWords = new ArrayList<>();

    Mention m2 = new Mention();
    m2.sentenceWords = new ArrayList<>();

    assertFalse(m1.sameSentence(m2));
  }
@Test
  public void testHeadsAgreeExactMatch() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();

    m1.headString = "john";
    m2.headString = "john";

    m1.nerString = "O";
    m2.nerString = "O";

    CoreLabel head1 = new CoreLabel();
    head1.set(CoreAnnotations.TextAnnotation.class, "John");
    m1.headWord = head1;

    CoreLabel head2 = new CoreLabel();
    head2.set(CoreAnnotations.TextAnnotation.class, "John");
    m2.headWord = head2;

    List<CoreLabel> span1 = new ArrayList<>();
    span1.add(head1);
    m1.originalSpan = span1;

    List<CoreLabel> span2 = new ArrayList<>();
    span2.add(head2);
    m2.originalSpan = span2;

    assertTrue(m1.headsAgree(m2));
  }
@Test
  public void testHeadsAgreeNamedEntitySubsetMatch() {
    Mention m1 = new Mention();
    m1.nerString = "PERSON";
    m1.headString = "joe";
    m1.originalSpan = new ArrayList<>();
    CoreLabel head1 = new CoreLabel();
    head1.set(CoreAnnotations.TextAnnotation.class, "Joe");
    head1.setTag("NNP");
    m1.headWord = head1;
    m1.originalSpan.add(head1);

    Mention m2 = new Mention();
    m2.nerString = "PERSON";
    m2.headString = "joe";
    m2.originalSpan = new ArrayList<>();
    CoreLabel head2 = new CoreLabel();
    head2.set(CoreAnnotations.TextAnnotation.class, "Joe");
    head2.setTag("NNP");
    m2.headWord = head2;
    m2.originalSpan.add(head2);
    m2.originalSpan.add(new CoreLabel()); 

    assertTrue(m1.headsAgree(m2));
  }
@Test
  public void testIsListMemberOfTrue() {
    Mention list = new Mention();
    list.mentionType = Dictionaries.MentionType.LIST;
    list.startIndex = 0;
    list.endIndex = 5;
    list.sentenceWords = new ArrayList<>();
    list.sentenceWords.add(new CoreLabel());

    Mention child = new Mention();
    child.mentionType = Dictionaries.MentionType.NOMINAL;
    child.startIndex = 1;
    child.endIndex = 2;
    child.sentenceWords = list.sentenceWords;

    assertTrue(child.isListMemberOf(list));
  }
@Test
  public void testIsListMemberOfFalseNestedList() {
    Mention m1 = new Mention();
    m1.mentionType = Dictionaries.MentionType.LIST;

    Mention m2 = new Mention();
    m2.mentionType = Dictionaries.MentionType.LIST;

    assertFalse(m1.isListMemberOf(m2));
  }
@Test
  public void testAppearEarlierThan() {
    Mention m1 = new Mention();
    m1.sentNum = 1;
    m1.startIndex = 2;
    m1.endIndex = 3;
    m1.headIndex = 2;
    m1.mentionType = Dictionaries.MentionType.PROPER;
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Obama");
    m1.originalSpan = Collections.singletonList(token);

    Mention m2 = new Mention();
    m2.sentNum = 2;
    m2.startIndex = 0;
    m2.endIndex = 1;
    m2.originalSpan = Collections.singletonList(token);

    assertTrue(m1.appearEarlierThan(m2));
  }
@Test
  public void testRemoveParenthesis() {
    String result1 = Mention.removeParenthesis("Google (USA)");
    assertEquals("Google", result1);

    String result2 = Mention.removeParenthesis("OpenAI");
    assertEquals("", result2);
  }
@Test
  public void testStringWithoutArticle() {
    Mention m = new Mention();
    assertEquals("boat", m.stringWithoutArticle("a boat"));
    assertEquals("apple", m.stringWithoutArticle("An apple"));
    assertEquals("same", m.stringWithoutArticle("The same"));
    assertEquals("unchanged", m.stringWithoutArticle("unchanged"));
    m.originalSpan = new ArrayList<>();
    CoreLabel w = new CoreLabel();
    w.set(CoreAnnotations.TextAnnotation.class, "just");
    m.originalSpan.add(w);
    assertEquals("just", m.stringWithoutArticle(null));
  }
@Test
  public void testBuildQueryText() {
    List<String> terms = new ArrayList<>();
    terms.add("john");
    terms.add("smith");
    String result = Mention.buildQueryText(terms);
    assertEquals("john smith", result);
  }
@Test
  public void testInsideInTrue() {
    Mention m1 = new Mention();
    m1.sentNum = 1;
    m1.startIndex = 2;
    m1.endIndex = 4;

    Mention m2 = new Mention();
    m2.sentNum = 1;
    m2.startIndex = 1;
    m2.endIndex = 6;

    assertTrue(m1.insideIn(m2));
  }
@Test
  public void testInsideInFalseDifferentSentence() {
    Mention m1 = new Mention();
    m1.sentNum = 1;

    Mention m2 = new Mention();
    m2.sentNum = 2;

    assertFalse(m1.insideIn(m2));
  }
@Test
  public void testAddAppositionAndIsApposition() {
    Mention mention = new Mention();
    Mention other = new Mention();

    assertFalse(mention.isApposition(other));

    mention.addApposition(other);

    assertTrue(mention.isApposition(other));
  }
@Test
  public void testAddPredicateNominativesAndIsPredicateNominatives() {
    Mention mention = new Mention();
    Mention other = new Mention();

    assertFalse(mention.isPredicateNominatives(other));

    mention.addPredicateNominatives(other);

    assertTrue(mention.isPredicateNominatives(other));
  }
@Test
  public void testAddRelativePronounAndIsRelativePronoun() {
    Mention mention = new Mention();
    Mention other = new Mention();

    assertFalse(mention.isRelativePronoun(other));

    mention.addRelativePronoun(other);

    assertTrue(mention.isRelativePronoun(other));
  }
@Test
  public void testIsTheCommonNoun() {
    Mention m = new Mention();
    CoreLabel the = new CoreLabel();
    the.set(CoreAnnotations.TextAnnotation.class, "The");
    CoreLabel cat = new CoreLabel();
    cat.set(CoreAnnotations.TextAnnotation.class, "cat");
    m.originalSpan = new ArrayList<>();
    m.originalSpan.add(the);
    m.originalSpan.add(cat);
    m.mentionType = Dictionaries.MentionType.NOMINAL;

    assertTrue(m.isTheCommonNoun());
  }
@Test
public void testNerTokensReturnsSubList() {
  CoreLabel tok1 = new CoreLabel();
  tok1.set(CoreAnnotations.TextAnnotation.class, "New");
  tok1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");

  CoreLabel tok2 = new CoreLabel();
  tok2.set(CoreAnnotations.TextAnnotation.class, "York");
  tok2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");

  CoreLabel tok3 = new CoreLabel();
  tok3.set(CoreAnnotations.TextAnnotation.class, "Times");
  tok3.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

  List<CoreLabel> span = new ArrayList<>();
  span.add(tok1);
  span.add(tok2);
  span.add(tok3);

  Mention mention = new Mention();
  mention.originalSpan = span;
  mention.nerString = "LOCATION";
  mention.startIndex = 0;
  mention.endIndex = 3;
  mention.headIndex = 1;

  List<CoreLabel> result = mention.nerTokens();

  assertEquals(2, result.size());
  assertEquals("New", result.get(0).get(CoreAnnotations.TextAnnotation.class));
  assertEquals("York", result.get(1).get(CoreAnnotations.TextAnnotation.class));
}
@Test
public void testRemovePhraseAfterHeadNoCommaNoWH() {
  Mention mention = new Mention();

  CoreLabel w1 = new CoreLabel();
  w1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "DT");
  w1.set(CoreAnnotations.TextAnnotation.class, "The");

  CoreLabel w2 = new CoreLabel();
  w2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
  w2.set(CoreAnnotations.TextAnnotation.class, "cat");

  CoreLabel w3 = new CoreLabel();
  w3.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBZ");
  w3.set(CoreAnnotations.TextAnnotation.class, "sits");

  List<CoreLabel> span = new ArrayList<>();
  span.add(w1);
  span.add(w2);
  span.add(w3);

  mention.originalSpan = span;
  mention.startIndex = 0;
  mention.headIndex = 1;

  String result = mention.removePhraseAfterHead();
  assertEquals("The cat sits", result);
}
@Test
public void testEqualsWithDifferentMentionTypes() {
  Mention m1 = new Mention();
  Mention m2 = new Mention();

  m1.mentionType = Dictionaries.MentionType.PROPER;
  m2.mentionType = Dictionaries.MentionType.NOMINAL;

  assertNotEquals(m1, m2);
}
@Test
public void testEqualsWithNull() {
  Mention mention = new Mention();
  assertFalse(mention.equals(null));
}
@Test
public void testEqualsWithDifferentClassType() {
  Mention mention = new Mention();
  assertFalse(mention.equals("A string"));
}
@Test
public void testIsRoleAppositiveFalseDueToNER() throws IOException, ClassNotFoundException {
  Mention m1 = new Mention();
  m1.mentionType = Dictionaries.MentionType.NOMINAL;
  m1.originalSpan = new ArrayList<>();
  CoreLabel tok1 = new CoreLabel();
  tok1.set(CoreAnnotations.TextAnnotation.class, "President");
  m1.originalSpan.add(tok1);
  m1.headWord = tok1;
  m1.nerString = "ORG";  

  Mention m2 = new Mention();
  m2.originalSpan = new ArrayList<>();
  CoreLabel tok2 = new CoreLabel();
  tok2.set(CoreAnnotations.TextAnnotation.class, "President Bush");
  m2.originalSpan.add(tok2);
  m2.nerString = "PER";

  m1.spanToString();
  m2.spanToString();

  Dictionaries dict = new Dictionaries();

  assertFalse(m1.isRoleAppositive(m2, dict));
}
@Test
public void testGetMentionStringStopsAtHead() throws IOException, ClassNotFoundException {
  CoreLabel w1 = new CoreLabel();
  w1.set(CoreAnnotations.TextAnnotation.class, "giant");
  CoreLabel w2 = new CoreLabel();
  w2.set(CoreAnnotations.TextAnnotation.class, "mutant");
  CoreLabel w3 = new CoreLabel();
  w3.set(CoreAnnotations.TextAnnotation.class, "lizard");

  List<CoreLabel> span = new ArrayList<>();
  span.add(w1);
  span.add(w2);
  span.add(w3);

  Mention m = new Mention();
  m.originalSpan = span;
  m.headWord = w2; 

  List<String> result = m.getSingletonFeatures(new Dictionaries());

  assertTrue(result.contains("mutant"));  
  assertFalse(result.contains("lizard")); 
}
@Test
public void testRemoveParenthesisOnlyParenthesis() {
  String result = Mention.removeParenthesis("(");
  assertEquals("", result);
}
@Test
public void testIsDemonymFalseEmptyDict() throws IOException, ClassNotFoundException {
  Mention m1 = new Mention();
  CoreLabel tok1 = new CoreLabel();
  tok1.set(CoreAnnotations.TextAnnotation.class, "French");
  m1.originalSpan = Collections.singletonList(tok1);

  Mention m2 = new Mention();
  CoreLabel tok2 = new CoreLabel();
  tok2.set(CoreAnnotations.TextAnnotation.class, "France");
  m2.originalSpan = Collections.singletonList(tok2);

  Dictionaries dict = new Dictionaries(); 

  assertFalse(m1.isDemonym(m2, dict));
}
@Test
public void testIsDemonymTrueViaReverseLookup() throws IOException, ClassNotFoundException {
  Mention m1 = new Mention();
  CoreLabel tok1 = new CoreLabel();
  tok1.set(CoreAnnotations.TextAnnotation.class, "Germany");
  m1.originalSpan = Collections.singletonList(tok1);

  Mention m2 = new Mention();
  CoreLabel tok2 = new CoreLabel();
  tok2.set(CoreAnnotations.TextAnnotation.class, "German");
  m2.originalSpan = Collections.singletonList(tok2);

  Dictionaries dict = new Dictionaries();
//  dict.addDemonym("german", "germany");

  assertTrue(m1.isDemonym(m2, dict));
}
@Test
public void testRemovePhraseAfterHeadWithWHWordBeforeComma() {
  Mention mention = new Mention();

  CoreLabel w1 = new CoreLabel();
  w1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "DT");
  w1.set(CoreAnnotations.TextAnnotation.class, "The");

  CoreLabel w2 = new CoreLabel();
  w2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
  w2.set(CoreAnnotations.TextAnnotation.class, "man");

  CoreLabel w3 = new CoreLabel();
  w3.set(CoreAnnotations.PartOfSpeechAnnotation.class, "WP");
  w3.set(CoreAnnotations.TextAnnotation.class, "who");

  CoreLabel w4 = new CoreLabel();
  w4.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBD");
  w4.set(CoreAnnotations.TextAnnotation.class, "ran");

  List<CoreLabel> span = new ArrayList<>();
  span.add(w1);
  span.add(w2);
  span.add(w3);
  span.add(w4);

  mention.originalSpan = span;
  mention.startIndex = 0;
  mention.headIndex = 1;
  mention.endIndex = 4;

  String result = mention.removePhraseAfterHead();
  assertEquals("The man", result);
}
@Test
public void testGetSplitPatternWithSinglePremodifier() {
  Mention mention = new Mention();

  CoreLabel adj = new CoreLabel();
  adj.set(CoreAnnotations.TextAnnotation.class, "big");
  adj.set(CoreAnnotations.LemmaAnnotation.class, "big");
  adj.set(CoreAnnotations.PartOfSpeechAnnotation.class, "JJ");
  IndexedWord iw1 = new IndexedWord(adj);
  iw1.setIndex(1);

  CoreLabel noun = new CoreLabel();
  noun.set(CoreAnnotations.TextAnnotation.class, "cat");
  noun.set(CoreAnnotations.LemmaAnnotation.class, "cat");
  noun.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
  IndexedWord iw2 = new IndexedWord(noun);
  iw2.setIndex(2);

  SemanticGraph graph = new SemanticGraph();
  graph.addVertex(iw1);
  graph.addVertex(iw2);
  graph.addEdge(iw2, iw1, GrammaticalRelation.valueOf("amod"), 1, false);

  noun.setIndex(2);

  mention.headIndexedWord = iw2;
  mention.headWord = noun;
  mention.enhancedDependency = graph;

  String[] result = mention.getSplitPattern();
  assertEquals("cat", result[0]);
  assertTrue(result[1].contains("big"));
  assertTrue(result[2].contains("big"));
  assertTrue(result[3].contains("big"));
}
@Test
public void testPreprocessSearchTermEscapingSymbols() {
  Mention mention = new Mention();
  mention.originalSpan = new ArrayList<>();
  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "Node.js");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
  mention.originalSpan.add(token);
  mention.headWord = token;
  mention.headIndex = 0;
  mention.startIndex = 0;

  List<String> terms = mention.preprocessSearchTerm();
  assertTrue(terms.stream().anyMatch(t -> t.contains("Node\\.js")));
}
@Test
public void testSetHeadStringSkipsKnownSuffix() {
  Mention mention = new Mention();
  mention.originalSpan = new ArrayList<>();

  CoreLabel org = new CoreLabel();
  org.set(CoreAnnotations.TextAnnotation.class, "Corp.");
  org.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORG");
  org.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
  org.setIndex(2);

  CoreLabel name = new CoreLabel();
  name.set(CoreAnnotations.TextAnnotation.class, "Tesla");
  name.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORG");
  name.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
  name.setIndex(1);

  mention.originalSpan.add(name);
  mention.originalSpan.add(org);
  mention.headWord = org;
  mention.headIndex = 1;
  mention.startIndex = 0;

  SemanticGraph graph = new SemanticGraph();
  IndexedWord word1 = new IndexedWord(name);
  word1.setIndex(1);
  IndexedWord word2 = new IndexedWord(org);
  word2.setIndex(2);

  graph.addVertex(word1);
  graph.addVertex(word2);

  mention.basicDependency = graph;

//  mention.setHeadString();
  assertEquals("tesla", mention.headString);
}
@Test
public void testGetPremodifiersFiltersOutDet() {
  Mention mention = new Mention();

  CoreLabel det = new CoreLabel();
  det.set(CoreAnnotations.TextAnnotation.class, "the");
  det.set(CoreAnnotations.PartOfSpeechAnnotation.class, "DT");
  IndexedWord iwDet = new IndexedWord(det);
  iwDet.setIndex(1);

  CoreLabel head = new CoreLabel();
  head.set(CoreAnnotations.TextAnnotation.class, "boat");
  head.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
  IndexedWord iwHead = new IndexedWord(head);
  iwHead.setIndex(2);

  SemanticGraph graph = new SemanticGraph();
  graph.addVertex(iwDet);
  graph.addVertex(iwHead);
//  graph.addEdge(iwHead, iwDet, GrammaticalRelation.DETERMINER, 1.0, false);

  mention.headWord = head;
  mention.headIndexedWord = iwHead;
  mention.enhancedDependency = graph;

  ArrayList<ArrayList<IndexedWord>> result = mention.getPremodifiers();
  assertTrue(result.isEmpty());
}
@Test
public void testGetPostmodifiersFiltersOutPossessiveClitic() {
  Mention mention = new Mention();

  CoreLabel poss = new CoreLabel();
  poss.set(CoreAnnotations.TextAnnotation.class, "'s");
  poss.set(CoreAnnotations.PartOfSpeechAnnotation.class, "POS");
  IndexedWord iwPoss = new IndexedWord(poss);
  iwPoss.setIndex(3);

  CoreLabel head = new CoreLabel();
  head.set(CoreAnnotations.TextAnnotation.class, "John");
  head.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
  IndexedWord iwHead = new IndexedWord(head);
  iwHead.setIndex(2);

  SemanticGraph graph = new SemanticGraph();
  graph.addVertex(iwHead);
  graph.addVertex(iwPoss);
  graph.addEdge(iwHead, iwPoss, GrammaticalRelation.valueOf("case"), 1.0, false);

  mention.headWord = head;
  mention.headIndexedWord = iwHead;
  mention.enhancedDependency = graph;

  ArrayList<ArrayList<IndexedWord>> result = mention.getPostmodifiers();
  assertTrue(result.isEmpty());
}
@Test
public void testIsCoordinatedReturnsTrue() {
  Mention mention = new Mention();

  CoreLabel cc = new CoreLabel();
  cc.set(CoreAnnotations.TextAnnotation.class, "and");
  cc.set(CoreAnnotations.PartOfSpeechAnnotation.class, "CC");
  IndexedWord iwCC = new IndexedWord(cc);
  iwCC.setIndex(3);

  CoreLabel head = new CoreLabel();
  head.set(CoreAnnotations.TextAnnotation.class, "Bob");
  head.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
  IndexedWord iwHead = new IndexedWord(head);
  iwHead.setIndex(2);

  SemanticGraph graph = new SemanticGraph();
  graph.addVertex(iwHead);
  graph.addVertex(iwCC);
  graph.addEdge(iwHead, iwCC, GrammaticalRelation.valueOf("cc"), 1.0, false);

  mention.headWord = head;
  mention.headIndexedWord = iwHead;
  mention.enhancedDependency = graph;

  assertTrue(mention.isCoordinated());
}
@Test
public void testMentionHashCodeSameStartEndIndex() {
  Mention m1 = new Mention();
  m1.startIndex = 5;
  m1.endIndex = 10;

  Mention m2 = new Mention();
  m2.startIndex = 5;
  m2.endIndex = 10;

  assertEquals(m1.hashCode(), m2.hashCode());
}
@Test
public void testProcessHandlesSingletonWithoutPredictor() throws Exception {
  Mention mention = new Mention();
  mention.originalSpan = new ArrayList<>();
  CoreLabel word = new CoreLabel();
  word.set(CoreAnnotations.TextAnnotation.class, "John");
  word.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
  word.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
  mention.originalSpan.add(word);
  mention.headWord = word;
  mention.headIndex = 0;
  mention.startIndex = 0;
  mention.endIndex = 1;

  SemanticGraph graph = new SemanticGraph();
  IndexedWord iw = new IndexedWord(word);
  iw.setIndex(1);
  graph.addVertex(iw);
  mention.basicDependency = graph;
  mention.enhancedDependency = graph;

  Dictionaries dict = new Dictionaries();
//  Semantics semantics = new Semantics(null, null);

//  mention.process(dict, semantics, null);

  assertNotNull(mention.headString);
}
@Test
public void testSetNERStringACEFallbackToO() {
  Mention mention = new Mention();
  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.EntityTypeAnnotation.class, "NAM");
  token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
  mention.headWord = token;

//  mention.setNERString();

  assertEquals("PERSON", mention.nerString);
}
@Test
public void testSetNERStringNonACEFallbackToO() {
  Mention mention = new Mention();
  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
  mention.headWord = token;

//  mention.setNERString();

  assertEquals("PERSON", mention.nerString);
}
@Test
public void testSetNERStringMissingNERDefaultsToO() {
  Mention mention = new Mention();
  CoreLabel token = new CoreLabel(); 
  mention.headWord = token;

//  mention.setNERString();

  assertEquals("O", mention.nerString);
}
@Test
public void testEntityTypesAgreeDifferentNERButOAllowed() throws IOException, ClassNotFoundException {
  Mention m1 = new Mention();
  Mention m2 = new Mention();
  Dictionaries dict = new Dictionaries();

  m1.nerString = "O";
  m2.nerString = "LOCATION";
  m1.mentionType = Dictionaries.MentionType.PRONOMINAL;
  m1.headString = "it";
  m2.headString = "tree";

  assertTrue(m1.entityTypesAgree(m2, dict));
}
@Test
public void testIsAppositionWhenSetIsNull() {
  Mention mention = new Mention();
  Mention other = new Mention();

  assertFalse(mention.isApposition(other));
}
@Test
public void testIsPredicateNominativesWhenSetIsNull() {
  Mention mention = new Mention();
  Mention other = new Mention();

  assertFalse(mention.isPredicateNominatives(other));
}
@Test
public void testNullMentionSpanStringDoesNotThrowInIsRoleAppositive() throws IOException, ClassNotFoundException {
  Mention m1 = new Mention();
  m1.mentionType = Dictionaries.MentionType.NOMINAL;
  m1.headWord = new CoreLabel();
  m1.originalSpan = new ArrayList<>();
  m1.nerString = "PER";

  Mention m2 = new Mention();
  m2.headWord = new CoreLabel();
  m2.originalSpan = new ArrayList<>();
  m2.nerString = "PER";

  Dictionaries dict = new Dictionaries();

  assertFalse(m1.isRoleAppositive(m2, dict));
}
@Test
public void testGetGenderWhenSpanMatchesUnknown() throws IOException, ClassNotFoundException {
  Mention m = new Mention();
  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "UnknownEntity");
  CoreLabel head = new CoreLabel();
  head.set(CoreAnnotations.TextAnnotation.class, "UnknownEntity");
  m.headWord = head;
  m.nerString = "PER";

  m.originalSpan = new ArrayList<>();
  m.originalSpan.add(token);
  m.originalSpan.add(head);

  Dictionaries dict = new Dictionaries();
//  Dictionaries.Gender result = m.getGender(dict, Collections.singletonList("unknownentity"));

//  assertNull(result);
}
@Test
public void testGetRelationWhenParentMissing() {
  Mention mention = new Mention();
  CoreLabel hw = new CoreLabel();
  hw.set(CoreAnnotations.TextAnnotation.class, "walked");
  hw.setIndex(2);
  IndexedWord hwIndexed = new IndexedWord(hw);

  SemanticGraph graph = new SemanticGraph();
  graph.addVertex(hwIndexed);

  mention.headIndexedWord = hwIndexed;
  mention.headWord = hw;

  mention.enhancedDependency = graph;

  String rel = mention.getRelation();
  assertNull(rel);
}
@Test
public void testIncludedInFalseDifferentSentence() {
  Mention m1 = new Mention();
  Mention m2 = new Mention();

  m1.sentNum = 0;
  m1.startIndex = 1;
  m1.endIndex = 3;

  m2.sentNum = 1;
  m2.startIndex = 0;
  m2.endIndex = 4;

  assertFalse(m1.includedIn(m2));
}
@Test
public void testIsMemberOfSameListFalseWhenAnyBelongsNull() {
  Mention m1 = new Mention();
  Mention m2 = new Mention();
  m1.belongToLists = null;
  m2.belongToLists = null;

  assertFalse(m1.isMemberOfSameList(m2));
}
@Test
public void testIsMemberOfSameListTrueIfOverlap() {
  Mention list = new Mention();

  Mention m1 = new Mention();
  m1.belongToLists = Collections.singleton(list);

  Mention m2 = new Mention();
  m2.belongToLists = Collections.singleton(list);

  assertTrue(m1.isMemberOfSameList(m2));
}
@Test
public void testGetPositionFirstWord() {
  Mention mention = new Mention();
  CoreLabel tok = new CoreLabel();
  tok.set(CoreAnnotations.TextAnnotation.class, "I");
  mention.headIndex = 0;
  List<CoreLabel> sent = new ArrayList<>();
  sent.add(tok);
  sent.add(new CoreLabel());
  sent.add(new CoreLabel());
  sent.add(new CoreLabel());
  mention.sentenceWords = sent;

  assertEquals("first", mention.getPosition());
}
@Test
public void testGetPremodifierContextNoNamedEntity() {
  Mention mention = new Mention();
  mention.headWord = new CoreLabel();
  mention.headWord.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
  mention.headWord.set(CoreAnnotations.TextAnnotation.class, "tree");

  IndexedWord iw = new IndexedWord(mention.headWord);
  iw.setIndex(1);
  mention.headIndexedWord = iw;

  SemanticGraph graph = new SemanticGraph();
  graph.addVertex(iw);
  mention.enhancedDependency = graph;

  List<String> context = mention.getPremodifierContext();
  assertTrue(context.isEmpty());
}
@Test
public void testGetReportEmbeddingViaSiblingAdvclAs() throws IOException, ClassNotFoundException {
  Mention mention = new Mention();

  CoreLabel head = new CoreLabel();
  head.set(CoreAnnotations.TextAnnotation.class, "John");
  head.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
  IndexedWord headIW = new IndexedWord(head);
  headIW.setIndex(2);

  CoreLabel sibling = new CoreLabel();
  sibling.set(CoreAnnotations.TextAnnotation.class, "said");
  sibling.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBD");
  IndexedWord siblingIW = new IndexedWord(sibling);
  siblingIW.setIndex(3);

  CoreLabel marker = new CoreLabel();
  marker.set(CoreAnnotations.TextAnnotation.class, "as");
  marker.set(CoreAnnotations.PartOfSpeechAnnotation.class, "IN");
  IndexedWord markerIW = new IndexedWord(marker);
  markerIW.setIndex(4);

  SemanticGraph graph = new SemanticGraph();
  graph.addVertex(headIW);
  graph.addVertex(siblingIW);
  graph.addVertex(markerIW);
  graph.addEdge(headIW, siblingIW, UniversalEnglishGrammaticalRelations.ADV_CLAUSE_MODIFIER, 1.0, false);
  graph.addEdge(siblingIW, markerIW, UniversalEnglishGrammaticalRelations.MARKER, 1.0, false);

  mention.headIndexedWord = headIW;
  mention.enhancedDependency = graph;

  Dictionaries dict = new Dictionaries();
  dict.reportVerb.add("said");

  int result = mention.getReportEmbedding(dict);
  assertEquals(1, result);
}
@Test
public void testMoreRepresentativeThanNERFallbackToMisc() {
  Mention m1 = new Mention();
  Mention m2 = new Mention();

  
  m1.mentionType = Dictionaries.MentionType.PROPER;
  m2.mentionType = Dictionaries.MentionType.PROPER;

  m1.nerString = "PERSON";
  m2.nerString = "MISC";

  m1.headIndex = 2;
  m1.startIndex = 0;

  m2.headIndex = 2;
  m2.startIndex = 0;

  m1.sentNum = 1;
  m2.sentNum = 1;

  CoreLabel l1 = new CoreLabel();
  l1.set(CoreAnnotations.TextAnnotation.class, "Obama");
  m1.originalSpan = Collections.singletonList(l1);
  m2.originalSpan = Collections.singletonList(l1);

  assertTrue(m1.moreRepresentativeThan(m2));
}
@Test
public void testMoreRepresentativeThanSameMentionFailsWithException() {
  Mention m = new Mention();
  m.mentionType = Dictionaries.MentionType.NOMINAL;
  m.nerString = "ORG";
  m.headIndex = 2;
  m.startIndex = 1;
  m.originalSpan = Arrays.asList(new CoreLabel(), new CoreLabel());
  m.sentNum = 0;

  boolean threw = false;
  try {
    m.moreRepresentativeThan(m);
  } catch (IllegalStateException e) {
    threw = true;
  }
  assertTrue(threw);
}
@Test
public void testAnimaciesAgreeStrictEqual() {
  Mention m1 = new Mention();
  Mention m2 = new Mention();
  m1.animacy = Dictionaries.Animacy.ANIMATE;
  m2.animacy = Dictionaries.Animacy.ANIMATE;

  assertTrue(m1.animaciesAgree(m2, true));
}
@Test
public void testAnimaciesAgreeStrictUnequal() {
  Mention m1 = new Mention();
  Mention m2 = new Mention();
  m1.animacy = Dictionaries.Animacy.ANIMATE;
  m2.animacy = Dictionaries.Animacy.INANIMATE;

  assertFalse(m1.animaciesAgree(m2, true));
}
@Test
public void testEntityTypesAgreeStrictMismatchReturnsFalse() throws IOException, ClassNotFoundException {
  Mention m1 = new Mention();
  Mention m2 = new Mention();
  Dictionaries dict = new Dictionaries();

  m1.nerString = "PERSON";
  m2.nerString = "LOCATION";

  assertFalse(m1.entityTypesAgree(m2, dict, true));
}
@Test
public void testEntityTypesAgreeStrictSameReturnsTrue() throws IOException, ClassNotFoundException {
  Mention m1 = new Mention();
  Mention m2 = new Mention();
  Dictionaries dict = new Dictionaries();

  m1.nerString = "ORG";
  m2.nerString = "ORG";

  assertTrue(m1.entityTypesAgree(m2, dict, true));
}
@Test
public void testHeadsAgreeSubsetThenExactFallback() {
  Mention m1 = new Mention();
  Mention m2 = new Mention();

  CoreLabel token1 = new CoreLabel();
  token1.set(CoreAnnotations.TextAnnotation.class, "John");
  token1.setTag("NNP");

  CoreLabel token2 = new CoreLabel();
  token2.set(CoreAnnotations.TextAnnotation.class, "F.");
  token2.setTag("NNP");

  CoreLabel token3 = new CoreLabel();
  token3.set(CoreAnnotations.TextAnnotation.class, "Kennedy");
  token3.setTag("NNP");

  CoreLabel tokenHead = new CoreLabel();
  tokenHead.set(CoreAnnotations.TextAnnotation.class, "Kennedy");
  tokenHead.setTag("NNP");

  m1.headWord = tokenHead;
  m2.headWord = tokenHead;

  m1.nerString = "PERSON";
  m2.nerString = "PERSON";

  m1.originalSpan = Arrays.asList(token1, token2, token3);
  m2.originalSpan = Arrays.asList(token3);

  m1.headString = "kennedy";
  m2.headString = "kennedy";

  assertTrue(m1.headsAgree(m2));
}
@Test
public void testGetPatternHandlesNamedEntities() {
  Mention mention = new Mention();

  CoreLabel tok1 = new CoreLabel();
  tok1.set(CoreAnnotations.TextAnnotation.class, "Barack");
  tok1.set(CoreAnnotations.LemmaAnnotation.class, "Barack");
  tok1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

  CoreLabel tok2 = new CoreLabel();
  tok2.set(CoreAnnotations.TextAnnotation.class, "Obama");
  tok2.set(CoreAnnotations.LemmaAnnotation.class, "Obama");
  tok2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

  CoreLabel tok3 = new CoreLabel();
  tok3.set(CoreAnnotations.TextAnnotation.class, "visited");
  tok3.set(CoreAnnotations.LemmaAnnotation.class, "visit");
  tok3.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

  mention.headWord = tok2;
  mention.headIndexedWord = new IndexedWord(tok2);
  mention.enhancedDependency = new SemanticGraph();

  List<AbstractCoreLabel> tokens = new ArrayList<>();
  tokens.add(tok1);
  tokens.add(tok2);
  tokens.add(tok3);

  String pattern = mention.getPattern(tokens);
  assertTrue(pattern.contains("<PERSON>"));
  assertTrue(pattern.contains("visit"));
}
@Test
public void testIncludedReturnsFalseForNonNNP() {
  CoreLabel small = new CoreLabel();
  small.set(CoreAnnotations.TextAnnotation.class, "test");
  small.setTag("NN"); 

  CoreLabel has = new CoreLabel();
  has.set(CoreAnnotations.TextAnnotation.class, "test");

  List<CoreLabel> big = new ArrayList<>();
  big.add(has);

//  assertFalse(Mention.included(small, big));
}
@Test
public void testRemoveParenthesisNoMatchReturnsEmpty() {
  assertEquals("", Mention.removeParenthesis("NoParenthesisHere"));
}
@Test
public void testGetPreprocessedTermSkipsEmptyAndEscape() {
  Mention m = new Mention();
  m.originalSpan = new ArrayList<>();

  CoreLabel tok = new CoreLabel();
  tok.set(CoreAnnotations.TextAnnotation.class, "Price+Waterhouse");

  CoreLabel head = new CoreLabel();
  head.set(CoreAnnotations.TextAnnotation.class, "Waterhouse");
  m.originalSpan.add(tok);
  m.headWord = head;
  m.headIndex = 0;

  List<String> terms = m.preprocessSearchTerm();
  assertFalse(terms.contains(""));
  assertTrue(terms.get(0).contains("\\+"));
}
@Test
public void testGetLowercaseNormalizedSpanStringReturnsSameCached() {
  CoreLabel word = new CoreLabel();
  word.set(CoreAnnotations.TextAnnotation.class, "Banana");

  Mention m = new Mention();
  m.originalSpan = Collections.singletonList(word);
  String first = m.lowercaseNormalizedSpanString();
  String second = m.lowercaseNormalizedSpanString(); 

  assertEquals("banana", first);
  assertEquals(first, second);
}
@Test
public void testSetTypeFallbackToNominal() throws IOException, ClassNotFoundException {
  Mention mention = new Mention();

  CoreLabel tok = new CoreLabel();
  tok.set(CoreAnnotations.TextAnnotation.class, "tree");
  tok.setTag("NN");
  tok.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

  mention.headWord = tok;
  mention.originalSpan = Collections.singletonList(tok);
  mention.startIndex = 0;
  mention.endIndex = 1;

  mention.basicDependency = new SemanticGraph();
  mention.enhancedDependency = new SemanticGraph();

  Dictionaries dict = new Dictionaries();
//  mention.setType(dict);

  assertEquals(Dictionaries.MentionType.NOMINAL, mention.mentionType);
}
@Test
public void testSetTypeNamedEntityFallbackToProper() throws IOException, ClassNotFoundException {
  Mention mention = new Mention();

  CoreLabel tok = new CoreLabel();
  tok.set(CoreAnnotations.TextAnnotation.class, "Tesla");
  tok.setTag("NNP");
  tok.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");

  mention.headWord = tok;
  mention.originalSpan = Collections.singletonList(tok);
  mention.startIndex = 0;
  mention.endIndex = 1;
  mention.basicDependency = new SemanticGraph();
  mention.enhancedDependency = new SemanticGraph();

  Dictionaries dict = new Dictionaries();
//  mention.setType(dict);

  assertEquals(Dictionaries.MentionType.PROPER, mention.mentionType);
}
@Test
public void testSetDiscourseSubjectDetection() {
  Mention mention = new Mention();

  CoreLabel verb = new CoreLabel();
//  verb.setText("drives");
  verb.setTag("VBZ");
  IndexedWord verbWord = new IndexedWord(verb);
  verbWord.setIndex(2);

  CoreLabel pronoun = new CoreLabel();
//  pronoun.setText("He");
  pronoun.setTag("PRP");
  IndexedWord subj = new IndexedWord(pronoun);
  subj.setIndex(1);

  SemanticGraph graph = new SemanticGraph();
  graph.addVertex(verbWord);
  graph.addVertex(subj);
  graph.addEdge(verbWord, subj, GrammaticalRelation.valueOf("nsubj"), 1.0, false);

  mention.enhancedDependency = graph;
  mention.headIndexedWord = subj;

//  mention.setDiscourse();

  assertTrue(mention.isSubject);
  assertFalse(mention.isDirectObject);
  assertFalse(mention.isIndirectObject);
  assertFalse(mention.isPrepositionObject);
}
@Test
public void testSetDiscourseNmodAgentIgnoredForPrepositionObject() {
  Mention mention = new Mention();

  CoreLabel verb = new CoreLabel();
//  verb.setText("destroyed");
  verb.setTag("VBD");
  IndexedWord verbWord = new IndexedWord(verb);
  verbWord.setIndex(5);

  CoreLabel by = new CoreLabel();
//  by.setText("by");
  by.setTag("IN");
  IndexedWord prep = new IndexedWord(by);
  prep.setIndex(6);

  SemanticGraph graph = new SemanticGraph();
  graph.addVertex(verbWord);
  graph.addVertex(prep);
  graph.addEdge(verbWord, prep, GrammaticalRelation.valueOf("nmod:agent"), 1.0, false);

  mention.enhancedDependency = graph;
  mention.headIndexedWord = prep;

//  mention.setDiscourse();

  assertFalse(mention.isPrepositionObject);
}
@Test
public void testSetNumberFromNEROrgReturnsUnknown() throws IOException, ClassNotFoundException {
  Mention mention = new Mention();

  CoreLabel hw = new CoreLabel();
  hw.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
//  hw.setText("Apple");

  mention.headWord = hw;
  mention.nerString = "ORGANIZATION";
  mention.mentionType = Dictionaries.MentionType.PROPER;

  Dictionaries dict = new Dictionaries();

  mention.setNumber(dict);

  assertEquals(Dictionaries.Number.UNKNOWN, mention.number);
}
@Test
public void testGetModifiersIgnoresPossDet() throws IOException, ClassNotFoundException {
  Mention mention = new Mention();

  CoreLabel head = new CoreLabel();
//  head.setText("car");
  head.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

  CoreLabel poss = new CoreLabel();
//  poss.setText("his");
  poss.set(CoreAnnotations.LemmaAnnotation.class, "his");
  poss.set(CoreAnnotations.PartOfSpeechAnnotation.class, "PRP$");

  IndexedWord headIW = new IndexedWord(head);
  headIW.setIndex(2);

  IndexedWord possIW = new IndexedWord(poss);
  possIW.setIndex(1);

  SemanticGraph graph = new SemanticGraph();
  graph.addVertex(headIW);
  graph.addVertex(possIW);
  graph.addEdge(headIW, possIW, UniversalEnglishGrammaticalRelations.POSSESSION_MODIFIER, 1.0, false);

  mention.headWord = head;
  mention.headIndexedWord = headIW;
  mention.enhancedDependency = graph;

  Dictionaries dict = new Dictionaries();
  dict.determiners.add("his");

  int mod = mention.getModifiers(dict);
  assertEquals(0, mod);
}
@Test
public void testGetContextHelperReturnsNEChunks() {
  CoreLabel tok1 = new CoreLabel();
//  tok1.setText("Barack");
  tok1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

  CoreLabel tok2 = new CoreLabel();
//  tok2.setText("Obama");
  tok2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

  CoreLabel tok3 = new CoreLabel();
//  tok3.setText("visited");
  tok3.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

  List<AbstractCoreLabel> tokens = new ArrayList<>();
  tokens.add(tok1);
  tokens.add(tok2);
  tokens.add(tok3);

//  List<String> context = Mention.getContextHelper(tokens);

//  assertEquals(1, context.size());
//  assertEquals("Barack Obama", context.get(0));
}
@Test
public void testGetSingletonFeaturesPersonYouPlural() throws IOException, ClassNotFoundException {
  Mention m = new Mention();
  m.mentionType = Dictionaries.MentionType.PRONOMINAL;
  m.nerString = "O";
  m.animacy = Dictionaries.Animacy.ANIMATE;
  m.person = Dictionaries.Person.YOU;
  m.number = Dictionaries.Number.PLURAL;
  m.headString = "you";
//  m.spanToString = "You";
  m.sentenceWords = new ArrayList<>();
  m.enhancedDependency = new SemanticGraph();
  m.basicDependency = new SemanticGraph();

  CoreLabel hw = new CoreLabel();
  hw.set(CoreAnnotations.TextAnnotation.class, "you");
  m.headWord = hw;
  m.headIndexedWord = new IndexedWord(hw);
  m.headIndexedWord.setIndex(1);

  Dictionaries dict = new Dictionaries();

  List<String> features = m.getSingletonFeatures(dict);

  assertTrue(features.contains("2")); 
  assertTrue(features.contains("ANIMATE"));
  assertTrue(features.contains("PLURAL"));
}
@Test
public void testGetQuantificationPossThenDefinite() throws IOException, ClassNotFoundException {
  Mention mention = new Mention();

  CoreLabel car = new CoreLabel();
//  car.setText("car");
  car.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

  IndexedWord carIW = new IndexedWord(car);
  carIW.setIndex(2);

  CoreLabel his = new CoreLabel();
//  his.setText("his");
  his.set(CoreAnnotations.LemmaAnnotation.class, "his");

  IndexedWord hisIW = new IndexedWord(his);
  hisIW.setIndex(1);

  SemanticGraph graph = new SemanticGraph();
  graph.addVertex(carIW);
  graph.addVertex(hisIW);
  graph.addEdge(carIW, hisIW, UniversalEnglishGrammaticalRelations.POSSESSION_MODIFIER, 1.0, false);

  mention.headWord = car;
  mention.headIndexedWord = carIW;
  mention.nerString = "O";
  mention.enhancedDependency = graph;

  Dictionaries dict = new Dictionaries();

  String q = mention.getQuantification(dict);
  assertEquals("definite", q);
}
@Test
public void testHashCodeConsistencyForSameMention() {
  Mention m1 = new Mention();
  m1.startIndex = 3;
  m1.endIndex = 6;

  Mention m2 = new Mention();
  m2.startIndex = 3;
  m2.endIndex = 6;

  assertEquals(m1.hashCode(), m2.hashCode());
}
@Test
public void testSetGenderPronounFemaleMatch() throws IOException, ClassNotFoundException {
  Mention mention = new Mention();
  mention.headString = "she";
  mention.mentionType = Dictionaries.MentionType.PRONOMINAL;

  Dictionaries dict = new Dictionaries();
  dict.femalePronouns.add("she");

//  mention.setGender(dict, null);
  assertEquals(Dictionaries.Gender.FEMALE, mention.gender);
}
@Test
public void testSetGenderPronounMaleMatch() throws IOException, ClassNotFoundException {
  Mention mention = new Mention();
  mention.headString = "he";
  mention.mentionType = Dictionaries.MentionType.PRONOMINAL;

  Dictionaries dict = new Dictionaries();
  dict.malePronouns.add("he");

//  mention.setGender(dict, null);
  assertEquals(Dictionaries.Gender.MALE, mention.gender);
}
@Test
public void testSetGenderNERPersonWithMatchingNERWord() throws IOException, ClassNotFoundException {
  Mention mention = new Mention();
  mention.mentionType = Dictionaries.MentionType.PROPER;
  mention.nerString = "PERSON";

  CoreLabel t1 = new CoreLabel();
  t1.set(CoreAnnotations.TextAnnotation.class, "Sally");
  mention.originalSpan = new ArrayList<>();
  mention.originalSpan.add(t1);

  mention.headWord = t1;
  mention.headString = "sally";

  Dictionaries dict = new Dictionaries();
  dict.femaleWords.add("sally");

//  mention.setGender(dict, null);
  assertEquals(Dictionaries.Gender.FEMALE, mention.gender);
}
@Test
public void testSetGenderFallbackToNeutral() throws IOException, ClassNotFoundException {
  Mention mention = new Mention();
  mention.mentionType = Dictionaries.MentionType.NOMINAL;
  mention.nerString = "O";
  mention.headString = "object";
  mention.headWord = new CoreLabel();
  mention.headWord.set(CoreAnnotations.TextAnnotation.class, "object");

  Dictionaries dict = new Dictionaries();
  dict.neutralWords.add("object");

//  mention.setGender(dict, null);
//  assertEquals(Dictionaries.Gender.NEUTRAL, mention.gender);
}
@Test
public void testGetGenderWithPaddedInitials() throws IOException, ClassNotFoundException {
  Mention mention = new Mention();

  CoreLabel head = new CoreLabel();
  head.set(CoreAnnotations.TextAnnotation.class, "Martin");
  mention.headWord = head;
  mention.nerString = "PER";

  List<String> mStr = new ArrayList<>();
  mStr.add("dr.");
  mStr.add("martin");
  mStr.add("luther");
  mStr.add("king");

  List<String> partial = new ArrayList<>();
  partial.add("king");
  partial.add("!");

  Dictionaries dict = new Dictionaries();
  dict.genderNumber.put(partial, Dictionaries.Gender.MALE);

//  Gender g = mention.getGender(dict, mStr);
//  assertEquals(Dictionaries.Gender.MALE, g);
}
@Test
public void testRemovePhraseAfterHeadWithWHAfterHead() {
  Mention mention = new Mention();

  CoreLabel w1 = new CoreLabel();
  w1.set(CoreAnnotations.TextAnnotation.class, "The");
  w1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "DT");

  CoreLabel w2 = new CoreLabel();
  w2.set(CoreAnnotations.TextAnnotation.class, "person");
  w2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

  CoreLabel w3 = new CoreLabel();
  w3.set(CoreAnnotations.TextAnnotation.class, "who");
  w3.set(CoreAnnotations.PartOfSpeechAnnotation.class, "WP");

  CoreLabel w4 = new CoreLabel();
  w4.set(CoreAnnotations.TextAnnotation.class, "ran");
  w4.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBD");

  List<CoreLabel> span = new ArrayList<>();
  span.add(w1);
  span.add(w2);
  span.add(w3);
  span.add(w4);

  mention.originalSpan = span;
  mention.startIndex = 0;
  mention.endIndex = 4;
  mention.headIndex = 1;

  String result = mention.removePhraseAfterHead();
  assertEquals("The person", result);
}
@Test
public void testRemovePhraseAfterHeadNothingToRemove() {
  Mention mention = new Mention();

  CoreLabel w1 = new CoreLabel();
  w1.set(CoreAnnotations.TextAnnotation.class, "Only");
  w1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "RB");

  mention.originalSpan = Collections.singletonList(w1);
  mention.startIndex = 0;
  mention.endIndex = 1;
  mention.headIndex = 0;

  String result = mention.removePhraseAfterHead();
  assertEquals("Only", result);
}
@Test
public void testIsRoleAppositiveDemonymRejection() throws IOException, ClassNotFoundException {
  Mention m1 = new Mention();
  m1.originalSpan = Collections.singletonList(new CoreLabel());
  m1.originalSpan.get(0).set(CoreAnnotations.TextAnnotation.class, "French");
  m1.headWord = m1.originalSpan.get(0);
  m1.headString = "french";
  m1.mentionType = Dictionaries.MentionType.NOMINAL;
  m1.sentNum = 0;
  m1.nerString = "PER";
  m1.generic = false;
  m1.animacy = Dictionaries.Animacy.ANIMATE;
  m1.gender = Dictionaries.Gender.FEMALE;
  m1.number = Dictionaries.Number.SINGULAR;
  m1.startIndex = 0;
  m1.endIndex = 1;

  Mention m2 = new Mention();
  m2.originalSpan = Collections.singletonList(new CoreLabel());
  m2.originalSpan.get(0).set(CoreAnnotations.TextAnnotation.class, "President French");
  m2.spanToString(); 
  m2.headWord = m2.originalSpan.get(0);
  m2.headString = "president";
  m2.sentNum = 0;
  m2.nerString = "PER";
  m2.generic = false;
  m2.animacy = Dictionaries.Animacy.ANIMATE;
  m2.gender = Dictionaries.Gender.FEMALE;
  m2.number = Dictionaries.Number.SINGULAR;
  m2.startIndex = 0;
  m2.endIndex = 2;

  Dictionaries dict = new Dictionaries();
  dict.demonymSet.add("french");

  boolean result = m1.isRoleAppositive(m2, dict);
  assertFalse(result);
}
@Test
public void testSetPersonForIWe() throws IOException, ClassNotFoundException {
  Mention m = new Mention();
  m.mentionType = Dictionaries.MentionType.PRONOMINAL;

  m.headWord = new CoreLabel();
//  m.headWord.setText("I");
  CoreLabel c = new CoreLabel();
  c.set(CoreAnnotations.TextAnnotation.class, "I");
  m.originalSpan = Collections.singletonList(c);

  Dictionaries dict = new Dictionaries();
  dict.firstPersonPronouns.add("i");

//  m.spanString = "I";
  m.number = Dictionaries.Number.SINGULAR;

//  m.setPerson(dict);

//  assertEquals(Dictionaries.Person.I, m.person);
}
@Test
public void testSetPersonFallbackToUnknown() throws IOException, ClassNotFoundException {
  Mention m = new Mention();
  m.mentionType = Dictionaries.MentionType.PRONOMINAL;

  CoreLabel coreLabel = new CoreLabel();
  coreLabel.set(CoreAnnotations.TextAnnotation.class, "whodafuq");
  m.originalSpan = Collections.singletonList(coreLabel);
//  m.spanString = "whodafuq";
  m.headWord = coreLabel;
  m.headString = "whodafuq";
  m.number = Dictionaries.Number.UNKNOWN;
  m.gender = Dictionaries.Gender.UNKNOWN;
  m.animacy = Dictionaries.Animacy.UNKNOWN;

  Dictionaries dict = new Dictionaries();

//  m.setPerson(dict);
  assertEquals(Dictionaries.Person.UNKNOWN, m.person);
}
@Test
public void testIsCoordinatedWhenNoCCChildReturnsFalse() {
  Mention m = new Mention();

  CoreLabel head = new CoreLabel();
  head.set(CoreAnnotations.TextAnnotation.class, "dogs");

  IndexedWord iw = new IndexedWord(head);
  iw.setIndex(1);
  m.headIndexedWord = iw;

  SemanticGraph graph = new SemanticGraph();
  graph.addVertex(iw);
  m.enhancedDependency = graph;

  assertFalse(m.isCoordinated());
} 
}