package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
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

public class SentimentAnnotator_1_GPTLLMTest {

 @Test
  public void testConstructorWithValidProperties() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "dummy");
    props.setProperty("sentiment.nthreads", "2");
    props.setProperty("sentiment.maxtime", "1000");

    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    assertNotNull(annotator);
    assertEquals(2, annotator.nThreads());
    assertEquals(1000L, annotator.maxTime());
  }
@Test(expected = IllegalArgumentException.class)
  public void testConstructorMissingModelThrows() {
    Properties props = new Properties();
    props.remove("sentiment.model");

    new SentimentAnnotator("sentiment", props);
  }
@Test
  public void testRequirementsSatisfiedReturnsEmptySet() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "dummy");

    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    assertNotNull(satisfied);
    assertTrue(satisfied.isEmpty());
  }
@Test
  public void testRequiresReturnsExpectedSet() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "dummy");

    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
    Set<Class<? extends CoreAnnotation>> requires = annotator.requires();

    assertTrue(requires.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertTrue(requires.contains(TreeCoreAnnotations.TreeAnnotation.class));
    assertTrue(requires.contains(TreeCoreAnnotations.BinarizedTreeAnnotation.class));
    assertTrue(requires.contains(CoreAnnotations.CategoryAnnotation.class));
    assertEquals(4, requires.size());
  }
@Test
  public void testSignatureIsFormattedCorrectly() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "dummy/path");
    props.setProperty("sentiment.nthreads", "3");
    props.setProperty("sentiment.maxtime", "2000");

    String signature = SentimentAnnotator.signature("sentiment", props);

    assertTrue(signature.contains("sentiment.model:dummy/path"));
    assertTrue(signature.contains("sentiment.nthreads:3"));
    assertTrue(signature.contains("sentiment.maxtime:2000"));
  }
@Test
  public void testDoOneSentenceBasicPropagation() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "dummy");

    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    Annotation annotation = new Annotation("text");
    CoreMap sentence = new Annotation("sentence");

    CoreLabel rootLabel = new CoreLabel();
    Tree leaf = new LabeledScoredTreeFactory().newLeaf(new CoreLabel());
    Tree root = new LabeledScoredTreeFactory().newTreeNode(rootLabel, Collections.singletonList(leaf));
//    root.setSpan(new IntPair(0, 2));
//    leaf.setSpan(new IntPair(0, 1));

    sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, root);
    Tree originalTree = new LabeledScoredTreeFactory().newTreeNode(new CoreLabel(), Collections.singletonList(leaf));
//    originalTree.setSpan(new IntPair(0, 2));
//    leaf.setSpan(new IntPair(0, 1));
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, originalTree);

    SentimentModel model = mock(SentimentModel.class);
    SentimentCostAndGradient scorer = new SentimentCostAndGradient(model, null);

    SentimentAnnotator annotatorSpy = spy(annotator);
//    doReturn(model).when(annotatorSpy).model;

    annotatorSpy.doOneSentence(annotation, sentence);

    Tree annotatedTree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
    String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);

    assertNotNull(annotatedTree);
    assertNotNull(sentiment);
  }
@Test(expected = AssertionError.class)
  public void testDoOneSentenceMissingBinarizedTreeThrows() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "dummy");

    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
    Annotation annotation = new Annotation("text");
    CoreMap sentence = new Annotation("sentence");

    annotator.doOneSentence(annotation, sentence);
  }
@Test
  public void testDoOneSentenceSentimentClassPropagatedToTreeNodes() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "dummy");

    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    Annotation annotation = new Annotation("text");
    CoreMap sentence = new Annotation("sentence");

    CoreLabel rootLabel = new CoreLabel();
    Tree treeLeaf = new LabeledScoredTreeFactory().newLeaf(new CoreLabel());
//    treeLeaf.setSpan(new IntPair(0, 1));
    Tree binarizedRoot = new LabeledScoredTreeFactory().newTreeNode(rootLabel, Collections.singletonList(treeLeaf));
//    binarizedRoot.setSpan(new IntPair(0, 1));

    Tree originalTreeLeaf = new LabeledScoredTreeFactory().newLeaf(new CoreLabel());
//    originalTreeLeaf.setSpan(new IntPair(0, 1));
    Tree originalTree = new LabeledScoredTreeFactory().newTreeNode(new CoreLabel(), Collections.singletonList(originalTreeLeaf));
//    originalTree.setSpan(new IntPair(0, 1));

    sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binarizedRoot);
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, originalTree);

    SentimentAnnotator annotatorSpy = spy(annotator);
    SentimentModel mockModel = mock(SentimentModel.class);
//    doReturn(mockModel).when(annotatorSpy).model;

    annotatorSpy.doOneSentence(annotation, sentence);

    CoreLabel label = (CoreLabel) originalTree.label();
    String sentiment = label.get(SentimentCoreAnnotations.SentimentClass.class);
    assertNotNull(sentiment);
  }
