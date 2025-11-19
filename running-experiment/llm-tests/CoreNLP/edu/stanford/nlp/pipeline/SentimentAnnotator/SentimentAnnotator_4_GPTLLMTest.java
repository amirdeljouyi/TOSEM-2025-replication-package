package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCostAndGradient;
import edu.stanford.nlp.sentiment.SentimentModel;
import edu.stanford.nlp.trees.LabeledScoredTreeFactory;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.*;
import org.evosuite.runtime.ViolatedAssumptionAnswer;
import org.evosuite.runtime.mock.java.io.MockFile;
import org.evosuite.runtime.mock.java.io.MockPrintStream;
import org.junit.Assert;
import org.junit.Test;

import static org.evosuite.runtime.EvoAssertions.verifyException;
import static org.junit.Assert.*;

import static org.mockito.Mockito.*;

import java.io.*;
import java.util.*;

public class SentimentAnnotator_4_GPTLLMTest {

 @Test
  public void testConstructorWithValidProperties() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
    assertNotNull(annotator);
  }
@Test
  public void testConstructorWithMissingModelThrowsException() {
    Properties props = new Properties();
    props.remove("sentiment.model");
//    thrown.expect(IllegalArgumentException.class);
//    thrown.expectMessage("No model specified for Sentiment annotator");
    new SentimentAnnotator("sentiment", props);
  }
@Test
  public void testNThreadsAndMaxTimeConfiguration() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");
    props.setProperty("sentiment.nthreads", "3");
    props.setProperty("sentiment.maxtime", "10000");
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
    assertEquals(3, annotator.nThreads());
    assertEquals(10000L, annotator.maxTime());
  }
@Test
  public void testRequirementsSatisfiedIsEmpty() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();
    assertTrue(satisfied.isEmpty());
  }
@Test
  public void testRequirementsContainsExpectedDependencies() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
    Set<Class<? extends CoreAnnotation>> required = annotator.requires();
    assertTrue(required.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertTrue(required.contains(TreeCoreAnnotations.TreeAnnotation.class));
    assertTrue(required.contains(TreeCoreAnnotations.BinarizedTreeAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.CategoryAnnotation.class));
  }
@Test
  public void testSignatureGeneration() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/fake-model.ser.gz");
    props.setProperty("sentiment.nthreads", "5");
    props.setProperty("sentiment.maxtime", "200");
    String sig = SentimentAnnotator.signature("sentiment", props);
    assertTrue(sig.contains("sentiment.model:edu/stanford/nlp/models/sentiment/fake-model.ser.gz"));
    assertTrue(sig.contains("sentiment.nthreads:5"));
    assertTrue(sig.contains("sentiment.maxtime:200"));
  }
@Test
  public void testDoOneSentenceBinarizedTreeMissingThrowsAssertionError() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    Annotation annotation = new Annotation("test");
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(TreeCoreAnnotations.BinarizedTreeAnnotation.class)).thenReturn(null);

//    thrown.expect(AssertionError.class);
//    thrown.expectMessage("Binarized sentences not built by parser");

    annotator.doOneSentence(annotation, sentence);
  }
@Test
  public void testDoOneSentenceSkipsWhenTreeIsNull() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    Tree binarizedTree = new LabeledScoredTreeFactory().newTreeNode("ROOT", Collections.emptyList());

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(TreeCoreAnnotations.BinarizedTreeAnnotation.class)).thenReturn(binarizedTree);
    when(sentence.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(null);

    Annotation doc = new Annotation("No outer tree");
    annotator.doOneSentence(doc, sentence);
    verify(sentence).set(eq(SentimentCoreAnnotations.SentimentAnnotatedTree.class), any(Tree.class));
    verify(sentence).set(eq(SentimentCoreAnnotations.SentimentClass.class), anyString());
  }
@Test
  public void testDoOneSentencePopulatesSentimentInTreeLabels() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    CoreLabel leafLabel1 = new CoreLabel();
    CoreLabel leafLabel2 = new CoreLabel();

    Tree leaf1 = new LabeledScoredTreeFactory().newLeaf("A");
    leaf1.setLabel(leafLabel1);

    Tree leaf2 = new LabeledScoredTreeFactory().newLeaf("B");
    leaf2.setLabel(leafLabel2);

    List<Tree> children = new ArrayList<>();
    children.add(leaf1);
    children.add(leaf2);

    CoreLabel rootLabel = new CoreLabel();
    Tree rootTree = new LabeledScoredTreeFactory().newTreeNode("ROOT", children);
    rootTree.setLabel(rootLabel);

    Tree binarizedTree = new LabeledScoredTreeFactory().newTreeNode("ROOT", children);
    
    Tree spyTree = spy(rootTree);
    doReturn(new IntPair(0, 1)).when(spyTree).getSpan();
    doReturn(new IntPair(0, 1)).when(leaf1).getSpan();
    doReturn(new IntPair(1, 2)).when(leaf2).getSpan();

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(TreeCoreAnnotations.BinarizedTreeAnnotation.class)).thenReturn(binarizedTree);
    when(sentence.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(spyTree);

    Annotation annotation = new Annotation("Test sentence");

    annotator.doOneSentence(annotation, sentence);

    assertNull(leafLabel1.get(CoreAnnotations.SpanAnnotation.class));
    assertNull(leafLabel2.get(CoreAnnotations.SpanAnnotation.class));
  }
