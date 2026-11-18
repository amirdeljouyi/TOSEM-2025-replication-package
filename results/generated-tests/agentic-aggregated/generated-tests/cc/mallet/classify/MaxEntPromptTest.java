package cc.mallet.classify;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.BitSet;

import org.junit.Test;

import cc.mallet.pipe.Noop;
import cc.mallet.pipe.Pipe;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureSelection;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.LabelAlphabet;
import cc.mallet.types.LabelVector;

public class MaxEntPromptTest {

    @Test
    public void constructorWithNullParametersCreatesZeroFilledModel() {
        Alphabet features = new Alphabet();
        features.lookupIndex("red");
        features.lookupIndex("blue");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupIndex("yes");
        labels.lookupIndex("no");
        labels.lookupIndex("maybe");
        Pipe pipe = new Noop(features, labels);

        MaxEnt classifier = new MaxEnt(pipe, (double[]) null);

        assertEquals(9, classifier.getNumParameters());
        assertEquals(9, MaxEnt.getNumParameters(pipe));
        assertArrayEquals(new double[9], classifier.getParameters(), 0.0);
        assertEquals(2, classifier.getDefaultFeatureIndex());
        assertNull(classifier.getFeatureSelection());
        assertNull(classifier.getPerClassFeatureSelection());
        assertSame(pipe, classifier.getInstancePipe());
        assertSame(features, classifier.getAlphabet());
        assertSame(labels, classifier.getLabelAlphabet());
    }

    @Test
    public void parameterArrayIsRetainedAndCanBeReplaced() {
        Alphabet features = new Alphabet();
        features.lookupIndex("token");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupIndex("positive");
        labels.lookupIndex("negative");
        MaxEnt classifier = new MaxEnt(new Noop(features, labels), new double[] { 1.0, 2.0, 3.0, 4.0 });

        double[] original = classifier.getParameters();
        original[0] = 7.5;
        assertEquals(7.5, classifier.getParameters()[0], 0.0);

        double[] replacement = new double[] { -1.0, -2.0, -3.0, -4.0 };
        classifier.setParameters(replacement);
        assertSame(replacement, classifier.getParameters());
        assertArrayEquals(new double[] { -1.0, -2.0, -3.0, -4.0 }, classifier.getParameters(), 0.0);
    }

    @Test
    public void setParameterUsesLabelMajorLayoutIncludingDefaultFeature() {
        Alphabet features = new Alphabet();
        features.lookupIndex("left");
        features.lookupIndex("right");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupIndex("A");
        labels.lookupIndex("B");
        MaxEnt classifier = new MaxEnt(new Noop(features, labels), null);

        classifier.setParameter(0, 0, 1.25);
        classifier.setParameter(0, 2, -0.5);
        classifier.setParameter(1, 1, 3.0);
        classifier.setParameter(1, 2, 0.75);

        assertArrayEquals(new double[] { 1.25, 0.0, -0.5, 0.0, 3.0, 0.75 },
                classifier.getParameters(), 0.0);
    }

    @Test
    public void unnormalizedScoresCombineSparseValuesAndBiases() {
        Alphabet features = new Alphabet();
        int f0 = features.lookupIndex("length");
        features.lookupIndex("unused");
        int f2 = features.lookupIndex("frequency");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupIndex("sports");
        labels.lookupIndex("science");
        MaxEnt classifier = new MaxEnt(new Noop(features, labels),
                new double[] { 2.0, 100.0, -1.0, 0.5, -3.0, 100.0, 4.0, -2.0 });
        FeatureVector vector = new FeatureVector(features, new int[] { f0, f2 }, new double[] { 1.5, 2.0 });
        Instance instance = new Instance(vector, null, "document-1", "raw text");
        double[] scores = new double[2];

        classifier.getUnnormalizedClassificationScores(instance, scores);

        assertArrayEquals(new double[] { 1.5, 1.5 }, scores, 1.0e-12);
    }

