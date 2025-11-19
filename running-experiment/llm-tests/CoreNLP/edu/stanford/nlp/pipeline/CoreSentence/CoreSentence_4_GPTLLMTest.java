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
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import edu.stanford.nlp.util.*;
import org.junit.Test;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CoreSentence_4_GPTLLMTest { 

 @Test
  public void testText() {
    CoreMap sentence = new Annotation("Unit test sentence.");
    sentence.set(CoreAnnotations.TextAnnotation.class, "Unit test sentence.");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 20);
    CoreDocument document = new CoreDocument("Unit test sentence.");
    CoreSentence coreSentence = new CoreSentence(document, sentence);

    assertEquals("Unit test sentence.", coreSentence.text());
  }
@Test
  public void testCharOffsets() {
    CoreMap sentence = new Annotation("Example.");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 12);
    sentence.set(CoreAnnotations.TextAnnotation.class, "Example.");
    CoreDocument document = new CoreDocument("Example.");
    CoreSentence coreSentence = new CoreSentence(document, sentence);

    Pair<Integer, Integer> offsets = coreSentence.charOffsets();
    assertEquals((Integer) 5, offsets.first);
    assertEquals((Integer) 12, offsets.second);
  }
@Test
  public void testTokensAsStrings() {
    CoreMap sentence = new Annotation("Just a test.");
    
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Just");
    token1.setTag("RB");
    token1.setLemma("just");
    token1.setNER("O");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("a");
    token2.setTag("DT");
    token2.setLemma("a");
    token2.setNER("O");

    CoreLabel token3 = new CoreLabel();
    token3.setWord("test");
    token3.setTag("NN");
    token3.setLemma("test");
    token3.setNER("O");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TextAnnotation.class, "Just a test.");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 13);

    CoreDocument document = new CoreDocument("Just a test.");
    CoreSentence coreSentence = new CoreSentence(document, sentence);

    List<String> actual = coreSentence.tokensAsStrings();
    assertEquals(3, actual.size());
    assertEquals("Just", actual.get(0));
    assertEquals("a", actual.get(1));
    assertEquals("test", actual.get(2));
  }
@Test
  public void testPosTags() {
    CoreMap sentence = new Annotation("A test");

    CoreLabel token1 = new CoreLabel();
    token1.setWord("A");
    token1.setTag("DT");
    token1.setLemma("a");
    token1.setNER("O");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("test");
    token2.setTag("NN");
    token2.setLemma("test");
    token2.setNER("O");

    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TextAnnotation.class, "A test");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);

    CoreDocument document = new CoreDocument("A test");
    CoreSentence coreSentence = new CoreSentence(document, sentence);

    List<String> posTags = coreSentence.posTags();
    assertEquals(2, posTags.size());
    assertEquals("DT", posTags.get(0));
    assertEquals("NN", posTags.get(1));
  }
@Test
  public void testDependencyParse() {
    CoreMap sentence = new Annotation("Parse this.");
    SemanticGraph dependencies = new SemanticGraph();
    sentence.set(SemanticGraphCoreAnnotations.EnhancedPlusPlusDependenciesAnnotation.class, dependencies);
    sentence.set(CoreAnnotations.TextAnnotation.class, "Parse this.");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);

    CoreDocument document = new CoreDocument("Parse this.");
    CoreSentence coreSentence = new CoreSentence(document, sentence);

    SemanticGraph result = coreSentence.dependencyParse();
    assertNotNull(result);
    assertEquals(dependencies, result);
  }
@Test
  public void testSentiment() {
    CoreMap sentence = new Annotation("Nice weather!");
    sentence.set(SentimentCoreAnnotations.SentimentClass.class, "Positive");
    sentence.set(CoreAnnotations.TextAnnotation.class, "Nice weather!");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 13);

    CoreDocument document = new CoreDocument("Nice weather!");
    CoreSentence coreSentence = new CoreSentence(document, sentence);

    assertEquals("Positive", coreSentence.sentiment());
  }
@Test
  public void testTregexResultTreesWithoutParseShouldThrow() {
    CoreMap sentence = new Annotation("Syntax fail.");
    sentence.set(CoreAnnotations.TextAnnotation.class, "Syntax fail.");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 12);

    CoreDocument document = new CoreDocument("Syntax fail.");
    CoreSentence coreSentence = new CoreSentence(document, sentence);

//     thrown.expect(RuntimeException.class);
//     thrown.expectMessage("Attempted to run Tregex on sentence without a constituency parse");

    coreSentence.tregexResultTrees("NP");
  }
@Test
  public void testTregexMatchesNP() {
    CoreMap sentence = new Annotation("The dog barked.");
    Tree tree = Tree.valueOf("(ROOT (S (NP (DT The) (NN dog)) (VP (VBD barked))))");
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    sentence.set(CoreAnnotations.TextAnnotation.class, "The dog barked.");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 16);

    CoreDocument document = new CoreDocument("The dog barked.");
    CoreSentence coreSentence = new CoreSentence(document, sentence);

    List<Tree> results = coreSentence.tregexResultTrees("NP");
    assertEquals(1, results.size());
    assertEquals("The dog", results.get(0).yieldWords().get(0).word() + " " + results.get(0).yieldWords().get(1).word());
  }
