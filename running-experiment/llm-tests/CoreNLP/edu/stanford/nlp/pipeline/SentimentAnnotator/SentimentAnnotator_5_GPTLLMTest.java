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

public class SentimentAnnotator_5_GPTLLMTest {

 @Test
  public void testConstructor_WithValidModelProperty() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
    assertNotNull(annotator);
  }
@Test(expected = IllegalArgumentException.class)
  public void testConstructor_MissingModelThrowsException() {
    Properties props = new Properties();
    props.remove("sentiment.model");
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
  }
@Test
  public void testSignature_WithDefaultProperties() {
    Properties props = new Properties();
    String signature = SentimentAnnotator.signature("sentiment", props);
    assertTrue(signature.contains("sentiment.model:edu/stanford/nlp/models/sentiment/sentiment.ser.gz"));
    assertTrue(signature.contains("sentiment.nthreads:"));
    assertTrue(signature.contains("sentiment.maxtime:-1"));
  }
@Test
  public void testSignature_WithCustomProperties() {
    Properties props = new Properties();
    props.setProperty("custom.model", "my-model.gz");
    props.setProperty("custom.nthreads", "2");
    props.setProperty("custom.maxtime", "5000");

    String signature = SentimentAnnotator.signature("custom", props);

    assertTrue(signature.contains("custom.model:my-model.gz"));
    assertTrue(signature.contains("custom.nthreads:2"));
    assertTrue(signature.contains("custom.maxtime:5000"));
  }
@Test
  public void testRequirementsSatisfiedReturnsEmpty() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
    Set<Class<? extends CoreAnnotation>> result = annotator.requirementsSatisfied();
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
@Test
  public void testRequiresContainsExpectedAnnotations() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    Set<Class<? extends CoreAnnotation>> result = annotator.requires();

    assertTrue(result.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertTrue(result.contains(TreeCoreAnnotations.TreeAnnotation.class));
    assertTrue(result.contains(TreeCoreAnnotations.BinarizedTreeAnnotation.class));
    assertTrue(result.contains(CoreAnnotations.CategoryAnnotation.class));
  }
@Test
  public void testDoOneFailedSentenceDoesNotThrowException() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    Annotation annotation = new Annotation("Some text");
    Annotation sentence = new Annotation("Sentence text");

    try {
      annotator.doOneFailedSentence(annotation, sentence);
    } catch (Exception e) {
      fail("doOneFailedSentence should not throw: " + e.getMessage());
    }
  }
@Test(expected = AssertionError.class)
  public void testDoOneSentenceThrowsIfBinarizedTreeMissing() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    CoreMap sentence = new Annotation("This is a test.");
    annotator.doOneSentence(new Annotation(""), sentence);
  }
@Test
  public void testDoOneSentenceSetsSentimentAnnotations() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    
    Tree binarized = Tree.valueOf("(ROOT (S (NP (DT The) (NN cat)) (VP (VBD sat))))");

    
    Tree original = Tree.valueOf("(ROOT (S (NP (DT The) (NN cat)) (VP (VBD sat))))");

    CoreMap sentence = new Annotation("The cat sat.");
    sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binarized);
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, original);

    annotator.doOneSentence(new Annotation("Document text"), sentence);

    Tree annotatedTree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
    String sentimentClass = sentence.get(SentimentCoreAnnotations.SentimentClass.class);

    assertNotNull(annotatedTree);
    assertNotNull(sentimentClass);
  }
@Test
  public void testDoOneSentenceWithTreeHavingNonConflictingSpanAnnotation() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    Tree binarized = Tree.valueOf("(ROOT (S (NP (NNX Mock)) (VP (VBD tested))))");
    Tree origTree = Tree.valueOf("(ROOT (S (NP (NNX Mock)) (VP (VBD tested))))");

    CoreLabel label = new CoreLabel(); 
    origTree.setLabel(label);

    CoreMap sentence = new Annotation("Mock tested");

    sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binarized);
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, origTree);

    annotator.doOneSentence(new Annotation(""), sentence);

    Tree collapsed = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
    String sentimentStr = sentence.get(SentimentCoreAnnotations.SentimentClass.class);

    assertNotNull(collapsed);
    assertNotNull(sentimentStr);
    assertTrue(((CoreLabel) origTree.label()).containsKey(SentimentCoreAnnotations.SentimentClass.class));
  }
