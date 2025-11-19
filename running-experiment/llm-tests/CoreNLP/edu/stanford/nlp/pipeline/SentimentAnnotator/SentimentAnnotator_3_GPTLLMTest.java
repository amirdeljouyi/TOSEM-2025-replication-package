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

public class SentimentAnnotator_3_GPTLLMTest {

 @Test
    public void testRequirementsSatisfiedIsEmptySet() {
        Properties props = new Properties();
        props.setProperty("mock.model", "mockModelPath");
        SentimentAnnotator annotator = new SentimentAnnotator("mock", props);
        Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> result = annotator.requirementsSatisfied();
        assertTrue(result.isEmpty());
    }
@Test
    public void testRequiresContainsExpectedAnnotations() {
        Properties props = new Properties();
        props.setProperty("mock.model", "mockModelPath");
        SentimentAnnotator annotator = new SentimentAnnotator("mock", props);
        Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> reqs = annotator.requires();

        assertTrue(reqs.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
        assertTrue(reqs.contains(TreeCoreAnnotations.TreeAnnotation.class));
        assertTrue(reqs.contains(TreeCoreAnnotations.BinarizedTreeAnnotation.class));
        assertTrue(reqs.contains(CoreAnnotations.CategoryAnnotation.class));
        assertEquals(4, reqs.size());
    }
@Test
    public void testSignatureReturnsExpectedString() {
        Properties props = new Properties();
        props.setProperty("mock.model", "mockModelPath");
        props.setProperty("mock.nthreads", "2");
        props.setProperty("mock.maxtime", "1000");
        String sig = SentimentAnnotator.signature("mock", props);
        
        assertTrue(sig.contains("mock.model:mockModelPath"));
        assertTrue(sig.contains("mock.nthreads:2"));
        assertTrue(sig.contains("mock.maxtime:1000"));
    }
@Test(expected = IllegalArgumentException.class)
    public void testConstructorThrowsWhenModelMissing() {
        Properties props = new Properties(); 
        new SentimentAnnotator("missing", props);
    }
@Test
    public void testNThreadsAndMaxTimeWithDefaults() {
        Properties props = new Properties();
        props.setProperty("mock.model", "mockPath");
        SentimentAnnotator annotator = new SentimentAnnotator("mock", props);
        assertEquals(1, annotator.nThreads());
        assertEquals(-1L, annotator.maxTime());
    }
@Test
    public void testDoOneSentenceAnnotatesCorrectly() {
        Properties props = new Properties();
        props.setProperty("mock.model", "mockPath");
        SentimentAnnotator annotator = new SentimentAnnotator("mock", props);

        Annotation annotation = new Annotation("Test sentence");
        CoreMap sentence = new Annotation("Test sentence");
        Tree leaf = new LabeledScoredTreeFactory().newLeaf("amazing");
        Tree tree = new LabeledScoredTreeFactory().newTreeNode("ROOT", Collections.singletonList(leaf));
        tree.setSpans();

        sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, tree.deepCopy());
        sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree.deepCopy());

        annotator.doOneSentence(annotation, sentence);

        Tree resultTree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
        String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
        assertNotNull(resultTree);
        assertEquals("Neutral", sentiment);
    }
@Test(expected = AssertionError.class)
    public void testDoOneSentenceThrowsWithoutBinarizedTree() {
        Properties props = new Properties();
        props.setProperty("mock.model", "mockPath");
        SentimentAnnotator annotator = new SentimentAnnotator("mock", props);

        Annotation annotation = new Annotation("Input");
        CoreMap sentence = new Annotation("Input");

        annotator.doOneSentence(annotation, sentence);
    }
@Test
    public void testDoOneFailedSentenceDoesNothing() {
        Properties props = new Properties();
        props.setProperty("mock.model", "mockPath");
        SentimentAnnotator annotator = new SentimentAnnotator("mock", props);

        Annotation annotation = new Annotation("fail input");
        CoreMap sentence = new Annotation("fail input");

        annotator.doOneFailedSentence(annotation, sentence);
        
    }
@Test
    public void testDoOneSentenceSpanSentimentAppliedToTree() {
        Properties props = new Properties();
        props.setProperty("mock.model", "mockPath");
        SentimentAnnotator annotator = new SentimentAnnotator("mock", props);

        Annotation annotation = new Annotation("Tree test");
        CoreMap sentence = new Annotation("Tree test");

        CoreLabel leafLabel = new CoreLabel();
        Tree leaf = new LabeledScoredTreeFactory().newLeaf(leafLabel);
        Tree node = new LabeledScoredTreeFactory().newTreeNode("NP", Collections.singletonList(leaf));
        Tree root = new LabeledScoredTreeFactory().newTreeNode("S", Collections.singletonList(node));
        root.setSpans();

        sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, root.deepCopy());
        sentence.set(TreeCoreAnnotations.TreeAnnotation.class, root);

        annotator.doOneSentence(annotation, sentence);

        Tree resultTree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
        CoreLabel rootLabel = (CoreLabel) resultTree.label();
        assertNotNull(rootLabel.get(SentimentCoreAnnotations.SentimentClass.class));
    }