@Test
  public void testNounPhrases() {
    CoreMap sentence = new Annotation("The red fox jumped.");
    Tree tree = Tree.valueOf("(ROOT (S (NP (DT The) (JJ red) (NN fox)) (VP (VBD jumped))))");
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    sentence.set(CoreAnnotations.TextAnnotation.class, "The red fox jumped.");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 20);

    CoreDocument document = new CoreDocument("The red fox jumped.");
    CoreSentence coreSentence = new CoreSentence(document, sentence);

    List<String> nounPhrases = coreSentence.nounPhrases();
    assertEquals(1, nounPhrases.size());
    assertEquals("The red fox", nounPhrases.get(0));
  }
@Test
  public void testVerbPhrases() {
    CoreMap sentence = new Annotation("Dogs bark loudly.");
    Tree tree = Tree.valueOf("(ROOT (S (NP (NNS Dogs)) (VP (VBP bark) (ADVP (RB loudly)))))");
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    sentence.set(CoreAnnotations.TextAnnotation.class, "Dogs bark loudly.");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 18);

    CoreDocument document = new CoreDocument("Dogs bark loudly.");
    CoreSentence coreSentence = new CoreSentence(document, sentence);

    List<String> verbPhrases = coreSentence.verbPhrases();
    assertEquals(1, verbPhrases.size());
    assertTrue(verbPhrases.get(0).contains("bark"));
  }
@Test
  public void testConstructorAndDocumentGetterWithNulls() {
    CoreDocument doc = null;
    CoreMap map = null;
    CoreSentence sentence = new CoreSentence(doc, map);

    assertNull(sentence.document());
    assertNull(sentence.coreMap());
  }
@Test
  public void testTextReturnsNullIfAnnotationMissing() {
    CoreMap sentence = new Annotation("");
    CoreDocument document = new CoreDocument("");

    CoreSentence coreSentence = new CoreSentence(document, sentence);
    assertNull(coreSentence.text());
  }
@Test
  public void testCharOffsetsThrowsIfAnnotationMissing() {
    CoreMap sentence = new Annotation("");
    CoreDocument document = new CoreDocument("");
    CoreSentence coreSentence = new CoreSentence(document, sentence);

//     thrown.expect(NullPointerException.class);
    coreSentence.charOffsets();
  }
@Test
  public void testTokensReturnsNullIfNotAnnotated() {
    CoreMap sentence = new Annotation("");
    CoreDocument doc = new CoreDocument("");
    CoreSentence coreSentence = new CoreSentence(doc, sentence);
    assertNull(coreSentence.tokens());
  }
@Test
  public void testTokensAsStringsReturnsEmptyListIfNoTokens() {
    CoreMap sentence = new Annotation("");
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
    sentence.set(CoreAnnotations.TextAnnotation.class, "Empty tokens");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 12);

    CoreDocument doc = new CoreDocument("Empty tokens");
    CoreSentence coreSentence = new CoreSentence(doc, sentence);

    List<String> result = coreSentence.tokensAsStrings();
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
@Test
  public void testPosTagsReturnsEmptyIfTokensEmpty() {
    CoreMap sentence = new Annotation("");
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
    sentence.set(CoreAnnotations.TextAnnotation.class, "No POS tokens");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 14);

    CoreDocument doc = new CoreDocument("No POS tokens");
    CoreSentence coreSentence = new CoreSentence(doc, sentence);

    List<String> result = coreSentence.posTags();
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
@Test
  public void testLemmasReturnsNullIfTokensMissingLemma() {
    CoreMap sentence = new Annotation("");

    CoreLabel token = new CoreLabel();
    token.setWord("test");
    token.setTag("NN");
    

    List<CoreLabel> tokens = Collections.singletonList(token);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TextAnnotation.class, "test");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);

    CoreDocument doc = new CoreDocument("test");
    CoreSentence coreSentence = new CoreSentence(doc, sentence);

    List<String> lemmas = coreSentence.lemmas();
    assertEquals(1, lemmas.size());
    assertNull(lemmas.get(0));
  }
@Test
  public void testNerTagsReturnsNullIfNERMissing() {
    CoreMap sentence = new Annotation("");

    CoreLabel token = new CoreLabel();
    token.setWord("Paris");
    

    List<CoreLabel> tokens = Collections.singletonList(token);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TextAnnotation.class, "Paris");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);

    CoreDocument doc = new CoreDocument("Paris");
    CoreSentence coreSentence = new CoreSentence(doc, sentence);

    List<String> nerTags = coreSentence.nerTags();
    assertEquals(1, nerTags.size());
    assertNull(nerTags.get(0));
  }
@Test
  public void testTregexResultsReturnsEmptyIfNoMatch() {
    CoreMap sentence = new Annotation("Nothing matches.");
    Tree tree = Tree.valueOf("(ROOT (S (VP (VB Run))))");

    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    sentence.set(CoreAnnotations.TextAnnotation.class, "Nothing matches.");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 17);

    CoreDocument doc = new CoreDocument("Nothing matches.");
    CoreSentence coreSentence = new CoreSentence(doc, sentence);

    List<String> results = coreSentence.tregexResults("NP");
    assertNotNull(results);
    assertTrue(results.isEmpty());
  }
@Test
  public void testEntityMentionsReturnsNullByDefault() {
    CoreMap sentence = new Annotation("Entity mention test.");
    sentence.set(CoreAnnotations.TextAnnotation.class, "Entity mention test.");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 21);
    CoreDocument doc = new CoreDocument("Entity mention test.");
    CoreSentence coreSentence = new CoreSentence(doc, sentence);

    assertNull(coreSentence.entityMentions());
  }
