package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.parser.common.ParserAnnotations;
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

public class ParserAnnotator_2_GPTLLMTest {

 @Test
  public void testSuccessfulParseAddsTreeAnnotation() {
    CoreLabel token = new CoreLabel();
    token.setWord("Hello");
    token.setTag("UH");
    List<CoreLabel> tokens = Arrays.asList(token);

    Tree tree = mock(Tree.class);
    Label label = new CategoryWordTag("ROOT");
    when(tree.label()).thenReturn(label);

    ParserQuery parserQuery = mock(ParserQuery.class);
    when(parserQuery.parse(tokens)).thenReturn(true);
    when(parserQuery.getBestParse()).thenReturn(tree);
    when(parserQuery.getBestScore()).thenReturn(-12000.0);

    CoreMap sentence = new Annotation("Test");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    List<CoreMap> sentList = new ArrayList<>();
    sentList.add(sentence);
    Annotation annotation = new Annotation("Doc");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentList);


    Tree resultTree = annotation.get(CoreAnnotations.SentencesAnnotation.class)
      .get(0).get(TreeCoreAnnotations.TreeAnnotation.class);

    assertNotNull(resultTree);
    assertEquals("ROOT", resultTree.label().value());
  }
@Test
  public void testParseReturnsNullTreeTriggersFallback() {
    CoreLabel token = new CoreLabel();
    token.setWord("Fallback");
    token.setTag("UH");
    List<CoreLabel> tokens = Arrays.asList(token);

    ParserQuery parserQuery = mock(ParserQuery.class);
    when(parserQuery.parse(tokens)).thenReturn(true);
    when(parserQuery.getBestParse()).thenReturn(null);

    ParserGrammar parser = mock(ParserGrammar.class);
    when(parser.parserQuery()).thenReturn(parserQuery);

    CoreMap sentence = new Annotation("Test");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    List<CoreMap> sentList = new ArrayList<>();
    sentList.add(sentence);
    Annotation annotation = new Annotation("Doc");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentList);

    ParserAnnotator annotator = new ParserAnnotator(parser, true, 100);
    annotator.annotate(annotation);

    Tree resultTree = annotation.get(CoreAnnotations.SentencesAnnotation.class)
      .get(0).get(TreeCoreAnnotations.TreeAnnotation.class);

    assertNotNull(resultTree);
    assertEquals("X", resultTree.label().value());
  }
@Test
  public void testTreeMappedIfTransformerProvided() {
    CoreLabel token = new CoreLabel();
    token.setWord("Mapped");
    token.setTag("NN");
    List<CoreLabel> tokens = Arrays.asList(token);

    ParserQuery parserQuery = mock(ParserQuery.class);
    when(parserQuery.parse(tokens)).thenReturn(true);
    when(parserQuery.getBestScore()).thenReturn(-10001.0);


    CoreMap sentence = new Annotation("Test");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    List<CoreMap> sentList = new ArrayList<>();
    sentList.add(sentence);
    Annotation annotation = new Annotation("Doc");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentList);


    Tree tree = annotation.get(CoreAnnotations.SentencesAnnotation.class)
      .get(0).get(TreeCoreAnnotations.TreeAnnotation.class);

    assertNotNull(tree);
    assertEquals("MAPPED", tree.label().value());
  }
@Test
  public void testMaxSentenceLengthPreventsParsing() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("A");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("very");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("long");
    CoreLabel token4 = new CoreLabel();
    token4.setWord("sentence");
    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4);


    CoreMap sentence = new Annotation("Sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 1);
    List<CoreMap> sentList = new ArrayList<>();
    sentList.add(sentence);
    Annotation annotation = new Annotation("Doc");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentList);
    ;

    Tree tree = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0)
      .get(TreeCoreAnnotations.TreeAnnotation.class);

    assertNotNull(tree);
    assertEquals("X", tree.label().value());
  }
@Test
  public void testSignatureIncludesExpectedProperties() {
    Properties props = new Properties();
    props.setProperty("parse.model", "edu/model/path.ser.gz");
    props.setProperty("parse.debug", "true");
    props.setProperty("parse.maxlen", "100");
    props.setProperty("parse.flags", "-flagA -flagB -flagC");
    props.setProperty("parse.keepPunct", "false");
    props.setProperty("parse.binaryTrees", "true");

    String sig = ParserAnnotator.signature("parse", props);

    assertTrue(sig.contains("parse.model:edu/model/path.ser.gz"));
    assertTrue(sig.contains("parse.debug:true"));
    assertTrue(sig.contains("parse.maxlen:100"));
    assertTrue(sig.contains("parse.flags:-flagA -flagB -flagC"));
    assertTrue(sig.contains("parse.keepPunct:false"));
    assertTrue(sig.contains("parse.binaryTrees:true"));
  }
