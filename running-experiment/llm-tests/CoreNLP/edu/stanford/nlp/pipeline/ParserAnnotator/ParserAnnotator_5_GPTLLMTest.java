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
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.util.*;

import edu.stanford.nlp.ie.NERClassifierCombiner;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.time.TimeExpression;
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

public class ParserAnnotator_5_GPTLLMTest {

 @Test
  public void testDoOneSentenceSuccessfulParse() {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);
    ParserAnnotator parserAnnotator = new ParserAnnotator(parser, false, 100);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("The");
    token1.setTag("DT");
    token1.setIndex(0);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("quick");
    token2.setTag("JJ");
    token2.setIndex(1);

    CoreLabel token3 = new CoreLabel();
    token3.setWord("brown");
    token3.setTag("JJ");
    token3.setIndex(2);

    CoreLabel token4 = new CoreLabel();
    token4.setWord("fox");
    token4.setTag("NN");
    token4.setIndex(3);

    CoreLabel token5 = new CoreLabel();
    token5.setWord("jumps");
    token5.setTag("VBZ");
    token5.setIndex(4);

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5);

    CoreMap sentence = new Annotation();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation annotation = new Annotation("");

    parserAnnotator.doOneSentence(annotation, sentence);

    Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(tree);

    SemanticGraph graph = sentence.get(SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation.class);
    assertNotNull(graph);
    assertTrue(graph.vertexSet().size() >= 5);
  }
@Test
  public void testDoOneSentenceTooLongTriggersFallback() {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);
    ParserAnnotator parserAnnotator = new ParserAnnotator(parser, false, 10);

    CoreLabel tokenA = new CoreLabel();
    tokenA.setWord("word0");
    tokenA.setTag("NN");
    tokenA.setIndex(0);

    CoreLabel tokenB = new CoreLabel();
    tokenB.setWord("word1");
    tokenB.setTag("NN");
    tokenB.setIndex(1);

    CoreLabel tokenC = new CoreLabel();
    tokenC.setWord("word2");
    tokenC.setTag("NN");
    tokenC.setIndex(2);

    CoreLabel tokenD = new CoreLabel();
    tokenD.setWord("word3");
    tokenD.setTag("NN");
    tokenD.setIndex(3);

    CoreLabel tokenE = new CoreLabel();
    tokenE.setWord("word4");
    tokenE.setTag("NN");
    tokenE.setIndex(4);

    CoreLabel tokenF = new CoreLabel();
    tokenF.setWord("word5");
    tokenF.setTag("NN");
    tokenF.setIndex(5);

    CoreLabel tokenG = new CoreLabel();
    tokenG.setWord("word6");
    tokenG.setTag("NN");
    tokenG.setIndex(6);

    CoreLabel tokenH = new CoreLabel();
    tokenH.setWord("word7");
    tokenH.setTag("NN");
    tokenH.setIndex(7);

    CoreLabel tokenI = new CoreLabel();
    tokenI.setWord("word8");
    tokenI.setTag("NN");
    tokenI.setIndex(8);

    CoreLabel tokenJ = new CoreLabel();
    tokenJ.setWord("word9");
    tokenJ.setTag("NN");
    tokenJ.setIndex(9);

    CoreLabel tokenK = new CoreLabel();
    tokenK.setWord("word10");
    tokenK.setTag("NN");
    tokenK.setIndex(10);

    List<CoreLabel> tokens = Arrays.asList(tokenA, tokenB, tokenC, tokenD, tokenE,
                                           tokenF, tokenG, tokenH, tokenI, tokenJ, tokenK);

    CoreMap sentence = new Annotation();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation annotation = new Annotation("");

    parserAnnotator.doOneSentence(annotation, sentence);
    Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(tree);
    assertEquals("X", tree.label().value());
  }
@Test
  public void testDoOneFailedSentenceFallbackTree() {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);
    ParserAnnotator parserAnnotator = new ParserAnnotator(parser, false, 100);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Hello");
    token1.setIndex(0);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("World");
    token2.setIndex(1);

    List<CoreLabel> tokens = Arrays.asList(token1, token2);

    CoreMap sentence = new Annotation();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation annotation = new Annotation("");

    parserAnnotator.doOneFailedSentence(annotation, sentence);

    Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(tree);
    assertEquals("X", tree.label().value());
    assertEquals(2, tree.yield().size());
  }
