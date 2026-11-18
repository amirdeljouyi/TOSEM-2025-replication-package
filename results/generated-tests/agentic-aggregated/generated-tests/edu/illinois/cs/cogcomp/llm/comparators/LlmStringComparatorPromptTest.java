package edu.illinois.cs.cogcomp.llm.comparators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import edu.illinois.cs.cogcomp.annotation.TextAnnotationBuilder;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.SpanLabelView;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager;
import edu.illinois.cs.cogcomp.mrcs.comparators.Comparator;
import edu.illinois.cs.cogcomp.mrcs.dataStructures.Alignment;
import edu.illinois.cs.cogcomp.mrcs.dataStructures.EntailmentResult;
import edu.illinois.cs.cogcomp.nlp.tokenizer.IllinoisTokenizer;
import edu.illinois.cs.cogcomp.nlp.utility.TokenizerTextAnnotationBuilder;

public class LlmStringComparatorPromptTest {

    @Test
    public void identicalArraysProducePerfectEntailment() throws Exception {
        Comparator<String, EntailmentResult> exact = (left, right) -> new EntailmentResult(
                "exact", left.equals(right) ? 1.0f : 0.0f, "test", true, true, true,
                Collections.emptyMap());
        LlmStringComparator subject = new LlmStringComparator(
                new ResourceManager(new Properties()), exact);

        EntailmentResult result = subject.determineEntailment(
                new String[] { "bright", "moon" }, new String[] { "bright", "moon" });

        assertEquals(1.0f, result.getScore(), 0.0f);
        assertTrue(result.getDecision());
        assertTrue(result.isPolarityPositive());
        assertTrue(result.isUpwardMonotone());
    }

    @Test
    public void unmatchedHypothesisTokenReducesAverageScore() throws Exception {
        Comparator<String, EntailmentResult> exact = (left, right) -> new EntailmentResult(
                "exact", left.equals(right) ? 1.0f : 0.0f, null, true, true, true, null);
        LlmStringComparator subject = new LlmStringComparator(
                new ResourceManager(new Properties()), exact);

        EntailmentResult result = subject.determineEntailment(
                new String[] { "red" }, new String[] { "red", "blue" });

        assertEquals(0.5f, result.getScore(), 0.00001f);
        assertFalse(result.getDecision());
    }

    @Test
    public void configuredThresholdControlsDecision() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("llmThreshold", "0.75");
        Comparator<String, EntailmentResult> constant = (left, right) -> new EntailmentResult(
                "constant", 0.7f, null, true, true, true, null);
        LlmStringComparator subject = new LlmStringComparator(
                new ResourceManager(properties), constant);

        EntailmentResult result = subject.determineEntailment(
                new String[] { "source" }, new String[] { "target" });