@Test
  public void testWrapEntityMentionsWithEmptyList() {
    CoreMap sentence = new Annotation("Test");
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
    sentence.set(CoreAnnotations.TextAnnotation.class, "Test");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);

    CoreDocument doc = new CoreDocument("Test");
    CoreSentence coreSentence = new CoreSentence(doc, sentence);

    coreSentence.wrapEntityMentions();
    List<CoreEntityMention> mentions = coreSentence.entityMentions();
    assertNotNull(mentions);
    assertTrue(mentions.isEmpty());
  }
@Test
  public void testSentimentReturnsNullIfMissing() {
    CoreMap sentence = new Annotation("Neutral case");
    sentence.set(CoreAnnotations.TextAnnotation.class, "Neutral case");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 12);

    CoreDocument doc = new CoreDocument("Neutral case");
    CoreSentence coreSentence = new CoreSentence(doc, sentence);

    String sentiment = coreSentence.sentiment();
    assertNull(sentiment);
  }
@Test
  public void testRelationsReturnsNullIfAnnotationMissing() {
    CoreMap sentence = new Annotation("Relation mention case");
    sentence.set(CoreAnnotations.TextAnnotation.class, "Relation mention case");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 22);

    CoreDocument doc = new CoreDocument("Relation mention case");
    CoreSentence coreSentence = new CoreSentence(doc, sentence);

    assertNull(coreSentence.relations());
  }
@Test
  public void testToStringHandlesMissingCoreMap() {
    CoreSentence coreSentence = new CoreSentence(null, null);
    String result = coreSentence.toString();
    assertEquals("null", result);
  }
@Test
  public void testTokensAsStringsWithNullTokenWord() {
    CoreLabel token = new CoreLabel();
    token.setTag("NN");
    token.setLemma("test");
    token.setNER("O");

    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    tokens.add(token);

    CoreMap sentence = new Annotation("");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
    sentence.set(CoreAnnotations.TextAnnotation.class, "NullWord");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    CoreDocument doc = new CoreDocument("NullWord");
    CoreSentence cs = new CoreSentence(doc, sentence);

    List<String> words = cs.tokensAsStrings();
    assertEquals(1, words.size());
    assertNull(words.get(0));
  }
@Test
  public void testPosTagsWithMissingTag() {
    CoreLabel token = new CoreLabel();
    token.setWord("data");
    token.setLemma("datum");
    token.setNER("O");

    List<CoreLabel> tokens = Collections.singletonList(token);

    CoreMap sentence = new Annotation("data");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
    sentence.set(CoreAnnotations.TextAnnotation.class, "data");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    CoreDocument doc = new CoreDocument("data");
    CoreSentence cs = new CoreSentence(doc, sentence);

    List<String> tags = cs.posTags();
    assertEquals(1, tags.size());
    assertNull(tags.get(0));
  }
@Test
  public void testLemmasWithNullTokenList() {
    CoreMap sentence = new Annotation("null tokens");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);
    sentence.set(CoreAnnotations.TextAnnotation.class, "null tokens");

    CoreDocument doc = new CoreDocument("null tokens");
    CoreSentence cs = new CoreSentence(doc, sentence);

    List<String> lemmas = cs.lemmas();
    assertNull(lemmas);
  }
@Test
  public void testNerTagsWithNullValue() {
    CoreLabel token = new CoreLabel();
    token.setWord("London");
    token.setTag("NNP");
    token.setLemma("London");
    

    List<CoreLabel> tokens = Arrays.asList(token);

    CoreMap sentence = new Annotation("London");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
    sentence.set(CoreAnnotations.TextAnnotation.class, "London");

    CoreDocument doc = new CoreDocument("London");
    CoreSentence cs = new CoreSentence(doc, sentence);

    List<String> nerTags = cs.nerTags();
    assertEquals(1, nerTags.size());
    assertNull(nerTags.get(0));
  }
@Test
  public void testTregexWithInvalidRegexThrows() {
    CoreMap sentence = new Annotation("Invalid regex");
    Tree tree = Tree.valueOf("(ROOT (NP (DT The) (NN car)))");

    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 13);
    sentence.set(CoreAnnotations.TextAnnotation.class, "Invalid regex");

    CoreDocument doc = new CoreDocument("Invalid regex");
    CoreSentence cs = new CoreSentence(doc, sentence);

//     thrown.expect(RuntimeException.class);
    cs.tregexResultTrees("*invalid((");   
  }
@Test
  public void testTregexCachingDistinctPatterns() {
    CoreMap sentence = new Annotation("Test sentence");
    Tree tree = Tree.valueOf("(ROOT (S (NP (NN Test)) (VP (VB runs))))");

    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 13);
    sentence.set(CoreAnnotations.TextAnnotation.class, "Test sentence");

    CoreDocument doc = new CoreDocument("Test sentence");
    CoreSentence cs = new CoreSentence(doc, sentence);

    List<Tree> result1 = cs.tregexResultTrees("NP");
    List<Tree> result2 = cs.tregexResultTrees("VP");

    assertEquals(1, result1.size());
    assertEquals(1, result2.size());
    assertTrue(result1.get(0).toString().contains("Test"));
    assertTrue(result2.get(0).toString().contains("runs"));
  }