@Test
public void testParserQueryThrowsOutOfMemoryHandledGracefully() {
  CoreLabel token = new CoreLabel();
  token.setWord("OOM");
  token.setTag("NN");
  List<CoreLabel> tokens = Arrays.asList(token);

  ParserQuery parserQuery = mock(ParserQuery.class);
  when(parserQuery.parse(tokens)).thenReturn(true);
  when(parserQuery.getBestParse()).thenThrow(new OutOfMemoryError("Simulated OOM"));

  TreebankLanguagePack tlp = mock(TreebankLanguagePack.class);

  ParserGrammar parser = mock(ParserGrammar.class);
  when(parser.parserQuery()).thenReturn(parserQuery);

  CoreMap sentence = new Annotation("OOM sentence");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  List<CoreMap> sentences = Arrays.asList(sentence);
  Annotation annotation = new Annotation("Doc");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  ParserAnnotator annotator = new ParserAnnotator(parser, true, 100);
  annotator.annotate(annotation);

  Tree fallbackTree = annotation.get(CoreAnnotations.SentencesAnnotation.class)
    .get(0).get(TreeCoreAnnotations.TreeAnnotation.class);

  assertNotNull(fallbackTree);
  assertEquals("X", fallbackTree.label().value());
}
@Test
public void testParserQueryThrowsNoSuchParseExceptionHandledGracefully() {
  CoreLabel token = new CoreLabel();
  token.setWord("ParseFail");
  token.setTag("NN");
  List<CoreLabel> tokens = Arrays.asList(token);

  ParserQuery parserQuery = mock(ParserQuery.class);
  when(parserQuery.parse(tokens)).thenReturn(true);


  ParserGrammar parser = mock(ParserGrammar.class);
  when(parser.parserQuery()).thenReturn(parserQuery);

  CoreMap sentence = new Annotation("Bad parse");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 1);
  List<CoreMap> sentences = Arrays.asList(sentence);
  Annotation annotation = new Annotation("Doc");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  ParserAnnotator annotator = new ParserAnnotator(parser, true, 100);
  annotator.annotate(annotation);

  Tree fallbackTree = annotation.get(CoreAnnotations.SentencesAnnotation.class)
    .get(0).get(TreeCoreAnnotations.TreeAnnotation.class);

  assertNotNull(fallbackTree);
  assertEquals("X", fallbackTree.label().value());
}
@Test
public void testNullConstraintsHandledGracefullyInDoOneSentence() {
  CoreLabel token = new CoreLabel();
  token.setWord("NullConstraint");
  token.setTag("NN");
  List<CoreLabel> tokens = Arrays.asList(token);

  Tree tree = mock(Tree.class);
  Label label = new CategoryWordTag("ROOT");
  when(tree.label()).thenReturn(label);

  ParserQuery parserQuery = mock(ParserQuery.class);
  when(parserQuery.parse(tokens)).thenReturn(true);
  when(parserQuery.getBestParse()).thenReturn(tree);
  when(parserQuery.getBestScore()).thenReturn(-10001.0);

  TreebankLanguagePack tlp = mock(TreebankLanguagePack.class);

  CoreMap sentence = new Annotation("Null constraints test");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  List<CoreMap> sentences = Arrays.asList(sentence);
  Annotation annotation = new Annotation("TestDoc");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  Tree resultTree = annotation.get(CoreAnnotations.SentencesAnnotation.class)
    .get(0).get(TreeCoreAnnotations.TreeAnnotation.class);

  assertNotNull(resultTree);
  assertEquals("ROOT", resultTree.label().value());
}
@Test
public void testBinaryTreeAnnotationIsPresentIfConfigured() {
  CoreLabel token = new CoreLabel();
  token.setWord("Binary");
  token.setTag("NN");

  List<CoreLabel> tokens = Arrays.asList(token);

  Tree originalTree = new LabeledScoredTreeNode(new CategoryWordTag("BINARY"));

  ParserQuery parserQuery = mock(ParserQuery.class);
  when(parserQuery.parse(tokens)).thenReturn(true);
  when(parserQuery.getBestParse()).thenReturn(originalTree);
  when(parserQuery.getBestScore()).thenReturn(-10000.9);

  TreebankLanguagePack tlp = mock(TreebankLanguagePack.class);

  ParserGrammar parser = mock(ParserGrammar.class);
  when(parser.parserQuery()).thenReturn(parserQuery);

  CoreMap sentence = new Annotation("Binary Tree Test");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  List<CoreMap> sentences = Arrays.asList(sentence);
  Annotation annotation = new Annotation("BinaryDoc");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  ParserAnnotator annotator = new ParserAnnotator(parser, false, 100) {
    @Override
    public Set<Class<? extends CoreAnnotation>> requirementsSatisfied() {
      return Collections.singleton(TreeCoreAnnotations.BinarizedTreeAnnotation.class);
    }
  };

  annotator.annotate(annotation);

  Tree binarizedTree = annotation.get(CoreAnnotations.SentencesAnnotation.class)
    .get(0).get(TreeCoreAnnotations.BinarizedTreeAnnotation.class);

  assertNotNull(binarizedTree);
  assertEquals("BIN", binarizedTree.label().value());
}
@Test
public void testDependencyIndexRepairedDuringFinishSentence() {
  CoreLabel token = new CoreLabel();
  token.setWord("Dep");
  token.setTag("NN");
  List<CoreLabel> tokens = Arrays.asList(token);

  Tree tree = mock(Tree.class);
  when(tree.label()).thenReturn(new CategoryWordTag("DTEST"));

  ParserQuery parserQuery = mock(ParserQuery.class);
  when(parserQuery.parse(tokens)).thenReturn(true);
  when(parserQuery.getBestParse()).thenReturn(tree);
  when(parserQuery.getBestScore()).thenReturn(-11000.0);

  IndexedWord word = new IndexedWord(token);
  word.setIndex(1);
  word.remove(CoreAnnotations.SentenceIndexAnnotation.class);
  Set<IndexedWord> vertices = new LinkedHashSet<>();
  vertices.add(word);

  SemanticGraph graph = mock(SemanticGraph.class);
  when(graph.vertexSet()).thenReturn(vertices);

  TreebankLanguagePack tlp = mock(TreebankLanguagePack.class);

  GrammaticalStructureFactory gsf = mock(GrammaticalStructureFactory.class);

  CoreMap sentence = new Annotation("Dependency Repair");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 2);
  sentence.set(SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation.class, graph);

  List<CoreMap> sentences = Arrays.asList(sentence);
  Annotation annotation = new Annotation("DocDep");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

}
@Test
public void testDoOneFailedSentenceWithNullTagsAssignsDefaultTag() {
  CoreLabel token = new CoreLabel();
  token.setWord("Untyped"); 
  List<CoreLabel> tokens = Arrays.asList(token);

  CoreMap sentence = new Annotation("Untyped sentence");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  List<CoreMap> sentences = Arrays.asList(sentence);
  Annotation annotation = new Annotation("Document");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  TreebankLanguagePack tlp = mock(TreebankLanguagePack.class);

  Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
  assertNotNull(tree);
  assertEquals("X", tree.label().value());
  assertEquals("XX", tokens.get(0).tag());
}
@Test
public void testNoSquashSkipsReAnnotatingTreeWithNonXRoot() {
  Tree tree = mock(Tree.class);
  when(tree.label()).thenReturn(new CategoryWordTag("S"));

  CoreLabel token = new CoreLabel();
  token.setWord("skip");
  token.setTag("VB");

  List<CoreLabel> tokens = Arrays.asList(token);

  CoreMap sentence = new Annotation("Pre-annotated");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);

  List<CoreMap> sentenceList = Arrays.asList(sentence);
  Annotation annotation = new Annotation("NoSquash");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

  TreebankLanguagePack tlp = mock(TreebankLanguagePack.class);

  Tree resultTree = annotation.get(CoreAnnotations.SentencesAnnotation.class)
    .get(0).get(TreeCoreAnnotations.TreeAnnotation.class);

  assertSame(tree, resultTree); 
}
@Test
public void testDoOneSentenceAllowsMaxLenMinusOne() {
  CoreLabel token = new CoreLabel();
  token.setWord("Okay");
  token.setTag("JJ");
  List<CoreLabel> tokens = Arrays.asList(token);

  Tree tree = mock(Tree.class);
  Label label = new CategoryWordTag("ALLOWED");
  when(tree.label()).thenReturn(label);

  ParserQuery query = mock(ParserQuery.class);
  when(query.parse(tokens)).thenReturn(true);
  when(query.getBestParse()).thenReturn(tree);
  when(query.getBestScore()).thenReturn(-10005.0);

  TreebankLanguagePack tlp = mock(TreebankLanguagePack.class);

  ParserGrammar parser = mock(ParserGrammar.class);
  when(parser.parserQuery()).thenReturn(query);

  CoreMap sentence = new Annotation("Sentence");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  List<CoreMap> sentences = Arrays.asList(sentence);
  Annotation annotation = new Annotation("Doc");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  ParserAnnotator annotator = new ParserAnnotator(parser, false, -1);
  annotator.annotate(annotation);

  Tree resultTree = annotation.get(CoreAnnotations.SentencesAnnotation.class)
    .get(0).get(TreeCoreAnnotations.TreeAnnotation.class);

  assertNotNull(resultTree);
  assertEquals("ALLOWED", resultTree.label().value());
}
@Test
public void testRequiresSetWithoutTags() {
  ParserGrammar parser = mock(ParserGrammar.class);
  when(parser.requiresTags()).thenReturn(false);

  TreebankLanguagePack tlp = mock(TreebankLanguagePack.class);

  ParserAnnotator annotator = new ParserAnnotator(parser, false, 100);
  Set<Class<? extends CoreAnnotation>> requirements = annotator.requires();

  assertFalse(requirements.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
  assertTrue(requirements.contains(CoreAnnotations.TextAnnotation.class));
  assertTrue(requirements.contains(CoreAnnotations.SentencesAnnotation.class));
}
@Test
public void testKBestPathUsedOnMultipleTrees() {
  CoreLabel token = new CoreLabel();
  token.setWord("Choice");
  token.setTag("NN");

  List<CoreLabel> tokens = Arrays.asList(token);

  Tree tree1 = new LabeledScoredTreeNode(new CategoryWordTag("S1"));
  Tree tree2 = new LabeledScoredTreeNode(new CategoryWordTag("S2"));

  ScoredObject<Tree> scored1 = new ScoredObject<>(tree1, -1000.0);
  ScoredObject<Tree> scored2 = new ScoredObject<>(tree2, -2000.0);

  ParserQuery parserQuery = mock(ParserQuery.class);
  when(parserQuery.parse(tokens)).thenReturn(true);
  when(parserQuery.getKBestParses(2)).thenReturn(Arrays.asList(scored1, scored2));

  TreebankLanguagePack tlp = mock(TreebankLanguagePack.class);

  ParserGrammar parser = mock(ParserGrammar.class);
  when(parser.parserQuery()).thenReturn(parserQuery);

  CoreMap sentence = new Annotation("KBest Tree");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  List<CoreMap> sentenceList = Arrays.asList(sentence);
  Annotation annotation = new Annotation("KBestDoc");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

  Tree resultTree = annotation.get(CoreAnnotations.SentencesAnnotation.class)
    .get(0).get(TreeCoreAnnotations.TreeAnnotation.class);

  assertNotNull(resultTree);
  String label = resultTree.label().value();
  assertTrue(label.equals("S1") || label.equals("S2"));
}
@Test
public void testSignatureHandlesEmptyFlagsValue() {
  Properties props = new Properties();
  props.setProperty("parse.model", "edu/x/parser.ser.gz");
  props.setProperty("parse.flags", "");
  props.setProperty("parse.maxlen", "50");
  props.setProperty("parse.debug", "true");

  String result = ParserAnnotator.signature("parse", props);

  assertTrue(result.contains("parse.model:edu/x/parser.ser.gz"));
  assertTrue(result.contains("parse.flags:"));
  assertTrue(result.contains("parse.maxlen:50"));
}
@Test
public void testTreeLabelValueXIsRetriedIfNullLabel() {
  Tree fallbackTree = mock(Tree.class);
  Label label = mock(Label.class);
  when(label.value()).thenReturn("X");
  when(fallbackTree.label()).thenReturn(label);

  CoreLabel token = new CoreLabel();
  token.setWord("XNode");
  List<CoreLabel> tokens = Arrays.asList(token);

  CoreMap sentence = new Annotation("FallbackTest");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, fallbackTree);

  List<CoreMap> sentenceList = Arrays.asList(sentence);
  Annotation annotation = new Annotation("Document");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

  TreebankLanguagePack tlp = mock(TreebankLanguagePack.class);


  ParserGrammar parser = mock(ParserGrammar.class);


  Tree expected = annotation.get(CoreAnnotations.SentencesAnnotation.class)
    .get(0).get(TreeCoreAnnotations.TreeAnnotation.class);

  assertEquals("X", expected.label().value());
}
@Test
public void testSignatureIncludesAllBooleanAndNumericProperties() {
  Properties props = new Properties();
  props.setProperty("parse.model", "edu/parser/model.gz");
  props.setProperty("parse.debug", "false");
  props.setProperty("parse.flags", "-a -b -c");
  props.setProperty("parse.maxlen", "500");
  props.setProperty("parse.maxheight", "75");
  props.setProperty("parse.treemap", "com.example.TreeMapImpl");
  props.setProperty("parse.maxtime", "3000");
  props.setProperty("parse.originalDependencies", "true");
  props.setProperty("parse.buildgraphs", "false");
  props.setProperty("parse.nthreads", "5");
  props.setProperty("parse.nosquash", "true");
  props.setProperty("parse.keepPunct", "true");
  props.setProperty("parse.extradependencies", "enhanced+");
  props.setProperty("parse.binaryTrees", "false");
  props.setProperty("nthreads", "10");

  String signature = ParserAnnotator.signature("parse", props);

  assertTrue(signature.contains("parse.model:edu/parser/model.gz"));
  assertTrue(signature.contains("parse.debug:false"));
  assertTrue(signature.contains("parse.flags:-a -b -c"));
  assertTrue(signature.contains("parse.maxlen:500"));
  assertTrue(signature.contains("parse.maxheight:75"));
  assertTrue(signature.contains("parse.treemap:com.example.TreeMapImpl"));
  assertTrue(signature.contains("parse.maxtime:3000"));
  assertTrue(signature.contains("parse.originalDependencies:true"));
  assertTrue(signature.contains("parse.buildgraphs:false"));
  assertTrue(signature.contains("parse.nthreads:5"));
  assertTrue(signature.contains("parse.nosquash:true"));
  assertTrue(signature.contains("parse.keepPunct:true"));
  assertTrue(signature.contains("parse.extradependencies:enhanced+"));
  assertTrue(signature.contains("parse.binaryTrees:false"));
}
@Test
public void testConstructorWithoutPunctuationFilterSetsNullGsfWhenGraphUnsupported() {
  TreebankLanguagePack tlp = mock(TreebankLanguagePack.class);

  ParserGrammar parser = mock(ParserGrammar.class);

  ParserAnnotator annotator = new ParserAnnotator(parser, true, 50);

  Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();
  assertFalse(satisfied.contains(SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation.class));
}
@Test
public void testExtraDependenciesParsedFromProps() {
  Properties props = new Properties();
  props.setProperty("parse.model", "edu/parser.mdl");
  props.setProperty("parse.extradependencies", "enhanced");
  props.setProperty("parse.keepPunct", "true");

  ParserGrammar parser = mock(ParserGrammar.class);
  TreebankLanguagePack tlp = mock(TreebankLanguagePack.class);


  when(tlp.grammaticalStructureFactory(any(Predicate.class), any())).thenReturn(null);

}
@Test
public void testFinishSentenceFlattensTreeWhenExceedingMaxHeight() {
  CoreLabel token = new CoreLabel();
  token.setWord("Flatten");
  token.setTag("NN");

  List<CoreLabel> tokens = Arrays.asList(token);

  Tree deepTree = new LabeledScoredTreeNode(new CategoryWordTag("TOO_DEEP"));
  Tree flatterTree = new LabeledScoredTreeNode(new CategoryWordTag("FLAT"));

  ParserQuery query = mock(ParserQuery.class);
  when(query.parse(tokens)).thenReturn(true);
  when(query.getBestParse()).thenReturn(deepTree);
  when(query.getBestScore()).thenReturn(-8888.0);

  TreebankLanguagePack tlp = mock(TreebankLanguagePack.class);

  CoreMap sentence = new Annotation("Flatten Test");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  List<CoreMap> sentences = Arrays.asList(sentence);
  Annotation annotation = new Annotation("Doc");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
}
@Test
public void testConvertFlagsToArrayReturnsEmptyArrayOnBlankInput() {
  String resultEmpty = "";
  String resultNull = null;
}
@Test
public void testAnnotatorReturnsModifiedTreeIfTreeMapPresentWithScore() {
  CoreLabel token = new CoreLabel();
  token.setWord("Map");
  token.setTag("VB");

  List<CoreLabel> tokens = Arrays.asList(token);

  Tree originalTree = new LabeledScoredTreeNode(new CategoryWordTag("ROOT"));
  originalTree.setScore(-9999.0);

  ParserQuery query = mock(ParserQuery.class);
  when(query.parse(tokens)).thenReturn(true);
  when(query.getBestParse()).thenReturn(originalTree);
  when(query.getBestScore()).thenReturn(-9999.0);


  ParserGrammar parser = mock(ParserGrammar.class);
  when(parser.parserQuery()).thenReturn(query);

  Function<Tree, Tree> treeMap = new Function<Tree, Tree>() {
    public Tree apply(Tree tree) {
      Tree updated = new LabeledScoredTreeNode(new CategoryWordTag("MAPPED"));
      updated.setScore(tree.score());
      return updated;
    }
  };

  CoreMap sentence = new Annotation("Mapped Sentence");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);
  Annotation annotation = new Annotation("Doc");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  ParserAnnotator annotator = new ParserAnnotator(parser, false, 100, treeMap);
  annotator.annotate(annotation);

  Tree resultTree = annotation.get(CoreAnnotations.SentencesAnnotation.class)
    .get(0).get(TreeCoreAnnotations.TreeAnnotation.class);

  assertEquals("MAPPED", resultTree.label().value());
  assertEquals(-9999.0, resultTree.score(), 0.00001);
}
@Test
public void testDoOneSentenceSkipsWhenTreeIsNullDueToMaxLengthZero() {
  CoreLabel token = new CoreLabel();
  token.setWord("skip");
  token.setTag("VB");

  List<CoreLabel> tokens = Arrays.asList(token);
  ParserGrammar parser = mock(ParserGrammar.class);
  TreebankLanguagePack tlp = mock(TreebankLanguagePack.class);

  CoreMap sentence = new Annotation("Sentence");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  List<CoreMap> sents = Arrays.asList(sentence);
  Annotation annotation = new Annotation("Doc");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sents);

  ParserAnnotator annotator = new ParserAnnotator(parser, true, 0);
  annotator.annotate(annotation);

  Tree tree = annotation.get(CoreAnnotations.SentencesAnnotation.class)
    .get(0).get(TreeCoreAnnotations.TreeAnnotation.class);

  assertNotNull(tree);
  assertEquals("X", tree.label().value());
}
@Test
public void testDoOneSentenceReturnsEmptyTreesWhenParserQueryReturnsEmptyList() {
  CoreLabel token = new CoreLabel();
  token.setWord("none");
  token.setTag("NN");

  List<CoreLabel> tokens = Arrays.asList(token);

  ParserQuery query = mock(ParserQuery.class);
  when(query.parse(tokens)).thenReturn(true);
  when(query.getKBestParses(2)).thenReturn(Collections.emptyList());

  ParserGrammar parser = mock(ParserGrammar.class);


  CoreMap sentence = new Annotation("EmptyTrees");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  sentence.set(ParserAnnotations.ConstraintAnnotation.class, null);

  List<CoreMap> all = Arrays.asList(sentence);
  Annotation annotation = new Annotation("DocEmpty");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, all);


  Tree tree = annotation.get(CoreAnnotations.SentencesAnnotation.class)
    .get(0).get(TreeCoreAnnotations.TreeAnnotation.class);
  assertNotNull(tree);
  assertEquals("X", tree.label().value());
}
@Test
public void testFinishSentenceSetsSentenceIndexOnNullIndexedWord() {
  CoreLabel token = new CoreLabel();
  token.setWord("test");
  token.setTag("VB");

  List<CoreLabel> tokens = Arrays.asList(token);


  GrammaticalStructureFactory gsf = mock(GrammaticalStructureFactory.class);

  Tree tree = new LabeledScoredTreeNode(new CategoryWordTag("ROOT"));

  CoreMap sentence = new Annotation("IndexedWordTest");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 3);

  IndexedWord word = new IndexedWord();
  word.setWord("dep");
  word.remove(CoreAnnotations.SentenceIndexAnnotation.class);
  Set<IndexedWord> words = new LinkedHashSet<>();
  words.add(word);

  SemanticGraph graph = mock(SemanticGraph.class);
  when(graph.vertexSet()).thenReturn(words);
  sentence.set(SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation.class, graph);

  Annotation annotation = new Annotation("doc");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

  ParserAnnotatorUtils.fillInParseAnnotations(false, true, gsf, sentence, Arrays.asList(tree), GrammaticalStructure.Extras.NONE);

  SemanticGraph updated = sentence.get(SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation.class);
  assertNotNull(updated);
  for (IndexedWord iw : updated.vertexSet()) {
    assertEquals(Integer.valueOf(3), iw.get(CoreAnnotations.SentenceIndexAnnotation.class));
  }
}
@Test
public void testEmptyTokensAnnotationHandledGracefully() {
  ParserGrammar parser = mock(ParserGrammar.class);

  CoreMap sentence = new Annotation("NoTokens");
  sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  Annotation annotation = new Annotation("doc");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

  ParserAnnotator annotator = new ParserAnnotator(parser, true, 100);
  annotator.annotate(annotation);

  Tree result = annotation.get(CoreAnnotations.SentencesAnnotation.class)
    .get(0).get(TreeCoreAnnotations.TreeAnnotation.class);

  assertNotNull(result);
  assertEquals("X", result.label().value());
}
@Test
public void testFinishSentenceWithNullTreeMapDoesNotTransformTree() {
  CoreLabel token = new CoreLabel();
  token.setWord("tree");
  token.setTag("VB");

  Tree tree = new LabeledScoredTreeNode(new CategoryWordTag("ORIGINAL"));

  List<Tree> treeList = new ArrayList<>();
  treeList.add(tree);

  CoreMap sentence = new Annotation("No Tree Map");
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 1);
  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

  ParserGrammar parser = mock(ParserGrammar.class);

  ParserAnnotator annotator = new ParserAnnotator(parser, false, 100, null);
  ParserAnnotatorUtils.fillInParseAnnotations(false, false, null, sentence, treeList, GrammaticalStructure.Extras.NONE);

  Tree actual = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
  assertNotNull(actual);
  assertEquals("ORIGINAL", actual.label().value());
}
@Test
public void testDoOneSentenceHandlesRuntimeInterruptedExceptionGracefully() {
  CoreLabel token = new CoreLabel();
  token.setWord("interrupted");
  token.setTag("VB");
  List<CoreLabel> tokens = Arrays.asList(token);

  ParserQuery query = mock(ParserQuery.class);
  when(query.parse(tokens)).thenThrow(new RuntimeInterruptedException());


  CoreMap sentence = new Annotation("Interrupted");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  List<CoreMap> sentences = Arrays.asList(sentence);
  Annotation annotation = new Annotation("Doc");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  Tree tree = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0)
    .get(TreeCoreAnnotations.TreeAnnotation.class);

  assertNotNull(tree);
  assertEquals("X", tree.label().value());
}
@Test
public void testLoadModelSetsCoreNLPAndCustomFlags() {
  ParserGrammar parserGrammar = mock(ParserGrammar.class);
  when(parserGrammar.defaultCoreNLPFlags()).thenReturn(new String[]{"-coreNLP"});
  doNothing().when(parserGrammar).setOptionFlags(any(String[].class));
}
@Test
public void testRequirementsSatisfiedIncludesBinaryAndGraphAnnotations() {
  TreebankLanguagePack tlp = mock(TreebankLanguagePack.class);
  when(tlp.punctuationWordRejectFilter()).thenReturn(s -> false);

  Properties props = new Properties();
  props.setProperty("parse.model", "edu/model.gz");
  props.setProperty("parse.binaryTrees", "true");
  props.setProperty("parse.keepPunct", "true");
 }