    @Test
    public void unnormalizedScoresTreatBinaryFeaturesAsOne() {
        Alphabet features = new Alphabet();
        int first = features.lookupIndex("first");
        features.lookupIndex("middle");
        int last = features.lookupIndex("last");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupIndex("only");
        MaxEnt classifier = new MaxEnt(new Noop(features, labels), new double[] { 1.25, 99.0, -0.5, 2.0 });
        Instance instance = new Instance(new FeatureVector(features, new int[] { first, last }), null, null, null);
        double[] scores = new double[1];

        classifier.getUnnormalizedClassificationScores(instance, scores);

        assertArrayEquals(new double[] { 2.75 }, scores, 1.0e-12);
    }

    @Test
    public void normalizedScoresUseStableSoftmaxForVeryLargeWeights() {
        Alphabet features = new Alphabet();
        int feature = features.lookupIndex("signal");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupIndex("high");
        labels.lookupIndex("low");
        labels.lookupIndex("mid");
        MaxEnt classifier = new MaxEnt(new Noop(features, labels),
                new double[] { 1000.0, 1000.0, 997.0, 1000.0, 999.0, 1000.0 });
        Instance instance = new Instance(new FeatureVector(features, new int[] { feature }), null, null, null);
        double[] scores = new double[3];

        classifier.getClassificationScores(instance, scores);

        double denominator = 1.0 + Math.exp(-3.0) + Math.exp(-1.0);
        assertEquals(1.0 / denominator, scores[0], 1.0e-12);
        assertEquals(Math.exp(-3.0) / denominator, scores[1], 1.0e-12);
        assertEquals(Math.exp(-1.0) / denominator, scores[2], 1.0e-12);
        assertEquals(1.0, scores[0] + scores[1] + scores[2], 1.0e-12);
    }

    @Test
    public void equalRawScoresProduceUniformProbabilities() {
        Alphabet features = new Alphabet();
        features.lookupIndex("ignored-by-zero-weights");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupIndex("one");
        labels.lookupIndex("two");
        labels.lookupIndex("three");
        MaxEnt classifier = new MaxEnt(new Noop(features, labels), null);
        Instance instance = new Instance(new FeatureVector(features, new int[0]), null, null, null);
        double[] scores = new double[3];

        classifier.getClassificationScores(instance, scores);

        assertArrayEquals(new double[] { 1.0 / 3.0, 1.0 / 3.0, 1.0 / 3.0 }, scores, 1.0e-12);
    }

    @Test
    public void globalFeatureSelectionExcludesUnselectedActiveFeatures() {
        Alphabet features = new Alphabet();
        int selected = features.lookupIndex("selected");
        int excluded = features.lookupIndex("excluded");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupIndex("L0");
        labels.lookupIndex("L1");
        FeatureSelection selection = new FeatureSelection(features);
        selection.add(selected);
        MaxEnt classifier = new MaxEnt(new Noop(features, labels),
                new double[] { 2.0, 50.0, 1.0, -1.0, 70.0, 3.0 }, selection);
        Instance instance = new Instance(
                new FeatureVector(features, new int[] { selected, excluded }, new double[] { 4.0, 10.0 }),
                null, null, null);
        double[] scores = new double[2];

        classifier.getUnnormalizedClassificationScores(instance, scores);

        assertArrayEquals(new double[] { 9.0, -1.0 }, scores, 1.0e-12);
        assertSame(selection, classifier.getFeatureSelection());
        assertNull(classifier.getPerClassFeatureSelection());
    }

    @Test
    public void perClassFeatureSelectionsApplyDifferentMasks() {
        Alphabet features = new Alphabet();
        int first = features.lookupIndex("first");
        int second = features.lookupIndex("second");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupIndex("L0");
        labels.lookupIndex("L1");
        FeatureSelection forFirstClass = new FeatureSelection(features);
        forFirstClass.add(first);
        FeatureSelection forSecondClass = new FeatureSelection(features);
        forSecondClass.add(second);
        FeatureSelection[] selections = new FeatureSelection[] { forFirstClass, forSecondClass };
        MaxEnt classifier = new MaxEnt(new Noop(features, labels),
                new double[] { 2.0, 20.0, 0.5, 30.0, -4.0, -1.0 }, selections);
        Instance instance = new Instance(
                new FeatureVector(features, new int[] { first, second }, new double[] { 3.0, 5.0 }),
                null, null, null);
        double[] scores = new double[2];

        classifier.getUnnormalizedClassificationScores(instance, scores);

        assertArrayEquals(new double[] { 6.5, -21.0 }, scores, 1.0e-12);
        assertSame(selections, classifier.getPerClassFeatureSelection());
        assertNull(classifier.getFeatureSelection());
    }

