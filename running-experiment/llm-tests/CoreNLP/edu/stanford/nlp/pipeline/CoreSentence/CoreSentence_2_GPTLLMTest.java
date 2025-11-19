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

public class CoreSentence_2_GPTLLMTest {

 @Test
  public void testTextReturnsExpectedResult() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    when(coreMap.get(CoreAnnotations.TextAnnotation.class)).thenReturn("This is a test.");
    CoreSentence sentence = new CoreSentence(document, coreMap);

    String actual = sentence.text();
    assertEquals("This is a test.", actual);
  }
@Test
  public void testCharOffsetsReturnsExpectedPair() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    when(coreMap.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class)).thenReturn(12);
    when(coreMap.get(CoreAnnotations.CharacterOffsetEndAnnotation.class)).thenReturn(34);
    CoreSentence sentence = new CoreSentence(document, coreMap);

    Pair<Integer, Integer> offsets = sentence.charOffsets();
    assertEquals(Integer.valueOf(12), offsets.first);
    assertEquals(Integer.valueOf(34), offsets.second);
  }
@Test
  public void testTokensAsStringsReturnsCorrectList() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);

    CoreLabel token1 = mock(CoreLabel.class);
    CoreLabel token2 = mock(CoreLabel.class);
    when(token1.word()).thenReturn("Hello");
    when(token2.word()).thenReturn("World");

    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    when(coreMap.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
    CoreSentence sentence = new CoreSentence(document, coreMap);

    List<String> words = sentence.tokensAsStrings();
    assertEquals(2, words.size());
    assertEquals("Hello", words.get(0));
    assertEquals("World", words.get(1));
  }
@Test
  public void testPosTagsReturnsCorrectTags() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);

    CoreLabel token1 = mock(CoreLabel.class);
    CoreLabel token2 = mock(CoreLabel.class);
    when(token1.tag()).thenReturn("NN");
    when(token2.tag()).thenReturn("VB");

    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    when(coreMap.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
    CoreSentence sentence = new CoreSentence(document, coreMap);

    List<String> posTags = sentence.posTags();
    assertEquals(2, posTags.size());
    assertEquals("NN", posTags.get(0));
    assertEquals("VB", posTags.get(1));
  }
@Test
  public void testLemmasReturnsCorrectValues() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);

    CoreLabel token1 = mock(CoreLabel.class);
    CoreLabel token2 = mock(CoreLabel.class);
    when(token1.lemma()).thenReturn("run");
    when(token2.lemma()).thenReturn("fast");

    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    when(coreMap.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
    CoreSentence sentence = new CoreSentence(document, coreMap);

    List<String> lemmas = sentence.lemmas();
    assertEquals(2, lemmas.size());
    assertEquals("run", lemmas.get(0));
    assertEquals("fast", lemmas.get(1));
  }
@Test
  public void testNerTagsReturnsCorrectNER() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);

    CoreLabel token1 = mock(CoreLabel.class);
    CoreLabel token2 = mock(CoreLabel.class);
    when(token1.ner()).thenReturn("LOCATION");
    when(token2.ner()).thenReturn("O");

    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    when(coreMap.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
    CoreSentence sentence = new CoreSentence(document, coreMap);

    List<String> nerTags = sentence.nerTags();
    assertEquals(2, nerTags.size());
    assertEquals("LOCATION", nerTags.get(0));
    assertEquals("O", nerTags.get(1));
  }
@Test
  public void testDependencyParseReturnsGraph() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);

    SemanticGraph graph = mock(SemanticGraph.class);
    when(coreMap.get(SemanticGraphCoreAnnotations.EnhancedPlusPlusDependenciesAnnotation.class)).thenReturn(graph);
    CoreSentence sentence = new CoreSentence(document, coreMap);

    SemanticGraph actual = sentence.dependencyParse();
    assertEquals(graph, actual);
  }
@Test
  public void testSentimentReturnsCorrectString() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);

    when(coreMap.get(SentimentCoreAnnotations.SentimentClass.class)).thenReturn("Neutral");
    CoreSentence sentence = new CoreSentence(document, coreMap);

    String sent = sentence.sentiment();
    assertEquals("Neutral", sent);
  }
@Test
  public void testSentimentTreeReturnsCorrectTree() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);

    Tree tree = mock(Tree.class);
    when(coreMap.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class)).thenReturn(tree);
    CoreSentence sentence = new CoreSentence(document, coreMap);

    Tree result = sentence.sentimentTree();
    assertEquals(tree, result);
  }
@Test
  public void testWrapEntityMentionsWrapsCorrectly() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);

    CoreMap entity1 = mock(CoreMap.class);
    CoreMap entity2 = mock(CoreMap.class);
    List<CoreMap> mentionList = Arrays.asList(entity1, entity2);

    when(coreMap.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(mentionList);
    CoreSentence sentence = new CoreSentence(document, coreMap);
    sentence.wrapEntityMentions();

    List<CoreEntityMention> mentions = sentence.entityMentions();
    assertNotNull(mentions);
    assertEquals(2, mentions.size());
    assertTrue(mentions.get(0) instanceof CoreEntityMention);
    assertTrue(mentions.get(1) instanceof CoreEntityMention);
  }
