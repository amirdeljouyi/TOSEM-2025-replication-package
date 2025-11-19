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
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import edu.stanford.nlp.util.*;
import org.junit.Test;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CoreSentence_1_GPTLLMTest {

 @Test
  public void testTextReturnsCorrectString() {
    CoreDocument doc = new CoreDocument("Example.");
    CoreMap map = new Annotation("");
    map.set(CoreAnnotations.TextAnnotation.class, "Hello world.");
    CoreSentence sentence = new CoreSentence(doc, map);
    String text = sentence.text();
    assertEquals("Hello world.", text);
  }
@Test
  public void testCharOffsetsReturnsCorrectPair() {
    CoreDocument doc = new CoreDocument("Example.");
    CoreMap map = new Annotation("");
    map.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    map.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 13);
    CoreSentence sentence = new CoreSentence(doc, map);
    Pair<Integer, Integer> offsets = sentence.charOffsets();
    assertEquals(Integer.valueOf(5), offsets.first);
    assertEquals(Integer.valueOf(13), offsets.second);
  }
@Test
  public void testTokensReturnsCorrectList() {
    CoreDocument doc = new CoreDocument("Example.");
    CoreMap map = new Annotation("");
    CoreLabel token1 = new CoreLabel(); token1.setWord("Hello");
    CoreLabel token2 = new CoreLabel(); token2.setWord("world");
    List<CoreLabel> tokenList = Arrays.asList(token1, token2);
    map.set(CoreAnnotations.TokensAnnotation.class, tokenList);
    CoreSentence sentence = new CoreSentence(doc, map);
    List<CoreLabel> tokens = sentence.tokens();
    assertEquals(2, tokens.size());
    assertSame(token1, tokens.get(0));
    assertSame(token2, tokens.get(1));
  }
@Test
  public void testTokensAsStringsReturnsWords() {
    CoreDocument doc = new CoreDocument("Example.");
    CoreMap map = new Annotation("");
    CoreLabel token1 = new CoreLabel(); token1.setWord("Open");
    CoreLabel token2 = new CoreLabel(); token2.setWord("NLP");
    map.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2));
    CoreSentence sentence = new CoreSentence(doc, map);
    List<String> words = sentence.tokensAsStrings();
    assertEquals(2, words.size());
    assertEquals("Open", words.get(0));
    assertEquals("NLP", words.get(1));
  }
@Test
  public void testPosTagsReturnsTags() {
    CoreDocument doc = new CoreDocument("Example.");
    CoreMap map = new Annotation("");
    CoreLabel t1 = new CoreLabel(); t1.setTag("NN");
    CoreLabel t2 = new CoreLabel(); t2.setTag("VB");
    map.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(t1, t2));
    CoreSentence sentence = new CoreSentence(doc, map);
    List<String> tags = sentence.posTags();
    assertEquals("NN", tags.get(0));
    assertEquals("VB", tags.get(1));
  }
@Test
  public void testLemmasReturnsLemmas() {
    CoreDocument doc = new CoreDocument("Example.");
    CoreMap map = new Annotation("");
    CoreLabel token1 = new CoreLabel(); token1.setLemma("run");
    CoreLabel token2 = new CoreLabel(); token2.setLemma("jump");
    map.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2));
    CoreSentence sentence = new CoreSentence(doc, map);
    List<String> lemmas = sentence.lemmas();
    assertEquals("run", lemmas.get(0));
    assertEquals("jump", lemmas.get(1));
  }
@Test
  public void testNerTagsReturnsNERLabels() {
    CoreDocument doc = new CoreDocument("Example.");
    CoreMap map = new Annotation("");
    CoreLabel t1 = new CoreLabel(); t1.setNER("LOCATION");
    CoreLabel t2 = new CoreLabel(); t2.setNER("O");
    map.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(t1, t2));
    CoreSentence sentence = new CoreSentence(doc, map);
    List<String> nerTags = sentence.nerTags();
    assertEquals("LOCATION", nerTags.get(0));
    assertEquals("O", nerTags.get(1));
  }
@Test
  public void testConstituencyParseReturnsTree() {
    CoreDocument doc = new CoreDocument("Example.");
    CoreMap map = new Annotation("");
    Tree tree = Tree.valueOf("(ROOT (NP (DT The) (NN dog)))");
    map.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    CoreSentence sentence = new CoreSentence(doc, map);
    Tree result = sentence.constituencyParse();
    assertNotNull(result);
    assertEquals(tree.toString(), result.toString());
  }
@Test(expected = RuntimeException.class)
  public void testTregexThrowsWithoutParse() {
    CoreDocument doc = new CoreDocument("Example.");
    CoreMap map = new Annotation("");
    map.set(TreeCoreAnnotations.TreeAnnotation.class, null);
    CoreSentence sentence = new CoreSentence(doc, map);
    sentence.tregexResultTrees("NP");
  }
@Test
  public void testNounPhrasesReturnsText() {
    CoreDocument doc = new CoreDocument("Example.");
    CoreMap map = new Annotation("");
    Tree tree = Tree.valueOf("(ROOT (S (NP (DT A) (NN dog)) (VP (VBD barked))))");
    map.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    CoreSentence sentence = new CoreSentence(doc, map);
    List<String> nounPhrases = sentence.nounPhrases();
    assertEquals(1, nounPhrases.size());
    assertTrue(nounPhrases.get(0).contains("A dog"));
  }
@Test
  public void testVerbPhrasesReturnsText() {
    CoreDocument doc = new CoreDocument("Example.");
    CoreMap map = new Annotation("");
    Tree tree = Tree.valueOf("(ROOT (S (NP (DT A) (NN dog)) (VP (VBD barked))))");
    map.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    CoreSentence sentence = new CoreSentence(doc, map);
    List<String> verbPhrases = sentence.verbPhrases();
    assertEquals(1, verbPhrases.size());
    assertTrue(verbPhrases.get(0).contains("barked"));
  }
