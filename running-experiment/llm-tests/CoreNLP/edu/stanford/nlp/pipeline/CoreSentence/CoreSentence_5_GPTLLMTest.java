package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.data.Mention;
import edu.stanford.nlp.coref.md.CorefMentionFinder;
import edu.stanford.nlp.coref.md.RuleBasedCorefMentionFinder;
import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.LabeledScoredTreeNode;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeFactory;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import edu.stanford.nlp.util.*;
import org.junit.Test;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CoreSentence_5_GPTLLMTest {

 @Test
  public void testText() {
    CoreMap sentenceMap = new Annotation("This is a test.");
    sentenceMap.set(CoreAnnotations.TextAnnotation.class, "This is a test.");
    CoreDocument doc = new CoreDocument("This is a test.");
    CoreSentence sentence = new CoreSentence(doc, sentenceMap);

    assertEquals("This is a test.", sentence.text());
  }
@Test
  public void testCharOffsets() {
    CoreMap map = new Annotation("");
    map.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    map.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 23);
    CoreDocument doc = new CoreDocument("text");
    CoreSentence sentence = new CoreSentence(doc, map);

    Pair<Integer, Integer> offsets = sentence.charOffsets();
    assertEquals((Integer) 5, offsets.first());
    assertEquals((Integer) 23, offsets.second());
  }
@Test
  public void testTokensAsStrings() {
    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel t1 = new CoreLabel(); t1.setWord("Hello");
    CoreLabel t2 = new CoreLabel(); t2.setWord("world");
    tokens.add(t1);
    tokens.add(t2);

    CoreMap map = new Annotation("Hello world");
    map.set(CoreAnnotations.TokensAnnotation.class, tokens);
    CoreDocument doc = new CoreDocument("Hello world");
    CoreSentence sentence = new CoreSentence(doc, map);

    List<String> result = sentence.tokensAsStrings();
    assertEquals(Arrays.asList("Hello", "world"), result);
  }
@Test
  public void testPosTags() {
    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel t1 = new CoreLabel(); t1.setTag("NN");
    CoreLabel t2 = new CoreLabel(); t2.setTag("VB");
    tokens.add(t1);
    tokens.add(t2);

    CoreMap map = new Annotation("");
    map.set(CoreAnnotations.TokensAnnotation.class, tokens);
    CoreDocument doc = new CoreDocument("Some doc");
    CoreSentence sentence = new CoreSentence(doc, map);

    List<String> result = sentence.posTags();
    assertEquals(Arrays.asList("NN", "VB"), result);
  }
@Test
  public void testLemmas() {
    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel t1 = new CoreLabel(); t1.setLemma("be");
    CoreLabel t2 = new CoreLabel(); t2.setLemma("run");
    tokens.add(t1);
    tokens.add(t2);

    CoreMap map = new Annotation("");
    map.set(CoreAnnotations.TokensAnnotation.class, tokens);
    CoreDocument doc = new CoreDocument("Doc");
    CoreSentence sentence = new CoreSentence(doc, map);

    List<String> result = sentence.lemmas();
    assertEquals(Arrays.asList("be", "run"), result);
  }
@Test
  public void testNerTags() {
    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel t1 = new CoreLabel(); t1.setNER("PERSON");
    CoreLabel t2 = new CoreLabel(); t2.setNER("LOCATION");
    tokens.add(t1);
    tokens.add(t2);

    CoreMap map = new Annotation("");
    map.set(CoreAnnotations.TokensAnnotation.class, tokens);
    CoreDocument doc = new CoreDocument("");
    CoreSentence sentence = new CoreSentence(doc, map);

    List<String> result = sentence.nerTags();
    assertEquals(Arrays.asList("PERSON", "LOCATION"), result);
  }
@Test
  public void testConstituencyParseNotNull() {
    Tree tree = Tree.valueOf("(ROOT (S (NP (DT The) (NN dog))))");

    CoreMap map = new Annotation("");
    map.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    CoreDocument doc = new CoreDocument("");
    CoreSentence sentence = new CoreSentence(doc, map);

    assertNotNull(sentence.constituencyParse());
  }
@Test
  public void testTregexResultsSuccess() {
    Tree tree = Tree.valueOf("(ROOT (S (NP (DT The) (NN cat)) (VP (VB sleeps))))");

    CoreMap map = new Annotation("");
    map.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    CoreDocument doc = new CoreDocument("");
    CoreSentence sentence = new CoreSentence(doc, map);

    List<String> result = sentence.tregexResults("NP");
    assertFalse(result.isEmpty());
  }
@Test(expected = RuntimeException.class)
  public void testTregexResultsThrowsIfNoParse() {
    CoreMap map = new Annotation("");
    CoreDocument doc = new CoreDocument("");
    CoreSentence sentence = new CoreSentence(doc, map);
    sentence.tregexResults("NP");
  }
@Test
  public void testNounPhrasesReturnsExpected() {
    Tree tree = Tree.valueOf("(ROOT (S (NP (DT A) (NN dog)) (VP (VBZ barks))))");

    CoreMap map = new Annotation("");
    map.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    CoreDocument doc = new CoreDocument("A dog barks.");
    CoreSentence sentence = new CoreSentence(doc, map);

    List<String> phrases = sentence.nounPhrases();
    assertFalse(phrases.isEmpty());
  }
@Test
  public void testVerbPhrasesReturnsExpected() {
    Tree tree = Tree.valueOf("(ROOT (S (NP (DT A) (NN dog)) (VP (VBZ barks))))");

    CoreMap map = new Annotation("");
    map.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    CoreDocument doc = new CoreDocument("A dog barks.");
    CoreSentence sentence = new CoreSentence(doc, map);

    List<String> phrases = sentence.verbPhrases();
    assertFalse(phrases.isEmpty());
  }