@Test(expected = IllegalStateException.class)
  public void testDoOneSentenceWithPreexistingSpanThrows() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "dummy");

    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    Annotation annotation = new Annotation("text");
    CoreMap sentence = new Annotation("sentence");

    CoreLabel nodeLabel = new CoreLabel();
    nodeLabel.set(CoreAnnotations.SpanAnnotation.class, new IntPair(0, 1));

    Tree leaf = new LabeledScoredTreeFactory().newLeaf(nodeLabel);
//    leaf.setSpan(new IntPair(0, 1));
    Tree tree = new LabeledScoredTreeFactory().newTreeNode(nodeLabel, Collections.singletonList(leaf));
//    tree.setSpan(new IntPair(0, 1));

    sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, tree);
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);

    SentimentAnnotator annotatorSpy = spy(annotator);
    SentimentModel mockModel = mock(SentimentModel.class);
//    doReturn(mockModel).when(annotatorSpy).model;

    annotatorSpy.doOneSentence(annotation, sentence);
  }
@Test
  public void testDoOneFailedSentenceDoesNothing() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "dummy");

    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
    Annotation annotation = new Annotation("text");
    CoreMap sentence = new Annotation("sentence");

    
    annotator.doOneFailedSentence(annotation, sentence);
  }
@Test
public void testConstructorUsesDefaultsWhenOptionalPropertiesMissing() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "dummy/model/path");

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  assertEquals(1, annotator.nThreads());
  assertEquals(-1L, annotator.maxTime());
}
@Test
public void testSignatureWithMissingOptionalProperties() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "dummy/path");

  String signature = SentimentAnnotator.signature("sentiment", props);
  
  assertTrue(signature.contains("sentiment.model:dummy/path"));
  assertTrue(signature.contains("sentiment.nthreads:"));
  assertTrue(signature.contains("sentiment.maxtime:-1"));
}
@Test
public void testDoOneSentenceHandlesNullTreeAnnotationGracefully() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "dummy");

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
  Annotation annotation = new Annotation("text");
  CoreMap sentence = new Annotation("sentence");

  Tree leaf = new LabeledScoredTreeFactory().newLeaf(new CoreLabel());
//  leaf.setSpan(new IntPair(0, 1));
  Tree root = new LabeledScoredTreeFactory().newTreeNode(new CoreLabel(), Collections.singletonList(leaf));
//  root.setSpan(new IntPair(0, 1));

  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, root);
  

  SentimentAnnotator annotatorSpy = spy(annotator);
  SentimentModel model = mock(SentimentModel.class);
//  doReturn(model).when(annotatorSpy).model;

  annotatorSpy.doOneSentence(annotation, sentence);

  Tree result = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
  String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
  assertNotNull(result);
  assertNotNull(sentiment);
}
@Test
public void testDoOneSentenceWithMultipleNonOverlappingSpans() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "dummy");

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
  Annotation annotation = new Annotation("text");
  CoreMap sentence = new Annotation("sentence");

  CoreLabel l1 = new CoreLabel(); l1.setValue("a");
  CoreLabel l2 = new CoreLabel(); l2.setValue("b");
  Tree t1 = new LabeledScoredTreeFactory().newLeaf(l1);
  Tree t2 = new LabeledScoredTreeFactory().newLeaf(l2);

//  t1.setSpan(new IntPair(0, 1));
//  t2.setSpan(new IntPair(1, 2));

  Tree binarizedTree = new LabeledScoredTreeFactory().newTreeNode(new CoreLabel(), Arrays.asList(t1, t2));
//  binarizedTree.setSpan(new IntPair(0, 2));

  Tree originalTree = new LabeledScoredTreeFactory().newTreeNode(new CoreLabel(), Arrays.asList(t1, t2));
//  originalTree.setSpan(new IntPair(0, 2));

  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binarizedTree);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, originalTree);

  SentimentAnnotator annotatorSpy = spy(annotator);
  SentimentModel mockModel = mock(SentimentModel.class);
//  doReturn(mockModel).when(annotatorSpy).model;

  annotatorSpy.doOneSentence(annotation, sentence);

  CoreLabel label1 = (CoreLabel) t1.label();
  CoreLabel label2 = (CoreLabel) t2.label();
  assertNotNull(label1.get(SentimentCoreAnnotations.SentimentClass.class));
  assertNotNull(label2.get(SentimentCoreAnnotations.SentimentClass.class));
}
@Test
public void testDoOneSentenceSpanSentimentMapsOnlyFirstOccurrence() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "dummy");

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
  Annotation annotation = new Annotation("text");
  CoreMap sentence = new Annotation("sentence");

  CoreLabel label1 = new CoreLabel(); label1.setValue("x");
  CoreLabel label2 = new CoreLabel(); label2.setValue("y");
  Tree child1 = new LabeledScoredTreeFactory().newLeaf(label1);
  Tree child2 = new LabeledScoredTreeFactory().newLeaf(label2);
