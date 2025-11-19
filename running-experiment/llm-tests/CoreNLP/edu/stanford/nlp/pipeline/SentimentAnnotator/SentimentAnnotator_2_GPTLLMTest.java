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

public class SentimentAnnotator_2_GPTLLMTest {

 @Test
  public void testConstructorWithCustomPathLoadsModel() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "dummy/path");

    SentimentModel mockModel = mock(SentimentModel.class);
//    SentimentModelLoader loader = mock(SentimentModelLoader.class);
//    when(loader.load("dummy/path")).thenReturn(mockModel);

    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
    assertNotNull(annotator);
  }
@Test
  public void testConstructorThrowsIfModelMissing() {
    Properties props = new Properties();
    props.remove("sentiment.model");

//    thrown.expect(IllegalArgumentException.class);
//    thrown.expectMessage("No model specified for Sentiment annotator");

    new SentimentAnnotator("sentiment", props);
  }
@Test
  public void testRequirementsAreCorrect() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "dummy.path");

    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
    Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> requirements = annotator.requires();

    assertTrue(requirements.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertTrue(requirements.contains(TreeCoreAnnotations.TreeAnnotation.class));
    assertTrue(requirements.contains(TreeCoreAnnotations.BinarizedTreeAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.CategoryAnnotation.class));
    assertEquals(4, requirements.size());
  }
@Test
  public void testRequirementsSatisfiedIsEmpty() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "model");

    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
    Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    assertNotNull(satisfied);
    assertTrue(satisfied.isEmpty());
  }
@Test
  public void testSignatureDefaults() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "model/path");
    props.setProperty("sentiment.nthreads", "2");
    props.setProperty("sentiment.maxtime", "1000");

    String sig = SentimentAnnotator.signature("sentiment", props);

    assertTrue(sig.contains("sentiment.model:model/path"));
    assertTrue(sig.contains("sentiment.nthreads:2"));
    assertTrue(sig.contains("sentiment.maxtime:1000"));
  }
@Test
  public void testNThreadsFromProperties() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "m");
    props.setProperty("sentiment.nthreads", "4");

    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    int n = annotator.nThreads();
    assertEquals(4, n);
  }
@Test
  public void testMaxTimeFromProperties() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "m");
    props.setProperty("sentiment.maxtime", "30000");

    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    long max = annotator.maxTime();
    assertEquals(30000L, max);
  }
@Test
  public void testDoOneFailedSentenceDoesNotThrow() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "dummy");

    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    Annotation annotation = new Annotation("Test");
    CoreMap sentence = new Annotation("Sentence");

    annotator.doOneFailedSentence(annotation, sentence);
  }
@Test
  public void testDoOneSentenceThrowsWithoutBinarizedTree() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "dummy");

    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
    CoreMap sentence = new Annotation("Some sentence");

    sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, null);
    Annotation document = new Annotation("Document");

//    thrown.expect(AssertionError.class);
//    thrown.expectMessage("Binarized sentences not built by parser");

    annotator.doOneSentence(document, sentence);
  }
@Test
  public void testDoOneSentenceSetsSentimentAttributes() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "foo");

    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    CoreMap sentence = new Annotation("Text");
    Annotation annotation = new Annotation("Doc");

    Tree tree = Tree.valueOf("(ROOT (NP (DT The) (NN dog)))");

    Tree binTree = Tree.valueOf("(ROOT (NP (DT The) (NN dog)))");
    IntPair span = new IntPair(0, 1);

    Tree binLeaf1 = binTree.getChild(0).getChild(0);
    Tree binLeaf2 = binTree.getChild(0).getChild(1);

//    binLeaf1.setSpan(span);
//    binLeaf2.setSpan(new IntPair(1, 2));
    binTree.setSpans();

    sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binTree);
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    CoreLabel rootLabel = new CoreLabel();
    tree.setLabel(rootLabel);

    annotator.doOneSentence(annotation, sentence);

    Tree annotatedTree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
    String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);

    assertNotNull(annotatedTree);
    assertNotNull(sentiment);
  }
