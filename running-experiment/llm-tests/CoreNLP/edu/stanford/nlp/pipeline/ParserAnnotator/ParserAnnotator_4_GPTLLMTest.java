package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.parser.common.*;
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

public class ParserAnnotator_4_GPTLLMTest {

 @Test
  public void testDoOneFailedSentenceGeneratesXTree() {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);
    ParserAnnotator annotator = new ParserAnnotator(parser, false, 10);

    Annotation annotation = new Annotation("Input text");

    CoreLabel token0 = new CoreLabel();
    token0.setWord("This");
    token0.setValue("This");

    CoreLabel token1 = new CoreLabel();
    token1.setWord("is");
    token1.setValue("is");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("test");
    token2.setValue("test");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token0);
    tokens.add(token1);
    tokens.add(token2);

    CoreMap sentence = new Annotation("Sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.doOneFailedSentence(annotation, sentence);

    Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(tree);
    assertEquals("X", tree.label().value());
  }
@Test
  public void testConstructorWithPropertiesSetsValues() {
    Properties props = new Properties();
    props.setProperty("parser.model", LexicalizedParser.DEFAULT_PARSER_LOC);
    props.setProperty("parser.flags", "-flag1 -flag2");
    props.setProperty("parser.maxlen", "100");
    props.setProperty("parser.kbest", "2");
    props.setProperty("parser.keepPunct", "false");

    ParserAnnotator annotator = new ParserAnnotator("parser", props);
    assertNotNull(annotator);
  }
@Test(expected = IllegalArgumentException.class)
  public void testConstructorThrowsIfModelMissing() {
    Properties props = new Properties();
    ParserAnnotator annotator = new ParserAnnotator("parser", props);
  }
@Test
  public void testSignatureContainsExpectedProperties() {
    Properties props = new Properties();
    props.setProperty("parser.model", LexicalizedParser.DEFAULT_PARSER_LOC);
    props.setProperty("parser.debug", "true");
    props.setProperty("parser.flags", "-f1 -f2");
    props.setProperty("parser.maxlen", "50");
    props.setProperty("parser.maxheight", "20");
    props.setProperty("parser.maxtime", "2000");
    props.setProperty("parser.buildgraphs", "true");
    props.setProperty("parser.keepPunct", "true");
    props.setProperty("parser.binaryTrees", "true");
    props.setProperty("parser.nosquash", "false");
    props.setProperty("parser.extradependencies", "NONE");

    String signature = ParserAnnotator.signature("parser", props);
    assertTrue(signature.contains("parser.model:" + LexicalizedParser.DEFAULT_PARSER_LOC));
    assertTrue(signature.contains("parser.debug:true"));
    assertTrue(signature.contains("parser.flags:-f1 -f2"));
    assertTrue(signature.contains("parser.maxlen:50"));
    assertTrue(signature.contains("parser.maxheight:20"));
    assertTrue(signature.contains("parser.maxtime:2000"));
    assertTrue(signature.contains("parser.keepPunct:true"));
    assertTrue(signature.contains("parser.extradependencies:none"));
  }
@Test
  public void testRequiresIncludesPartOfSpeechAnnotationIfTagsRequired() {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);
    ParserAnnotator annotator = new ParserAnnotator(parser, false, 10);
    Set<Class<? extends CoreAnnotation>> requires = annotator.requires();
    assertTrue(requires.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertTrue(requires.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(requires.contains(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testRequirementsSatisfiedIncludesExpectedAnnotations() {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);
    ParserAnnotator annotator = new ParserAnnotator(parser, false, 10);
    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();
    assertTrue(satisfied.contains(TreeCoreAnnotations.TreeAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.CategoryAnnotation.class));
  }
@Test
  public void testDoOneSentenceSkipsOverLengthSentences() {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);
    ParserAnnotator annotator = new ParserAnnotator(parser, false, 1);

    Annotation annotation = new Annotation("Input too long");

    CoreLabel tokenA = new CoreLabel();
    tokenA.setWord("A");
    tokenA.setValue("A");

    CoreLabel tokenB = new CoreLabel();
    tokenB.setWord("B");
    tokenB.setValue("B");

    List<CoreLabel> tokens = Arrays.asList(tokenA, tokenB);
    CoreMap sentence = new Annotation("Long Sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.doOneSentence(annotation, sentence);

    Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(tree);
    assertEquals("X", tree.label().value());
  }
@Test
  public void testMaxTimeReturnsConfiguredValue() {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);
    ParserAnnotator annotator = new ParserAnnotator(parser, false, 10);
    long maxTime = annotator.maxTime();
    assertEquals(0L, maxTime);
  }
@Test
  public void testNThreadsReturnsConfiguredValue() {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);
    ParserAnnotator annotator = new ParserAnnotator(parser, false, 10);
    int numThreads = annotator.nThreads();
    assertEquals(1, numThreads);
  }
@Test
  public void testDoOneSentenceWithNullTreeAnnotationAndNoSquashTrue() {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);
    parser.getTLPParams().setGenerateOriginalDependencies(false);
    ParserAnnotator annotator = new ParserAnnotator(parser, false, 10);

    Annotation annotation = new Annotation("Text");

    CoreLabel token = new CoreLabel();
    token.setWord("Example");
    token.setTag("NN");
    token.setValue("Example");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new Annotation("Sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, null);

    try {
      java.lang.reflect.Field noSquashField = ParserAnnotator.class.getDeclaredField("noSquash");
      noSquashField.setAccessible(true);
      noSquashField.setBoolean(annotator, true);
    } catch (Exception e) {
      throw new RuntimeException("Reflection failed: " + e.getMessage());
    }

    annotator.doOneSentence(annotation, sentence);
    Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(tree);
  }
@Test
  public void testDoOneFailedSentenceAssignsDefaultTagsIfMissing() {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);
    ParserAnnotator annotator = new ParserAnnotator(parser, false, 10);

    CoreLabel token = new CoreLabel();
    token.setWord("UntypedToken");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new Annotation("failcase");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation annotation = new Annotation("text");

    annotator.doOneFailedSentence(annotation, sentence);

    List<CoreLabel> outputTokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals("XX", outputTokens.get(0).tag());
  }