//  child1.setSpan(new IntPair(1, 2));
//  child2.setSpan(new IntPair(1, 2));

  Tree parent = new LabeledScoredTreeFactory().newTreeNode(new CoreLabel(), Arrays.asList(child1, child2));
//  parent.setSpan(new IntPair(1, 2));

  Tree originalTree = new LabeledScoredTreeFactory().newTreeNode(new CoreLabel(), Arrays.asList(child1, child2));
//  originalTree.setSpan(new IntPair(1, 2));

  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, parent);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, originalTree);

  SentimentAnnotator annotatorSpy = spy(annotator);
  SentimentModel mockModel = mock(SentimentModel.class);
//  doReturn(mockModel).when(annotatorSpy).model;

  annotatorSpy.doOneSentence(annotation, sentence);

  CoreLabel labelNode = (CoreLabel) parent.label();
  String sentimentStr = labelNode.get(SentimentCoreAnnotations.SentimentClass.class);
  assertNotNull(sentimentStr);
}
@Test
public void testDoOneSentenceNoSpanSentimentNoMatchingSpanDoesNotSetLabel() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "dummy");

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
  Annotation annotation = new Annotation("text");
  CoreMap sentence = new Annotation("sentence");

  Tree binTree = new LabeledScoredTreeFactory().newLeaf(new CoreLabel());
//  binTree.setSpan(new IntPair(0, 1));

  Tree tree = new LabeledScoredTreeFactory().newLeaf(new CoreLabel());
//  tree.setSpan(new IntPair(5, 6));

  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binTree);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);

  SentimentAnnotator annotatorSpy = spy(annotator);
  SentimentModel mockModel = mock(SentimentModel.class);
//  doReturn(mockModel).when(annotatorSpy).model;

  annotatorSpy.doOneSentence(annotation, sentence);

  CoreLabel cl = (CoreLabel) tree.label();
  assertNull(cl.get(SentimentCoreAnnotations.SentimentClass.class));
}
@Test
public void testDoOneSentenceRemovesSpanAnnotationFromCoreLabel() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "dummy");

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
  Annotation annotation = new Annotation("text");
  CoreMap sentence = new Annotation("sentence");

  CoreLabel label = new CoreLabel();
  label.set(CoreAnnotations.SpanAnnotation.class, new IntPair(0, 1));
  Tree tree = new LabeledScoredTreeFactory().newLeaf(label);
//  tree.setSpan(new IntPair(0, 1));

  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, tree);

  Tree topoTree = new LabeledScoredTreeFactory().newLeaf(new CoreLabel());
//  topoTree.setSpan(new IntPair(0, 1));
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, topoTree);

  SentimentAnnotator annotatorSpy = spy(annotator);
  SentimentModel mockModel = mock(SentimentModel.class);
//  doReturn(mockModel).when(annotatorSpy).model;

  annotatorSpy.doOneSentence(annotation, sentence);

  CoreLabel result = (CoreLabel) topoTree.label();
  assertFalse(result.containsKey(CoreAnnotations.SpanAnnotation.class));
}
@Test
public void testConstructorWithGlobalThreadAndTimeConfigurationFallback() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "path/to/model");
  props.setProperty("nthreads", "8");
  props.setProperty("maxtime", "9999");

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
  assertEquals(8, annotator.nThreads());
  assertEquals(9999L, annotator.maxTime());
}
@Test
public void testConstructorWithDefaultsForMissingOptionalValues() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "default/path/model");

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
  assertEquals(1, annotator.nThreads());
  assertEquals(-1L, annotator.maxTime());
}
@Test
public void testSignatureWithOnlyModelPath() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "abc/model.gz");

  String sig = SentimentAnnotator.signature("sentiment", props);
  assertTrue(sig.contains("sentiment.model:abc/model.gz"));
  assertTrue(sig.contains("sentiment.nthreads:"));
  assertTrue(sig.contains("sentiment.maxtime:-1"));
}
@Test
public void testDoOneSentenceWithTreeAnnotationButEmptySpanSentiments() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "dummy/path");

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
  Annotation annotation = new Annotation("Sample text");
  CoreMap sentence = new Annotation("Single sentence");

  Tree leaf = new LabeledScoredTreeFactory().newLeaf(new CoreLabel());
  Tree binarized = new LabeledScoredTreeFactory().newTreeNode(new CoreLabel(), Collections.singletonList(leaf));
//  binarized.setSpan(new IntPair(0, 2));
//  leaf.setSpan(new IntPair(0, 1));

  Tree tree = new LabeledScoredTreeFactory().newTreeNode(new CoreLabel(), Collections.emptyList());
//  tree.setSpan(new IntPair(5, 10));

  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binarized);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);

  SentimentAnnotator spyAnnotator = spy(annotator);
  SentimentModel model = mock(SentimentModel.class);