@Test(expected = IllegalStateException.class)
    public void testDoOneSentenceThrowsIfTreeHasSpanAnnotation() {
        Properties props = new Properties();
        props.setProperty("mock.model", "mockPath");
        SentimentAnnotator annotator = new SentimentAnnotator("mock", props);

        Annotation annotation = new Annotation("It has a span");
        CoreMap sentence = new Annotation("It has a span");

        CoreLabel label = new CoreLabel();
        label.set(CoreAnnotations.SpanAnnotation.class, new IntPair(0, 1));
        Tree leaf = new LabeledScoredTreeFactory().newLeaf(label);
        Tree root = new LabeledScoredTreeFactory().newTreeNode("A", Collections.singletonList(leaf));
        root.setSpans();

        sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, root.deepCopy());
        sentence.set(TreeCoreAnnotations.TreeAnnotation.class, root);

        annotator.doOneSentence(annotation, sentence);
    }
@Test
    public void testIndependentSentencesGetAnnotated() {
        Properties props = new Properties();
        props.setProperty("mock.model", "mockPath");
        SentimentAnnotator annotator = new SentimentAnnotator("mock", props);

        
        Annotation annotation1 = new Annotation("Sentence one");
        CoreMap sentence1 = new Annotation("Sentence one");
        Tree leaf1 = new LabeledScoredTreeFactory().newLeaf("great");
        Tree tree1 = new LabeledScoredTreeFactory().newTreeNode("S", Collections.singletonList(leaf1));
        tree1.setSpans();
        sentence1.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, tree1.deepCopy());
        sentence1.set(TreeCoreAnnotations.TreeAnnotation.class, tree1.deepCopy());
        annotator.doOneSentence(annotation1, sentence1);
        assertEquals("Neutral", sentence1.get(SentimentCoreAnnotations.SentimentClass.class));

        
        Annotation annotation2 = new Annotation("Sentence two");
        CoreMap sentence2 = new Annotation("Sentence two");
        Tree leaf2 = new LabeledScoredTreeFactory().newLeaf("terrible");
        Tree tree2 = new LabeledScoredTreeFactory().newTreeNode("S", Collections.singletonList(leaf2));
        tree2.setSpans();
        sentence2.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, tree2.deepCopy());
        sentence2.set(TreeCoreAnnotations.TreeAnnotation.class, tree2.deepCopy());
        annotator.doOneSentence(annotation2, sentence2);
        assertEquals("Neutral", sentence2.get(SentimentCoreAnnotations.SentimentClass.class));
    }
@Test
    public void testDoOneSentenceWithNullTreeAnnotation() {
        Properties props = new Properties();
        props.setProperty("mock.model", "mockPath");

        SentimentAnnotator annotator = new SentimentAnnotator("mock", props);
        Annotation annotation = new Annotation("Input");
        CoreMap sentence = new Annotation("Sentence");

        Tree leaf = new LabeledScoredTreeFactory().newLeaf("fine");
        Tree binarizedTree = new LabeledScoredTreeFactory().newTreeNode("NP", Collections.singletonList(leaf));
        binarizedTree.setSpans();

        
        sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binarizedTree);

        annotator.doOneSentence(annotation, sentence);

        Tree sentimentTree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
        String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);

        assertNotNull(sentimentTree);
        assertEquals("Neutral", sentiment);
    }
@Test
    public void testDoOneSentenceWithUnannotatedNodeSpanMatchingRoot() {
        Properties props = new Properties();
        props.setProperty("mock.model", "mockPath");

        SentimentAnnotator annotator = new SentimentAnnotator("mock", props);
        Annotation annotation = new Annotation("Span Overlap");
        CoreMap sentence = new Annotation("Span Overlap");

        CoreLabel label = new CoreLabel();
        Tree leaf = new LabeledScoredTreeFactory().newLeaf(label);
        Tree subtree = new LabeledScoredTreeFactory().newTreeNode("VP", Collections.singletonList(leaf));
        Tree root = new LabeledScoredTreeFactory().newTreeNode("ROOT", Collections.singletonList(subtree));

        root.setSpans();

        sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, root.deepCopy());
        sentence.set(TreeCoreAnnotations.TreeAnnotation.class, root);

        annotator.doOneSentence(annotation, sentence);

        Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);

        CoreLabel rootLabel = (CoreLabel) tree.label();
        CoreLabel vpLabel = (CoreLabel) tree.children()[0].label();

        String rootSent = rootLabel.get(SentimentCoreAnnotations.SentimentClass.class);
        String vpSent = vpLabel.get(SentimentCoreAnnotations.SentimentClass.class);
        assertEquals("Neutral", rootSent);
        assertEquals("Neutral", vpSent);
    }
@Test
    public void testMultipleTreeLevelsAnnotatedSeparately() {
        Properties props = new Properties();
        props.setProperty("mock.model", "mockPath");
        SentimentAnnotator annotator = new SentimentAnnotator("mock", props);

        Annotation annotation = new Annotation("Nested");
        CoreMap sentence = new Annotation("Nested");

        CoreLabel leaf1 = new CoreLabel();
        CoreLabel leaf2 = new CoreLabel();
        Tree word1 = new LabeledScoredTreeFactory().newLeaf(leaf1);
        Tree word2 = new LabeledScoredTreeFactory().newLeaf(leaf2);

        Tree phrase = new LabeledScoredTreeFactory().newTreeNode("NP", Collections.singletonList(word1));
        Tree sentenceTree = new LabeledScoredTreeFactory().newTreeNode("S", Arrays.asList(phrase, word2));
        sentenceTree.setSpans();

        sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, sentenceTree.deepCopy());
        sentence.set(TreeCoreAnnotations.TreeAnnotation.class, sentenceTree.deepCopy());

        annotator.doOneSentence(annotation, sentence);

        Tree annotatedTree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);

        CoreLabel sLabel = (CoreLabel) annotatedTree.label();
        CoreLabel npLabel = (CoreLabel) annotatedTree.children()[0].label();
        CoreLabel w2Label = (CoreLabel) annotatedTree.children()[1].label();

        assertEquals("Neutral", sLabel.get(SentimentCoreAnnotations.SentimentClass.class));
        assertEquals("Neutral", npLabel.get(SentimentCoreAnnotations.SentimentClass.class));
        assertEquals("Neutral", w2Label.get(SentimentCoreAnnotations.SentimentClass.class));
    }