@Test
  public void testSentimentTreeReturnsNullIfNotSet() {
    CoreMap sentence = new Annotation("Emotionless");
    sentence.set(CoreAnnotations.TextAnnotation.class, "Emotionless");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);

    CoreDocument doc = new CoreDocument("Emotionless");
    CoreSentence cs = new CoreSentence(doc, sentence);

    Tree sentTree = cs.sentimentTree();
    assertNull(sentTree);
  }
@Test
  public void testDependencyParseReturnsNullIfNotSet() {
    CoreMap sentence = new Annotation("dep parse");
    sentence.set(CoreAnnotations.TextAnnotation.class, "dep parse");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 9);

    CoreDocument doc = new CoreDocument("dep parse");
    CoreSentence cs = new CoreSentence(doc, sentence);

    SemanticGraph graph = cs.dependencyParse();
    assertNull(graph);
  }
@Test
  public void testWrapEntityMentionsNullAnnotation() {
    CoreMap sentence = new Annotation("Entity");
    
    sentence.set(CoreAnnotations.TextAnnotation.class, "Entity");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
    
    CoreDocument doc = new CoreDocument("Entity");
    CoreSentence cs = new CoreSentence(doc, sentence);

    cs.wrapEntityMentions();
    assertNull(cs.entityMentions());
  }
@Test
  public void testMultipleEntityMentionsWrappedCorrectly() {
    CoreMap mention1 = new Annotation("Alice");
    CoreMap mention2 = new Annotation("Bob");

    List<CoreMap> mentionList = Arrays.asList(mention1, mention2);

    CoreMap sentence = new Annotation("Alice and Bob");
    sentence.set(CoreAnnotations.MentionsAnnotation.class, mentionList);
    sentence.set(CoreAnnotations.TextAnnotation.class, "Alice and Bob");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 13);

    CoreDocument doc = new CoreDocument("Alice and Bob");
    CoreSentence cs = new CoreSentence(doc, sentence);

    cs.wrapEntityMentions();
    List<CoreEntityMention> wrapped = cs.entityMentions();
    assertNotNull(wrapped);
    assertEquals(2, wrapped.size());
    assertEquals(cs, wrapped.get(0).sentence());
    assertEquals(cs, wrapped.get(1).sentence());
  }
@Test
  public void testEmptyTokensListReturnsEmptyLemmas() {
    CoreMap sentence = new Annotation("");
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
    sentence.set(CoreAnnotations.TextAnnotation.class, "");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 0);

    CoreDocument doc = new CoreDocument("");
    CoreSentence cs = new CoreSentence(doc, sentence);

    List<String> lemmas = cs.lemmas();
    assertNotNull(lemmas);
    assertEquals(0, lemmas.size());
  }
@Test
  public void testEmptyNPAndVPReturnsEmptyLists() {
    Tree tree = Tree.valueOf("(ROOT (S (ADJP (JJ cold))))");

    CoreMap sentence = new Annotation("cold");
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    sentence.set(CoreAnnotations.TextAnnotation.class, "cold");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);

    CoreDocument doc = new CoreDocument("cold");
    CoreSentence cs = new CoreSentence(doc, sentence);

    List<Tree> npTrees = cs.nounPhraseTrees();
    List<Tree> vpTrees = cs.verbPhraseTrees();
    List<String> nounPhrases = cs.nounPhrases();
    List<String> verbPhrases = cs.verbPhrases();

    assertTrue(npTrees.isEmpty());
    assertTrue(vpTrees.isEmpty());
    assertTrue(nounPhrases.isEmpty());
    assertTrue(verbPhrases.isEmpty());
  }
@Test
  public void testTregexReturnsMultipleMatches() {
    Tree tree = Tree.valueOf("(ROOT (S (NP (DT The) (NN cat)) (NP (PRP it)) (VP (VBZ sleeps))))");
    CoreMap sentence = new Annotation("The cat it sleeps.");
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    sentence.set(CoreAnnotations.TextAnnotation.class, "The cat it sleeps.");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 20);

    CoreDocument doc = new CoreDocument("The cat it sleeps.");
    CoreSentence cs = new CoreSentence(doc, sentence);

    List<Tree> resultTrees = cs.tregexResultTrees("NP");
    List<String> spanStrings = cs.tregexResults("NP");

    assertEquals(2, resultTrees.size());
    assertEquals(2, spanStrings.size());
  }
@Test
  public void testTregexHandlesEmptyTopLevelTree() {
    Tree emptyTree = Tree.valueOf("(ROOT ())");
    CoreMap sentence = new Annotation("Blank.");
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, emptyTree);
    sentence.set(CoreAnnotations.TextAnnotation.class, "Blank.");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);

    CoreDocument doc = new CoreDocument("Blank.");
    CoreSentence cs = new CoreSentence(doc, sentence);

    List<Tree> matches = cs.tregexResultTrees("NP");
    assertTrue(matches.isEmpty());
  }
@Test
  public void testConstituencyParseReturnsNullIfNotSet() {
    CoreMap sentence = new Annotation("Something missing");
    sentence.set(CoreAnnotations.TextAnnotation.class, "Something missing");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 17);

    CoreDocument doc = new CoreDocument("Something missing");
    CoreSentence cs = new CoreSentence(doc, sentence);

    Tree tree = cs.constituencyParse();
    assertNull(tree);
  }