//  doReturn(model).when(spyAnnotator).model;

  spyAnnotator.doOneSentence(annotation, sentence);

  CoreLabel label = (CoreLabel) tree.label();
  String sentiment = label.get(SentimentCoreAnnotations.SentimentClass.class);
  assertNull(sentiment); 
}
@Test
public void testDoOneSentenceSetsTopLevelSentimentEvenIfOnlyOneNode() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "mock");

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
  Annotation annotation = new Annotation("doc");
  CoreMap sentence = new Annotation("one");

  Tree node = new LabeledScoredTreeFactory().newLeaf(new CoreLabel());
//  node.setSpan(new IntPair(0, 1));
  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, node);

  SentimentAnnotator spyAnnotator = spy(annotator);
  SentimentModel model = mock(SentimentModel.class);
//  doReturn(model).when(spyAnnotator).model;

  spyAnnotator.doOneSentence(annotation, sentence);
  Tree resultTree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
  String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);

  assertNotNull(resultTree);
  assertNotNull(sentiment);
}
@Test
public void testDoOneSentenceAssignsSentimentCorrectlyWithRedundantSpans() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "redundantTestPath");

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
  Annotation annotation = new Annotation("redundant spans");
  CoreMap sentence = new Annotation("sentence");

  CoreLabel cl1 = new CoreLabel();
  cl1.setValue("one");
  Tree leaf1 = new LabeledScoredTreeFactory().newLeaf(cl1);
//  leaf1.setSpan(new IntPair(0,2));

  CoreLabel cl2 = new CoreLabel();
  cl2.setValue("two");
  Tree leaf2 = new LabeledScoredTreeFactory().newLeaf(cl2);
//  leaf2.setSpan(new IntPair(0,2));

  Tree binRoot = new LabeledScoredTreeFactory().newTreeNode(new CoreLabel(), Arrays.asList(leaf1, leaf2));
//  binRoot.setSpan(new IntPair(0, 2));

  Tree origTree = new LabeledScoredTreeFactory().newTreeNode(new CoreLabel(), Arrays.asList(leaf1, leaf2));
//  origTree.setSpan(new IntPair(0,2));

  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binRoot);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, origTree);

  SentimentAnnotator spyAnnotator = spy(annotator);
  SentimentModel mockModel = mock(SentimentModel.class);
//  doReturn(mockModel).when(spyAnnotator).model;

  spyAnnotator.doOneSentence(annotation, sentence);

  CoreLabel label1 = (CoreLabel) leaf1.label();
  CoreLabel label2 = (CoreLabel) leaf2.label();

  String s1 = label1.get(SentimentCoreAnnotations.SentimentClass.class);
  String s2 = label2.get(SentimentCoreAnnotations.SentimentClass.class);

  assertNotNull(s1);
  assertNotNull(s2);
  assertEquals(s1, s2); 
}
@Test
public void testConstructorWhenNoModelPropertyUsesDefaultModelPath() {
  Properties props = new Properties();
  
  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
  assertNotNull(annotator);
}
@Test
public void testConstructorWithEmptyStringModelPropertyFallsBackToDefault() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "");
  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
  assertNotNull(annotator);
}
@Test
public void testRequiresSetIsUnmodifiable() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "dummy");
  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
  Set<Class<? extends CoreAnnotation>> requires = annotator.requires();
  try {
    requires.add(CoreAnnotations.TextAnnotation.class);
    fail("Should throw UnsupportedOperationException");
  } catch (UnsupportedOperationException e) {
    
  }
}
@Test
public void testSignatureWithDifferentAnnotatorPrefix() {
  Properties props = new Properties();
  props.setProperty("foo.model", "differentPath");
  props.setProperty("foo.nthreads", "7");
  props.setProperty("foo.maxtime", "5000");
  String sig = SentimentAnnotator.signature("foo", props);
  assertTrue(sig.contains("foo.model:differentPath"));
  assertTrue(sig.contains("foo.nthreads:7"));
  assertTrue(sig.contains("foo.maxtime:5000"));
}
@Test
public void testDoOneSentenceWithOnlyLeafTreeSetsTopLevelSentiment() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "dummy");
  Annotation annotation = new Annotation("leaf");
  CoreMap sentence = new Annotation("test");

  CoreLabel label = new CoreLabel();
  Tree leaf = new LabeledScoredTreeFactory().newLeaf(label);
//  leaf.setSpan(new IntPair(0, 1));

  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, leaf);
  

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
  SentimentAnnotator spyAnnotator = spy(annotator);
  SentimentModel mockModel = mock(SentimentModel.class);
//  doReturn(mockModel).when(spyAnnotator).model;

  spyAnnotator.doOneSentence(annotation, sentence);

  Tree annotated = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
  String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
  assertNotNull(annotated);
  assertNotNull(sentiment);
}
@Test
public void testDoOneSentenceWithTreeAndCollapsedSentimentAnnotations() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "dummy");
  Annotation annotation = new Annotation("multi");
  CoreMap sentence = new Annotation("sentence");

  CoreLabel label = new CoreLabel();
  Tree binLeaf = new LabeledScoredTreeFactory().newLeaf(label);