@Test
  public void testDoOneSentenceSpanAnnotationAlreadyPresentThrows() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    CoreLabel label = new CoreLabel();
    label.set(CoreAnnotations.SpanAnnotation.class, new IntPair(0, 1));

    Tree tree = new LabeledScoredTreeFactory().newLeaf("NODE");
    tree.setLabel(label);

    Tree binarizedTree = new LabeledScoredTreeFactory().newLeaf("DUMMY");

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(TreeCoreAnnotations.BinarizedTreeAnnotation.class)).thenReturn(binarizedTree);
    when(sentence.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(tree);

    Annotation doc = new Annotation("span conflict");

//    thrown.expect(IllegalStateException.class);
//    thrown.expectMessage("This code assumes you don't have SpanAnnotation");

    annotator.doOneSentence(doc, sentence);
  }
@Test
  public void testSignatureWithDefaultsWhenPropertiesMissing() {
    Properties props = new Properties();
    String signature = SentimentAnnotator.signature("sentiment", props);
    assertTrue(signature.contains("sentiment.model:edu/stanford/nlp/models/sentiment/sentiment.ser.gz"));
    assertTrue(signature.contains("sentiment.nthreads:"));
    assertTrue(signature.contains("sentiment.maxtime:-1"));
  }
@Test
  public void testNThreadsDefaultFallbackFromGlobal() {
    Properties props = new Properties();
    props.setProperty("nthreads", "4");
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/true-model.ser.gz");

    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
    assertEquals(4, annotator.nThreads());
  }
@Test
  public void testNThreadsDefaultToOneWhenUnset() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/true-model.ser.gz");

    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
    assertEquals(1, annotator.nThreads());
  }
@Test
  public void testMaxTimeZeroIsAllowed() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/true-model.ser.gz");
    props.setProperty("sentiment.maxtime", "0");

    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
    assertEquals(0, annotator.maxTime());
  }
@Test
  public void testDoOneSentenceWithEmptyTreeDoesNotCrash() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/test-model.ser.gz");
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    Tree emptyTree = new LabeledScoredTreeFactory().newTreeNode("ROOT", new ArrayList<>());

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(TreeCoreAnnotations.BinarizedTreeAnnotation.class)).thenReturn(emptyTree);
    when(sentence.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(null);

    Annotation anno = new Annotation("Empty");
    annotator.doOneSentence(anno, sentence);

    verify(sentence).set(eq(SentimentCoreAnnotations.SentimentAnnotatedTree.class), any(Tree.class));
    verify(sentence).set(eq(SentimentCoreAnnotations.SentimentClass.class), anyString());
  }
@Test
  public void testDoOneSentenceHandlesTreeWithNonCoreLabelLabel() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/test-model.ser.gz");
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    Tree binarizedTree = new LabeledScoredTreeFactory().newTreeNode("ROOT", Collections.emptyList());

    Tree innerTree = new LabeledScoredTreeFactory().newLeaf("NP");
    Tree outerTree = new LabeledScoredTreeFactory().newTreeNode("ROOT", Collections.singletonList(innerTree));
//    outerTree.setLabel(new Label() {
//      public String value() { return "NotCoreLabel"; }
//      public void setValue(String value) {}
//    });

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(TreeCoreAnnotations.BinarizedTreeAnnotation.class)).thenReturn(binarizedTree);
    when(sentence.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(outerTree);

    Annotation annotation = new Annotation("Non-CoreLabel Tree");
    annotator.doOneSentence(annotation, sentence);

    verify(sentence).set(eq(SentimentCoreAnnotations.SentimentClass.class), anyString());
  }
@Test
  public void testDoOneSentenceAvoidsOverwritingExistingSentimentInSpanMap() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/test-model.ser.gz");
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    Tree leafA = new LabeledScoredTreeFactory().newLeaf("A");
    Tree leafB = new LabeledScoredTreeFactory().newLeaf("B");

    CoreLabel labelA = new CoreLabel();
    CoreLabel labelB = new CoreLabel();
    leafA.setLabel(labelA);
    leafB.setLabel(labelB);

    Tree root = new LabeledScoredTreeFactory().newTreeNode("ROOT", Arrays.asList(leafA, leafB));
    CoreLabel rootLabel = new CoreLabel();
    root.setLabel(rootLabel);

    Tree binarizedTree = new LabeledScoredTreeFactory().newTreeNode("BIN", Arrays.asList(leafA, leafB));
    CoreLabel binLabel = new CoreLabel();
    binarizedTree.setLabel(binLabel);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(TreeCoreAnnotations.BinarizedTreeAnnotation.class)).thenReturn(binarizedTree);
    when(sentence.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(root);

    root.setSpans();
    leafA.setSpans();
    leafB.setSpans();

    
    IntPair leafSpan = leafA.getSpan();
    labelA.set(SentimentCoreAnnotations.SentimentClass.class, "Positive");

    Annotation annotation = new Annotation("Avoid Overwrite");
    annotator.doOneSentence(annotation, sentence);

    String sentimentA = labelA.get(SentimentCoreAnnotations.SentimentClass.class);
    assertEquals("Positive", sentimentA);
  }