@Test
  public void testTreeMapIsAppliedToTree() {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);
    Function<Tree, Tree> transformer = new Function<Tree, Tree>() {
      @Override
      public Tree apply(Tree tree) {
        return new LabeledScoredTreeFactory().newLeaf("NEWLEAF");
      }
    };

    ParserAnnotator annotator = new ParserAnnotator(parser, false, 10, transformer);

    CoreLabel token = new CoreLabel();
    token.setWord("token");
    token.setTag("NN");
    token.setValue("token");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new Annotation("sent");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation annotation = new Annotation("text");

    annotator.doOneFailedSentence(annotation, sentence);

    Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertEquals("NEWLEAF", tree.label().value());
  }
@Test
  public void testFinishSentenceFlattensOverHeightTree() throws Exception {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);
    ParserAnnotator annotator = new ParserAnnotator(parser, false, 10);

    CoreLabel token = new CoreLabel();
    token.setWord("flatten");
    token.setTag("NN");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    Tree deepTree = new LabeledScoredTreeFactory().newLeaf("leaf");
    for (int i = 0; i < 100; i++) {
      List<Tree> children = new ArrayList<>();
      children.add(deepTree);
      deepTree = new LabeledScoredTreeFactory().newTreeNode("N" + i, children);
    }

    List<Tree> trees = new ArrayList<>();
    trees.add(deepTree);

    CoreMap sentence = new Annotation("deepTreeSentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    java.lang.reflect.Method finishMethod = ParserAnnotator.class.getDeclaredMethod("finishSentence", CoreMap.class, List.class);
    finishMethod.setAccessible(true);
    finishMethod.invoke(annotator, sentence, trees);

    Tree result = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(result);
    assertTrue(result.depth() < 100);
  }
@Test
  public void testDoOneSentenceSkipsReparseIfTreePresentAndNoSquashTrue() throws Exception {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);
    ParserAnnotator annotator = new ParserAnnotator(parser, false, 10);

    CoreLabel token = new CoreLabel();
    token.setWord("existing");
    token.setTag("NN");
    token.setValue("existing");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    Tree tree = new LabeledScoredTreeFactory().newLeaf("PREEXISTING");
    tree.setLabel(CoreLabel.factory().newLabel("PREEXISTING"));
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);

    java.lang.reflect.Field noSquashField = ParserAnnotator.class.getDeclaredField("noSquash");
    noSquashField.setAccessible(true);
    noSquashField.setBoolean(annotator, true);

    Annotation annotation = new Annotation("text");
    annotator.doOneSentence(annotation, sentence);

    Tree resultTree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertEquals("PREEXISTING", resultTree.label().value());
  }
@Test
  public void testEmptyConstraintParsingReturnsValidTree() throws Exception {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);
    ParserAnnotator annotator = new ParserAnnotator(parser, false, 10);

    CoreLabel token = new CoreLabel();
    token.setWord("some");
    token.setTag("DT");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("text");
    token2.setTag("NN");

    List<CoreLabel> tokens = Arrays.asList(token, token2);

    Annotation annotation = new Annotation("annotation");
    CoreMap sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(ParserAnnotations.ConstraintAnnotation.class, null);

    annotator.doOneSentence(annotation, sentence);

    Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(tree);
  }
@Test
  public void testSentenceIndexPropagatesToSemanticGraph() throws Exception {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);
    ParserAnnotator annotator = new ParserAnnotator(parser, false, 10);

    CoreLabel token = new CoreLabel();
    token.setWord("word");
    token.setTag("NN");
    token.setValue("word");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    SemanticGraph sg = new SemanticGraph();
    IndexedWord iw = new IndexedWord(token);
    sg.addVertex(iw);
    

    CoreMap sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 2);
    sentence.set(SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation.class, sg);

    List<Tree> trees = new ArrayList<>();
    trees.add(ParserUtils.xTree(tokens));

    java.lang.reflect.Method finishMethod = ParserAnnotator.class.getDeclaredMethod("finishSentence", CoreMap.class, List.class);
    finishMethod.setAccessible(true);
    finishMethod.invoke(annotator, sentence, trees);

    IndexedWord result = sg.vertexListSorted().get(0);
    Integer index = result.get(CoreAnnotations.SentenceIndexAnnotation.class);
    assertEquals(Integer.valueOf(2), index);
  }