@Test
  public void testThrowsIfTreeHasSpanAnnotationOnLabel() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "foo");

    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    CoreMap sentence = new Annotation("S");

    Tree tree = Tree.valueOf("(ROOT (NN Word))");

    CoreLabel label = new CoreLabel();
    label.set(CoreAnnotations.SpanAnnotation.class, new IntPair(0, 1));
    tree.setLabel(label);

    sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, tree.deepCopy());
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);

    Annotation annotation = new Annotation("Doc");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

//    thrown.expect(IllegalStateException.class);
//    thrown.expectMessage("This code assumes you don't have SpanAnnotation");

    annotator.doOneSentence(annotation, sentence);
  }
@Test
public void testConstructorUsesDefaultModelWhenNotExplicitlySet() {
  Properties props = new Properties();
  

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
  assertNotNull(annotator);
}
@Test
public void testNThreadsFallsBackToGlobalNThreads() {
  Properties props = new Properties();
  props.setProperty("nthreads", "3");
  props.setProperty("sentiment.model", "dummy");

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
  assertEquals(3, annotator.nThreads());
}
@Test
public void testNThreadsDefaultsToOneWhenNotSpecified() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "dummy");

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
  assertEquals(1, annotator.nThreads());
}
@Test
public void testMaxTimeDefaultsToMinusOneWhenNotSet() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "dummy");

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
  assertEquals(-1L, annotator.maxTime());
}
@Test
public void testDoOneSentenceWithEmptyTreeShouldSetSentiment() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "dummy");

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  CoreMap sentence = new Annotation("Empty sentence");
  Tree emptyTree = Tree.valueOf("(ROOT)");

//  emptyTree.setSpan(new IntPair(0, 0));
  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, emptyTree);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, emptyTree);

  Annotation annotation = new Annotation("Doc");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  CoreLabel label = new CoreLabel();
  emptyTree.setLabel(label);

  annotator.doOneSentence(annotation, sentence);

  String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
  Tree outputTree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
  assertNotNull(sentiment);
  assertNotNull(outputTree);
}
@Test
public void testDoOneSentenceWithTreeHavingNonCoreLabel() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "dummy");

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  CoreMap sentence = new Annotation("Sentence");

  Tree tree = Tree.valueOf("(ROOT (NN Test))");
  tree.setLabel(new StringLabel("non-corelabel"));

//  tree.setSpan(new IntPair(0, 1));

  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, tree.deepCopy());

  Annotation annotation = new Annotation("Text");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.doOneSentence(annotation, sentence);

  String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
  Tree resultTree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
  assertNotNull(sentiment);
  assertNotNull(resultTree);
}
@Test
public void testDoOneSentenceWhenSpanSentimentMappingMissesSomeSubtrees() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "dummy");

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  CoreMap sentence = new Annotation("Sentence");

  Tree tree = Tree.valueOf("(ROOT (NP (NN Test)))");

  CoreLabel label = new CoreLabel();
  tree.setLabel(label);

  Tree binTree = tree.deepCopy();
  Tree childNode = binTree.getChild(0).getChild(0); 
//  childNode.setSpan(new IntPair(0, 2));

//  tree.setSpan(new IntPair(0, 3));

  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binTree);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);

  Annotation annotation = new Annotation("Anno");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.doOneSentence(annotation, sentence);

  String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
  Tree resultTree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
  assertNotNull(sentiment);
  assertNotNull(resultTree);
}
@Test
public void testDoOneSentenceWithNullTreeAnnotationSkipsTreeMappingLogic() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "dummy");

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  CoreMap sentence = new Annotation("text");
  Tree binarized = Tree.valueOf("(ROOT (NN dog))");

  binarized.setLabel(new CoreLabel());