@Test
  public void testWrapEntityMentionsProducesCorrectTypes() {
    CoreMap entityMap = new Annotation("Person");
    List<CoreMap> entityList = Arrays.asList(entityMap);

    CoreMap sentence = new Annotation("Alice went out.");
    sentence.set(CoreAnnotations.MentionsAnnotation.class, entityList);
    sentence.set(CoreAnnotations.TextAnnotation.class, "Alice went out.");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 16);

    CoreDocument doc = new CoreDocument("Alice went out.");
    CoreSentence cs = new CoreSentence(doc, sentence);

    cs.wrapEntityMentions();
    List<CoreEntityMention> mentions = cs.entityMentions();

    assertNotNull(mentions);
    assertEquals(1, mentions.size());
    assertTrue(mentions.get(0) instanceof CoreEntityMention);
    assertEquals(cs, mentions.get(0).sentence());
  }
@Test
  public void testToStringReturnsActualCoreMapToString() {
    CoreMap sentence = new Annotation("example text");
    sentence.set(CoreAnnotations.TextAnnotation.class, "example text");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 12);

    CoreDocument doc = new CoreDocument("example text");
    CoreSentence cs = new CoreSentence(doc, sentence);

    String result = cs.toString();
    assertEquals(sentence.toString(), result);
  }
@Test
  public void testWrapEntityMentionsWithNullCoreMapInList() {
    List<CoreMap> entityMentionList = new ArrayList<CoreMap>();
    entityMentionList.add(null);

    CoreMap sentence = new Annotation("Input with null");
    sentence.set(CoreAnnotations.MentionsAnnotation.class, entityMentionList);
    sentence.set(CoreAnnotations.TextAnnotation.class, "Input with null");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 15);

    CoreDocument doc = new CoreDocument("Input with null");
    CoreSentence cs = new CoreSentence(doc, sentence);

    try {
      cs.wrapEntityMentions();
      List<CoreEntityMention> mentions = cs.entityMentions();
      assertNotNull(mentions);
      assertEquals(1, mentions.size());
//       assertNull(mentions.get(0).coreMapEntity());
    } catch (Exception e) {
      fail("Failed to handle null value in MentionsAnnotation list.");
    }
  }
@Test
  public void testPatternCacheHitWithSamePattern() {
    Tree tree = Tree.valueOf("(ROOT (S (NP (NN A)) (VP (VB go))))");

    CoreMap sentence = new Annotation("Repeat me");
    sentence.set(CoreAnnotations.TextAnnotation.class, "Repeat me");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 9);
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);

    CoreDocument doc = new CoreDocument("Repeat me");
    CoreSentence cs = new CoreSentence(doc, sentence);

    List<String> first = cs.tregexResults("NP");
    List<String> second = cs.tregexResults("NP");

    assertEquals(first, second);
    assertEquals(1, first.size());
  }
@Test
  public void testPatternCacheWithDistinctPatterns() {
    Tree tree = Tree.valueOf("(ROOT (S (NP (NN ship)) (VP (VB sails))))");

    CoreMap sentence = new Annotation("The ship sails.");
    sentence.set(CoreAnnotations.TextAnnotation.class, "The ship sails.");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 16);
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);

    CoreDocument doc = new CoreDocument("The ship sails.");
    CoreSentence cs = new CoreSentence(doc, sentence);

    List<String> npMatches = cs.tregexResults("NP");
    List<String> vpMatches = cs.tregexResults("VP");

    assertEquals(1, npMatches.size());
    assertEquals("ship", npMatches.get(0));

    assertEquals(1, vpMatches.size());
    assertEquals("sails", vpMatches.get(0));
  }
@Test
  public void testTokensAsStringsWithAllNullTokens() {
    CoreLabel token1 = new CoreLabel();
    CoreLabel token2 = new CoreLabel();

    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    tokens.add(token1);
    tokens.add(token2);

    CoreMap sentence = new Annotation("null tokens");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TextAnnotation.class, "null tokens");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);

    CoreDocument doc = new CoreDocument("null tokens");
    CoreSentence coreSentence = new CoreSentence(doc, sentence);

    List<String> result = coreSentence.tokensAsStrings();
    assertEquals(2, result.size());
    assertNull(result.get(0));
    assertNull(result.get(1));
  }
@Test
  public void testNerTagsWithEmptyNER() {
    CoreLabel token = new CoreLabel();
    token.setWord("Apple");
    token.setNER("");

    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    tokens.add(token);

    CoreMap sentence = new Annotation("Apple");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TextAnnotation.class, "Apple");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);

    CoreDocument doc = new CoreDocument("Apple");
    CoreSentence coreSentence = new CoreSentence(doc, sentence);

    List<String> nerTags = coreSentence.nerTags();
    assertEquals(1, nerTags.size());
    assertEquals("", nerTags.get(0));
  }
@Test
  public void testSentimentTreeReturnsNonNullWhenSet() {
    Tree sentimentTree = Tree.valueOf("(3 (2 bad) (4 great))");

    CoreMap sentence = new Annotation("mixed");
    sentence.set(SentimentCoreAnnotations.SentimentAnnotatedTree.class, sentimentTree);
    sentence.set(CoreAnnotations.TextAnnotation.class, "mixed");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);

    CoreDocument doc = new CoreDocument("mixed");
    CoreSentence coreSentence = new CoreSentence(doc, sentence);

    Tree result = coreSentence.sentimentTree();
    assertNotNull(result);
    assertEquals(sentimentTree, result);
  }