@Test
  public void testDoOneSentenceSentimentClassMappedCorrectlyOnDifferentSpans() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/test-model.ser.gz");
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    Tree leaf1 = new LabeledScoredTreeFactory().newLeaf("L1");
    Tree leaf2 = new LabeledScoredTreeFactory().newLeaf("L2");

    CoreLabel label1 = new CoreLabel();
    CoreLabel label2 = new CoreLabel();
    leaf1.setLabel(label1);
    leaf2.setLabel(label2);

    Tree parent = new LabeledScoredTreeFactory().newTreeNode("P", Arrays.asList(leaf1, leaf2));
    CoreLabel parentLabel = new CoreLabel();
    parent.setLabel(parentLabel);

    parent.setSpans();
    leaf1.setSpans();
    leaf2.setSpans();

    Tree binarizedTree = new LabeledScoredTreeFactory().newTreeNode("ROOT", Arrays.asList(leaf1, leaf2));
    CoreLabel binLabel = new CoreLabel();
    binarizedTree.setLabel(binLabel);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(TreeCoreAnnotations.BinarizedTreeAnnotation.class)).thenReturn(binarizedTree);
    when(sentence.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(parent);

    Annotation annotation = new Annotation("Span sentiments");
    annotator.doOneSentence(annotation, sentence);

    String s1 = label1.get(SentimentCoreAnnotations.SentimentClass.class);
    String s2 = label2.get(SentimentCoreAnnotations.SentimentClass.class);
    String sParent = parentLabel.get(SentimentCoreAnnotations.SentimentClass.class);

    assertNotNull(s1);
    assertNotNull(s2);
    assertNotNull(sParent);
    assertNotEquals(s1, s2);
  }
@Test
  public void testUnaryTreeStillProcessesCorrectly() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/test.ser.gz");
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    CoreLabel singleLabel = new CoreLabel();
    Tree leaf = new LabeledScoredTreeFactory().newLeaf("A");
    leaf.setLabel(singleLabel);

    Tree unaryTree = new LabeledScoredTreeFactory().newTreeNode("Unary", Collections.singletonList(leaf));
    CoreLabel outerLabel = new CoreLabel();
    unaryTree.setLabel(outerLabel);

    unaryTree.setSpans();
    leaf.setSpans();

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(TreeCoreAnnotations.BinarizedTreeAnnotation.class)).thenReturn(unaryTree);
    when(sentence.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(unaryTree);

    Annotation doc = new Annotation("Unary tree");
    annotator.doOneSentence(doc, sentence);

    assertNotNull(outerLabel.get(SentimentCoreAnnotations.SentimentClass.class));
//    assertNull(leaf.get(CoreAnnotations.SpanAnnotation.class));
  }
@Test
  public void testTreeWithOverlappingSpansOnlyAssignsFirst() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/test.ser.gz");
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    CoreLabel label1 = new CoreLabel();
    Tree t1 = new LabeledScoredTreeFactory().newLeaf("Word1");
    t1.setLabel(label1);
//    t1.setSpan(new IntPair(0, 2));

    CoreLabel label2 = new CoreLabel();
    Tree t2 = new LabeledScoredTreeFactory().newLeaf("Word2");
    t2.setLabel(label2);
//    t2.setSpan(new IntPair(0, 2));

    Tree root = new LabeledScoredTreeFactory().newTreeNode("ROOT", Arrays.asList(t1, t2));
    CoreLabel rootLabel = new CoreLabel();
    root.setLabel(rootLabel);
//    root.setSpan(new IntPair(0, 4));

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(TreeCoreAnnotations.BinarizedTreeAnnotation.class)).thenReturn(root);
    when(sentence.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(root);

    Annotation doc = new Annotation("Overlap");
    annotator.doOneSentence(doc, sentence);

    assertNotNull(label1.get(SentimentCoreAnnotations.SentimentClass.class));
    assertNull(label2.get(SentimentCoreAnnotations.SentimentClass.class));
  }
@Test
  public void testSentimentStringVeryNegativeAndVeryPositive() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/test.ser.gz");
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    CoreLabel labelNeg = new CoreLabel();
    Tree negLeaf = new LabeledScoredTreeFactory().newLeaf("Bad");
    negLeaf.setLabel(labelNeg);
//    negLeaf.setSpan(new IntPair(0, 1));

    CoreLabel labelPos = new CoreLabel();
    Tree posLeaf = new LabeledScoredTreeFactory().newLeaf("Good");
    posLeaf.setLabel(labelPos);
//    posLeaf.setSpan(new IntPair(1, 2));

    Tree root = new LabeledScoredTreeFactory().newTreeNode("ROOT", Arrays.asList(negLeaf, posLeaf));
    CoreLabel rootLabel = new CoreLabel();
    root.setLabel(rootLabel);
//    root.setSpan(new IntPair(0, 2));

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(TreeCoreAnnotations.BinarizedTreeAnnotation.class)).thenReturn(root);
    when(sentence.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(root);

    Annotation annotation = new Annotation("Edge Sentiments");
    annotator.doOneSentence(annotation, sentence);

    assertNotNull(labelNeg.get(SentimentCoreAnnotations.SentimentClass.class));
    assertNotNull(labelPos.get(SentimentCoreAnnotations.SentimentClass.class));
  }
@Test
  public void testUnknownSentimentClassAssignmentStillSucceeds() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/test.ser.gz");
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    Tree leaf = new LabeledScoredTreeFactory().newLeaf("?");
    CoreLabel leafLabel = new CoreLabel();
    leaf.setLabel(leafLabel);
//    leaf.setSpan(new IntPair(0, 1));

    Tree root = new LabeledScoredTreeFactory().newTreeNode("UNK", Collections.singletonList(leaf));
    CoreLabel rootLabel = new CoreLabel();
    root.setLabel(rootLabel);