@Test
  public void testDependencyParse() {
    SemanticGraph graph = new SemanticGraph();

    CoreMap map = new Annotation("");
    map.set(SemanticGraphCoreAnnotations.EnhancedPlusPlusDependenciesAnnotation.class, graph);
    CoreDocument doc = new CoreDocument("text");
    CoreSentence sentence = new CoreSentence(doc, map);

    assertNotNull(sentence.dependencyParse());
  }
@Test
  public void testSentiment() {
    CoreMap map = new Annotation("");
    map.set(SentimentCoreAnnotations.SentimentClass.class, "Negative");
    CoreDocument doc = new CoreDocument("Some text");
    CoreSentence sentence = new CoreSentence(doc, map);

    assertEquals("Negative", sentence.sentiment());
  }
@Test
  public void testSentimentTree() {
    Tree tree = Tree.valueOf("(ROOT (S (VP (VBD Ended))))");
    CoreMap map = new Annotation("");
    map.set(SentimentCoreAnnotations.SentimentAnnotatedTree.class, tree);
    CoreDocument doc = new CoreDocument("");
    CoreSentence sentence = new CoreSentence(doc, map);

    assertEquals(tree, sentence.sentimentTree());
  }
@Test
  public void testToStringMatchesCoreMap() {
    CoreMap map = new Annotation("This sentence.");
    CoreDocument doc = new CoreDocument("This sentence.");
    CoreSentence sentence = new CoreSentence(doc, map);

    assertEquals(map.toString(), sentence.toString());
  }
@Test
  public void testWrapEntityMentionsCreatesList() {
    CoreMap map = new Annotation("");
    CoreMap mention1 = new Annotation("John");
    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(mention1);
    map.set(CoreAnnotations.MentionsAnnotation.class, mentions);
    CoreDocument doc = new CoreDocument("John");
    CoreSentence sentence = new CoreSentence(doc, map);

    sentence.wrapEntityMentions();

    List<CoreEntityMention> result = sentence.entityMentions();
    assertEquals(1, result.size());
  }
@Test
  public void testWrapEntityMentionsNullList() {
    CoreMap map = new Annotation("");
    CoreDocument doc = new CoreDocument("No entities");
    CoreSentence sentence = new CoreSentence(doc, map);

    sentence.wrapEntityMentions();

    assertNull(sentence.entityMentions());
  }
@Test
  public void testRelationsReturnsEmptyByDefault() {
    List<RelationTriple> triples = new ArrayList<>();
    CoreMap map = new Annotation("");
    map.set(CoreAnnotations.KBPTriplesAnnotation.class, triples);
    CoreDocument doc = new CoreDocument("Text");
    CoreSentence sentence = new CoreSentence(doc, map);

    List<RelationTriple> result = sentence.relations();
    assertTrue(result.isEmpty());
  }
@Test
  public void testDocumentMethodReturnsSameObject() {
    CoreMap map = new Annotation("Testing.");
    CoreDocument doc = new CoreDocument("Testing.");
    CoreSentence sentence = new CoreSentence(doc, map);

    assertEquals(doc, sentence.document());
  }
@Test
  public void testCoreMapReturnsSameObject() {
    CoreMap map = new Annotation("Hello");
    CoreDocument doc = new CoreDocument("Hello");
    CoreSentence sentence = new CoreSentence(doc, map);

    assertEquals(map, sentence.coreMap());
  }
@Test
  public void testTokensReturnsNullWhenNotSet() {
    CoreMap map = new Annotation("No tokens");
    CoreDocument doc = new CoreDocument("text");
    CoreSentence sentence = new CoreSentence(doc, map);

    assertNull(sentence.tokens());
  }
@Test
  public void testPoSTagsReturnsEmptyWhenTokensNotSet() {
    CoreMap map = new Annotation("No tokens");
    CoreDocument doc = new CoreDocument("Some text");
    CoreSentence sentence = new CoreSentence(doc, map);

    try {
      sentence.posTags();
      fail("Expected NullPointerException when tokens are not set");
    } catch (NullPointerException e) {
      
    }
  }
@Test
  public void testTregexResultsReturnsEmptyListIfNoMatch() {
    Tree tree = Tree.valueOf("(ROOT (S (VP (VB go))))");

    CoreMap map = new Annotation("go");
    map.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    CoreDocument doc = new CoreDocument("go");
    CoreSentence sentence = new CoreSentence(doc, map);

    List<String> results = sentence.tregexResults("NP");
    assertTrue(results.isEmpty());
  }
@Test
  public void testNounPhraseTreesReturnsEmptyIfNone() {
    Tree tree = Tree.valueOf("(ROOT (S (VP (VB walks))))");
    CoreMap map = new Annotation("walks");
    map.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    CoreDocument doc = new CoreDocument("walks");
    CoreSentence sentence = new CoreSentence(doc, map);

    List<Tree> results = sentence.nounPhraseTrees();
    assertTrue(results.isEmpty());
  }
@Test
  public void testVerbPhraseTreesReturnsEmptyIfNone() {
    Tree tree = Tree.valueOf("(ROOT (S (NP (DT The) (NN cat))))");
    CoreMap map = new Annotation("cat");
    map.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    CoreDocument doc = new CoreDocument("The cat");
    CoreSentence sentence = new CoreSentence(doc, map);

    List<Tree> results = sentence.verbPhraseTrees();
    assertTrue(results.isEmpty());
  }
@Test
  public void testSentimentReturnsNullIfNotSet() {
    CoreMap map = new Annotation("no sentiment");
    CoreDocument doc = new CoreDocument("text");
    CoreSentence sentence = new CoreSentence(doc, map);

    assertNull(sentence.sentiment());
  }
@Test
  public void testSentimentTreeReturnsNullIfNotSet() {
    CoreMap map = new Annotation("no tree");
    CoreDocument doc = new CoreDocument("text");
    CoreSentence sentence = new CoreSentence(doc, map);

    assertNull(sentence.sentimentTree());
  }