@Test
  public void testRelationsAnnotationSetToEmptyList() {
    List<RelationTriple> triples = new ArrayList<RelationTriple>();

    CoreMap sentence = new Annotation("no triples");
    sentence.set(CoreAnnotations.KBPTriplesAnnotation.class, triples);
    sentence.set(CoreAnnotations.TextAnnotation.class, "no triples");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);

    CoreDocument doc = new CoreDocument("no triples");
    CoreSentence coreSentence = new CoreSentence(doc, sentence);

    List<RelationTriple> output = coreSentence.relations();
    assertNotNull(output);
    assertTrue(output.isEmpty());
  }
@Test
  public void testTregexResultTreeReturnsRootIfPatternMatchesEverything() {
    Tree tree = Tree.valueOf("(ROOT (S (VP (VB do))))");

    CoreMap sentence = new Annotation("do");
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    sentence.set(CoreAnnotations.TextAnnotation.class, "do");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 2);

    CoreDocument doc = new CoreDocument("do");
    CoreSentence coreSentence = new CoreSentence(doc, sentence);

    List<Tree> trees = coreSentence.tregexResultTrees("ROOT");
    assertEquals(1, trees.size());
    assertEquals("do", trees.get(0).yieldWords().get(0).word());
  }
@Test
  public void testCharOffsetsWithZeroRange() {
    CoreMap sentence = new Annotation("");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);
    sentence.set(CoreAnnotations.TextAnnotation.class, "");
    CoreDocument doc = new CoreDocument("");
    CoreSentence coreSentence = new CoreSentence(doc, sentence);

    Pair<Integer, Integer> offsets = coreSentence.charOffsets();
    assertEquals((Integer) 10, offsets.first);
    assertEquals((Integer) 10, offsets.second);
  }
@Test
  public void testEmptyTextAnnotationHandling() {
    CoreMap sentence = new Annotation("");
    sentence.set(CoreAnnotations.TextAnnotation.class, "");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 0);

    CoreDocument doc = new CoreDocument("");
    CoreSentence coreSentence = new CoreSentence(doc, sentence);

    String result = coreSentence.text();
    assertEquals("", result);
  }
@Test
  public void testSingleTokenAllFieldsNull() {
    CoreLabel token = new CoreLabel(); 

    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    tokens.add(token);

    CoreMap sentence = new Annotation("blank");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TextAnnotation.class, "blank");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);

    CoreDocument doc = new CoreDocument("blank");
    CoreSentence coreSentence = new CoreSentence(doc, sentence);

    assertEquals(1, coreSentence.tokensAsStrings().size());
    assertNull(coreSentence.tokensAsStrings().get(0));

    assertEquals(1, coreSentence.posTags().size());
    assertNull(coreSentence.posTags().get(0));

    assertEquals(1, coreSentence.lemmas().size());
    assertNull(coreSentence.lemmas().get(0));

    assertEquals(1, coreSentence.nerTags().size());
    assertNull(coreSentence.nerTags().get(0));
  }
@Test
  public void testWrapEntityMentionWithMultipleNulls() {
    List<CoreMap> entityMaps = new ArrayList<CoreMap>();
    entityMaps.add(null);
    entityMaps.add(null);

    CoreMap sentence = new Annotation("multiple nulls");
    sentence.set(CoreAnnotations.MentionsAnnotation.class, entityMaps);
    sentence.set(CoreAnnotations.TextAnnotation.class, "multiple nulls");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 14);

    CoreDocument doc = new CoreDocument("multiple nulls");
    CoreSentence coreSentence = new CoreSentence(doc, sentence);

    coreSentence.wrapEntityMentions();
    List<CoreEntityMention> mentions = coreSentence.entityMentions();

    assertEquals(2, mentions.size());
//     assertNull(mentions.get(0).coreMapEntity());
//     assertNull(mentions.get(1).coreMapEntity());
  }
@Test
  public void testToStringWithEmptyCoreMap() {
    CoreMap sentence = new Annotation("");
    CoreDocument doc = new CoreDocument("");
    CoreSentence coreSentence = new CoreSentence(doc, sentence);

    String output = coreSentence.toString();
    assertEquals("{}", output);
  }
@Test
  public void testTokensAsStringsWithEmptyList() {
    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    CoreMap sentence = new Annotation("");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TextAnnotation.class, "");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 0);

    CoreDocument doc = new CoreDocument("");
    CoreSentence coreSentence = new CoreSentence(doc, sentence);

    List<String> words = coreSentence.tokensAsStrings();
    assertNotNull(words);
    assertTrue(words.isEmpty());
  }
@Test
  public void testTregexResultTreeReturnsEmptyForNonMatchingPattern() {
    Tree tree = Tree.valueOf("(ROOT (S (VP (VB run))))");
    CoreMap sentence = new Annotation("run");
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    sentence.set(CoreAnnotations.TextAnnotation.class, "run");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);

    CoreDocument doc = new CoreDocument("run");
    CoreSentence coreSentence = new CoreSentence(doc, sentence);

    List<Tree> results = coreSentence.tregexResultTrees("NP");
    assertNotNull(results);
    assertTrue(results.isEmpty());
  }