@Test
  public void testDependencyParseReturnsCorrectGraph() {
    CoreDocument doc = new CoreDocument("Example.");
    CoreMap map = new Annotation("");
    SemanticGraph graph = new SemanticGraph();
    map.set(SemanticGraphCoreAnnotations.EnhancedPlusPlusDependenciesAnnotation.class, graph);
    CoreSentence sentence = new CoreSentence(doc, map);
    SemanticGraph result = sentence.dependencyParse();
    assertSame(graph, result);
  }
@Test
  public void testSentimentReturnsCorrectValue() {
    CoreDocument doc = new CoreDocument("Example.");
    CoreMap map = new Annotation("");
    map.set(SentimentCoreAnnotations.SentimentClass.class, "Positive");
    CoreSentence sentence = new CoreSentence(doc, map);
    String sentiment = sentence.sentiment();
    assertEquals("Positive", sentiment);
  }
@Test
  public void testSentimentTreeReturnsCorrectTree() {
    CoreDocument doc = new CoreDocument("Example.");
    CoreMap map = new Annotation("");
    Tree sentimentTree = Tree.valueOf("(3 (2 Good) (4 job))");
    map.set(SentimentCoreAnnotations.SentimentAnnotatedTree.class, sentimentTree);
    CoreSentence sentence = new CoreSentence(doc, map);
    Tree result = sentence.sentimentTree();
    assertEquals(sentimentTree.toString(), result.toString());
  }
@Test
  public void testWrapEntityMentionsCreatesList() {
    CoreDocument doc = new CoreDocument("Example.");
    CoreMap mention1 = new Annotation("John");
    CoreMap mention2 = new Annotation("Seattle");
    List<CoreMap> mentionList = Arrays.asList(mention1, mention2);
    CoreMap sentenceMap = new Annotation("");
    sentenceMap.set(CoreAnnotations.MentionsAnnotation.class, mentionList);
    CoreSentence sentence = new CoreSentence(doc, sentenceMap);
    sentence.wrapEntityMentions();
    List<CoreEntityMention> entityMentions = sentence.entityMentions();
    assertEquals(2, entityMentions.size());
  }
@Test
  public void testRelationsReturnsList() {
    CoreDocument doc = new CoreDocument("Example.");
    CoreMap map = new Annotation("");
//     RelationTriple triple = new RelationTriple(null, null, null, 0.9, null, null);
//     List<RelationTriple> triples = Arrays.asList(triple);
//     map.set(CoreAnnotations.KBPTriplesAnnotation.class, triples);
    CoreSentence sentence = new CoreSentence(doc, map);
    List<RelationTriple> result = sentence.relations();
    assertEquals(1, result.size());
//     assertSame(triple, result.get(0));
  }
@Test
  public void testToStringDelegatesToCoreMap() {
    CoreDocument doc = new CoreDocument("Example.");
    CoreMap map = new Annotation("");
    map.set(CoreAnnotations.TextAnnotation.class, "ToString Test");
    CoreSentence sentence = new CoreSentence(doc, map);
    String result = sentence.toString();
    assertTrue(result.contains("ToString Test"));
  }
@Test
  public void testDocumentReturnsDocument() {
    CoreDocument doc = new CoreDocument("Hello");
    CoreMap map = new Annotation("");
    CoreSentence sentence = new CoreSentence(doc, map);
    assertSame(doc, sentence.document());
  }
@Test
  public void testCoreMapReturnsMap() {
    CoreDocument doc = new CoreDocument("Hello");
    CoreMap map = new Annotation("");
    CoreSentence sentence = new CoreSentence(doc, map);
    assertSame(map, sentence.coreMap());
  }
@Test
  public void testTextWithNullAnnotationReturnsNull() {
    CoreDocument doc = new CoreDocument("Test");
    CoreMap map = new Annotation("");
    CoreSentence sentence = new CoreSentence(doc, map);
    String result = sentence.text();
    assertNull(result);
  }
@Test
  public void testCharOffsetsWithMissingAnnotationsThrowsNPE() {
    CoreDocument doc = new CoreDocument("Test");
    CoreMap map = new Annotation("");
    CoreSentence sentence = new CoreSentence(doc, map);
    try {
      sentence.charOffsets();
      fail("Expected NullPointerException");
    } catch (NullPointerException e) {
      
    }
  }
@Test
  public void testTokensReturnsNullIfNotSet() {
    CoreDocument doc = new CoreDocument("Doc");
    CoreMap map = new Annotation("");
    CoreSentence sentence = new CoreSentence(doc, map);
    List<CoreLabel> tokens = sentence.tokens();
    assertNull(tokens);
  }
@Test
  public void testTokensAsStringsReturnsEmptyListWhenTokensIsNull() {
    CoreDocument doc = new CoreDocument("Doc");
    CoreMap map = new Annotation("");
    CoreSentence sentence = new CoreSentence(doc, map);
    List<String> tokens = sentence.tokensAsStrings();
    assertEquals(0, tokens.size());
  }
@Test
  public void testPosTagsReturnsEmptyListWhenTokensAreMissing() {
    CoreDocument doc = new CoreDocument("Doc");
    CoreMap map = new Annotation("");
    CoreSentence sentence = new CoreSentence(doc, map);
    List<String> posTags = sentence.posTags();
    assertEquals(0, posTags.size());
  }
@Test
  public void testLemmasReturnsEmptyListWhenTokensAreMissing() {
    CoreDocument doc = new CoreDocument("Doc");
    CoreMap map = new Annotation("");
    CoreSentence sentence = new CoreSentence(doc, map);
    List<String> lemmas = sentence.lemmas();
    assertEquals(0, lemmas.size());
  }
@Test
  public void testNerTagsReturnsEmptyListWhenTokensAreMissing() {
    CoreDocument doc = new CoreDocument("Doc");
    CoreMap map = new Annotation("");
    CoreSentence sentence = new CoreSentence(doc, map);
    List<String> nerTags = sentence.nerTags();
    assertEquals(0, nerTags.size());
  }