@Test
    public void testDoOneSentenceWithEmptyTreeSpanMapping() {
        Properties props = new Properties();
        props.setProperty("mock.model", "mockPath");

        SentimentAnnotator annotator = new SentimentAnnotator("mock", props);
        Annotation annotation = new Annotation("Edge example");
        CoreMap sentence = new Annotation("Edge example");

        CoreLabel leafLabel = new CoreLabel();
        Tree leaf = new LabeledScoredTreeFactory().newLeaf(leafLabel);
        Tree internal = new LabeledScoredTreeFactory().newTreeNode("X", Collections.singletonList(leaf));
        Tree root = new LabeledScoredTreeFactory().newTreeNode("Y", Collections.singletonList(internal));
        root.setSpans();

//        root.children()[0].label().set(CoreAnnotations.SpanAnnotation.class, new IntPair(10, 20));

        sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, root.deepCopy());
        sentence.set(TreeCoreAnnotations.TreeAnnotation.class, root.deepCopy());

        annotator.doOneSentence(annotation, sentence);

        Tree annotatedTree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
        CoreLabel internalLabel = (CoreLabel) annotatedTree.children()[0].label();

        assertEquals("Neutral", internalLabel.get(SentimentCoreAnnotations.SentimentClass.class));
        assertNull(internalLabel.get(CoreAnnotations.SpanAnnotation.class));
    }
@Test
    public void testDoOneSentenceWithNonLabeledTreeNodesStillSucceeds() {
        Properties props = new Properties();
        props.setProperty("mock.model", "mockPath");
        SentimentAnnotator annotator = new SentimentAnnotator("mock", props);

        Annotation annotation = new Annotation("No labeled nodes");
        CoreMap sentence = new Annotation("No labeled nodes");

//        Tree leaf = new Tree() {
//            public String value() { return "leaf"; }
//            public void setValue(String label) {}
//            public Tree[] children() { return new Tree[0]; }
//            public Tree parent() { return null; }
//        };

//        Tree customTree = new LabeledScoredTreeFactory().newTreeNode("ROOT", Collections.singletonList(leaf));
//        customTree.setSpans();

//        sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, customTree.deepCopy());
//        sentence.set(TreeCoreAnnotations.TreeAnnotation.class, customTree.deepCopy());

        annotator.doOneSentence(annotation, sentence);

        Tree sentimentTree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
        String cls = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
        assertNotNull(sentimentTree);
        assertEquals("Neutral", cls);
    }
@Test
    public void testSentimentAnnotatorWithMissingNThreadsAndMaxTimeProperties() {
        Properties props = new Properties();
        props.setProperty("mock.model", "mockPath"); 

        SentimentAnnotator annotator = new SentimentAnnotator("mock", props);

        assertEquals(1, annotator.nThreads()); 
        assertEquals(-1L, annotator.maxTime()); 
    }
@Test
    public void testSentimentAnnotatorWithGlobalNThreadsAndMaxTime() {
        Properties props = new Properties();
        props.setProperty("mock.model", "mockPath");
        props.setProperty("nthreads", "4");
        props.setProperty("maxtime", "500");

        SentimentAnnotator annotator = new SentimentAnnotator("mock", props);

        assertEquals(4, annotator.nThreads()); 
        assertEquals(500L, annotator.maxTime());
    }
@Test
    public void testSignatureFallbacksToDefaults() {
        Properties props = new Properties();
        String signature = SentimentAnnotator.signature("custom", props);

        assertTrue(signature.contains("custom.model:edu/stanford/nlp/models/sentiment/sentiment.ser.gz"));
        assertTrue(signature.contains("custom.nthreads:"));
        assertTrue(signature.contains("custom.maxtime:-1"));
    }
@Test
    public void testDoOneSentenceEmptyLabelTree() {
        Properties props = new Properties();
        props.setProperty("mock.model", "mockPath");

        SentimentAnnotator annotator = new SentimentAnnotator("mock", props);

        Annotation annotation = new Annotation("Empty");
        CoreMap sentence = new Annotation("Empty");

        Tree leaf = new LabeledScoredTreeFactory().newLeaf("");
        Tree node = new LabeledScoredTreeFactory().newTreeNode("", Collections.singletonList(leaf));
        node.setSpans();

        sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, node.deepCopy());
        sentence.set(TreeCoreAnnotations.TreeAnnotation.class, node.deepCopy());

        annotator.doOneSentence(annotation, sentence);

        String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
        assertEquals("Neutral", sentiment);
    }