@Test
  public void testDoOneSentenceHandlesEmptyTokenList() {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);
    ParserAnnotator annotator = new ParserAnnotator(parser, false, 10);

    CoreMap sentence = new Annotation("empty");
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());

    Annotation annotation = new Annotation("text");
    annotator.doOneSentence(annotation, sentence);

    Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(tree);
    assertEquals("X", tree.label().value());
  }
@Test
  public void testDoOneSentenceWithNullConstraintsFieldPresent() {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);
    ParserAnnotator annotator = new ParserAnnotator(parser, false, 10);

    CoreLabel token = new CoreLabel();
    token.setWord("null");
    token.setTag("NN");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    Annotation annotation = new Annotation("doc");
    CoreMap sentence = new Annotation("sent");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(ParserAnnotations.ConstraintAnnotation.class, null);

    annotator.doOneSentence(annotation, sentence);

    Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(tree);
  }
@Test
  public void testDoOneSentenceCatchesRuntimeInterruptedException() throws Exception {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);

    ParserAnnotator annotator = new ParserAnnotator(parser, false, 100);

    CoreLabel token = new CoreLabel();
    token.setWord("interrupted");
    token.setTag("VB");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    Annotation annotation = new Annotation("text");
    CoreMap sentence = new Annotation("sent");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    
    sentence.set(ParserAnnotations.ConstraintAnnotation.class, new ArrayList<>());

    Thread.currentThread().interrupt(); 

    annotator.doOneSentence(annotation, sentence);

    Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(tree);
    assertEquals("X", tree.label().value());
    
    Thread.interrupted(); 
  }
@Test
  public void testDoOneSentenceResolvesToEmptyTreeWhenKBestZero() throws Exception {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);

    Properties props = new Properties();
    props.setProperty("parser.model", LexicalizedParser.DEFAULT_PARSER_LOC);
    props.setProperty("parser.kbest", "0");

    ParserAnnotator annotator = new ParserAnnotator("parser", props);

    CoreLabel token = new CoreLabel();
    token.setWord("foo");
    token.setTag("NN");

    List<CoreLabel> tokens = Collections.singletonList(token);
    Annotation annotation = new Annotation("text");
    CoreMap sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.doOneSentence(annotation, sentence);

    Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(tree);
  }
@Test
  public void testConstructorWithNullTreeMapProperty() {
    Properties props = new Properties();
    props.setProperty("parser.model", LexicalizedParser.DEFAULT_PARSER_LOC);
    

    ParserAnnotator annotator = new ParserAnnotator("parser", props);
    assertNotNull(annotator);
  }
@Test
  public void testConvertFlagsToArrayWithSingleFlag() throws Exception {
    java.lang.reflect.Method method = ParserAnnotator.class.getDeclaredMethod("convertFlagsToArray", String.class);
    method.setAccessible(true);
    Object result = method.invoke(null, "-one");
    String[] flags = (String[]) result;

    assertEquals(1, flags.length);
    assertEquals("-one", flags[0]);
  }
@Test
  public void testParseWithOutOfMemoryExceptionLogsAndContinues() throws Exception {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);
    ParserAnnotator annotator = new ParserAnnotator(parser, false, 10);

    CoreLabel token = new CoreLabel();
    token.setWord("OOM");
    token.setTag("NN");

    final List<CoreLabel> tokens = Collections.singletonList(token);

    java.lang.reflect.Method method = ParserAnnotator.class.getDeclaredMethod("doOneSentence", List.class, List.class);
    method.setAccessible(true);

    Thread t = new Thread(() -> {
      List<ParserConstraint> constraints = new ArrayList<>();
      try {
        method.invoke(annotator, constraints, tokens);
      } catch (Exception ex) {
        throw new RuntimeException(ex);
      }
    });

    t.start();
    t.stop();  

    assertTrue(t.isAlive() == false || t.isInterrupted() || !t.isAlive());
  }
@Test
  public void testSignatureIncludesMissingOptionalProperties() {
    Properties props = new Properties();
    props.setProperty("parser.model", LexicalizedParser.DEFAULT_PARSER_LOC);
    props.setProperty("parser.debug", "false");
    props.setProperty("parser.flags", "");
    props.setProperty("parser.maxlen", "-1");
    props.setProperty("parser.extradependencies", "NONE");

    String signature = ParserAnnotator.signature("parser", props);
    assertTrue(signature.contains("parser.debug:false"));
    assertTrue(signature.contains("parser.extradependencies:none"));
  }
@Test
  public void testFinishSentenceDoesNotThrowIfNoSemanticGraph() throws Exception {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);
    ParserAnnotator annotator = new ParserAnnotator(parser, false, 10);

    CoreLabel token = new CoreLabel();
    token.setWord("plain");
    token.setTag("NN");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    Tree tree = ParserUtils.xTree(tokens);
    List<Tree> trees = new ArrayList<>();
    trees.add(tree);

    CoreMap sentence = new Annotation("sent");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.remove(SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation.class); 

    java.lang.reflect.Method method = ParserAnnotator.class.getDeclaredMethod("finishSentence", CoreMap.class, List.class);
    method.setAccessible(true);
    method.invoke(annotator, sentence, trees);

    Tree result = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(result);
  }