@Test
  public void testTregexResultsReturnsEmptyListIfNoMatch() {
    CoreDocument doc = new CoreDocument("Doc");
    CoreMap map = new Annotation("");
    Tree tree = Tree.valueOf("(ROOT (S (VP (VB Run))))");
    map.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    CoreSentence sentence = new CoreSentence(doc, map);
    List<String> result = sentence.tregexResults("NP");
    assertEquals(0, result.size());
  }
@Test
  public void testDependencyParseReturnsNullIfNotSet() {
    CoreDocument doc = new CoreDocument("Document");
    CoreMap map = new Annotation("");
    CoreSentence sentence = new CoreSentence(doc, map);
    SemanticGraph semGraph = sentence.dependencyParse();
    assertNull(semGraph);
  }
@Test
  public void testSentimentReturnsNullIfNotSet() {
    CoreDocument doc = new CoreDocument("Document");
    CoreMap map = new Annotation("");
    CoreSentence sentence = new CoreSentence(doc, map);
    String sentiment = sentence.sentiment();
    assertNull(sentiment);
  }
@Test
  public void testSentimentTreeReturnsNullIfNotSet() {
    CoreDocument doc = new CoreDocument("Document");
    CoreMap map = new Annotation("");
    CoreSentence sentence = new CoreSentence(doc, map);
    Tree result = sentence.sentimentTree();
    assertNull(result);
  }
@Test
  public void testWrapEntityMentionsDoesNothingIfAnnotationMissing() {
    CoreDocument doc = new CoreDocument("Doc");
    CoreMap map = new Annotation("");
    CoreSentence sentence = new CoreSentence(doc, map);
    sentence.wrapEntityMentions();
    List<CoreEntityMention> entities = sentence.entityMentions();
    assertNull(entities);
  }
@Test
  public void testEntityMentionsReturnsNullBeforeWrapCall() {
    CoreDocument doc = new CoreDocument("Doc");
    CoreMap map = new Annotation("");
    CoreSentence sentence = new CoreSentence(doc, map);
    List<CoreEntityMention> mentions = sentence.entityMentions();
    assertNull(mentions);
  }
@Test
  public void testRelationsReturnsNullWhenNotSet() {
    CoreDocument doc = new CoreDocument("Doc");
    CoreMap map = new Annotation("");
    CoreSentence sentence = new CoreSentence(doc, map);
    List<RelationTriple> rels = sentence.relations();
    assertNull(rels);
  }
@Test
  public void testNounPhraseTreesReturnsEmptyListIfNoNP() {
    CoreDocument doc = new CoreDocument("Sentence");
    CoreMap map = new Annotation("");
    Tree tree = Tree.valueOf("(ROOT (VP (VB Go)))");
    map.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    CoreSentence sentence = new CoreSentence(doc, map);
    List<Tree> result = sentence.nounPhraseTrees();
    assertTrue(result.isEmpty());
  }
@Test
  public void testVerbPhraseTreesReturnsEmptyListIfNoVP() {
    CoreDocument doc = new CoreDocument("Text");
    CoreMap map = new Annotation("");
    Tree tree = Tree.valueOf("(ROOT (NP (DT The) (NN apple)))");
    map.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    CoreSentence sentence = new CoreSentence(doc, map);
    List<Tree> result = sentence.verbPhraseTrees();
    assertTrue(result.isEmpty());
  }
@Test
  public void testTregexWithInvalidPatternThrowsRuntime() {
    CoreDocument doc = new CoreDocument("Text");
    CoreMap map = new Annotation("");
    Tree tree = Tree.valueOf("(ROOT (NP (DT A) (NN test)))");
    map.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    CoreSentence sentence = new CoreSentence(doc, map);
    try {
      sentence.tregexResultTrees("INVALID_PATTERN[");
      fail("Expected exception for invalid pattern");
    } catch (RuntimeException e) {
      
    }
  }
@Test
  public void testToStringWithEmptyCoreMapReturnsDefault() {
    CoreDocument doc = new CoreDocument("Doc");
    CoreMap map = new Annotation("");
    CoreSentence sentence = new CoreSentence(doc, map);
    String result = sentence.toString();
    assertNotNull(result);
  }
@Test
  public void testWrapEntityMentionsHandlesDuplicateMentions() {
    CoreDocument doc = new CoreDocument("Doc");
    CoreMap mention = new Annotation("New York");
    List<CoreMap> mentionList = Arrays.asList(mention, mention); 
    CoreMap sentenceMap = new Annotation("");
    sentenceMap.set(CoreAnnotations.MentionsAnnotation.class, mentionList);

    CoreSentence sentence = new CoreSentence(doc, sentenceMap);
    sentence.wrapEntityMentions();

    List<CoreEntityMention> mentions = sentence.entityMentions();
    assertEquals(2, mentions.size());
    assertNotSame(mentions.get(0), mentions.get(1));
  }
@Test
  public void testTokensAsStringsReturnsNullsSafely() {
    CoreDocument doc = new CoreDocument("Doc");
    CoreLabel token1 = new CoreLabel(); token1.setWord(null);
    CoreLabel token2 = new CoreLabel(); token2.setWord("text");
    List<CoreLabel> list = Arrays.asList(token1, token2);

    CoreMap sentenceMap = new Annotation("");
    sentenceMap.set(CoreAnnotations.TokensAnnotation.class, list);
    CoreSentence sentence = new CoreSentence(doc, sentenceMap);

    List<String> result = sentence.tokensAsStrings();
    assertEquals(2, result.size());
    assertNull(result.get(0));
    assertEquals("text", result.get(1));
  }
@Test
  public void testPosTagsReturnsNullValuesGracefully() {
    CoreDocument doc = new CoreDocument("Doc");
    CoreLabel token1 = new CoreLabel(); token1.setTag(null);
    CoreLabel token2 = new CoreLabel(); token2.setTag("NNS");
    List<CoreLabel> tokens = Arrays.asList(token1, token2);

    CoreMap map = new Annotation("");
    map.set(CoreAnnotations.TokensAnnotation.class, tokens);
    CoreSentence sentence = new CoreSentence(doc, map);

    List<String> pos = sentence.posTags();
    assertEquals(2, pos.size());
    assertNull(pos.get(0));
    assertEquals("NNS", pos.get(1));
  }