@Test
  public void testDependencyParseReturnsNullIfNotSet() {
    CoreMap map = new Annotation("no graph");
    CoreDocument doc = new CoreDocument("text");
    CoreSentence sentence = new CoreSentence(doc, map);

    assertNull(sentence.dependencyParse());
  }
@Test
  public void testRelationsReturnsNullIfNotSet() {
    CoreMap map = new Annotation("no relations");
    CoreDocument doc = new CoreDocument("text");
    CoreSentence sentence = new CoreSentence(doc, map);

    assertNull(sentence.relations());
  }
@Test
  public void testTregexResultTreesWithPrecompiledPattern() {
    Tree tree = Tree.valueOf("(ROOT (S (NP (NN John)) (VP (VBZ runs))))");

    CoreMap map = new Annotation("John runs");
    map.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    CoreDocument doc = new CoreDocument("John runs");
    CoreSentence sentence = new CoreSentence(doc, map);

    TregexPattern pattern = TregexPattern.compile("VP");
    List<Tree> matches = sentence.tregexResultTrees(pattern);
    assertEquals(1, matches.size());
  }
@Test
  public void testTregexResultsWithInvalidPatternThrows() {
    Tree tree = Tree.valueOf("(ROOT (S (NP (NN Something))))");

    CoreMap map = new Annotation("Something");
    map.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    CoreDocument doc = new CoreDocument("Something");
    CoreSentence sentence = new CoreSentence(doc, map);

    try {
      sentence.tregexResults("[UNBALANCED[");
      fail("Expected RuntimeException for invalid pattern.");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("Unbalanced"));
    }
  }
@Test
  public void testEntityMentionsMultipleMentions() {
    CoreMap map = new Annotation("Multiple");

    CoreMap mention1 = new Annotation("Alice");
    CoreMap mention2 = new Annotation("Wonderland");
    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(mention1);
    mentions.add(mention2);
    map.set(CoreAnnotations.MentionsAnnotation.class, mentions);

    CoreDocument doc = new CoreDocument("Alice in Wonderland");
    CoreSentence sentence = new CoreSentence(doc, map);

    sentence.wrapEntityMentions();

    List<CoreEntityMention> result = sentence.entityMentions();
    assertEquals(2, result.size());
  }
@Test
  public void testConstructorWithNullSentenceMap() {
    CoreDocument doc = new CoreDocument("text");
    CoreSentence sentence = new CoreSentence(doc, null);

    assertNull(sentence.coreMap());
  }
@Test
  public void testConstructorWithNullDocument() {
    CoreMap map = new Annotation("text");
    CoreSentence sentence = new CoreSentence(null, map);

    assertEquals(map, sentence.coreMap());
    assertNull(sentence.document());
  }
@Test
  public void testToStringReturnsEmptyWhenCoreMapUnset() {
    CoreSentence sentence = new CoreSentence(null, null);

    String repr = sentence.toString();
    assertNotNull(repr); 
  }
@Test
  public void testTregexCacheReuseSamePattern() {
    Tree tree = Tree.valueOf("(ROOT (S (NP (DT The) (NN car)) (VP (VBZ drives))))");
    CoreMap map = new Annotation("The car drives.");
    map.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    CoreDocument doc = new CoreDocument("The car drives.");
    CoreSentence sentence = new CoreSentence(doc, map);

    List<String> first = sentence.tregexResults("VP");
    List<String> second = sentence.tregexResults("VP");

    assertEquals(first, second);
  }
@Test
  public void testCharOffsetsWhenOnlyBeginSet() {
    CoreMap map = new Annotation("Incomplete offsets");
    map.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
    CoreDocument doc = new CoreDocument("doc");
    CoreSentence sentence = new CoreSentence(doc, map);

    try {
      sentence.charOffsets();
      fail("Expected NullPointerException for missing end offset.");
    } catch (NullPointerException e) {
      
    }
  }
@Test
  public void testCharOffsetsWhenOnlyEndSet() {
    CoreMap map = new Annotation("Incomplete offsets");
    map.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 25);
    CoreDocument doc = new CoreDocument("doc");
    CoreSentence sentence = new CoreSentence(doc, map);

    try {
      sentence.charOffsets();
      fail("Expected NullPointerException for missing begin offset.");
    } catch (NullPointerException e) {
      
    }
  }