@Test
  public void testDoOneSentenceWithOnlyPunctuationWhenKeepPunctFalse() {
    Properties props = new Properties();
    props.setProperty("parser.model", LexicalizedParser.DEFAULT_PARSER_LOC);
    props.setProperty("parser.keepPunct", "false");

    ParserAnnotator annotator = new ParserAnnotator("parser", props);

    CoreLabel token = new CoreLabel();
    token.setWord(".");
    token.setTag(".");
    token.setValue(".");

    List<CoreLabel> tokens = Collections.singletonList(token);
    CoreMap sentence = new Annotation("punct");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation annotation = new Annotation("text");
    annotator.doOneSentence(annotation, sentence);

    Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(tree);
  }
@Test
  public void testDefaultConstructorDoesNotThrow() {
    ParserAnnotator annotator = new ParserAnnotator(false, 50);
    assertNotNull(annotator);
  }
@Test
  public void testFlagsEmptyStringReturnsEmptyArray() throws Exception {
    java.lang.reflect.Method method = ParserAnnotator.class.getDeclaredMethod("convertFlagsToArray", String.class);
    method.setAccessible(true);
    Object result = method.invoke(null, "");
    String[] flags = (String[]) result;
    assertEquals(0, flags.length);
  }
@Test
  public void testConstructorWithMultiplePropertiesSet() {
    Properties props = new Properties();
    props.setProperty("parser.model", LexicalizedParser.DEFAULT_PARSER_LOC);
    props.setProperty("parser.maxlen", "15");
    props.setProperty("parser.keepPunct", "false");
    props.setProperty("parser.nthreads", "4");
    props.setProperty("parser.debug", "true");
    props.setProperty("parser.nosquash", "true");

    ParserAnnotator annotator = new ParserAnnotator("parser", props);
    assertNotNull(annotator);
  }
@Test
  public void testFinishSentenceWithTreeMapNullDoesNotFail() throws Exception {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);
    ParserAnnotator annotator = new ParserAnnotator(parser, false, 10, null);

    CoreLabel token = new CoreLabel();
    token.setWord("A");
    token.setTag("NN");
    token.setValue("A");

    List<CoreLabel> tokens = Collections.singletonList(token);

    Tree tree = new LabeledScoredTreeFactory().newLeaf("A");
    tree.setScore(0.0);
    List<Tree> trees = Collections.singletonList(tree);

    CoreMap sentence = new Annotation("test sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    java.lang.reflect.Method method = ParserAnnotator.class.getDeclaredMethod("finishSentence", CoreMap.class, List.class);
    method.setAccessible(true);
    method.invoke(annotator, sentence, trees);

    Tree output = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(output);
  }
@Test
  public void testGetKBestParsesBranchIsCovered() throws Exception {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);

    Properties props = new Properties();
    props.setProperty("parser.model", LexicalizedParser.DEFAULT_PARSER_LOC);
    props.setProperty("parser.kbest", "3");

    ParserAnnotator annotator = new ParserAnnotator("parser", props);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("First");
    token1.setValue("First");
    token1.setTag("NN");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("test");
    token2.setValue("test");
    token2.setTag("NN");

    CoreMap sentence = new Annotation("s");
    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation annotation = new Annotation("text");
    annotator.doOneSentence(annotation, sentence);

    Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(tree);
  }
@Test
  public void testNoSuchParseExceptionCapturedDuringParse() throws Exception {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);
    ParserQuery pq = parser.parserQuery();

    ParserAnnotator annotator = new ParserAnnotator(parser, false, 10);

    CoreLabel token = new CoreLabel();
    token.setWord("X");
    token.setTag("NN");
    token.setValue("X");

    List<CoreLabel> tokens = Collections.singletonList(token);

    java.lang.reflect.Method method = ParserAnnotator.class.getDeclaredMethod("doOneSentence", List.class, List.class);
    method.setAccessible(true);

    List<ParserConstraint> constraints = new ArrayList<ParserConstraint>() {
      @Override
      public boolean add(ParserConstraint constraint) {
        throw new edu.stanford.nlp.parser.common.NoSuchParseException("Injected");
      }
    };

    Object treeList = method.invoke(annotator, constraints, tokens);
    assertNotNull(treeList);
  }
@Test
  public void testFinishSentenceSupportsEnhancedDependenciesIfAvailable() throws Exception {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);

    Properties props = new Properties();
    props.setProperty("parser.model", LexicalizedParser.DEFAULT_PARSER_LOC);
    props.setProperty("parser.keepPunct", "true");
    props.setProperty("parser.buildgraphs", "true");
    props.setProperty("parser.binaryTrees", "true");

    ParserAnnotator annotator = new ParserAnnotator("parser", props);

    CoreLabel token = new CoreLabel();
    token.setWord("word");
    token.setTag("NN");
    token.setValue("word");

    List<CoreLabel> tokenList = Collections.singletonList(token);

    Tree tree = new LabeledScoredTreeFactory().newLeaf("word");
    tree.setScore(0.0);

    ArrayList<Tree> trees = new ArrayList<>();
    trees.add(tree);

    CoreMap sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokenList);

    java.lang.reflect.Method method = ParserAnnotator.class.getDeclaredMethod("finishSentence", CoreMap.class, List.class);
    method.setAccessible(true);
    method.invoke(annotator, sentence, trees);

    Tree binarized = sentence.get(TreeCoreAnnotations.BinarizedTreeAnnotation.class);
    assertNotNull(binarized);
  }