@Test
  public void testMultipleTregexPatternCallsUsesCache() {
    CoreDocument doc = new CoreDocument("Doc");
    CoreMap map = new Annotation("");
    Tree tree = Tree.valueOf("(ROOT (S (NP (DT The) (NN dog)) (VP (VBZ barks))))");
    map.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    CoreSentence sentence = new CoreSentence(doc, map);

    List<Tree> matches1 = sentence.tregexResultTrees("NP");
    List<Tree> matches2 = sentence.tregexResultTrees("NP");

    assertEquals(matches1.size(), matches2.size());
    assertEquals(matches1.get(0).toString(), matches2.get(0).toString());
  }
@Test
  public void testWrapEntityMentionsWithNullAnnotationEntry() {
    CoreDocument doc = new CoreDocument("Doc");
    List<CoreMap> mentionList = Arrays.asList(null, new Annotation("Name"));
    CoreMap map = new Annotation("");
    map.set(CoreAnnotations.MentionsAnnotation.class, mentionList);
    CoreSentence sentence = new CoreSentence(doc, map);
    try {
      sentence.wrapEntityMentions();
    } catch (Exception e) {
      fail("wrapEntityMentions should not fail on null entries");
    }
    List<CoreEntityMention> mentions = sentence.entityMentions();
    assertNotNull(mentions);
    assertEquals(1, mentions.size());
  }
@Test
  public void testSentimentTreeHandlesExplicitNull() {
    CoreDocument doc = new CoreDocument("Doc");
    CoreMap map = new Annotation("");
    map.set(SentimentCoreAnnotations.SentimentAnnotatedTree.class, null);
    CoreSentence sentence = new CoreSentence(doc, map);
    Tree result = sentence.sentimentTree();
    assertNull(result);
  }
@Test
  public void testCharOffsetsWithZeroLengthSpan() {
    CoreDocument doc = new CoreDocument("Doc");
    CoreMap map = new Annotation("");
    map.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
    map.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);

    CoreSentence sentence = new CoreSentence(doc, map);
    Pair<Integer, Integer> offsets = sentence.charOffsets();
    assertEquals(Integer.valueOf(10), offsets.first);
    assertEquals(Integer.valueOf(10), offsets.second);
  }
@Test
  public void testNullTextAnnotationReturnsNullFromToString() {
    CoreDocument doc = new CoreDocument("Doc");
    CoreMap map = new Annotation("");
    map.set(CoreAnnotations.TextAnnotation.class, null);
    CoreSentence sentence = new CoreSentence(doc, map);
    assertNotNull(sentence.toString());
  }
@Test
  public void testMultipleEntityMentionsDifferentContent() {
    CoreDocument doc = new CoreDocument("Doc");
    CoreMap mention1 = new Annotation("London");
    CoreMap mention2 = new Annotation("Paris");
    CoreMap mention3 = new Annotation("Berlin");

    CoreMap map = new Annotation("");
    map.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention1, mention2, mention3));

    CoreSentence sentence = new CoreSentence(doc, map);
    sentence.wrapEntityMentions();

    List<CoreEntityMention> ems = sentence.entityMentions();
    assertEquals(3, ems.size());
  }
@Test
  public void testWrapEntityMentionsWithEmptyList() {
    CoreDocument doc = new CoreDocument("Doc");
    CoreMap map = new Annotation("");
    map.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
    CoreSentence sentence = new CoreSentence(doc, map);
    sentence.wrapEntityMentions();
    List<CoreEntityMention> result = sentence.entityMentions();
    assertEquals(0, result.size());
  }
@Test
  public void testLemmasHandlesNullFieldsInTokens() {
    CoreDocument doc = new CoreDocument("Doc");
    CoreLabel t1 = new CoreLabel();
    CoreLabel t2 = new CoreLabel(); t2.setLemma("eat");
    List<CoreLabel> tokens = Arrays.asList(t1, t2);
    CoreMap map = new Annotation("");
    map.set(CoreAnnotations.TokensAnnotation.class, tokens);
    CoreSentence sentence = new CoreSentence(doc, map);
    List<String> lemmas = sentence.lemmas();
    assertEquals(2, lemmas.size());
    assertNull(lemmas.get(0));
    assertEquals("eat", lemmas.get(1));
  }
@Test
  public void testNerTagsHandlesNullNERInTokens() {
    CoreDocument doc = new CoreDocument("Doc");
    CoreLabel t1 = new CoreLabel(); t1.setNER(null);
    CoreLabel t2 = new CoreLabel(); t2.setNER("ORG");
    List<CoreLabel> tokens = Arrays.asList(t1, t2);
    CoreMap map = new Annotation("");
    map.set(CoreAnnotations.TokensAnnotation.class, tokens);
    CoreSentence sentence = new CoreSentence(doc, map);
    List<String> ner = sentence.nerTags();
    assertEquals(2, ner.size());
    assertNull(ner.get(0));
    assertEquals("ORG", ner.get(1));
  }
@Test
  public void testDependencyParseHandlesNullExplicitlySet() {
    CoreDocument doc = new CoreDocument("Doc");
    CoreMap map = new Annotation("");
    map.set(SemanticGraphCoreAnnotations.EnhancedPlusPlusDependenciesAnnotation.class, null);
    CoreSentence sentence = new CoreSentence(doc, map);
    SemanticGraph graph = sentence.dependencyParse();
    assertNull(graph);
  }
@Test
  public void testEmptyTreeProducesNoTregexResults() {
    CoreDocument doc = new CoreDocument("EmptyTree");
    CoreMap map = new Annotation("");
    Tree tree = Tree.valueOf("(ROOT)");
    map.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    CoreSentence sentence = new CoreSentence(doc, map);

    List<String> npResults = sentence.tregexResults("NP");
    assertNotNull(npResults);
    assertTrue(npResults.isEmpty());
  }