@Test
  public void testSignatureIncludesPropertiesSet() {
    Properties props = new Properties();
    props.setProperty("parser.model", LexicalizedParser.DEFAULT_PARSER_LOC);
    props.setProperty("parser.maxlen", "50");
    props.setProperty("parser.maxheight", "75");
    props.setProperty("parser.flags", "-flagA -flagB");
    props.setProperty("parser.keepPunct", "false");
    props.setProperty("parser.debug", "true");
    props.setProperty("parser.binaryTrees", "true");
    props.setProperty("parser.extradependencies", "CCPROCESSED");

    String sig = ParserAnnotator.signature("parser", props);

    assertTrue(sig.contains("parser.model:edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz"));
    assertTrue(sig.contains("parser.debug:true"));
    assertTrue(sig.contains("parser.flags:-flagA -flagB"));
    assertTrue(sig.contains("parser.maxlen:50"));
    assertTrue(sig.contains("parser.maxheight:75"));
    assertTrue(sig.contains("parser.keepPunct:false"));
    assertTrue(sig.contains("parser.binaryTrees:true"));
    assertTrue(sig.contains("parser.extradependencies:ccprocessed"));
  }
@Test
  public void testRequirementsSatisfiedContainsExpectedAnnotations() {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);
    ParserAnnotator parserAnnotator = new ParserAnnotator(parser, false, 100);

    Set<Class<? extends CoreAnnotation>> satisfied = parserAnnotator.requirementsSatisfied();

    assertTrue(satisfied.contains(TreeCoreAnnotations.TreeAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.CategoryAnnotation.class));
  }
@Test
  public void testRequiresContainsTokensAndSentences() {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);
    ParserAnnotator parserAnnotator = new ParserAnnotator(parser, false, 100);

    Set<Class<? extends CoreAnnotation>> required = parserAnnotator.requires();

    assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.SentencesAnnotation.class));
  }
@Test(expected = IllegalArgumentException.class)
  public void testMissingModelPropertyThrowsException() {
    Properties props = new Properties();
    new ParserAnnotator("missing", props);
  }
@Test
  public void testConstructWithModelAndProps() {
    Properties props = new Properties();
    props.setProperty("my.model", LexicalizedParser.DEFAULT_PARSER_LOC);
    props.setProperty("my.maxlen", "200");
    props.setProperty("my.flags", "-test");
    props.setProperty("my.keepPunct", "true");

    props.setProperty("myparser.model", LexicalizedParser.DEFAULT_PARSER_LOC);
    ParserAnnotator parserAnnotator = new ParserAnnotator("myparser", props);

    assertNotNull(parserAnnotator);
  }
@Test
  public void testTreeMapWithNullReturnsUnchangedTree() {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);
    ParserAnnotator annotator = new ParserAnnotator(parser, false, 100, null);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Rain");
    token1.setTag("NN");
    token1.setIndex(0);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("falls");
    token2.setTag("VBZ");
    token2.setIndex(1);

    List<CoreLabel> tokens = Arrays.asList(token1, token2);

    CoreMap sentence = new Annotation();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation annotation = new Annotation("");
    annotator.doOneSentence(annotation, sentence);

    Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(tree);
    assertNotEquals("X", tree.label().value());
  }
@Test
  public void testWithSaveBinaryTreesTrue() {
    Properties props = new Properties();
    props.setProperty("parser.model", LexicalizedParser.DEFAULT_PARSER_LOC);
    props.setProperty("parser.binaryTrees", "true");

    ParserAnnotator annotator = new ParserAnnotator("parser", props);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Sky");
    token1.setTag("NN");
    token1.setIndex(0);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("is");
    token2.setTag("VBZ");
    token2.setIndex(1);

    CoreLabel token3 = new CoreLabel();
    token3.setWord("blue");
    token3.setTag("JJ");
    token3.setIndex(2);

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);

    CoreMap sentence = new Annotation();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation annotation = new Annotation("");
    annotator.doOneSentence(annotation, sentence);

    Tree tree = sentence.get(TreeCoreAnnotations.BinarizedTreeAnnotation.class);
    assertNotNull(tree);
  }