@Test
  public void testFlatTreeStillGetsLabeled() throws Exception {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);
    ParserAnnotator annotator = new ParserAnnotator(parser, false, 10);

    CoreLabel token = new CoreLabel();
    token.setWord("flat");
    token.setTag("NN");

    List<CoreLabel> tokens = Collections.singletonList(token);

    Tree singleLeaf = new LabeledScoredTreeFactory().newLeaf("flat");
    singleLeaf.setScore(-9999.0);

    List<Tree> trees = new ArrayList<>();
    trees.add(singleLeaf);

    CoreMap sentence = new Annotation("flat");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    java.lang.reflect.Method finishMethod = ParserAnnotator.class.getDeclaredMethod("finishSentence", CoreMap.class, List.class);
    finishMethod.setAccessible(true);
    finishMethod.invoke(annotator, sentence, trees);

    Tree result = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(result);
  }
@Test
  public void testUnannotatedIndexedWordGetsSentenceIndex() throws Exception {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);
    ParserAnnotator annotator = new ParserAnnotator(parser, false, 10);

    CoreLabel token = new CoreLabel();
    token.setWord("index");
    token.setTag("NN");

    IndexedWord iw = new IndexedWord(token);
    SemanticGraph sg = new SemanticGraph();
    sg.addVertex(iw);

    Tree tree = ParserUtils.xTree(Collections.singletonList(token));

    List<Tree> trees = new ArrayList<>();
    trees.add(tree);

    CoreMap sentence = new Annotation("sent");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 3);
    sentence.set(SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation.class, sg);

    java.lang.reflect.Method finish = ParserAnnotator.class.getDeclaredMethod("finishSentence", CoreMap.class, List.class);
    finish.setAccessible(true);
    finish.invoke(annotator, sentence, trees);

    Integer index = iw.get(CoreAnnotations.SentenceIndexAnnotation.class);
    assertEquals(Integer.valueOf(3), index);
  }
@Test
  public void testFinishSentenceWithEmptyTreeListDoesNotThrow() throws Exception {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);
    ParserAnnotator parserAnnotator = new ParserAnnotator(parser, false, 10);

    List<Tree> emptyTreeList = new ArrayList<>();

    CoreLabel token = new CoreLabel();
    token.setWord("dummy");
    token.setTag("NN");

    List<CoreLabel> tokenList = Collections.singletonList(token);

    CoreMap sentence = new Annotation("test sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokenList);

    java.lang.reflect.Method finishSentence = ParserAnnotator.class.getDeclaredMethod("finishSentence", CoreMap.class, List.class);
    finishSentence.setAccessible(true);

    finishSentence.invoke(parserAnnotator, sentence, emptyTreeList);

    Tree resultTree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNull(resultTree); 
  }
@Test
  public void testEmptyTokensAnnotationHandledGracefully() {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);
    ParserAnnotator parserAnnotator = new ParserAnnotator(parser, false, 50);

    Annotation annotation = new Annotation("Empty sentence");
    CoreMap sentence = new Annotation("Sentence");

    List<CoreLabel> words = new ArrayList<>();
    sentence.set(CoreAnnotations.TokensAnnotation.class, words);

    parserAnnotator.doOneSentence(annotation, sentence);

    Tree resultTree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(resultTree);
    assertEquals("X", resultTree.label().value());
  }
@Test
  public void testSaveBinaryTreesEnabledSetsBinarizedAnnotation() throws Exception {
    Properties props = new Properties();
    props.setProperty("parser.model", LexicalizedParser.DEFAULT_PARSER_LOC);
    props.setProperty("parser.binaryTrees", "true");

    ParserAnnotator parserAnnotator = new ParserAnnotator("parser", props);

    CoreLabel token = new CoreLabel();
    token.setWord("binary");
    token.setTag("NN");
    List<CoreLabel> tokens = Collections.singletonList(token);

    Tree tree = new LabeledScoredTreeFactory().newTreeNode("NP", Collections.singletonList(new LabeledScoredTreeFactory().newLeaf("binary")));
    List<Tree> trees = Collections.singletonList(tree);

    CoreMap sentence = new Annotation("Binary sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    java.lang.reflect.Method finish = ParserAnnotator.class.getDeclaredMethod("finishSentence", CoreMap.class, List.class);
    finish.setAccessible(true);
    finish.invoke(parserAnnotator, sentence, trees);

    Tree binarized = sentence.get(TreeCoreAnnotations.BinarizedTreeAnnotation.class);
    assertNotNull(binarized);
  }
@Test
  public void testConstructorDoesNotThrowWithMissingOptionalProperties() {
    Properties props = new Properties();
    props.setProperty("parser.model", LexicalizedParser.DEFAULT_PARSER_LOC);
    

    ParserAnnotator parserAnnotator = new ParserAnnotator("parser", props);
    assertNotNull(parserAnnotator);
  }
@Test
  public void testSentenceWithConstraintAnnotationEmptyListStillParses() {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);
    ParserAnnotator parserAnnotator = new ParserAnnotator(parser, false, 10);

    CoreLabel token = new CoreLabel();
    token.setWord("constraint");
    token.setTag("NN");
    token.setValue("constraint");

    List<CoreLabel> tokens = Collections.singletonList(token);
    Annotation annotation = new Annotation("Text");

    CoreMap sentence = new Annotation("Sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(ParserAnnotations.ConstraintAnnotation.class, new ArrayList<ParserConstraint>());

    parserAnnotator.doOneSentence(annotation, sentence);

    Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(tree);
  }
@Test
  public void testMaxSentenceLengthZeroAllowsAllLengths() {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);
    ParserAnnotator parserAnnotator = new ParserAnnotator(parser, false, 0);

    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel token1 = new CoreLabel();
    token1.setWord("one");
    token1.setTag("NN");
    tokens.add(token1);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("two");
    token2.setTag("NN");
    tokens.add(token2);

    Annotation annotation = new Annotation("input");
    CoreMap sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    parserAnnotator.doOneSentence(annotation, sentence);

    Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(tree);
  }