@Test
  public void testTregexMultipleMatchResults() {
    CoreDocument doc = new CoreDocument("Multiple");
    CoreMap map = new Annotation("");
    Tree tree = Tree.valueOf("(ROOT (S (NP (DT The) (NN cat)) (VP (VBD chased) (NP (DT a) (NN mouse)))))");
    map.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    CoreSentence sentence = new CoreSentence(doc, map);

    List<String> results = sentence.tregexResults("NP");
    assertEquals(2, results.size());
    assertTrue(results.get(0).contains("The"));
    assertTrue(results.get(1).contains("a"));
  }
@Test
  public void testDefaultToStringWithNoTextAnnotation() {
    CoreDocument doc = new CoreDocument("ToStringNull");
    CoreMap map = new Annotation("");
    CoreSentence sentence = new CoreSentence(doc, map);
    String result = sentence.toString();
    assertNotNull(result);
    assertTrue(result.contains("edu.stanford.nlp.util.Annotation"));
  }
@Test
  public void testWrapEntityMentionsTwicePreservesResult() {
    CoreDocument doc = new CoreDocument("RepeatWrap");
    CoreMap map = new Annotation("");
    CoreMap entity = new Annotation("Entity");
    map.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(entity));

    CoreSentence sentence = new CoreSentence(doc, map);
    sentence.wrapEntityMentions();
    List<CoreEntityMention> first = sentence.entityMentions();
    sentence.wrapEntityMentions(); 
    List<CoreEntityMention> second = sentence.entityMentions();

    assertEquals(1, first.size());
    assertEquals(1, second.size());
    assertSame(first, second);
  }
@Test
  public void testTregexResultsWithCachedAndNonCachedPatterns() {
    CoreDocument doc = new CoreDocument("Cached");
    CoreMap map = new Annotation("");
    Tree tree = Tree.valueOf("(ROOT (S (NP (NNP John)) (VP (VBD left))))");
    map.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    CoreSentence sentence = new CoreSentence(doc, map);

    List<String> withStringPattern = sentence.tregexResults("NP");
    List<String> withCompiledPattern = sentence.tregexResults(edu.stanford.nlp.trees.tregex.TregexPattern.compile("NP"));

    assertEquals(1, withStringPattern.size());
    assertEquals(1, withCompiledPattern.size());
  }
@Test
  public void testAccessingAllMethodsOnEmptyAnnotation() {
    CoreDocument doc = new CoreDocument("Minimal");
    CoreMap map = new Annotation("");
    CoreSentence sentence = new CoreSentence(doc, map);

    assertNull(sentence.text());
    assertNull(sentence.tokens());
    assertEquals(0, sentence.tokensAsStrings().size());
    assertEquals(0, sentence.posTags().size());
    assertEquals(0, sentence.lemmas().size());
    assertEquals(0, sentence.nerTags().size());
    assertNull(sentence.sentiment());
    assertNull(sentence.sentimentTree());
    assertNull(sentence.dependencyParse());
    assertNull(sentence.entityMentions());
    assertNull(sentence.relations());
  }
@Test
  public void testDependencyParseMixedWithOtherAnnotationTypes() {
    CoreDocument doc = new CoreDocument("Mixed");
    CoreMap map = new Annotation("");
    Tree tree = Tree.valueOf("(ROOT (S (VP (VB Walk))))");
    map.set(SemanticGraphCoreAnnotations.EnhancedPlusPlusDependenciesAnnotation.class, null);
    map.set(TreeCoreAnnotations.TreeAnnotation.class, tree);

    CoreSentence sentence = new CoreSentence(doc, map);
    SemanticGraph dep = sentence.dependencyParse();
    Tree parsedTree = sentence.constituencyParse();

    assertNull(dep);
    assertNotNull(parsedTree);
  }
@Test
  public void testCharOffsetsWithNegativeOffsets() {
    CoreDocument doc = new CoreDocument("NegativeOffsets");
    CoreMap map = new Annotation("");
    map.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, -1);
    map.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, -1);
    CoreSentence sentence = new CoreSentence(doc, map);
    Pair<Integer, Integer> offsets = sentence.charOffsets();
    assertEquals(Integer.valueOf(-1), offsets.first);
    assertEquals(Integer.valueOf(-1), offsets.second);
  }
@Test
  public void testRelationListEmptyButNotNull() {
    CoreDocument doc = new CoreDocument("KBP");
    CoreMap map = new Annotation("");
    map.set(CoreAnnotations.KBPTriplesAnnotation.class, new ArrayList<RelationTriple>());
    CoreSentence sentence = new CoreSentence(doc, map);
    List<RelationTriple> rels = sentence.relations();
    assertNotNull(rels);
    assertTrue(rels.isEmpty());
  }
@Test
  public void testSettingMultipleTokenTypesWithNulls() {
    CoreDocument doc = new CoreDocument("Tokens");
    CoreLabel token1 = new CoreLabel(); token1.setTag(null); token1.setNER(null); token1.setLemma(null);
    CoreLabel token2 = new CoreLabel(); token2.setTag("VB"); token2.setNER("O"); token2.setLemma("play");
    CoreMap map = new Annotation("");
    map.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2));
    CoreSentence sentence = new CoreSentence(doc, map);

    List<String> tags = sentence.posTags();
    List<String> ner = sentence.nerTags();
    List<String> lemmas = sentence.lemmas();

    assertEquals(2, tags.size());
    assertNull(tags.get(0));
    assertEquals("VB", tags.get(1));

    assertEquals(2, ner.size());
    assertNull(ner.get(0));
    assertEquals("O", ner.get(1));

    assertEquals(2, lemmas.size());
    assertNull(lemmas.get(0));
    assertEquals("play", lemmas.get(1));
  }
@Test
  public void testTextAnnotationPresentButEmpty() {
    CoreDocument doc = new CoreDocument("Dummy");
    CoreMap map = new Annotation("");
    map.set(CoreAnnotations.TextAnnotation.class, "");
    CoreSentence sentence = new CoreSentence(doc, map);
    assertEquals("", sentence.text());
  }