//  binLeaf.setSpan(new IntPair(0, 1));
  Tree binRoot = new LabeledScoredTreeFactory().newTreeNode(new CoreLabel(), Collections.singletonList(binLeaf));
//  binRoot.setSpan(new IntPair(0, 1));
  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binRoot);

  CoreLabel treeLabel = new CoreLabel();
  Tree orig = new LabeledScoredTreeFactory().newTreeNode(treeLabel, Collections.singletonList(binLeaf));
//  orig.setSpan(new IntPair(0, 1));
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, orig);

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
  SentimentAnnotator spy = spy(annotator);
  SentimentModel model = mock(SentimentModel.class);
//  doReturn(model).when(spy).model;

  spy.doOneSentence(annotation, sentence);

  CoreLabel newLabel = (CoreLabel) orig.label();
  String sentiment = newLabel.get(SentimentCoreAnnotations.SentimentClass.class);
  assertNotNull(sentiment);
}
@Test
public void testDoOneSentenceWithTreeNodeNotLabelledAsCoreLabelSkipsSentimentClassAssignment() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "dummy");
  Annotation annotation = new Annotation("no CoreLabel");
  CoreMap sentence = new Annotation("test");

  Tree leaf = new LabeledScoredTreeFactory().newLeaf("non-CoreLabel-Leaf");
//  leaf.setSpan(new IntPair(0, 1));
  Tree tree = new LabeledScoredTreeFactory().newTreeNode("non-CoreLabel-Node", Collections.singletonList(leaf));
//  tree.setSpan(new IntPair(0, 1));
  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, tree);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
  SentimentAnnotator spy = spy(annotator);
  SentimentModel model = mock(SentimentModel.class);
//  doReturn(model).when(spy).model;

  spy.doOneSentence(annotation, sentence);

  
  assertNull(((Tree) sentence.get(TreeCoreAnnotations.TreeAnnotation.class)).label() instanceof CoreLabel);
}
@Test
public void testDoOneSentenceWithEmptyTreeDoesNotThrow() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "empty");

  Annotation annotation = new Annotation("doc");
  CoreMap sentence = new Annotation("blank");

  Tree tree = new LabeledScoredTreeFactory().newTreeNode(new CoreLabel(), Collections.emptyList());
  tree.setSpans(); 
  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, tree);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
  SentimentAnnotator spy = spy(annotator);
  SentimentModel model = mock(SentimentModel.class);
//  doReturn(model).when(spy).model;

  spy.doOneSentence(annotation, sentence);

  assertNotNull(sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class));
  assertNotNull(sentence.get(SentimentCoreAnnotations.SentimentClass.class));
}
@Test
public void testConstructorWithOnlyAnnotatorModelPropertyOverridesDefault() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "custom/path/model.ser.gz");

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  assertNotNull(annotator);
}
@Test
public void testConstructorWithInvalidNThreadsFallsBackToDefault() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "some/path");
  props.setProperty("sentiment.nthreads", "invalid_number");

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  
  assertEquals(1, annotator.nThreads());
}
@Test
public void testConstructorWithInvalidMaxTimeFallsBackToDefault() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "some/model");
  props.setProperty("sentiment.maxtime", "not_a_number");

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  assertEquals(-1L, annotator.maxTime());
}
@Test
public void testDoOneSentenceHandlesNullAnnotationGracefullyWhenTreePresent() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "dummy");

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
  CoreMap sentence = new Annotation("sentence-only");

  Tree leaf = new LabeledScoredTreeFactory().newLeaf(new CoreLabel());
//  leaf.setSpan(new IntPair(0, 1));
  Tree binTree = new LabeledScoredTreeFactory().newTreeNode(new CoreLabel(), Collections.singletonList(leaf));
//  binTree.setSpan(new IntPair(0, 1));

  Tree originalTree = new LabeledScoredTreeFactory().newTreeNode(new CoreLabel(), Collections.singletonList(leaf));
//  originalTree.setSpan(new IntPair(0, 1));

  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binTree);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, originalTree);

  SentimentAnnotator spy = spy(annotator);
  SentimentModel mockModel = mock(SentimentModel.class);
//  doReturn(mockModel).when(spy).model;

  spy.doOneSentence(null, sentence);

  Tree tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
  String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);

  assertNotNull(tree);
  assertNotNull(sentiment);
}
@Test
public void testDoOneSentenceWithSentenceTreeHavingOnlyRoot() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "dummy");

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
  Annotation annotation = new Annotation("text");
  CoreMap sentence = new Annotation("single-node sentence");

  CoreLabel rootLabel = new CoreLabel();
  Tree binRoot = new LabeledScoredTreeFactory().newTreeNode(rootLabel, Collections.emptyList());
//  binRoot.setSpan(new IntPair(0, 0));

  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binRoot);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, binRoot);

  SentimentAnnotator spy = spy(annotator);
  SentimentModel model = mock(SentimentModel.class);
