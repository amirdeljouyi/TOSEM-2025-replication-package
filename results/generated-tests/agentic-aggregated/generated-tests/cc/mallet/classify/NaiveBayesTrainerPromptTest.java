package cc.mallet.classify;

import cc.mallet.pipe.Noop;
import cc.mallet.pipe.Pipe;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.LabelAlphabet;
import cc.mallet.types.LabelVector;
import cc.mallet.types.Multinomial;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static org.junit.Assert.*;

public class NaiveBayesTrainerPromptTest {

    @Test
    public void defaultsAndFluentNormalizationAreExposed() {
        NaiveBayesTrainer trainer = new NaiveBayesTrainer();

        assertNull(trainer.getClassifier());
        assertNull(trainer.getAlphabet());
        assertEquals(-1.0, trainer.getDocLengthNormalization(), 0.0);
        assertTrue(trainer.getFeatureMultinomialEstimator() instanceof Multinomial.LaplaceEstimator);
        assertTrue(trainer.getPriorMultinomialEstimator() instanceof Multinomial.LaplaceEstimator);
        assertSame(trainer, trainer.setDocLengthNormalization(17.5));
        assertEquals(17.5, trainer.getDocLengthNormalization(), 0.0);
        assertEquals("NaiveBayesTrainer", trainer.toString());
    }

    @Test
    public void estimatorSettersAreFluentAndRetainConfiguredPrototypes() {
        NaiveBayesTrainer trainer = new NaiveBayesTrainer();
        Multinomial.Estimator featureEstimator = new Multinomial.MLEstimator();
        Multinomial.Estimator priorEstimator = new Multinomial.LaplaceEstimator();

        assertSame(trainer, trainer.setFeatureMultinomialEstimator(featureEstimator));
        assertSame(trainer, trainer.setPriorMultinomialEstimator(priorEstimator));
        assertSame(featureEstimator, trainer.getFeatureMultinomialEstimator());
        assertSame(priorEstimator, trainer.getPriorMultinomialEstimator());
    }

    @Test
    public void pipeConstructorPublishesAlphabetsAndPreventsEstimatorChanges() {
        Alphabet data = new Alphabet();
        LabelAlphabet labels = new LabelAlphabet();
        Pipe pipe = new Noop(data, labels);
        NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);