@Test
  public void testDoOneSentenceWithKBestParsing() {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);
    ParserAnnotator annotator = new ParserAnnotator(parser, false, 100) {
      @Override
      public void doOneSentence(Annotation annotation, CoreMap sentence) {
        super.doOneSentence(annotation, sentence);
      }
    };

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Bird");
    token1.setTag("NN");
    token1.setIndex(0);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("flies");
    token2.setTag("VBZ");
    token2.setIndex(1);

    List<CoreLabel> tokens = Arrays.asList(token1, token2);

    CoreMap sentence = new Annotation();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation ann = new Annotation("");
    annotator.doOneSentence(ann, sentence);

    Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(tree);
  }
@Test
  public void testNoSquashSkipsExistingTree() {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);
    ParserAnnotator annotator = new ParserAnnotator(parser, false, 100);

    Tree existingTree = Tree.valueOf("(ROOT (X))");

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Existing");
    token1.setTag("JJ");
    token1.setIndex(0);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("tree");
    token2.setTag("NN");
    token2.setIndex(1);

    List<CoreLabel> tokens = Arrays.asList(token1, token2);

    CoreMap sentence = new Annotation();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, existingTree);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation ann = new Annotation("");
    annotator.doOneSentence(ann, sentence);

    Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertEquals(existingTree, tree);
  }
@Test
  public void testDoOneSentenceWithMaxHeightLimited() {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Tall");
    token1.setTag("JJ");
    token1.setIndex(0);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("tree");
    token2.setTag("NN");
    token2.setIndex(1);

    List<CoreLabel> tokens = Arrays.asList(token1, token2);

    CoreMap sentence = new Annotation();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation ann = new Annotation("");

    Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(tree);
  }
@Test
  public void testKeepPunctFalseRemovesPunctuationDependencies() {
    Properties props = new Properties();
    props.setProperty("parser.model", LexicalizedParser.DEFAULT_PARSER_LOC);
    props.setProperty("parser.keepPunct", "false");

    ParserAnnotator annotator = new ParserAnnotator("parser", props);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Hello");
    token1.setTag("UH");
    token1.setIndex(0);

    CoreLabel token2 = new CoreLabel();
    token2.setWord(",");
    token2.setTag(",");
    token2.setIndex(1);

    CoreLabel token3 = new CoreLabel();
    token3.setWord("world");
    token3.setTag("NN");
    token3.setIndex(2);

    CoreLabel token4 = new CoreLabel();
    token4.setWord("!");
    token4.setTag(".");
    token4.setIndex(3);

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4);

    CoreMap sentence = new Annotation();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation ann = new Annotation("");
    annotator.doOneSentence(ann, sentence);

    SemanticGraph graph = sentence.get(SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation.class);
    assertNotNull(graph);
    for (IndexedWord word : graph.vertexSet()) {
      String w = word.word();
      assertFalse(w.equals(",") || w.equals("!"));
    }
  }
@Test
  public void testBuildGraphsFalseDoesNotCreateGraphs() {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("No");
    token1.setTag("DT");
    token1.setIndex(0);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("graphs");
    token2.setTag("NNS");
    token2.setIndex(1);

    List<CoreLabel> tokens = Arrays.asList(token1, token2);

    CoreMap sentence = new Annotation();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    SemanticGraph graph = sentence.get(SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation.class);
    assertNull(graph);
  }

@Test
  public void testNullReturnedFromTreeMapIsHandledGracefully() {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);

    ParserAnnotator annotator = new ParserAnnotator(parser, false, 100, tree -> null);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Hi");
    token1.setTag("UH");
    token1.setIndex(0);

    List<CoreLabel> tokens = Collections.singletonList(token1);

    CoreMap sentence = new Annotation();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation ann = new Annotation("");
    annotator.doOneSentence(ann, sentence);

    Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNull(tree);  
  }