//  binarized.setSpan(new IntPair(0, 1));

  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binarized);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, null); 

  Annotation annotation = new Annotation("doc");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.doOneSentence(annotation, sentence);

  Tree sentimentTree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
  String sentimentClass = sentence.get(SentimentCoreAnnotations.SentimentClass.class);

  assertNotNull(sentimentTree);
  assertNotNull(sentimentClass);
}
@Test
public void testDoOneSentenceWhenCollapsedUnaryHasNoSpansSetButGetSpanWorks() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "dummy");

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  CoreMap sentence = new Annotation("Sentence content.");
  Tree binarized = Tree.valueOf("(ROOT (NP (DT The)))");

  binarized.setLabel(new CoreLabel());
  Tree np = binarized.getChild(0);
  Tree dt = np.getChild(0);

//  dt.setSpan(new IntPair(0, 1));
//  np.setSpan(new IntPair(0, 1));
//  binarized.setSpan(new IntPair(0, 1));

  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binarized);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, binarized.deepCopy());

  Annotation annotation = new Annotation("Document");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  Tree labeledTree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
  labeledTree.setLabel(new CoreLabel());

  annotator.doOneSentence(annotation, sentence);

  Tree sentimentTree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
  String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);

  assertNotNull(sentimentTree);
  assertNotNull(sentiment);
}
@Test
public void testDoOneSentenceWithTreeNodeMissingSpanDoesNotThrow() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "dummy");

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  CoreMap sentence = new Annotation("A sentence.");

  Tree binarizedTree = Tree.valueOf("(ROOT (NP (JJ nice) (NN day)))");

  Tree JJ = binarizedTree.getChild(0).getChild(0);
  Tree NN = binarizedTree.getChild(0).getChild(1);

//  JJ.setSpan(new IntPair(0, 1));
  

  binarizedTree.setSpans();

  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binarizedTree);
  Tree tree = Tree.valueOf("(ROOT (NP (JJ nice) (NN day)))");
  tree.setLabel(new CoreLabel());
  tree.setSpans();

  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);

  Annotation doc = new Annotation("Doc");
  doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.doOneSentence(doc, sentence);

  Tree sentimentTree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
  String sentimentClass = sentence.get(SentimentCoreAnnotations.SentimentClass.class);

  assertNotNull(sentimentTree);
  assertNotNull(sentimentClass);
}
@Test
public void testDoOneSentenceTreeWithPartialSentimentPropagationToLabels() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "m");

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  Tree inputTree = Tree.valueOf("(ROOT (NP (DT The) (NN dog)))");
  CoreLabel rootLabel = new CoreLabel();
  inputTree.setLabel(rootLabel);

  Tree binarized = Tree.valueOf("(ROOT (NP (DT The) (NN dog)))");

  Tree DT = binarized.getChild(0).getChild(0);
  Tree NN = binarized.getChild(0).getChild(1);

//  DT.setSpan(new IntPair(0, 1));
  
//  binarized.setSpan(new IntPair(0, 2));

  CoreMap sentence = new Annotation("Test");
  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binarized);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, inputTree);

  Annotation doc = new Annotation("Doc");
  doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.doOneSentence(doc, sentence);

  Tree resultTree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
  String sentString = sentence.get(SentimentCoreAnnotations.SentimentClass.class);

  assertNotNull(resultTree);
  assertNotNull(sentString);

  CoreLabel resultRoot = (CoreLabel) resultTree.label();
  assertNotNull(resultRoot); 
}
@Test
public void testSignatureWithEmptyPropertiesUsesDefaults() {
  Properties props = new Properties();

  String signature = SentimentAnnotator.signature("sentiment", props);

  assertTrue(signature.contains("sentiment.model:edu/stanford/nlp/models/sentiment/sentiment.ser.gz"));
  assertTrue(signature.contains("sentiment.nthreads:"));
  assertTrue(signature.contains("sentiment.maxtime:-1"));
}
@Test
public void testSignatureUsesExplicitGlobalAndLocalProperties() {
  Properties props = new Properties();
  props.setProperty("nthreads", "4");
  props.setProperty("sentiment.nthreads", "6");
  props.setProperty("sentiment.model", "/custom/path/model.ser.gz");
  props.setProperty("sentiment.maxtime", "1500");

  String sig = SentimentAnnotator.signature("sentiment", props);

  assertTrue(sig.contains("sentiment.model:/custom/path/model.ser.gz"));
  assertTrue(sig.contains("sentiment.nthreads:6"));
  assertTrue(sig.contains("sentiment.maxtime:1500"));
}
@Test
public void testDoOneSentenceWithTreeHavingAllNullSpans() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "dummy");

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  Tree tree = Tree.valueOf("(ROOT (NP (NN Something)))");
  tree.setLabel(new CoreLabel());

  Tree binarized = tree.deepCopy();