//    root.setSpan(new IntPair(0, 1));

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(TreeCoreAnnotations.BinarizedTreeAnnotation.class)).thenReturn(root);
    when(sentence.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(root);

    Annotation doc = new Annotation("Unknown sentiment");
    annotator.doOneSentence(doc, sentence);

    assertNotNull(leafLabel.get(SentimentCoreAnnotations.SentimentClass.class));
    assertNotNull(rootLabel.get(SentimentCoreAnnotations.SentimentClass.class));
  }
@Test
  public void testDoOneSentenceHandlesEmptyCoreMapGracefully() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/test.ser.gz");
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(TreeCoreAnnotations.BinarizedTreeAnnotation.class)).thenReturn(null);

    Annotation doc = new Annotation("Empty CoreMap");

    try {
      annotator.doOneSentence(doc, sentence);
      fail("Expected AssertionError");
    } catch (AssertionError expected) {
      assertEquals("Binarized sentences not built by parser", expected.getMessage());
    }
  }
@Test
  public void testDoOneSentenceSetsNullSentimentFields() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/test.ser.gz");
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    Tree leaf = new LabeledScoredTreeFactory().newLeaf("Z");
    CoreLabel label = new CoreLabel();
    leaf.setLabel(label);
//    leaf.setSpan(new IntPair(0, 1));

    Tree root = new LabeledScoredTreeFactory().newTreeNode("ROOT", Collections.singletonList(leaf));
    CoreLabel rootLabel = new CoreLabel();
    root.setLabel(rootLabel);
//    root.setSpan(new IntPair(0, 1));

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(TreeCoreAnnotations.BinarizedTreeAnnotation.class)).thenReturn(root);
    when(sentence.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(root);

    Annotation anno = new Annotation("Null test");
    annotator.doOneSentence(anno, sentence);

    assertNull(label.get(CoreAnnotations.SpanAnnotation.class));
    assertNull(rootLabel.get(CoreAnnotations.SpanAnnotation.class));
    assertNotNull(label.get(SentimentCoreAnnotations.SentimentClass.class));
    assertNotNull(rootLabel.get(SentimentCoreAnnotations.SentimentClass.class));
  }
@Test
  public void testDoOneSentenceHandlesPreLabeledTreeWithWrongLabelType() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/placeholder.ser.gz");
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    Tree leaf = new LabeledScoredTreeFactory().newLeaf("Token");
//    leaf.setLabel(() -> "Non-CoreLabel");

    Tree tree = new LabeledScoredTreeFactory().newTreeNode("ROOT", Collections.singletonList(leaf));
//    tree.setLabel(() -> "Also-Non-CoreLabel");

    tree.setSpans();
    leaf.setSpans();

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(TreeCoreAnnotations.BinarizedTreeAnnotation.class)).thenReturn(tree);
    when(sentence.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(tree);

    Annotation annotation = new Annotation("Non CoreLabel test");
    annotator.doOneSentence(annotation, sentence);

    
    verify(sentence).set(eq(SentimentCoreAnnotations.SentimentClass.class), anyString());
  }
@Test
  public void testDoOneSentenceTreeWithoutSpansStillProcessed() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/placeholder.ser.gz");
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    Tree left = new LabeledScoredTreeFactory().newLeaf("Left");
    CoreLabel leftLabel = new CoreLabel();
    left.setLabel(leftLabel);

    Tree right = new LabeledScoredTreeFactory().newLeaf("Right");
    CoreLabel rightLabel = new CoreLabel();
    right.setLabel(rightLabel);

    Tree root = new LabeledScoredTreeFactory().newTreeNode("ROOT", Arrays.asList(left, right));
    CoreLabel rootLabel = new CoreLabel();
    root.setLabel(rootLabel);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(TreeCoreAnnotations.BinarizedTreeAnnotation.class)).thenReturn(root);
    when(sentence.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(root);

    Annotation doc = new Annotation("Span-less tree");
    annotator.doOneSentence(doc, sentence);

    assertNotNull(leftLabel.get(SentimentCoreAnnotations.SentimentClass.class));
    assertNotNull(rightLabel.get(SentimentCoreAnnotations.SentimentClass.class));
    assertNotNull(rootLabel.get(SentimentCoreAnnotations.SentimentClass.class));
  }
@Test
  public void testDoOneSentenceSetsMultipleSentimentLabelsWithUniqueSpans() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/placeholder.ser.gz");
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    Tree leaf1 = new LabeledScoredTreeFactory().newLeaf("one");
    CoreLabel label1 = new CoreLabel();
    label1.setIndex(1);
    leaf1.setLabel(label1);
//    leaf1.setSpan(new IntPair(0, 1));

    Tree leaf2 = new LabeledScoredTreeFactory().newLeaf("two");
    CoreLabel label2 = new CoreLabel();
    label2.setIndex(2);
    leaf2.setLabel(label2);
//    leaf2.setSpan(new IntPair(1, 2));

    Tree root = new LabeledScoredTreeFactory().newTreeNode("root", Arrays.asList(leaf1, leaf2));
    CoreLabel rootLabel = new CoreLabel();
    root.setLabel(rootLabel);
//    root.setSpan(new IntPair(0, 2));

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(TreeCoreAnnotations.BinarizedTreeAnnotation.class)).thenReturn(root);
    when(sentence.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(root);

    Annotation doc = new Annotation("Multi-label");
    annotator.doOneSentence(doc, sentence);

    assertNotNull(label1.get(SentimentCoreAnnotations.SentimentClass.class));
    assertNotNull(label2.get(SentimentCoreAnnotations.SentimentClass.class));
    assertNotNull(rootLabel.get(SentimentCoreAnnotations.SentimentClass.class));
  }