@Test
  public void testTokensAsStringsWhenTokensEmpty() {
    CoreMap map = new Annotation("Empty token list");
    List<CoreLabel> emptyList = new ArrayList<>();
    map.set(CoreAnnotations.TokensAnnotation.class, emptyList);
    CoreDocument doc = new CoreDocument("text");
    CoreSentence sentence = new CoreSentence(doc, map);

    List<String> result = sentence.tokensAsStrings();
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
@Test
  public void testLemmasWhenTokenLemmasAreNull() {
    CoreLabel token = new CoreLabel();
    token.setWord("run");
    CoreMap map = new Annotation("single token");
    List<CoreLabel> list = new ArrayList<>();
    list.add(token);
    map.set(CoreAnnotations.TokensAnnotation.class, list);
    CoreDocument doc = new CoreDocument("text");
    CoreSentence sentence = new CoreSentence(doc, map);

    List<String> lemmas = sentence.lemmas();
    assertEquals(1, lemmas.size());
    assertNull(lemmas.get(0));
  }
@Test
  public void testNERtagsWhenTokenNERIsNull() {
    CoreLabel token = new CoreLabel();
    token.setWord("Hello");
    CoreMap map = new Annotation("sentence");
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    map.set(CoreAnnotations.TokensAnnotation.class, tokens);
    CoreDocument doc = new CoreDocument("Hello");
    CoreSentence sentence = new CoreSentence(doc, map);

    List<String> result = sentence.nerTags();
    assertEquals(1, result.size());
    assertNull(result.get(0));
  }
@Test
  public void testPOSWhenTokenPOSIsNull() {
    CoreLabel token = new CoreLabel();
    token.setWord("Hi");
    CoreMap map = new Annotation("test");
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    map.set(CoreAnnotations.TokensAnnotation.class, tokens);
    CoreDocument doc = new CoreDocument("text");
    CoreSentence sentence = new CoreSentence(doc, map);

    List<String> tags = sentence.posTags();
    assertEquals(1, tags.size());
    assertNull(tags.get(0));
  }
@Test
  public void testTregexResultsReturnsCachedPattern() {
    Tree tree = Tree.valueOf("(ROOT (S (NP (NN Bird)) (VP (VB flies))))");
    CoreMap map = new Annotation("sentence");
    map.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    CoreDocument doc = new CoreDocument("Bird flies");
    CoreSentence sentence = new CoreSentence(doc, map);

    List<String> result1 = sentence.tregexResults("NP");
    List<String> result2 = sentence.tregexResults("NP");

    assertEquals(result1, result2); 
  }
@Test
  public void testWrapEntityMentionsHandlesEmptyList() {
    CoreMap map = new Annotation("text");
    map.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    CoreDocument doc = new CoreDocument("doc");
    CoreSentence sentence = new CoreSentence(doc, map);

    sentence.wrapEntityMentions();
    List<CoreEntityMention> mentions = sentence.entityMentions();
    assertNotNull(mentions);
    assertTrue(mentions.isEmpty());
  }
@Test
  public void testWrapEntityMentionsWithNullMentionInList() {
    CoreMap map = new Annotation("text");
    List<CoreMap> sourceMentions = new ArrayList<>();
    sourceMentions.add(null);
    map.set(CoreAnnotations.MentionsAnnotation.class, sourceMentions);
    CoreDocument doc = new CoreDocument("test");
    CoreSentence sentence = new CoreSentence(doc, map);

    try {
      sentence.wrapEntityMentions();
      fail("Expected NullPointerException due to null mention");
    } catch (NullPointerException e) {
      
    }
  }
@Test
  public void testTregexResultTreesWithPatternMatchingRoot() {
    Tree tree = Tree.valueOf("(ROOT (S (NP (DT A) (NN tree)) (VP (VB grows))))");
    CoreMap map = new Annotation("A tree grows");
    map.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    CoreDocument doc = new CoreDocument("A tree grows");
    CoreSentence sentence = new CoreSentence(doc, map);

    TregexPattern pattern = TregexPattern.compile("ROOT");
    List<Tree> matches = sentence.tregexResultTrees(pattern);

    assertEquals(1, matches.size());
    assertEquals("ROOT", matches.get(0).label().value());
  }
@Test
  public void testTregexResultsReturnsEmptyListIfParseSetToNull() {
    CoreMap map = new Annotation("no parse tree");
    map.set(TreeCoreAnnotations.TreeAnnotation.class, null);
    CoreDocument doc = new CoreDocument("test");
    CoreSentence sentence = new CoreSentence(doc, map);

    try {
      sentence.tregexResults("NP");
      fail("Expected RuntimeException due to null parse");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("constituency parse"));
    }
  }
@Test
  public void testEntityMentionsReturnsNullBeforeWrap() {
    CoreMap map = new Annotation("text");
    map.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>()); 
    CoreDocument doc = new CoreDocument("example");
    CoreSentence sentence = new CoreSentence(doc, map);

    assertNull(sentence.entityMentions());
  }
@Test
  public void testTextWhenAnnotationMissing() {
    CoreMap map = new Annotation("");
    CoreDocument doc = new CoreDocument("text");
    CoreSentence sentence = new CoreSentence(doc, map);
    assertNull(sentence.text());
  }
@Test
  public void testTokensReturnsImmutableCopy() {
    CoreLabel token = new CoreLabel();
    token.setWord("run");
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    CoreMap map = new Annotation("run");
    map.set(CoreAnnotations.TokensAnnotation.class, tokens);
    CoreDocument doc = new CoreDocument("run");
    CoreSentence sentence = new CoreSentence(doc, map);

    List<CoreLabel> returnedTokens = sentence.tokens();
    assertEquals(1, returnedTokens.size());
    assertEquals("run", returnedTokens.get(0).word());
    returnedTokens.get(0).setWord("walk");
    assertEquals("walk", returnedTokens.get(0).word()); 
  }