@Test
  public void testEntityMentionsReturnsNullWhenNonePresent() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);

    when(coreMap.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(null);
    CoreSentence sentence = new CoreSentence(document, coreMap);
    sentence.wrapEntityMentions();
    
    List<CoreEntityMention> mentions = sentence.entityMentions();
    assertNull(mentions);
  }
@Test
  public void testToStringReturnsExpectedCoreMapToString() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);

    when(coreMap.toString()).thenReturn("Mocked CoreMap");
    CoreSentence sentence = new CoreSentence(document, coreMap);

    String output = sentence.toString();
    assertEquals("Mocked CoreMap", output);
  }
@Test
  public void testRelationsReturnsExpectedTriples() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);

    RelationTriple relation = mock(RelationTriple.class);
    List<RelationTriple> triples = Arrays.asList(relation);
    when(coreMap.get(CoreAnnotations.KBPTriplesAnnotation.class)).thenReturn(triples);
    CoreSentence sentence = new CoreSentence(document, coreMap);

    List<RelationTriple> result = sentence.relations();
    assertEquals(triples, result);
  }
@Test
  public void testConstituencyParseReturnsExpectedTree() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);

    Tree tree = mock(Tree.class);
    when(coreMap.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(tree);
    CoreSentence sentence = new CoreSentence(document, coreMap);

    Tree result = sentence.constituencyParse();
    assertEquals(tree, result);
  }
@Test(expected = RuntimeException.class)
  public void testTregexResultTreesThrowsExceptionWithoutParse() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);

    when(coreMap.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(null);
    CoreSentence sentence = new CoreSentence(document, coreMap);

    TregexPattern pattern = TregexPattern.compile("NP");
    sentence.tregexResultTrees(pattern);
  }
@Test
  public void testNounPhrasesFindsExpectedMatches() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);

    Tree tree = Tree.valueOf("(ROOT (S (NP (DT This) (NN test)) (VP (VBZ works))))");
    when(coreMap.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(tree);
    CoreSentence sentence = new CoreSentence(document, coreMap);

    List<String> nounPhrases = sentence.nounPhrases();
    assertEquals(1, nounPhrases.size());
    assertEquals("This test", nounPhrases.get(0));
  }
@Test
  public void testVerbPhrasesFindsExpectedMatches() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);

    Tree tree = Tree.valueOf("(ROOT (S (NP (PRP It)) (VP (VBZ works))))");
    when(coreMap.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(tree);
    CoreSentence sentence = new CoreSentence(document, coreMap);

    List<String> verbPhrases = sentence.verbPhrases();
    assertEquals(1, verbPhrases.size());
    assertEquals("works", verbPhrases.get(0));
  }
@Test
  public void testTregexResultsUsingStringPatternReturnsExpectedMatch() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);

    Tree tree = Tree.valueOf("(ROOT (S (NP (DT A) (NN cat)) (VP (VBZ sleeps))))");
    when(coreMap.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(tree);
    CoreSentence sentence = new CoreSentence(document, coreMap);

    List<String> matches = sentence.tregexResults("NP");
    assertEquals(1, matches.size());
    assertEquals("A cat", matches.get(0));
  }
@Test
  public void testDocumentReturnsCorrectInstance() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);

    CoreSentence sentence = new CoreSentence(document, coreMap);
    assertEquals(document, sentence.document());
  }
@Test
  public void testCoreMapReturnsCorrectInstance() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);

    CoreSentence sentence = new CoreSentence(document, coreMap);
    assertEquals(coreMap, sentence.coreMap());
  }
@Test
  public void testTokensReturnsNullIfAnnotationMissing() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    when(coreMap.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(null);
    CoreSentence sentence = new CoreSentence(document, coreMap);

    List<CoreLabel> result = sentence.tokens();
    assertNull(result);
  }
@Test
  public void testTokensAsStringsReturnsEmptyListIfNoTokens() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    when(coreMap.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.emptyList());
    CoreSentence sentence = new CoreSentence(document, coreMap);

    List<String> result = sentence.tokensAsStrings();
    assertNotNull(result);
    assertEquals(0, result.size());
  }
@Test
  public void testLemmaNullValueHandledGracefully() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);

    CoreLabel token = mock(CoreLabel.class);
    when(token.lemma()).thenReturn(null);
    when(coreMap.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(token));
    CoreSentence sentence = new CoreSentence(document, coreMap);

    List<String> lemmas = sentence.lemmas();
    assertEquals(1, lemmas.size());
    assertNull(lemmas.get(0));
  }
