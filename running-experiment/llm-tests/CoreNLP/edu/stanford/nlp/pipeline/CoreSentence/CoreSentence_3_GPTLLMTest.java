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

public class CoreSentence_3_GPTLLMTest {

 @Test
  public void testDocumentReturnsCorrectInstance() {
    CoreMap mockCoreMap = mock(CoreMap.class);
    CoreDocument mockDocument = mock(CoreDocument.class);
    CoreSentence sentence = new CoreSentence(mockDocument, mockCoreMap);
    assertSame(mockDocument, sentence.document());
  }
@Test
  public void testCoreMapReturnsCorrectInstance() {
    CoreMap mockCoreMap = mock(CoreMap.class);
    CoreDocument mockDocument = mock(CoreDocument.class);
    CoreSentence sentence = new CoreSentence(mockDocument, mockCoreMap);
    assertSame(mockCoreMap, sentence.coreMap());
  }
@Test
  public void testTextReturnsExpectedValue() {
    CoreMap mockCoreMap = mock(CoreMap.class);
    CoreDocument mockDocument = mock(CoreDocument.class);
    when(mockCoreMap.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Example sentence.");
    CoreSentence sentence = new CoreSentence(mockDocument, mockCoreMap);
    assertEquals("Example sentence.", sentence.text());
  }
@Test
  public void testCharOffsetsReturnsCorrectPair() {
    CoreMap mockCoreMap = mock(CoreMap.class);
    CoreDocument mockDocument = mock(CoreDocument.class);
    when(mockCoreMap.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class)).thenReturn(5);
    when(mockCoreMap.get(CoreAnnotations.CharacterOffsetEndAnnotation.class)).thenReturn(20);
    CoreSentence sentence = new CoreSentence(mockDocument, mockCoreMap);
    Pair<Integer, Integer> offsets = sentence.charOffsets();
    assertEquals(Integer.valueOf(5), offsets.first);
    assertEquals(Integer.valueOf(20), offsets.second);
  }
@Test
  public void testTokensReturnsCorrectList() {
    CoreMap mockCoreMap = mock(CoreMap.class);
    CoreDocument mockDocument = mock(CoreDocument.class);
    CoreLabel token1 = new CoreLabel();
    CoreLabel token2 = new CoreLabel();
    List<CoreLabel> tokenList = Arrays.asList(token1, token2);
    when(mockCoreMap.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokenList);
    CoreSentence sentence = new CoreSentence(mockDocument, mockCoreMap);
    assertEquals(tokenList, sentence.tokens());
  }
@Test
  public void testTokensAsStringsReturnsCorrectValues() {
    CoreMap mockCoreMap = mock(CoreMap.class);
    CoreDocument mockDocument = mock(CoreDocument.class);
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Hello");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("World");
    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    when(mockCoreMap.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
    CoreSentence sentence = new CoreSentence(mockDocument, mockCoreMap);
    List<String> result = sentence.tokensAsStrings();
    assertEquals(2, result.size());
    assertEquals("Hello", result.get(0));
    assertEquals("World", result.get(1));
  }
@Test
  public void testPosTagsReturnsCorrectTags() {
    CoreMap mockCoreMap = mock(CoreMap.class);
    CoreDocument mockDocument = mock(CoreDocument.class);
    CoreLabel token1 = new CoreLabel();
    token1.setTag("NN");
    CoreLabel token2 = new CoreLabel();
    token2.setTag("VB");
    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    when(mockCoreMap.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
    CoreSentence sentence = new CoreSentence(mockDocument, mockCoreMap);
    List<String> posTags = sentence.posTags();
    assertEquals(Arrays.asList("NN", "VB"), posTags);
  }
@Test
  public void testLemmasReturnsCorrectValues() {
    CoreMap mockCoreMap = mock(CoreMap.class);
    CoreDocument mockDocument = mock(CoreDocument.class);
    CoreLabel token1 = new CoreLabel();
    token1.setLemma("go");
    CoreLabel token2 = new CoreLabel();
    token2.setLemma("run");
    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    when(mockCoreMap.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
    CoreSentence sentence = new CoreSentence(mockDocument, mockCoreMap);
    List<String> result = sentence.lemmas();
    assertEquals(Arrays.asList("go", "run"), result);
  }
@Test
  public void testNerTagsReturnsCorrectValues() {
    CoreMap mockCoreMap = mock(CoreMap.class);
    CoreDocument mockDocument = mock(CoreDocument.class);
    CoreLabel token1 = new CoreLabel();
    token1.setNER("PERSON");
    CoreLabel token2 = new CoreLabel();
    token2.setNER("LOCATION");
    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    when(mockCoreMap.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
    CoreSentence sentence = new CoreSentence(mockDocument, mockCoreMap);
    assertEquals(Arrays.asList("PERSON", "LOCATION"), sentence.nerTags());
  }
@Test
  public void testConstituencyParseReturnsCorrectTree() {
    CoreMap mockCoreMap = mock(CoreMap.class);
    CoreDocument mockDocument = mock(CoreDocument.class);
    Tree tree = Tree.valueOf("(ROOT (NP (DT The) (NN dog)))");
    when(mockCoreMap.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(tree);
    CoreSentence sentence = new CoreSentence(mockDocument, mockCoreMap);
    assertEquals(tree, sentence.constituencyParse());
  }
@Test(expected = RuntimeException.class)
  public void testTregexThrowsRuntimeExceptionWhenNoParseAvailable() {
    CoreMap mockCoreMap = mock(CoreMap.class);
    CoreDocument mockDocument = mock(CoreDocument.class);
    when(mockCoreMap.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(null);
    CoreSentence sentence = new CoreSentence(mockDocument, mockCoreMap);
    TregexPattern pattern = TregexPattern.compile("NP");
    sentence.tregexResultTrees(pattern);
  }
@Test
  public void testDependencyParseReturnsCorrectValue() {
    CoreMap mockCoreMap = mock(CoreMap.class);
    CoreDocument mockDocument = mock(CoreDocument.class);
    SemanticGraph mockGraph = mock(SemanticGraph.class);
    when(mockCoreMap.get(SemanticGraphCoreAnnotations.EnhancedPlusPlusDependenciesAnnotation.class)).thenReturn(mockGraph);
    CoreSentence sentence = new CoreSentence(mockDocument, mockCoreMap);
    assertSame(mockGraph, sentence.dependencyParse());
  }
@Test
  public void testSentimentReturnsExpectedValue() {
    CoreMap mockCoreMap = mock(CoreMap.class);
    CoreDocument mockDocument = mock(CoreDocument.class);
    when(mockCoreMap.get(SentimentCoreAnnotations.SentimentClass.class)).thenReturn("Positive");
    CoreSentence sentence = new CoreSentence(mockDocument, mockCoreMap);
    assertEquals("Positive", sentence.sentiment());
  }
@Test
  public void testSentimentTreeReturnsTree() {
    CoreMap mockCoreMap = mock(CoreMap.class);
    CoreDocument mockDocument = mock(CoreDocument.class);
    Tree tree = Tree.valueOf("(3 (3 good))");
    when(mockCoreMap.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class)).thenReturn(tree);
    CoreSentence sentence = new CoreSentence(mockDocument, mockCoreMap);
    assertEquals(tree, sentence.sentimentTree());
  }
@Test
  public void testWrapEntityMentionsNormalCase() {
    CoreMap mockCoreMap = mock(CoreMap.class);
    CoreDocument mockDocument = mock(CoreDocument.class);
    CoreMap mention1 = mock(CoreMap.class);
    CoreMap mention2 = mock(CoreMap.class);
    List<CoreMap> mentions = Arrays.asList(mention1, mention2);
    when(mockCoreMap.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(mentions);
    CoreSentence sentence = new CoreSentence(mockDocument, mockCoreMap);
    sentence.wrapEntityMentions();
    List<CoreEntityMention> result = sentence.entityMentions();
    assertEquals(2, result.size());
    assertNotNull(result.get(0));
    assertNotNull(result.get(1));
  }
@Test
  public void testWrapEntityMentionsWithNullDoesNotThrow() {
    CoreMap mockCoreMap = mock(CoreMap.class);
    CoreDocument mockDocument = mock(CoreDocument.class);
    when(mockCoreMap.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(null);
    CoreSentence sentence = new CoreSentence(mockDocument, mockCoreMap);
    sentence.wrapEntityMentions();
    assertNull(sentence.entityMentions());
  }
@Test
  public void testRelationsReturnsExpectedTriples() {
    CoreMap mockCoreMap = mock(CoreMap.class);
    CoreDocument mockDocument = mock(CoreDocument.class);
    RelationTriple triple1 = mock(RelationTriple.class);
    RelationTriple triple2 = mock(RelationTriple.class);
    List<RelationTriple> triples = Arrays.asList(triple1, triple2);
    when(mockCoreMap.get(CoreAnnotations.KBPTriplesAnnotation.class)).thenReturn(triples);
    CoreSentence sentence = new CoreSentence(mockDocument, mockCoreMap);
    assertEquals(triples, sentence.relations());
  }
@Test
  public void testToStringDelegatesToCoreMap() {
    CoreMap mockCoreMap = mock(CoreMap.class);
    CoreDocument mockDocument = mock(CoreDocument.class);
    when(mockCoreMap.toString()).thenReturn("MockedCoreMapToString");
    CoreSentence sentence = new CoreSentence(mockDocument, mockCoreMap);
    assertEquals("MockedCoreMapToString", sentence.toString());
  }
@Test
  public void testTokensAsStringsWithEmptyTokenList() {
    CoreMap mockCoreMap = mock(CoreMap.class);
    CoreDocument mockDocument = mock(CoreDocument.class);
    when(mockCoreMap.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.emptyList());
    CoreSentence sentence = new CoreSentence(mockDocument, mockCoreMap);
    List<String> strings = sentence.tokensAsStrings();
    assertTrue(strings.isEmpty());
  }
@Test
  public void testPosTagsWithNullTags() {
    CoreMap mockCoreMap = mock(CoreMap.class);
    CoreDocument mockDocument = mock(CoreDocument.class);
    CoreLabel token1 = new CoreLabel();
    CoreLabel token2 = new CoreLabel();
    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    when(mockCoreMap.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
    CoreSentence sentence = new CoreSentence(mockDocument, mockCoreMap);
    List<String> result = sentence.posTags();
    assertEquals(Arrays.asList(null, null), result);
  }
@Test
  public void testLemmasWithNullLemmas() {
    CoreMap mockCoreMap = mock(CoreMap.class);
    CoreDocument mockDocument = mock(CoreDocument.class);
    CoreLabel token1 = new CoreLabel();
    CoreLabel token2 = new CoreLabel();
    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    when(mockCoreMap.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
    CoreSentence sentence = new CoreSentence(mockDocument, mockCoreMap);
    List<String> result = sentence.lemmas();
    assertEquals(Arrays.asList(null, null), result);
  }
@Test
  public void testNerTagsWithNullNERs() {
    CoreMap mockCoreMap = mock(CoreMap.class);
    CoreDocument mockDocument = mock(CoreDocument.class);
    CoreLabel token1 = new CoreLabel();
    CoreLabel token2 = new CoreLabel();
    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    when(mockCoreMap.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
    CoreSentence sentence = new CoreSentence(mockDocument, mockCoreMap);
    List<String> result = sentence.nerTags();
    assertEquals(Arrays.asList(null, null), result);
  }
@Test
  public void testTregexResultTreeReturnsMultipleMatches() {
    CoreMap mockCoreMap = mock(CoreMap.class);
    CoreDocument mockDocument = mock(CoreDocument.class);

    Tree tree = Tree.valueOf("(ROOT (NP (DT The) (NN cat)) (NP (DT a) (NN mouse)))");
    when(mockCoreMap.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(tree);

    CoreSentence sentence = new CoreSentence(mockDocument, mockCoreMap);
    TregexPattern pattern = TregexPattern.compile("NP");
    List<Tree> results = sentence.tregexResultTrees(pattern);
    assertEquals(2, results.size());
  }
@Test
  public void testTregexResultsStringReturnsExpectedStrings() {
    CoreMap mockCoreMap = mock(CoreMap.class);
    CoreDocument mockDocument = mock(CoreDocument.class);

    Tree tree = Tree.valueOf("(ROOT (NP (DT The) (NN dog)))");
    when(mockCoreMap.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(tree);

    CoreSentence sentence = new CoreSentence(mockDocument, mockCoreMap);
    List<String> results = sentence.tregexResults("NP");
    assertEquals(1, results.size());
    assertTrue(results.get(0).contains("The dog"));
  }
@Test
  public void testNounPhrasesReturnsEmptyWhenNoMatch() {
    CoreMap mockCoreMap = mock(CoreMap.class);
    CoreDocument mockDocument = mock(CoreDocument.class);

    Tree tree = Tree.valueOf("(ROOT (VP (VB runs)))");
    when(mockCoreMap.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(tree);

    CoreSentence sentence = new CoreSentence(mockDocument, mockCoreMap);
    List<String> np = sentence.nounPhrases();
    assertTrue(np.isEmpty());
  }
@Test
  public void testVerbPhrasesReturnsEmptyWhenNoMatch() {
    CoreMap mockCoreMap = mock(CoreMap.class);
    CoreDocument mockDocument = mock(CoreDocument.class);

    Tree tree = Tree.valueOf("(ROOT (NP (DT A) (NN tree)))");
    when(mockCoreMap.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(tree);

    CoreSentence sentence = new CoreSentence(mockDocument, mockCoreMap);
    List<Tree> vp = sentence.verbPhraseTrees();
    assertTrue(vp.isEmpty());
  }
@Test
  public void testCharOffsetsWithNullValuesCausesNullPointerException() {
    CoreMap mockCoreMap = mock(CoreMap.class);
    CoreDocument mockDocument = mock(CoreDocument.class);

    when(mockCoreMap.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class)).thenReturn(null);
    when(mockCoreMap.get(CoreAnnotations.CharacterOffsetEndAnnotation.class)).thenReturn(null);

    CoreSentence sentence = new CoreSentence(mockDocument, mockCoreMap);
    try {
      sentence.charOffsets();
      fail("Expected NullPointerException");
    } catch (NullPointerException e) {
      
    }
  }
@Test
  public void testEntityMentionsReturnsNullBeforeWrapCall() {
    CoreMap mockCoreMap = mock(CoreMap.class);
    CoreDocument mockDocument = mock(CoreDocument.class);

    CoreSentence sentence = new CoreSentence(mockDocument, mockCoreMap);
    assertNull(sentence.entityMentions());
  }
@Test
  public void testRelationsReturnsNullSafely() {
    CoreMap mockCoreMap = mock(CoreMap.class);
    CoreDocument mockDocument = mock(CoreDocument.class);

    when(mockCoreMap.get(CoreAnnotations.KBPTriplesAnnotation.class)).thenReturn(null);
    CoreSentence sentence = new CoreSentence(mockDocument, mockCoreMap);
    List<RelationTriple> triples = sentence.relations();
    assertNull(triples);
  }
@Test
  public void testSentimentReturnsNullSafely() {
    CoreMap mockCoreMap = mock(CoreMap.class);
    CoreDocument mockDocument = mock(CoreDocument.class);

    when(mockCoreMap.get(SentimentCoreAnnotations.SentimentClass.class)).thenReturn(null);
    CoreSentence sentence = new CoreSentence(mockDocument, mockCoreMap);
    assertNull(sentence.sentiment());
  }
@Test
  public void testSentimentTreeReturnsNullSafely() {
    CoreMap mockCoreMap = mock(CoreMap.class);
    CoreDocument mockDocument = mock(CoreDocument.class);

    when(mockCoreMap.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class)).thenReturn(null);
    CoreSentence sentence = new CoreSentence(mockDocument, mockCoreMap);
    assertNull(sentence.sentimentTree());
  }
@Test
  public void testTokensReturnsNullSafely() {
    CoreMap mockCoreMap = mock(CoreMap.class);
    CoreDocument mockDocument = mock(CoreDocument.class);
    when(mockCoreMap.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(null);
    CoreSentence sentence = new CoreSentence(mockDocument, mockCoreMap);
    try {
      sentence.tokensAsStrings();
      fail("Expected NullPointerException");
    } catch (NullPointerException ex) {
      
    }
  }
@Test
  public void testTregexResultsWithUnknownPatternReturnsEmptyList() {
    CoreMap mockCoreMap = mock(CoreMap.class);
    CoreDocument mockDocument = mock(CoreDocument.class);

    Tree tree = Tree.valueOf("(ROOT (S (VP (VB run))))");
    when(mockCoreMap.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(tree);

    CoreSentence sentence = new CoreSentence(mockDocument, mockCoreMap);
    List<String> results = sentence.tregexResults("ADJP");
    assertNotNull(results);
    assertTrue(results.isEmpty());
  }
@Test
  public void testTregexResultsReturnsSamePatternFromCache() {
    String pattern = "NP";
    TregexPattern compiledPattern = TregexPattern.compile(pattern);

    CoreMap mockCoreMap = mock(CoreMap.class);
    CoreDocument mockDocument = mock(CoreDocument.class);

    Tree tree = Tree.valueOf("(ROOT (NP (NN cat)))");
    when(mockCoreMap.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(tree);

    CoreSentence sentence = new CoreSentence(mockDocument, mockCoreMap);
    List<String> result1 = sentence.tregexResults(pattern);
    List<String> result2 = sentence.tregexResults(pattern);

    assertEquals(result1, result2);
  }
@Test
  public void testToStringReturnsNullIfCoreMapIsNull() {
    CoreSentence sentence = new CoreSentence(null, null);
    try {
      sentence.toString();
      fail("Expected NullPointerException");
    } catch (NullPointerException expected) {
      
    }
  }
@Test
  public void testCharOffsetsThrowsIfOnlyOneOffsetIsNull() {
    CoreMap mockCoreMap = mock(CoreMap.class);
    CoreDocument mockDocument = mock(CoreDocument.class);
    when(mockCoreMap.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class)).thenReturn(5);
    when(mockCoreMap.get(CoreAnnotations.CharacterOffsetEndAnnotation.class)).thenReturn(null);

    CoreSentence sentence = new CoreSentence(mockDocument, mockCoreMap);
    try {
      sentence.charOffsets();
      fail("Expected NullPointerException due to null end offset");
    } catch (NullPointerException expected) {
      
    }
  }
@Test
  public void testDependencyParseReturnsNullWithoutException() {
    CoreMap mockCoreMap = mock(CoreMap.class);
    CoreDocument mockDocument = mock(CoreDocument.class);
    when(mockCoreMap.get(SemanticGraphCoreAnnotations.EnhancedPlusPlusDependenciesAnnotation.class)).thenReturn(null);
    CoreSentence sentence = new CoreSentence(mockDocument, mockCoreMap);
    SemanticGraph dep = sentence.dependencyParse();
    assertNull(dep);
  }
@Test
  public void testWrapEntityMentionsWithEmptyListStillInitializesField() {
    CoreMap mockCoreMap = mock(CoreMap.class);
    CoreDocument mockDocument = mock(CoreDocument.class);
    when(mockCoreMap.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.emptyList());

    CoreSentence sentence = new CoreSentence(mockDocument, mockCoreMap);
    sentence.wrapEntityMentions();

    List<CoreEntityMention> em = sentence.entityMentions();
    assertNotNull(em);
    assertTrue(em.isEmpty());
  }
@Test
  public void testTregexResultTreesWithInvalidPatternSyntaxThrows() {
    CoreMap mockCoreMap = mock(CoreMap.class);
    CoreDocument mockDocument = mock(CoreDocument.class);
    Tree tree = Tree.valueOf("(ROOT (NP (NN foo)))");
    when(mockCoreMap.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(tree);

    CoreSentence sentence = new CoreSentence(mockDocument, mockCoreMap);

    try {
      sentence.tregexResults("???");
      fail("Expected RuntimeException due to invalid pattern");
    } catch (RuntimeException expected) {
      assertTrue(expected.getMessage().contains("Invalid"));
//     } catch (IllegalArgumentException illegal) {
//       
    }
  }
@Test
  public void testSentimentTreeWithNonNullTreeReturnsSameReference() {
    CoreMap mockCoreMap = mock(CoreMap.class);
    CoreDocument mockDocument = mock(CoreDocument.class);
    Tree tree = Tree.valueOf("(3 (3 good))");
    when(mockCoreMap.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class)).thenReturn(tree);
    CoreSentence sentence = new CoreSentence(mockDocument, mockCoreMap);
    Tree returnedTree = sentence.sentimentTree();
    assertSame(tree, returnedTree);
  }
@Test
  public void testNounPhraseTreesReturnsMultipleNPs() {
    CoreMap mockCoreMap = mock(CoreMap.class);
    CoreDocument mockDocument = mock(CoreDocument.class);
    Tree tree = Tree.valueOf("(ROOT (NP (DT The) (NN boy)) (NP (DT a) (NN dog)))");
    when(mockCoreMap.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(tree);
    CoreSentence sentence = new CoreSentence(mockDocument, mockCoreMap);
    List<Tree> nounTrees = sentence.nounPhraseTrees();
    assertEquals(2, nounTrees.size());
  }
@Test
  public void testVerbPhrasesWithComplexVPStructure() {
    CoreMap mockCoreMap = mock(CoreMap.class);
    CoreDocument mockDocument = mock(CoreDocument.class);
    Tree tree = Tree.valueOf("(ROOT (VP (VBD saw) (NP (DT the) (NN cat))))");
    when(mockCoreMap.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(tree);
    CoreSentence sentence = new CoreSentence(mockDocument, mockCoreMap);
    List<String> vps = sentence.verbPhrases();
    assertEquals(1, vps.size());
    assertTrue(vps.get(0).contains("saw the cat"));
  }
@Test
  public void testConstructorWithNullCoreMapAllowsAccessToDocument() {
    CoreDocument doc = mock(CoreDocument.class);
    CoreSentence sentence = new CoreSentence(doc, null);
    assertSame(doc, sentence.document());
  }
@Test
  public void testConstructorWithNullDocumentAllowsAccessToCoreMap() {
    CoreMap map = mock(CoreMap.class);
    CoreSentence sentence = new CoreSentence(null, map);
    assertSame(map, sentence.coreMap());
  }
@Test(expected = NullPointerException.class)
  public void testTextThrowsWhenTextAnnotationMissing() {
    CoreMap map = mock(CoreMap.class);
    CoreDocument doc = mock(CoreDocument.class);
    when(map.get(CoreAnnotations.TextAnnotation.class)).thenReturn(null);
    CoreSentence sentence = new CoreSentence(doc, map);
    String text = sentence.text(); 
    assertNull(text);
  }
@Test
  public void testTregexResultTreesReturnsEmptyWhenNoMatchFound() {
    CoreMap map = mock(CoreMap.class);
    CoreDocument doc = mock(CoreDocument.class);
    Tree tree = Tree.valueOf("(ROOT (VP (VB hit)))");
    when(map.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(tree);
    CoreSentence sentence = new CoreSentence(doc, map);
    TregexPattern pattern = TregexPattern.compile("NP");
    List<Tree> result = sentence.tregexResultTrees(pattern);
    assertTrue(result.isEmpty());
  }
@Test
  public void testTregexResultsWithComplexPatternReturnsExpectedStrings() {
    CoreMap map = mock(CoreMap.class);
    CoreDocument doc = mock(CoreDocument.class);
    Tree tree = Tree.valueOf("(ROOT (S (NP (DT The) (NN cat)) (VP (VBZ sleeps))))");
    when(map.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(tree);
    CoreSentence sentence = new CoreSentence(doc, map);
    List<String> results = sentence.tregexResults("NP|VP");
    assertEquals(2, results.size());
    assertTrue(results.get(0).contains("The cat"));
    assertTrue(results.get(1).contains("sleeps"));
  }
@Test
  public void testSentimentReturnsNullWhenNotSet() {
    CoreMap map = mock(CoreMap.class);
    CoreDocument doc = mock(CoreDocument.class);
    when(map.get(SentimentCoreAnnotations.SentimentClass.class)).thenReturn(null);
    CoreSentence sentence = new CoreSentence(doc, map);
    assertNull(sentence.sentiment());
  }
@Test
  public void testSentimentTreeReturnsNullWhenNotSet() {
    CoreMap map = mock(CoreMap.class);
    CoreDocument doc = mock(CoreDocument.class);
    when(map.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class)).thenReturn(null);
    CoreSentence sentence = new CoreSentence(doc, map);
    assertNull(sentence.sentimentTree());
  }
@Test
  public void testToStringCallsCoreMapToString() {
    CoreMap map = mock(CoreMap.class);
    CoreDocument doc = mock(CoreDocument.class);
    when(map.toString()).thenReturn("MockToString");
    CoreSentence sentence = new CoreSentence(doc, map);
    assertEquals("MockToString", sentence.toString());
  }
@Test
  public void testTregexPatternCachingMechanismReturnsSameInstance() {
    CoreMap map = mock(CoreMap.class);
    CoreDocument doc = mock(CoreDocument.class);
    Tree tree = Tree.valueOf("(ROOT (NP (NN something)))");
    when(map.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(tree);

    CoreSentence sentence = new CoreSentence(doc, map);
    TregexPattern pattern1 = TregexPattern.compile("NP");
    List<Tree> result1 = sentence.tregexResultTrees("NP");
    List<Tree> result2 = sentence.tregexResultTrees("NP");
    assertEquals(result1.size(), result2.size());
  }
@Test
  public void testTokensAsStringsHandlesNullTokenListGracefully() {
    CoreMap map = mock(CoreMap.class);
    CoreDocument doc = mock(CoreDocument.class);
    when(map.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(null);
    CoreSentence sentence = new CoreSentence(doc, map);
    try {
      sentence.tokensAsStrings();
      fail("Expected NullPointerException due to null tokens list");
    } catch (NullPointerException e) {
      
    }
  }
@Test
  public void testNounPhrasesReturnsEmptyWhenNoNPInTree() {
    CoreMap map = mock(CoreMap.class);
    CoreDocument doc = mock(CoreDocument.class);
    Tree tree = Tree.valueOf("(ROOT (VP (VBZ jumps)))");
    when(map.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(tree);
    CoreSentence sentence = new CoreSentence(doc, map);
    List<String> nps = sentence.nounPhrases();
    assertTrue(nps.isEmpty());
  }
@Test
  public void testVerbPhrasesReturnsEmptyWhenNoVPInTree() {
    CoreMap map = mock(CoreMap.class);
    CoreDocument doc = mock(CoreDocument.class);
    Tree tree = Tree.valueOf("(ROOT (NP (DT the) (NN man)))");
    when(map.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(tree);
    CoreSentence sentence = new CoreSentence(doc, map);
    List<String> vps = sentence.verbPhrases();
    assertTrue(vps.isEmpty());
  }
@Test
  public void testWrapEntityMentionsHandlesSingleMention() {
    CoreMap map = mock(CoreMap.class);
    CoreDocument doc = mock(CoreDocument.class);
    CoreMap mention = mock(CoreMap.class);
    when(map.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.singletonList(mention));
    CoreSentence sentence = new CoreSentence(doc, map);
    sentence.wrapEntityMentions();
    List<CoreEntityMention> mentions = sentence.entityMentions();
    assertNotNull(mentions);
    assertEquals(1, mentions.size());
  }
@Test
  public void testWrapEntityMentionsHandlesNullElementInsideList() {
    CoreMap map = mock(CoreMap.class);
    CoreDocument doc = mock(CoreDocument.class);
    List<CoreMap> mentionList = new ArrayList<>();
    mentionList.add(null);
    when(map.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(mentionList);
    CoreSentence sentence = new CoreSentence(doc, map);
    try {
      sentence.wrapEntityMentions();
      fail("Expected NullPointerException due to null mention in list");
    } catch (NullPointerException expected) {
      
    }
  }
@Test
  public void testCharOffsetsHandlesZeroOffsets() {
    CoreMap map = mock(CoreMap.class);
    CoreDocument doc = mock(CoreDocument.class);
    when(map.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class)).thenReturn(0);
    when(map.get(CoreAnnotations.CharacterOffsetEndAnnotation.class)).thenReturn(0);
    CoreSentence sentence = new CoreSentence(doc, map);
    Pair<Integer, Integer> offsets = sentence.charOffsets();
    assertEquals(Integer.valueOf(0), offsets.first);
    assertEquals(Integer.valueOf(0), offsets.second);
  }
@Test
  public void testRelationsReturnsEmptyListIfEmptyAnnotation() {
    CoreMap map = mock(CoreMap.class);
    CoreDocument doc = mock(CoreDocument.class);
    when(map.get(CoreAnnotations.KBPTriplesAnnotation.class)).thenReturn(Collections.emptyList());
    CoreSentence sentence = new CoreSentence(doc, map);
    List<edu.stanford.nlp.ie.util.RelationTriple> relations = sentence.relations();
    assertTrue(relations.isEmpty());
  }
@Test
  public void testTokensReturnsNullTokenClassGracefully() {
    CoreMap map = mock(CoreMap.class);
    CoreDocument doc = mock(CoreDocument.class);
    CoreLabel token = new CoreLabel(); 
    List<CoreLabel> tokens = Collections.singletonList(token);
    when(map.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
    CoreSentence sentence = new CoreSentence(doc, map);
    List<String> tags = sentence.posTags();
    assertEquals(1, tags.size());
    assertNull(tags.get(0));
  }
@Test
  public void testTregexResultsReturnsEmptyListWhenParseTreeHasNoMatch() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    Tree tree = Tree.valueOf("(ROOT (VP (VB sleeps)))");
    when(coreMap.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(tree);
    CoreSentence sentence = new CoreSentence(document, coreMap);
    List<String> result = sentence.tregexResults("NP");
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
@Test
  public void testWrapEntityMentionsHandlesEmptyMentionClassList() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    when(coreMap.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.emptyList());
    CoreSentence sentence = new CoreSentence(document, coreMap);
    sentence.wrapEntityMentions();
    List<CoreEntityMention> entityMentions = sentence.entityMentions();
    assertNotNull(entityMentions);
    assertEquals(0, entityMentions.size());
  }
@Test
  public void testCharOffsetsWithNegativeOffsets() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    when(coreMap.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class)).thenReturn(-1);
    when(coreMap.get(CoreAnnotations.CharacterOffsetEndAnnotation.class)).thenReturn(-5);
    CoreSentence sentence = new CoreSentence(document, coreMap);
    Pair<Integer, Integer> offsets = sentence.charOffsets();
    assertEquals(Integer.valueOf(-1), offsets.first);
    assertEquals(Integer.valueOf(-5), offsets.second);
  }
@Test
  public void testTextAnnotationIsNullThrowsNullPointerException() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    when(coreMap.get(CoreAnnotations.TextAnnotation.class)).thenReturn(null);
    CoreSentence sentence = new CoreSentence(document, coreMap);
    try {
      sentence.text();
      fail("Expected NullPointerException");
    } catch (NullPointerException expected) {
      
    }
  }
@Test
  public void testDependencyAnnotationIsNullReturnsNull() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    when(coreMap.get(SemanticGraphCoreAnnotations.EnhancedPlusPlusDependenciesAnnotation.class)).thenReturn(null);
    CoreSentence sentence = new CoreSentence(document, coreMap);
    assertNull(sentence.dependencyParse());
  }
@Test
  public void testTregexReturnsCachedPatternOnRepeatedUse() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    Tree tree = Tree.valueOf("(ROOT (NP (DT some) (NN text)))");
    when(coreMap.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(tree);
    CoreSentence sentence = new CoreSentence(document, coreMap);
    List<String> result1 = sentence.tregexResults("NP");
    List<String> result2 = sentence.tregexResults("NP");
    assertEquals(result1, result2);
  }
@Test
  public void testNullSentimentAnnotationReturnsNull() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    when(coreMap.get(SentimentCoreAnnotations.SentimentClass.class)).thenReturn(null);
    CoreSentence sentence = new CoreSentence(document, coreMap);
    assertNull(sentence.sentiment());
  }
@Test
  public void testNullSentimentTreeAnnotationReturnsNull() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    when(coreMap.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class)).thenReturn(null);
    CoreSentence sentence = new CoreSentence(document, coreMap);
    assertNull(sentence.sentimentTree());
  }
@Test
  public void testRelationsReturnsNullWithoutException() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    when(coreMap.get(CoreAnnotations.KBPTriplesAnnotation.class)).thenReturn(null);
    CoreSentence sentence = new CoreSentence(document, coreMap);
    List<RelationTriple> result = sentence.relations();
    assertNull(result);
  }
@Test
  public void testTokensAnnotationReturnsEmptyListForEmptyTokenStream() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    when(coreMap.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.emptyList());
    CoreSentence sentence = new CoreSentence(document, coreMap);
    List<String> tokens = sentence.tokensAsStrings();
    assertTrue(tokens.isEmpty());
  }
@Test
  public void testNullTokensListThrowsOnPosTags() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    when(coreMap.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(null);
    CoreSentence sentence = new CoreSentence(document, coreMap);
    try {
      sentence.posTags();
      fail("Expected NullPointerException");
    } catch (NullPointerException expected) {
      
    }
  }
@Test
  public void testTregexResultTreesThrowsWhenParseTreeMissing() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    when(coreMap.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(null);
    CoreSentence sentence = new CoreSentence(document, coreMap);
    TregexPattern pattern = TregexPattern.compile("NP");
    try {
      sentence.tregexResultTrees(pattern);
      fail("Expected RuntimeException due to missing parse");
    } catch (RuntimeException expected) {
      assertTrue(expected.getMessage().contains("constituency parse"));
    }
  }
@Test
  public void testToStringWithNullCoreMapThrowsNullPointerException() {
    CoreSentence sentence = new CoreSentence(mock(CoreDocument.class), null);
    try {
      sentence.toString();
      fail("Expected NullPointerException");
    } catch (NullPointerException expected) {
      
    }
  }
@Test
  public void testWrapEntityMentionsWithNullEntityMentionThrowsException() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(null);
    when(coreMap.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(mentions);
    CoreSentence sentence = new CoreSentence(document, coreMap);
    try {
      sentence.wrapEntityMentions();
      fail("Expected NullPointerException");
    } catch (NullPointerException expected) {
      
    }
  }
@Test
  public void testWrapEntityMentionsDoesNothingIfAlreadyCalled() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    when(coreMap.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.emptyList());
    CoreSentence sentence = new CoreSentence(document, coreMap);
    sentence.wrapEntityMentions();
    sentence.wrapEntityMentions(); 
    assertNotNull(sentence.entityMentions());
  }
@Test
  public void testTokenListWithNullTokenDoesNotThrowOnPosTags() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    List<CoreLabel> tokens = Arrays.asList(new CoreLabel(), null);
    when(coreMap.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
    CoreSentence sentence = new CoreSentence(document, coreMap);
    try {
      sentence.posTags();
      fail("Expected NullPointerException");
    } catch (NullPointerException expected) {
      
    }
  }
@Test
  public void testLemmaListWithMixedNullDoesNotThrow() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    CoreLabel token1 = new CoreLabel();
    token1.setLemma("eat");
    CoreLabel token2 = mock(CoreLabel.class);
    when(token2.lemma()).thenReturn(null);
    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    when(coreMap.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
    CoreSentence sentence = new CoreSentence(document, coreMap);
    List<String> lemmas = sentence.lemmas();
    assertEquals(2, lemmas.size());
    assertEquals("eat", lemmas.get(0));
    assertNull(lemmas.get(1));
  }
@Test
  public void testMultipleTregexResultsReturnCorrectSubArrays() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    Tree tree = Tree.valueOf("(ROOT (NP (NN Alice)) (VP (VB loves) (NP (NN Bob))))");
    when(coreMap.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(tree);
    CoreSentence sentence = new CoreSentence(document, coreMap);
    List<String> results = sentence.tregexResults("NP");
    assertEquals(2, results.size());
    assertTrue(results.get(0).contains("Alice"));
    assertTrue(results.get(1).contains("Bob"));
  }
@Test
  public void testTregexPatternFallbackDoesNotFailOnInvalidPattern() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    Tree tree = Tree.valueOf("(ROOT (NP (DT some) (NN demonstration)))");
    when(coreMap.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(tree);
    CoreSentence sentence = new CoreSentence(document, coreMap);
    try {
      sentence.tregexResults("[unclosed");
      fail("Expected IllegalArgumentException from TregexPattern.compile");
    } catch (IllegalArgumentException e) {
      
    } catch (RuntimeException e) {
      
    }
  }
@Test
  public void testTokenListWithNullFieldsReturnsNullNERs() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    CoreLabel token1 = mock(CoreLabel.class);
    when(token1.ner()).thenReturn(null);
    CoreLabel token2 = mock(CoreLabel.class);
    when(token2.ner()).thenReturn("ORG");
    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    when(coreMap.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
    CoreSentence sentence = new CoreSentence(document, coreMap);
    List<String> nerTags = sentence.nerTags();
    assertNull(nerTags.get(0));
    assertEquals("ORG", nerTags.get(1));
  }
@Test
  public void testConstructorWithNullDocumentAndNullCoreMapResultsInUnsupportedUsage() {
    CoreSentence sentence = new CoreSentence(null, null);
    try {
      sentence.coreMap();
      fail("Expected NullPointerException");
    } catch (NullPointerException e) {
      
    }
  }
@Test
  public void testConstituencyParseReturnsNullGracefully() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    when(coreMap.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(null);
    CoreSentence sentence = new CoreSentence(document, coreMap);
    assertNull(sentence.constituencyParse());
  }
@Test
  public void testSentimentTreeReturnsSameInstanceTwice() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    Tree tree = Tree.valueOf("(3 (3 good))");
    when(coreMap.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class)).thenReturn(tree);
    CoreSentence sentence = new CoreSentence(document, coreMap);
    Tree t1 = sentence.sentimentTree();
    Tree t2 = sentence.sentimentTree();
    assertSame(t1, t2);
  }
@Test
  public void testCachedTregexResultTreesReturnsIdenticalResultForSamePattern() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    Tree tree = Tree.valueOf("(ROOT (NP (NN test)))");
    when(coreMap.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(tree);
    CoreSentence sentence = new CoreSentence(document, coreMap);
    List<Tree> first = sentence.tregexResultTrees("NP");
    List<Tree> second = sentence.tregexResultTrees("NP");
    assertEquals(first.toString(), second.toString());
  }
@Test
  public void testToStringReturnsCoreMapString() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    when(coreMap.toString()).thenReturn("mockedCoreMap");
    CoreSentence sentence = new CoreSentence(document, coreMap);
    assertEquals("mockedCoreMap", sentence.toString());
  }
@Test
  public void testNullTreeResultsInEmptyNounPhraseList() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    when(coreMap.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(null);
    CoreSentence sentence = new CoreSentence(document, coreMap);
    try {
      sentence.nounPhraseTrees();
      fail("Expected RuntimeException due to missing parse");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("must annotate the document"));
    }
  }
@Test
  public void testDependencyParseReturnsSameObjectTwice() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    SemanticGraph graph = mock(SemanticGraph.class);
    when(coreMap.get(SemanticGraphCoreAnnotations.EnhancedPlusPlusDependenciesAnnotation.class)).thenReturn(graph);
    CoreSentence sentence = new CoreSentence(document, coreMap);
    SemanticGraph g1 = sentence.dependencyParse();
    SemanticGraph g2 = sentence.dependencyParse();
    assertSame(g1, g2);
  }
@Test
  public void testRelationsReturnsEmptyListIfCoreMapReturnsEmpty() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    when(coreMap.get(CoreAnnotations.KBPTriplesAnnotation.class)).thenReturn(Collections.emptyList());
    CoreSentence sentence = new CoreSentence(document, coreMap);
    List<RelationTriple> relations = sentence.relations();
    assertNotNull(relations);
    assertTrue(relations.isEmpty());
  }
@Test
  public void testTokensAsStringsWithTokenMissingWord() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument doc = mock(CoreDocument.class);

    CoreLabel token1 = new CoreLabel();
    CoreLabel token2 = mock(CoreLabel.class);
    when(token2.word()).thenReturn(null);

    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    token1.setWord("Hi");

    when(coreMap.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
    CoreSentence sentence = new CoreSentence(doc, coreMap);

    List<String> result = sentence.tokensAsStrings();
    assertEquals(2, result.size());
    assertEquals("Hi", result.get(0));
    assertNull(result.get(1));
  }
@Test
  public void testMultipleVPStructuresExtractedWithTregex() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument doc = mock(CoreDocument.class);

    Tree tree = Tree.valueOf(
      "(ROOT (S (VP (VB eats)) (VP (VB runs))))"
    );
    when(coreMap.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(tree);

    CoreSentence sentence = new CoreSentence(doc, coreMap);
    List<String> verbPhrases = sentence.verbPhrases();

    assertEquals(2, verbPhrases.size());
    assertTrue(verbPhrases.get(0).contains("eats") || verbPhrases.get(0).contains("runs"));
    assertTrue(verbPhrases.get(1).contains("runs") || verbPhrases.get(1).contains("eats"));
  }
@Test
  public void testTregexReturnsNoMatchWhenPatternHasNoCorrespondence() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument doc = mock(CoreDocument.class);

    Tree tree = Tree.valueOf("(ROOT (S (VP (VB sleeps))))");
    when(coreMap.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(tree);

    CoreSentence sentence = new CoreSentence(doc, coreMap);

    List<String> results = sentence.tregexResults("WHNP"); 
    assertNotNull(results);
    assertTrue(results.isEmpty());
  }
@Test
  public void testTregexCacheDistinctPatternsCreateDistinctInstances() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument doc = mock(CoreDocument.class);

    Tree tree = Tree.valueOf("(ROOT (S (NP (NN Alice)) (VP (VBZ writes))))");
    when(coreMap.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(tree);

    CoreSentence sentence = new CoreSentence(doc, coreMap);
    List<String> res1 = sentence.tregexResults("NP");
    List<String> res2 = sentence.tregexResults("VP");
    assertNotEquals(res1, res2);
  }
@Test
  public void testSentimentWithMissingStringValueReturnsNull() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument doc = mock(CoreDocument.class);

    when(coreMap.get(SentimentCoreAnnotations.SentimentClass.class)).thenReturn(null);
    CoreSentence sentence = new CoreSentence(doc, coreMap);

    assertNull(sentence.sentiment());
  }
@Test
  public void testWrapEntityMentionsWithDuplicatedCoreMapMentions() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument doc = mock(CoreDocument.class);

    CoreMap mention = mock(CoreMap.class);
    when(coreMap.get(CoreAnnotations.MentionsAnnotation.class))
      .thenReturn(Arrays.asList(mention, mention));

    CoreSentence sentence = new CoreSentence(doc, coreMap);
    sentence.wrapEntityMentions();

    List<CoreEntityMention> mentions = sentence.entityMentions();
    assertEquals(2, mentions.size());
    assertNotNull(mentions.get(0));
    assertNotNull(mentions.get(1));
  }
@Test
  public void testKBPTriplesAnnotationReturnsEmptyList() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument doc = mock(CoreDocument.class);
    List<RelationTriple> empty = Collections.emptyList();
    when(coreMap.get(CoreAnnotations.KBPTriplesAnnotation.class)).thenReturn(empty);

    CoreSentence sentence = new CoreSentence(doc, coreMap);
    List<RelationTriple> result = sentence.relations();

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
@Test(expected = RuntimeException.class)
  public void testTregexThrowsOnNullTreeAnnotation() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument doc = mock(CoreDocument.class);
    when(coreMap.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(null);
    CoreSentence sentence = new CoreSentence(doc, coreMap);

    TregexPattern pattern = TregexPattern.compile("NP");
    sentence.tregexResultTrees(pattern); 
  }
@Test
  public void testLemmaExtractionHandlesEmptyCoreLabelList() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument doc = mock(CoreDocument.class);

    when(coreMap.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.emptyList());
    CoreSentence sentence = new CoreSentence(doc, coreMap);

    List<String> lemmas = sentence.lemmas();
    assertNotNull(lemmas);
    assertTrue(lemmas.isEmpty());
  }
@Test
  public void testCoreMapWithNullTextAnnotationReturnsNullText() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument doc = mock(CoreDocument.class);
    when(coreMap.get(CoreAnnotations.TextAnnotation.class)).thenReturn(null);
    CoreSentence sentence = new CoreSentence(doc, coreMap);

    try {
      sentence.text();
      fail("Expected NullPointerException");
    } catch (NullPointerException expected) {
      
    }
  }
@Test
  public void testCharOffsetsReturnsExtremeOffsetValues() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument doc = mock(CoreDocument.class);

    when(coreMap.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class)).thenReturn(Integer.MIN_VALUE);
    when(coreMap.get(CoreAnnotations.CharacterOffsetEndAnnotation.class)).thenReturn(Integer.MAX_VALUE);

    CoreSentence sentence = new CoreSentence(doc, coreMap);
    Pair<Integer, Integer> pair = sentence.charOffsets();

    assertEquals(Integer.valueOf(Integer.MIN_VALUE), pair.first);
    assertEquals(Integer.valueOf(Integer.MAX_VALUE), pair.second);
  }
@Test
  public void testDependencyParseReturnsNullWithoutFailureWhenAnnotationNotSet() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument doc = mock(CoreDocument.class);
    when(coreMap.get(SemanticGraphCoreAnnotations.EnhancedPlusPlusDependenciesAnnotation.class)).thenReturn(null);
    CoreSentence sentence = new CoreSentence(doc, coreMap);
    assertNull(sentence.dependencyParse());
  }
@Test
  public void testSentimentTreeReturnsNullIfMissingAnnotation() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument doc = mock(CoreDocument.class);
    when(coreMap.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class)).thenReturn(null);
    CoreSentence sentence = new CoreSentence(doc, coreMap);
    assertNull(sentence.sentimentTree());
  } 
}