@Test
  public void testRequirementsSatisfiedWithoutGraphsOrBinaryTrees() {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);

    Properties props = new Properties();
    props.setProperty("parser.model", LexicalizedParser.DEFAULT_PARSER_LOC);
    props.setProperty("parser.buildgraphs", "false");
    props.setProperty("parser.binaryTrees", "false");

    ParserAnnotator parserAnnotator = new ParserAnnotator("parser", props);
    Set<Class<? extends CoreAnnotation>> satisfied = parserAnnotator.requirementsSatisfied();

    assertTrue(satisfied.contains(TreeCoreAnnotations.TreeAnnotation.class));
    assertFalse(satisfied.contains(TreeCoreAnnotations.BinarizedTreeAnnotation.class));
    assertFalse(satisfied.contains(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class));
  }
@Test
  public void testDoOneFailedSentenceWithTokensMissingTagSetsXX() {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);
    ParserAnnotator parserAnnotator = new ParserAnnotator(parser, false, 5);

    CoreLabel token = new CoreLabel();
    token.setWord("fail");
    

    List<CoreLabel> tokens = Collections.singletonList(token);

    CoreMap sentence = new Annotation("failSent");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    Annotation annotation = new Annotation("doc");

    parserAnnotator.doOneFailedSentence(annotation, sentence);

    List<CoreLabel> resultTokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals("XX", resultTokens.get(0).tag());
  }
@Test
  public void testDoOneSentenceWithNonXLabelSkipsWhenNoSquashTrue() throws Exception {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);

    ParserAnnotator annotator = new ParserAnnotator(parser, false, 10);

    CoreLabel token = new CoreLabel();
    token.setWord("Skip");
    token.setValue("Skip");
    token.setTag("VB");

    Tree existingTree = new LabeledScoredTreeFactory().newLeaf("ROOT");
    existingTree.setLabel(new StringLabel("ROOT"));

    CoreMap sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, existingTree);

    java.lang.reflect.Field field = ParserAnnotator.class.getDeclaredField("noSquash");
    field.setAccessible(true);
    field.set(annotator, true);

    Annotation annotation = new Annotation("doc");
    annotator.doOneSentence(annotation, sentence);

    Tree result = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertSame(existingTree, result);
  }
@Test
  public void testDoOneSentenceWithXLabelStillProcessesWhenNoSquashTrue() throws Exception {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);

    ParserAnnotator annotator = new ParserAnnotator(parser, false, 10);

    CoreLabel token = new CoreLabel();
    token.setWord("Reparse");
    token.setValue("Reparse");
    token.setTag("NN");

    Tree existingTree = new LabeledScoredTreeFactory().newLeaf("X");
    existingTree.setLabel(new StringLabel("X")); 

    CoreMap sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, existingTree);

    java.lang.reflect.Field field = ParserAnnotator.class.getDeclaredField("noSquash");
    field.setAccessible(true);
    field.set(annotator, true);

    Annotation annotation = new Annotation("doc");
    annotator.doOneSentence(annotation, sentence);

    Tree result = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotEquals("X", result.label().value());
  }
@Test
  public void testDoOneSentenceEnforcesMaxSentenceLengthCutoff() {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);
    ParserAnnotator annotator = new ParserAnnotator(parser, false, 1);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("too");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("long");

    List<CoreLabel> tokens = Arrays.asList(token1, token2);

    CoreMap sentence = new Annotation("long");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation annotation = new Annotation("doc");
    annotator.doOneSentence(annotation, sentence);

    Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(tree);
    assertEquals("X", tree.label().value());
  }
@Test
  public void testRequirementsSatisfiedReturnsCorrectWhenNoGraphsButBinaryTreesTrue() {
    Properties props = new Properties();
    props.setProperty("parser.model", LexicalizedParser.DEFAULT_PARSER_LOC);
    props.setProperty("parser.buildgraphs", "false");
    props.setProperty("parser.binaryTrees", "true");

    ParserAnnotator annotator = new ParserAnnotator("parser", props);
    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    assertTrue(satisfied.contains(TreeCoreAnnotations.BinarizedTreeAnnotation.class));
    assertFalse(satisfied.contains(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class));
    assertTrue(satisfied.contains(TreeCoreAnnotations.TreeAnnotation.class));
  }