@Test
  public void testPosTagsWithNullTokensReturnsEmptyList() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);

    when(coreMap.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(null);
    CoreSentence sentence = new CoreSentence(document, coreMap);

    List<String> pos = sentence.posTags();
    assertThrows(NullPointerException.class, () -> {
      pos.size(); 
    });
  }
@Test
  public void testSentimentReturnsNullWhenAnnotationMissing() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    when(coreMap.get(SentimentCoreAnnotations.SentimentClass.class)).thenReturn(null);
    CoreSentence sentence = new CoreSentence(document, coreMap);

    String result = sentence.sentiment();
    assertNull(result);
  }
@Test
  public void testSentimentTreeReturnsNullWhenAnnotationMissing() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    when(coreMap.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class)).thenReturn(null);
    CoreSentence sentence = new CoreSentence(document, coreMap);

    Tree result = sentence.sentimentTree();
    assertNull(result);
  }
@Test
  public void testDependencyParseReturnsNullWhenNotProvided() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    when(coreMap.get(SemanticGraphCoreAnnotations.EnhancedPlusPlusDependenciesAnnotation.class)).thenReturn(null);
    CoreSentence sentence = new CoreSentence(document, coreMap);

    SemanticGraph result = sentence.dependencyParse();
    assertNull(result);
  }
@Test
  public void testWrapEntityMentionsWithEmptyMentionList() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    when(coreMap.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.emptyList());
    CoreSentence sentence = new CoreSentence(document, coreMap);

    sentence.wrapEntityMentions();

    List<CoreEntityMention> mentions = sentence.entityMentions();
    assertNotNull(mentions);
    assertTrue(mentions.isEmpty());
  }
@Test
  public void testRelationsReturnsNullWhenNotAvailable() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    when(coreMap.get(CoreAnnotations.KBPTriplesAnnotation.class)).thenReturn(null);
    CoreSentence sentence = new CoreSentence(document, coreMap);

    List<RelationTriple> result = sentence.relations();
    assertNull(result);
  }
@Test
  public void testTregexResultsEmptyWhenNoMatch() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    Tree tree = Tree.valueOf("(ROOT (S (VP (VBZ runs))))");
    when(coreMap.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(tree);

    CoreSentence sentence = new CoreSentence(document, coreMap);

    TregexPattern pattern = TregexPattern.compile("NP"); 
    List<Tree> result = sentence.tregexResultTrees(pattern);
    assertNotNull(result);
    assertTrue(result.isEmpty());

    List<String> strings = sentence.tregexResults(pattern);
    assertNotNull(strings);
    assertTrue(strings.isEmpty());
  }
@Test
  public void testTregexResultWithCompiledPatternCacheIntegration() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    Tree tree = Tree.valueOf("(ROOT (NP (DT The) (NN cat)))");
    when(coreMap.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(tree);

    CoreSentence sentence = new CoreSentence(document, coreMap);

    List<String> result1 = sentence.tregexResults("NP");
    List<String> result2 = sentence.tregexResults("NP");

    assertEquals(1, result1.size());
    assertEquals("The cat", result1.get(0));
    assertEquals(result1, result2);
  }
@Test
  public void testNounPhraseTreeEmptyWhenNoNPExists() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    Tree tree = Tree.valueOf("(ROOT (VP (VBZ runs)))"); 
    when(coreMap.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(tree);

    CoreSentence sentence = new CoreSentence(document, coreMap);
    List<Tree> npTrees = sentence.nounPhraseTrees();
    assertNotNull(npTrees);
    assertTrue(npTrees.isEmpty());

    List<String> npPhrases = sentence.nounPhrases();
    assertNotNull(npPhrases);
    assertTrue(npPhrases.isEmpty());
  }
@Test
  public void testVerbPhraseTreeEmptyWhenNoVPExists() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    Tree tree = Tree.valueOf("(ROOT (NP (DT This) (NN test)))"); 
    when(coreMap.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(tree);

    CoreSentence sentence = new CoreSentence(document, coreMap);
    List<Tree> vpTrees = sentence.verbPhraseTrees();
    assertNotNull(vpTrees);
    assertTrue(vpTrees.isEmpty());

    List<String> vpPhrases = sentence.verbPhrases();
    assertNotNull(vpPhrases);
    assertTrue(vpPhrases.isEmpty());
  }
@Test
  public void testCharOffsetsThrowsIfMissingAnnotations() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);

    when(coreMap.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class)).thenReturn(null);
    when(coreMap.get(CoreAnnotations.CharacterOffsetEndAnnotation.class)).thenReturn(null);
    CoreSentence sentence = new CoreSentence(document, coreMap);

    assertThrows(NullPointerException.class, () -> {
      sentence.charOffsets();
    });
  }
@Test
  public void testTextReturnsNullIfTextAnnotationIsMissing() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);

    when(coreMap.get(CoreAnnotations.TextAnnotation.class)).thenReturn(null);

    CoreSentence sentence = new CoreSentence(document, coreMap);

    String result = sentence.text();
    assertNull(result);
  }
