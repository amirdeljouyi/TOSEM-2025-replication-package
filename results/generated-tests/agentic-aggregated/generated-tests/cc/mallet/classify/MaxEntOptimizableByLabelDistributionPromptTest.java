package cc.mallet.classify;

import static org.junit.Assert.*;

import org.junit.Test;

import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureSelection;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.LabelAlphabet;
import cc.mallet.types.LabelVector;

public class MaxEntOptimizableByLabelDistributionPromptTest {

    @Test
    public void constructorCreatesZeroInitializedClassifierAndStopsLabelGrowth() {
        Alphabet features = new Alphabet();
        features.lookupIndex("word");
        features.lookupIndex("shape");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupIndex("positive");
        labels.lookupIndex("negative");
        InstanceList training = new InstanceList(features, labels);
        training.add(new Instance(new FeatureVector(features, new double[] { 1.0, 2.0 }),
                new LabelVector(labels, new double[] { 0.6, 0.4 }), "first", "source"));

        MaxEntOptimizableByLabelDistribution subject =
                new MaxEntOptimizableByLabelDistribution(training, null);

        assertNotNull(subject.getClassifier());
        assertSame(training.getPipe(), subject.getClassifier().getInstancePipe());
        assertEquals(6, subject.getNumParameters());
        assertEquals(0.0, subject.getParameter(0), 0.0);
        assertEquals(0.0, subject.getParameter(5), 0.0);
        assertEquals(-1, labels.lookupIndex("unknown-label"));
    }

    @Test
    public void uniformModelComputesSoftLabelLogLikelihoodAndGradient() {
        Alphabet features = new Alphabet();
        features.lookupIndex("x");
        features.lookupIndex("y");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupIndex("A");
        labels.lookupIndex("B");
        InstanceList training = new InstanceList(features, labels);
        training.add(new Instance(new FeatureVector(features, new double[] { 2.0, 1.0 }),
                new LabelVector(labels, new double[] { 0.75, 0.25 }), "soft", null));
        MaxEntOptimizableByLabelDistribution subject =
                new MaxEntOptimizableByLabelDistribution(training, null);
        double[] gradient = new double[6];

        double value = subject.getValue();
        subject.getValueGradient(gradient);

        assertEquals(-Math.log(2.0), value, 1.0e-12);
        assertArrayEquals(new double[] { 0.5, 0.25, 0.25, -0.5, -0.25, -0.25 },
                gradient, 1.0e-12);
        assertEquals(1, subject.getValueCalls());
        assertEquals(1, subject.getValueGradientCalls());
    }

    @Test
    public void multipleInstancesAndWeightsContributeToLikelihoodAndGradient() {
        Alphabet features = new Alphabet();
        features.lookupIndex("f");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupIndex("left");
        labels.lookupIndex("right");
        InstanceList training = new InstanceList(features, labels);
        Instance first = new Instance(new FeatureVector(features, new double[] { 2.0 }),
                new LabelVector(labels, new double[] { 1.0, 0.0 }), "one", null);
        Instance second = new Instance(new FeatureVector(features, new double[] { 4.0 }),
                new LabelVector(labels, new double[] { 0.25, 0.75 }), "two", null);
        training.add(first);
        training.add(second);
        training.setInstanceWeight(second, 2.0);
        MaxEntOptimizableByLabelDistribution subject =
                new MaxEntOptimizableByLabelDistribution(training, null);
        double[] gradient = new double[4];

        subject.getValueGradient(gradient);

        assertEquals(-3.0 * Math.log(2.0), subject.getValue(), 1.0e-12);
        assertArrayEquals(new double[] { -1.0, 0.0, 1.0, 0.0 }, gradient, 1.0e-12);
    }

    @Test
    public void nonzeroParametersAffectScoresLikelihoodAndGaussianPrior() {
        Alphabet features = new Alphabet();
        features.lookupIndex("signal");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupIndex("yes");
        labels.lookupIndex("no");
        InstanceList training = new InstanceList(features, labels);
        Instance instance = new Instance(new FeatureVector(features, new double[] { 1.0 }),
                new LabelVector(labels, new double[] { 1.0, 0.0 }), "item", null);
        training.add(instance);
        MaxEntOptimizableByLabelDistribution subject =
                new MaxEntOptimizableByLabelDistribution(training, null);
        subject.setParameters(new double[] { 1.0, 0.0, 0.0, 0.0 });
        subject.setGaussianPriorVariance(2.0);
        double probabilityYes = Math.exp(1.0) / (Math.exp(1.0) + 1.0);
        double[] scores = new double[2];
        double[] gradient = new double[4];

        subject.getClassifier().getClassificationScores(instance, scores);
        subject.getValueGradient(gradient);

        assertEquals(probabilityYes, scores[0], 1.0e-12);
        assertEquals(1.0 - probabilityYes, scores[1], 1.0e-12);
        assertEquals(Math.log(probabilityYes) - 0.25, subject.getValue(), 1.0e-12);
        assertArrayEquals(new double[] { 0.5 - probabilityYes, 1.0 - probabilityYes,
                -(1.0 - probabilityYes), -(1.0 - probabilityYes) }, gradient, 1.0e-12);
    }