    @Test
    public void featureSelectionSettersAreFluentAndImmediatelyAffectScoring() {
        Alphabet features = new Alphabet();
        int first = features.lookupIndex("first");
        int second = features.lookupIndex("second");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupIndex("label");
        MaxEnt classifier = new MaxEnt(new Noop(features, labels), new double[] { 2.0, 3.0, 1.0 });
        FeatureSelection global = new FeatureSelection(features);
        global.add(second);
        FeatureSelection perClass = new FeatureSelection(features);
        perClass.add(first);
        Instance instance = new Instance(new FeatureVector(features, new int[] { first, second }), null, null, null);
        double[] globalScore = new double[1];
        double[] perClassScore = new double[1];

        assertSame(classifier, classifier.setFeatureSelection(global));
        classifier.getUnnormalizedClassificationScores(instance, globalScore);
        assertSame(classifier, classifier.setPerClassFeatureSelection(new FeatureSelection[] { perClass }));
        classifier.getUnnormalizedClassificationScores(instance, perClassScore);

        assertArrayEquals(new double[] { 4.0 }, globalScore, 1.0e-12);
        assertArrayEquals(new double[] { 3.0 }, perClassScore, 1.0e-12);
    }

    @Test
    public void temperatureAboveOneFlattensDistribution() {
        Alphabet features = new Alphabet();
        int signal = features.lookupIndex("signal");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupIndex("preferred");
        labels.lookupIndex("other");
        MaxEnt classifier = new MaxEnt(new Noop(features, labels), new double[] { 4.0, 0.0, 0.0, 0.0 });
        Instance instance = new Instance(new FeatureVector(features, new int[] { signal }), null, null, null);
        double[] ordinary = new double[2];
        double[] warmed = new double[2];

        classifier.getClassificationScores(instance, ordinary);
        classifier.getClassificationScoresWithTemperature(instance, 2.0, warmed);

        assertEquals(1.0 / (1.0 + Math.exp(-4.0)), ordinary[0], 1.0e-12);
        assertEquals(1.0 / (1.0 + Math.exp(-2.0)), warmed[0], 1.0e-12);
        assertTrue(warmed[0] < ordinary[0]);
        assertEquals(1.0, warmed[0] + warmed[1], 1.0e-12);
    }

    @Test
    public void negativeTemperatureReversesClassPreference() {
        Alphabet features = new Alphabet();
        int signal = features.lookupIndex("signal");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupIndex("positive");
        labels.lookupIndex("negative");
        MaxEnt classifier = new MaxEnt(new Noop(features, labels), new double[] { 2.0, 0.0, 0.0, 0.0 });
        Instance instance = new Instance(new FeatureVector(features, new int[] { signal }), null, null, null);
        double[] scores = new double[2];

        classifier.getClassificationScoresWithTemperature(instance, -1.0, scores);

        assertTrue(scores[0] < scores[1]);
        assertEquals(1.0 / (1.0 + Math.exp(2.0)), scores[0], 1.0e-12);
        assertEquals(1.0, scores[0] + scores[1], 1.0e-12);
    }

    @Test
    public void classifyBuildsClassificationWithOriginalObjectsAndBestLabel() {
        Alphabet features = new Alphabet();
        int signal = features.lookupIndex("signal");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupIndex("cat");
        labels.lookupIndex("dog");
        MaxEnt classifier = new MaxEnt(new Noop(features, labels), new double[] { -1.0, 0.0, 2.0, 0.0 });
        LabelVector target = new LabelVector(labels, new double[] { 0.0, 1.0 });
        Instance instance = new Instance(new FeatureVector(features, new int[] { signal }), target, "pet", "source");

        Classification result = classifier.classify(instance);

        assertSame(instance, result.getInstance());
        assertSame(classifier, result.getClassifier());
        assertEquals("dog", result.getLabeling().getBestLabel().toString());
        assertTrue(result.bestLabelIsCorrect());
        assertEquals(1.0 / (1.0 + Math.exp(-3.0)), result.getLabeling().getBestValue(), 1.0e-12);
    }