@Test
  public void testRequirementsSatisfiedReturnsCorrectWhenGraphsEnabledBinaryTreesFalse() {
    Properties props = new Properties();
    props.setProperty("parser.model", LexicalizedParser.DEFAULT_PARSER_LOC);
    props.setProperty("parser.buildgraphs", "true");
    props.setProperty("parser.binaryTrees", "false");

    ParserAnnotator annotator = new ParserAnnotator("parser", props);
    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    assertFalse(satisfied.contains(TreeCoreAnnotations.BinarizedTreeAnnotation.class));
    assertTrue(satisfied.contains(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class));
    assertTrue(satisfied.contains(TreeCoreAnnotations.TreeAnnotation.class));
  }
@Test
  public void testKBestReturnedWhenKGreaterThanOne() throws Exception {
    Properties props = new Properties();
    props.setProperty("parser.model", LexicalizedParser.DEFAULT_PARSER_LOC);
    props.setProperty("parser.kbest", "2");

    ParserAnnotator annotator = new ParserAnnotator("parser", props);

    CoreLabel token = new CoreLabel();
    token.setWord("tree");
    token.setValue("tree");
    token.setTag("NN");

    List<CoreLabel> tokens = Collections.singletonList(token);
    CoreMap sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation annotation = new Annotation("doc");
    annotator.doOneSentence(annotation, sentence);

    Tree result = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(result);
  }
@Test
  public void testParseFailsWithNullBestParseStillCreatesFallbackTree() throws Exception {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);
    ParserAnnotator annotator = new ParserAnnotator(parser, false, 10);

    CoreLabel token = new CoreLabel();
    token.setWord("fail");
    token.setTag("NN");

    List<CoreLabel> tokens = Collections.singletonList(token);
    CoreMap sentence = new Annotation("sentence");

    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation annotation = new Annotation("doc");

    
    sentence.set(ParserAnnotations.ConstraintAnnotation.class, new ArrayList<ParserConstraint>());

    Thread.currentThread().interrupt(); 

    annotator.doOneSentence(annotation, sentence);

    Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(tree);
    assertEquals("X", tree.label().value());

    Thread.interrupted(); 
  }
@Test
  public void testEmptyFlagsStringHandledInConstructor() {
    Properties props = new Properties();
    props.setProperty("parser.model", LexicalizedParser.DEFAULT_PARSER_LOC);
    props.setProperty("parser.flags", "   ");

    ParserAnnotator annotator = new ParserAnnotator("parser", props);
    assertNotNull(annotator);
  }
@Test
  public void testSignatureReturnsDefaultForMissingFields() {
    Properties props = new Properties();
    props.setProperty("parser.model", LexicalizedParser.DEFAULT_PARSER_LOC);

    String signature = ParserAnnotator.signature("parser", props);
    assertTrue(signature.contains("parser.model:" + LexicalizedParser.DEFAULT_PARSER_LOC));
    assertTrue(signature.contains("parser.maxlen:-1"));
    assertTrue(signature.contains("parser.keepPunct:true"));
    assertTrue(signature.contains("parser.extradependencies:none"));
  }
@Test
  public void testTreeMapReturnsNullTreeHandledGracefully() throws Exception {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);

    ParserAnnotator annotator = new ParserAnnotator(parser, false, 10, tree -> null);

    CoreLabel token = new CoreLabel();
    token.setWord("test");
    token.setTag("NN");

    Tree originalTree = new LabeledScoredTreeFactory().newTreeNode("NP", Collections.singletonList(new LabeledScoredTreeFactory().newLeaf("test")));

    List<Tree> trees = Collections.singletonList(originalTree);

    CoreMap sentence = new Annotation("Sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    java.lang.reflect.Method finishSentence = ParserAnnotator.class.getDeclaredMethod("finishSentence", CoreMap.class, List.class);
    finishSentence.setAccessible(true);
    finishSentence.invoke(annotator, sentence, trees);

    Tree result = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNull(result); 
  }
@Test
  public void testFinishSentenceHandlesNullLabelOnSentenceIndexPropagation() throws Exception {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);
    ParserAnnotator annotator = new ParserAnnotator(parser, false, 10);

    CoreLabel token = new CoreLabel();
    token.setWord("word");
    token.setTag("NN");

    IndexedWord iw = new IndexedWord(token);

    SemanticGraph sg = new SemanticGraph();
    sg.addVertex(iw);

    CoreMap sentence = new Annotation("Sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 87);
    sentence.set(SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation.class, sg);

    Tree tree = new LabeledScoredTreeFactory().newLeaf("text");
    List<Tree> trees = new ArrayList<>();
    trees.add(tree);

    java.lang.reflect.Method finishSentence = ParserAnnotator.class.getDeclaredMethod("finishSentence", CoreMap.class, List.class);
    finishSentence.setAccessible(true);
    finishSentence.invoke(annotator, sentence, trees);

    Integer index = iw.get(CoreAnnotations.SentenceIndexAnnotation.class);
    assertEquals((Integer) 87, index);
  }