@Test(expected = IllegalStateException.class)
  public void testDoOneSentenceThrowsIfTreeHasSpanAnnotation() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    Tree binarized = Tree.valueOf("(ROOT (S (NN Testing)))");
    Tree origTree = Tree.valueOf("(ROOT (S (NN Testing)))");

    CoreLabel label = new CoreLabel();
    label.set(CoreAnnotations.SpanAnnotation.class, new IntPair(0, 1)); 
    origTree.setLabel(label);

    CoreMap sentence = new Annotation("Testing");

    sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binarized);
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, origTree);

    annotator.doOneSentence(new Annotation(""), sentence);
  }
@Test
  public void testNThreadsAndMaxTimeDefaults() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");

    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
    assertEquals(1, annotator.nThreads());
    assertEquals(-1L, annotator.maxTime());
  }
@Test
  public void testNThreadsAndMaxTimeSetViaProperties() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");
    props.setProperty("sentiment.nthreads", "4");
    props.setProperty("sentiment.maxtime", "2500");

    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
    assertEquals(4, annotator.nThreads());
    assertEquals(2500L, annotator.maxTime());
  }
@Test
public void testDoOneSentence_WithNullTreeAnnotation() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");
  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  Tree binarized = Tree.valueOf("(ROOT (S (VB Run)))");

  CoreMap sentence = new Annotation("Run");
  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binarized);
  

  try {
    annotator.doOneSentence(new Annotation("Doc"), sentence);
  } catch (Exception e) {
    fail("Should not throw when TreeAnnotation is null");
  }

  Tree annotatedTree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
  String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);

  assertNotNull(annotatedTree);
  assertNotNull(sentiment);
}
@Test
public void testDoOneSentence_SpanSentimentOnlySetsFirstMatch() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");
  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  Tree binarized = Tree.valueOf("(ROOT (S (NP (DT A) (NN test)) (VP (VBZ passes))))");
  Tree original = Tree.valueOf("(ROOT (S (NP (DT A) (NN test)) (VP (VBZ passes))))");

  CoreLabel label = new CoreLabel();
  original.setLabel(label);

  CoreMap sentence = new Annotation("A test passes");
  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binarized);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, original);

  annotator.doOneSentence(new Annotation("Doc"), sentence);

  Tree annotatedTree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
  String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);

  assertNotNull(annotatedTree);
  assertNotNull(sentiment);
  assertTrue(((CoreLabel) original.label()).containsKey(SentimentCoreAnnotations.SentimentClass.class));
}
@Test(expected = NullPointerException.class)
public void testConstructor_WithNullProperties() {
  new SentimentAnnotator("sentiment", null);
}
@Test
public void testDoOneSentence_TreeWithEmptyLabels() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");
  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  Tree binarized = new LabeledScoredTreeFactory().newTreeNode("ROOT", new ArrayList<>());
  binarized.addChild(new LabeledScoredTreeFactory().newLeaf("word"));
  Tree original = binarized.deepCopy();

  CoreLabel label = new CoreLabel();
  original.setLabel(label);

  CoreMap sentence = new Annotation("word");
  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binarized);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, original);

  annotator.doOneSentence(new Annotation(""), sentence);

  Tree resultTree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
  String sentimentClass = sentence.get(SentimentCoreAnnotations.SentimentClass.class);

  assertNotNull(resultTree);
  assertNotNull(sentimentClass);
}
@Test
public void testDoOneSentence_TreeWithMultipleIdenticalSpans() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");
  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  Tree child1 = Tree.valueOf("(NN word)");
  Tree child2 = Tree.valueOf("(NN word)");
  Tree binarized = new LabeledScoredTreeFactory().newTreeNode("NP", Arrays.asList(child1, child2));
  Tree original = binarized.deepCopy();
  original.setLabel(new CoreLabel());

  CoreMap sentence = new Annotation("word word");
  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binarized);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, original);

  annotator.doOneSentence(new Annotation(""), sentence);

  Tree resultTree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
  String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);

  assertNotNull(resultTree);
  assertNotNull(sentiment);
}
@Test
public void testDoOneSentence_TreeWithDeepNesting() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");
  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  
  Tree leaf = Tree.valueOf("(NN word)");
  Tree current = leaf;
  for (int i = 0; i < 10; i++) {
    current = new LabeledScoredTreeFactory().newTreeNode("X", Collections.singletonList(current));
  }
  Tree binarized = current;
  Tree original = current.deepCopy();

  CoreLabel label = new CoreLabel();
  original.setLabel(label);

  CoreMap sentence = new Annotation("word");
  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binarized);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, original);

  annotator.doOneSentence(new Annotation("deep tree"), sentence);

  Tree annotated = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
  String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);

  assertNotNull(annotated);
  assertNotNull(sentiment);
}
@Test
public void testSignatureHandlesEmptyAnnotatorName() {
  Properties props = new Properties();
  props.setProperty(".model", "model.gz");
  props.setProperty(".nthreads", "1");
  props.setProperty(".maxtime", "100");
  String signature = SentimentAnnotator.signature("", props);
  assertTrue(signature.contains(".model:model.gz"));
  assertTrue(signature.contains(".nthreads:1"));
  assertTrue(signature.contains(".maxtime:100"));
}
@Test
public void testConstructor_ModelPropertySetToEmptyString() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "");
  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
  assertNotNull(annotator);
}
@Test
public void testSignature_WithMissingIndividualProperties() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "model.ser.gz");

  String signature = SentimentAnnotator.signature("sentiment", props);

  assertTrue(signature.contains("sentiment.model:model.ser.gz"));
  assertTrue(signature.contains("sentiment.nthreads:"));
  assertTrue(signature.contains("sentiment.maxtime:-1"));
}
@Test
public void testRequires_ReturnsUnmodifiableSet() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");
  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  Set<Class<? extends CoreAnnotation>> required = annotator.requires();

  try {
    required.add(CoreAnnotations.AfterAnnotation.class);
    fail("Expected UnsupportedOperationException");
  } catch (UnsupportedOperationException e) {
    
  }
}
@Test
public void testDoOneSentence_WithSingleLeafTree() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");
  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  Tree leaf = Tree.valueOf("(NN Solo)");

  CoreMap sentence = new Annotation("Solo");
  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, leaf);

  Tree original = Tree.valueOf("(NN Solo)");
  CoreLabel label = new CoreLabel();
  original.setLabel(label);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, original);

  annotator.doOneSentence(new Annotation(""), sentence);

  assertNotNull(sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class));
  assertNotNull(sentence.get(SentimentCoreAnnotations.SentimentClass.class));
}
@Test
public void testDoOneSentence_WithEmptyChildListTree() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");
  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  Tree emptyNode = new LabeledScoredTreeFactory().newTreeNode("Empty", new ArrayList<>());
  Tree original = emptyNode.deepCopy();
  CoreLabel label = new CoreLabel();
  original.setLabel(label);

  CoreMap sentence = new Annotation("Empty");
  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, emptyNode);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, original);

  annotator.doOneSentence(new Annotation(""), sentence);

  assertNotNull(sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class));
  assertNotNull(sentence.get(SentimentCoreAnnotations.SentimentClass.class));
}
@Test
public void testDoOneSentence_TreeLabelNotCoreLabel() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");
  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  Tree binarized = Tree.valueOf("(ROOT (NN test))");
  Tree original = Tree.valueOf("(ROOT (NN test))");

  original.setLabel(new StringLabel("NotCoreLabel"));

  CoreMap sentence = new Annotation("test");
  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binarized);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, original);

  try {
    annotator.doOneSentence(new Annotation(""), sentence);
  } catch (ClassCastException e) {
    
    return;
  }

  fail("Expected ClassCastException when tree label is not a CoreLabel");
}
@Test
public void testDoOneSentence_EmptyTreeLabelValueHandledGracefully() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");
  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  Tree binarized = Tree.valueOf("(NN )");
  Tree original = Tree.valueOf("(NN )");

  CoreLabel label = new CoreLabel();
  original.setLabel(label);

  CoreMap sentence = new Annotation("empty");
  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binarized);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, original);

  annotator.doOneSentence(new Annotation(""), sentence);

  assertNotNull(sentence.get(SentimentCoreAnnotations.SentimentClass.class));
}
@Test
public void testDoOneSentence_SetsSentimentOnlyOnceForDuplicateSpan() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");
  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  Tree leaf1 = Tree.valueOf("(NN Word1)");
  Tree leaf2 = Tree.valueOf("(NN Word2)");
  Tree parent = new LabeledScoredTreeFactory().newTreeNode("NP", Arrays.asList(leaf1, leaf2));

  Tree binarized = new LabeledScoredTreeFactory().newTreeNode("ROOT", Collections.singletonList(parent));
  binarized.setSpans(); 

  Tree original = binarized.deepCopy();
  for (Tree subtree : original) {
    CoreLabel label = new CoreLabel();
    subtree.setLabel(label);
  }

  CoreMap sentence = new Annotation("Word1 Word2");
  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binarized);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, original);

  annotator.doOneSentence(new Annotation(""), sentence);

  Tree annotated = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
  String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);

  assertNotNull(annotated);
  assertNotNull(sentiment);

  
  for (Tree t : original) {
    Label label = t.label();
    if (label instanceof CoreLabel) {
      assertTrue(((CoreLabel) label).containsKey(SentimentCoreAnnotations.SentimentClass.class));
    }
  }
}
@Test
public void testDoOneSentence_TreeWithOneNode_NoChildren() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");
  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  Tree leaf = Tree.valueOf("(NN Single)");

  CoreLabel label = new CoreLabel();
  leaf.setLabel(label);

  CoreMap sentence = new Annotation("Single");
  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, leaf);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, leaf);

  annotator.doOneSentence(new Annotation(""), sentence);

  assertNotNull(sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class));
  assertNotNull(sentence.get(SentimentCoreAnnotations.SentimentClass.class));
  assertTrue(label.containsKey(SentimentCoreAnnotations.SentimentClass.class));
}
@Test
public void testDoOneSentence_TreeWhereSubtreeLacksSpan() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");
  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  Tree leaf = Tree.valueOf("(NN Child)");
  Tree parent = new LabeledScoredTreeFactory().newTreeNode("NP", Collections.singletonList(leaf));
  parent.label().setValue("NP");

  
  Tree binarized = new LabeledScoredTreeFactory().newTreeNode("ROOT", Collections.singletonList(parent));
  binarized.setSpans(); 

  Tree original = binarized.deepCopy();
  for (Tree t : original) {
    CoreLabel cl = new CoreLabel();
    t.setLabel(cl);
  }

  CoreMap sentence = new Annotation("Child");
  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binarized);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, original);

  annotator.doOneSentence(new Annotation(""), sentence);

  Tree resultTree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
  String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);

  assertNotNull(resultTree);
  assertNotNull(sentiment);
}
@Test
public void testDoOneSentence_LabelMissingCoreAnnotationsIsSafe() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");
  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  Tree leaf = Tree.valueOf("(NN Danger)");
  Tree root = new LabeledScoredTreeFactory().newTreeNode("ROOT", Collections.singletonList(leaf));
  root.setSpans();

  Tree original = root.deepCopy();
  for (Tree t : original) {
    t.setLabel(new CoreLabel()); 
  }

  CoreMap sentence = new Annotation("Danger");
  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, root);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, original);

  annotator.doOneSentence(new Annotation("Doc"), sentence);

  Tree annotatedTree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
  String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);

  assertNotNull(annotatedTree);
  assertNotNull(sentiment);
}
@Test
public void testDoOneSentence_TreeWithMixedCoreLabelAndStringLabel() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");
  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  Tree leaf1 = Tree.valueOf("(NN one)");
  leaf1.setLabel(new CoreLabel());
  Tree leaf2 = Tree.valueOf("(NN two)");
  leaf2.setLabel(new StringLabel("two"));  

  Tree parent = new LabeledScoredTreeFactory().newTreeNode("NP", Arrays.asList(leaf1, leaf2));
  Tree binarized = new LabeledScoredTreeFactory().newTreeNode("ROOT", Collections.singletonList(parent));
  binarized.setSpans();

  Tree original = binarized.deepCopy();
  for (Tree t : original) {
    if (t.isLeaf()) {
      t.setLabel(new CoreLabel());
    } else {
      t.setLabel(new CoreLabel());
    }
  }

  CoreMap sentence = new Annotation("one two");
  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binarized);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, original);

  annotator.doOneSentence(new Annotation("Document"), sentence);

  Tree annotated = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
  String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);

  assertNotNull(annotated);
  assertNotNull(sentiment);
}
@Test
public void testDoOneSentence_WithNullAnnotationArgument() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");
  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  Tree tree = Tree.valueOf("(ROOT (NN text))");
  CoreLabel label = new CoreLabel();
  tree.setLabel(label);

  CoreMap sentence = new Annotation("text");
  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, tree);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);

  try {
    annotator.doOneSentence(null, sentence);
  } catch (NullPointerException e) {
    fail("Method should not throw NullPointerException if input Annotation is null");
  }
}
@Test
public void testDoOneSentence_WithEmptyAnnotationText() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");
  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  Tree tree = Tree.valueOf("(ROOT (NN nothing))");
  CoreLabel label = new CoreLabel();
  tree.setLabel(label);

  CoreMap sentence = new Annotation("");
  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, tree);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);

  annotator.doOneSentence(new Annotation(""), sentence);

  assertNotNull(sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class));
  assertNotNull(sentence.get(SentimentCoreAnnotations.SentimentClass.class));
}
@Test
public void testDoOneSentence_WithSpanSentimentConflictIgnored() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");
  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  Tree n1 = Tree.valueOf("(NN one)");
  Tree n2 = Tree.valueOf("(NN two)");
  Tree parent = new LabeledScoredTreeFactory().newTreeNode("NP", Arrays.asList(n1, n2));
  Tree root = new LabeledScoredTreeFactory().newTreeNode("ROOT", Collections.singletonList(parent));
  root.setSpans();

  Tree original = root.deepCopy();
  for (Tree t : original) {
    CoreLabel cl = new CoreLabel();
    t.setLabel(cl);
  }

  CoreMap sentence = new Annotation("one two");
  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, root);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, original);

  annotator.doOneSentence(new Annotation("Document"), sentence);

  Tree annotated = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
  String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
  assertNotNull(annotated);
  assertNotNull(sentiment);
}
@Test
public void testConstructor_WithUnrelatedPropertiesPresent() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");
  props.setProperty("irrelevant.key", "somevalue");
  props.setProperty("nthreads", "99");

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  assertEquals(1, annotator.nThreads()); 
  assertEquals(-1L, annotator.maxTime());
}
@Test
public void testMaxTimePropertyParsingWithoutAnnotatorPrefix() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");
  props.setProperty("maxtime", "4321");

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  assertEquals(-1L, annotator.maxTime()); 
}
@Test
public void testNThreadsFallbackToGlobalProperty() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");
  props.setProperty("nthreads", "2");

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  assertEquals(2, annotator.nThreads());
}
@Test
public void testSignatureHandlesAllValuesAbsent() {
  Properties props = new Properties(); 

  String sig = SentimentAnnotator.signature("sentiment", props);

  assertTrue(sig.contains("sentiment.model:edu/stanford/nlp/models/sentiment/sentiment.ser.gz"));
  assertTrue(sig.contains("sentiment.nthreads:"));
  assertTrue(sig.contains("sentiment.maxtime:-1"));
}
@Test
public void testTreeSpanSentiment_MixedSpansAssignedCorrectly() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");
  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  Tree leaf1 = Tree.valueOf("(NN A)");
  Tree leaf2 = Tree.valueOf("(NN B)");
  Tree inner = new LabeledScoredTreeFactory().newTreeNode("NP", Arrays.asList(leaf1, leaf2));
  Tree root = new LabeledScoredTreeFactory().newTreeNode("ROOT", Collections.singletonList(inner));
  root.setSpans();

  Tree original = root.deepCopy();
  for (Tree t : original) {
    CoreLabel cl = new CoreLabel();
    t.setLabel(cl);
  }

  CoreMap sentence = new Annotation("A B");
  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, root);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, original);

  annotator.doOneSentence(new Annotation("Doc"), sentence);

  for (Tree t : original) {
    if (t.label() instanceof CoreLabel) {
      assertTrue(((CoreLabel) t.label()).containsKey(SentimentCoreAnnotations.SentimentClass.class));
    }
  }
}
@Test
public void testDoOneSentence_WithNullTreeLabels() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");
  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  Tree leaf = Tree.valueOf("(NN test)");
  Tree root = new LabeledScoredTreeFactory().newTreeNode("ROOT", Collections.singletonList(leaf));
  root.setSpans();

  Tree original = root.deepCopy();
  for (Tree node : original) {
    node.setLabel(null); 
  }

  CoreMap sentence = new Annotation("test");
  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, root);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, original);

  try {
    annotator.doOneSentence(new Annotation("Done"), sentence);
  } catch (Exception e) {
    fail("Should safely handle null tree labels without throwing exception");
  }

  Tree annotated = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
  String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);

  assertNotNull(annotated);
  assertNotNull(sentiment);
}
@Test
public void testDoOneSentence_SubtreeWithoutLabelObject() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");
  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  Tree leaf1 = Tree.valueOf("(NN A)");
  Tree leaf2 = Tree.valueOf("(NN B)");
  Tree midNode = new LabeledScoredTreeFactory().newTreeNode("NP", Arrays.asList(leaf1, leaf2));
  Tree root = new LabeledScoredTreeFactory().newTreeNode("ROOT", Collections.singletonList(midNode));
  root.setSpans();

  Tree original = root.deepCopy();
  int index = 0;
  for (Tree node : original) {
    if (index == 1) {
      node.setLabel(null); 
    } else {
      node.setLabel(new CoreLabel());
    }
    index++;
  }

  CoreMap sentence = new Annotation("A B");
  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, root);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, original);

  try {
    annotator.doOneSentence(new Annotation("Doc"), sentence);
  } catch (Exception e) {
    fail("Should ignore subtree without label safely");
  }

  Tree annotatedTree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
  String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);

  assertNotNull(annotatedTree);
  assertNotNull(sentiment);
}
@Test
public void testDoOneSentence_WithTreeLabelOtherThanCoreLabel_LeavesUnchanged() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");
  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  Tree tree = Tree.valueOf("(ROOT (VP does))");
  tree.setSpans();

  Tree original = tree.deepCopy();
  for (Tree node : original) {
    node.setLabel(new StringLabel("notCore")); 
  }

  CoreMap sentence = new Annotation("does");
  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, tree);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, original);

  annotator.doOneSentence(new Annotation("X"), sentence);

  assertNotNull(sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class));
  assertNotNull(sentence.get(SentimentCoreAnnotations.SentimentClass.class));

  for (Tree t : original) {
    assertFalse(t.label() instanceof CoreLabel && ((CoreLabel) t.label()).containsKey(SentimentCoreAnnotations.SentimentClass.class));
  }
}
@Test
public void testDoOneSentence_CreatesSentimentOnlyForMatchingSpan() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");
  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  Tree left = Tree.valueOf("(NN A)");
  Tree right = Tree.valueOf("(NN B)");
  Tree root = new LabeledScoredTreeFactory().newTreeNode("NP", Arrays.asList(left, right));
  root.setSpans();

  Tree original = root.deepCopy();
  for (Tree node : original) {
    IntPair span = node.getSpan();
    if (span.getTarget() == 1) {
      continue; 
    }
    CoreLabel label = new CoreLabel();
    node.setLabel(label);
  }

  CoreMap sentence = new Annotation("A B");
  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, root);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, original);

  annotator.doOneSentence(new Annotation("Doc"), sentence);

  Tree annotated = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
  String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
  assertNotNull(annotated);
  assertNotNull(sentiment);
}
@Test
public void testDoOneSentence_SafeOnEmptyTreeStructure() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");
  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  Tree root = new LabeledScoredTreeFactory().newTreeNode("EMPTY", new ArrayList<>());
  Tree original = root.deepCopy();
  original.setLabel(new CoreLabel());

  CoreMap sentence = new Annotation("");
  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, root);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, original);

  try {
    annotator.doOneSentence(new Annotation(""), sentence);
  } catch (Exception e) {
    fail("Should not error on empty tree structure");
  }

  assertNotNull(sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class));
  assertNotNull(sentence.get(SentimentCoreAnnotations.SentimentClass.class));
}
@Test
public void testDoOneSentence_TreeWithDeepUnaryChain() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");
  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  Tree leaf = Tree.valueOf("(NN deep)");
  Tree current = leaf;
  for (int i = 0; i < 6; i++) {
    current = new LabeledScoredTreeFactory().newTreeNode("U", Collections.singletonList(current));
  }
  Tree root = new LabeledScoredTreeFactory().newTreeNode("ROOT", Collections.singletonList(current));
  root.setSpans();

  Tree original = root.deepCopy();
  for (Tree node : original) {
    CoreLabel label = new CoreLabel();
    node.setLabel(label);
  }

  CoreMap sentence = new Annotation("deep");
  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, root);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, original);

  annotator.doOneSentence(new Annotation("Doc"), sentence);

  assertNotNull(sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class));
  assertNotNull(sentence.get(SentimentCoreAnnotations.SentimentClass.class));
}
@Test
public void testDoOneSentence_TreeMissingSpansMethodCall() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");
  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  Tree leaf = Tree.valueOf("(NN word)");
  Tree root = new LabeledScoredTreeFactory().newTreeNode("ROOT", Collections.singletonList(leaf));

  
  Tree original = root.deepCopy();
  for (Tree t : original) {
    CoreLabel label = new CoreLabel();
    t.setLabel(label);
  }

  CoreMap sentence = new Annotation("word");
  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, root);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, original);

  annotator.doOneSentence(new Annotation("some text"), sentence);

  assertNotNull(sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class));
  assertNotNull(sentence.get(SentimentCoreAnnotations.SentimentClass.class));
}
@Test
public void testDoOneSentence_WithTreeHavingSharedChildren() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");
  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  Tree sharedLeaf = Tree.valueOf("(NN shared)");
  Tree node1 = new LabeledScoredTreeFactory().newTreeNode("NP", Collections.singletonList(sharedLeaf));
  Tree node2 = new LabeledScoredTreeFactory().newTreeNode("VP", Collections.singletonList(sharedLeaf));
  Tree root = new LabeledScoredTreeFactory().newTreeNode("ROOT", Arrays.asList(node1, node2));
  root.setSpans(); 

  Tree original = root.deepCopy();
  for (Tree t : original) {
    CoreLabel label = new CoreLabel();
    t.setLabel(label);
  }

  CoreMap sentence = new Annotation("shared shared");
  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, root);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, original);

  annotator.doOneSentence(new Annotation("multi use"), sentence);

  assertNotNull(sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class));
  assertNotNull(sentence.get(SentimentCoreAnnotations.SentimentClass.class));
}
@Test
public void testConstructor_IgnoresUnrecognizedProperties() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");
  props.setProperty("sentiment.unknown", "foobar");

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
  assertNotNull(annotator);
}
@Test
public void testDoOneSentence_WithSpanAnnotationRemovedFromCoreLabel() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");
  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  Tree leaf = Tree.valueOf("(NN X)");
  Tree root = new LabeledScoredTreeFactory().newTreeNode("ROOT", Collections.singletonList(leaf));
  root.setSpans();

  Tree original = root.deepCopy();
  for (Tree t : original) {
    CoreLabel label = new CoreLabel();
    label.set(CoreAnnotations.SpanAnnotation.class, new IntPair(0, 1));
    t.setLabel(label);
  }

  
  for (Tree t : original) {
    ((CoreLabel) t.label()).remove(CoreAnnotations.SpanAnnotation.class);
  }

  CoreMap sentence = new Annotation("X");
  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, root);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, original);

  annotator.doOneSentence(new Annotation(""), sentence);

  assertNotNull(sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class));
  assertNotNull(sentence.get(SentimentCoreAnnotations.SentimentClass.class));
}
@Test
public void testDoOneSentence_TreeWithOnlyUnaryBranches() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");
  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  Tree child = Tree.valueOf("(NN Leaf)");
  Tree unary1 = new LabeledScoredTreeFactory().newTreeNode("A", Collections.singletonList(child));
  Tree unary2 = new LabeledScoredTreeFactory().newTreeNode("B", Collections.singletonList(unary1));
  Tree unary3 = new LabeledScoredTreeFactory().newTreeNode("C", Collections.singletonList(unary2));
  Tree root = new LabeledScoredTreeFactory().newTreeNode("ROOT", Collections.singletonList(unary3));
  root.setSpans();

  Tree original = root.deepCopy();
  for (Tree t : original) {
    CoreLabel label = new CoreLabel();
    t.setLabel(label);
  }

  CoreMap sentence = new Annotation("Leaf");
  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, root);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, original);

  annotator.doOneSentence(new Annotation("Test unary"), sentence);

  assertNotNull(sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class));
  assertNotNull(sentence.get(SentimentCoreAnnotations.SentimentClass.class));
}
@Test
public void testRequirementsContainsExactlyExpectedTypes() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
  Set<Class<? extends CoreAnnotation>> result = annotator.requires();

  assertEquals(4, result.size());
  assertTrue(result.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
  assertTrue(result.contains(CoreAnnotations.CategoryAnnotation.class));
  assertTrue(result.contains(TreeCoreAnnotations.TreeAnnotation.class));
  assertTrue(result.contains(TreeCoreAnnotations.BinarizedTreeAnnotation.class));
} 
}