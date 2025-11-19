package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.parser.common.ParserAnnotations;
import edu.stanford.nlp.parser.common.ParserConstraint;
import edu.stanford.nlp.parser.common.ParserGrammar;
import edu.stanford.nlp.parser.common.ParserQuery;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.parser.lexparser.TreebankLangParserParams;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.time.TimeAnnotations;
import edu.stanford.nlp.time.TimeExpression;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.util.*;

import edu.stanford.nlp.ie.NERClassifierCombiner;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.time.Timex;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import org.junit.Test;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ParserAnnotator_3_GPTLLMTest {

@Test
  public void testDoOneSentenceWithValidTree() {
    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel word1 = new CoreLabel();
    word1.setWord("Hello");
    word1.setTag("UH");
    tokens.add(word1);

    CoreLabel word2 = new CoreLabel();
    word2.setWord("world");
    word2.setTag("NN");
    tokens.add(word2);

    Annotation annotation = new Annotation("Text");
    CoreMap sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    ParserQuery query = mock(ParserQuery.class);
    Tree tree = Tree.valueOf("(ROOT (UH Hello) (NN world))");
    when(query.getBestParse()).thenReturn(tree);
    when(query.getBestScore()).thenReturn(-1.0);
    when(query.parse(anyList())).thenReturn(true);

    Tree resultTree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(resultTree);
    assertEquals("ROOT", resultTree.label().value());
  }
@Test
  public void testDoOneSentenceExceedsMaxLength() {
    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel tok1 = new CoreLabel();
    tok1.setWord("This");
    tok1.setTag("DT");
    CoreLabel tok2 = new CoreLabel();
    tok2.setWord("is");
    tok2.setTag("VBZ");
    CoreLabel tok3 = new CoreLabel();
    tok3.setWord("too");
    tok3.setTag("RB");
    CoreLabel tok4 = new CoreLabel();
    tok4.setWord("long");
    tok4.setTag("JJ");

    tokens.add(tok1);
    tokens.add(tok2);
    tokens.add(tok3);
    tokens.add(tok4);

    Annotation annotation = new Annotation("Doc");
    CoreMap sentence = new Annotation("Sent");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    Tree fallbackTree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(fallbackTree);
    assertEquals("X", fallbackTree.label().value());
  }
@Test
  public void testDoOneSentenceWithNullTreeTriggersFailure() {
    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel tok1 = new CoreLabel();
    tok1.setWord("Test");
    tok1.setTag(null);
    tokens.add(tok1);

    Annotation annotation = new Annotation("Text");
    CoreMap sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    ParserQuery query = mock(ParserQuery.class);
    when(query.getBestParse()).thenReturn(null);
    when(query.parse(anyList())).thenReturn(true);


    Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(tree);
    assertEquals("X", tree.label().value());
    String tag = tokens.get(0).tag();
    assertEquals("XX", tag);
  }
@Test
  public void testTreeMapIsApplied() {
    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel word = new CoreLabel();
    word.setWord("Testing");
    word.setTag("VBG");
    tokens.add(word);

    Annotation annotation = new Annotation("MyText");
    CoreMap sentence = new Annotation("Map");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    Tree rawTree = Tree.valueOf("(ROOT (VBG Testing))");
    Tree mappedTree = Tree.valueOf("(ROOT (NN replacement))");

    ParserQuery query = mock(ParserQuery.class);
    when(query.parse(anyList())).thenReturn(true);
    when(query.getBestParse()).thenReturn(rawTree);
    when(query.getBestScore()).thenReturn(-2.0);


    Tree resultTree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(resultTree);
    assertEquals("ROOT", resultTree.label().value());
    assertEquals("NN", resultTree.getChild(0).label().value());
  }
@Test
  public void testSignatureContainsExpectedProperties() {
    Properties props = new Properties();
    props.setProperty("parse.model", "/models/englishPCFG.ser.gz");
    props.setProperty("parse.debug", "true");
    props.setProperty("parse.maxlen", "200");
    props.setProperty("parse.flags", "-flag1 -flag2");
    props.setProperty("parse.maxheight", "150");
    props.setProperty("parse.maxtime", "3000");
    props.setProperty("parse.nthreads", "4");
    props.setProperty("parse.keepPunct", "false");
    props.setProperty("parse.nosquash", "true");
    props.setProperty("parse.extradependencies", "NONE");
    props.setProperty("parse.binaryTrees", "true");

    String sig = ParserAnnotator.signature("parse", props);
    assertTrue(sig.contains("parse.model:/models/englishPCFG.ser.gz"));
    assertTrue(sig.contains("parse.debug:true"));
    assertTrue(sig.contains("parse.flags:-flag1 -flag2"));
    assertTrue(sig.contains("parse.maxlen:200"));
    assertTrue(sig.contains("parse.maxheight:150"));
    assertTrue(sig.contains("parse.maxtime:3000"));
    assertTrue(sig.contains("parse.nthreads:4"));
    assertTrue(sig.contains("parse.keepPunct:false"));
    assertTrue(sig.contains("parse.nosquash:true"));
    assertTrue(sig.contains("parse.extradependencies:none"));
    assertTrue(sig.contains("parse.binaryTrees:true"));
  }
@Test
  public void testDoOneFailedSentenceSetsTree() {
    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel token = new CoreLabel();
    token.setWord("fail");
    token.setTag(null);
    tokens.add(token);

    CoreMap sentence = new Annotation("Sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation annotation = new Annotation("Doc");

    Tree fallbackTree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(fallbackTree);
    assertEquals("X", fallbackTree.label().value());

    CoreLabel updatedToken = sentence.get(CoreAnnotations.TokensAnnotation.class).get(0);
    assertEquals("XX", updatedToken.tag());
  }
@Test
  public void testRequirementsSatisfiedIncludesTreeAndDeps() {
    ParserGrammar parser = mock(ParserGrammar.class);
    ParserAnnotator annotator = new ParserAnnotator(parser, false, 10);

    Set<Class<? extends CoreAnnotation>> result = annotator.requirementsSatisfied();
    assertTrue(result.contains(TreeCoreAnnotations.TreeAnnotation.class));
  }
@Test
  public void testRequiresReturnsWithPOSTagsWhenNeeded() {
    ParserGrammar parser = mock(ParserGrammar.class);
    ParserAnnotator annotator = new ParserAnnotator(parser, true, 10);
    Set<Class<? extends CoreAnnotation>> required = annotator.requires();
    assertTrue(required.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.SentencesAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
  }
@Test
  public void testConstructorWithNullTreeMap() {
    ParserGrammar parser = mock(ParserGrammar.class);
    ParserAnnotator annotator = new ParserAnnotator(parser, true, 20, null);
    assertNotNull(annotator);
  }
@Test
  public void testDoOneSentenceWithEmptyTreeList() {
    ParserGrammar parser = mock(ParserGrammar.class);

    ParserQuery query = mock(ParserQuery.class);
    when(query.parse(anyList())).thenReturn(true);
    when(query.getBestParse()).thenReturn(null); 

    when(parser.parserQuery()).thenReturn(query);

    ParserAnnotator annotator = new ParserAnnotator(parser, false, 100);

    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel tok = new CoreLabel();
    tok.setWord("fail");
    tokens.add(tok);

    CoreMap sentence = new Annotation("s");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation annotation = new Annotation("doc");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    Tree t = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(t);
    assertEquals("X", t.label().value());
  }
@Test
  public void testDoOneSentenceThrowsNoSuchParseException() {
    ParserGrammar parser = mock(ParserGrammar.class);

    ParserAnnotator annotator = new ParserAnnotator(parser, false, 100);

    CoreLabel token = new CoreLabel();
    token.setWord("unknown");

    CoreMap sentence = new Annotation("s");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation annotation = new Annotation("doc");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    Tree fallback = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(fallback);
    assertEquals("X", fallback.label().value());
  }
@Test
  public void testFinishSentenceFlattensTallTree() {
    ParserGrammar parser = mock(ParserGrammar.class);

    ParserAnnotator annotator = new ParserAnnotator(parser, false, 100);

    Tree tallTree = Tree.valueOf("(ROOT (S (S (S (S (VP (VBD Stacked)))))))"); 

    CoreMap sentence = new Annotation("Sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());

    List<Tree> trees = new ArrayList<>();
    trees.add(tallTree);

    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    
    Tree resultBefore = trees.get(0);
    assertTrue(resultBefore.depth() > 2);

    
    try {
      java.lang.reflect.Method method = ParserAnnotator.class.getDeclaredMethod("finishSentence", CoreMap.class, List.class);
      method.setAccessible(true);
      method.invoke(annotator, sentence, trees);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    Tree resultTree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(resultTree);
  }
@Test
  public void testMaxSentenceLengthZeroAllowsAnyLength() {
    ParserGrammar parser = mock(ParserGrammar.class);
    ParserQuery query = mock(ParserQuery.class);
    Tree tree = Tree.valueOf("(ROOT (NN all))");
    when(query.getBestParse()).thenReturn(tree);
    when(query.getBestScore()).thenReturn(-0.1);
    when(parser.parserQuery()).thenReturn(query);

    ParserAnnotator annotator = new ParserAnnotator(parser, false, 0);

    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel token = new CoreLabel();
    token.setWord("test");
    token.setTag("NN");
    tokens.add(token);

    CoreMap sentence = new Annotation("s");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation annotation = new Annotation("Main");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    Tree result = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(result);
    assertEquals("ROOT", result.label().value());
  }
@Test
  public void testKBestGreaterThanOneReturnsMultipleTrees() {
    ParserGrammar parser = mock(ParserGrammar.class);

    List<ScoredObject<Tree>> kBestTrees = new ArrayList<>();
    Tree tree1 = Tree.valueOf("(ROOT (NN First))");
    ScoredObject<Tree> obj1 = new ScoredObject<>(tree1, -1.0);
    kBestTrees.add(obj1);

    Tree tree2 = Tree.valueOf("(ROOT (NN Second))");
    ScoredObject<Tree> obj2 = new ScoredObject<>(tree2, -2.0);
    kBestTrees.add(obj2);

    ParserQuery query = mock(ParserQuery.class);
    when(query.parse(anyList())).thenReturn(true);
    when(query.getKBestParses(2)).thenReturn(kBestTrees);

    when(parser.parserQuery()).thenReturn(query);

    CoreLabel token = new CoreLabel();
    token.setWord("multi");
    token.setTag("NN");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new Annotation("Sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation annotation = new Annotation("Doc");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    ParserAnnotator annotator = new ParserAnnotator(parser, false, 100) {
      {
        
        try {
          java.lang.reflect.Field kBestField = ParserAnnotator.class.getDeclaredField("kBest");
          kBestField.setAccessible(true);
          kBestField.setInt(this, 2);
        } catch (Exception e) {
          throw new RuntimeException("Failed to setup kBest edge case", e);
        }
      }
    };

    annotator.annotate(annotation);

    Tree finalTree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(finalTree);
    assertEquals("ROOT", finalTree.label().value());
  }
@Test
  public void testNoGraphIfDependencySupportIsFalse() {
    ParserGrammar parser = mock(ParserGrammar.class);

    ParserQuery query = mock(ParserQuery.class);
    Tree tree = Tree.valueOf("(ROOT (NN NoGraph))");
    when(query.getBestParse()).thenReturn(tree);
    when(query.getBestScore()).thenReturn(-0.2);
    when(parser.parserQuery()).thenReturn(query);

    ParserAnnotator annotator = new ParserAnnotator(parser, false, 100);

    CoreLabel token = new CoreLabel();
    token.setWord("none");
    token.setTag("NN");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new Annotation("Sent");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation annotation = new Annotation("MainDoc");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    SemanticGraph graph = sentence.get(SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation.class);
    assertNull(graph);
  }
@Test
  public void testNoSquashSkipsNonXTree() {
    ParserGrammar parser = mock(ParserGrammar.class);

    ParserAnnotator annotator = new ParserAnnotator(parser, false, 100) {
      {
        try {
          java.lang.reflect.Field noSquashField = ParserAnnotator.class.getDeclaredField("noSquash");
          noSquashField.setAccessible(true);
          noSquashField.set(this, true);
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    };

    Tree preAnnotTree = Tree.valueOf("(ROOT (NP Something))");
    CoreMap sentence = new Annotation("Sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, preAnnotTree);

    Annotation doc = new Annotation("Doc");
    List<CoreMap> sents = new ArrayList<>();
    sents.add(sentence);
    doc.set(CoreAnnotations.SentencesAnnotation.class, sents);

    annotator.annotate(doc);

    Tree resultTree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertSame(preAnnotTree, resultTree);
  }
@Test
  public void testNoSquashProcessesWhenTreeIsXOnlyLabel() {
    ParserGrammar parser = mock(ParserGrammar.class);

    ParserQuery query = mock(ParserQuery.class);
    Tree validTree = Tree.valueOf("(ROOT (VB Run))");
    when(query.getBestParse()).thenReturn(validTree);
    when(query.getBestScore()).thenReturn(-0.5);

    when(parser.parserQuery()).thenReturn(query);

    ParserAnnotator annotator = new ParserAnnotator(parser, false, 100) {
      {
        try {
          java.lang.reflect.Field noSquashField = ParserAnnotator.class.getDeclaredField("noSquash");
          noSquashField.setAccessible(true);
          noSquashField.set(this, true);
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    };

    Tree treeX = Tree.valueOf("(X)");
    treeX.setLabel(new StringLabel("X"));
    CoreMap sentence = new Annotation("Sentence");
    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel cl = new CoreLabel();
    cl.setWord("Go");
    cl.setTag("VB");
    tokens.add(cl);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, treeX);

    Annotation doc = new Annotation("Doc");
    List<CoreMap> sents = new ArrayList<>();
    sents.add(sentence);
    doc.set(CoreAnnotations.SentencesAnnotation.class, sents);

    annotator.annotate(doc);

    Tree result = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertEquals("ROOT", result.label().value());
  }

@Test
  public void testDefaultConstructionUsesSystemPropertyParserModel() {
    System.setProperty("parse.model", "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");

    ParserAnnotator annotator = new ParserAnnotator(false, 100);
    assertNotNull(annotator);

    System.clearProperty("parse.model");
  }
@Test
  public void testOverlyTallTreeIsFlattened() {
    ParserGrammar parser = mock(ParserGrammar.class);

    ParserAnnotator annotator = new ParserAnnotator(parser, false, 100);

    Tree deepTree = Tree.valueOf("(ROOT (A (B (C (D (E (F (G leaf))))))) )");

    List<Tree> trees = new ArrayList<>();
    trees.add(deepTree);

    CoreMap sentence = new Annotation("Sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    try {
      java.lang.reflect.Method m = ParserAnnotator.class.getDeclaredMethod("finishSentence", CoreMap.class, List.class);
      m.setAccessible(true);
      m.invoke(annotator, sentence, trees);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    Tree resultTree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(resultTree);
  }
@Test
  public void testDoOneSentenceSetsCorrectSentenceIndexOnVertices() {
    ParserGrammar parser = mock(ParserGrammar.class);
    ParserQuery query = mock(ParserQuery.class);

    Tree tree = Tree.valueOf("(ROOT (VB runs))");
    when(query.getBestParse()).thenReturn(tree);
    when(query.getBestScore()).thenReturn(-0.1);

    when(parser.parserQuery()).thenReturn(query);

    ParserAnnotator annotator = new ParserAnnotator(parser, false, 10);

    CoreLabel token = new CoreLabel();
    token.setWord("runs");
    token.setTag("VB");
    token.setIndex(1);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new Annotation("s");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation annotation = new Annotation("doc");
    List<CoreMap> sents = new ArrayList<>();
    sents.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sents);

    annotator.annotate(annotation);

    Tree t = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(t);

    SemanticGraph graph = sentence.get(SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation.class);
    if (graph != null) {
      for (IndexedWord iw : graph.vertexSet()) {
        Integer sentIndex = iw.get(CoreAnnotations.SentenceIndexAnnotation.class);
        assertNotNull(sentIndex);
        assertEquals(Integer.valueOf(0), sentIndex);
      }
    }
  }
@Test
  public void testParserAnnotatorWithNullTreemapProperty() {
    Properties props = new Properties();
    props.setProperty("parse.model", "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
    props.setProperty("parse.debug", "true");
    

    ParserAnnotator annotator = new ParserAnnotator("parse", props);
    assertNotNull(annotator);
  }
@Test
  public void testParserAnnotatorWithMissingModelPropertyThrowsException() {
    Properties props = new Properties();
    try {
      new ParserAnnotator("custom", props);
      fail("Expected IllegalArgumentException due to missing model property");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("No model specified"));
    }
  }
@Test
  public void testDoOneSentenceHandlesRuntimeInterruptedExceptionGracefully() {
    ParserGrammar parser = mock(ParserGrammar.class);

    ParserQuery query = mock(ParserQuery.class);
    when(query.parse(anyList())).thenThrow(new RuntimeInterruptedException());

    when(parser.parserQuery()).thenReturn(query);

    ParserAnnotator annotator = new ParserAnnotator(parser, false, 100);

    CoreLabel token = new CoreLabel();
    token.setWord("hello");
    token.setTag("UH");
    token.set(CoreAnnotations.IndexAnnotation.class, 1);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new Annotation("sent");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation annotation = new Annotation("doc");
    List<CoreMap> sentList = new ArrayList<>();
    sentList.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentList);

    annotator.annotate(annotation);

    Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(tree);
    assertEquals("X", tree.label().value());
  }
@Test
  public void testEmptyTokenListTriggersGracefulParsing() {
    ParserGrammar parser = mock(ParserGrammar.class);

    when(parser.parserQuery()).thenReturn(mock(ParserQuery.class));

    ParserAnnotator annotator = new ParserAnnotator(parser, false, 100);

    CoreMap sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation annotation = new Annotation("annot");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(tree);
    assertEquals("X", tree.label().value());
  }
@Test
  public void testRequiresWhenParserDoesNotRequireTags() {
    ParserGrammar parser = mock(ParserGrammar.class);

    ParserAnnotator annotator = new ParserAnnotator(parser, false, 100);

    Set<Class<? extends CoreAnnotation>> result = annotator.requires();
    assertFalse(result.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertTrue(result.contains(CoreAnnotations.TokensAnnotation.class));
  }
@Test
  public void testRequirementsSatisfiedWithoutBinaryOrGraph() {
    ParserGrammar parser = mock(ParserGrammar.class);

    ParserAnnotator annotator = new ParserAnnotator(parser, false, 100);

    Set<Class<? extends CoreAnnotation>> result = annotator.requirementsSatisfied();
    assertTrue(result.contains(TreeCoreAnnotations.TreeAnnotation.class));
    assertFalse(result.contains(TreeCoreAnnotations.BinarizedTreeAnnotation.class));
    assertFalse(result.contains(SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation.class));
  }
@Test
  public void testFinishSentenceWithCustomTreeMapAppliesTransformation() {
    ParserGrammar parser = mock(ParserGrammar.class);

    Tree originalTree = Tree.valueOf("(S (VP (VB hello)))");
    Tree transformedTree = Tree.valueOf("(S (VP (VB changed)))");

    Function<Tree, Tree> transformer = new Function<Tree, Tree>() {
      public Tree apply(Tree t) {
        return transformedTree;
      }
    };

    ParserAnnotator annotator = new ParserAnnotator(parser, false, 100, transformer);

    List<Tree> trees = new ArrayList<>();
    trees.add(originalTree);

    CoreMap sentence = new Annotation("sent");
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    try {
      java.lang.reflect.Method method = ParserAnnotator.class.getDeclaredMethod("finishSentence", CoreMap.class, List.class);
      method.setAccessible(true);
      method.invoke(annotator, sentence, trees);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    Tree resultTree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertEquals("S", resultTree.label().value());
    assertEquals("changed", resultTree.firstChild().firstChild().yield().get(0).value());
  }
@Test
  public void testSignatureWithMinimalPropertiesDefaultsApplied() {
    Properties props = new Properties();
    props.setProperty("parse.model", "model.gz");
    String signature = ParserAnnotator.signature("parse", props);

    assertTrue(signature.contains("parse.model:model.gz"));
    assertTrue(signature.contains("parse.debug:false"));
    assertTrue(signature.contains("parse.flags:"));
    assertTrue(signature.contains("parse.maxlen:-1"));
    assertTrue(signature.contains("parse.maxheight:80"));
    assertTrue(signature.contains("parse.maxtime:-1"));
  }
@Test
  public void testKBestSetToZeroYieldsParseFailure() {
    ParserGrammar parser = mock(ParserGrammar.class);

    ParserAnnotator annotator = new ParserAnnotator(parser, false, 100) {
      {
        try {
          java.lang.reflect.Field kBestField = ParserAnnotator.class.getDeclaredField("kBest");
          kBestField.setAccessible(true);
          kBestField.setInt(this, 0); 
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    };

    CoreLabel cl = new CoreLabel();
    cl.setWord("fail");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(cl);

    CoreMap sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation annot = new Annotation("doc");
    annot.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(annot);

    Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(tree);
    assertEquals("X", tree.label().value());
  }
@Test
  public void testNullTreeInKBestReturnsFallbackTree() {
    ParserGrammar parser = mock(ParserGrammar.class);
    ParserQuery query = mock(ParserQuery.class);

    List<ScoredObject<Tree>> kbestList = new ArrayList<>();
    kbestList.add(new ScoredObject<>(null, -999.0));  

    when(parser.parserQuery()).thenReturn(query);
    when(query.parse(anyList())).thenReturn(true);
    when(query.getKBestParses(eq(1))).thenReturn(kbestList);

    ParserAnnotator annotator = new ParserAnnotator(parser, false, 100);

    CoreLabel token = new CoreLabel();
    token.setWord("broken");
    token.setTag("NN");
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new Annotation("sent");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation document = new Annotation("doc");
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(document);

    Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(tree);
    assertEquals("X", tree.label().value());
  }
@Test
  public void testFinishSentenceDoesNotOverwriteExistingBinarizedTree() {
    ParserGrammar parser = mock(ParserGrammar.class);

    ParserAnnotator annotator = new ParserAnnotator(parser, false, 100);

    Tree tree = Tree.valueOf("(S (NP test))");
    Tree binarized = Tree.valueOf("(S (NP bin))");

    CoreMap sentence = new Annotation("sent");
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binarized);

    List<Tree> trees = new ArrayList<>();
    trees.add(tree);

    try {
      java.lang.reflect.Method method = ParserAnnotator.class.getDeclaredMethod("finishSentence", CoreMap.class, List.class);
      method.setAccessible(true);
      method.invoke(annotator, sentence, trees);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    Tree resultBin = sentence.get(TreeCoreAnnotations.BinarizedTreeAnnotation.class);
    assertNotNull(resultBin);  
  }
@Test
  public void testDoOneSentenceWithParserConstraintAnnotationPresent() {
    ParserGrammar parser = mock(ParserGrammar.class);
    ParserQuery query = mock(ParserQuery.class);
    Tree parseTree = Tree.valueOf("(ROOT (NN constrained))");

    when(query.getBestParse()).thenReturn(parseTree);
    when(query.getBestScore()).thenReturn(-0.97);
    when(query.parse(anyList())).thenReturn(true);
    when(parser.parserQuery()).thenReturn(query);

    ParserAnnotator annotator = new ParserAnnotator(parser, false, 100);

    CoreLabel word = new CoreLabel();
    word.setWord("example");
    word.setTag("NN");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(word);

    ParserConstraint constraint = new ParserConstraint(0, 1, "NP");

    CoreMap sentence = new Annotation("test");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(ParserAnnotations.ConstraintAnnotation.class, Collections.singletonList(constraint));

    Annotation document = new Annotation("doc");
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(document);

    Tree resultTree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(resultTree);
    assertEquals("ROOT", resultTree.label().value());
  }
@Test
  public void testMaxHeightZeroSkipsFlattening() {
    ParserGrammar parser = mock(ParserGrammar.class);

    ParserAnnotator annotator = new ParserAnnotator(parser, false, 100) {
      {
        try {
          java.lang.reflect.Field heightField = ParserAnnotator.class.getDeclaredField("maxHeight");
          heightField.setAccessible(true);
          heightField.setInt(this, 0);
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    };

    Tree tall = Tree.valueOf("(ROOT (A (B (C (D (E (F test)))))))");
    List<Tree> trees = new ArrayList<>();
    trees.add(tall);

    CoreMap sentence = new Annotation("s");
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    try {
      java.lang.reflect.Method m = ParserAnnotator.class.getDeclaredMethod("finishSentence", CoreMap.class, List.class);
      m.setAccessible(true);
      m.invoke(annotator, sentence, trees);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    Tree out = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(out);
    assertTrue(out.depth() > 2);
  }
@Test
  public void testEmptySentenceListInAnnotationDoesNotThrow() {
    ParserGrammar parser = mock(ParserGrammar.class);

    ParserAnnotator annotator = new ParserAnnotator(parser, false, 100);

    Annotation blankDoc = new Annotation("empty");
    blankDoc.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());

    annotator.annotate(blankDoc); 
  }
@Test
  public void testDoOneFailedSentenceWithNullTagsResetsToXX() {
    ParserGrammar parser = mock(ParserGrammar.class);

    ParserAnnotator annotator = new ParserAnnotator(parser, false, 100);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("word");
    token1.setTag(null);
    token1.set(CoreAnnotations.IndexAnnotation.class, 1);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("test");
    token2.setTag(null);
    token2.set(CoreAnnotations.IndexAnnotation.class, 2);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    tokens.add(token2);

    CoreMap sentence = new Annotation("failedSent");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation doc = new Annotation("doc");

    annotator.doOneFailedSentence(doc, sentence);

    Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(tree);
    assertEquals("X", tree.label().value());

    String tag1 = tokens.get(0).tag();
    String tag2 = tokens.get(1).tag();

    assertEquals("XX", tag1);
    assertEquals("XX", tag2);
  }
@Test
  public void testConstructorPropertyBasedParserWithAllOptionalPropertiesMissing() {
    Properties props = new Properties();
    props.setProperty("myparser.model", "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");

    ParserAnnotator annotator = new ParserAnnotator("myparser", props);
    assertNotNull(annotator);
  }
@Test
  public void testConstructorWithOriginalDependenciesFlagTrue() {
    Properties props = new Properties();
    props.setProperty("myp.model", "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
    props.setProperty("myp.originalDependencies", "true");
    props.setProperty("myp.buildgraphs", "true");

    ParserAnnotator annotator = new ParserAnnotator("myp", props);
    assertNotNull(annotator);
  }
@Test
  public void testSignatureWithCustomPropsOverridesDefaults() {
    Properties props = new Properties();
    props.setProperty("parse.model", "custom-model.gz");
    props.setProperty("parse.binaryTrees", "false");
    props.setProperty("parse.treemap", "my.CustomTreeMap");

    String signature = ParserAnnotator.signature("parse", props);
    assertTrue(signature.contains("parse.binaryTrees:false"));
    assertTrue(signature.contains("parse.treemap:my.CustomTreeMap"));
  }
@Test
  public void testdoOneSentenceSkipsWhenNoSquashAndTreeIsXLabel() {
    ParserGrammar parser = mock(ParserGrammar.class);

    ParserAnnotator annotator = new ParserAnnotator(parser, false, 100) {
      {
        try {
          java.lang.reflect.Field f = ParserAnnotator.class.getDeclaredField("noSquash");
          f.setAccessible(true);
          f.setBoolean(this, true);
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    };

    CoreLabel token = new CoreLabel();
    token.setWord("skip");
    token.setTag("VB");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    Tree tree = Tree.valueOf("(X)");
    tree.setLabel(new StringLabel("X"));

    CoreMap sentence = new Annotation("s");
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation doc = new Annotation("doc");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(doc);

    Tree output = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertEquals("X", output.label().value());
  }
@Test
  public void testdoOneSentenceSkipsOnTreeWithNullLabel() {
    ParserGrammar parser = mock(ParserGrammar.class);

    ParserAnnotator annotator = new ParserAnnotator(parser, false, 100) {
      {
        try {
          java.lang.reflect.Field f = ParserAnnotator.class.getDeclaredField("noSquash");
          f.setAccessible(true);
          f.setBoolean(this, true);
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    };

    CoreLabel token = new CoreLabel();
    token.setWord("null");
    token.setTag("VB");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    Tree tree = Tree.valueOf("(X)");
    tree.setLabel(null); 

    CoreMap sentence = new Annotation("s");
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation doc = new Annotation("doc");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(doc);

    Tree result = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNull(result.label()); 
  }
@Test
  public void testRequirementsSatisfiedReturnsSupersetWhenBinaryAndGraphTrue() {
    ParserGrammar parser = mock(ParserGrammar.class);
    ParserAnnotator annotator = new ParserAnnotator(parser, true, 100) {
      {
        try {
          java.lang.reflect.Field b = ParserAnnotator.class.getDeclaredField("saveBinaryTrees");
          java.lang.reflect.Field g = ParserAnnotator.class.getDeclaredField("BUILD_GRAPHS");
          b.setAccessible(true);
          g.setAccessible(true);
          b.setBoolean(this, true);
          g.setBoolean(this, true);
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    };

    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();
    assertTrue(satisfied.contains(TreeCoreAnnotations.BinarizedTreeAnnotation.class));
    assertTrue(satisfied.contains(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class));
  }
@Test
  public void testEmptyTokensHandlesGracefullyWithNoTreeSet() {
    ParserGrammar parser = mock(ParserGrammar.class);
    ParserQuery query = mock(ParserQuery.class);
    when(parser.parserQuery()).thenReturn(query);
    when(query.parse(anyList())).thenReturn(true);
    when(query.getBestParse()).thenReturn(null);

    ParserAnnotator annotator = new ParserAnnotator(parser, false, 100);

    
    CoreMap sentence = new Annotation("s");
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation doc = new Annotation("doc");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(doc);

    Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(tree);
    assertEquals("X", tree.label().value());
  }
@Test
  public void testFinishSentenceMaintainsScoreInMappedTree() {
    ParserGrammar parser = mock(ParserGrammar.class);
    Tree originalTree = Tree.valueOf("(ROOT (NN keep))");
    originalTree.setScore(-5.0);

    Tree mappedTree = Tree.valueOf("(ROOT (NN mapped))");

    Function<Tree, Tree> treeMapper = new Function<Tree, Tree>() {
      public Tree apply(Tree tree) {
        mappedTree.setScore(tree.score());
        return mappedTree;
      }
    };

    ParserAnnotator annotator = new ParserAnnotator(parser, false, 100, treeMapper);

    List<Tree> trees = new ArrayList<>();
    trees.add(originalTree);

    CoreMap sentence = new Annotation("sent");
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    try {
      java.lang.reflect.Method m = ParserAnnotator.class.getDeclaredMethod("finishSentence", CoreMap.class, List.class);
      m.setAccessible(true);
      m.invoke(annotator, sentence, trees);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    Tree result = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertEquals("mapped", result.getChild(0).yield().get(0).value());
    assertEquals(-5.0, result.score(), 0.001);
  }
@Test
  public void testAnnotatorAnnotatesMultipleSentences() {
    ParserGrammar parser = mock(ParserGrammar.class);

    ParserQuery query = mock(ParserQuery.class);
    Tree tree = Tree.valueOf("(ROOT (NN sentence))");
    when(query.getBestParse()).thenReturn(tree);
    when(query.getBestScore()).thenReturn(-10.0);
    when(query.parse(anyList())).thenReturn(true);
    when(parser.parserQuery()).thenReturn(query);

    ParserAnnotator annotator = new ParserAnnotator(parser, false, 10);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Hello");
    token1.setTag("NN");

    CoreMap sent1 = new Annotation("sent1");
    sent1.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token1));
    sent1.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    CoreMap sent2 = new Annotation("sent2");
    sent2.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token1));
    sent2.set(CoreAnnotations.SentenceIndexAnnotation.class, 1);

    Annotation annotation = new Annotation("doc");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sent1, sent2));

    annotator.annotate(annotation);

    Tree t1 = sent1.get(TreeCoreAnnotations.TreeAnnotation.class);
    Tree t2 = sent2.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(t1);
    assertNotNull(t2);
    assertEquals("ROOT", t1.label().value());
    assertEquals("ROOT", t2.label().value());
  }
@Test
  public void testDoOneSentenceUsesKBestParsing() {
    ParserGrammar parser = mock(ParserGrammar.class);

    Tree tree = Tree.valueOf("(ROOT (NN data))");
    ScoredObject<Tree> scored = new ScoredObject<>(tree, -2.0);
    List<ScoredObject<Tree>> scoredList = Collections.singletonList(scored);

    ParserQuery query = mock(ParserQuery.class);
    when(query.parse(anyList())).thenReturn(true);
    when(query.getKBestParses(3)).thenReturn(scoredList);
    when(parser.parserQuery()).thenReturn(query);

    ParserAnnotator annotator = new ParserAnnotator(parser, false, 20) {
      {
        try {
          java.lang.reflect.Field kBestField = ParserAnnotator.class.getDeclaredField("kBest");
          kBestField.setAccessible(true);
          kBestField.setInt(this, 3);
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    };

    CoreLabel token = new CoreLabel();
    token.setWord("info");
    token.setTag("NN");
    token.setIndex(1);

    CoreMap sentence = new Annotation("sent");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation doc = new Annotation("doc");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(doc);

    Tree result = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(result);
    assertEquals("ROOT", result.label().value());
  }
@Test
  public void testFallbackFailsGracefullyWhenTokensAreMissingTags() {
    ParserGrammar parser = mock(ParserGrammar.class);

    ParserAnnotator annotator = new ParserAnnotator(parser, false, 100);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("missing");
    token1.setTag(null);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("tags");
    token2.setTag(null);

    List<CoreLabel> tokens = Arrays.asList(token1, token2);

    CoreMap sentence = new Annotation("sent");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation doc = new Annotation("doc");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.doOneFailedSentence(doc, sentence);

    Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(tree);
    assertEquals("X", tree.label().value());
    assertEquals("XX", token1.tag());
    assertEquals("XX", token2.tag());
  }
@Test
  public void testSentenceWithNullSentenceIndexAnnotationHandled() {
    ParserGrammar parser = mock(ParserGrammar.class);

    ParserQuery query = mock(ParserQuery.class);
    Tree tree = Tree.valueOf("(ROOT (NN Hello))");
    when(query.parse(anyList())).thenReturn(true);
    when(query.getBestParse()).thenReturn(tree);
    when(query.getBestScore()).thenReturn(-3.0);
    when(parser.parserQuery()).thenReturn(query);

    ParserAnnotator annotator = new ParserAnnotator(parser, false, 100);

    CoreLabel token = new CoreLabel();
    token.setWord("Hi");
    token.setTag("NN");
    token.setIndex(1);

    List<CoreLabel> tokens = Collections.singletonList(token);

    CoreMap sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    

    Annotation doc = new Annotation("doc");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(doc);

    Tree result = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(result);
    assertEquals("ROOT", result.label().value());
  }
@Test
  public void testConstructorWithEmptyFlagsArrayParsesSuccessfully() {
    String[] flags = new String[0];

    ParserGrammar parser = mock(ParserGrammar.class);
    when(parser.defaultCoreNLPFlags()).thenReturn(new String[0]);

    doNothing().when(parser).setOptionFlags(any(String[].class));

    ParserAnnotator annotator = new ParserAnnotator(parser, true, 5);
    assertNotNull(annotator);
  }
@Test
  public void testFlattenTallTreeToConformToMaxHeight() {
    ParserGrammar parser = mock(ParserGrammar.class);

    ParserAnnotator annotator = new ParserAnnotator(parser, false, 100);

    Tree deepTree = Tree.valueOf("(ROOT (A (B (C (D (E test))))))");
    CoreMap sentence = new Annotation("sent");
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    List<Tree> trees = Collections.singletonList(deepTree);

    try {
      java.lang.reflect.Method method = ParserAnnotator.class.getDeclaredMethod("finishSentence", CoreMap.class, List.class);
      method.setAccessible(true);
      method.invoke(annotator, sentence, trees);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    Tree flattened = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(flattened);
    assertTrue(flattened.depth() <= 80);
  }
@Test
  public void testConvertFlagsToArrayHandlesMultipleSpaces() throws Exception {
    String flagsString = "   -flag1    -flag2  -flag3  ";
    java.lang.reflect.Method method = ParserAnnotator.class.getDeclaredMethod("convertFlagsToArray", String.class);
    method.setAccessible(true);

    String[] result = (String[]) method.invoke(null, flagsString);
    assertEquals(3, result.length);
    assertEquals("-flag1", result[0]);
    assertEquals("-flag2", result[1]);
    assertEquals("-flag3", result[2]);
  } 
}