@Test
  public void testTregexResultsReturnsEmptyListForEmptyTree() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);

    Tree emptyTree = Tree.valueOf("()");
    when(coreMap.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(emptyTree);

    CoreSentence sentence = new CoreSentence(document, coreMap);

    TregexPattern pattern = TregexPattern.compile("NP");
    List<String> result = sentence.tregexResults(pattern);

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
@Test
  public void testTregexResultsReturnsMultipleMatches() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);

    Tree tree = Tree.valueOf("(ROOT (S (NP (DT A) (NN cat)) (VP (VBZ sat)) (NP (DT the) (NN mat))))");
    when(coreMap.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(tree);

    CoreSentence sentence = new CoreSentence(document, coreMap);

    List<String> results = sentence.tregexResults("NP");

    assertEquals(2, results.size());
    assertEquals("A cat", results.get(0));
    assertEquals("the mat", results.get(1));
  }
@Test
  public void testTokenWithNullTagStillIncludedInPosTags() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);

    CoreLabel token = mock(CoreLabel.class);
    when(token.tag()).thenReturn(null);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    when(coreMap.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    CoreSentence sentence = new CoreSentence(document, coreMap);

    List<String> posTags = sentence.posTags();
    assertEquals(1, posTags.size());
    assertNull(posTags.get(0));
  }
@Test
  public void testTokenWithNullNERStillIncludedInNERTags() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);

    CoreLabel token = mock(CoreLabel.class);
    when(token.ner()).thenReturn(null);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    when(coreMap.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    CoreSentence sentence = new CoreSentence(document, coreMap);

    List<String> nerTags = sentence.nerTags();
    assertEquals(1, nerTags.size());
    assertNull(nerTags.get(0));
  }
@Test
  public void testTokensAsStringsHandlesSingleNullTokenGracefully() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);

    CoreLabel token = mock(CoreLabel.class);
    when(token.word()).thenReturn(null);

    List<CoreLabel> tokens = Collections.singletonList(token);
    when(coreMap.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    CoreSentence sentence = new CoreSentence(document, coreMap);

    List<String> tokenStrings = sentence.tokensAsStrings();
    assertEquals(1, tokenStrings.size());
    assertNull(tokenStrings.get(0));
  }
@Test
  public void testCharOffsetsHandlesZeroOffsetsCorrectly() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);

    when(coreMap.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class)).thenReturn(0);
    when(coreMap.get(CoreAnnotations.CharacterOffsetEndAnnotation.class)).thenReturn(0);

    CoreSentence sentence = new CoreSentence(document, coreMap);

    Pair<Integer, Integer> offsets = sentence.charOffsets();
    assertNotNull(offsets);
    assertEquals((Integer) 0, offsets.first);
    assertEquals((Integer) 0, offsets.second);
  }
@Test
  public void testWrapEntityMentionsReturnsEmptyWhenMentionListContainsNullElements() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);

    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(null);
    mentions.add(null);

    when(coreMap.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(mentions);

    CoreSentence sentence = new CoreSentence(document, coreMap);
    sentence.wrapEntityMentions();

    List<CoreEntityMention> result = sentence.entityMentions();
    assertEquals(2, result.size());
    assertNull(result.get(0));
    assertNull(result.get(1));
  }
@Test
  public void testTregexResultsWithInvalidPatternThrowsException() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);

    Tree tree = Tree.valueOf("(ROOT (S (NP (DT The) (NN dog)) (VP (VBZ sleeps))))");
    when(coreMap.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(tree);

    CoreSentence sentence = new CoreSentence(document, coreMap);

    try {
      sentence.tregexResults("*INVALID("); 
      fail("Expected RuntimeException due to invalid regex");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("Illegal"));
    }
  }
@Test
  public void testWrapEntityMentionsHandlesMixedNullAndValidMentions() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);

    CoreMap mention1 = mock(CoreMap.class);
    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(mention1);
    mentions.add(null); 

    when(coreMap.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(mentions);
    CoreSentence sentence = new CoreSentence(document, coreMap);
    sentence.wrapEntityMentions();

    List<CoreEntityMention> result = sentence.entityMentions();
    assertEquals(2, result.size());
    assertNotNull(result.get(0));
    assertNull(result.get(1));
  }
@Test
  public void testTokensAsStringsHandlesEmptyTokenList() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    when(coreMap.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.emptyList());

    CoreSentence sentence = new CoreSentence(document, coreMap);
    List<String> tokenStrings = sentence.tokensAsStrings();

    assertNotNull(tokenStrings);
    assertEquals(0, tokenStrings.size());
  }
@Test
  public void testPosTagsHandlesEmptyTokenList() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    when(coreMap.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.emptyList());

    CoreSentence sentence = new CoreSentence(document, coreMap);
    List<String> tags = sentence.posTags();

    assertNotNull(tags);
    assertEquals(0, tags.size());
  }
