package cc.mallet.classify;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Test;

import cc.mallet.pipe.Noop;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.LabelAlphabet;
import cc.mallet.types.LabelVector;
import cc.mallet.types.Labeling;
import cc.mallet.types.Multinomial;

public class NaiveBayesPromptTest {

    @Test
    public void loggedConstructorPreservesSuppliedParameterObjects() {
        Alphabet features = new Alphabet(new Object[] {"sunny", "rainy"});
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("positive");
        labels.lookupLabel("negative");
        Noop pipe = new Noop(features, labels);
        Multinomial.Logged prior = new Multinomial.Logged(new double[] {0.6, 0.4});
        Multinomial.Logged positive = new Multinomial.Logged(new double[] {0.8, 0.2}, features);
        Multinomial.Logged negative = new Multinomial.Logged(new double[] {0.1, 0.9}, features);
        Multinomial.Logged[] conditionals = new Multinomial.Logged[] {positive, negative};

        NaiveBayes classifier = new NaiveBayes(pipe, prior, conditionals);

        assertSame(pipe, classifier.getInstancePipe());
        assertSame(prior, classifier.getPriors());
        assertSame(conditionals, classifier.getMultinomials());
        assertSame(positive, classifier.getMultinomials()[0]);
        assertSame(negative, classifier.getMultinomials()[1]);
    }

    @Test
    public void ordinaryMultinomialConstructorConvertsParametersToLoggedForm() {
        Alphabet features = new Alphabet(new Object[] {"a", "b"});
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("left");
        labels.lookupLabel("right");
        Noop pipe = new Noop(features, labels);
        Multinomial prior = new Multinomial(new double[] {0.25, 0.75});
        Multinomial left = new Multinomial(new double[] {0.9, 0.1}, features);
        Multinomial right = new Multinomial(new double[] {0.4, 0.6}, features);

        NaiveBayes classifier = new NaiveBayes(pipe, prior, new Multinomial[] {left, right});

        assertNotSame(prior, classifier.getPriors());
        assertEquals(Math.log(0.25), classifier.getPriors().logProbability(0), 0.0);
        assertEquals(Math.log(0.75), classifier.getPriors().logProbability(1), 0.0);
        assertEquals(0.9, classifier.getMultinomials()[0].probability(0), 1.0e-12);
        assertEquals(0.6, classifier.getMultinomials()[1].probability(1), 1.0e-12);
    }

    @Test
    public void classifyCombinesPriorRepeatedCountsAndMultipleFeatures() {
        Alphabet features = new Alphabet(new Object[] {"sunny", "rainy", "windy"});
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("positive");
        labels.lookupLabel("negative");
        Noop pipe = new Noop(features, labels);
        NaiveBayes classifier = new NaiveBayes(pipe,
                new Multinomial(new double[] {0.6, 0.4}),
                new Multinomial[] {
                        new Multinomial(new double[] {0.5, 0.3, 0.2}, features),
                        new Multinomial(new double[] {0.1, 0.2, 0.7}, features)
                });
        FeatureVector vector = new FeatureVector(features, new int[] {0, 2}, new double[] {2.0, 1.0});
        Instance instance = new Instance(vector, labels.lookupLabel("positive"), "document-1", "sunny sunny windy");

        Classification result = classifier.classify(instance);

        assertSame(instance, result.getInstance());
        assertSame(classifier, result.getClassifier());
        assertEquals(0.03 / 0.0328, result.getLabeling().value(0), 1.0e-12);
        assertEquals(0.0028 / 0.0328, result.getLabeling().value(1), 1.0e-12);
        assertEquals("positive", result.getLabeling().getBestLabel().toString());
        assertTrue(result.bestLabelIsCorrect());
    }

    @Test
    public void classifyEmptyFeatureVectorReturnsPriorDistribution() {
        Alphabet features = new Alphabet(new Object[] {"x", "y"});
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("first");
        labels.lookupLabel("second");
        NaiveBayes classifier = new NaiveBayes(new Noop(features, labels),
                new Multinomial.Logged(new double[] {0.7, 0.3}),
                new Multinomial.Logged[] {
                        new Multinomial.Logged(new double[] {0.2, 0.8}, features),
                        new Multinomial.Logged(new double[] {0.9, 0.1}, features)
                });
        Instance instance = new Instance(new FeatureVector(features, new int[0]), null, "empty", null);

        Labeling prediction = classifier.classify(instance).getLabeling();

        assertEquals(0.7, prediction.value(0), 1.0e-12);
        assertEquals(0.3, prediction.value(1), 1.0e-12);
        assertEquals(1.0, prediction.value(0) + prediction.value(1), 1.0e-12);
    }