        assertSame(data, trainer.getAlphabet());
        assertSame(data, trainer.getAlphabets()[0]);
        assertSame(labels, trainer.getAlphabets()[1]);
        assertTrue(trainer.alphabetsMatch(pipe));
        try {
            trainer.setFeatureMultinomialEstimator(new Multinomial.MLEstimator());
            fail("A trainer bound to a pipe must reject estimator replacement");
        } catch (IllegalStateException expected) {
            assertTrue(expected.getMessage().contains("incrementalTrain"));
        }
        try {
            trainer.setPriorMultinomialEstimator(new Multinomial.MLEstimator());
            fail("A trainer bound to a pipe must reject estimator replacement");
        } catch (IllegalStateException expected) {
            assertTrue(expected.getMessage().contains("incrementalTrain"));
        }
    }

    @Test
    public void batchTrainingLearnsFeatureSpecificLabels() {
        Alphabet data = new Alphabet();
        int sunny = data.lookupIndex("sunny");
        int rainy = data.lookupIndex("rainy");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("outdoor");
        labels.lookupLabel("indoor");
        Pipe pipe = new Noop(data, labels);
        InstanceList training = new InstanceList(pipe);
        training.add(new Instance(new FeatureVector(data, new int[]{sunny}, new double[]{3.0}),
                labels.lookupLabel("outdoor"), "sunny-document", null));
        training.add(new Instance(new FeatureVector(data, new int[]{rainy}, new double[]{3.0}),
                labels.lookupLabel("indoor"), "rainy-document", null));

        NaiveBayesTrainer trainer = new NaiveBayesTrainer();
        NaiveBayes classifier = trainer.train(training);
        Instance sunnyQuery = new Instance(new FeatureVector(data, new int[]{sunny}), null, "query", null);
        Instance rainyQuery = new Instance(new FeatureVector(data, new int[]{rainy}), null, "query", null);

        assertSame(classifier, trainer.getClassifier());
        assertSame(pipe, classifier.getInstancePipe());
        assertEquals("outdoor", classifier.classify(sunnyQuery).getLabeling().getBestLabel().toString());
        assertEquals("indoor", classifier.classify(rainyQuery).getLabeling().getBestLabel().toString());
        assertEquals(0.8, classifier.classify(sunnyQuery).getLabeling().value(labels.lookupLabel("outdoor")), 1.0e-12);
    }

    @Test
    public void allZeroNormTrainingDataCannotProduceFeatureMultinomials() {
        Alphabet data = new Alphabet();
        data.lookupIndex("unused-but-defines-the-multinomial");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("heavy");
        labels.lookupLabel("light");
        Pipe pipe = new Noop(data, labels);
        InstanceList training = new InstanceList(pipe);
        Instance heavy = new Instance(new FeatureVector(data, new int[0]), labels.lookupLabel("heavy"), "h", null);
        Instance light = new Instance(new FeatureVector(data, new int[0]), labels.lookupLabel("light"), "l", null);
        training.add(heavy, 5.0);
        training.add(light, 1.0);

        try {
            new NaiveBayesTrainer().train(training);
            fail("Skipping every instance leaves no estimable class-prior multinomial");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("Probabilities sum to 0.0"));
        }
    }

    @Test
    public void weightedNonEmptyInstancesInfluenceClassPriors() {
        Alphabet data = new Alphabet();
        int token = data.lookupIndex("token");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("heavy");
        labels.lookupLabel("light");
        Pipe pipe = new Noop(data, labels);
        InstanceList training = new InstanceList(pipe);
        training.add(new Instance(new FeatureVector(data, new int[]{token}), labels.lookupLabel("heavy"), "h", null), 5.0);
        training.add(new Instance(new FeatureVector(data, new int[]{token}), labels.lookupLabel("light"), "l", null), 1.0);

        NaiveBayes classifier = new NaiveBayesTrainer().train(training);
        Instance query = new Instance(new FeatureVector(data, new int[0]), null, "q", null);

        assertEquals(0.75, classifier.classify(query).getLabeling().value(labels.lookupLabel("heavy")), 1.0e-12);
        assertEquals(0.25, classifier.classify(query).getLabeling().value(labels.lookupLabel("light")), 1.0e-12);
    }

    @Test
    public void documentLengthNormalizationBalancesDocumentsBeforeCounting() {
        Alphabet data = new Alphabet();
        int x = data.lookupIndex("x");
        int y = data.lookupIndex("y");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("long");
        labels.lookupLabel("short");
        Pipe pipe = new Noop(data, labels);
        InstanceList training = new InstanceList(pipe);
        training.add(new Instance(new FeatureVector(data, new int[]{x}, new double[]{10.0}), labels.lookupLabel("long"), "l", null));
        training.add(new Instance(new FeatureVector(data, new int[]{y}, new double[]{1.0}), labels.lookupLabel("short"), "s", null));
        Instance mixed = new Instance(new FeatureVector(data, new int[]{x, y}, new double[]{1.0, 1.0}), null, "mixed", null);

        NaiveBayes unnormalized = new NaiveBayesTrainer().train(training);
        NaiveBayes normalized = new NaiveBayesTrainer().setDocLengthNormalization(1.0).train(training);

        assertEquals("short", unnormalized.classify(mixed).getLabeling().getBestLabel().toString());
        assertEquals(11.0 / 31.0, normalized.classify(mixed).getLabeling().value(labels.lookupLabel("long")), 1.0e-12);
        assertEquals(20.0 / 31.0, normalized.classify(mixed).getLabeling().value(labels.lookupLabel("short")), 1.0e-12);
    }

    @Test
    public void singleInstanceIncrementalCallsAccumulateEvidence() {
        Alphabet data = new Alphabet();
        int red = data.lookupIndex("red");
        int blue = data.lookupIndex("blue");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("warm");
        labels.lookupLabel("cool");
        NaiveBayesTrainer trainer = new NaiveBayesTrainer();
        Instance warm = new Instance(new FeatureVector(data, new int[]{red}, new double[]{4.0}), labels.lookupLabel("warm"), "w", null);
        Instance cool = new Instance(new FeatureVector(data, new int[]{blue}, new double[]{4.0}), labels.lookupLabel("cool"), "c", null);

        NaiveBayes first = trainer.trainIncremental(warm);
        NaiveBayes second = trainer.trainIncremental(cool);
        Instance redQuery = new Instance(new FeatureVector(data, new int[]{red}), null, "r", null);
        Instance blueQuery = new Instance(new FeatureVector(data, new int[]{blue}), null, "b", null);

        assertNotSame(first, second);
        assertSame(second, trainer.getClassifier());
        assertNotNull(second.getInstancePipe());
        assertEquals("warm", second.classify(redQuery).getLabeling().getBestLabel().toString());
        assertEquals("cool", second.classify(blueQuery).getLabeling().getBestLabel().toString());
    }

    @Test
    public void incrementalListsAccumulateAcrossCalls() {
        Alphabet data = new Alphabet();
        int alpha = data.lookupIndex("alpha");
        int beta = data.lookupIndex("beta");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("A");
        labels.lookupLabel("B");
        Pipe pipe = new Noop(data, labels);
        InstanceList firstList = new InstanceList(pipe);
        firstList.add(new Instance(new FeatureVector(data, new int[]{alpha}, new double[]{3.0}), labels.lookupLabel("A"), "a", null));
        InstanceList secondList = new InstanceList(pipe);
        secondList.add(new Instance(new FeatureVector(data, new int[]{beta}, new double[]{3.0}), labels.lookupLabel("B"), "b", null));
        NaiveBayesTrainer trainer = new NaiveBayesTrainer();

        trainer.trainIncremental(firstList);
        NaiveBayes classifier = trainer.trainIncremental(secondList);

        assertEquals("A", classifier.classify(new Instance(new FeatureVector(data, new int[]{alpha}), null, null, null)).getLabeling().getBestLabel().toString());
        assertEquals("B", classifier.classify(new Instance(new FeatureVector(data, new int[]{beta}), null, null, null)).getLabeling().getBestLabel().toString());
    }

    @Test
    public void ordinaryTrainResetsFeatureStatisticsButPriorPrototypeRetainsCounts() {
        Alphabet data = new Alphabet();
        int x = data.lookupIndex("x");
        int y = data.lookupIndex("y");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("A");
        labels.lookupLabel("B");
        Pipe pipe = new Noop(data, labels);
        InstanceList oldData = new InstanceList(pipe);
        oldData.add(new Instance(new FeatureVector(data, new int[]{x}, new double[]{100.0}), labels.lookupLabel("A"), "old", null));
        InstanceList replacement = new InstanceList(pipe);
        replacement.add(new Instance(new FeatureVector(data, new int[]{y}, new double[]{1.0}), labels.lookupLabel("B"), "new", null));
        NaiveBayesTrainer trainer = new NaiveBayesTrainer();

        trainer.trainIncremental(oldData);
        NaiveBayes classifier = trainer.train(replacement);

        // Estimator cloning is shallow: the unbound prior prototype shares its count array,
        // while feature clones replace theirs when an alphabet is assigned.
        assertEquals(0.5, classifier.getPriors().probability(0), 1.0e-12);
        assertEquals(0.5, classifier.getPriors().probability(1), 1.0e-12);
        assertEquals("A", classifier.classify(new Instance(new FeatureVector(data, new int[]{x}), null, "q", null))
                .getLabeling().getBestLabel().toString());
    }

    @Test
    public void zeroNormInstancesAreIgnoredCompletely() {
        Alphabet data = new Alphabet();
        int x = data.lookupIndex("x");
        int y = data.lookupIndex("y");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("A");
        labels.lookupLabel("B");
        Pipe pipe = new Noop(data, labels);
        Instance valid = new Instance(new FeatureVector(data, new int[]{x}), labels.lookupLabel("A"), "valid", null);
        Instance query = new Instance(new FeatureVector(data, new int[]{y}), null, "q", null);
        NaiveBayesTrainer baselineTrainer = new NaiveBayesTrainer(pipe);
        NaiveBayes baseline = baselineTrainer.trainIncremental(valid);
        NaiveBayesTrainer noisyTrainer = new NaiveBayesTrainer(pipe);
        noisyTrainer.trainIncremental(valid);
        NaiveBayes noisy = noisyTrainer.trainIncremental(
                new Instance(new FeatureVector(data, new int[0]), labels.lookupLabel("B"), "empty", null));

        double expected = baseline.classify(query).getLabeling().value(labels.lookupLabel("A"));
        double actual = noisy.classify(query).getLabeling().value(labels.lookupLabel("A"));

        assertEquals(expected, actual, 1.0e-12);
    }

    @Test
    public void unlabeledIncrementIsRejectedByAlphabetValidationAfterInitialization() {
        Alphabet data = new Alphabet();
        int x = data.lookupIndex("x");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("A");
        Pipe pipe = new Noop(data, labels);
        NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
        trainer.trainIncremental(new Instance(new FeatureVector(data, new int[]{x}), labels.lookupLabel("A"), "labeled", null));

        try {
            trainer.trainIncremental(new Instance(new FeatureVector(data, new int[]{x}), null, "unlabeled", null));
            fail("An unlabeled instance has no target alphabet and fails the alphabet contract");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("alphabets do not match"));
        }
    }

    @Test
    public void fractionalLabelingContributesFractionalCounts() {
        Alphabet data = new Alphabet();
        int token = data.lookupIndex("token");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("A");
        labels.lookupLabel("B");
        LabelVector softTarget = new LabelVector(labels, new double[]{0.75, 0.25});
        NaiveBayesTrainer trainer = new NaiveBayesTrainer();

        NaiveBayes classifier = trainer.trainIncremental(new Instance(
                new FeatureVector(data, new int[]{token}), softTarget, "soft", null));
        Instance query = new Instance(new FeatureVector(data, new int[0]), null, "q", null);

        assertEquals(1.75 / 3.0, classifier.classify(query).getLabeling().value(labels.lookupLabel("A")), 1.0e-12);
        assertEquals(1.25 / 3.0, classifier.classify(query).getLabeling().value(labels.lookupLabel("B")), 1.0e-12);
    }

    @Test
    public void incrementalTrainingExpandsForNewTargetLabels() {
        Alphabet data = new Alphabet();
        int x = data.lookupIndex("x");
        int y = data.lookupIndex("y");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("A");
        NaiveBayesTrainer trainer = new NaiveBayesTrainer();

        trainer.trainIncremental(new Instance(new FeatureVector(data, new int[]{x}), labels.lookupLabel("A"), "a", null));
        labels.lookupLabel("B");
        NaiveBayes expanded = trainer.trainIncremental(new Instance(
                new FeatureVector(data, new int[]{y}, new double[]{5.0}), labels.lookupLabel("B"), "b", null));

        assertEquals("B", expanded.classify(new Instance(new FeatureVector(data, new int[]{y}), null, "q", null))
                .getLabeling().getBestLabel().toString());
        assertEquals(2, expanded.getLabelAlphabet().size());
    }

    @Test
    public void mismatchedAlphabetIsRejectedDuringIncrementalTraining() {
        Alphabet firstData = new Alphabet();
        int firstFeature = firstData.lookupIndex("same-name");
        LabelAlphabet firstLabels = new LabelAlphabet();
        firstLabels.lookupLabel("A");
        NaiveBayesTrainer trainer = new NaiveBayesTrainer();
        trainer.trainIncremental(new Instance(new FeatureVector(firstData, new int[]{firstFeature}), firstLabels.lookupLabel("A"), "first", null));
        Alphabet otherData = new Alphabet();
        int otherFeature = otherData.lookupIndex("same-name");
        LabelAlphabet otherLabels = new LabelAlphabet();
        otherLabels.lookupLabel("A");

        try {
            trainer.trainIncremental(new Instance(new FeatureVector(otherData, new int[]{otherFeature}), otherLabels.lookupLabel("A"), "other", null));
            fail("Distinct alphabet objects must be rejected");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("alphabets do not match"));
        }
    }

    @Test
    public void mismatchedPipeIdentityIsRejectedEvenWithSharedAlphabets() {
        Alphabet data = new Alphabet();
        int x = data.lookupIndex("x");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("A");
        Pipe firstPipe = new Noop(data, labels);
        Pipe secondPipe = new Noop(data, labels);
        InstanceList first = new InstanceList(firstPipe);
        first.add(new Instance(new FeatureVector(data, new int[]{x}), labels.lookupLabel("A"), "first", null));
        InstanceList second = new InstanceList(secondPipe);
        second.add(new Instance(new FeatureVector(data, new int[]{x}), labels.lookupLabel("A"), "second", null));
        NaiveBayesTrainer trainer = new NaiveBayesTrainer();
        trainer.trainIncremental(first);

        try {
            trainer.trainIncremental(second);
            fail("A different pipe instance must be rejected");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("pipe does not match"));
        }
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyTrainingListIsRejected() {
        Alphabet data = new Alphabet();
        LabelAlphabet labels = new LabelAlphabet();
        InstanceList empty = new InstanceList(new Noop(data, labels));

        new NaiveBayesTrainer().train(empty);
    }

    @Test
    public void classifierConstructorAdoptsClassifierAndLocksConfiguration() {
        Alphabet data = new Alphabet();
        int x = data.lookupIndex("x");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("A");
        Pipe pipe = new Noop(data, labels);
        InstanceList training = new InstanceList(pipe);
        training.add(new Instance(new FeatureVector(data, new int[]{x}), labels.lookupLabel("A"), "a", null));
        NaiveBayes original = new NaiveBayesTrainer().train(training);

        NaiveBayesTrainer trainer = new NaiveBayesTrainer(original);

        assertSame(original, trainer.getClassifier());
        assertSame(data, trainer.getAlphabet());
        assertSame(labels, trainer.getAlphabets()[1]);
        try {
            trainer.setFeatureMultinomialEstimator(new Multinomial.MLEstimator());
            fail("An initialized trainer must reject estimator replacement");
        } catch (IllegalStateException expected) {
            assertTrue(expected.getMessage().contains("incrementalTrain"));
        }
    }

    @Test
    public void serializationPreservesCountsForFurtherIncrementalTraining() throws Exception {
        Alphabet data = new Alphabet();
        int x = data.lookupIndex("x");
        int y = data.lookupIndex("y");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("A");
        labels.lookupLabel("B");
        NaiveBayesTrainer trainer = new NaiveBayesTrainer();
        trainer.trainIncremental(new Instance(new FeatureVector(data, new int[]{x}, new double[]{4.0}), labels.lookupLabel("A"), "a", null));
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ObjectOutputStream output = new ObjectOutputStream(bytes);
        output.writeObject(trainer);
        output.close();
        ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()));
        NaiveBayesTrainer restored = (NaiveBayesTrainer) input.readObject();
        input.close();

        assertNull(restored.getClassifier());
        NaiveBayes classifier = restored.trainIncremental(new Instance(
                new FeatureVector(restored.getAlphabet(), new int[]{y}, new double[]{4.0}),
                ((LabelAlphabet) restored.getAlphabets()[1]).lookupLabel("B"), "b", null));

        assertEquals("A", classifier.classify(new Instance(
                new FeatureVector(restored.getAlphabet(), new int[]{x}), null, "qx", null)).getLabeling().getBestLabel().toString());
        assertEquals("B", classifier.classify(new Instance(
                new FeatureVector(restored.getAlphabet(), new int[]{y}), null, "qy", null)).getLabeling().getBestLabel().toString());
    }
}
