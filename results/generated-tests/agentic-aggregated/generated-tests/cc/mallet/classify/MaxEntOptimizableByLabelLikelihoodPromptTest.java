package cc.mallet.classify;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import cc.mallet.pipe.Noop;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureSelection;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.LabelAlphabet;

public class MaxEntOptimizableByLabelLikelihoodPromptTest {

    @Test
    public void constructorCreatesClassifierAndExpectedParameterSpace() {
        Alphabet features = new Alphabet();
        features.lookupIndex("word");
        features.lookupIndex("shape");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("positive");
        labels.lookupLabel("negative");
        Noop pipe = new Noop(features, labels);
        InstanceList training = new InstanceList(pipe);
        training.add(new Instance(new FeatureVector(features, new int[] {0}, new double[] {1.0}),
                labels.lookupLabel("positive"), "first", null));

        MaxEntOptimizableByLabelLikelihood objective =
                new MaxEntOptimizableByLabelLikelihood(training, null);

        assertEquals(6, objective.getNumParameters());
        assertSame(pipe, objective.getClassifier().getInstancePipe());
        assertEquals(0.0, objective.getParameter(0), 0.0);
        assertEquals(0.0, objective.getParameter(5), 0.0);
        assertFalse(labels.growthStopped() == false);
    }

    @Test
    public void zeroParametersProduceWeightedLogLikelihood() {
        Alphabet features = new Alphabet();
        features.lookupIndex("x");
        features.lookupIndex("y");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("A");
        labels.lookupLabel("B");
        InstanceList training = new InstanceList(new Noop(features, labels));
        Instance first = new Instance(new FeatureVector(features, new int[] {0, 1},
                new double[] {1.0, 2.0}), labels.lookupLabel("A"), "first", "source-1");
        Instance second = new Instance(new FeatureVector(features, new int[] {0},
                new double[] {3.0}), labels.lookupLabel("B"), "second", "source-2");
        training.add(first);
        training.add(second);
        training.setInstanceWeight(second, 2.0);
        MaxEntOptimizableByLabelLikelihood objective =
                new MaxEntOptimizableByLabelLikelihood(training, null).useNoPrior();

        double value = objective.getValue();

        assertEquals(-3.0 * Math.log(2.0), value, 1.0e-12);
        assertEquals(1, objective.getValueCalls());
    }

    @Test
    public void zeroParametersProduceExactWeightedGradient() {
        Alphabet features = new Alphabet();
        features.lookupIndex("x");
        features.lookupIndex("y");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("A");
        labels.lookupLabel("B");
        InstanceList training = new InstanceList(new Noop(features, labels));
        Instance first = new Instance(new FeatureVector(features, new int[] {0, 1},
                new double[] {1.0, 2.0}), labels.lookupLabel("A"), "first", null);
        Instance second = new Instance(new FeatureVector(features, new int[] {0},
                new double[] {3.0}), labels.lookupLabel("B"), "second", null);
        training.add(first);
        training.add(second);
        training.setInstanceWeight(second, 2.0);
        MaxEntOptimizableByLabelLikelihood objective =
                new MaxEntOptimizableByLabelLikelihood(training, null).useNoPrior();
        double[] gradient = new double[6];

        objective.getValueGradient(gradient);

        assertArrayEquals(new double[] {-2.5, 1.0, -0.5, 2.5, -1.0, 0.5},
                gradient, 1.0e-12);
        assertEquals(1, objective.getValueCalls());
        assertEquals(1, objective.getValueGradientCalls());
    }

    @Test
    public void unlabeledInstancesDoNotContributeToValueOrGradient() {
        Alphabet features = new Alphabet();
        features.lookupIndex("x");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("A");
        labels.lookupLabel("B");
        InstanceList training = new InstanceList(new Noop(features, labels));
        Instance unlabeled = new Instance(new FeatureVector(features, new int[] {0},
                new double[] {9.0}), labels.lookupLabel("A"), "unlabeled", null);
        training.add(unlabeled);
        unlabeled.unLock();
        unlabeled.setTarget(null);
        unlabeled.lock();
        MaxEntOptimizableByLabelLikelihood objective =
                new MaxEntOptimizableByLabelLikelihood(training, null).useNoPrior();
        double[] gradient = new double[4];

        objective.getValueGradient(gradient);

        assertEquals(0.0, objective.getValue(), 0.0);
        assertArrayEquals(new double[] {0.0, 0.0, 0.0, 0.0}, gradient, 0.0);
    }