    @Test
    public void classifySupportsFractionalFeatureWeights() {
        Alphabet features = new Alphabet(new Object[] {"term"});
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("a");
        labels.lookupLabel("b");
        NaiveBayes classifier = new NaiveBayes(new Noop(features, labels),
                new Multinomial.Logged(new double[] {0.5, 0.5}),
                new Multinomial.Logged[] {
                        new Multinomial.Logged(new double[] {0.8}, features),
                        new Multinomial.Logged(new double[] {0.2}, features)
                });
        Instance instance = new Instance(new FeatureVector(features, new int[] {0}, new double[] {0.5}), null, null, null);

        Labeling prediction = classifier.classify(instance).getLabeling();

        assertEquals(2.0 / 3.0, prediction.value(0), 1.0e-12);
        assertEquals(1.0 / 3.0, prediction.value(1), 1.0e-12);
    }

    @Test
    public void featureAddedAfterTrainingIsIgnored() {
        Alphabet features = new Alphabet(new Object[] {"known"});
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("a");
        labels.lookupLabel("b");
        NaiveBayes classifier = new NaiveBayes(new Noop(features, labels),
                new Multinomial.Logged(new double[] {0.2, 0.8}),
                new Multinomial.Logged[] {
                        new Multinomial.Logged(new double[] {1.0}, features),
                        new Multinomial.Logged(new double[] {1.0}, features)
                });
        int unseenIndex = features.lookupIndex("unseen-after-training");
        Instance instance = new Instance(new FeatureVector(features, new int[] {unseenIndex}, new double[] {25.0}), null, null, null);

        Labeling prediction = classifier.classify(instance).getLabeling();

        assertEquals(0.2, prediction.value(0), 1.0e-12);
        assertEquals(0.8, prediction.value(1), 1.0e-12);
    }

    @Test
    public void labelAddedAfterTrainingReceivesZeroPosteriorProbability() {
        Alphabet features = new Alphabet(new Object[] {"known"});
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("a");
        labels.lookupLabel("b");
        NaiveBayes classifier = new NaiveBayes(new Noop(features, labels),
                new Multinomial.Logged(new double[] {0.4, 0.6}),
                new Multinomial.Logged[] {
                        new Multinomial.Logged(new double[] {1.0}, features),
                        new Multinomial.Logged(new double[] {1.0}, features)
                });
        labels.lookupLabel("new-class");
        Instance instance = new Instance(new FeatureVector(features, new int[0]), null, null, null);

        Labeling prediction = classifier.classify(instance).getLabeling();

        assertEquals(0.4, prediction.value(0), 1.0e-12);
        assertEquals(0.6, prediction.value(1), 1.0e-12);
        assertEquals(0.0, prediction.value(2), 0.0);
        assertEquals(3, prediction.numLocations());
    }

    @Test
    public void dataLogLikelihoodUsesObservedLabelsCountsAndInstanceWeights() {
        Alphabet features = new Alphabet(new Object[] {"sunny", "rainy", "windy"});
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("positive");
        labels.lookupLabel("negative");
        Noop pipe = new Noop(features, labels);
        NaiveBayes classifier = new NaiveBayes(pipe,
                new Multinomial(new double[] {0.6, 0.4}),
                new Multinomial[] {
                        new Multinomial(new double[] {0.5, 0.3, 0.2}, features),
                        new Multinomial(new double[] {0.1, 0.2, 0.7}, features)
                });
        Instance positive = new Instance(new FeatureVector(features, new int[] {0, 2}, new double[] {2.0, 1.0}), labels.lookupLabel("positive"), "p", null);
        Instance negative = new Instance(new FeatureVector(features, new int[] {1}, new double[] {3.0}), labels.lookupLabel("negative"), "n", null);
        InstanceList instances = new InstanceList(features, labels);
        instances.add(positive, 2.0);
        instances.add(negative, 0.5);

        double actual = classifier.dataLogLikelihood(instances);

        double expected = 2.0 * (2.0 * Math.log(0.5) + Math.log(0.2))
                + 0.5 * (3.0 * Math.log(0.2));
        assertEquals(expected, actual, 1.0e-12);
    }