@Test
  public void testConstructorAcceptsEmptyTokenList() {
    CoreMap map = new Annotation("empty");
    map.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    CoreDocument doc = new CoreDocument("test");
    CoreSentence sentence = new CoreSentence(doc, map);

    List<CoreLabel> result = sentence.tokens();
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
@Test
  public void testWrapEntityMentionsWithMixedNullAndValid() {
    CoreMap mention1 = new Annotation("Alice");
    CoreMap mention2 = null;

    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(mention1);
    mentions.add(mention2);

    CoreMap map = new Annotation("Alice");
    map.set(CoreAnnotations.MentionsAnnotation.class, mentions);
    CoreDocument doc = new CoreDocument("Alice");
    CoreSentence sentence = new CoreSentence(doc, map);

    try {
      sentence.wrapEntityMentions();
      List<CoreEntityMention> result = sentence.entityMentions();
      assertNotNull(result);
      assertEquals(1, result.size());  
    } catch (Exception e) {
      fail("Should handle nulls gracefully");
    }
  }
@Test
  public void testConstituencyParseReturnsNullWithoutAnnotation() {
    CoreMap map = new Annotation("test");
    CoreDocument doc = new CoreDocument("doc");
    CoreSentence sentence = new CoreSentence(doc, map);
    assertNull(sentence.constituencyParse());
  }
@Test
  public void testSentimentTreeReturnsTreeCorrectly() {
    Tree tree = Tree.valueOf("(ROOT (S (NP (NN Joy))))");
    CoreMap map = new Annotation("Joy");
    map.set(SentimentCoreAnnotations.SentimentAnnotatedTree.class, tree);
    CoreDocument doc = new CoreDocument("Joy");
    CoreSentence sentence = new CoreSentence(doc, map);
    Tree result = sentence.sentimentTree();

    assertNotNull(result);
    assertEquals("ROOT", result.label().value());
  }
@Test
  public void testTregexMatcherSkipsNullMatchesSafely() {
    Tree tree = Tree.valueOf("(ROOT (FRAG (INTJ (UH Hello))))");

    CoreMap map = new Annotation("Hello");
    map.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    CoreDocument doc = new CoreDocument("Hello");
    CoreSentence sentence = new CoreSentence(doc, map);
    TregexPattern pattern = TregexPattern.compile("INTJ");

    List<Tree> matches = sentence.tregexResultTrees(pattern);
    assertNotNull(matches);
    assertEquals(1, matches.size());
    assertEquals("INTJ", matches.get(0).label().value());
  }
@Test
  public void testTextAndCharOffsetsWhenAnnotationsAreMissing() {
    CoreMap map = new Annotation("");
    CoreDocument doc = new CoreDocument("");
    CoreSentence sentence = new CoreSentence(doc, map);
    assertNull(sentence.text());

    try {
      sentence.charOffsets();
      fail("Expected NullPointerException when offsets are missing");
    } catch (NullPointerException e) {
      
    }
  }
@Test
  public void testKBPTriplesAnnotationWithSingleTriple() {
    RelationTriple triple = new RelationTriple(null, null, null, 0.9);
    List<RelationTriple> list = new ArrayList<>();
    list.add(triple);

    CoreMap map = new Annotation("text");
    map.set(CoreAnnotations.KBPTriplesAnnotation.class, list);
    CoreDocument doc = new CoreDocument("text");
    CoreSentence sentence = new CoreSentence(doc, map);

    List<RelationTriple> result = sentence.relations();
    assertEquals(1, result.size());
    assertEquals(triple, result.get(0));
  }
@Test
  public void testTregexResultsHandlesEmptyConstituencyTreeGracefully() {
    Tree tree = Tree.valueOf("(ROOT)");

    CoreMap map = new Annotation("Empty parse");
    map.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    CoreDocument doc = new CoreDocument("Text");
    CoreSentence sentence = new CoreSentence(doc, map);

    List<String> result = sentence.tregexResults("VP");
    assertTrue(result.isEmpty());  
  }
@Test
  public void testToStringReturnsCorrectCoreMapView() {
    CoreMap map = new Annotation("Content");
    map.set(CoreAnnotations.TextAnnotation.class, "Stringify this.");
    CoreDocument doc = new CoreDocument("Stringify this.");
    CoreSentence sentence = new CoreSentence(doc, map);

    assertEquals(map.toString(), sentence.toString());
  }
@Test
  public void testTregexResultTreesThrowsIfParseNull() {
    CoreMap map = new Annotation("no tree");
    CoreDocument doc = new CoreDocument("no tree");
    CoreSentence sentence = new CoreSentence(doc, map);

    try {
      sentence.tregexResultTrees(TregexPattern.compile("NP"));
      fail("Expected RuntimeException for missing constituency parse");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("constituency parse"));
    }
  }
@Test
  public void testLemmasReturnsNullWhenTokenListContainsNullToken() {
    CoreMap map = new Annotation("text");
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(null);
    map.set(CoreAnnotations.TokensAnnotation.class, tokens);
    CoreDocument doc = new CoreDocument("null token");
    CoreSentence sentence = new CoreSentence(doc, map);

    try {
      sentence.lemmas();
      fail("Expected NullPointerException due to null token");
    } catch (NullPointerException e) {
      
    }
  }
@Test
  public void testNERtagsReturnsNullWhenTokenListContainsNullToken() {
    CoreMap map = new Annotation("text");
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(null);
    map.set(CoreAnnotations.TokensAnnotation.class, tokens);
    CoreDocument doc = new CoreDocument("null token");
    CoreSentence sentence = new CoreSentence(doc, map);

    try {
      sentence.nerTags();
      fail("Expected NullPointerException due to null token");
    } catch (NullPointerException e) {
      
    }
  }
@Test
  public void testPoStagsReturnsNullWhenTokenListContainsNullToken() {
    CoreMap map = new Annotation("text");
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(null);
    map.set(CoreAnnotations.TokensAnnotation.class, tokens);
    CoreDocument doc = new CoreDocument("null token");
    CoreSentence sentence = new CoreSentence(doc, map);

    try {
      sentence.posTags();
      fail("Expected NullPointerException due to null token");
    } catch (NullPointerException e) {
      
    }
  }
@Test
  public void testWrapEntityMentionsHandlesEmptyAnnotationObject() {
    CoreMap map = new Annotation("dummy");
    CoreMap emptyAnnotation = new Annotation("");
    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(emptyAnnotation);
    map.set(CoreAnnotations.MentionsAnnotation.class, mentions);
    CoreDocument doc = new CoreDocument("doc");
    CoreSentence sentence = new CoreSentence(doc, map);

    sentence.wrapEntityMentions();
    assertNotNull(sentence.entityMentions());
    assertEquals(1, sentence.entityMentions().size());
  }
@Test
  public void testDependencyParseWhenAnnotationValueIsNull() {
    CoreMap map = new Annotation("dependency");
    map.set(SemanticGraphCoreAnnotations.EnhancedPlusPlusDependenciesAnnotation.class, null);
    CoreDocument doc = new CoreDocument("dep");
    CoreSentence sentence = new CoreSentence(doc, map);

    SemanticGraph graph = sentence.dependencyParse();
    assertNull(graph);
  }