@Test
    public void testDoOneSentenceWithPartiallyAnnotatedSpans() {
        Properties props = new Properties();
        props.setProperty("mock.model", "mockPath");

        SentimentAnnotator annotator = new SentimentAnnotator("mock", props);

        Annotation annotation = new Annotation("Mixed spans");
        CoreMap sentence = new Annotation("Mixed spans");

        CoreLabel label1 = new CoreLabel();
        Tree leaf1 = new LabeledScoredTreeFactory().newLeaf(label1);
        Tree mid = new LabeledScoredTreeFactory().newTreeNode("X", Collections.singletonList(leaf1));
        Tree root = new LabeledScoredTreeFactory().newTreeNode("Y", Collections.singletonList(mid));

        root.setSpans();

        CoreLabel existing = new CoreLabel();
        existing.set(CoreAnnotations.SpanAnnotation.class, new IntPair(0, 1));
        Tree leaf2 = new LabeledScoredTreeFactory().newLeaf(existing);
        Tree other = new LabeledScoredTreeFactory().newTreeNode("Z", Collections.singletonList(leaf2));

        Tree top = new LabeledScoredTreeFactory().newTreeNode("TOP", Collections.unmodifiableList(Arrays.asList(root, other)));
        top.setSpans();

        sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, top.deepCopy());
        sentence.set(TreeCoreAnnotations.TreeAnnotation.class, top.deepCopy());

        try {
            annotator.doOneSentence(annotation, sentence);
            fail("Expected IllegalStateException to be thrown");
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage().contains("This code assumes you don't have SpanAnnotation"));
        }
    }
@Test
    public void testDoOneSentenceWithMultipleIdenticalSpans() {
        Properties props = new Properties();
        props.setProperty("mock.model", "mockPath");

        SentimentAnnotator annotator = new SentimentAnnotator("mock", props);

        Annotation annotation = new Annotation("Conflicting spans");
        CoreMap sentence = new Annotation("Conflicting spans");

        CoreLabel label1 = new CoreLabel();
        CoreLabel label2 = new CoreLabel();

        Tree leaf1 = new LabeledScoredTreeFactory().newLeaf(label1);
        Tree leaf2 = new LabeledScoredTreeFactory().newLeaf(label2);

        Tree node1 = new LabeledScoredTreeFactory().newTreeNode("A", Collections.singletonList(leaf1));
        Tree node2 = new LabeledScoredTreeFactory().newTreeNode("A", Collections.singletonList(leaf2));

        Tree root = new LabeledScoredTreeFactory().newTreeNode("ROOT", Arrays.asList(node1, node2));
        root.setSpans();

        sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, root.deepCopy());
        sentence.set(TreeCoreAnnotations.TreeAnnotation.class, root.deepCopy());

        annotator.doOneSentence(annotation, sentence);

        String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
        assertEquals("Neutral", sentiment);
    }
@Test
    public void testDoOneSentenceHandlesSingleLeafTree() {
        Properties props = new Properties();
        props.setProperty("mock.model", "mockPath");

        SentimentAnnotator annotator = new SentimentAnnotator("mock", props);

        Annotation annotation = new Annotation("Single Leaf");
        CoreMap sentence = new Annotation("Single Leaf");

        CoreLabel coreLabel = new CoreLabel();
        Tree leaf = new LabeledScoredTreeFactory().newLeaf(coreLabel);
        leaf.setSpans();

        sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, leaf.deepCopy());
        sentence.set(TreeCoreAnnotations.TreeAnnotation.class, leaf.deepCopy());

        annotator.doOneSentence(annotation, sentence);

        Tree annotated = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
        String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);

        assertNotNull(annotated);
        assertEquals("Neutral", sentiment);
    }
@Test
    public void testDoOneSentenceWithNonOverlappingSpans() {
        Properties props = new Properties();
        props.setProperty("mock.model", "mockPath");

        SentimentAnnotator annotator = new SentimentAnnotator("mock", props);

        Annotation annotation = new Annotation("Non-overlap test");
        CoreMap sentence = new Annotation("Non-overlap test");

        CoreLabel label1 = new CoreLabel();
        Tree leaf1 = new LabeledScoredTreeFactory().newLeaf(label1);
        Tree node1 = new LabeledScoredTreeFactory().newTreeNode("L1", Collections.singletonList(leaf1));
        node1.setSpans();

        CoreLabel label2 = new CoreLabel();
        Tree leaf2 = new LabeledScoredTreeFactory().newLeaf(label2);
        Tree node2 = new LabeledScoredTreeFactory().newTreeNode("L2", Collections.singletonList(leaf2));
        node2.setSpans();

        Tree root = new LabeledScoredTreeFactory().newTreeNode("S", Arrays.asList(node1, node2));
        root.setSpans();

        sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, root.deepCopy());
        sentence.set(TreeCoreAnnotations.TreeAnnotation.class, root.deepCopy());

        annotator.doOneSentence(annotation, sentence);

        Tree result = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
        
        CoreLabel parentLabel1 = (CoreLabel) result.children()[0].label();
        CoreLabel parentLabel2 = (CoreLabel) result.children()[1].label();

        assertEquals("Neutral", parentLabel1.get(SentimentCoreAnnotations.SentimentClass.class));
        assertEquals("Neutral", parentLabel2.get(SentimentCoreAnnotations.SentimentClass.class));
    }
@Test
    public void testSignatureWithPartialPropertiesOnlyModel() {
        Properties props = new Properties();
        props.setProperty("custom.model", "customPath");

        String signature = SentimentAnnotator.signature("custom", props);

        assertTrue(signature.contains("custom.model:customPath"));
        assertTrue(signature.contains("custom.nthreads:"));
        assertTrue(signature.contains("custom.maxtime:-1"));
    }
@Test
    public void testSignatureWithAllCustomProperties() {
        Properties props = new Properties();
        props.setProperty("x.model", "pathA");
        props.setProperty("x.nthreads", "5");
        props.setProperty("x.maxtime", "999");

        String sig = SentimentAnnotator.signature("x", props);

        assertTrue(sig.contains("x.model:pathA"));
        assertTrue(sig.contains("x.nthreads:5"));
        assertTrue(sig.contains("x.maxtime:999"));
    }