    @Test
    public void cachedComputationsAreReusedUntilAParameterChanges() {
        Alphabet features = new Alphabet();
        features.lookupIndex("f");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupIndex("a");
        labels.lookupIndex("b");
        InstanceList training = new InstanceList(features, labels);
        training.add(new Instance(new FeatureVector(features, new double[] { 1.0 }),
                new LabelVector(labels, new double[] { 0.5, 0.5 }), null, null));
        MaxEntOptimizableByLabelDistribution subject =
                new MaxEntOptimizableByLabelDistribution(training, null);
        double[] first = new double[4];
        double[] second = new double[4];

        subject.getValue();
        subject.getValue();
        subject.getValueGradient(first);
        subject.getValueGradient(second);
        assertEquals(1, subject.getValueCalls());
        assertEquals(1, subject.getValueGradientCalls());
        assertArrayEquals(first, second, 0.0);

        subject.setParameter(0, 0.25);
        subject.getValueGradient(second);
        assertEquals(2, subject.getValueCalls());
        assertEquals(2, subject.getValueGradientCalls());
        assertNotEquals(first[0], second[0], 1.0e-12);
    }

    @Test
    public void parameterBulkAccessCopiesValuesAndDoesNotExposeInternalArray() {
        Alphabet features = new Alphabet();
        features.lookupIndex("f");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupIndex("a");
        labels.lookupIndex("b");
        InstanceList training = new InstanceList(features, labels);
        training.add(new Instance(new FeatureVector(features, new double[] { 0.0 }),
                new LabelVector(labels, new double[] { 0.5, 0.5 }), null, null));
        MaxEntOptimizableByLabelDistribution subject =
                new MaxEntOptimizableByLabelDistribution(training, null);
        double[] supplied = new double[] { 1.0, 2.0, 3.0, 4.0 };
        subject.setParameters(supplied);
        supplied[0] = 99.0;
        double[] retrieved = new double[4];

        subject.getParameters(retrieved);
        retrieved[1] = 88.0;

        assertArrayEquals(new double[] { 1.0, 2.0, 3.0, 4.0 },
                subject.getClassifier().getParameters(), 0.0);
        assertEquals(2.0, subject.getParameter(1), 0.0);
    }

    @Test
    public void globalFeatureSelectionZerosDeselectedGradientCoordinates() {
        Alphabet features = new Alphabet();
        int selectedIndex = features.lookupIndex("selected");
        features.lookupIndex("excluded");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupIndex("a");
        labels.lookupIndex("b");
        FeatureSelection selection = new FeatureSelection(features);
        selection.add(selectedIndex);
        InstanceList training = new InstanceList(features, labels);
        training.setFeatureSelection(selection);
        training.add(new Instance(new FeatureVector(features, new double[] { 2.0, 4.0 }),
                new LabelVector(labels, new double[] { 1.0, 0.0 }), null, null));
        MaxEntOptimizableByLabelDistribution subject =
                new MaxEntOptimizableByLabelDistribution(training, null);
        double[] gradient = new double[6];

        subject.getValueGradient(gradient);

        assertArrayEquals(new double[] { 1.0, 0.0, 0.5, -1.0, 0.0, -0.5 },
                gradient, 1.0e-12);
        assertEquals(2, selection.cardinality());
    }

    @Test
    public void perLabelFeatureSelectionsAreAppliedIndependently() {
        Alphabet features = new Alphabet();
        int firstIndex = features.lookupIndex("first");
        int secondIndex = features.lookupIndex("second");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupIndex("a");
        labels.lookupIndex("b");
        FeatureSelection firstSelection = new FeatureSelection(features);
        firstSelection.add(firstIndex);
        FeatureSelection secondSelection = new FeatureSelection(features);
        secondSelection.add(secondIndex);
        InstanceList training = new InstanceList(features, labels);
        training.setPerLabelFeatureSelection(new FeatureSelection[] { firstSelection, secondSelection });
        training.add(new Instance(new FeatureVector(features, new double[] { 2.0, 4.0 }),
                new LabelVector(labels, new double[] { 1.0, 0.0 }), null, null));
        MaxEntOptimizableByLabelDistribution subject =
                new MaxEntOptimizableByLabelDistribution(training, null);
        double[] gradient = new double[6];

        subject.getValueGradient(gradient);

        assertArrayEquals(new double[] { 1.0, 0.0, 0.5, 0.0, -2.0, -0.5 },
                gradient, 1.0e-12);
        assertEquals(2, firstSelection.cardinality());
        assertEquals(2, secondSelection.cardinality());
    }