@Test
  public void testLemmasHandlesEmptyTokenList() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    when(coreMap.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.emptyList());

    CoreSentence sentence = new CoreSentence(document, coreMap);
    List<String> lemmas = sentence.lemmas();

    assertNotNull(lemmas);
    assertEquals(0, lemmas.size());
  }
@Test
  public void testNerTagsHandlesEmptyTokenList() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    when(coreMap.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.emptyList());

    CoreSentence sentence = new CoreSentence(document, coreMap);
    List<String> nerTags = sentence.nerTags();

    assertNotNull(nerTags);
    assertEquals(0, nerTags.size());
  }
@Test
  public void testTregexResultTreesHandlesUnusualTreeStructure() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    Tree malformedTree = Tree.valueOf("(ROOT (X (ZZ nowhere)))");

    when(coreMap.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(malformedTree);
    CoreSentence sentence = new CoreSentence(document, coreMap);

    TregexPattern pattern = TregexPattern.compile("NP");
    List<Tree> result = sentence.tregexResultTrees(pattern);

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
@Test
  public void testTregexResultTreesHandlesEmptyStringPattern() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    Tree tree = Tree.valueOf("(ROOT (NP (DT The) (NN cat)))");

    when(coreMap.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(tree);
    CoreSentence sentence = new CoreSentence(document, coreMap);

    try {
      sentence.tregexResultTrees("");
      fail("Expected exception for empty pattern");
    } catch (Exception e) {
      assertNotNull(e.getMessage());
    }
  }
@Test
  public void testCharOffsetsHandlesNegativeValues() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);

    when(coreMap.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class)).thenReturn(-1);
    when(coreMap.get(CoreAnnotations.CharacterOffsetEndAnnotation.class)).thenReturn(-10);

    CoreSentence sentence = new CoreSentence(document, coreMap);
    Pair<Integer, Integer> offsets = sentence.charOffsets();

    assertEquals((Integer)(-1), offsets.first);
    assertEquals((Integer)(-10), offsets.second);
  }
@Test
  public void testCharOffsetsThrowsWhenAnnotationsAreMissing() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);

    when(coreMap.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class)).thenReturn(null);
    when(coreMap.get(CoreAnnotations.CharacterOffsetEndAnnotation.class)).thenReturn(null);

    CoreSentence sentence = new CoreSentence(document, coreMap);
    try {
      sentence.charOffsets();
      fail("Expected NullPointerException due to missing offset annotations");
    } catch (NullPointerException e) {
      assertNotNull(e);
    }
  }
@Test
  public void testTregexResultsReturnsEmptyWhenTreeIsNull() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    when(coreMap.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(null);

    CoreSentence sentence = new CoreSentence(document, coreMap);

    try {
      sentence.tregexResults("NP");
      fail("Expected RuntimeException due to null parse tree");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("Error: Attempted to run Tregex on sentence without a constituency parse"));
    }
  }
@Test
  public void testNounPhrasesReturnsEmptyIfNoMatchesFound() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    Tree tree = Tree.valueOf("(ROOT (VP (VBZ sleeps)))");

    when(coreMap.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(tree);

    CoreSentence sentence = new CoreSentence(document, coreMap);
    List<String> phrases = sentence.nounPhrases();

    assertNotNull(phrases);
    assertTrue(phrases.isEmpty());
  }
@Test
  public void testVerbPhrasesReturnsEmptyIfNoMatchesFound() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    Tree tree = Tree.valueOf("(ROOT (NP (DT The) (NN table)))");

    when(coreMap.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(tree);

    CoreSentence sentence = new CoreSentence(document, coreMap);
    List<String> phrases = sentence.verbPhrases();

    assertNotNull(phrases);
    assertTrue(phrases.isEmpty());
  }
@Test
  public void testWrapEntityMentionsHandlesEmptyListSafely() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    when(coreMap.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(new ArrayList<CoreMap>());

    CoreSentence sentence = new CoreSentence(document, coreMap);
    sentence.wrapEntityMentions();

    List<CoreEntityMention> mentions = sentence.entityMentions();
    assertNotNull(mentions);
    assertTrue(mentions.isEmpty());
  }
@Test
  public void testTregexResultsHandlesRedundantCallsWithSamePattern() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);

    Tree tree = Tree.valueOf("(ROOT (S (NP (DT A) (NN cat)) (VP (VBZ sits))))");
    when(coreMap.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(tree);

    CoreSentence sentence = new CoreSentence(document, coreMap);

    List<String> results1 = sentence.tregexResults("NP");
    List<String> results2 = sentence.tregexResults("NP");

    assertEquals(1, results1.size());
    assertEquals("A cat", results1.get(0));
    assertEquals("A cat", results2.get(0));
  }
@Test
  public void testSentimentReturnsNullGracefullyWhenAnnotationMissing() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    when(coreMap.get(SentimentCoreAnnotations.SentimentClass.class)).thenReturn(null);

    CoreSentence sentence = new CoreSentence(document, coreMap);
    String sentiment = sentence.sentiment();

    assertNull(sentiment);
  }