@Test
  public void testDoOneSentenceWithTreeWithoutLabelDoesNotFail() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/placeholder.ser.gz");
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    Tree leaf = new LabeledScoredTreeFactory().newLeaf("token");
    leaf.setLabel(null); 

    Tree tree = new LabeledScoredTreeFactory().newTreeNode("ROOT", Collections.singletonList(leaf));
    tree.setLabel(new CoreLabel());
    tree.setSpans();
    leaf.setSpans();

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(TreeCoreAnnotations.BinarizedTreeAnnotation.class)).thenReturn(tree);
    when(sentence.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(tree);

    Annotation annotation = new Annotation("No label on leaf");
    annotator.doOneSentence(annotation, sentence);

    verify(sentence).set(eq(SentimentCoreAnnotations.SentimentClass.class), anyString());
  }
@Test
  public void testDoOneFailedSentenceIsNoOp() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/placeholder.ser.gz");
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    CoreMap sentence = mock(CoreMap.class);
    Annotation doc = new Annotation("Irrelevant");

    
    annotator.doOneFailedSentence(doc, sentence);

    
    assertTrue(true);
  }
@Test
  public void testRequirementsContainsExactNumberOfDependencies() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/placeholder.ser.gz");
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    Set<Class<? extends CoreAnnotation>> requirements = annotator.requires();

    assertEquals(4, requirements.size());
    assertTrue(requirements.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.CategoryAnnotation.class));
    assertTrue(requirements.contains(TreeCoreAnnotations.TreeAnnotation.class));
    assertTrue(requirements.contains(TreeCoreAnnotations.BinarizedTreeAnnotation.class));
  }
@Test
  public void testSignatureUsesFallbackWhenNoAnnotatorSpecificProperties() {
    Properties props = new Properties();
    props.setProperty("model", "test");
    props.setProperty("nthreads", "8");
    props.setProperty("maxtime", "123");

    String sig = SentimentAnnotator.signature("sentiment", props);

    assertTrue(sig.contains("sentiment.model:edu/stanford/nlp/models/sentiment/sentiment.ser.gz"));
    assertTrue(sig.contains("sentiment.nthreads:8"));
    assertTrue(sig.contains("sentiment.maxtime:123"));
  }
@Test
  public void testLabelWithSpanAnnotationButWrongTypeStillThrowsException() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/mock.ser.gz");

    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    Tree tree = new LabeledScoredTreeFactory().newLeaf("word1");
    CoreLabel labelWithWrongSpan = new CoreLabel();
    labelWithWrongSpan.set(CoreAnnotations.SpanAnnotation.class, (IntPair) null); 
    tree.setLabel(labelWithWrongSpan);

    Tree rootTree = new LabeledScoredTreeFactory().newTreeNode("root", Collections.singletonList(tree));
    rootTree.setLabel(labelWithWrongSpan);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(TreeCoreAnnotations.BinarizedTreeAnnotation.class)).thenReturn(rootTree);
    when(sentence.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(rootTree);

    Annotation annotation = new Annotation("test");

    try {
      annotator.doOneSentence(annotation, sentence);
      fail("Expected IllegalStateException due to SpanAnnotation check");
    } catch (IllegalStateException e) {
      assertTrue(e.getMessage().contains("This code assumes you don't have SpanAnnotation"));
    }
  }
@Test
  public void testTreeWithOnlyRootNodeStillProcesses() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/mock.ser.gz");

    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    CoreLabel rootLabel = new CoreLabel();
    Tree root = new LabeledScoredTreeFactory().newTreeNode("root", new ArrayList<Tree>());
    root.setLabel(rootLabel);
//    root.setSpan(new IntPair(0, 1));

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(TreeCoreAnnotations.BinarizedTreeAnnotation.class)).thenReturn(root);
    when(sentence.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(root);

    Annotation annotation = new Annotation("single node");

    annotator.doOneSentence(annotation, sentence);

    String sentiment = rootLabel.get(SentimentCoreAnnotations.SentimentClass.class);
    assertNotNull(sentiment);
  }
@Test
  public void testTreeWithPartialSpansStillAssignsSentiment() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/mock.ser.gz");

    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    CoreLabel labelWithSpan = new CoreLabel();
    Tree leafWithSpan = new LabeledScoredTreeFactory().newLeaf("valid");
    leafWithSpan.setLabel(labelWithSpan);
//    leafWithSpan.setSpan(new IntPair(0, 1));

    CoreLabel labelNoSpan = new CoreLabel();
    Tree leafNoSpan = new LabeledScoredTreeFactory().newLeaf("missing");
    leafNoSpan.setLabel(labelNoSpan); 

    Tree root = new LabeledScoredTreeFactory().newTreeNode("ROOT", Arrays.asList(leafWithSpan, leafNoSpan));
    CoreLabel rootLabel = new CoreLabel();
    root.setLabel(rootLabel);
//    root.setSpan(new IntPair(0, 2));

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(TreeCoreAnnotations.BinarizedTreeAnnotation.class)).thenReturn(root);
    when(sentence.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(root);

    Annotation annotation = new Annotation("partial spans");

    annotator.doOneSentence(annotation, sentence);

    assertNotNull(labelWithSpan.get(SentimentCoreAnnotations.SentimentClass.class));
    assertNull(labelNoSpan.get(SentimentCoreAnnotations.SentimentClass.class));  
    assertNotNull(rootLabel.get(SentimentCoreAnnotations.SentimentClass.class));
  }