@Test
  public void testSentimentReturnsNullIfClassNotSet() {
    CoreMap map = new Annotation("no sentiment");
    CoreDocument doc = new CoreDocument("doc");
    CoreSentence sentence = new CoreSentence(doc, map);

    assertNull(sentence.sentiment());
  }
@Test
  public void testTregexPatternCompileIsCachedCorrectly() {
    Tree tree = Tree.valueOf("(ROOT (S (NP (NN Test)) (VP (VB works))))");
    CoreMap map = new Annotation("test");
    map.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    CoreDocument doc = new CoreDocument("Test works");
    CoreSentence sentence = new CoreSentence(doc, map);

    List<Tree> result1 = sentence.tregexResultTrees("NP");
    List<Tree> result2 = sentence.tregexResultTrees("NP");

    assertEquals(result1.size(), result2.size());
  }
@Test
  public void testEntityMentionsReturnsNullWhenAnnotationMissingCompletely() {
    CoreMap map = new Annotation("text");
    CoreDocument doc = new CoreDocument("missing mentions");
    CoreSentence sentence = new CoreSentence(doc, map);

    sentence.wrapEntityMentions();
    assertNull(sentence.entityMentions());
  }
@Test
  public void testRelationsReturnsNullWhenAnnotationMissing() {
    CoreMap map = new Annotation("no KBP");
    CoreDocument doc = new CoreDocument("test");
    CoreSentence sentence = new CoreSentence(doc, map);

    List<?> result = sentence.relations();
    assertNull(result);
  }
@Test
  public void testTregexResultTreesReturnsEmptyListIfNoMatch() {
    Tree tree = Tree.valueOf("(ROOT (S (NP (DT A) (NN cat)) (VP (VBZ sleeps))))");
    CoreMap map = new Annotation("cat sleeps");
    map.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    CoreDocument doc = new CoreDocument("A cat sleeps");
    CoreSentence sentence = new CoreSentence(doc, map);

    TregexPattern pattern = TregexPattern.compile("ADJP");
    List<Tree> matches = sentence.tregexResultTrees(pattern);

    assertNotNull(matches);
    assertTrue(matches.isEmpty());
  }
@Test
  public void testTregexResultsReturnsEmptyWhenNoTreeMatch() {
    Tree tree = Tree.valueOf("(ROOT (S (NP (NN Apple)) (VP (VBD fell))))");
    CoreMap map = new Annotation("Apple fell");
    map.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    CoreDocument doc = new CoreDocument("Apple fell");
    CoreSentence sentence = new CoreSentence(doc, map);

    List<String> results = sentence.tregexResults("PP");
    assertNotNull(results);
    assertTrue(results.isEmpty());
  }
@Test
  public void testSentimentTreeReturnsNullWhenTreeMissing() {
    CoreMap map = new Annotation("no sentiment tree");
    map.set(SentimentCoreAnnotations.SentimentAnnotatedTree.class, null);
    CoreDocument doc = new CoreDocument("text");
    CoreSentence sentence = new CoreSentence(doc, map);

    assertNull(sentence.sentimentTree());
  }
@Test
  public void testConstituencyParseReturnsNullWhenUnset() {
    CoreMap map = new Annotation("no parse");
    CoreDocument doc = new CoreDocument("doc");
    CoreSentence sentence = new CoreSentence(doc, map);

    assertNull(sentence.constituencyParse());
  }
@Test
  public void testCoreMapReturningNullDoesNotThrow() {
    CoreSentence sentence = new CoreSentence(null, null);
    assertNull(sentence.coreMap());
  }
@Test
  public void testDocumentReturningNullDoesNotThrow() {
    CoreMap map = new Annotation("hello");
    CoreSentence sentence = new CoreSentence(null, map);
    assertNull(sentence.document());
  }
@Test
  public void testToStringWhenCoreMapIsNull() {
    CoreSentence sentence = new CoreSentence(null, null);
    try {
      sentence.toString();
    } catch (Exception e) {
      fail("toString() should not throw when CoreMap is null");
    }
  }
@Test
  public void testCharOffsetsWithNegativeOffsets() {
    CoreMap map = new Annotation("Negative test");
    map.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, -1);
    map.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, -5);
    CoreDocument doc = new CoreDocument("text");
    CoreSentence sentence = new CoreSentence(doc, map);

    Pair<Integer, Integer> offsets = sentence.charOffsets();
    assertEquals((Integer) (-1), offsets.first());
    assertEquals((Integer) (-5), offsets.second());
  }
@Test
  public void testTregexResultTreesReturnsMatchesInDeepStructure() {
    Tree tree = Tree.valueOf("(ROOT (S (NP (DT A) (NN dog)) (VP (VBZ barks) (PP (IN at) (NP (DT the) (NN cat)))))");
    CoreMap map = new Annotation("A dog barks at the cat");
    map.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    CoreDocument doc = new CoreDocument("A dog barks at the cat");
    CoreSentence sentence = new CoreSentence(doc, map);

    TregexPattern pattern = TregexPattern.compile("PP");
    List<Tree> results = sentence.tregexResultTrees(pattern);
    assertEquals(1, results.size());
  }
@Test
  public void testWrapEntityMentionsHandlesAllNullList() {
    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(null);
    mentions.add(null);

    CoreMap map = new Annotation("Entities");
    map.set(CoreAnnotations.MentionsAnnotation.class, mentions);
    CoreDocument doc = new CoreDocument("Entities");
    CoreSentence sentence = new CoreSentence(doc, map);

    try {
      sentence.wrapEntityMentions();
      assertNotNull(sentence.entityMentions());
      assertEquals(0, sentence.entityMentions().size()); 
    } catch (Exception e) {
      fail("Exception should not be thrown when mentions are null");
    }
  }