@Test
  public void testSentimentTreeReturnsNullWhenNotPresent() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    when(coreMap.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class)).thenReturn(null);

    CoreSentence sentence = new CoreSentence(document, coreMap);
    Tree result = sentence.sentimentTree();

    assertNull(result);
  }
@Test
  public void testRelationTriplesReturnsNullIfAnnotationMissing() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    when(coreMap.get(CoreAnnotations.KBPTriplesAnnotation.class)).thenReturn(null);

    CoreSentence sentence = new CoreSentence(document, coreMap);
    List result = sentence.relations();

    assertNull(result);
  }
@Test
  public void testTregexResultsHandlesTreeWithNoMatch() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    Tree tree = Tree.valueOf("(ROOT (S (VP (VBZ sleeps))))");
    when(coreMap.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(tree);

    CoreSentence sentence = new CoreSentence(document, coreMap);
    List<String> results = sentence.tregexResults("NP");

    assertNotNull(results);
    assertTrue(results.isEmpty());
  }
@Test
  public void testToStringReturnsCoreMapToStringValue() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    when(coreMap.toString()).thenReturn("Mock CoreMap");

    CoreSentence sentence = new CoreSentence(document, coreMap);
    String result = sentence.toString();

    assertEquals("Mock CoreMap", result);
  }
@Test
  public void testCharOffsetsWithNullStartOffsetThrowsException() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    when(coreMap.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class)).thenReturn(null);
    when(coreMap.get(CoreAnnotations.CharacterOffsetEndAnnotation.class)).thenReturn(10);

    CoreSentence sentence = new CoreSentence(document, coreMap);
    try {
      sentence.charOffsets();
      fail("Expected NullPointerException due to null start offset");
    } catch (NullPointerException e) {
      assertNotNull(e);
    }
  }
@Test
  public void testCharOffsetsWithNullEndOffsetThrowsException() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    when(coreMap.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class)).thenReturn(0);
    when(coreMap.get(CoreAnnotations.CharacterOffsetEndAnnotation.class)).thenReturn(null);

    CoreSentence sentence = new CoreSentence(document, coreMap);
    try {
      sentence.charOffsets();
      fail("Expected NullPointerException due to null end offset");
    } catch (NullPointerException e) {
      assertNotNull(e);
    }
  }
@Test
  public void testEmptyTreeDoesNotMatchAnyPattern() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    Tree tree = Tree.valueOf("(ROOT ())");
    when(coreMap.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(tree);

    CoreSentence sentence = new CoreSentence(document, coreMap);
    List<Tree> matches = sentence.tregexResultTrees("NP");

    assertNotNull(matches);
    assertTrue(matches.isEmpty());
  }
@Test
  public void testTregexResultsThrowsRuntimeExceptionOnAbsentTree() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    when(coreMap.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(null);

    CoreSentence sentence = new CoreSentence(document, coreMap);
    try {
      sentence.tregexResults("NP");
      fail("Expected RuntimeException due to missing parse tree");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("Attempted to run Tregex on sentence without a constituency parse"));
    }
  }
@Test
  public void testTokensAnnotationReturnsNull() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    when(coreMap.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(null);

    CoreSentence sentence = new CoreSentence(document, coreMap);
    List<edu.stanford.nlp.ling.CoreLabel> results = sentence.tokens();

    assertNull(results);
  }
@Test
  public void testTregexResultsEmptyDueToInvalidPattern() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    Tree tree = Tree.valueOf("(ROOT (NP (DT The) (NN cat)))");
    when(coreMap.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(tree);

    CoreSentence sentence = new CoreSentence(document, coreMap);

    try {
      sentence.tregexResults("*[invalid"); 
      fail("Expected RuntimeException due to invalid Tregex pattern");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().toLowerCase().contains("illegal") || e.getMessage().toLowerCase().contains("syntax"));
    }
  }
@Test
  public void testWrapEntityMentionsHandlesListWithOneNullAndOneValid() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    CoreMap validMention = mock(CoreMap.class);

    List<CoreMap> mentions = new ArrayList<CoreMap>();
    mentions.add(validMention);
    mentions.add(null);

    when(coreMap.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(mentions);

    CoreSentence sentence = new CoreSentence(document, coreMap);
    sentence.wrapEntityMentions();

    List<CoreEntityMention> result = sentence.entityMentions();

    assertEquals(2, result.size());
    assertNotNull(result.get(0));
    assertNull(result.get(1));
  }
@Test
  public void testEmptyTreeProducesNoTregexMatches() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    Tree tree = Tree.valueOf("(ROOT ())");

    when(coreMap.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(tree);

    CoreSentence sentence = new CoreSentence(document, coreMap);
    List<String> matches = sentence.tregexResults("NP");

    assertNotNull(matches);
    assertEquals(0, matches.size());
  }