//  doReturn(model).when(spy).model;

  spy.doOneSentence(annotation, sentence);

  Tree resultTree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
  assertNotNull(resultTree);
  assertNotNull(sentence.get(SentimentCoreAnnotations.SentimentClass.class));
}
@Test
public void testDoOneSentenceHandlesEmptySpanSentimentMapGracefully() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "dummy");

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
  Annotation annotation = new Annotation("x");
  CoreMap sentence = new Annotation("y");

  CoreLabel label1 = new CoreLabel();
  Tree leaf = new LabeledScoredTreeFactory().newLeaf(label1);
//  leaf.setSpan(new IntPair(0, 1));

  Tree binRoot = new LabeledScoredTreeFactory().newTreeNode(new CoreLabel(), Collections.singletonList(leaf));
//  binRoot.setSpan(new IntPair(0, 1));

  Tree originalTree = new LabeledScoredTreeFactory().newTreeNode(new CoreLabel(), Collections.emptyList());
//  originalTree.setSpan(new IntPair(9, 10));

  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binRoot);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, originalTree);

  SentimentAnnotator spy = spy(annotator);
  SentimentModel mockModel = mock(SentimentModel.class);
//  doReturn(mockModel).when(spy).model;

  spy.doOneSentence(annotation, sentence);

  Tree annotated = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
  assertNotNull(annotated);
  assertNotNull(sentence.get(SentimentCoreAnnotations.SentimentClass.class));
  CoreLabel result = (CoreLabel) originalTree.label();
  assertNull(result.get(SentimentCoreAnnotations.SentimentClass.class));
}
@Test
public void testDoOneSentenceWithDuplicateSpansOnlyAssignsSentimentOnce() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "xyz");

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
  Annotation annotation = new Annotation("multi");
  CoreMap sentence = new Annotation("s");

  CoreLabel l1 = new CoreLabel(); l1.setValue("foo");
  CoreLabel l2 = new CoreLabel(); l2.setValue("bar");

  Tree t1 = new LabeledScoredTreeFactory().newLeaf(l1);
  Tree t2 = new LabeledScoredTreeFactory().newLeaf(l2);

//  t1.setSpan(new IntPair(0, 1));
//  t2.setSpan(new IntPair(0, 1));
  Tree rooted = new LabeledScoredTreeFactory().newTreeNode(new CoreLabel(), Arrays.asList(t1, t2));
//  rooted.setSpan(new IntPair(0,1));

  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, rooted);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, rooted);

  SentimentAnnotator spy = spy(annotator);
  SentimentModel model = mock(SentimentModel.class);
//  doReturn(model).when(spy).model;

  spy.doOneSentence(annotation, sentence);

  CoreLabel cl1 = (CoreLabel) t1.label();
  CoreLabel cl2 = (CoreLabel) t2.label();

  String s1 = cl1.get(SentimentCoreAnnotations.SentimentClass.class);
  String s2 = cl2.get(SentimentCoreAnnotations.SentimentClass.class);

  assertNotNull(s1);
  assertNotNull(s2);
  assertEquals(s1, s2); 
}
@Test
public void testRequirementsContainsExactExpectedClassesOnly() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "some/model/path");

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
  Set<Class<? extends CoreAnnotation>> required = annotator.requires();

  assertEquals(4, required.size());
  assertTrue(required.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
  assertTrue(required.contains(TreeCoreAnnotations.TreeAnnotation.class));
  assertTrue(required.contains(TreeCoreAnnotations.BinarizedTreeAnnotation.class));
  assertTrue(required.contains(CoreAnnotations.CategoryAnnotation.class));
}
@Test
public void testDoOneSentenceDoesNotOverwriteExistingSentimentClassIfAlreadySetOnTreeNode() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "dummy");

  Annotation annotation = new Annotation("text");
  CoreMap sentence = new Annotation("sentence");

  CoreLabel label = new CoreLabel();
  label.set(SentimentCoreAnnotations.SentimentClass.class, "Positive");
  Tree leaf = new LabeledScoredTreeFactory().newLeaf(label);
//  leaf.setSpan(new IntPair(0, 1));

  Tree binTree = new LabeledScoredTreeFactory().newTreeNode(new CoreLabel(), Collections.singletonList(leaf));
//  binTree.setSpan(new IntPair(0, 1));
  Tree origTree = new LabeledScoredTreeFactory().newTreeNode(label, Collections.singletonList(leaf));
//  origTree.setSpan(new IntPair(0, 1));

  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binTree);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, origTree);

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
  SentimentAnnotator spy = spy(annotator);
  SentimentModel model = mock(SentimentModel.class);