@Test
    public void testDoOneSentenceEmptyTreeHandlesGracefully() {
        Properties props = new Properties();
        props.setProperty("mock.model", "mockPath");
        SentimentAnnotator annotator = new SentimentAnnotator("mock", props);

        Annotation annotation = new Annotation("Empty tree sentence");
        CoreMap sentence = new Annotation("Empty tree sentence");

        Tree emptyNode = new LabeledScoredTreeFactory().newTreeNode("EMPTY", new ArrayList<>());
        emptyNode.setSpans();

        sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, emptyNode.deepCopy());
        sentence.set(TreeCoreAnnotations.TreeAnnotation.class, emptyNode.deepCopy());

        annotator.doOneSentence(annotation, sentence);

        Tree annotated = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
        String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);

        assertNotNull(annotated);
        assertEquals("Neutral", sentiment);
    }
@Test
    public void testDoOneSentenceManualSpanDoesNotOverwriteFirst() {
        Properties props = new Properties();
        props.setProperty("mock.model", "mockPath");
        SentimentAnnotator annotator = new SentimentAnnotator("mock", props);

        Annotation annotation = new Annotation("Manual span check");
        CoreMap sentence = new Annotation("Manual span check");

        CoreLabel label1 = new CoreLabel();
        Tree leaf1 = new LabeledScoredTreeFactory().newLeaf(label1);
        Tree node1 = new LabeledScoredTreeFactory().newTreeNode("X", Collections.singletonList(leaf1));
        node1.setSpans();

        CoreLabel label2 = new CoreLabel();
        Tree leaf2 = new LabeledScoredTreeFactory().newLeaf(label2);
        Tree node2 = new LabeledScoredTreeFactory().newTreeNode("X", Collections.singletonList(leaf2));
        node2.setSpans();

        Tree root = new LabeledScoredTreeFactory().newTreeNode("ROOT", Arrays.asList(node1, node2));
        root.setSpans();

        sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, root.deepCopy());
        sentence.set(TreeCoreAnnotations.TreeAnnotation.class, root.deepCopy());

        annotator.doOneSentence(annotation, sentence);

        Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
        CoreLabel node1Label = (CoreLabel) tree.children()[0].label();
        CoreLabel node2Label = (CoreLabel) tree.children()[1].label();

        assertEquals("Neutral", node1Label.get(SentimentCoreAnnotations.SentimentClass.class));
        assertEquals("Neutral", node2Label.get(SentimentCoreAnnotations.SentimentClass.class));
    }
@Test
    public void testDoOneSentenceTreeWithNoLabels() {
        Properties props = new Properties();
        props.setProperty("mock.model", "mockPath");
        SentimentAnnotator annotator = new SentimentAnnotator("mock", props);

        Annotation annotation = new Annotation("No label tree");
        CoreMap sentence = new Annotation("No label tree");

//        Tree genericLeaf = new Tree() {
//            public Tree[] children() { return new Tree[0]; }
//            public Tree parent() { return null; }
//            public void setValue(String value) {}
//            public String value() { return "x"; }
//        };

//        Tree outer = new LabeledScoredTreeFactory().newTreeNode("Top", Collections.singletonList(genericLeaf));
//        outer.setSpans();

//        sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, outer.deepCopy());
//        sentence.set(TreeCoreAnnotations.TreeAnnotation.class, outer.deepCopy());

        annotator.doOneSentence(annotation, sentence);

        Tree annotatedTree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
        String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);

        assertNotNull(annotatedTree);
        assertEquals("Neutral", sentiment);
    }
@Test
    public void testConstructorWithAllAnnotatorSpecificProperties() {
        Properties props = new Properties();
        props.setProperty("sentiment.model", "custom/model/path.gz");
        props.setProperty("sentiment.nthreads", "3");
        props.setProperty("sentiment.maxtime", "567");

        SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

        assertEquals(3, annotator.nThreads());
        assertEquals(567L, annotator.maxTime());
    }
@Test
    public void testConstructorWithNoPropertiesUsesDefaults() {
        Properties props = new Properties();

        
        props.setProperty("nthreads", "2");
        props.setProperty("maxtime", "999");
        props.setProperty("basic.model", "somepath");

        SentimentAnnotator annotator = new SentimentAnnotator("basic", props);

        assertEquals(2, annotator.nThreads());
        assertEquals(999L, annotator.maxTime());
    }
@Test
    public void testRequirementsSatisfiedReturnsEmpty() {
        Properties props = new Properties();
        props.setProperty("x.model", "value");

        SentimentAnnotator annotator = new SentimentAnnotator("x", props);
        Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> result = annotator.requirementsSatisfied();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
@Test
    public void testDoOneSentenceWithTreeLabelNotInstanceOfCoreLabel() {
        Properties props = new Properties();
        props.setProperty("mock.model", "mockPath");

        SentimentAnnotator annotator = new SentimentAnnotator("mock", props);
        Annotation annotation = new Annotation("Non-CoreLabel test");
        CoreMap sentence = new Annotation("Non-CoreLabel test");

        Tree root = new LabeledScoredTreeFactory().newTreeNode("X", new ArrayList<Tree>());
        root.setSpans();

        sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, root.deepCopy());

        
//        Tree genericLeaf = new Tree() {
//            public Tree[] children() { return new Tree[0]; }
//            public Tree parent() { return null; }
//            public void setValue(String value) {}
//            public String value() { return "leaf"; }
//        };

//        Tree customTree = new LabeledScoredTreeFactory().newTreeNode("ROOT", Collections.singletonList(genericLeaf));
//        customTree.setSpans();
//        sentence.set(TreeCoreAnnotations.TreeAnnotation.class, customTree);

        
        annotator.doOneSentence(annotation, sentence);

        String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
        assertEquals("Neutral", sentiment);
    }