    @Test
    public void printWriterListsEveryClassFeatureAndDefaultWeight() {
        Alphabet features = new Alphabet();
        features.lookupIndex("word");
        features.lookupIndex("shape");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupIndex("spam");
        labels.lookupIndex("ham");
        MaxEnt classifier = new MaxEnt(new Noop(features, labels),
                new double[] { 1.0, -2.0, 0.25, 3.5, 4.0, -0.75 });
        StringWriter buffer = new StringWriter();
        PrintWriter writer = new PrintWriter(buffer);

        classifier.print(writer);
        writer.flush();

        String output = buffer.toString();
        assertTrue(output.contains("FEATURES FOR CLASS spam"));
        assertTrue(output.contains(" <default> 0.25"));
        assertTrue(output.contains(" word 1.0"));
        assertTrue(output.contains(" shape -2.0"));
        assertTrue(output.contains("FEATURES FOR CLASS ham"));
        assertTrue(output.contains(" <default> -0.75"));
        assertTrue(output.contains(" word 3.5"));
        assertTrue(output.contains(" shape 4.0"));
    }

    @Test
    public void printStreamOverloadLeavesItsInternalWriterUnflushed() throws Exception {
        Alphabet features = new Alphabet();
        features.lookupIndex("token");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupIndex("class-A");
        MaxEnt classifier = new MaxEnt(new Noop(features, labels), new double[] { 6.0, -1.5 });
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream stream = new PrintStream(bytes, true, "UTF-8");

        classifier.print(stream);
        stream.flush();

        assertEquals("", bytes.toString("UTF-8"));
    }

    @Test
    public void rankedAndExtremePrintingExposeOrderingAndBias() {
        Alphabet features = new Alphabet();
        features.lookupIndex("weak");
        features.lookupIndex("strong");
        features.lookupIndex("negative");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupIndex("topic");
        MaxEnt classifier = new MaxEnt(new Noop(features, labels), new double[] { 0.5, 7.0, -3.0, 1.25 });
        StringWriter rankedBuffer = new StringWriter();
        PrintWriter rankedWriter = new PrintWriter(rankedBuffer);
        StringWriter extremeBuffer = new StringWriter();
        PrintWriter extremeWriter = new PrintWriter(extremeBuffer);

        classifier.printRank(rankedWriter);
        classifier.printExtremeFeatures(extremeWriter, 1);
        rankedWriter.flush();
        extremeWriter.flush();

        String ranked = rankedBuffer.toString();
        String extreme = extremeBuffer.toString();
        assertTrue(ranked.contains("FEATURES FOR CLASS topic"));
        assertTrue(ranked.contains("strong"));
        assertTrue(ranked.contains("weak"));
        assertTrue(ranked.contains("negative"));
        assertTrue(ranked.contains("<default> 1.25"));
        assertTrue(ranked.indexOf("strong") < ranked.indexOf("weak"));
        assertTrue(ranked.indexOf("weak") < ranked.indexOf("negative"));
        assertTrue(extreme.contains("strong"));
        assertTrue(extreme.contains("negative"));
        assertTrue(extreme.contains("<default> 1.25"));
    }