    @Test
    public void dataLogLikelihoodMarginalizesUnlabeledInstanceUsingPrediction() {
        Alphabet features = new Alphabet(new Object[] {"sunny", "rainy", "windy"});
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("positive");
        labels.lookupLabel("negative");
        NaiveBayes classifier = new NaiveBayes(new Noop(features, labels),
                new Multinomial(new double[] {0.6, 0.4}),
                new Multinomial[] {
                        new Multinomial(new double[] {0.5, 0.3, 0.2}, features),
                        new Multinomial(new double[] {0.1, 0.2, 0.7}, features)
                });
        Instance unlabeled = new Instance(new FeatureVector(features, new int[] {0, 2}, new double[] {2.0, 1.0}), null, "u", null);
        InstanceList instances = new InstanceList(features, null);
        instances.add(unlabeled, 1.25);

        double actual = classifier.dataLogLikelihood(instances);

        double positivePosterior = 0.03 / 0.0328;
        double negativePosterior = 0.0028 / 0.0328;
        double expected = 1.25 * (positivePosterior * (2.0 * Math.log(0.5) + Math.log(0.2))
                + negativePosterior * (2.0 * Math.log(0.1) + Math.log(0.7)));
        assertEquals(expected, actual, 1.0e-12);
    }

    @Test
    public void labelLogLikelihoodUsesHardLabelAndInstanceWeight() {
        Alphabet features = new Alphabet(new Object[] {"sunny", "rainy", "windy"});
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("positive");
        labels.lookupLabel("negative");
        NaiveBayes classifier = new NaiveBayes(new Noop(features, labels),
                new Multinomial(new double[] {0.6, 0.4}),
                new Multinomial[] {
                        new Multinomial(new double[] {0.5, 0.3, 0.2}, features),
                        new Multinomial(new double[] {0.1, 0.2, 0.7}, features)
                });
        Instance instance = new Instance(new FeatureVector(features, new int[] {0}, new double[] {1.0}), labels.lookupLabel("negative"), null, null);
        InstanceList instances = new InstanceList(features, labels);
        instances.add(instance, 1.5);

        double actual = classifier.labelLogLikelihood(instances);

        assertEquals(1.5 * Math.log(0.04 / 0.34), actual, 1.0e-12);
    }

    @Test
    public void labelLogLikelihoodSupportsSoftLabelDistribution() {
        Alphabet features = new Alphabet(new Object[] {"sunny", "rainy", "windy"});
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("positive");
        labels.lookupLabel("negative");
        NaiveBayes classifier = new NaiveBayes(new Noop(features, labels),
                new Multinomial(new double[] {0.6, 0.4}),
                new Multinomial[] {
                        new Multinomial(new double[] {0.5, 0.3, 0.2}, features),
                        new Multinomial(new double[] {0.1, 0.2, 0.7}, features)
                });
        LabelVector softTarget = new LabelVector(labels, new double[] {0.25, 0.75});
        Instance instance = new Instance(new FeatureVector(features, new int[] {0}, new double[] {1.0}), softTarget, null, null);
        InstanceList instances = new InstanceList(features, labels);
        instances.add(instance, 2.0);

        double actual = classifier.labelLogLikelihood(instances);

        double expected = 2.0 * (0.25 * Math.log(0.30 / 0.34) + 0.75 * Math.log(0.04 / 0.34));
        assertEquals(expected, actual, 1.0e-12);
    }

    @Test
    public void labelLogLikelihoodSkipsZeroWeightInSparseSoftLabeling() {
        Alphabet features = new Alphabet(new Object[] {"sunny"});
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("positive");
        labels.lookupLabel("negative");
        NaiveBayes classifier = new NaiveBayes(new Noop(features, labels),
                new Multinomial(new double[] {0.6, 0.4}),
                new Multinomial[] {
                        new Multinomial(new double[] {1.0}, features),
                        new Multinomial(new double[] {1.0}, features)
                });
        LabelVector target = new LabelVector(labels, new int[] {0, 1}, new double[] {1.0, 0.0});
        Instance instance = new Instance(new FeatureVector(features, new int[0]), target, null, null);
        InstanceList instances = new InstanceList(features, labels);
        instances.add(instance);

        double actual = classifier.labelLogLikelihood(instances);

        assertEquals(Math.log(0.6), actual, 1.0e-12);
    }

    @Test
    public void labelLogLikelihoodIgnoresUnlabeledInstances() {
        Alphabet features = new Alphabet(new Object[] {"x"});
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("a");
        labels.lookupLabel("b");
        NaiveBayes classifier = new NaiveBayes(new Noop(features, labels),
                new Multinomial(new double[] {0.5, 0.5}),
                new Multinomial[] {
                        new Multinomial(new double[] {1.0}, features),
                        new Multinomial(new double[] {1.0}, features)
                });
        Instance unlabeled = new Instance(new FeatureVector(features, new int[] {0}), null, null, null);
        InstanceList instances = new InstanceList(features, null);
        instances.add(unlabeled, 9.0);

        assertEquals(0.0, classifier.labelLogLikelihood(instances), 0.0);
    }