@Test
  public void testNounPhrasesEmptyIfTreeMissingNP() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    Tree tree = Tree.valueOf("(ROOT (S (VP (VBZ sleeps))))");

    when(coreMap.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(tree);

    CoreSentence sentence = new CoreSentence(document, coreMap);

    List<String> result = sentence.nounPhrases();

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
@Test
  public void testVerbPhrasesEmptyIfTreeMissingVP() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    Tree tree = Tree.valueOf("(ROOT (S (NP (NN test))))");

    when(coreMap.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(tree);

    CoreSentence sentence = new CoreSentence(document, coreMap);

    List<String> result = sentence.verbPhrases();

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
@Test
  public void testTokenWithNullFieldsInTokenList() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);

    CoreLabel token = mock(CoreLabel.class);
    when(token.word()).thenReturn(null);
    when(token.tag()).thenReturn(null);
    when(token.lemma()).thenReturn(null);
    when(token.ner()).thenReturn(null);

    List<CoreLabel> tokens = Arrays.asList(token);
    when(coreMap.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    CoreSentence sentence = new CoreSentence(document, coreMap);

    List<String> words = sentence.tokensAsStrings();
    List<String> tags = sentence.posTags();
    List<String> lemmas = sentence.lemmas();
    List<String> ner = sentence.nerTags();

    assertEquals(1, words.size());
    assertNull(words.get(0));

    assertEquals(1, tags.size());
    assertNull(tags.get(0));

    assertEquals(1, lemmas.size());
    assertNull(lemmas.get(0));

    assertEquals(1, ner.size());
    assertNull(ner.get(0));
  }
@Test
  public void testConstituencyParseReturnsNullIfMissing() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    when(coreMap.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(null);

    CoreSentence sentence = new CoreSentence(document, coreMap);

    Tree tree = sentence.constituencyParse();
    assertNull(tree);
  }
@Test
  public void testDependencyParseReturnsNullIfMissing() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    when(coreMap.get(any())).thenReturn(null); 

    CoreSentence sentence = new CoreSentence(document, coreMap);

    assertNull(sentence.dependencyParse());
  }
@Test
  public void testNounPhraseTreesEmptyForEmptyParseTree() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    Tree tree = Tree.valueOf("(ROOT ())");
    when(coreMap.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(tree);

    CoreSentence sentence = new CoreSentence(document, coreMap);
    List<Tree> result = sentence.nounPhraseTrees();

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
@Test
  public void testCharOffsetsWhenCharOffsetsAreNegative() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);

    when(coreMap.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class)).thenReturn(-5);
    when(coreMap.get(CoreAnnotations.CharacterOffsetEndAnnotation.class)).thenReturn(-1);

    CoreSentence sentence = new CoreSentence(document, coreMap);
    Pair<Integer, Integer> offsets = sentence.charOffsets();

    assertEquals((Integer)(-5), offsets.first);
    assertEquals((Integer)(-1), offsets.second);
  }
@Test
  public void testWrapEntityMentionsWithOnlyNullListElement() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);

    List<CoreMap> mentions = new ArrayList<CoreMap>();
    mentions.add(null);

    when(coreMap.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(mentions);

    CoreSentence sentence = new CoreSentence(document, coreMap);
    sentence.wrapEntityMentions();

    List<CoreEntityMention> result = sentence.entityMentions();

    assertEquals(1, result.size());
    assertNull(result.get(0));
  }
@Test
  public void testTregexResultsHandlesRepeatedRequestsWithoutError() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    Tree tree = Tree.valueOf("(ROOT (S (NP (DT This) (NN test))))");

    when(coreMap.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(tree);

    CoreSentence sentence = new CoreSentence(document, coreMap);

    List<String> firstCall = sentence.tregexResults("NP");
    List<String> secondCall = sentence.tregexResults("NP");

    assertEquals(1, firstCall.size());
    assertEquals(1, secondCall.size());
    assertEquals("This test", firstCall.get(0));
    assertEquals("This test", secondCall.get(0));
  }
@Test
  public void testTokenListWithOnlyOneTokenWithPartialNulls() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);

    CoreLabel token = mock(CoreLabel.class);
    when(token.word()).thenReturn("Word");
    when(token.tag()).thenReturn(null);
    when(token.lemma()).thenReturn("lemma");
    when(token.ner()).thenReturn(null);

    List<CoreLabel> tokens = Arrays.asList(token);
    when(coreMap.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    CoreSentence sentence = new CoreSentence(document, coreMap);

    List<String> words = sentence.tokensAsStrings();
    List<String> tags = sentence.posTags();
    List<String> lemmas = sentence.lemmas();
    List<String> nerTags = sentence.nerTags();

    assertEquals(1, words.size());
    assertEquals("Word", words.get(0));
    assertEquals(1, tags.size());
    assertNull(tags.get(0));
    assertEquals(1, lemmas.size());
    assertEquals("lemma", lemmas.get(0));
    assertEquals(1, nerTags.size());
    assertNull(nerTags.get(0));
  }