        assertEquals(0.7f, result.getScore(), 0.00001f);
        assertFalse(result.getDecision());
    }

    @Test
    public void scoreEqualToThresholdDoesNotEntail() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("llmThreshold", "0.7");
        Comparator<String, EntailmentResult> constant = (left, right) -> new EntailmentResult(
                "constant", 0.7f, null, true, true, true, null);
        LlmStringComparator subject = new LlmStringComparator(
                new ResourceManager(properties), constant);

        EntailmentResult result = subject.determineEntailment(
                new String[] { "one" }, new String[] { "two" });

        assertFalse(result.getDecision());
    }

    @Test
    public void bestTextCandidateIsUsedForEachHypothesisToken() throws Exception {
        Comparator<String, EntailmentResult> graded = (left, right) -> new EntailmentResult(
                "graded", right.equals("best") ? 0.9f : 0.2f, null, true, true, true, null);
        LlmStringComparator subject = new LlmStringComparator(
                new ResourceManager(new Properties()), graded);

        EntailmentResult result = subject.determineEntailment(
                new String[] { "weak", "best" }, new String[] { "hypothesis" });

        assertEquals(0.9f, result.getScore(), 0.00001f);
        assertTrue(result.getDecision());
    }

    @Test
    public void nonPositiveAndNullComparisonResultsCreateNoEdges() throws Exception {
        Comparator<String, EntailmentResult> sparse = (left, right) -> {
            if (right.equals("null-result")) {
                return null;
            }
            return new EntailmentResult("non-positive", -0.4f, null, false, true, true, null);
        };
        LlmStringComparator subject = new LlmStringComparator(
                new ResourceManager(new Properties()), sparse);

        Alignment<String> alignment = subject.alignStringArrays(
                new String[] { "null-result", "negative" }, new String[] { "query" });
        EntailmentResult result = subject.scoreAlignment(alignment);

        assertTrue(alignment.getIndexMap().get(0).isEmpty());
        assertEquals(0.0f, result.getScore(), 0.0f);
        assertFalse(result.getDecision());
    }

    @Test
    public void stopwordsAreExcludedFromEffectiveHypothesisSize() throws Exception {
        Comparator<String, EntailmentResult> exact = (left, right) -> new EntailmentResult(
                "exact", left.equals(right) ? 1.0f : 0.0f, null, true, true, true, null);
        LlmStringComparator subject = new LlmStringComparator(
                new ResourceManager(new Properties()), exact);

        Alignment<String> alignment = subject.alignStringArrays(
                new String[] { "cat" }, new String[] { "the", "cat" });
        EntailmentResult result = subject.scoreAlignment(alignment);

        assertEquals(1, alignment.getEffectiveHypSize());
        assertEquals(1.0f, result.getScore(), 0.0f);
    }

    @Test
    public void stopwordFilteringIsCaseInsensitive() throws Exception {
        Comparator<String, EntailmentResult> exact = (left, right) -> new EntailmentResult(
                "exact", left.equals(right) ? 1.0f : 0.0f, null, true, true, true, null);
        LlmStringComparator subject = new LlmStringComparator(
                new ResourceManager(new Properties()), exact);

        Alignment<String> alignment = subject.alignStringArrays(
                new String[] { "bird" }, new String[] { "THE", "bird" });

        assertEquals(1, alignment.getEffectiveHypSize());
        assertTrue(alignment.getIndexMap().get(0).isEmpty());
        assertEquals(1, alignment.getIndexMap().get(1).size());
    }

    @Test
    public void alignerCallsComparatorForEveryUnfilteredPair() throws Exception {
        AtomicInteger calls = new AtomicInteger();
        Comparator<String, EntailmentResult> counting = (left, right) -> {
            calls.incrementAndGet();
            return new EntailmentResult("count", 0.1f, null, false, true, true, null);
        };
        LlmStringComparator subject = new LlmStringComparator(
                new ResourceManager(new Properties()), counting);

        Alignment<String> alignment = subject.alignStringArrays(
                new String[] { "alpha", "beta" }, new String[] { "gamma", "delta", "epsilon" });

        assertNotNull(alignment);
        assertEquals(6, calls.get());
        assertEquals(3, alignment.getIndexMap().size());
    }

    @Test
    public void comparatorExceptionIsPropagated() throws Exception {
        Exception expected = new Exception("deliberate comparator failure");
        Comparator<String, EntailmentResult> failing = (left, right) -> {
            throw expected;
        };
        LlmStringComparator subject = new LlmStringComparator(
                new ResourceManager(new Properties()), failing);

        try {
            subject.alignStringArrays(new String[] { "xylophone" }, new String[] { "quasar" });
            fail("Expected comparator exception");
        } catch (Exception actual) {
            assertSame(expected, actual);
        }
    }

    @Test
    public void sentenceOverloadTokenizesPunctuationConsistently() throws Exception {
        Comparator<String, EntailmentResult> exact = (left, right) -> new EntailmentResult(
                "exact", left.equals(right) ? 1.0f : 0.0f, null, true, true, true, null);
        LlmStringComparator subject = new LlmStringComparator(
                new ResourceManager(new Properties()), exact);

        Alignment<String> alignment = subject.alignSentences("Cats chase mice.", "Cats chase mice.");
        EntailmentResult fromAlignment = subject.scoreAlignment(alignment);
        EntailmentResult direct = subject.determineEntailment("Cats chase mice.", "Cats chase mice.");

        assertEquals(1.0f, fromAlignment.getScore(), 0.0f);
        assertEquals(fromAlignment.getScore(), direct.getScore(), 0.0f);
        assertEquals(4, alignment.getIndexMap().size());
    }

    @Test
    public void compareStringsReturnsDetermineEntailmentScore() throws Exception {
        Comparator<String, EntailmentResult> exact = (left, right) -> new EntailmentResult(
                "exact", left.equals(right) ? 1.0f : 0.0f, null, true, true, true, null);
        LlmStringComparator subject = new LlmStringComparator(
                new ResourceManager(new Properties()), exact);

        double scalar = subject.compareStrings_("small green tree", "green tree");
        float resultScore = subject.determineEntailment("small green tree", "green tree").getScore();

        assertEquals(resultScore, scalar, 0.0);
        assertEquals(1.0, scalar, 0.0);
    }

    @Test
    public void manuallyBuiltAlignmentUsesHighestEdgePerHypothesis() throws Exception {
        Comparator<String, EntailmentResult> unused = (left, right) -> new EntailmentResult(
                "unused", 0.0f, null, false, true, true, null);
        LlmStringComparator subject = new LlmStringComparator(
                new ResourceManager(new Properties()), unused);
        Alignment<String> alignment = new Alignment<>(
                new String[] { "t1", "t2" }, new String[] { "h1", "h2" });
        alignment.addEdge("h1", "t1", "low", 0.2f);
        alignment.addEdge("h1", "t2", "high", 0.8f);
        alignment.addEdge("h2", "t1", "only", 0.4f);

        EntailmentResult result = subject.scoreAlignment(alignment);

        assertEquals(0.6f, result.getScore(), 0.00001f);
        assertTrue(result.getDecision());
    }

    @Test
    public void allFilteredHypothesisProducesUndefinedScore() throws Exception {
        Comparator<String, EntailmentResult> exact = (left, right) -> new EntailmentResult(
                "exact", left.equals(right) ? 1.0f : 0.0f, null, true, true, true, null);
        LlmStringComparator subject = new LlmStringComparator(
                new ResourceManager(new Properties()), exact);

        EntailmentResult result = subject.determineEntailment(
                new String[] { "content" }, new String[] { "the", "and" });

        assertTrue(Float.isNaN(result.getScore()));
        assertFalse(result.getDecision());
    }

    @Test
    public void emptyHypothesisProducesUndefinedScoreWithoutCallingComparator() throws Exception {
        AtomicInteger calls = new AtomicInteger();
        Comparator<String, EntailmentResult> counting = (left, right) -> {
            calls.incrementAndGet();
            return new EntailmentResult("count", 1.0f, null, true, true, true, null);
        };
        LlmStringComparator subject = new LlmStringComparator(
                new ResourceManager(new Properties()), counting);

        EntailmentResult result = subject.determineEntailment(
                new String[] { "content" }, new String[0]);

        assertEquals(0, calls.get());
        assertTrue(Float.isNaN(result.getScore()));
        assertFalse(result.getDecision());
    }

    @Test
    public void namedEntityAlignmentUsesItsDedicatedComparator() throws Exception {
        Comparator<String, EntailmentResult> alwaysZero = (left, right) -> new EntailmentResult(
                "injected", 0.0f, null, false, true, true, null);
        LlmStringComparator subject = new LlmStringComparator(
                new ResourceManager(new Properties()), alwaysZero);

        Alignment<String> lexical = subject.alignStringArrays(
                new String[] { "Paris" }, new String[] { "Paris" });
        Alignment<String> namedEntity = subject.alignNEStringArrays(
                new String[] { "Paris" }, new String[] { "Paris" });

        assertTrue(lexical.getIndexMap().get(0).isEmpty());
        assertFalse(namedEntity.getIndexMap().get(0).isEmpty());
        assertEquals(1.0f, subject.scoreAlignment(namedEntity).getScore(), 0.0f);
    }

    @Test
    public void compareAnnotationWithoutEntitiesUsesSentenceAlignment() throws Exception {
        Comparator<String, EntailmentResult> exact = (left, right) -> new EntailmentResult(
                "exact", left.equals(right) ? 1.0f : 0.0f, null, true, true, true, null);
        LlmStringComparator subject = new LlmStringComparator(
                new ResourceManager(new Properties()), exact);
        TextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new IllinoisTokenizer());
        TextAnnotation source = builder.createTextAnnotation("corpus", "source", "Alice runs.");
        TextAnnotation target = builder.createTextAnnotation("corpus", "target", "Alice runs.");
        source.addView("NER_CONLL", new SpanLabelView("NER_CONLL", source));
        target.addView("NER_CONLL", new SpanLabelView("NER_CONLL", target));

        double score = subject.compareAnnotation(source, target);

        assertEquals(1.0, score, 0.0);
    }

    @Test
    public void compareAnnotationCombinesEntityAndRemainingTokenScores() throws Exception {
        Comparator<String, EntailmentResult> exact = (left, right) -> new EntailmentResult(
                "exact", left.equals(right) ? 1.0f : 0.0f, null, true, true, true, null);
        LlmStringComparator subject = new LlmStringComparator(
                new ResourceManager(new Properties()), exact);
        TextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new IllinoisTokenizer());
        TextAnnotation source = builder.createTextAnnotation("corpus", "source", "Alice runs.");
        TextAnnotation target = builder.createTextAnnotation("corpus", "target", "Alice runs.");
        SpanLabelView sourceEntities = new SpanLabelView("NER_CONLL", source);
        SpanLabelView targetEntities = new SpanLabelView("NER_CONLL", target);
        sourceEntities.addSpanLabel(0, 1, "PER", 1.0);
        targetEntities.addSpanLabel(0, 1, "PER", 1.0);
        source.addView("NER_CONLL", sourceEntities);
        target.addView("NER_CONLL", targetEntities);

        double score = subject.compareAnnotation(source, target);

        assertEquals(1.0, score, 0.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void malformedThresholdIsRejectedByConstructor() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("llmThreshold", "not-a-number");
        Comparator<String, EntailmentResult> exact = (left, right) -> new EntailmentResult(
                "exact", 1.0f, null, true, true, true, null);

        new LlmStringComparator(new ResourceManager(properties), exact);
    }
}