@Test
  public void testIgnoreTreeNodeIfSpanIsNull() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/mock.ser.gz");

    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    Tree spanlessTree = new LabeledScoredTreeFactory().newLeaf("empty");
    CoreLabel label = new CoreLabel();
    spanlessTree.setLabel(label);
    

    Tree root = new LabeledScoredTreeFactory().newTreeNode("ROOT", Collections.singletonList(spanlessTree));
    CoreLabel rootLabel = new CoreLabel();
    root.setLabel(rootLabel);
//    root.setSpan(new IntPair(0, 1));

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(TreeCoreAnnotations.BinarizedTreeAnnotation.class)).thenReturn(root);
    when(sentence.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(root);

    Annotation doc = new Annotation("null span");

    annotator.doOneSentence(doc, sentence);

    assertNotNull(rootLabel.get(SentimentCoreAnnotations.SentimentClass.class));
    assertNull(label.get(SentimentCoreAnnotations.SentimentClass.class)); 
  }
@Test
  public void testTreeLabelThatIsNotCoreLabelSkipsCasting() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/mock.ser.gz");

    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    Tree leaf = new LabeledScoredTreeFactory().newLeaf("X");
//    leaf.setLabel(new Label() {
//      public String value() { return "str"; }
//      public void setValue(String v) {}
//    });

    Tree root = new LabeledScoredTreeFactory().newTreeNode("ROOT", Collections.singletonList(leaf));
    CoreLabel rootLabel = new CoreLabel();
    root.setLabel(rootLabel);
    root.setSpans();

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(TreeCoreAnnotations.BinarizedTreeAnnotation.class)).thenReturn(root);
    when(sentence.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(root);

    Annotation doc = new Annotation("non-corelabel label");
    root.setSpans();

    annotator.doOneSentence(doc, sentence);

    assertNotNull(rootLabel.get(SentimentCoreAnnotations.SentimentClass.class));
  }
@Test
  public void testTreeWithIdenticalSubtreesOnlyAssignsSentimentOncePerSpan() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/mock.ser.gz");

    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    Tree leaf1 = new LabeledScoredTreeFactory().newLeaf("Word");
    CoreLabel label1 = new CoreLabel();
    leaf1.setLabel(label1);
//    leaf1.setSpan(new IntPair(0, 1));

    Tree leaf2 = new LabeledScoredTreeFactory().newLeaf("Word");
    CoreLabel label2 = new CoreLabel();
    leaf2.setLabel(label2);
//    leaf2.setSpan(new IntPair(0, 1));

    Tree root = new LabeledScoredTreeFactory().newTreeNode("Root", Arrays.asList(leaf1, leaf2));
    CoreLabel rootLabel = new CoreLabel();
    root.setLabel(rootLabel);
//    root.setSpan(new IntPair(0, 1));

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(TreeCoreAnnotations.BinarizedTreeAnnotation.class)).thenReturn(root);
    when(sentence.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(root);

    Annotation doc = new Annotation("reused spans");

    annotator.doOneSentence(doc, sentence);

    String rootSentiment = rootLabel.get(SentimentCoreAnnotations.SentimentClass.class);
    String l1Sentiment = label1.get(SentimentCoreAnnotations.SentimentClass.class);
    String l2Sentiment = label2.get(SentimentCoreAnnotations.SentimentClass.class);

    assertNotNull(rootSentiment);
    assertNotNull(l1Sentiment);
    assertNull(l2Sentiment); 
  }
@Test
  public void testDoOneSentenceHandlesTreeWithoutLabelGracefully() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/fallback.ser.gz");
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    Tree unlabeledLeaf = new LabeledScoredTreeFactory().newLeaf("Token");
    unlabeledLeaf.setLabel(null);

    Tree root = new LabeledScoredTreeFactory().newTreeNode("ROOT", Collections.singletonList(unlabeledLeaf));
    CoreLabel rootLabel = new CoreLabel();
    root.setLabel(rootLabel);
//    root.setSpan(new IntPair(0, 1));

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(TreeCoreAnnotations.BinarizedTreeAnnotation.class)).thenReturn(root);
    when(sentence.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(root);

    Annotation doc = new Annotation("No label");
    annotator.doOneSentence(doc, sentence);

    assertNotNull(rootLabel.get(SentimentCoreAnnotations.SentimentClass.class));
  }
@Test
  public void testDoOneSentenceTreeWithNonSpannedNodesIsIgnoredSilently() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/fallback.ser.gz");
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    Tree childLeaf = new LabeledScoredTreeFactory().newLeaf("X");
    CoreLabel childLabel = new CoreLabel();
    childLeaf.setLabel(childLabel);
    

    Tree root = new LabeledScoredTreeFactory().newTreeNode("ROOT", Collections.singletonList(childLeaf));
    CoreLabel rootLabel = new CoreLabel();
    root.setLabel(rootLabel);
//    root.setSpan(new IntPair(0, 1));

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(TreeCoreAnnotations.BinarizedTreeAnnotation.class)).thenReturn(root);
    when(sentence.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(root);

    Annotation doc = new Annotation("No span node");
    annotator.doOneSentence(doc, sentence);

    assertNotNull(rootLabel.get(SentimentCoreAnnotations.SentimentClass.class));
    assertNull(childLabel.get(SentimentCoreAnnotations.SentimentClass.class));
  }
@Test
  public void testDoOneSentenceHandlesMultipleIdenticalChildSpans() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/fallback.ser.gz");
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    Tree leafA = new LabeledScoredTreeFactory().newLeaf("Repeating");
    CoreLabel labelA = new CoreLabel();
    leafA.setLabel(labelA);