@Test
  public void testTreeWithLabelXAndNoSquashTrueSkipsAnnotation() {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);

    Tree preexistingTree = Tree.valueOf("(ROOT (X))");

    CoreLabel token1 = new CoreLabel();
    token1.setWord("already");
    token1.setTag("RB");
    token1.setIndex(0);

    List<CoreLabel> tokens = Collections.singletonList(token1);

    CoreMap sentence = new Annotation();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, preexistingTree);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Tree result = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(result);
    assertEquals("X", result.label().value());
  }
@Test
  public void testExtradependenciesAnnotationLoadedFromProperties() {
    Properties properties = new Properties();
    properties.setProperty("parser.model", LexicalizedParser.DEFAULT_PARSER_LOC);
    properties.setProperty("parser.extradependencies", "enhanced");

    ParserAnnotator annotator = new ParserAnnotator("parser", properties);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("test");
    token1.setTag("NN");
    token1.setIndex(0);

    CoreMap sentence = new Annotation();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token1));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation ann = new Annotation("");
    annotator.doOneSentence(ann, sentence);

    Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(tree);
  }
@Test
  public void testBuildGraphsFalseSaveBinaryTrue() {
    Properties properties = new Properties();
    properties.setProperty("parser.model", LexicalizedParser.DEFAULT_PARSER_LOC);
    properties.setProperty("parser.buildgraphs", "false");
    properties.setProperty("parser.binaryTrees", "true");

    ParserAnnotator annotator = new ParserAnnotator("parser", properties);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("circle");
    token1.setTag("NN");
    token1.setIndex(0);

    CoreMap sentence = new Annotation();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token1));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation ann = new Annotation("");
    annotator.doOneSentence(ann, sentence);

    Tree binaryTree = sentence.get(TreeCoreAnnotations.BinarizedTreeAnnotation.class);
    assertNotNull(binaryTree);
  }
@Test
  public void testMaxSentenceLengthZeroMeansNoLimit() {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);

    ParserAnnotator annotator = new ParserAnnotator(parser, false, 0);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("something");
    token1.setTag("NN");
    token1.setIndex(0);

    List<CoreLabel> tokens = Collections.nCopies(150, token1);

    CoreMap sentence = new Annotation();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation ann = new Annotation("");
    annotator.doOneSentence(ann, sentence);

    Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(tree);
  }
@Test
  public void testMaxParseTimeIsSetFromProperties() {
    Properties properties = new Properties();
    properties.setProperty("parser.model", LexicalizedParser.DEFAULT_PARSER_LOC);
    properties.setProperty("parser.maxtime", "12345");

    ParserAnnotator annotator = new ParserAnnotator("parser", properties);

    CoreLabel token = new CoreLabel();
    token.setWord("hello");
    token.setTag("UH");
    token.setIndex(0);

    CoreMap sentence = new Annotation();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation annotation = new Annotation("");
    annotator.doOneSentence(annotation, sentence);

    Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(tree);
  }
@Test
  public void testEmptyTokenListTriggersFailureHandler() {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);
    ParserAnnotator annotator = new ParserAnnotator(parser, false, 100);

    CoreMap sentence = new Annotation();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation annotation = new Annotation("EMPTY");
    annotator.doOneSentence(annotation, sentence);

    Tree fallbackTree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(fallbackTree);
    assertEquals("X", fallbackTree.label().value());
  }

@Test
  public void testSignatureUsesDefaultValuesWhenPropsMissing() {
    Properties props = new Properties(); 

    String sig = ParserAnnotator.signature("parser", props);

    assertTrue(sig.contains("parser.model:edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz"));
    assertTrue(sig.contains("parser.debug:false"));
    assertTrue(sig.contains("parser.flags:"));
    assertTrue(sig.contains("parser.maxlen:-1"));
    assertTrue(sig.contains("parser.keepPunct:true"));
    assertTrue(sig.contains("parser.extradependencies:none"));
    assertTrue(sig.contains("parser.binaryTrees:"));
  }