    @Test
    public void singleLabelTrainingHasCertainPredictionAndZeroNoPriorGradient() {
        Alphabet features = new Alphabet();
        features.lookupIndex("x");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("only");
        InstanceList training = new InstanceList(new Noop(features, labels));
        training.add(new Instance(new FeatureVector(features, new int[] {0}, new double[] {7.5}),
                labels.lookupLabel("only"), "single", null));
        MaxEntOptimizableByLabelLikelihood objective =
                new MaxEntOptimizableByLabelLikelihood(training, null).useNoPrior();
        objective.setParameters(new double[] {2.0, -3.0});
        double[] gradient = new double[2];

        objective.getValueGradient(gradient);

        assertEquals(0.0, objective.getValue(), 0.0);
        assertArrayEquals(new double[] {0.0, 0.0}, gradient, 1.0e-12);
    }

    @Test
    public void gaussianPriorChangesValueAndEveryRelevantGradientCoordinate() {
        Alphabet features = new Alphabet();
        features.lookupIndex("x");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("A");
        labels.lookupLabel("B");
        InstanceList training = new InstanceList(new Noop(features, labels));
        training.add(new Instance(new FeatureVector(features, new int[] {0}, new double[] {1.0}),
                labels.lookupLabel("A"), "one", null));
        MaxEntOptimizableByLabelLikelihood objective =
                new MaxEntOptimizableByLabelLikelihood(training, null).setGaussianPriorVariance(2.0);
        double logTwo = Math.log(2.0);
        objective.setParameter(0, logTwo);
        double[] gradient = new double[4];

        double value = objective.getValue();
        objective.getValueGradient(gradient);

        assertEquals(Math.log(2.0 / 3.0) - logTwo * logTwo / 4.0, value, 1.0e-12);
        assertArrayEquals(new double[] {1.0 / 3.0 - logTwo / 2.0, 1.0 / 3.0,
                -1.0 / 3.0, -1.0 / 3.0}, gradient, 1.0e-12);
    }

    @Test
    public void noPriorOmitsPenaltyAtNonzeroParameters() {
        Alphabet features = new Alphabet();
        features.lookupIndex("x");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("A");
        labels.lookupLabel("B");
        InstanceList training = new InstanceList(new Noop(features, labels));
        training.add(new Instance(new FeatureVector(features, new int[] {0}, new double[] {1.0}),
                labels.lookupLabel("A"), "one", null));
        MaxEntOptimizableByLabelLikelihood objective =
                new MaxEntOptimizableByLabelLikelihood(training, null).useNoPrior();
        objective.setParameter(0, Math.log(2.0));
        double[] gradient = new double[4];

        objective.getValueGradient(gradient);

        assertEquals(Math.log(2.0 / 3.0), objective.getValue(), 1.0e-12);
        assertArrayEquals(new double[] {1.0 / 3.0, 1.0 / 3.0, -1.0 / 3.0, -1.0 / 3.0},
                gradient, 1.0e-12);
    }