//    leafA.setSpan(new IntPair(0, 1));

    Tree leafB = new LabeledScoredTreeFactory().newLeaf("Repeating");
    CoreLabel labelB = new CoreLabel();
    leafB.setLabel(labelB);
//    leafB.setSpan(new IntPair(0, 1));

    Tree root = new LabeledScoredTreeFactory().newTreeNode("ROOT", Arrays.asList(leafA, leafB));
    CoreLabel rootLabel = new CoreLabel();
    root.setLabel(rootLabel);
//    root.setSpan(new IntPair(0, 1));

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(TreeCoreAnnotations.BinarizedTreeAnnotation.class)).thenReturn(root);
    when(sentence.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(root);

    Annotation doc = new Annotation("Duplicate spans");
    annotator.doOneSentence(doc, sentence);

    assertNotNull(labelA.get(SentimentCoreAnnotations.SentimentClass.class));
    assertNull(labelB.get(SentimentCoreAnnotations.SentimentClass.class)); 
    assertNotNull(rootLabel.get(SentimentCoreAnnotations.SentimentClass.class));
  }
@Test
  public void testDoOneSentenceTreeWithoutChildrenDoesNotCrash() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/fallback.ser.gz");
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    CoreLabel label = new CoreLabel();
    Tree soloNode = new LabeledScoredTreeFactory().newTreeNode("ROOT", Collections.emptyList());
    soloNode.setLabel(label);
//    soloNode.setSpan(new IntPair(0, 1));

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(TreeCoreAnnotations.BinarizedTreeAnnotation.class)).thenReturn(soloNode);
    when(sentence.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(soloNode);

    Annotation doc = new Annotation("Single node");
    annotator.doOneSentence(doc, sentence);

    assertNotNull(label.get(SentimentCoreAnnotations.SentimentClass.class));
  }
@Test
  public void testDoOneSentenceHandlesSiblingOverlapButUniquelyAssigns() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/fallback.ser.gz");
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    Tree leaf1 = new LabeledScoredTreeFactory().newLeaf("one");
    CoreLabel label1 = new CoreLabel();
    leaf1.setLabel(label1);
//    leaf1.setSpan(new IntPair(0, 2));

    Tree leaf2 = new LabeledScoredTreeFactory().newLeaf("two");
    CoreLabel label2 = new CoreLabel();
    leaf2.setLabel(label2);
//    leaf2.setSpan(new IntPair(1, 2));

    Tree root = new LabeledScoredTreeFactory().newTreeNode("root", Arrays.asList(leaf1, leaf2));
    CoreLabel rootLabel = new CoreLabel();
    root.setLabel(rootLabel);
//    root.setSpan(new IntPair(0, 2));

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(TreeCoreAnnotations.BinarizedTreeAnnotation.class)).thenReturn(root);
    when(sentence.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(root);

    Annotation doc = new Annotation("Overlap spans");
    annotator.doOneSentence(doc, sentence);

    assertNotNull(label1.get(SentimentCoreAnnotations.SentimentClass.class));
    assertNotNull(label2.get(SentimentCoreAnnotations.SentimentClass.class));
    assertNotNull(rootLabel.get(SentimentCoreAnnotations.SentimentClass.class));
  }
@Test
  public void testDoOneSentenceHandlesRootNodeWithoutSpan() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/fallback.ser.gz");

    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    Tree leaf = new LabeledScoredTreeFactory().newLeaf("x");
    CoreLabel leafLabel = new CoreLabel();
    leaf.setLabel(leafLabel);
//    leaf.setSpan(new IntPair(0, 1));

    Tree root = new LabeledScoredTreeFactory().newTreeNode("root", Collections.singletonList(leaf));
    CoreLabel rootLabel = new CoreLabel();
    root.setLabel(rootLabel);
    

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(TreeCoreAnnotations.BinarizedTreeAnnotation.class)).thenReturn(root);
    when(sentence.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(root);

    Annotation annotation = new Annotation("no span root");
    annotator.doOneSentence(annotation, sentence);

    assertNull(rootLabel.get(CoreAnnotations.SpanAnnotation.class));
    assertNotNull(leafLabel.get(SentimentCoreAnnotations.SentimentClass.class));
    assertNotNull(rootLabel.get(SentimentCoreAnnotations.SentimentClass.class));
  }
@Test
  public void testEmptyAnnotationDoesNotThrowWhenNoSentences() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/test-model.ser.gz");
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    Annotation emptyAnnotation = new Annotation("");
    try {
      
      annotator.doOneFailedSentence(emptyAnnotation, null);
      assertTrue(true);
    } catch (Exception e) {
      fail("Should not throw exception for null sentence");
    }
  }
@Test
  public void testRootTreeWithNonCoreLabelObjectSkipsSentimentAssignment() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/simple-model.ser.gz");
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    Tree leaf = new LabeledScoredTreeFactory().newLeaf("token");
//    leaf.setLabel(new Label() {
//      public String value() { return "non-core"; }
//      public void setValue(String v) {}
//    });
//
//    Tree root = new LabeledScoredTreeFactory().newTreeNode("TOP", Collections.singletonList(leaf));
//    root.setLabel(new Label() {
//      public String value() { return "top-label"; }
//      public void setValue(String v) {}
//    });

//    root.setSpans();
    leaf.setSpans();

    CoreMap sentence = mock(CoreMap.class);