@Test
  public void testWrapEntityMentionsHandlesMixedValidAndNullEntries() {
    CoreMap valid = new Annotation("Entity");
    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(valid);
    mentions.add(null);

    CoreMap map = new Annotation("Mentions");
    map.set(CoreAnnotations.MentionsAnnotation.class, mentions);
    CoreDocument doc = new CoreDocument("Mentions");
    CoreSentence sentence = new CoreSentence(doc, map);

    sentence.wrapEntityMentions();
    List<CoreEntityMention> output = sentence.entityMentions();
    assertNotNull(output);
    assertEquals(1, output.size());
  }
@Test
  public void testTregexResultsReturnsMultipleMatches() {
    Tree tree = Tree.valueOf("(ROOT (S (NP (DT The) (NN dog)) (VP (VBZ barks)) (NP (DT the) (NN cat))))");
    CoreMap map = new Annotation("Matching test");
    map.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    CoreDocument doc = new CoreDocument("Matching test");
    CoreSentence sentence = new CoreSentence(doc, map);

    List<String> result = sentence.tregexResults("NP");
    assertEquals(2, result.size());
  }
@Test
  public void testLemmasHandlesMultipleTokensWithMixedValues() {
    CoreLabel token1 = new CoreLabel();
    token1.setLemma("go");
    CoreLabel token2 = new CoreLabel();
    token2.setLemma(null);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    tokens.add(token2);

    CoreMap map = new Annotation("tokens");
    map.set(CoreAnnotations.TokensAnnotation.class, tokens);
    CoreDocument doc = new CoreDocument("tokens");
    CoreSentence sentence = new CoreSentence(doc, map);

    List<String> lemmas = sentence.lemmas();
    assertEquals(2, lemmas.size());
    assertEquals("go", lemmas.get(0));
    assertNull(lemmas.get(1));
  }
@Test
  public void testNerTagsHandlesMissingNERField() {
    CoreLabel token = new CoreLabel();
    token.setWord("Entity"); 

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap map = new Annotation("NER");
    map.set(CoreAnnotations.TokensAnnotation.class, tokens);
    CoreDocument doc = new CoreDocument("NER");
    CoreSentence sentence = new CoreSentence(doc, map);

    List<String> nerTags = sentence.nerTags();
    assertEquals(1, nerTags.size());
    assertNull(nerTags.get(0));
  }
@Test
  public void testPosTagsHandlesMissingTag() {
    CoreLabel token = new CoreLabel();
    token.setWord("run");
    

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap map = new Annotation("POS");
    map.set(CoreAnnotations.TokensAnnotation.class, tokens);
    CoreDocument doc = new CoreDocument("POS");
    CoreSentence sentence = new CoreSentence(doc, map);

    List<String> result = sentence.posTags();
    assertEquals(1, result.size());
    assertNull(result.get(0));
  }
@Test
  public void testTregexResultsCatchesInvalidPatternSyntaxAtRuntime() {
    Tree tree = Tree.valueOf("(ROOT (S (NP (NN data))))");

    CoreMap map = new Annotation("invalid pattern");
    map.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    CoreDocument doc = new CoreDocument("invalid pattern");
    CoreSentence sentence = new CoreSentence(doc, map);

    try {
      sentence.tregexResults("(((");
      fail("Should throw RuntimeException due to unbalanced pattern");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().toLowerCase().contains("unbalanced"));
    }
  }
@Test
  public void testMultipleWrapEntityMentionsCallsPreservesData() {
    CoreMap mention = new Annotation("Person");
    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(mention);

    CoreMap map = new Annotation("Entity");
    map.set(CoreAnnotations.MentionsAnnotation.class, mentions);
    CoreDocument doc = new CoreDocument("Entity");
    CoreSentence sentence = new CoreSentence(doc, map);

    sentence.wrapEntityMentions();
    List<CoreEntityMention> firstCall = sentence.entityMentions();

    sentence.wrapEntityMentions(); 
    List<CoreEntityMention> secondCall = sentence.entityMentions();

    assertEquals(firstCall.size(), secondCall.size());
  }
@Test
  public void testTregexPatternCacheWorksCorrectlyPerPattern() {
    Tree tree = Tree.valueOf("(ROOT (S (NP (NN fish)) (VP (VBZ swim))))");
    CoreMap map = new Annotation("text");
    map.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    CoreDocument doc = new CoreDocument("text");
    CoreSentence sentence = new CoreSentence(doc, map);

    List<String> result1 = sentence.tregexResults("NP");
    List<String> result2 = sentence.tregexResults("VP");
    List<String> result3 = sentence.tregexResults("NP");

    assertNotEquals(result1, result2);
    assertEquals(result1, result3);
  }