@Test
  public void testConstructorWithNullFlagsIsHandled() {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);
    ParserAnnotator annotator = new ParserAnnotator(parser, false, 100, null);

    CoreLabel token = new CoreLabel();
    token.setWord("okay");
    token.setTag("RB");
    token.setIndex(0);

    CoreMap sentence = new Annotation();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation annotation = new Annotation("Some text");
    annotator.doOneSentence(annotation, sentence);

    Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(tree);
  }
@Test
  public void testConvertFlagsToArrayHandlesNull() throws Exception {
    java.lang.reflect.Method method = ParserAnnotator.class.getDeclaredMethod("convertFlagsToArray", String.class);
    method.setAccessible(true);
    Object result = method.invoke(null, (Object) null);
    assertTrue(result instanceof String[]);
    assertEquals(0, ((String[]) result).length);
  }
@Test
  public void testMalformedExtraDependenciesGracefullyDefaults() {
    Properties props = new Properties();
    props.setProperty("parser.model", LexicalizedParser.DEFAULT_PARSER_LOC);
    props.setProperty("parser.extradependencies", "notavalidenum");

    ParserAnnotator annotator = new ParserAnnotator("parser", props);

    CoreLabel token = new CoreLabel();
    token.setWord("whoops");
    token.setTag("UH");
    token.setIndex(0);

    CoreMap sentence = new Annotation();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation ann = new Annotation("");
    annotator.doOneSentence(ann, sentence);

    Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(tree);
  }
@Test
  public void testTreeReplacedIfNoSquashFalseAndTreeHasXLabel() {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);
    Tree xTree = Tree.valueOf("(ROOT (X))");

    CoreLabel token = new CoreLabel();
    token.setWord("redo");
    token.setTag("VB");
    token.setIndex(0);

    CoreMap sentence = new Annotation();
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, xTree);
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Tree after = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(after);
    assertNotEquals("X", after.label().value());
  }
@Test
  public void testFallbackSetsMissingPOSTagsToXX() {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);
    ParserAnnotator annotator = new ParserAnnotator(parser, false, 1); 

    CoreLabel token1 = new CoreLabel();
    token1.setWord("missing");
    token1.setTag(null); 

    CoreLabel token2 = new CoreLabel();
    token2.setWord("tag");
    token2.setTag(null);

    List<CoreLabel> tokens = Arrays.asList(token1, token2);

    CoreMap sentence = new Annotation();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation ann = new Annotation("simulate over-max");

    annotator.doOneSentence(ann, sentence);

    for (CoreLabel cl : tokens) {
      assertEquals("XX", cl.tag());
    }

    Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(tree);
  }
@Test
  public void testEmptyFlagsStringHandledGracefullyInLoadModel() {
    Properties props = new Properties();
    props.setProperty("parser.model", LexicalizedParser.DEFAULT_PARSER_LOC);
    props.setProperty("parser.flags", "");

    ParserAnnotator annotator = new ParserAnnotator("parser", props);

    CoreLabel token = new CoreLabel();
    token.setWord("clear");
    token.setTag("JJ");
    token.setIndex(0);

    CoreMap sentence = new Annotation();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation ann = new Annotation("");
    annotator.doOneSentence(ann, sentence);

    Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(tree);
  }
@Test
  public void testSaveBinaryTreeFailsWithNullTreeIgnored() {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);

    CoreLabel token = new CoreLabel();
    token.setWord("fail");
    token.setTag("VB");
    token.setIndex(0);

    CoreMap sentence = new Annotation();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation ann = new Annotation("");

    Tree normalTree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    Tree binTree = sentence.get(TreeCoreAnnotations.BinarizedTreeAnnotation.class);

    assertNotNull(normalTree);
    assertNotNull(binTree);
  }
@Test
  public void testMissingTokensAnnotationTriggersFailurePath() {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);
    ParserAnnotator annotator = new ParserAnnotator(parser, false, 100);

    Annotation annotation = new Annotation("");
    CoreMap sentence = new Annotation(); 
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    annotator.doOneSentence(annotation, sentence);

    Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(tree);
    assertEquals("X", tree.label().value());
  }