@Test
  public void testTokensAsStringsWithTokensThatHaveEmptyWords() {
    CoreDocument doc = new CoreDocument("EmptyWords");
    CoreLabel t1 = new CoreLabel(); t1.setWord("");
    CoreLabel t2 = new CoreLabel(); t2.setWord("");
    CoreMap map = new Annotation("");
    map.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(t1, t2));
    CoreSentence sentence = new CoreSentence(doc, map);
    List<String> result = sentence.tokensAsStrings();
    assertEquals(2, result.size());
    assertEquals("", result.get(0));
    assertEquals("", result.get(1));
  }
@Test
  public void testLemmasWithAllNullLemmas() {
    CoreDocument doc = new CoreDocument("NullLemmas");
    CoreLabel a = new CoreLabel(); a.setLemma(null);
    CoreLabel b = new CoreLabel(); b.setLemma(null);
    CoreMap map = new Annotation("");
    map.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(a, b));
    CoreSentence sentence = new CoreSentence(doc, map);
    List<String> lemmas = sentence.lemmas();
    assertEquals(2, lemmas.size());
    assertNull(lemmas.get(0));
    assertNull(lemmas.get(1));
  }
@Test
  public void testRelationTriplesExplicitSetToNullListValue() {
    CoreDocument doc = new CoreDocument("RelationTest");
    CoreMap map = new Annotation("");
    map.set(CoreAnnotations.KBPTriplesAnnotation.class, null);
    CoreSentence sentence = new CoreSentence(doc, map);
    List<RelationTriple> triples = sentence.relations();
    assertNull(triples);
  }
@Test
  public void testWrapEntityMentionsHandlesEmptyCoreMapElement() {
    CoreDocument doc = new CoreDocument("Mentions");
    CoreMap emptyMention = new Annotation("");
    List<CoreMap> mentions = Arrays.asList(emptyMention);
    CoreMap map = new Annotation("");
    map.set(CoreAnnotations.MentionsAnnotation.class, mentions);
    CoreSentence sentence = new CoreSentence(doc, map);
    sentence.wrapEntityMentions();
    List<CoreEntityMention> wrapped = sentence.entityMentions();
    assertEquals(1, wrapped.size());
    assertNotNull(wrapped.get(0));
  }
@Test
  public void testTregexResultsOnTreeWithNoMatch() {
    CoreDocument doc = new CoreDocument("NoMatch");
    Tree tree = Tree.valueOf("(ROOT (VP (VBD ran)))");
    CoreMap map = new Annotation("");
    map.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    CoreSentence sentence = new CoreSentence(doc, map);
    List<String> nounPhrases = sentence.tregexResults("NP");
    assertTrue(nounPhrases.isEmpty());
  }
@Test
  public void testTregexResultTreesWithPatternReusedStringKey() {
    CoreDocument doc = new CoreDocument("CacheTest");
    Tree tree = Tree.valueOf("(ROOT (S (NP (NN Cat)) (VP (VBD slept))))");
    CoreMap map = new Annotation("");
    map.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    CoreSentence sentence = new CoreSentence(doc, map);

    List<Tree> trees1 = sentence.tregexResultTrees("NP");
    List<Tree> trees2 = sentence.tregexResultTrees("NP");
    assertEquals(trees1.size(), trees2.size());
  }
@Test
  public void testDependencyParseReturnsGraphWithNoEdges() {
    CoreDocument doc = new CoreDocument("NoEdges");
    SemanticGraph graph = new SemanticGraph();
    CoreMap map = new Annotation("");
    map.set(SemanticGraphCoreAnnotations.EnhancedPlusPlusDependenciesAnnotation.class, graph);
    CoreSentence sentence = new CoreSentence(doc, map);
    SemanticGraph result = sentence.dependencyParse();
    assertTrue(result.isEmpty());
  }
@Test
  public void testSentimentAnnotationExplicitlySetToEmptyString() {
    CoreDocument doc = new CoreDocument("Sentiment");
    CoreMap map = new Annotation("");
    map.set(SentimentCoreAnnotations.SentimentClass.class, "");
    CoreSentence sentence = new CoreSentence(doc, map);
    assertEquals("", sentence.sentiment());
  }
@Test
  public void testConstParseNullReturnsExceptionForTregex() {
    CoreDocument doc = new CoreDocument("NullParse");
    CoreMap map = new Annotation("");
    map.set(TreeCoreAnnotations.TreeAnnotation.class, null);
    CoreSentence sentence = new CoreSentence(doc, map);
    try {
      sentence.nounPhraseTrees();
      fail("Expected RuntimeException due to missing parse");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("To use this method you must annotate"));
    }
  }
@Test
  public void testToStringPrintsCoreMapContentsEvenWithMultipleKeys() {
    CoreDocument doc = new CoreDocument("ToStringCheck");
    CoreMap map = new Annotation("");
    map.set(CoreAnnotations.TextAnnotation.class, "Sample sentence.");
    map.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    map.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 17);
    CoreSentence sentence = new CoreSentence(doc, map);
    String toStr = sentence.toString();
    assertTrue(toStr.contains("Sample sentence."));
  }
@Test
  public void testConstituencyTreeWithoutNPOrVPReturnsEmptyResults() {
    CoreDocument doc = new CoreDocument("EmptyNPVP");
    Tree tree = Tree.valueOf("(ROOT (S (ADJP (JJ silent))))");
    CoreMap map = new Annotation("");
    map.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    CoreSentence sentence = new CoreSentence(doc, map);

    List<Tree> nounTrees = sentence.nounPhraseTrees();
    List<Tree> verbTrees = sentence.verbPhraseTrees();

    assertTrue(nounTrees.isEmpty());
    assertTrue(verbTrees.isEmpty());
  }