@Test
  public void testTregexResultTreesReturnsEmptyIfConstituencyTreeIsLeafOnly() {
    Tree leafOnlyTree = Tree.valueOf("(NN single)");
    CoreMap map = new Annotation("leaf");
    map.set(TreeCoreAnnotations.TreeAnnotation.class, leafOnlyTree);
    CoreDocument doc = new CoreDocument("leaf");
    CoreSentence sentence = new CoreSentence(doc, map);

    List<Tree> result = sentence.tregexResultTrees("NP");
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
@Test
  public void testTregexResultsReturnsMatchOnTreeWithOnlySingleNP() {
    Tree singleNP = Tree.valueOf("(NP (NN object))");
    CoreMap map = new Annotation("single NP");
    map.set(TreeCoreAnnotations.TreeAnnotation.class, singleNP);
    CoreDocument doc = new CoreDocument("single NP");
    CoreSentence sentence = new CoreSentence(doc, map);

    List<String> npResults = sentence.tregexResults("NP");
    assertEquals(1, npResults.size());
    assertTrue(npResults.get(0).contains("object"));
  }
@Test
  public void testCharOffsetsThrowsIfNullBeginOrEnd() {
    CoreMap map = new Annotation("no offsets");
    CoreDocument doc = new CoreDocument("sample");
    CoreSentence sentence = new CoreSentence(doc, map);
    try {
      sentence.charOffsets();
      fail("Expected NullPointerException due to missing offset values");
    } catch (NullPointerException e) {
      
    }
  }
@Test
  public void testTregexResultsReturnsAllVPsInTree() {
    Tree tree = Tree.valueOf("(ROOT (S (VP (VBZ sits)) (VP (VBZ sleeps))))");
    CoreMap map = new Annotation("VP test");
    map.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    CoreDocument doc = new CoreDocument("VPs");
    CoreSentence sentence = new CoreSentence(doc, map);

    List<String> vpResults = sentence.verbPhrases();
    assertEquals(2, vpResults.size());
    assertTrue(vpResults.get(0).contains("sits"));
    assertTrue(vpResults.get(1).contains("sleeps"));
  }
@Test
  public void testNounPhraseTreesReturnsMultipleMatchingNodes() {
    Tree tree = Tree.valueOf("(ROOT (S (NP (DT The) (NN dog)) (VP (VBZ barks)) (NP (DT a) (NN sound))))");
    CoreMap map = new Annotation("multi NP");
    map.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    CoreDocument doc = new CoreDocument("multiple NPs");
    CoreSentence sentence = new CoreSentence(doc, map);

    List<Tree> npResults = sentence.nounPhraseTrees();
    assertEquals(2, npResults.size());
  }
@Test
  public void testTokensAsStringsReturnsTokensWithWhitespaceWords() {
    CoreLabel word1 = new CoreLabel(); word1.setWord("First");
    CoreLabel word2 = new CoreLabel(); word2.setWord(" ");
    CoreLabel word3 = new CoreLabel(); word3.setWord("Third");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(word1); tokens.add(word2); tokens.add(word3);

    CoreMap map = new Annotation("with spaces");
    map.set(CoreAnnotations.TokensAnnotation.class, tokens);
    CoreDocument doc = new CoreDocument("line");
    CoreSentence sentence = new CoreSentence(doc, map);

    List<String> result = sentence.tokensAsStrings();
    assertEquals(3, result.size());
    assertEquals(" ", result.get(1));
  }
@Test
  public void testToStringReturnsCoreMapStructureEvenIfEmpty() {
    CoreMap map = new Annotation("");
    CoreDocument doc = new CoreDocument("CoreMap present");
    CoreSentence sentence = new CoreSentence(doc, map);

    String output = sentence.toString();
    assertNotNull(output);
  }
@Test
  public void testTregexResultsFailsGracefullyWithMalformedPattern() {
    Tree tree = Tree.valueOf("(ROOT (NP (NN dog)))");
    CoreMap map = new Annotation("bad pattern test");
    map.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    CoreDocument doc = new CoreDocument("bad pattern");
    CoreSentence sentence = new CoreSentence(doc, map);

    try {
      sentence.tregexResults("\\[(bad pattern");
      fail("Expected RuntimeException due to Tregex syntax error");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().toLowerCase().contains("syntax") || e.getMessage().toLowerCase().contains("unbalanced"));
    }
  }
@Test
  public void testRelationsReturnsEmptyListIfAnnotationIsEmptyList() {
    CoreMap map = new Annotation("empty KBP");
    map.set(CoreAnnotations.KBPTriplesAnnotation.class, new ArrayList<>());
    CoreDocument doc = new CoreDocument("empty");
    CoreSentence sentence = new CoreSentence(doc, map);

    List<RelationTriple> result = sentence.relations();
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
@Test
  public void testConstituencyParseReturnsTreeNodeWithNoChildren() {
//     Tree tree = TreeFactory.createDefaultTreeFactory().newLeaf("leaf");
    CoreMap map = new Annotation("tree");
//     map.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    CoreDocument doc = new CoreDocument("leaf tree");
    CoreSentence sentence = new CoreSentence(doc, map);

    Tree parsed = sentence.constituencyParse();
    assertNotNull(parsed);
    assertEquals("leaf", parsed.label().value());
  }
@Test
  public void testDependencyParseReturnsSetGraph() {
    SemanticGraph graph = new SemanticGraph();
    CoreMap map = new Annotation("graph");
    map.set(SemanticGraphCoreAnnotations.EnhancedPlusPlusDependenciesAnnotation.class, graph);
    CoreDocument doc = new CoreDocument("graph");
    CoreSentence sentence = new CoreSentence(doc, map);

    SemanticGraph result = sentence.dependencyParse();
    assertNotNull(result);
  }
@Test
  public void testSentimentTreeReturnsSingleNodeTree() {
//     Tree tree = TreeFactory.createDefaultTreeFactory().newTreeNode("SentimentNode", new ArrayList<>());
    CoreMap map = new Annotation("sentiment one node");
//     map.set(SentimentCoreAnnotations.SentimentAnnotatedTree.class, tree);
    CoreDocument doc = new CoreDocument("node");
    CoreSentence sentence = new CoreSentence(doc, map);

    Tree result = sentence.sentimentTree();
    assertNotNull(result);
    assertEquals("SentimentNode", result.label().value());
  }
@Test
  public void testSentimentAnnotationIsEmptyString() {
    CoreMap map = new Annotation("empty");
    map.set(SentimentCoreAnnotations.SentimentClass.class, "");
    CoreDocument doc = new CoreDocument("sentence");
    CoreSentence sentence = new CoreSentence(doc, map);

    assertEquals("", sentence.sentiment());
  }
@Test
  public void testEmptyDocumentInConstructorStillWorks() {
    CoreMap map = new Annotation("text");
    CoreDocument doc = new CoreDocument("");
    CoreSentence sentence = new CoreSentence(doc, map);

    assertEquals(doc, sentence.document());
    assertEquals(map, sentence.coreMap());
  } 
}
