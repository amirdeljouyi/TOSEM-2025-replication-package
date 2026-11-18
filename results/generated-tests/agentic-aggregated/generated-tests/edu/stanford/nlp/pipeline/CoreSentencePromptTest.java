package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import edu.stanford.nlp.util.ArrayCoreMap;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.Pair;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class CoreSentencePromptTest {

  @Test
  public void constructorExposesDocumentAndExactCoreMap() {
    CoreDocument document = new CoreDocument("A short document.");
    CoreMap map = new ArrayCoreMap();
    CoreSentence sentence = new CoreSentence(document, map);

    assertSame(document, sentence.document());
    assertSame(map, sentence.coreMap());
  }

  @Test
  public void textReflectsUnderlyingMapChanges() {
    CoreDocument document = new CoreDocument("original");
    CoreMap map = new ArrayCoreMap();
    map.set(CoreAnnotations.TextAnnotation.class, "First value");
    CoreSentence sentence = new CoreSentence(document, map);

    assertEquals("First value", sentence.text());
    map.set(CoreAnnotations.TextAnnotation.class, "Replacement value");
    assertEquals("Replacement value", sentence.text());
  }

  @Test
  public void absentTextAndOptionalAnnotationsReturnNull() {
    CoreSentence sentence = new CoreSentence(new CoreDocument(""), new ArrayCoreMap());

    assertNull(sentence.text());
    assertNull(sentence.tokens());
    assertNull(sentence.constituencyParse());
    assertNull(sentence.dependencyParse());
    assertNull(sentence.sentiment());
    assertNull(sentence.sentimentTree());
    assertNull(sentence.entityMentions());
    assertNull(sentence.relations());
  }

  @Test
  public void charOffsetsReturnsConfiguredHalfOpenBounds() {
    CoreMap map = new ArrayCoreMap();
    map.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 7);
    map.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 23);
    CoreSentence sentence = new CoreSentence(new CoreDocument("0123456some sentence text"), map);

    assertEquals(new Pair<Integer, Integer>(7, 23), sentence.charOffsets());
  }

  @Test(expected = NullPointerException.class)
  public void charOffsetsFailsWhenBeginOffsetIsMissing() {
    CoreMap map = new ArrayCoreMap();
    map.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
    CoreSentence sentence = new CoreSentence(new CoreDocument("text"), map);

    sentence.charOffsets();
  }

  @Test(expected = NullPointerException.class)
  public void charOffsetsFailsWhenEndOffsetIsMissing() {
    CoreMap map = new ArrayCoreMap();
    map.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    CoreSentence sentence = new CoreSentence(new CoreDocument("text"), map);

    sentence.charOffsets();
  }

  @Test
  public void tokenAttributeMethodsProjectValuesInOrder() {
    CoreLabel first = new CoreLabel();
    first.setWord("Stanford");
    first.setTag("NNP");
    first.setLemma("Stanford");
    first.setNER("ORGANIZATION");
    CoreLabel second = new CoreLabel();
    second.setWord("rocks");
    second.setTag("VBZ");
    second.setLemma("rock");
    second.setNER("O");
    CoreLabel third = new CoreLabel();
    third.setWord("!");
    third.setTag(".");
    third.setLemma("!");
    third.setNER("O");
    List<CoreLabel> tokens = Arrays.asList(first, second, third);
    CoreMap map = new ArrayCoreMap();
    map.set(CoreAnnotations.TokensAnnotation.class, tokens);
    CoreSentence sentence = new CoreSentence(new CoreDocument("Stanford rocks!"), map);

    assertSame(tokens, sentence.tokens());
    assertEquals(Arrays.asList("Stanford", "rocks", "!"), sentence.tokensAsStrings());
    assertEquals(Arrays.asList("NNP", "VBZ", "."), sentence.posTags());
    assertEquals(Arrays.asList("Stanford", "rock", "!"), sentence.lemmas());
    assertEquals(Arrays.asList("ORGANIZATION", "O", "O"), sentence.nerTags());
  }

  @Test
  public void tokenProjectionPreservesNullAttributesAndReturnsIndependentList() {
    CoreLabel token = new CoreLabel();
    token.setWord("unknown");
    CoreMap map = new ArrayCoreMap();
    map.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    CoreSentence sentence = new CoreSentence(new CoreDocument("unknown"), map);

    List<String> words = sentence.tokensAsStrings();
    words.set(0, "changed only in projection");
    assertEquals(Collections.singletonList("unknown"), sentence.tokensAsStrings());
    assertEquals(Collections.singletonList(null), sentence.posTags());
    assertEquals(Collections.singletonList(null), sentence.lemmas());
    assertEquals(Collections.singletonList(null), sentence.nerTags());
  }

  @Test(expected = NullPointerException.class)
  public void tokenProjectionFailsWhenTokensAnnotationIsAbsent() {
    CoreSentence sentence = new CoreSentence(new CoreDocument("no tokens"), new ArrayCoreMap());

    sentence.tokensAsStrings();
  }

  @Test
  public void constituencyAndPhraseConveniencesFindNestedPhrases() {
    Tree tree = Tree.valueOf("(ROOT (S (NP (DT The) (NN cat)) (VP (VBD saw) (NP (DT a) (NN dog))) (. .)))");
    CoreMap map = new ArrayCoreMap();
    map.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    CoreSentence sentence = new CoreSentence(new CoreDocument("The cat saw a dog."), map);

    assertSame(tree, sentence.constituencyParse());
    assertEquals(Arrays.asList("Thecat", "adog"), sentence.nounPhrases());
    assertEquals(Collections.singletonList("sawadog"), sentence.verbPhrases());
    assertEquals(2, sentence.nounPhraseTrees().size());
    assertEquals("NP", sentence.nounPhraseTrees().get(0).value());
    assertEquals(1, sentence.verbPhraseTrees().size());
    assertEquals("VP", sentence.verbPhraseTrees().get(0).value());
  }

  @Test
  public void stringAndCompiledTregexPatternsProduceEquivalentResults() {
    Tree tree = Tree.valueOf("(ROOT (S (NP (NNP Alice)) (VP (VBD greeted) (NP (NNP Bob))) (. .)))");
    CoreMap map = new ArrayCoreMap();
    map.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    CoreSentence sentence = new CoreSentence(new CoreDocument("Alice greeted Bob."), map);
    TregexPattern compiled = TregexPattern.compile("NP < NNP");

    assertEquals(Arrays.asList("Alice", "Bob"), sentence.tregexResults("NP < NNP"));
    assertEquals(Arrays.asList("Alice", "Bob"), sentence.tregexResults(compiled));
    assertEquals(2, sentence.tregexResultTrees("NP < NNP").size());
    assertEquals(2, sentence.tregexResultTrees(compiled).size());
  }

  @Test
  public void tregexStringPatternCanBeReusedAcrossDifferentSentences() {
    CoreMap firstMap = new ArrayCoreMap();
    firstMap.set(TreeCoreAnnotations.TreeAnnotation.class,
        Tree.valueOf("(ROOT (S (NP (NN Birds)) (VP (VBP fly))))"));
    CoreMap secondMap = new ArrayCoreMap();
    secondMap.set(TreeCoreAnnotations.TreeAnnotation.class,
        Tree.valueOf("(ROOT (S (NP (NNS Fish)) (VP (VBP swim))))"));
    CoreSentence first = new CoreSentence(new CoreDocument("Birds fly"), firstMap);
    CoreSentence second = new CoreSentence(new CoreDocument("Fish swim"), secondMap);

    assertEquals(Collections.singletonList("Birds"), first.tregexResults("NP"));
    assertEquals(Collections.singletonList("Fish"), second.tregexResults("NP"));
    assertEquals(Collections.singletonList("Birds"), first.tregexResults("NP"));
  }

  @Test
  public void tregexWithNoMatchesReturnsEmptyLists() {
    CoreMap map = new ArrayCoreMap();
    map.set(TreeCoreAnnotations.TreeAnnotation.class,
        Tree.valueOf("(ROOT (S (NP (PRP It)) (VP (VBZ works))))"));
    CoreSentence sentence = new CoreSentence(new CoreDocument("It works"), map);

    assertNotNull(sentence.tregexResultTrees("ADJP"));
    assertTrue(sentence.tregexResultTrees("ADJP").isEmpty());
    assertTrue(sentence.tregexResults(TregexPattern.compile("ADVP")).isEmpty());
  }

  @Test
  public void tregexStringReportsHelpfulFailureWithoutParse() {
    CoreSentence sentence = new CoreSentence(new CoreDocument("Unparsed."), new ArrayCoreMap());

    try {
      sentence.tregexResults("NP");
      fail("Expected a missing-parse exception");
    } catch (RuntimeException exception) {
      assertTrue(exception.getMessage().contains("without a constituency parse"));
      assertTrue(exception.getMessage().contains("'parse' annotator"));
    }
  }

  @Test
  public void tregexCompiledPatternReportsHelpfulFailureWithoutParse() {
    CoreSentence sentence = new CoreSentence(new CoreDocument("Unparsed."), new ArrayCoreMap());

    try {
      sentence.tregexResultTrees(TregexPattern.compile("VP"));
      fail("Expected a missing-parse exception");
    } catch (RuntimeException exception) {
      assertTrue(exception.getMessage().contains("without a constituency parse"));
    }
  }

  @Test
  public void wrapEntityMentionsLeavesNullWhenAnnotationIsAbsent() {
    CoreSentence sentence = new CoreSentence(new CoreDocument("No entities"), new ArrayCoreMap());

    sentence.wrapEntityMentions();
    assertNull(sentence.entityMentions());
  }

  @Test
  public void wrapEntityMentionsWrapsEveryMapAndRetainsSentenceAssociation() {
    CoreMap alice = new ArrayCoreMap();
    alice.set(CoreAnnotations.TextAnnotation.class, "Alice");
    alice.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
    CoreMap paris = new ArrayCoreMap();
    paris.set(CoreAnnotations.TextAnnotation.class, "Paris");
    paris.set(CoreAnnotations.EntityTypeAnnotation.class, "CITY");
    CoreMap sentenceMap = new ArrayCoreMap();
    sentenceMap.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(alice, paris));
    CoreSentence sentence = new CoreSentence(new CoreDocument("Alice visited Paris"), sentenceMap);

    sentence.wrapEntityMentions();

    assertEquals(2, sentence.entityMentions().size());
    assertEquals("Alice", sentence.entityMentions().get(0).text());
    assertEquals("PERSON", sentence.entityMentions().get(0).entityType());
    assertSame(alice, sentence.entityMentions().get(0).coreMap());
    assertSame(sentence, sentence.entityMentions().get(0).sentence());
    assertEquals("Paris", sentence.entityMentions().get(1).text());
    assertEquals("CITY", sentence.entityMentions().get(1).entityType());
    assertSame(paris, sentence.entityMentions().get(1).coreMap());
    assertSame(sentence, sentence.entityMentions().get(1).sentence());
  }

  @Test
  public void wrappingEmptyMentionAnnotationProducesEmptyList() {
    CoreMap map = new ArrayCoreMap();
    map.set(CoreAnnotations.MentionsAnnotation.class, Collections.emptyList());
    CoreSentence sentence = new CoreSentence(new CoreDocument("Nothing named"), map);

    sentence.wrapEntityMentions();

    assertNotNull(sentence.entityMentions());
    assertTrue(sentence.entityMentions().isEmpty());
  }

  @Test
  public void coreDocumentConstructionTriggersSentenceAndEntityWrapping() {
    CoreMap mention = new ArrayCoreMap();
    mention.set(CoreAnnotations.TextAnnotation.class, "Ada");
    mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
    CoreMap sentenceMap = new ArrayCoreMap();
    sentenceMap.set(CoreAnnotations.TextAnnotation.class, "Ada computes.");
    sentenceMap.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
    Annotation annotation = new Annotation("Ada computes.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentenceMap));

    CoreDocument document = new CoreDocument(annotation);
    CoreSentence sentence = document.sentences().get(0);

    assertSame(document, sentence.document());
    assertSame(sentenceMap, sentence.coreMap());
    assertEquals("Ada computes.", sentence.text());
    assertEquals(1, sentence.entityMentions().size());
    assertEquals("Ada", sentence.entityMentions().get(0).text());
    assertSame(sentence, sentence.entityMentions().get(0).sentence());
    assertEquals(1, document.entityMentions().size());
    assertSame(sentence.entityMentions().get(0), document.entityMentions().get(0));
  }

  @Test
  public void dependencySentimentAndRelationAccessorsReturnExactAnnotations() {
    SemanticGraph dependencies = new SemanticGraph();
    Tree sentimentTree = Tree.valueOf("(3 (2 Good) (4 news))");
    List<RelationTriple> relations = Collections.emptyList();
    CoreMap map = new ArrayCoreMap();
    map.set(SemanticGraphCoreAnnotations.EnhancedPlusPlusDependenciesAnnotation.class, dependencies);
    map.set(SentimentCoreAnnotations.SentimentClass.class, "Positive");
    map.set(SentimentCoreAnnotations.SentimentAnnotatedTree.class, sentimentTree);
    map.set(CoreAnnotations.KBPTriplesAnnotation.class, relations);
    CoreSentence sentence = new CoreSentence(new CoreDocument("Good news"), map);

    assertSame(dependencies, sentence.dependencyParse());
    assertEquals("Positive", sentence.sentiment());
    assertSame(sentimentTree, sentence.sentimentTree());
    assertSame(relations, sentence.relations());
  }

  @Test
  public void toStringDelegatesToCurrentCoreMapRepresentation() {
    CoreMap map = new ArrayCoreMap();
    map.set(CoreAnnotations.TextAnnotation.class, "Printable sentence");
    CoreSentence sentence = new CoreSentence(new CoreDocument("Printable sentence"), map);

    assertEquals(map.toString(), sentence.toString());
    map.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    assertEquals(map.toString(), sentence.toString());
  }
}