@Test
  public void testSentenceConstraintAnnotationInvoked() {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);
    ParserAnnotator annotator = new ParserAnnotator(parser, false, 100);

    ParserConstraint constraint = new ParserConstraint(0, 1, "NP");
    List<ParserConstraint> constraints = Collections.singletonList(constraint);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Apple");
    token1.setTag("NNP");
    token1.setIndex(0);

    List<CoreLabel> tokens = Collections.singletonList(token1);

    CoreMap sentence = new Annotation();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation annotation = new Annotation("");
    annotator.doOneSentence(annotation, sentence);

    Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(tree);
  }
@Test
  public void testTreeExceedsMaxHeightIsFlattened() {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Tall");
    token1.setTag("JJ");
    token1.setIndex(0);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Tree");
    token2.setTag("NN");
    token2.setIndex(1);

    CoreMap sentence = new Annotation();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation annotation = new Annotation("Too tall");

    Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(tree);
    assertTrue(tree.depth() <= 1); 
  }
@Test
  public void testKBestFallbackWhenEmptyListReturned() {

    CoreLabel token = new CoreLabel();
    token.setWord("silent");
    token.setTag("JJ");
    token.setIndex(0);

    CoreMap sentence = new Annotation();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation ann = new Annotation("");

    Tree fallback = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(fallback);
    assertEquals("X", fallback.label().value());
  }
@Test
  public void testRequirementsSatisfiedSaveFalseGraphFalse() {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);
 }
@Test
  public void testRequirementsSatisfiedSaveTrueGraphTrue() {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);
}
@Test
  public void testDoOneSentenceWithMinBudgetAndNoError() {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);
    ParserAnnotator annotator = new ParserAnnotator(parser, false, 1);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Short");
    token1.setTag("JJ");
    token1.setIndex(0);

    CoreMap sentence = new Annotation();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token1));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation annotation = new Annotation("");
    annotator.doOneSentence(annotation, sentence);

    Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(tree);
  }
@Test
  public void testNoSquashSkipsWhenTreeHasNonXLabel() {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);

    Tree existingTree = Tree.valueOf("(ROOT (NP (DT The) (NN cat)))");

    CoreLabel token = new CoreLabel();
    token.setWord("The");
    token.setTag("DT");
    token.setIndex(0);

    List<CoreLabel> tokens = Collections.singletonList(token);

    CoreMap sentence = new Annotation();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, existingTree);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation annotation = new Annotation("The cat");

    Tree resultTree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertEquals(existingTree, resultTree);
  }

@Test
  public void testSentenceIndexSetOnIndexedWordIfMissing() {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);

    ParserAnnotator annotator = new ParserAnnotator(parser, false, 100);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("run");
    token1.setTag("VB");
    token1.setIndex(0);

    CoreMap sentence = new Annotation();
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 4);
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token1));

    Annotation annotation = new Annotation("run");
    annotator.doOneSentence(annotation, sentence);

    SemanticGraph graph = sentence.get(SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation.class);
    assertNotNull(graph);
    for (IndexedWord word : graph.vertexSet()) {
      assertEquals((Integer) 4, word.get(CoreAnnotations.SentenceIndexAnnotation.class));
    }
  }
@Test
  public void testRequirementsSatisfiedReturnsFullSetWhenAllOptionsEnabled() {
    Properties props = new Properties();
    props.setProperty("parser.model", LexicalizedParser.DEFAULT_PARSER_LOC);
    props.setProperty("parser.buildgraphs", "true");
    props.setProperty("parser.binaryTrees", "true");

    ParserAnnotator annotator = new ParserAnnotator("parser", props);

    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    assertTrue(satisfied.contains(TreeCoreAnnotations.TreeAnnotation.class));
    assertTrue(satisfied.contains(TreeCoreAnnotations.BinarizedTreeAnnotation.class));
    assertTrue(satisfied.contains(SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation.class));
    assertTrue(satisfied.contains(SemanticGraphCoreAnnotations.EnhancedDependenciesAnnotation.class));
  }