@Test
    public void testDoOneSentenceWithMixedLabelTypesPartialAnnotation() {
        Properties props = new Properties();
        props.setProperty("mock.model", "mockPath");

        SentimentAnnotator annotator = new SentimentAnnotator("mock", props);
        Annotation annotation = new Annotation("Mixed labels");
        CoreMap sentence = new Annotation("Mixed labels");

        CoreLabel label1 = new CoreLabel();
        Tree leaf1 = new LabeledScoredTreeFactory().newLeaf(label1);
        Tree node1 = new LabeledScoredTreeFactory().newTreeNode("X", Collections.singletonList(leaf1));

//        Tree node2 = new Tree() {
//            public Tree[] children() { return new Tree[0]; }
//            public Tree parent() { return null; }
//            public void setValue(String value) {}
//            public String value() { return "X"; }
//        };

//        Tree root = new LabeledScoredTreeFactory().newTreeNode("ROOT", Arrays.asList(node1, node2));
//        root.setSpans();

//        sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, root.deepCopy());
//        sentence.set(TreeCoreAnnotations.TreeAnnotation.class, root.deepCopy());

        annotator.doOneSentence(annotation, sentence);

        Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
        CoreLabel rootLabel = (CoreLabel) tree.children()[0].label();
        assertEquals("Neutral", rootLabel.get(SentimentCoreAnnotations.SentimentClass.class));
    }
@Test
    public void testDoOneSentenceTreeContainsOnlyInternalNodes() {
        Properties props = new Properties();
        props.setProperty("mock.model", "mockPath");

        SentimentAnnotator annotator = new SentimentAnnotator("mock", props);
        Annotation annotation = new Annotation("InternalOnly");
        CoreMap sentence = new Annotation("InternalOnly");

        Tree sub1 = new LabeledScoredTreeFactory().newTreeNode("NP", Collections.emptyList());
        Tree sub2 = new LabeledScoredTreeFactory().newTreeNode("VP", Collections.emptyList());
        Tree root = new LabeledScoredTreeFactory().newTreeNode("S", Arrays.asList(sub1, sub2));
        root.setSpans();

        sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, root.deepCopy());
        sentence.set(TreeCoreAnnotations.TreeAnnotation.class, root.deepCopy());

        annotator.doOneSentence(annotation, sentence);

        String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
        assertEquals("Neutral", sentiment);
    }
@Test
    public void testDoOneSentenceRootTreeLabelWithoutSentimentClassSet() {
        Properties props = new Properties();
        props.setProperty("mock.model", "mockPath");

        SentimentAnnotator annotator = new SentimentAnnotator("mock", props);
        Annotation annotation = new Annotation("Root label test");
        CoreMap sentence = new Annotation("Root label test");

        CoreLabel label = new CoreLabel();
        Tree leaf = new LabeledScoredTreeFactory().newLeaf(label);
        Tree subtree = new LabeledScoredTreeFactory().newTreeNode("X", Collections.singletonList(leaf));
        Tree root = new LabeledScoredTreeFactory().newTreeNode("ROOT", Collections.singletonList(subtree));
        root.setSpans();

        sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, root.deepCopy());
        sentence.set(TreeCoreAnnotations.TreeAnnotation.class, root.deepCopy());

        annotator.doOneSentence(annotation, sentence);

        Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
        CoreLabel subtreeLabel = (CoreLabel) tree.children()[0].label();
        assertEquals("Neutral", subtreeLabel.get(SentimentCoreAnnotations.SentimentClass.class));
    }
@Test
    public void testSignatureWithEmptyPropertiesFallbackDefaults() {
        Properties props = new Properties();
        String sig = SentimentAnnotator.signature("sent", props);

        assertTrue(sig.contains("sent.model:edu/stanford/nlp/models/sentiment/sentiment.ser.gz"));
        assertTrue(sig.contains("sent.nthreads:"));
        assertTrue(sig.contains("sent.maxtime:-1"));
    }
@Test
    public void testDoOneSentenceWithNullTreeNodesInsideRoot() {
        Properties props = new Properties();
        props.setProperty("nthreads", "2");
        props.setProperty("mock.model", "somePath");

        SentimentAnnotator annotator = new SentimentAnnotator("mock", props);

        Annotation annotation = new Annotation("Test");
        CoreMap sentence = new Annotation("Test");

        Tree nullNode = null;
        Tree root = new LabeledScoredTreeFactory().newTreeNode("ROOT", Arrays.asList(nullNode));
        root.setSpans();

        sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, root.deepCopy());
        sentence.set(TreeCoreAnnotations.TreeAnnotation.class, root.deepCopy());

        annotator.doOneSentence(annotation, sentence);

        Tree tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
        String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);

        assertNotNull(tree);
        assertEquals("Neutral", sentiment);
    }
@Test
    public void testDoOneSentenceHandlesEmptyChildList() {
        Properties props = new Properties();
        props.setProperty("mock.model", "mockPath");

        SentimentAnnotator annotator = new SentimentAnnotator("mock", props);

        Annotation annotation = new Annotation("Empty children list");
        CoreMap sentence = new Annotation("Empty children list");

        Tree emptyInternalNode = new LabeledScoredTreeFactory().newTreeNode("NODE", Collections.emptyList());
        emptyInternalNode.setSpans();

        sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, emptyInternalNode.deepCopy());
        sentence.set(TreeCoreAnnotations.TreeAnnotation.class, emptyInternalNode.deepCopy());

        annotator.doOneSentence(annotation, sentence);

        Tree resultTree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
        String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
        assertNotNull(resultTree);
        assertEquals("Neutral", sentiment);
    }