//  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binarized);
//  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);

  CoreMap sentence = new Annotation("Sentence with no span");
  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binarized);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);

  Annotation annotation = new Annotation("Doc");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.doOneSentence(annotation, sentence);

  String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
  Tree sentimentTree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);

  assertNotNull(sentiment);
  assertNotNull(sentimentTree);
}
@Test
public void testDoOneSentenceCoreLabelWithoutSentimentAnnotationKeyPresent() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "x");

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  Tree originalTree = Tree.valueOf("(ROOT (NP (NN test)))");
  CoreLabel label = new CoreLabel();
  originalTree.setLabel(label);
//  originalTree.setSpan(new IntPair(0, 1));

  Tree binTree = originalTree.deepCopy();
  CoreLabel newLabel = new CoreLabel();
  newLabel.set(CoreAnnotations.SpanAnnotation.class, new IntPair(0, 1));
  binTree.setLabel(newLabel);

  CoreMap sentence = new Annotation("Test");
  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binTree);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, originalTree);

  Annotation document = new Annotation("Document");
  document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  try {
    annotator.doOneSentence(document, sentence);
    fail("Exception expected due to presence of CoreAnnotations.SpanAnnotation");
  } catch (IllegalStateException e) {
    assertTrue(e.getMessage().contains("This code assumes you don't have SpanAnnotation"));
  }
}
@Test
public void testDoOneSentenceWithTreeHavingSingleNodeOnly() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "dummy");

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  Tree tree = Tree.valueOf("(A)");
  CoreLabel label = new CoreLabel();
  tree.setLabel(label);
//  tree.setSpan(new IntPair(0, 1));

  CoreMap sentence = new Annotation("Single node");
  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, tree);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree.deepCopy());

  Annotation annotation = new Annotation("Doc");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.doOneSentence(annotation, sentence);

  String sentimentClass = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
  Tree annotatedTree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);

  assertNotNull(sentimentClass);
  assertNotNull(annotatedTree);
}
@Test
public void testConstructorWithOnlyTopLevelModelPathProvided() {
  Properties props = new Properties();
  props.setProperty("model", "/default/top/model/path");

  
  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  assertNotNull(annotator);
}
@Test
public void testConstructorWithOnlyGlobalNThreadsAndNoAnnotatorPrefixedNThreads() {
  Properties props = new Properties();
  props.setProperty("nthreads", "10");
  props.setProperty("sentiment.model", "x");

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
  int threads = annotator.nThreads();

  assertEquals(10, threads);
}
@Test
public void testConstructorWithNegativeMaxTime() {
  Properties props = new Properties();
  props.setProperty("sentiment.maxtime", "-123");
  props.setProperty("sentiment.model", "x");

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
  long maxtime = annotator.maxTime();

  assertEquals(-123L, maxtime);
}
@Test
public void testDoOneSentenceWithCoreLabelThatHasNonSentimentKeys() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "dummy");

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  Tree tree = Tree.valueOf("(ROOT (NN Hello))");
  CoreLabel label = new CoreLabel();
  label.set(CoreAnnotations.ValueAnnotation.class, "Hello");
  tree.setLabel(label);
//  tree.setSpan(new IntPair(0, 1));

  Tree binTree = tree.deepCopy();
  binTree.setLabel(new CoreLabel());
  binTree.setSpans();

  CoreMap sentence = new Annotation("Sentence");
  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binTree);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);

  Annotation annotation = new Annotation("Document");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.doOneSentence(annotation, sentence);

  Tree result = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
  String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);

  assertNotNull(result);
  assertNotNull(sentiment);
}
@Test
public void testDoOneSentenceWithDeepNestedTree() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "dummy");

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  Tree tree = Tree.valueOf("(ROOT (S (VP (VP (VP (VB run))))) )");
  CoreLabel rootLabel = new CoreLabel();
  tree.setLabel(rootLabel);