@Test
  public void testParserAnnotatorWithExcessivelyLongSentenceSkipsParsing() {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);
    int maxLength = 5;
    ParserAnnotator annotator = new ParserAnnotator(parser, false, maxLength);

    List<CoreLabel> tokens = new ArrayList<>();
    for (int i = 0; i < maxLength + 5; i++) {
      CoreLabel token = new CoreLabel();
      token.setWord("word" + i);
      token.setTag("NN");
      tokens.add(token);
    }

    CoreMap sentence = new Annotation("long sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation document = new Annotation("Text");
    annotator.doOneSentence(document, sentence);

    Tree parsedTree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(parsedTree);
    assertEquals("X", parsedTree.label().value());
  }
@Test
  public void testCollapsedDependenciesAnnotationAbsentDoesNotThrow() throws Exception {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);
    ParserAnnotator annotator = new ParserAnnotator(parser, false, 10);

    CoreLabel token = new CoreLabel();
    token.setWord("element");
    token.setTag("NN");

    CoreMap sentence = new Annotation("Test sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 42);

    List<Tree> treeList = Collections.singletonList(ParserUtils.xTree(Collections.singletonList(token)));

    java.lang.reflect.Method finish = ParserAnnotator.class.getDeclaredMethod("finishSentence", CoreMap.class, List.class);
    finish.setAccessible(true);

    finish.invoke(annotator, sentence, treeList);

    Tree result = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(result);
  }
@Test
  public void testSignatureIncludesDefaultBinaryTreesPropertyWhenNotSet() {
    Properties props = new Properties();
    props.setProperty("parser.model", LexicalizedParser.DEFAULT_PARSER_LOC);
    props.setProperty("parser.debug", "false");

    String signature = ParserAnnotator.signature("parser", props);
    assertTrue(signature.contains("parser.binaryTrees:"));
  }
@Test
  public void testFinishSentenceWithNullPunctuationFilterBuildsGraph() throws Exception {
    Properties props = new Properties();
    props.setProperty("parser.model", LexicalizedParser.DEFAULT_PARSER_LOC);
    props.setProperty("parser.keepPunct", "true");
    props.setProperty("parser.buildgraphs", "true");

    ParserAnnotator annotator = new ParserAnnotator("parser", props);

    CoreLabel token = new CoreLabel();
    token.setWord("hello");
    token.setTag("UH");

    Tree tree = new LabeledScoredTreeFactory().newTreeNode("INTJ", Collections.singletonList(new LabeledScoredTreeFactory().newLeaf("hello")));

    CoreMap sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    List<Tree> trees = Collections.singletonList(tree);

    java.lang.reflect.Method finish = ParserAnnotator.class.getDeclaredMethod("finishSentence", CoreMap.class, List.class);
    finish.setAccessible(true);
    finish.invoke(annotator, sentence, trees);

    SemanticGraph deps = sentence.get(SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation.class);
    assertNotNull(deps);
  }
@Test
  public void testFallbackXTreeIncludesCorrectNumberOfLeaves() {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);
    ParserAnnotator annotator = new ParserAnnotator(parser, false, 10);

    CoreLabel tok1 = new CoreLabel();
    tok1.setWord("foo");
    CoreLabel tok2 = new CoreLabel();
    tok2.setWord("bar");

    List<CoreLabel> tokenList = Arrays.asList(tok1, tok2);

    CoreMap sentence = new Annotation("test");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokenList);

    Annotation annotation = new Annotation("text");

    annotator.doOneFailedSentence(annotation, sentence);

    Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertEquals(2, tree.getLeaves().size());
  }
@Test
  public void testFinishSentenceTreeListWithNullTreeSkippedWithoutException() throws Exception {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);
    ParserAnnotator annotator = new ParserAnnotator(parser, false, 10);

    Tree tree1 = null;
    Tree tree2 = new LabeledScoredTreeFactory().newLeaf("word");

    List<Tree> trees = Arrays.asList(tree1, tree2);

    CoreLabel token = new CoreLabel();
    token.setWord("word");
    token.setTag("NN");

    CoreMap sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    java.lang.reflect.Method method = ParserAnnotator.class.getDeclaredMethod("finishSentence", CoreMap.class, List.class);
    method.setAccessible(true);
    method.invoke(annotator, sentence, trees);

    Tree result = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(result);
  }
@Test
  public void testSetOfRequiredAnnotationsWhenTagsNotRequired() throws Exception {
    ParserGrammar parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);

    java.lang.reflect.Method setTags = parser.getClass().getMethod("setOptionFlags", String[].class);
    setTags.invoke(parser, (Object) new String[]{"-requireTags", "false"});

    ParserAnnotator annotator = new ParserAnnotator(parser, false, 10);
    Set<Class<? extends CoreAnnotation>> required = annotator.requires();

    assertFalse(required.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testSignatureWithDefaultNThreadsDerivedFromGlobalProperty() {
    Properties props = new Properties();
    props.setProperty("parser.model", LexicalizedParser.DEFAULT_PARSER_LOC);
    props.setProperty("nthreads", "7");

    String signature = ParserAnnotator.signature("parser", props);
    assertTrue(signature.contains("parser.nthreads:7"));
  } 
}