@Test
  public void testTregexResultsCalledTwiceWithSameInstance() {
    CoreDocument doc = new CoreDocument("RepeatCall");
    Tree tree = Tree.valueOf("(ROOT (S (NP (DT The) (NN dog)) (VP (VBZ barks))))");
    CoreMap map = new Annotation("");
    map.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    CoreSentence sentence = new CoreSentence(doc, map);

    List<String> firstMatches = sentence.tregexResults("NP");
    List<String> secondMatches = sentence.tregexResults("NP");

    assertEquals(firstMatches.size(), secondMatches.size());
    assertEquals(firstMatches.get(0), secondMatches.get(0));
  }
@Test
  public void testNamedEntityRecognitionTagNullFallback() {
    CoreDocument doc = new CoreDocument("NERNull");
    CoreLabel token = new CoreLabel();
    token.setNER(null);
    CoreMap map = new Annotation("");
    map.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    CoreSentence sentence = new CoreSentence(doc, map);
    List<String> ner = sentence.nerTags();
    assertEquals(1, ner.size());
    assertNull(ner.get(0));
  }
@Test
  public void testSentimentTreeEmptyLabelNodesHandled() {
    CoreDocument doc = new CoreDocument("SentimentTree");
    Tree sentiment = Tree.valueOf("(3 )");
    CoreMap map = new Annotation("");
    map.set(SentimentCoreAnnotations.SentimentAnnotatedTree.class, sentiment);
    CoreSentence sentence = new CoreSentence(doc, map);
    Tree result = sentence.sentimentTree();
    assertEquals(sentiment.toString(), result.toString());
  }
@Test
  public void testEntityMentionsAnnotationSetButEmptyList() {
    CoreDocument doc = new CoreDocument("EmptyMentions");
    CoreMap map = new Annotation("");
    map.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
    CoreSentence sentence = new CoreSentence(doc, map);
    sentence.wrapEntityMentions();
    List<CoreEntityMention> mentions = sentence.entityMentions();
    assertNotNull(mentions);
    assertTrue(mentions.isEmpty());
  }
@Test
  public void testCharOffsetsOnlyBeginSetThrowsNullPointerException() {
    CoreDocument doc = new CoreDocument("Offsets");
    CoreMap map = new Annotation("");
    map.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    CoreSentence sentence = new CoreSentence(doc, map);
    try {
      sentence.charOffsets();
      fail("Expected NullPointerException due to incomplete char offsets");
    } catch (NullPointerException e) {
      
    }
  }
@Test
  public void testToStringOnEmptyAnnotationReturnsNonNull() {
    CoreDocument doc = new CoreDocument("ToStringSafe");
    CoreMap map = new Annotation("");
    CoreSentence sentence = new CoreSentence(doc, map);
    String representation = sentence.toString();
    assertNotNull(representation);
    assertTrue(representation.contains("edu.stanford.nlp.util.Annotation"));
  }
@Test
  public void testTokensWithMixedValidAndNullFields() {
    CoreDocument doc = new CoreDocument("MixedTokens");
    CoreLabel t1 = new CoreLabel(); t1.setWord("walk"); t1.setTag(null); t1.setLemma("walk");
    CoreLabel t2 = new CoreLabel(); t2.setWord(null); t2.setTag("VB"); t2.setLemma(null);

    CoreMap map = new Annotation("");
    map.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(t1, t2));
    CoreSentence sentence = new CoreSentence(doc, map);

    List<String> words = sentence.tokensAsStrings();
    List<String> tags = sentence.posTags();
    List<String> lemmas = sentence.lemmas();

    assertEquals("walk", words.get(0));
    assertNull(words.get(1));
    assertNull(tags.get(0));
    assertEquals("VB", tags.get(1));
    assertEquals("walk", lemmas.get(0));
    assertNull(lemmas.get(1));
  }
@Test
  public void testRelationTriplesExplicitlySetToEmptyList() {
    CoreDocument doc = new CoreDocument("RelationEmpty");
    CoreMap map = new Annotation("");
    map.set(CoreAnnotations.KBPTriplesAnnotation.class, new ArrayList<RelationTriple>());
    CoreSentence sentence = new CoreSentence(doc, map);
    List<RelationTriple> result = sentence.relations();
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
@Test
  public void testDependencyParseSetToExplicitNull() {
    CoreDocument doc = new CoreDocument("DepNull");
    CoreMap map = new Annotation("");
    map.set(SemanticGraphCoreAnnotations.EnhancedPlusPlusDependenciesAnnotation.class, null);
    CoreSentence sentence = new CoreSentence(doc, map);
    assertNull(sentence.dependencyParse());
  }
@Test
  public void testTokensListProvidedButContainsNullObject() {
    CoreDocument doc = new CoreDocument("NullToken");
    List<CoreLabel> tokens = Arrays.asList(null, new CoreLabel());
    CoreMap map = new Annotation("");
    map.set(CoreAnnotations.TokensAnnotation.class, tokens);
    CoreSentence sentence = new CoreSentence(doc, map);

    try {
      sentence.posTags();
    } catch (NullPointerException e) {
      
      assertTrue(true);
    }
  }
@Test
  public void testTregexResultsFromCustomPatternObject() {
    CoreDocument doc = new CoreDocument("CustomTregex");
    Tree tree = Tree.valueOf("(ROOT (S (VP (VB Go))))");
    CoreMap map = new Annotation("");
    map.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    CoreSentence sentence = new CoreSentence(doc, map);

    TregexPattern pattern = TregexPattern.compile("VP");
    List<String> result = sentence.tregexResults(pattern);

    assertEquals(1, result.size());
    assertEquals("Go", result.get(0));
  }
@Test
  public void testEmptyTreeWithValidStructureReturnsNoTregex() {
    CoreDocument doc = new CoreDocument("EmptyTree");
    Tree tree = Tree.valueOf("(ROOT (S))");
    CoreMap map = new Annotation("");
    map.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    CoreSentence sentence = new CoreSentence(doc, map);

    List<String> matches = sentence.tregexResults("NP");
    assertNotNull(matches);
    assertTrue(matches.isEmpty());
  }
@Test
  public void testTregexPatternWithInvalidSyntaxThrowsRuntime() {
    CoreDocument doc = new CoreDocument("InvalidPattern");
    Tree tree = Tree.valueOf("(ROOT (S (NP (DT The) (NN cat))))");
    CoreMap map = new Annotation("");
    map.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    CoreSentence sentence = new CoreSentence(doc, map);

    try {
      sentence.tregexResultTrees("[");
      fail("Expected RuntimeException due to invalid pattern.");
    } catch (RuntimeException e) {
      
      assertTrue(e.getMessage().contains("Exception"));
    }
  }
@Test
  public void testOpaqueAnnotationSetShouldNotBreakToString() {
    CoreDocument doc = new CoreDocument("OpaqueToString");
    CoreMap map = new Annotation("");
    map.set(CoreAnnotations.TextAnnotation.class, "SomeText");
//     map.set(Object.class, "Irrelevant");
    CoreSentence sentence = new CoreSentence(doc, map);
    String output = sentence.toString();
    assertTrue(output.contains("SomeText"));
  }
@Test
  public void testTokensWithNullWordHandledSafelyInTokensAsStrings() {
    CoreDocument doc = new CoreDocument("NullWords");
    CoreLabel t1 = new CoreLabel(); t1.setWord(null);
    CoreLabel t2 = new CoreLabel(); t2.setWord("active");
    CoreMap map = new Annotation("");
    map.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(t1, t2));
    CoreSentence sentence = new CoreSentence(doc, map);

    List<String> words = sentence.tokensAsStrings();
    assertEquals(2, words.size());
    assertNull(words.get(0));
    assertEquals("active", words.get(1));
  }