//  tree.getChild(0).getChild(0).getChild(0).setSpan(new IntPair(0, 1));
  tree.setSpans();

  Tree binTree = tree.deepCopy();
  CoreLabel binLabel = new CoreLabel();
  binTree.setLabel(binLabel);
  binTree.setSpans();

  CoreMap sentence = new Annotation("Nested sentence");
  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binTree);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);

  Annotation doc = new Annotation("Doc");
  doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.doOneSentence(doc, sentence);

  Tree sentimentTree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
  String sentimentClass = sentence.get(SentimentCoreAnnotations.SentimentClass.class);

  assertNotNull(sentimentTree);
  assertNotNull(sentimentClass);
}
@Test
public void testDoOneSentenceWithMismatchedTreeStructureBetweenTreeAndBinarizedTree() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "dummy");

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  Tree binTree = Tree.valueOf("(ROOT (NP (NN Apple)))");
  binTree.setSpans();
//  binTree.getChild(0).getChild(0).setSpan(new IntPair(0, 1));
  binTree.setLabel(new CoreLabel());

  Tree tree = Tree.valueOf("(ROOT (NP (NN Apple) (NNs People)))"); 
  tree.setSpans();
  tree.setLabel(new CoreLabel());

  CoreMap sentence = new Annotation("Mismatch");
  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binTree);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);

  Annotation annotation = new Annotation("Doc");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.doOneSentence(annotation, sentence);

  String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
  assertNotNull(sentiment);
}
@Test
public void testDoOneSentenceTreeWithInvalidSpanIntPairOnLabel() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "dummy");

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  Tree tree = Tree.valueOf("(X)");
  CoreLabel rootLabel = new CoreLabel();
  rootLabel.setBeginPosition(5);
  rootLabel.setEndPosition(3);
  tree.setLabel(rootLabel);
//  tree.setSpan(new IntPair(5, 3));

  Tree binTree = tree.deepCopy();
  binTree.setLabel(new CoreLabel());
//  binTree.setSpan(new IntPair(5, 3));

  CoreMap sentence = new Annotation("Invalid Span");
  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binTree);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);

  Annotation doc = new Annotation("Doc");
  doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.doOneSentence(doc, sentence);

  Tree resultTree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
  String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
  assertNotNull(resultTree);
  assertNotNull(sentiment);
}
@Test
public void testDoOneSentenceWhenTreeContainsMultipleIdenticalSpans() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "dummy");

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  Tree repeated = Tree.valueOf("(ROOT (NP (NN One) (NN One)))");
  Tree child1 = repeated.getChild(0).getChild(0);
  Tree child2 = repeated.getChild(0).getChild(1);