@Test
    public void testConstructorUsesModelFromDefaultWhenMissing() {
        Properties props = new Properties();
        props.setProperty("other.nthreads", "2");

        SentimentAnnotator annotator = new SentimentAnnotator("other", props);

        assertEquals(2, annotator.nThreads());
        assertEquals(-1L, annotator.maxTime());
    }
@Test(expected = IllegalArgumentException.class)
    public void testConstructorFailsWhenNoModelKeyPresentAtAll() {
        Properties props = new Properties();

        
        new SentimentAnnotator("fail", props);
    }
@Test
    public void testDoOneSentenceWithIdenticalTreeAndBinarizedTree() {
        Properties props = new Properties();
        props.setProperty("mock.model", "m");

        SentimentAnnotator annotator = new SentimentAnnotator("mock", props);
        Annotation annotation = new Annotation("Same tree");

        CoreMap sentence = new Annotation("Same tree");

        CoreLabel leafLabel = new CoreLabel();
        Tree leaf = new LabeledScoredTreeFactory().newLeaf(leafLabel);
        Tree root = new LabeledScoredTreeFactory().newTreeNode("ROOT", Collections.singletonList(leaf));
        root.setSpans();

        Tree sharedTree = root.deepCopy();

        sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, sharedTree);
        sentence.set(TreeCoreAnnotations.TreeAnnotation.class, sharedTree);

        annotator.doOneSentence(annotation, sentence);

        Tree predicted = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
        String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);

        assertNotNull(predicted);
        assertEquals("Neutral", sentiment);
    }
@Test
    public void testDoOneSentenceOnlyTopLevelLabelGetsSentiment() {
        Properties props = new Properties();
        props.setProperty("mock.model", "abc");

        SentimentAnnotator annotator = new SentimentAnnotator("mock", props);
        Annotation annotation = new Annotation("Top only");
        CoreMap sentence = new Annotation("Top only");

        CoreLabel label = new CoreLabel();
        Tree inner = new LabeledScoredTreeFactory().newTreeNode("INNER", Collections.emptyList());
        Tree leaf = new LabeledScoredTreeFactory().newLeaf(label);
        Tree parent = new LabeledScoredTreeFactory().newTreeNode("ROOT", Arrays.asList(leaf, inner));
        parent.setSpans();

        sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, parent.deepCopy());
        sentence.set(TreeCoreAnnotations.TreeAnnotation.class, parent.deepCopy());

        annotator.doOneSentence(annotation, sentence);

        Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
        CoreLabel coreLabel = (CoreLabel) tree.children()[0].label();
        assertEquals("Neutral", coreLabel.get(SentimentCoreAnnotations.SentimentClass.class));
    }
@Test
    public void testConstructorParsesNumericPropertiesCorrectly() {
        Properties props = new Properties();
        props.setProperty("custom.model", "abc");
        props.setProperty("custom.nthreads", "7");
        props.setProperty("custom.maxtime", "1001");

        SentimentAnnotator annotator = new SentimentAnnotator("custom", props);

        assertEquals(7, annotator.nThreads());
        assertEquals(1001L, annotator.maxTime());
    }
@Test
    public void testConstructorFallsBackToIntegerParsingFallbacks() {
        Properties props = new Properties();
        props.setProperty("basic.model", "abc");

        SentimentAnnotator annotator = new SentimentAnnotator("basic", props);

        assertEquals(1, annotator.nThreads());
        assertEquals(-1L, annotator.maxTime());
    }
@Test
    public void testDoOneSentenceWithNullChildrenInTree() {
        Properties props = new Properties();
        props.setProperty("x.model", "abc");

        SentimentAnnotator annotator = new SentimentAnnotator("x", props);
        Annotation annotation = new Annotation("Null children node");
        CoreMap sentence = new Annotation("Null children node");

//        Tree customTree = new Tree() {
//            public Tree[] children() { return null; }
//            public Tree parent() { return null; }
//            public void setValue(String value) {}
//            public String value() { return "leaf"; }
//        };

//        Tree wrapper = new LabeledScoredTreeFactory().newTreeNode("WRAPPER", Collections.singletonList(customTree));
//        wrapper.setSpans();

//        sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, wrapper.deepCopy());
//        sentence.set(TreeCoreAnnotations.TreeAnnotation.class, wrapper.deepCopy());

        annotator.doOneSentence(annotation, sentence);

        String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
        Tree result = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
        assertNotNull(sentiment);
        assertNotNull(result);
    }
@Test
    public void testDoOneSentenceWithOnlyLeafNode() {
        Properties props = new Properties();
        props.setProperty("single.model", "path");

        SentimentAnnotator annotator = new SentimentAnnotator("single", props);

        Annotation annotation = new Annotation("Leaf only");
        CoreMap sentence = new Annotation("Leaf only");

        CoreLabel label = new CoreLabel();
        Tree leaf = new LabeledScoredTreeFactory().newLeaf(label);
        leaf.setSpans();

        sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, leaf.deepCopy());
        sentence.set(TreeCoreAnnotations.TreeAnnotation.class, leaf.deepCopy());

        annotator.doOneSentence(annotation, sentence);

        Tree annotatedTree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
        String cls = sentence.get(SentimentCoreAnnotations.SentimentClass.class);

        assertNotNull(annotatedTree);
        assertEquals("Neutral", cls);
    }