@Test
  public void testNullNERTagListReturnsListWithNullEntries() {
    CoreDocument doc = new CoreDocument("NullNER");
    CoreLabel token1 = new CoreLabel(); token1.setNER(null);
    CoreMap map = new Annotation("");
    map.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1));
    CoreSentence sentence = new CoreSentence(doc, map);
    List<String> nerTags = sentence.nerTags();
    assertEquals(1, nerTags.size());
    assertNull(nerTags.get(0));
  }
@Test
  public void testMultipleCachedTregexCallsWithDifferentPatterns() {
    CoreDocument doc = new CoreDocument("CacheVariants");
    Tree tree = Tree.valueOf("(ROOT (S (NP (DT A) (NN bot)) (VP (VBZ learns))))");
    CoreMap map = new Annotation("");
    map.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    CoreSentence sentence = new CoreSentence(doc, map);

    List<String> nounResults = sentence.tregexResults("NP");
    List<String> verbResults = sentence.tregexResults("VP");

    assertEquals(1, nounResults.size());
    assertEquals(1, verbResults.size());
    assertTrue(nounResults.get(0).contains("A bot"));
    assertTrue(verbResults.get(0).contains("learns"));
  }
@Test
  public void testWrapEntityMentionsMultipleCallsAreIdempotent() {
    CoreDocument doc = new CoreDocument("EntityIdempotent");
    CoreMap mentionMap = new Annotation("Seattle");
    CoreMap coreMap = new Annotation("");
    coreMap.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mentionMap));
    CoreSentence sentence = new CoreSentence(doc, coreMap);

    sentence.wrapEntityMentions();
    List<CoreEntityMention> mentions1 = sentence.entityMentions();
    sentence.wrapEntityMentions();
    List<CoreEntityMention> mentions2 = sentence.entityMentions();
    assertEquals(1, mentions1.size());
    assertEquals(1, mentions2.size());
    assertSame(mentions1, mentions2);
  }
@Test
  public void testLemmasWithNullTokenListReturnsEmpty() {
    CoreDocument doc = new CoreDocument("NoLemmas");
    CoreMap map = new Annotation("");
    CoreSentence sentence = new CoreSentence(doc, map);

    List<String> lemmas = sentence.lemmas();
    assertNotNull(lemmas);
    assertTrue(lemmas.isEmpty());
  }
@Test
  public void testEmptyTokensListReturnsEmptyInAllTokenMappings() {
    CoreDocument doc = new CoreDocument("EmptyTokens");
    CoreMap map = new Annotation("");
    map.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
    CoreSentence sentence = new CoreSentence(doc, map);

    assertTrue(sentence.tokensAsStrings().isEmpty());
    assertTrue(sentence.posTags().isEmpty());
    assertTrue(sentence.lemmas().isEmpty());
    assertTrue(sentence.nerTags().isEmpty());
  }
@Test
  public void testDependencyParseNullDoesNotThrow() {
    CoreDocument doc = new CoreDocument("NoDep");
    CoreMap map = new Annotation("");
    map.set(SemanticGraphCoreAnnotations.EnhancedPlusPlusDependenciesAnnotation.class, null);
    CoreSentence sentence = new CoreSentence(doc, map);
    SemanticGraph result = sentence.dependencyParse();
    assertNull(result);
  }
@Test
  public void testSentimentNullReturnsNull() {
    CoreDocument doc = new CoreDocument("NoSentiment");
    CoreMap map = new Annotation("");
    map.set(SentimentCoreAnnotations.SentimentClass.class, null);
    CoreSentence sentence = new CoreSentence(doc, map);
    String sentiment = sentence.sentiment();
    assertNull(sentiment);
  }
@Test
  public void testSentimentTreeNullReturnsNull() {
    CoreDocument doc = new CoreDocument("NoSentTree");
    CoreMap map = new Annotation("");
    map.set(SentimentCoreAnnotations.SentimentAnnotatedTree.class, null);
    CoreSentence sentence = new CoreSentence(doc, map);
    Tree tree = sentence.sentimentTree();
    assertNull(tree);
  }
@Test
  public void testToStringWithMissingTextAnnotationReturnsDefaultRepresentation() {
    CoreDocument doc = new CoreDocument("TextFallback");
    CoreMap map = new Annotation("");
    CoreSentence sentence = new CoreSentence(doc, map);
    String result = sentence.toString();
    assertNotNull(result);
    assertTrue(result.contains("Annotation"));
  } 
}