//  child1.setSpan(new IntPair(0, 1));
//  child2.setSpan(new IntPair(0, 1));
  repeated.setSpans();

  CoreLabel topLabel = new CoreLabel();
  repeated.setLabel(topLabel);

  Tree binarized = repeated.deepCopy();
  binarized.setLabel(new CoreLabel());

  CoreMap sentence = new Annotation("Repeated span");
  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binarized);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, repeated);

  Annotation annotation = new Annotation("Doc");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.doOneSentence(annotation, sentence);

  Tree sentimentTree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
  String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
  assertNotNull(sentimentTree);
  assertNotNull(sentiment);
}
@Test
public void testConstructorWithEmptyPropertiesUsesDefaultThreadAndMaxValues() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "dummy");

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  int threadCount = annotator.nThreads();
  long timeout = annotator.maxTime();

  assertEquals(1, threadCount);
  assertEquals(-1L, timeout);
}
@Test
public void testDoOneSentenceWhereTreeAnnotationContainsNonCoreLabelDoesNotThrow() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "dummy");

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  Tree tree = Tree.valueOf("(ROOT (VB Run))");
  tree.setLabel(new StringLabel("JustALabel"));
  tree.setSpans();

  Tree binTree = tree.deepCopy();
  binTree.setLabel(new CoreLabel());
  binTree.setSpans();

  CoreMap sentence = new Annotation("Non core label.");
  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binTree);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);

  Annotation annotation = new Annotation("Doc");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.doOneSentence(annotation, sentence);

  String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
  Tree result = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);

  assertNotNull(sentiment);
  assertNotNull(result);
}
@Test
public void testDoOneSentenceWithEmptyTreeSetStillGeneratesSentiment() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "dummy");

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  Tree tree = Tree.valueOf("(ROOT)");
  tree.setLabel(new CoreLabel());

  Tree binTree = Tree.valueOf("(ROOT)");
  binTree.setLabel(new CoreLabel());

  CoreMap sentence = new Annotation("Empty tree");
  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binTree);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);

  Annotation annotation = new Annotation("Doc");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.doOneSentence(annotation, sentence);

  Tree annotated = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
  String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);

  assertNotNull(annotated);
  assertNotNull(sentiment);
}
@Test
public void testDoOneSentenceBinarizedTreeWithNullSpanNodeSkipsMappingGracefully() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "dummy");

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  Tree binTree = Tree.valueOf("(ROOT (NN a) (NN b))");
//  binTree.getChild(0).setSpan(null);
//  binTree.getChild(1).setSpan(new IntPair(0, 1));
  binTree.setLabel(new CoreLabel());

  Tree tree = Tree.valueOf("(ROOT (NN a) (NN b))");
  tree.setLabel(new CoreLabel());

  CoreMap sentence = new Annotation("Test");
  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binTree);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);

  Annotation doc = new Annotation("Sentence");
  doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.doOneSentence(doc, sentence);

  String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
  Tree annotated = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);

  assertNotNull(sentiment);
  assertNotNull(annotated);
}
@Test
public void testDoOneSentenceTreeWithNoLeavesDoesNotThrow() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "x");

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  Tree tree = Tree.valueOf("(ROOT)");
  tree.setLabel(new CoreLabel());
  tree.setSpans();

  CoreMap sentence = new Annotation("No leaves");

  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, tree.deepCopy());
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);

  Annotation document = new Annotation("TestCase");
  document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.doOneSentence(document, sentence);

  Tree sentimentTree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
  String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);

  assertNotNull(sentimentTree);
  assertNotNull(sentiment);
}
@Test
public void testConstructorWithInvalidNThreadsFallsBackToDefault() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "x");
  props.setProperty("sentiment.nthreads", "not-a-number");

  
  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  assertEquals(1, annotator.nThreads());
}
@Test
public void testConstructorWithInvalidMaxtimeFallsBackToDefault() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "x");
  props.setProperty("sentiment.maxtime", "oops");

  
  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  assertEquals(-1L, annotator.maxTime());
}
@Test
public void testDoOneSentenceWithCollapsedTreeMissingPredictionCallsSetSentimentAnyway() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "m");

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  Tree tree = Tree.valueOf("(ROOT (NN cat))");
  tree.setLabel(new CoreLabel());
//  tree.setSpan(new IntPair(0, 1));

  Tree binTree = tree.deepCopy();
  binTree.setLabel(new CoreLabel());

  
  CoreMap sentence = new Annotation("No prediction");
  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binTree);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);

  Annotation annotation = new Annotation("Ann");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.doOneSentence(annotation, sentence);

  Tree parsedTree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
  String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);

  assertNotNull(parsedTree);
  assertNotNull(sentiment);
}
@Test
public void testDoOneSentenceWithNullLabelsOnTreeNodesDoesNotThrow() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "dummy");

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  Tree tree = Tree.valueOf("(ROOT (NP (DT The) (NN cat)))");
  tree.setLabel(null);
  Tree binTree = tree.deepCopy();
  binTree.setLabel(new CoreLabel());