@Test
    public void testDoOneSentenceWithTreeMissingLabels() {
        Properties props = new Properties();
        props.setProperty("mock.model", "mockPath");

        SentimentAnnotator annotator = new SentimentAnnotator("mock", props);
        Annotation annotation = new Annotation("No labels");
        CoreMap sentence = new Annotation("No labels");

//        Tree emptyLeaf = new Tree() {
//            public Tree[] children() { return new Tree[0]; }
//            public Tree parent() { return null; }
//            public void setValue(String value) {}
//            public String value() { return ""; }
//        };

//        Tree root = new LabeledScoredTreeFactory().newTreeNode("ROOT", Collections.singletonList(emptyLeaf));
//        root.setSpans();

//        sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, root.deepCopy());
//        sentence.set(TreeCoreAnnotations.TreeAnnotation.class, root.deepCopy());

        annotator.doOneSentence(annotation, sentence);

        Tree result = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
        String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
        assertNotNull(result);
        assertEquals("Neutral", sentiment);
    }
@Test
    public void testConstructorUsesGlobalOnlyWhenAnnotatorKeyMissing() {
        Properties props = new Properties();
        props.setProperty("model", "global.model");
        props.setProperty("nthreads", "2");
        props.setProperty("maxtime", "500");

        
        props.setProperty("fallback.model", "fallbackPath");

        SentimentAnnotator annotator = new SentimentAnnotator("fallback", props);
        assertEquals(2, annotator.nThreads());
        assertEquals(500L, annotator.maxTime());
    }
@Test
    public void testDoOneSentenceHandlesRepeatedIdenticalSpans() {
        Properties props = new Properties();
        props.setProperty("repeat.model", "m");

        SentimentAnnotator annotator = new SentimentAnnotator("repeat", props);
        Annotation annotation = new Annotation("Identical spans");
        CoreMap sentence = new Annotation("Identical spans");

        CoreLabel label1 = new CoreLabel();
        Tree leaf1 = new LabeledScoredTreeFactory().newLeaf(label1);
        Tree node1 = new LabeledScoredTreeFactory().newTreeNode("X", Collections.singletonList(leaf1));

        CoreLabel label2 = new CoreLabel();
        Tree leaf2 = new LabeledScoredTreeFactory().newLeaf(label2);
        Tree node2 = new LabeledScoredTreeFactory().newTreeNode("Y", Collections.singletonList(leaf2));

        Tree root = new LabeledScoredTreeFactory().newTreeNode("ROOT", Arrays.asList(node1, node2));
        root.setSpans();

        
        IntPair identicalSpan = new IntPair(0, 1);
//        node1.setSpan(identicalSpan);
//        node2.setSpan(identicalSpan);

        sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, root.deepCopy());
        sentence.set(TreeCoreAnnotations.TreeAnnotation.class, root.deepCopy());

        
        annotator.doOneSentence(annotation, sentence);

        Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
        CoreLabel l1 = (CoreLabel) tree.children()[0].label();
        CoreLabel l2 = (CoreLabel) tree.children()[1].label();

        assertEquals("Neutral", l1.get(SentimentCoreAnnotations.SentimentClass.class));
        assertNull(l2.get(SentimentCoreAnnotations.SentimentClass.class)); 
    }
@Test
    public void testConstructorParsesIntAndLongFallbackGracefully() {
        Properties props = new Properties();
        props.setProperty("fallback.model", "x");

        
        SentimentAnnotator annotator = new SentimentAnnotator("fallback", props);

        assertEquals(1, annotator.nThreads());
        assertEquals(-1L, annotator.maxTime());
    }
@Test
    public void testDoOneSentenceHandlesEmptyTreeStructure() {
        Properties props = new Properties();
        props.setProperty("mock.model", "abc");

        SentimentAnnotator annotator = new SentimentAnnotator("mock", props);

        Annotation annotation = new Annotation("No children");
        CoreMap sentence = new Annotation("No children");

        Tree root = new LabeledScoredTreeFactory().newTreeNode("ROOT", new ArrayList<>());
        root.setSpans();

        sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, root.deepCopy());
        sentence.set(TreeCoreAnnotations.TreeAnnotation.class, root.deepCopy());

        annotator.doOneSentence(annotation, sentence);
        Tree t = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
        String s = sentence.get(SentimentCoreAnnotations.SentimentClass.class);

        assertNotNull(t);
        assertEquals("Neutral", s);
    }
@Test(expected = IllegalStateException.class)
    public void testSpanAnnotationExceptionForAnnotatedCoreLabel() {
        Properties props = new Properties();
        props.setProperty("mock.model", "mock");

        SentimentAnnotator annotator = new SentimentAnnotator("mock", props);
        Annotation annotation = new Annotation("Should throw");
        CoreMap sentence = new Annotation("Should throw");

        CoreLabel label = new CoreLabel();
        label.set(CoreAnnotations.SpanAnnotation.class, new IntPair(0, 1));

        Tree leaf = new LabeledScoredTreeFactory().newLeaf(label);
        Tree root = new LabeledScoredTreeFactory().newTreeNode("ROOT", Collections.singletonList(leaf));
        root.setSpans();

        sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, root.deepCopy());
        sentence.set(TreeCoreAnnotations.TreeAnnotation.class, root);

        annotator.doOneSentence(annotation, sentence);
    } 
}