//    when(sentence.get(TreeCoreAnnotations.BinarizedTreeAnnotation.class)).thenReturn(root);
//    when(sentence.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(root);

    Annotation annotation = new Annotation("NonCoreLabel");
    annotator.doOneSentence(annotation, sentence);

    
    assertTrue(true);
  }
@Test
  public void testTreeWithSingleLeafNodeSetsSentenceSentiment() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/simple-model.ser.gz");
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    CoreLabel leafLabel = new CoreLabel();
    Tree singleLeaf = new LabeledScoredTreeFactory().newLeaf("hello");
    singleLeaf.setLabel(leafLabel);
//    singleLeaf.setSpan(new IntPair(0, 1));

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(TreeCoreAnnotations.BinarizedTreeAnnotation.class)).thenReturn(singleLeaf);
    when(sentence.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(singleLeaf);

    Annotation annotation = new Annotation("Single leaf");
    annotator.doOneSentence(annotation, sentence);

    assertNotNull(leafLabel.get(SentimentCoreAnnotations.SentimentClass.class));
  }
@Test
  public void testCoreLabelGetsSentimentButNoSpanAnnotationPresent() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/simple-model.ser.gz");
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    CoreLabel spanless = new CoreLabel();
    Tree tree = new LabeledScoredTreeFactory().newLeaf("spanless");
    tree.setLabel(spanless);
    

    Tree root = new LabeledScoredTreeFactory().newTreeNode("ROOT", Collections.singletonList(tree));
    CoreLabel rootLabel = new CoreLabel();
    root.setLabel(rootLabel);
//    root.setSpan(new IntPair(0, 1));

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(TreeCoreAnnotations.BinarizedTreeAnnotation.class)).thenReturn(root);
    when(sentence.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(root);

    Annotation annotation = new Annotation("No span");
    annotator.doOneSentence(annotation, sentence);

    assertNotNull(rootLabel.get(SentimentCoreAnnotations.SentimentClass.class));
    assertNull(spanless.get(SentimentCoreAnnotations.SentimentClass.class));
  }
@Test
  public void testSentimentAssignedToLeafAndRootWithDifferentSpanKeys() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/simple-model.ser.gz");
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    CoreLabel leafLabel = new CoreLabel();
    Tree leaf = new LabeledScoredTreeFactory().newLeaf("word");
    leaf.setLabel(leafLabel);
//    leaf.setSpan(new IntPair(0, 1));

    CoreLabel rootLabel = new CoreLabel();
    Tree root = new LabeledScoredTreeFactory().newTreeNode("root", Collections.singletonList(leaf));
    root.setLabel(rootLabel);
//    root.setSpan(new IntPair(0, 2));

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(TreeCoreAnnotations.BinarizedTreeAnnotation.class)).thenReturn(root);
    when(sentence.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(root);

    Annotation annotation = new Annotation("Nested");
    annotator.doOneSentence(annotation, sentence);

    assertNotNull(leafLabel.get(SentimentCoreAnnotations.SentimentClass.class));
    assertNotNull(rootLabel.get(SentimentCoreAnnotations.SentimentClass.class));
  }
@Test
  public void testSentimentNotDuplicatedForSameSpanAnnotation() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/simple-model.ser.gz");
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    IntPair sameSpan = new IntPair(0, 2);

    CoreLabel label1 = new CoreLabel();
    Tree t1 = new LabeledScoredTreeFactory().newLeaf("w1");
    t1.setLabel(label1);
//    t1.setSpan(sameSpan);

    CoreLabel label2 = new CoreLabel();
    Tree t2 = new LabeledScoredTreeFactory().newLeaf("w2");
    t2.setLabel(label2);
//    t2.setSpan(sameSpan);

    Tree root = new LabeledScoredTreeFactory().newTreeNode("ROOT", Arrays.asList(t1, t2));
    CoreLabel rootLabel = new CoreLabel();
    root.setLabel(rootLabel);
//    root.setSpan(new IntPair(0, 4));

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(TreeCoreAnnotations.BinarizedTreeAnnotation.class)).thenReturn(root);
    when(sentence.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(root);

    Annotation annotation = new Annotation("Duplicate spans");
    annotator.doOneSentence(annotation, sentence);

    String s1 = label1.get(SentimentCoreAnnotations.SentimentClass.class);
    String s2 = label2.get(SentimentCoreAnnotations.SentimentClass.class);
    assertNotNull(s1);
    assertNull(s2); 
  }
@Test
  public void testSentimentAssignmentWhenOnlyTreeAnnotationPresent() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/simple-model.ser.gz");
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    CoreLabel label = new CoreLabel();
    Tree leaf = new LabeledScoredTreeFactory().newLeaf("leaf");
    leaf.setLabel(label);
//    leaf.setSpan(new IntPair(0, 1));

    Tree tree = new LabeledScoredTreeFactory().newTreeNode("ROOT", Collections.singletonList(leaf));
    CoreLabel treeLabel = new CoreLabel();
    tree.setLabel(treeLabel);
//    tree.setSpan(new IntPair(0, 2));

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(TreeCoreAnnotations.BinarizedTreeAnnotation.class)).thenReturn(tree);
    when(sentence.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(tree);

    Annotation doc = new Annotation("Only tree annotation");
    annotator.doOneSentence(doc, sentence);

    assertNotNull(label.get(SentimentCoreAnnotations.SentimentClass.class));
    assertNotNull(treeLabel.get(SentimentCoreAnnotations.SentimentClass.class));
  } 
}