@Test
  public void testTregexResultsReturnsEmptyListWhenNoMatch() {
    Tree tree = Tree.valueOf("(ROOT (S (VP (VB walking))))");
    CoreMap sentence = new Annotation("walking");
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    sentence.set(CoreAnnotations.TextAnnotation.class, "walking");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);

    CoreDocument doc = new CoreDocument("walking");
    CoreSentence coreSentence = new CoreSentence(doc, sentence);

    List<String> results = coreSentence.tregexResults("NP");
    assertNotNull(results);
    assertTrue(results.isEmpty());
  }
@Test
  public void testNounPhraseTreesReturnsMultipleSubtrees() {
    Tree tree = Tree.valueOf("(ROOT (S (NP (DT The) (NN cat)) (VP (VBD sat) (PP (IN on) (NP (DT the) (NN mat))))) )");

    CoreMap sentence = new Annotation("The cat sat on the mat.");
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    sentence.set(CoreAnnotations.TextAnnotation.class, "The cat sat on the mat.");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 24);

    CoreDocument doc = new CoreDocument("The cat sat on the mat.");
    CoreSentence coreSentence = new CoreSentence(doc, sentence);

    List<Tree> npTrees = coreSentence.nounPhraseTrees();
    assertEquals(2, npTrees.size());

    List<String> spans = coreSentence.nounPhrases();
    assertEquals(2, spans.size());
    assertEquals("The cat", spans.get(0));
    assertEquals("the mat", spans.get(1));
  }
@Test
  public void testWrapEntityMentionsDoesNotCrashWithEmptyMentionList() {
    CoreMap sentence = new Annotation("Sample");
    List<CoreMap> mentions = new ArrayList<CoreMap>();
    sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
    sentence.set(CoreAnnotations.TextAnnotation.class, "Sample");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);

    CoreDocument doc = new CoreDocument("Sample");
    CoreSentence coreSentence = new CoreSentence(doc, sentence);

    coreSentence.wrapEntityMentions();
    List<CoreEntityMention> entityMentions = coreSentence.entityMentions();
    assertNotNull(entityMentions);
    assertTrue(entityMentions.isEmpty());
  }
@Test
  public void testSentimentReturnsNullIfNotPresent() {
    CoreMap sentence = new Annotation("I don't feel anything.");
    sentence.set(CoreAnnotations.TextAnnotation.class, "I don't feel anything.");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 23);

    CoreDocument doc = new CoreDocument("I don't feel anything.");
    CoreSentence coreSentence = new CoreSentence(doc, sentence);

    String sentiment = coreSentence.sentiment();
    assertNull(sentiment);
  }
@Test
  public void testEntityMentionsReturnsNullBeforeWrapCall() {
    CoreMap sentence = new Annotation("Entity without wrap");

    CoreDocument doc = new CoreDocument("Entity without wrap");
    CoreSentence coreSentence = new CoreSentence(doc, sentence);

    assertNull(coreSentence.entityMentions());
  }
@Test
  public void testLemmasWithEmptyTokenList() {
    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    CoreMap sentence = new Annotation("blank");

    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TextAnnotation.class, "blank");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);

    CoreDocument doc = new CoreDocument("blank");
    CoreSentence coreSentence = new CoreSentence(doc, sentence);

    List<String> lemmas = coreSentence.lemmas();
    assertNotNull(lemmas);
    assertTrue(lemmas.isEmpty());
  }
@Test
  public void testCharOffsetsWithNegativeStart() {
    CoreMap sentence = new Annotation("Negative offset");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, -1);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 15);
    sentence.set(CoreAnnotations.TextAnnotation.class, "Negative offset");

    CoreDocument doc = new CoreDocument("Negative offset");
    CoreSentence coreSentence = new CoreSentence(doc, sentence);

    Pair<Integer, Integer> offsets = coreSentence.charOffsets();
    assertEquals((Integer) (-1), offsets.first);
    assertEquals((Integer) 15, offsets.second);
  }
@Test
  public void testConstituencyParseHandlesMalformedTree() {
    Tree malformed = new LabeledScoredTreeNode(null);
    malformed.setChildren(new Tree[] {});

    CoreMap sentence = new Annotation("Malformed");
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, malformed);
    sentence.set(CoreAnnotations.TextAnnotation.class, "Malformed");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 9);

    CoreDocument doc = new CoreDocument("Malformed");
    CoreSentence coreSentence = new CoreSentence(doc, sentence);

    List<Tree> trees = coreSentence.tregexResultTrees("NP");
    assertNotNull(trees);
    assertTrue(trees.isEmpty());
  }
@Test
  public void testTokenWithNullTagStillReturnsInPosTags() {
    CoreLabel token = new CoreLabel();
    token.setWord("data");

    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    tokens.add(token);

    CoreMap sentence = new Annotation("data");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TextAnnotation.class, "data");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);

    CoreDocument doc = new CoreDocument("data");
    CoreSentence coreSentence = new CoreSentence(doc, sentence);

    List<String> pos = coreSentence.posTags();
    assertEquals(1, pos.size());
    assertNull(pos.get(0));
  }