    @Test
    public void initialClassifierIsReusedAndParameterUpdatesChangeItsPredictions() {
        Alphabet features = new Alphabet();
        features.lookupIndex("f");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupIndex("a");
        labels.lookupIndex("b");
        InstanceList training = new InstanceList(features, labels);
        Instance instance = new Instance(new FeatureVector(features, new double[] { 1.0 }),
                new LabelVector(labels, new double[] { 0.5, 0.5 }), null, null);
        training.add(instance);
        MaxEnt classifier = new MaxEnt(training.getPipe(), new double[] { 0.0, 0.0, 0.0, 0.0 });
        MaxEntOptimizableByLabelDistribution subject =
                new MaxEntOptimizableByLabelDistribution(training, classifier);
        double[] before = new double[2];
        double[] after = new double[2];

        classifier.getClassificationScores(instance, before);
        subject.setParameter(0, 2.0);
        classifier.getClassificationScores(instance, after);

        assertSame(classifier, subject.getClassifier());
        assertArrayEquals(new double[] { 0.5, 0.5 }, before, 1.0e-12);
        assertTrue(after[0] > 0.85);
        assertEquals(2.0, classifier.getParameters()[0], 0.0);
    }

    @Test
    public void unlabeledInstancesAreIgnoredButLabeledInstancesStillCount() {
        Alphabet features = new Alphabet();
        features.lookupIndex("f");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupIndex("a");
        labels.lookupIndex("b");
        InstanceList training = new InstanceList(features, labels);
        Instance unlabeled = new Instance(new FeatureVector(features, new double[] { 100.0 }),
                new LabelVector(labels, new double[] { 0.5, 0.5 }), "unlabeled", null);
        training.add(unlabeled);
        unlabeled.unLock();
        unlabeled.setTarget(null);
        unlabeled.lock();
        training.add(new Instance(new FeatureVector(features, new double[] { 2.0 }),
                new LabelVector(labels, new double[] { 1.0, 0.0 }), "labeled", null));
        MaxEntOptimizableByLabelDistribution subject =
                new MaxEntOptimizableByLabelDistribution(training, null);
        double[] gradient = new double[4];

        subject.getValueGradient(gradient);

        assertEquals(-Math.log(2.0), subject.getValue(), 1.0e-12);
        assertArrayEquals(new double[] { 1.0, 0.5, -1.0, -0.5 }, gradient, 1.0e-12);
    }

    @Test
    public void emptyTrainingListHasOnlyGaussianPriorContribution() {
        Alphabet features = new Alphabet();
        features.lookupIndex("f");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupIndex("a");
        labels.lookupIndex("b");
        InstanceList training = new InstanceList(features, labels);
        MaxEntOptimizableByLabelDistribution subject =
                new MaxEntOptimizableByLabelDistribution(training, null);
        subject.setParameters(new double[] { 1.0, -2.0, 3.0, -4.0 });
        subject.setGaussianPriorVariance(5.0);
        double[] gradient = new double[4];

        subject.getValueGradient(gradient);

        assertEquals(-3.0, subject.getValue(), 1.0e-12);
        assertArrayEquals(new double[] { -0.2, 0.4, -0.6, 0.8 }, gradient, 1.0e-12);
    }

    @Test
    public void priorConfigurationMethodsAreFluentAndVarianceControlsPenalty() {
        Alphabet features = new Alphabet();
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupIndex("only");
        InstanceList training = new InstanceList(features, labels);
        MaxEntOptimizableByLabelDistribution subject =
                new MaxEntOptimizableByLabelDistribution(training, null);
        subject.setParameter(0, 3.0);

        assertSame(subject, subject.useGaussianPrior());
        assertSame(subject, subject.setGaussianPriorVariance(9.0));
        assertEquals(-0.5, subject.getValue(), 1.0e-12);
        double[] gradient = new double[1];
        subject.getValueGradient(gradient);
        assertArrayEquals(new double[] { -1.0 / 3.0 }, gradient, 1.0e-12);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void getParameterRejectsIndexBeyondParameterVector() {
        Alphabet features = new Alphabet();
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupIndex("only");
        InstanceList training = new InstanceList(features, labels);
        MaxEntOptimizableByLabelDistribution subject =
                new MaxEntOptimizableByLabelDistribution(training, null);

        subject.getParameter(1);
    }
}