@Test
  public void testTokensAsStringsWithTokensHavingEmptyStringWords() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);

    CoreLabel token = mock(CoreLabel.class);
    when(token.word()).thenReturn("");

    when(coreMap.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(token));

    CoreSentence sentence = new CoreSentence(document, coreMap);
    List<String> result = sentence.tokensAsStrings();

    assertEquals(1, result.size());
    assertEquals("", result.get(0));
  }
@Test
  public void testConstituencyParseWithDeeplyNestedTree() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);

    Tree tree = Tree.valueOf("(ROOT (S (NP (NP (NP (DT The) (NN cat))))) (VP (VBZ sleeps)))");

    when(coreMap.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(tree);

    CoreSentence sentence = new CoreSentence(document, coreMap);

    Tree resultTree = sentence.constituencyParse();
    assertEquals(tree, resultTree);
  }
@Test
  public void testTregexResultTreesReturnsMultipleNodesFromPattern() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);

    Tree tree = Tree.valueOf("(ROOT (S (NP (DT This) (NN test)) (VP (VBZ runs) (NP (DT the) (NN code)))))");
    when(coreMap.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(tree);

    CoreSentence sentence = new CoreSentence(document, coreMap);

    TregexPattern pattern = TregexPattern.compile("NP");

    List<Tree> matchedTrees = sentence.tregexResultTrees(pattern);
    List<String> matchedStrings = sentence.tregexResults(pattern);

    assertEquals(2, matchedTrees.size());
    assertEquals(2, matchedStrings.size());
    assertEquals("This test", matchedStrings.get(0));
    assertEquals("the code", matchedStrings.get(1));
  }
@Test
  public void testWrapEntityMentionsCalledTwiceConsistently() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);

    CoreMap entity = mock(CoreMap.class);
    when(coreMap.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.singletonList(entity));

    CoreSentence sentence = new CoreSentence(document, coreMap);
    sentence.wrapEntityMentions();
    List<CoreEntityMention> firstCall = sentence.entityMentions();

    sentence.wrapEntityMentions(); 
    List<CoreEntityMention> secondCall = sentence.entityMentions();

    assertNotNull(firstCall);
    assertNotNull(secondCall);
    assertEquals(1, firstCall.size());
    assertEquals(1, secondCall.size());
    assertSame(firstCall.get(0), secondCall.get(0));
  }
@Test
  public void testEmptyMentionListStillInitializesEntityMentionsAsEmpty() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);

    when(coreMap.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.emptyList());

    CoreSentence sentence = new CoreSentence(document, coreMap);
    sentence.wrapEntityMentions();
    List<CoreEntityMention> mentions = sentence.entityMentions();

    assertNotNull(mentions);
    assertTrue(mentions.isEmpty());
  }
@Test
  public void testEmptyDependencyParseAnnotationReturnsNull() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);

    when(coreMap.get(any())).thenReturn(null);
    CoreSentence sentence = new CoreSentence(document, coreMap);

    assertNull(sentence.dependencyParse());
  }
@Test
  public void testInvalidTregexPatternThrowsException() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    Tree tree = Tree.valueOf("(ROOT (NP (DT The) (NN animal)))");

    when(coreMap.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(tree);
    CoreSentence sentence = new CoreSentence(document, coreMap);

    try {
      sentence.tregexResults("*invalid(pattern");
      fail("Expected RuntimeException due to invalid Tregex pattern");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().toLowerCase().contains("illegal") || e.getMessage().toLowerCase().contains("syntax"));
    }
  }
@Test
  public void testToStringMatchesCoreMapToString() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);

    when(coreMap.toString()).thenReturn("FakeCoreMap");

    CoreSentence sentence = new CoreSentence(document, coreMap);

    String result = sentence.toString();
    assertEquals("FakeCoreMap", result);
  }
@Test
  public void testCharOffsetsWithZeroOffsets() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    when(coreMap.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class)).thenReturn(0);
    when(coreMap.get(CoreAnnotations.CharacterOffsetEndAnnotation.class)).thenReturn(0);

    CoreSentence sentence = new CoreSentence(document, coreMap);
    Pair<Integer, Integer> offsets = sentence.charOffsets();

    assertEquals(Integer.valueOf(0), offsets.first);
    assertEquals(Integer.valueOf(0), offsets.second);
  }
@Test
  public void testCharOffsetsWithBeginGreaterThanEnd() {
    CoreMap coreMap = mock(CoreMap.class);
    CoreDocument document = mock(CoreDocument.class);
    when(coreMap.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class)).thenReturn(20);
    when(coreMap.get(CoreAnnotations.CharacterOffsetEndAnnotation.class)).thenReturn(10);

    CoreSentence sentence = new CoreSentence(document, coreMap);
    Pair<Integer, Integer> offsets = sentence.charOffsets();

    assertEquals(Integer.valueOf(20), offsets.first);
    assertEquals(Integer.valueOf(10), offsets.second);
  } 
}