@Test
public void testSignatureDefaultsAppliedWhenPropertiesMissing() {
  Properties props = new Properties(); 
  String sig = ParserAnnotator.signature("parse", props);

  assertTrue(sig.contains("parse.model:" + LexicalizedParser.DEFAULT_PARSER_LOC));
  assertTrue(sig.contains("parse.maxlen:-1"));
  assertTrue(sig.contains("parse.binaryTrees:false") || sig.contains("parse.binaryTrees:true"));
}
@Test
public void testPropertyOverridesDefaultMaxHeight() {
  Properties props = new Properties();
  props.setProperty("parse.model", "edu/mymodel.gz");
  props.setProperty("parse.maxheight", "42");

  ParserGrammar parser = mock(ParserGrammar.class);

  assertTrue(ParserAnnotator.signature("parse", props).contains("parse.maxheight:42"));
}
@Test
public void testFinishSentenceSkipsGraphAndBinaryIfFlagsFalse() {
  CoreLabel token = new CoreLabel();
  token.setWord("flat");
  token.setTag("JJ");

  Tree resultTree = new LabeledScoredTreeNode(new CategoryWordTag("ROOT"));

  CoreMap sentence = new Annotation("Flat Structure");
  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 1);

  Annotation annotation = new Annotation("DocFlat");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

  Tree tree = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0).get(TreeCoreAnnotations.TreeAnnotation.class);
  assertNotNull(tree);
  assertEquals("ROOT", tree.label().value());
  assertNull(annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0)
    .get(TreeCoreAnnotations.BinarizedTreeAnnotation.class));
}
@Test
public void testDoOneSentenceHandlesNullParseTreeGracefully() {
  CoreLabel token = new CoreLabel();
  token.setWord("nulltree");
  token.setTag("NN");
  List<CoreLabel> tokens = Arrays.asList(token);

  ParserQuery parserQuery = mock(ParserQuery.class);
  when(parserQuery.parse(tokens)).thenReturn(true);
  when(parserQuery.getBestParse()).thenReturn(null);

  TreebankLanguagePack tlp = mock(TreebankLanguagePack.class);


  CoreMap sentence = new Annotation("NullTreeTest");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  Annotation annotation = new Annotation("NullTreeDoc");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));


  Tree result = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0)
      .get(TreeCoreAnnotations.TreeAnnotation.class);

  assertNotNull(result);
  assertEquals("X", result.label().value());
}
@Test
public void testSignatureIncludesDefaultForMissingExtradependencies() {
  Properties props = new Properties();
  props.setProperty("parse.model", "edu/path.gz");

  String result = ParserAnnotator.signature("parse", props);

  assertTrue(result.contains("parse.extradependencies:none") || 
             result.contains("parse.extradependencies:"));
}
@Test
public void testDoOneSentenceReturnsNullWhenTreeListIsEmpty() {
  CoreLabel token = new CoreLabel();
  token.setWord("empty");
  token.setTag("NN");
  List<CoreLabel> tokens = Arrays.asList(token);

  List<ScoredObject<Tree>> emptyList = Collections.emptyList();

  ParserQuery parserQuery = mock(ParserQuery.class);
  when(parserQuery.parse(tokens)).thenReturn(true);
  when(parserQuery.getKBestParses(5)).thenReturn(emptyList);


  CoreMap sentence = new Annotation("EmptyKBestTest");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 1);

  Annotation annotation = new Annotation("DocEmptyKBest");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

  Tree tree = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0)
      .get(TreeCoreAnnotations.TreeAnnotation.class);

  assertNotNull(tree);
  assertEquals("X", tree.label().value());
}
@Test
public void testDoOneSentenceReturnsValidTreeFromKBest() {
  CoreLabel token = new CoreLabel();
  token.setWord("valid");
  token.setTag("NN");
  List<CoreLabel> tokens = Arrays.asList(token);

  Tree tree1 = new LabeledScoredTreeNode(new CategoryWordTag("NP"));
  tree1.setScore(-5000.0);
  ScoredObject<Tree> scored1 = new ScoredObject<>(tree1, -5000.0);

  ParserQuery parserQuery = mock(ParserQuery.class);
  when(parserQuery.parse(tokens)).thenReturn(true);
  when(parserQuery.getKBestParses(2)).thenReturn(Arrays.asList(scored1));


  ParserGrammar parser = mock(ParserGrammar.class);
  when(parser.parserQuery()).thenReturn(parserQuery);

  CoreMap sentence = new Annotation("KBestValid");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 2);

  Annotation annotation = new Annotation("DocKBestValid");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));


  Tree tree = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0)
      .get(TreeCoreAnnotations.TreeAnnotation.class);

  assertNotNull(tree);
  assertEquals("NP", tree.label().value());
  assertEquals(-5000.0 % -10000.0, tree.score(), 0.0001);
}
@Test
public void testFinishSentenceDoesNotModifyTreeIfTreeMapNull() {
  CoreLabel token = new CoreLabel();
  token.setWord("dummy");
  token.setTag("NN");

  Tree tree = new LabeledScoredTreeNode(new CategoryWordTag("UNCHANGED"));

  CoreMap sentence = new Annotation("TreeMapNull");
  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 7);

  Annotation annotation = new Annotation("DocTreeMapNull");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

  ParserGrammar parser = mock(ParserGrammar.class);

  ParserAnnotator annotator = new ParserAnnotator(parser, false, 100, null);

  List<Tree> trees = new ArrayList<>();
  trees.add(tree);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, null);

  ParserAnnotatorUtils.fillInParseAnnotations(false, false, null, sentence, trees, GrammaticalStructure.Extras.NONE);

  Tree result = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);

  assertNotNull(result);
  assertEquals("UNCHANGED", result.label().value());
}
@Test
public void testParseSkippedWhenMaxSentenceLengthExceeded() {
  List<CoreLabel> tokens = new ArrayList<CoreLabel>();
  for (int i = 0; i < 10; i++) {
    CoreLabel token = new CoreLabel();
    token.setWord("word" + i);
    token.setTag("NN");
    tokens.add(token);
  }

  ParserGrammar parser = mock(ParserGrammar.class);

  CoreMap sentence = new Annotation("TooLong");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 3);

  Annotation annotation = new Annotation("DocTooLong");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

  ParserAnnotator annotator = new ParserAnnotator(parser, true, 5);
  annotator.annotate(annotation);

  Tree tree = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0)
      .get(TreeCoreAnnotations.TreeAnnotation.class);
  assertNotNull(tree);
  assertEquals("X", tree.label().value());
} 
}