    @Test
    public void bothLikelihoodMethodsReturnZeroForEmptyList() {
        Alphabet features = new Alphabet(new Object[] {"x"});
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("a");
        labels.lookupLabel("b");
        Noop pipe = new Noop(features, labels);
        NaiveBayes classifier = new NaiveBayes(pipe,
                new Multinomial(new double[] {0.5, 0.5}),
                new Multinomial[] {
                        new Multinomial(new double[] {1.0}, features),
                        new Multinomial(new double[] {1.0}, features)
                });
        InstanceList empty = new InstanceList(pipe);

        assertEquals(0.0, classifier.dataLogLikelihood(empty), 0.0);
        assertEquals(0.0, classifier.labelLogLikelihood(empty), 0.0);
    }

    @Test(expected = ClassCastException.class)
    public void classifyRejectsDataThatIsNotAFeatureVector() {
        Alphabet features = new Alphabet(new Object[] {"x"});
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("a");
        labels.lookupLabel("b");
        NaiveBayes classifier = new NaiveBayes(new Noop(features, labels),
                new Multinomial(new double[] {0.5, 0.5}),
                new Multinomial[] {
                        new Multinomial(new double[] {1.0}, features),
                        new Multinomial(new double[] {1.0}, features)
                });

        classifier.classify(new Instance("not-a-vector", null, null, null));
    }

    @Test
    public void constructorAcceptsNullPipeToSuppressAlphabetCheck() {
        Multinomial prior = new Multinomial(new double[] {1.0});
        Multinomial conditional = new Multinomial(new double[] {1.0});

        NaiveBayes classifier = new NaiveBayes(null, prior, new Multinomial[] {conditional});

        assertNull(classifier.getInstancePipe());
        assertEquals(1.0, classifier.getPriors().probability(0), 0.0);
        assertEquals(1.0, classifier.getMultinomials()[0].probability(0), 0.0);
    }

    @Test
    public void allImpossibleClassScoresProduceNaNPosteriors() {
        Alphabet features = new Alphabet(new Object[] {"impossible", "possible"});
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("a");
        labels.lookupLabel("b");
        NaiveBayes classifier = new NaiveBayes(new Noop(features, labels),
                new Multinomial(new double[] {0.5, 0.5}),
                new Multinomial[] {
                        new Multinomial(new double[] {0.0, 1.0}, features),
                        new Multinomial(new double[] {0.0, 1.0}, features)
                });
        Instance impossible = new Instance(new FeatureVector(features, new int[] {0}), null, null, null);

        Labeling prediction = classifier.classify(impossible).getLabeling();

        assertTrue(Double.isNaN(prediction.value(0)));
        assertTrue(Double.isNaN(prediction.value(1)));
    }

    @Test
    public void serializationRoundTripRetainsPipeParametersAndBehavior() throws Exception {
        Alphabet features = new Alphabet(new Object[] {"sunny", "rainy"});
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("positive");
        labels.lookupLabel("negative");
        NaiveBayes original = new NaiveBayes(new Noop(features, labels),
                new Multinomial(new double[] {0.6, 0.4}),
                new Multinomial[] {
                        new Multinomial(new double[] {0.8, 0.2}, features),
                        new Multinomial(new double[] {0.1, 0.9}, features)
                });
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ObjectOutputStream output = new ObjectOutputStream(bytes);
        output.writeObject(original);
        output.close();
        ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()));
        NaiveBayes restored = (NaiveBayes) input.readObject();
        input.close();
        FeatureVector restoredVector = new FeatureVector(restored.getAlphabet(), new int[] {0}, new double[] {2.0});
        Instance restoredInstance = new Instance(restoredVector, null, "after-serialization", null);

        Labeling prediction = restored.classify(restoredInstance).getLabeling();

        assertNotSame(original, restored);
        assertEquals("positive", restored.getLabelAlphabet().lookupObject(0));
        assertEquals(0.6, restored.getPriors().probability(0), 1.0e-12);
        assertEquals(0.8, restored.getMultinomials()[0].probability(0), 1.0e-12);
        assertEquals(0.384 / 0.388, prediction.value(0), 1.0e-12);
        assertEquals(0.004 / 0.388, prediction.value(1), 1.0e-12);
        assertFalse(Double.isNaN(prediction.getBestValue()));
    }

    @Test
    public void printWordsAcceptsCountsOutsideFeatureRange() {
        Alphabet features = new Alphabet(new Object[] {"x", "y"});
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("a");
        labels.lookupLabel("b");
        NaiveBayes classifier = new NaiveBayes(new Noop(features, labels),
                new Multinomial(new double[] {0.5, 0.5}),
                new Multinomial[] {
                        new Multinomial(new double[] {0.7, 0.3}, features),
                        new Multinomial(new double[] {0.2, 0.8}, features)
                });

        classifier.printWords(0);
        classifier.printWords(-4);
        classifier.printWords(20);
    }
}