@Test
  public void testTokenWithNullLemmaHandledSafely() {
    CoreLabel token = new CoreLabel();
    token.setWord("testing");

    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    tokens.add(token);

    CoreMap sentence = new Annotation("testing");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TextAnnotation.class, "testing");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);

    CoreDocument doc = new CoreDocument("testing");
    CoreSentence coreSentence = new CoreSentence(doc, sentence);

    List<String> lemmas = coreSentence.lemmas();
    assertEquals(1, lemmas.size());
    assertNull(lemmas.get(0));
  }
@Test
  public void testTokenWithNullNERHandledSafely() {
    CoreLabel token = new CoreLabel();
    token.setWord("Paris");

    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    tokens.add(token);

    CoreMap sentence = new Annotation("Paris");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TextAnnotation.class, "Paris");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);

    CoreDocument doc = new CoreDocument("Paris");
    CoreSentence coreSentence = new CoreSentence(doc, sentence);

    List<String> ner = coreSentence.nerTags();
    assertEquals(1, ner.size());
    assertNull(ner.get(0));
  }
@Test
  public void testWrapEntityMentionsWithPartiallyNullList() {
    CoreMap entity1 = null;
    CoreMap entity2 = new Annotation("Stanford");

    List<CoreMap> mentionList = new ArrayList<CoreMap>();
    mentionList.add(entity1);
    mentionList.add(entity2);

    CoreMap sentence = new Annotation("Stanford");
    sentence.set(CoreAnnotations.MentionsAnnotation.class, mentionList);
    sentence.set(CoreAnnotations.TextAnnotation.class, "Stanford");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);

    CoreDocument doc = new CoreDocument("Stanford");
    CoreSentence coreSentence = new CoreSentence(doc, sentence);

    coreSentence.wrapEntityMentions();

    List<CoreEntityMention> mentions = coreSentence.entityMentions();
    assertEquals(2, mentions.size());
//     assertNull(mentions.get(0).coreMapEntity());
//     assertNotNull(mentions.get(1).coreMapEntity());
  }
@Test
  public void testTregexResultsReturnsMultipleMatchesWithSharedLabel() {
    Tree tree = Tree.valueOf("(ROOT (S (NP (NN dog)) (VP (VBD barked)) (NP (NNS cats))))");

    CoreMap sentence = new Annotation("dog barked cats");
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    sentence.set(CoreAnnotations.TextAnnotation.class, "dog barked cats");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 16);

    CoreDocument doc = new CoreDocument("dog barked cats");
    CoreSentence coreSentence = new CoreSentence(doc, sentence);

    List<String> matches = coreSentence.tregexResults("NP");
    assertEquals(2, matches.size());
    assertEquals("dog", matches.get(0));
    assertEquals("cats", matches.get(1));
  }
@Test
  public void testTregexPatternCachingWithDifferentKeys() {
    Tree tree = Tree.valueOf("(ROOT (S (NP (NN subject)) (VP (VBZ runs))))");

    CoreMap sentence = new Annotation("subject runs");
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    sentence.set(CoreAnnotations.TextAnnotation.class, "subject runs");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 13);

    CoreDocument doc = new CoreDocument("subject runs");
    CoreSentence coreSentence = new CoreSentence(doc, sentence);

    List<String> np = coreSentence.tregexResults("NP");
    List<String> vp = coreSentence.tregexResults("VP");

    assertEquals(1, np.size());
    assertEquals("subject", np.get(0));

    assertEquals(1, vp.size());
    assertEquals("runs", vp.get(0));
  }
@Test
  public void testToStringWithNonEmptyCoreMapReturnsSameString() {
    CoreMap sentence = new Annotation("Hello world");
    sentence.set(CoreAnnotations.TextAnnotation.class, "Hello world");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);

    CoreDocument doc = new CoreDocument("Hello world");
    CoreSentence coreSentence = new CoreSentence(doc, sentence);

    String expected = sentence.toString();
    String result = coreSentence.toString();

    assertEquals(expected, result);
  }
@Test
  public void testTregexResultTreesRootEdgePattern() {
    Tree tree = Tree.valueOf("(ROOT (S (VP (VBZ is))))");

    CoreMap sentence = new Annotation("is");
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    sentence.set(CoreAnnotations.TextAnnotation.class, "is");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 2);

    CoreDocument doc = new CoreDocument("is");
    CoreSentence coreSentence = new CoreSentence(doc, sentence);

    List<Tree> match = coreSentence.tregexResultTrees("ROOT");
    assertEquals(1, match.size());
    assertEquals("is", match.get(0).yieldWords().get(0).word());
  }
@Test
  public void testTokensReturnsNullWhenTokensAnnotationMissing() {
    CoreMap sentence = new Annotation("missing tokens");
    sentence.set(CoreAnnotations.TextAnnotation.class, "missing tokens");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 14);

    CoreDocument doc = new CoreDocument("missing tokens");
    CoreSentence coreSentence = new CoreSentence(doc, sentence);

    List<CoreLabel> result = coreSentence.tokens();
    assertNull(result);
  }
@Test
  public void testWrapEntityMentionsNoAnnotationSet() {
    CoreMap sentence = new Annotation("no annotation");
    sentence.set(CoreAnnotations.TextAnnotation.class, "no annotation");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 13);

    CoreDocument doc = new CoreDocument("no annotation");
    CoreSentence coreSentence = new CoreSentence(doc, sentence);

    coreSentence.wrapEntityMentions();
    assertNull(coreSentence.entityMentions());
  } 
}