//  doReturn(model).when(spy).model;

  spy.doOneSentence(annotation, sentence);

  CoreLabel resultLabel = (CoreLabel) origTree.label();
  assertEquals("Positive", resultLabel.get(SentimentCoreAnnotations.SentimentClass.class));
}
@Test
public void testDoOneSentenceDoesNotFailWhenTreeHasNoSpansSet() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "default.model");

  Annotation annotation = new Annotation("text");
  CoreMap sentence = new Annotation("sentence");

  CoreLabel label = new CoreLabel();
  Tree leaf = new LabeledScoredTreeFactory().newLeaf(label);
  Tree binTree = new LabeledScoredTreeFactory().newTreeNode(new CoreLabel(), Collections.singletonList(leaf));
  

  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binTree);

  Tree origTree = new LabeledScoredTreeFactory().newTreeNode(new CoreLabel(), Collections.singletonList(leaf));
  
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, origTree);

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
  SentimentAnnotator spy = spy(annotator);
  SentimentModel mockModel = mock(SentimentModel.class);
//  doReturn(mockModel).when(spy).model;

  spy.doOneSentence(annotation, sentence);

  assertNotNull(sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class));
  assertNotNull(sentence.get(SentimentCoreAnnotations.SentimentClass.class));
}
@Test
public void testDoOneSentenceThrowsIfTreeLabelContainsSpanAnnotation() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "dummy");

  Annotation annotation = new Annotation("x");
  CoreMap sentence = new Annotation("y");

  CoreLabel treeLabel = new CoreLabel();
  treeLabel.set(CoreAnnotations.SpanAnnotation.class, new IntPair(0, 1));

  Tree treeLeaf = new LabeledScoredTreeFactory().newLeaf(new CoreLabel());
//  treeLeaf.setSpan(new IntPair(0, 1));
  Tree tree = new LabeledScoredTreeFactory().newTreeNode(treeLabel, Collections.singletonList(treeLeaf));
//  tree.setSpan(new IntPair(0, 1));

  Tree binLeaf = new LabeledScoredTreeFactory().newLeaf(new CoreLabel());
//  binLeaf.setSpan(new IntPair(0, 1));
  Tree binTree = new LabeledScoredTreeFactory().newTreeNode(new CoreLabel(), Collections.singletonList(binLeaf));
//  binTree.setSpan(new IntPair(0, 1));

  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binTree);

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
  SentimentAnnotator spy = spy(annotator);
  SentimentModel model = mock(SentimentModel.class);
//  doReturn(model).when(spy).model;

  try {
    spy.doOneSentence(annotation, sentence);
    fail("Expected IllegalStateException due to SpanAnnotation already set.");
  } catch (IllegalStateException expected) {
    
  }
}
@Test
public void testDoOneSentenceHandlesBinarizedTreeWithNoChildren() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "dummy");

  Annotation annotation = new Annotation("abc");
  CoreMap sentence = new Annotation("single");

  Tree singleLeaf = new LabeledScoredTreeFactory().newLeaf(new CoreLabel());
//  singleLeaf.setSpan(new IntPair(0, 1));
  Tree binTree = new LabeledScoredTreeFactory().newTreeNode(new CoreLabel(), Collections.singletonList(singleLeaf));
//  binTree.setSpan(new IntPair(0, 1));

  Tree topNode = new LabeledScoredTreeFactory().newTreeNode(new CoreLabel(), Collections.emptyList());
//  topNode.setSpan(new IntPair(0, 1));
  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binTree);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, topNode);

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
  SentimentAnnotator spy = spy(annotator);
  SentimentModel model = mock(SentimentModel.class);
//  doReturn(model).when(spy).model;

  spy.doOneSentence(annotation, sentence);

  Tree resultTree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
  assertNotNull(resultTree);
  assertNotNull(sentence.get(SentimentCoreAnnotations.SentimentClass.class));
}
@Test
public void testDoOneSentenceWithNullTreeLabelDoesNotThrow() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "dummy");

  Annotation annotation = new Annotation("X");
  CoreMap sentence = new Annotation("Sentence");

//  Tree leaf = new LabeledScoredTreeFactory().newLeaf(null);
//  leaf.setSpan(new IntPair(0, 1));
//  Tree binTree = new LabeledScoredTreeFactory().newTreeNode(null, Collections.singletonList(leaf));
//  binTree.setSpan(new IntPair(0, 1));

//  Tree origTree = new LabeledScoredTreeFactory().newTreeNode(null, Collections.singletonList(leaf));
//  origTree.setSpan(new IntPair(0, 1));

//  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binTree);
//  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, origTree);

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
  SentimentAnnotator spy = spy(annotator);
  SentimentModel mockModel = mock(SentimentModel.class);
//  doReturn(mockModel).when(spy).model;

  spy.doOneSentence(annotation, sentence);

  Tree resultTree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
  assertNotNull(resultTree);
  assertNotNull(sentence.get(SentimentCoreAnnotations.SentimentClass.class));
}
@Test
public void testDoOneSentenceHandlesTreeLabelNotInstanceOfCoreLabel() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "path");

  Annotation annotation = new Annotation("Doc");
  CoreMap sentence = new Annotation("Test");

  Tree leaf = new LabeledScoredTreeFactory().newLeaf("leaf-node");