    @Test
    public void serializationRoundTripPreservesScoringAndSelections() throws Exception {
        Alphabet features = new Alphabet();
        int first = features.lookupIndex("first");
        int second = features.lookupIndex("second");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupIndex("up");
        labels.lookupIndex("down");
        FeatureSelection firstOnly = new FeatureSelection(features);
        firstOnly.add(first);
        FeatureSelection secondOnly = new FeatureSelection(features);
        secondOnly.add(second);
        MaxEnt original = new MaxEnt(new Noop(features, labels),
                new double[] { 2.0, 20.0, 0.5, 30.0, -4.0, -1.0 },
                new FeatureSelection[] { firstOnly, secondOnly });
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ObjectOutputStream output = new ObjectOutputStream(bytes);
        output.writeObject(original);
        output.close();

        ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()));
        MaxEnt restored = (MaxEnt) input.readObject();
        input.close();
        Alphabet restoredFeatures = restored.getAlphabet();
        Instance instance = new Instance(
                new FeatureVector(restoredFeatures, new int[] { 0, 1 }, new double[] { 3.0, 5.0 }),
                null, null, null);
        double[] scores = new double[2];
        restored.getUnnormalizedClassificationScores(instance, scores);

        assertNotSame(original, restored);
        assertArrayEquals(original.getParameters(), restored.getParameters(), 0.0);
        assertEquals(2, restored.getDefaultFeatureIndex());
        assertNull(restored.getFeatureSelection());
        assertEquals(2, restored.getPerClassFeatureSelection().length);
        assertTrue(restored.getPerClassFeatureSelection()[0].contains(0));
        assertTrue(restored.getPerClassFeatureSelection()[1].contains(1));
        assertArrayEquals(new double[] { 6.5, -21.0 }, scores, 1.0e-12);
    }

    @Test(expected = ClassCastException.class)
    public void scoringRejectsNonFeatureVectorData() {
        Alphabet features = new Alphabet();
        features.lookupIndex("feature");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupIndex("label");
        MaxEnt classifier = new MaxEnt(new Noop(features, labels), null);
        Instance malformed = new Instance("not a feature vector", null, null, null);

        classifier.getClassificationScores(malformed, new double[1]);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void scoringFailsWhenParameterArrayIsTooShort() {
        Alphabet features = new Alphabet();
        int feature = features.lookupIndex("feature");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupIndex("first");
        labels.lookupIndex("second");
        MaxEnt classifier = new MaxEnt(new Noop(features, labels), new double[] { 1.0 });
        Instance instance = new Instance(new FeatureVector(features, new int[] { feature }), null, null, null);

        classifier.getUnnormalizedClassificationScores(instance, new double[2]);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void setParameterRejectsOutOfRangeClassIndex() {
        Alphabet features = new Alphabet();
        features.lookupIndex("feature");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupIndex("only");
        MaxEnt classifier = new MaxEnt(new Noop(features, labels), null);

        classifier.setParameter(1, 0, 2.0);
    }

    @Test
    public void defaultFeatureIndexCanBeReconfiguredAndUsedForScoring() {
        Alphabet features = new Alphabet();
        int first = features.lookupIndex("first");
        features.lookupIndex("second");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupIndex("label");
        MaxEnt classifier = new MaxEnt(new Noop(features, labels), new double[] { 2.0, 5.0, 11.0 });
        classifier.setDefaultFeatureIndex(1);
        Instance instance = new Instance(new FeatureVector(features, new int[] { first }), null, null, null);
        double[] score = new double[1];

        classifier.getUnnormalizedClassificationScores(instance, score);

        assertEquals(1, classifier.getDefaultFeatureIndex());
        assertArrayEquals(new double[] { 7.0 }, score, 1.0e-12);
    }

    @Test
    public void bitSetBasedFeatureSelectionConstructorFiltersScores() {
        Alphabet features = new Alphabet();
        int first = features.lookupIndex("first");
        int second = features.lookupIndex("second");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupIndex("label");
        BitSet selected = new BitSet();
        selected.set(second);
        FeatureSelection selection = new FeatureSelection(features, selected);
        MaxEnt classifier = new MaxEnt(new Noop(features, labels), new double[] { 10.0, 3.0, -2.0 }, selection);
        Instance instance = new Instance(new FeatureVector(features, new int[] { first, second }), null, null, null);
        double[] score = new double[1];

        classifier.getUnnormalizedClassificationScores(instance, score);

        assertEquals(1, selection.cardinality());
        assertArrayEquals(new double[] { 1.0 }, score, 1.0e-12);
    }
}