    @Test
    public void hyperbolicPriorContributesExpectedValue() {
        Alphabet features = new Alphabet();
        features.lookupIndex("x");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("A");
        labels.lookupLabel("B");
        InstanceList training = new InstanceList(new Noop(features, labels));
        training.add(new Instance(new FeatureVector(features, new int[] {0}, new double[] {1.0}),
                labels.lookupLabel("A"), "one", null));
        MaxEntOptimizableByLabelLikelihood objective =
                new MaxEntOptimizableByLabelLikelihood(training, null)
                        .setHyperbolicPriorSlope(0.4)
                        .setHyperbolicPriorSharpness(0.5);
        double logTwo = Math.log(2.0);
        objective.setParameter(0, logTwo);

        double value = objective.getValue();

        double expectedPrior = 0.4 / 0.5 * Math.log(Math.cosh(0.5 * logTwo));
        assertEquals(Math.log(2.0 / 3.0) - expectedPrior, value, 1.0e-12);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void hyperbolicPriorGradientIsExplicitlyUnsupported() {
        Alphabet features = new Alphabet();
        features.lookupIndex("x");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("A");
        labels.lookupLabel("B");
        InstanceList training = new InstanceList(new Noop(features, labels));
        training.add(new Instance(new FeatureVector(features, new int[] {0}),
                labels.lookupLabel("A"), "one", null));
        MaxEntOptimizableByLabelLikelihood objective =
                new MaxEntOptimizableByLabelLikelihood(training, null).useHyperbolicPrior();

        objective.getValueGradient(new double[4]);
    }

    @Test
    public void globalFeatureSelectionZerosDeselectedGradientAndIncludesBias() {
        Alphabet features = new Alphabet();
        features.lookupIndex("selected");
        features.lookupIndex("excluded");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("A");
        labels.lookupLabel("B");
        InstanceList training = new InstanceList(new Noop(features, labels));
        FeatureSelection selection = new FeatureSelection(features);
        selection.add(0);
        training.setFeatureSelection(selection);
        training.add(new Instance(new FeatureVector(features, new int[] {0, 1},
                new double[] {2.0, 4.0}), labels.lookupLabel("A"), "one", null));
        MaxEntOptimizableByLabelLikelihood objective =
                new MaxEntOptimizableByLabelLikelihood(training, null).useNoPrior();
        double[] gradient = new double[6];

        objective.getValueGradient(gradient);

        assertTrue(selection.contains(2));
        assertArrayEquals(new double[] {1.0, 0.0, 0.5, -1.0, 0.0, -0.5},
                gradient, 1.0e-12);
    }

    @Test
    public void perLabelFeatureSelectionsAreAppliedIndependently() {
        Alphabet features = new Alphabet();
        features.lookupIndex("left");
        features.lookupIndex("right");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("A");
        labels.lookupLabel("B");
        InstanceList training = new InstanceList(new Noop(features, labels));
        FeatureSelection selectionA = new FeatureSelection(features);
        FeatureSelection selectionB = new FeatureSelection(features);
        selectionA.add(0);
        selectionB.add(1);
        training.setPerLabelFeatureSelection(new FeatureSelection[] {selectionA, selectionB});
        training.add(new Instance(new FeatureVector(features, new int[] {0, 1},
                new double[] {2.0, 4.0}), labels.lookupLabel("A"), "one", null));
        MaxEntOptimizableByLabelLikelihood objective =
                new MaxEntOptimizableByLabelLikelihood(training, null).useNoPrior();
        double[] gradient = new double[6];

        objective.getValueGradient(gradient);

        assertTrue(selectionA.contains(2));
        assertTrue(selectionB.contains(2));
        assertArrayEquals(new double[] {1.0, 0.0, 0.5, 0.0, -2.0, -0.5},
                gradient, 1.0e-12);
    }

    @Test
    public void existingClassifierIsReusedAndSharesParameterUpdates() {
        Alphabet features = new Alphabet();
        features.lookupIndex("x");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("A");
        labels.lookupLabel("B");
        Noop pipe = new Noop(features, labels);
        InstanceList training = new InstanceList(pipe);
        training.add(new Instance(new FeatureVector(features, new int[] {0}, new double[] {1.0}),
                labels.lookupLabel("A"), "one", null));
        double[] initialParameters = new double[] {0.2, 0.3, -0.4, 0.5};
        MaxEnt classifier = new MaxEnt(pipe, initialParameters);
        MaxEntOptimizableByLabelLikelihood objective =
                new MaxEntOptimizableByLabelLikelihood(training, classifier);

        objective.setParameter(2, 1.25);

        assertSame(classifier, objective.getClassifier());
        assertEquals(1.25, classifier.getParameters()[2], 0.0);
        double[] scores = new double[2];
        classifier.getClassificationScores(training.get(0), scores);
        assertEquals(1.0, scores[0] + scores[1], 1.0e-12);
        assertTrue(scores[1] > scores[0]);
    }

    @Test
    public void getParametersCopiesWithoutExposingBackingArray() {
        Alphabet features = new Alphabet();
        features.lookupIndex("x");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("A");
        labels.lookupLabel("B");
        InstanceList training = new InstanceList(new Noop(features, labels));
        MaxEntOptimizableByLabelLikelihood objective =
                new MaxEntOptimizableByLabelLikelihood(training, null);
        objective.setParameters(new double[] {1.0, 2.0, 3.0, 4.0});
        double[] copy = new double[4];

        objective.getParameters(copy);
        copy[0] = 99.0;

        assertArrayEquals(new double[] {99.0, 2.0, 3.0, 4.0}, copy, 0.0);
        assertEquals(1.0, objective.getParameter(0), 0.0);
        assertNotSame(copy, objective.getClassifier().getParameters());
    }

    @Test
    public void nullAndWrongSizedGetParameterBuffersAreTolerated() {
        Alphabet features = new Alphabet();
        features.lookupIndex("x");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("A");
        labels.lookupLabel("B");
        InstanceList training = new InstanceList(new Noop(features, labels));
        MaxEntOptimizableByLabelLikelihood objective =
                new MaxEntOptimizableByLabelLikelihood(training, null);
        objective.setParameters(new double[] {1.0, 2.0, 3.0, 4.0});
        double[] wrongSize = new double[] {17.0};

        objective.getParameters(null);
        objective.getParameters(wrongSize);

        assertArrayEquals(new double[] {17.0}, wrongSize, 0.0);
        assertEquals(4, objective.getNumParameters());
    }

    @Test(expected = NullPointerException.class)
    public void setParametersRejectsNullWhenAssertionsAreDisabled() {
        Alphabet features = new Alphabet();
        features.lookupIndex("x");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("A");
        labels.lookupLabel("B");
        InstanceList training = new InstanceList(new Noop(features, labels));
        MaxEntOptimizableByLabelLikelihood objective =
                new MaxEntOptimizableByLabelLikelihood(training, null);

        objective.setParameters(null);
    }

    @Test
    public void valueAndGradientCachesInvalidateAfterParameterChanges() {
        Alphabet features = new Alphabet();
        features.lookupIndex("x");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("A");
        labels.lookupLabel("B");
        InstanceList training = new InstanceList(new Noop(features, labels));
        training.add(new Instance(new FeatureVector(features, new int[] {0}, new double[] {1.0}),
                labels.lookupLabel("A"), "one", null));
        MaxEntOptimizableByLabelLikelihood objective =
                new MaxEntOptimizableByLabelLikelihood(training, null).useNoPrior();
        double[] firstGradient = new double[4];
        double[] secondGradient = new double[4];

        double firstValue = objective.getValue();
        assertEquals(firstValue, objective.getValue(), 0.0);
        objective.getValueGradient(firstGradient);
        objective.getValueGradient(firstGradient);
        assertEquals(1, objective.getValueCalls());
        assertEquals(1, objective.getValueGradientCalls());
        objective.setParameter(0, Math.log(3.0));
        objective.getValueGradient(secondGradient);

        assertEquals(2, objective.getValueCalls());
        assertEquals(2, objective.getValueGradientCalls());
        assertTrue(objective.getValue() > firstValue);
        assertTrue(secondGradient[0] < firstGradient[0]);
    }

    @Test
    public void priorConfigurationMethodsAreFluent() {
        MaxEntOptimizableByLabelLikelihood objective = new MaxEntOptimizableByLabelLikelihood();

        assertSame(objective, objective.useGaussianPrior());
        assertSame(objective, objective.useNoPrior());
        assertSame(objective, objective.useHyperbolicPrior());
        assertSame(objective, objective.setGaussianPriorVariance(3.0));
        assertSame(objective, objective.setHyperbolicPriorSlope(0.7));
        assertSame(objective, objective.setHyperbolicPriorSharpness(4.0));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getParameterRejectsIndexAtParameterCount() {
        Alphabet features = new Alphabet();
        features.lookupIndex("x");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("A");
        labels.lookupLabel("B");
        InstanceList training = new InstanceList(new Noop(features, labels));
        MaxEntOptimizableByLabelLikelihood objective =
                new MaxEntOptimizableByLabelLikelihood(training, null);

        objective.getParameter(objective.getNumParameters());
    }
}