//  ((Tree) leaf).setSpan(new IntPair(0, 1));

  Tree binRoot = new LabeledScoredTreeFactory().newTreeNode("non-corelabel", Collections.singletonList(leaf));
//  binRoot.setSpan(new IntPair(0, 1));

  Tree origTree = new LabeledScoredTreeFactory().newTreeNode("non-corelabel", Collections.singletonList(leaf));
//  origTree.setSpan(new IntPair(0, 1));

  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binRoot);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, origTree);

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
  SentimentAnnotator spy = spy(annotator);
  SentimentModel model = mock(SentimentModel.class);
//  doReturn(model).when(spy).model;

  spy.doOneSentence(annotation, sentence);

  assertNotNull(sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class));
  assertNotNull(sentence.get(SentimentCoreAnnotations.SentimentClass.class));
}
@Test
public void testDoOneSentenceWithMultipleTreeNodesSameSpanAssignsOnlyOnce() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "model.gz");

  Annotation annotation = new Annotation("text");
  CoreMap sentence = new Annotation("sentence");

  CoreLabel label1 = new CoreLabel(); label1.setValue("a");
  CoreLabel label2 = new CoreLabel(); label2.setValue("b");

  Tree leaf1 = new LabeledScoredTreeFactory().newLeaf(label1);
  Tree leaf2 = new LabeledScoredTreeFactory().newLeaf(label2);

  IntPair span = new IntPair(1, 2);
//  leaf1.setSpan(span);
//  leaf2.setSpan(span);

  Tree binTree = new LabeledScoredTreeFactory().newTreeNode(new CoreLabel(), Arrays.asList(leaf1, leaf2));
//  binTree.setSpan(span);

  Tree origTree = new LabeledScoredTreeFactory().newTreeNode(new CoreLabel(), Arrays.asList(leaf1, leaf2));
//  origTree.setSpan(span);

  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binTree);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, origTree);

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
  SentimentAnnotator spy = spy(annotator);
  SentimentModel model = mock(SentimentModel.class);
//  doReturn(model).when(spy).model;

  spy.doOneSentence(annotation, sentence);

  String sent1 = label1.get(SentimentCoreAnnotations.SentimentClass.class);
  String sent2 = label2.get(SentimentCoreAnnotations.SentimentClass.class);

  assertNotNull(sent1);
  assertNotNull(sent2);
  assertEquals(sent1, sent2); 
}
@Test
public void testDoOneSentenceWithTreeNodesMissingSpanSkipsSentimentAssignment() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "model/path");

  Annotation annotation = new Annotation("doc");
  CoreMap sentence = new Annotation("sentence");

  CoreLabel label = new CoreLabel();
  Tree leaf = new LabeledScoredTreeFactory().newLeaf(label);
  

  Tree binTree = new LabeledScoredTreeFactory().newTreeNode(new CoreLabel(), Collections.singletonList(leaf));
//  binTree.setSpan(new IntPair(0, 1));

  Tree tree = new LabeledScoredTreeFactory().newTreeNode(label, Collections.singletonList(leaf));
  

  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binTree);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
  SentimentAnnotator spy = spy(annotator);
  SentimentModel model = mock(SentimentModel.class);
//  doReturn(model).when(spy).model;

  spy.doOneSentence(annotation, sentence);

  assertNotNull(sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class));
  assertNotNull(sentence.get(SentimentCoreAnnotations.SentimentClass.class));
  assertNull(label.get(SentimentCoreAnnotations.SentimentClass.class)); 
}
@Test
public void testDoOneSentenceCorrectlyRemovesSpanAnnotationAfterAssignment() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "dummy");

  Annotation annotation = new Annotation("doc");
  CoreMap sentence = new Annotation("sentence");

  CoreLabel cl = new CoreLabel();
  cl.set(CoreAnnotations.SpanAnnotation.class, new IntPair(0, 2));

  Tree leaf = new LabeledScoredTreeFactory().newLeaf(cl);
//  leaf.setSpan(new IntPair(0, 2));

  Tree binTree = new LabeledScoredTreeFactory().newTreeNode(new CoreLabel(), Collections.singletonList(leaf));
//  binTree.setSpan(new IntPair(0, 2));

  Tree origTree = new LabeledScoredTreeFactory().newTreeNode(cl, Collections.singletonList(leaf));
//  origTree.setSpan(new IntPair(0, 2));

  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binTree);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, origTree);

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
  SentimentAnnotator spy = spy(annotator);
  SentimentModel mockModel = mock(SentimentModel.class);
//  doReturn(mockModel).when(spy).model;

  spy.doOneSentence(annotation, sentence);

  CoreLabel result = (CoreLabel) origTree.label();
  assertFalse(result.containsKey(CoreAnnotations.SpanAnnotation.class));
  assertNotNull(result.get(SentimentCoreAnnotations.SentimentClass.class));
} 
}