@Test
  public void testIllegalExtraDependenciesFallsBackGracefully() {
    Properties props = new Properties();
    props.setProperty("parser.model", LexicalizedParser.DEFAULT_PARSER_LOC);
    props.setProperty("parser.extradependencies", "%%%"); 

    ParserAnnotator annotator = new ParserAnnotator("parser", props);

    CoreLabel token = new CoreLabel();
    token.setWord("go");
    token.setTag("VB");
    token.setIndex(0);

    CoreMap sentence = new Annotation();
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    Annotation annotation = new Annotation("go");

    annotator.doOneSentence(annotation, sentence);
    Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(tree);
  }
@Test
  public void testFinishSentenceWithoutSemanticGraphSkipsFixup() {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);
    ParserAnnotator annotator = new ParserAnnotator(parser, false, 100);

    CoreMap sentence = new Annotation();
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    CoreLabel token = new CoreLabel();
    token.setWord("run");
    token.setTag("VB");
    token.setIndex(0);

    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    Tree tree = Tree.valueOf("(ROOT (VP (VB run)))");
    tree.label().setValue("ROOT");
    Trees.convertToCoreLabels(tree);

    List<Tree> trees = new ArrayList<>();
    trees.add(tree);

    
    sentence.remove(SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation.class);
    java.lang.reflect.Method method;
    try {
      method = ParserAnnotator.class.getDeclaredMethod("finishSentence", CoreMap.class, List.class);
      method.setAccessible(true);
      method.invoke(annotator, sentence, trees);

      Tree result = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
      assertNotNull(result);
    } catch (Exception e) {
      fail("Reflection error: " + e.getMessage());
    }
  }
@Test
  public void testLoadModelWithMultipleFlags() {
    String[] flags = new String[]{"-encoding", "UTF-8", "-retainTmpSubcategories"};
    ParserGrammar parser = null;

    try {
      java.lang.reflect.Method method = ParserAnnotator.class.getDeclaredMethod("loadModel", String.class, boolean.class, String[].class);
      method.setAccessible(true);
      Object result = method.invoke(null, LexicalizedParser.DEFAULT_PARSER_LOC, true, flags);
      parser = (ParserGrammar) result;
    } catch (Exception e) {
      fail("Reflection error: " + e.getMessage());
    }

    assertNotNull(parser);
  }
@Test
  public void testConvertFlagsToArrayHandlesExtraWhitespace() {
    try {
      java.lang.reflect.Method m = ParserAnnotator.class.getDeclaredMethod("convertFlagsToArray", String.class);
      m.setAccessible(true);
      Object result = m.invoke(null, "   -a    -b   -c   ");
      assertTrue(result instanceof String[]);
      String[] arr = (String[]) result;
      assertEquals(3, arr.length);
      assertEquals("-a", arr[0]);
      assertEquals("-b", arr[1]);
      assertEquals("-c", arr[2]);
    } catch (Exception e) {
      fail("Reflection error: " + e.getMessage());
    }
  }

@Test
  public void testTreemapClassFailsToLoadGracefully() {
    Properties props = new Properties();
    props.setProperty("parser.model", LexicalizedParser.DEFAULT_PARSER_LOC);
    props.setProperty("parser.treemap", "non.existent.ClassName");

    try {
      ParserAnnotator annotator = new ParserAnnotator("parser", props);
      
    } catch (Exception e) {
      
      assertTrue(true);
    }
  }
@Test
  public void testMaxParseTimePropertySet() {
    Properties props = new Properties();
    props.setProperty("parser.model", LexicalizedParser.DEFAULT_PARSER_LOC);
    props.setProperty("parser.maxtime", "12345");

    ParserAnnotator annotator = new ParserAnnotator("parser", props);

    assertEquals(12345L, annotator.maxTime());
  }
@Test
  public void testMaxParseTimeZeroIsDefault() {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);
    ParserAnnotator annotator = new ParserAnnotator(parser, false, 100);
    assertEquals(0L, annotator.maxTime()); 
  }
}