//  binTree.getChild(0).getChild(0).setSpan(new IntPair(0, 1));
//  binTree.getChild(0).getChild(1).setSpan(new IntPair(1, 2));

  CoreMap sentence = new Annotation("The cat.");
  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binTree);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);

  Annotation annotation = new Annotation("Complete");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.doOneSentence(annotation, sentence);

  assertNotNull(sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class));
  assertNotNull(sentence.get(SentimentCoreAnnotations.SentimentClass.class));
}
@Test
public void testDoOneSentenceWithNullAnnotationMapOnTreeLabelDoesNotThrow() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "dummy");

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  Tree tree = Tree.valueOf("(ROOT (JJ fast))");
  CoreLabel label = new CoreLabel();
  tree.setLabel(label);
  tree.getChild(0).setLabel(null);
//  tree.setSpan(new IntPair(0,1));

  Tree binTree = tree.deepCopy();
  binTree.setLabel(new CoreLabel());
//  binTree.getChild(0).setSpan(new IntPair(0,1));

  CoreMap sentence = new Annotation("Fast.");
  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binTree);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);

  Annotation annotation = new Annotation("Doc");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.doOneSentence(annotation, sentence);

  assertNotNull(sentence.get(SentimentCoreAnnotations.SentimentClass.class));
}
@Test
public void testDoOneSentenceDoesNotOverwriteExistingSentimentClass() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "dummy");

  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  Tree tree = Tree.valueOf("(ROOT (JJ amazing))");
  CoreLabel label = new CoreLabel();
  label.set(SentimentCoreAnnotations.SentimentClass.class, "Positive");
  tree.setLabel(label);
  tree.setSpans();

  Tree binTree = tree.deepCopy();
  binTree.setLabel(new CoreLabel());
  binTree.setSpans();

  CoreMap sentence = new Annotation("Amazing.");
  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binTree);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);

  Annotation annotation = new Annotation("Doc");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.doOneSentence(annotation, sentence);

  String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
  assertNotNull(sentiment); 
}
@Test
public void testDoOneSentenceWithBinarizedTreeWhoseLabelsAreNull() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "dummy");
  
  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
  
  Tree binarizedTree = Tree.valueOf("(ROOT (JJ happy))");
  binarizedTree.setLabel(null); 
  Tree child = binarizedTree.getChild(0);
//  child.setSpan(new IntPair(0,1));

  Tree fullTree = binarizedTree.deepCopy();
  fullTree.setLabel(new CoreLabel());

  CoreMap sentence = new Annotation("happy");
  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binarizedTree);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, fullTree);

  Annotation annotation = new Annotation("doc");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.doOneSentence(annotation, sentence);

  assertNotNull(sentence.get(SentimentCoreAnnotations.SentimentClass.class));
}
@Test
public void testNullSentenceMapSkipsWithoutException() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "dummy");
  
  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

  CoreMap sentence = new Annotation("Unannotated");
  

  Annotation annotation = new Annotation("doc");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  try {
    annotator.doOneSentence(annotation, sentence);
    fail("Expected AssertionError due to missing binarized tree");
  } catch (AssertionError e) {
    assertTrue(e.getMessage().contains("Binarized sentences not built by parser"));
  }
}
@Test
public void testSentimentMappingSkipsWhenSpansMismatch() {
  Properties props = new Properties();
  props.setProperty("sentiment.model", "dummy");
  
  SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
  
  Tree tree = Tree.valueOf("(ROOT (NN crab))");
  Tree binTree = Tree.valueOf("(ROOT (NN crab))");
  
  CoreLabel label = new CoreLabel();
  tree.setLabel(label);
  binTree.setLabel(new CoreLabel());
  
  
//  tree.setSpan(new IntPair(10, 20));
//  binTree.getChild(0).setSpan(new IntPair(0, 1));
//  binTree.setSpan(new IntPair(0, 1));
  
  CoreMap sentence = new Annotation("Span mismatch");
  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binTree);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
  
  Annotation annotation = new Annotation("Mismatch doc");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
  
  annotator.doOneSentence(annotation, sentence);
  
  assertNotNull(sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class));
  assertNotNull(sentence.get(SentimentCoreAnnotations.SentimentClass.class));
} 
}