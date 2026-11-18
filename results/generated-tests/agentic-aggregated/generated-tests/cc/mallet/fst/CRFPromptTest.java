package cc.mallet.fst;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import org.junit.Test;

import cc.mallet.pipe.Noop;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.FeatureVectorSequence;
import cc.mallet.types.IndexedSparseVector;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Sequence;
import cc.mallet.types.SparseVector;

public class CRFPromptTest {

    @Test
    public void alphabetConstructorExposesAlphabetsAndStopsInputGrowth() {
        Alphabet input = new Alphabet();
        input.lookupIndex("known");
        Alphabet output = new Alphabet();
        output.lookupIndex("A");

        CRF crf = new CRF(input, output);

        assertSame(input, crf.getInputAlphabet());
        assertSame(output, crf.getOutputAlphabet());
        assertTrue(input.growthStopped());
        assertEquals(-1, input.lookupIndex("unseen"));
        assertTrue(crf.isTrainable());
        assertEquals(0, crf.numStates());
    }

    @Test
    public void pipeConstructorUsesInputPipeAlphabetsAndPreservesPipes() {
        Alphabet input = new Alphabet();
        Alphabet output = new Alphabet();
        Noop inputPipe = new Noop(input, output);
        Noop outputPipe = new Noop(output, output);

        CRF crf = new CRF(inputPipe, outputPipe);

        assertSame(input, crf.getInputAlphabet());
        assertSame(output, crf.getOutputAlphabet());
        assertSame(inputPipe, crf.getInputPipe());
        assertSame(outputPipe, crf.getOutputPipe());
        assertFalse(input.growthStopped());
    }

    @Test
    public void fullyConnectedStatesExposeTopologyLabelsAndWeightNames() {
        Alphabet input = new Alphabet(new Object[] {"f"});
        Alphabet output = new Alphabet(new Object[] {"A", "B"});
        CRF crf = new CRF(input, output);

        crf.addFullyConnectedStatesForLabels();

        assertEquals(2, crf.numStates());
        CRF.State a = crf.getState("A");
        CRF.State b = crf.getState("B");
        assertSame(a, crf.getState(0));
        assertSame(b, crf.getState(1));
        assertEquals(2, a.numDestinations());
        assertEquals("A", a.getLabelName(0));
        assertEquals("B", a.getLabelName(1));
        assertSame(a, a.getDestinationState(0));
        assertSame(b, a.getDestinationState(1));
        assertArrayEquals(new String[] {"A->A:A"}, a.getWeightNames(0));
        assertArrayEquals(new String[] {"A->B:B"}, a.getWeightNames(1));
        assertEquals("A->A:A", crf.getWeightsName(crf.getWeightsIndex("A->A:A")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void duplicateStateNameIsRejected() {
        CRF crf = new CRF(new Alphabet(), new Alphabet());
        crf.addState("same", new String[] {"same"});
        crf.addState("same", new String[] {"same"});
    }

    @Test(expected = IllegalArgumentException.class)
    public void unresolvedDestinationFailsWhenItIsResolved() {
        CRF crf = new CRF(new Alphabet(), new Alphabet());
        crf.addState("source", 0.0, 0.0, new String[] {"missing"},
                new String[] {"label"});

        crf.getState("source").getDestinationState(0);
    }

    @Test
    public void startStateAndSetAsStartStateUpdateInitialWeights() {
        Alphabet output = new Alphabet(new Object[] {"A", "B"});
        CRF crf = new CRF(new Alphabet(), output);
        crf.addFullyConnectedStatesForLabels();
        int before = crf.getWeightsValueChangeStamp();

        crf.addStartState("BEGIN");

        assertEquals(3, crf.numStates());
        assertEquals(Transducer.IMPOSSIBLE_WEIGHT, crf.getState("A").getInitialWeight(), 0.0);
        assertEquals(Transducer.IMPOSSIBLE_WEIGHT, crf.getState("B").getInitialWeight(), 0.0);
        assertEquals(0.0, crf.getState("BEGIN").getInitialWeight(), 0.0);
        assertEquals(2, crf.getState("BEGIN").numDestinations());
        crf.setAsStartState(crf.getState("B"));
        assertEquals(0.0, crf.getState("B").getInitialWeight(), 0.0);
        assertEquals(Transducer.IMPOSSIBLE_WEIGHT, crf.getState("BEGIN").getInitialWeight(), 0.0);
        assertTrue(crf.getWeightsValueChangeStamp() > before);
    }

    @Test
    public void initialStateIteratorContainsInitiallyPossibleState() {
        CRF crf = new CRF(new Alphabet(), new Alphabet());
        crf.addState("never", Transducer.IMPOSSIBLE_WEIGHT, 0.0,
                new String[] {"never"}, new String[] {"N"});
        crf.addState("possible", -2.0, 0.0,
                new String[] {"possible"}, new String[] {"P"});

        Iterator<?> iterator = crf.initialStateIterator();

        assertTrue(iterator.hasNext());
        assertSame(crf.getState("possible"), iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void denseParameterMutationChangesFeatureAndDefaultScores() {
        Alphabet input = new Alphabet(new Object[] {"f0", "f1"});
        Alphabet output = new Alphabet(new Object[] {"A"});
        CRF crf = new CRF(input, output);
        crf.addState("A", new String[] {"A"});
        crf.setWeightsDimensionDensely();
        int oldStamp = crf.getWeightsValueChangeStamp();

        crf.setParameter(0, 0, 0, 2.5);
        crf.setParameter(0, 0, -1, -0.75);

        assertEquals(2.5, crf.getParameter(0, 0, 0), 0.0);
        assertEquals(-0.75, crf.getParameter(0, 0, -1), 0.0);
        assertEquals(0.0, crf.getParameter(0, 0, 1), 0.0);
        assertEquals(oldStamp + 2, crf.getWeightsValueChangeStamp());
        assertEquals(2.5 + 0.75, crf.getParametersAbsNorm(), 0.000001);
    }

    @Test
    public void transitionIteratorAddsMultipleWeightGroupsAndFiltersByOutput() {
        Alphabet input = new Alphabet(new Object[] {"f0"});
        Alphabet output = new Alphabet(new Object[] {"yes", "no"});
        CRF crf = new CRF(input, output);
        crf.addState("S", 0.0, 0.0,
                new String[] {"S", "S"}, new String[] {"yes", "no"},
                new String[][] {{"shared", "bonus"}, {"negative"}});
        crf.setWeightsDimensionDensely();
        crf.setWeights("shared", new IndexedSparseVector(new int[] {0}, new double[] {2.0}));
        crf.setWeights("bonus", new IndexedSparseVector(new int[] {0}, new double[] {3.0}));
        crf.setDefaultWeight(crf.getWeightsIndex("shared"), 0.5);
        crf.setDefaultWeight(crf.getWeightsIndex("bonus"), 1.5);
        FeatureVector fv = new FeatureVector(input, new int[] {0}, new double[] {2.0});

        Transducer.TransitionIterator all = crf.getState("S").transitionIterator(fv, null);
        assertTrue(all.hasNext());
        assertSame(crf.getState("S"), all.nextState());
        assertEquals("yes", all.getOutput());
        assertSame(fv, all.getInput());
        assertEquals(12.0, all.getWeight(), 0.000001);
        assertTrue(all.hasNext());
        all.nextState();
        assertEquals("no", all.getOutput());
        assertFalse(all.hasNext());

        Transducer.TransitionIterator filtered = crf.getState("S").transitionIterator(fv, "no");
        assertTrue(filtered.hasNext());
        filtered.nextState();
        assertEquals(1, filtered.getIndex());
        assertEquals("no", filtered.getOutput());
        assertFalse(filtered.hasNext());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void sequenceTransitionIteratorRejectsEpsilonPosition() {
        Alphabet input = new Alphabet(new Object[] {"f"});
        CRF crf = new CRF(input, new Alphabet());
        crf.addState("S", new String[] {"S"});
        FeatureVectorSequence sequence = new FeatureVectorSequence(
                new FeatureVector[] {new FeatureVector(input, new int[] {0})});

        crf.getState("S").transitionIterator(sequence, -1, null, 0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void sequenceTransitionIteratorRejectsNullInput() {
        CRF crf = new CRF(new Alphabet(), new Alphabet());
        crf.addState("S", new String[] {"S"});

        crf.getState("S").transitionIterator(null, 0, null, 0);
    }

    @Test
    public void missingTransitionIsRejectedByParameterAccessors() {
        CRF crf = new CRF(new Alphabet(new Object[] {"f"}),
                new Alphabet(new Object[] {"A", "B"}));
        crf.addState("A", new String[] {"A"});
        crf.addState("B", new String[] {"B"});
        crf.setWeightsDimensionDensely();

        try {
            crf.setParameter(0, 1, 0, 1.0);
            fail("Expected missing transition to be rejected");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("No transtition"));
        }
        try {
            crf.getParameter(1, 0, 0);
            fail("Expected missing transition to be rejected");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("No transtition"));
        }
    }

    @Test
    public void weightFreezingWorksByIndexAndName() {
        CRF crf = new CRF(new Alphabet(), new Alphabet());
        crf.addState("S", new String[] {"S"});
        int index = crf.getWeightsIndex("S->S:S");

        assertFalse(crf.isWeightsFrozen(index));
        crf.freezeWeights(index);
        assertTrue(crf.isWeightsFrozen(index));
        crf.unfreezeWeights("S->S:S");
        assertFalse(crf.isWeightsFrozen(index));
        crf.freezeWeights("S->S:S");
        assertTrue(crf.isWeightsFrozen(index));
    }

    @Test
    public void sparseWeightReplacementSupportsNameAndChecksBounds() {
        Alphabet input = new Alphabet(new Object[] {"f"});
        CRF crf = new CRF(input, new Alphabet());
        crf.addState("S", new String[] {"S"});
        int index = crf.getWeightsIndex("S->S:S");
        SparseVector replacement = new IndexedSparseVector(new int[] {0}, new double[] {4.0});

        crf.setWeights("S->S:S", replacement);

        assertSame(replacement, crf.getWeights(index));
        assertSame(replacement, crf.getWeights("S->S:S"));
        try {
            crf.setWeights(-1, replacement);
            fail("Expected negative index to fail");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("out of bounds"));
        }
        try {
            crf.setWeights(3, replacement);
            fail("Expected large index to fail");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("out of bounds"));
        }
    }

    @Test
    public void numParametersReflectsStatesDefaultsAndDenseFeatures() {
        Alphabet input = new Alphabet(new Object[] {"f0", "f1", "f2"});
        Alphabet output = new Alphabet(new Object[] {"A", "B"});
        CRF crf = new CRF(input, output);
        crf.addFullyConnectedStatesForLabels();

        crf.setWeightsDimensionDensely();

        assertEquals(20, crf.getNumParameters());
        assertEquals(crf.getNumParameters(), crf.getParameters().getNumFactors());
        assertEquals(4, crf.getWeights().length);
        assertEquals(4, crf.getDefaultWeights().length);
    }

    @Test
    public void factorsCanFlattenScaleZeroAndEvaluatePriors() {
        Alphabet input = new Alphabet(new Object[] {"f"});
        CRF crf = new CRF(input, new Alphabet());
        crf.addState("S", 1.0, -2.0, new String[] {"S"}, new String[] {"L"});
        crf.setWeightsDimensionDensely();
        crf.setParameter(0, 0, -1, 3.0);
        crf.setParameter(0, 0, 0, -4.0);
        CRF.Factors original = crf.getParameters();
        CRF.Factors copy = new CRF.Factors(original, true);
        double[] flattened = new double[copy.getNumFactors()];

        copy.getParameters(flattened);

        assertArrayEquals(new double[] {1.0, -2.0, 3.0, -4.0}, flattened, 0.0);
        assertTrue(copy.structureMatches(original));
        assertEquals(10.0, copy.getParametersAbsNorm(), 0.000001);
        assertEquals(-15.0, copy.gaussianPrior(1.0), 0.000001);
        copy.plusEquals(original, 0.5);
        assertEquals(1.5, copy.getParameter(0), 0.000001);
        assertEquals(-6.0, copy.getParameter(3), 0.000001);
        copy.zero();
        assertEquals(0.0, copy.getParametersAbsNorm(), 0.0);
        copy.assertNotNaN();
        copy.assertNotNaNOrInfinite();
    }

    @Test
    public void factorsRejectWrongFlatteningBufferAndHighIndex() {
        CRF crf = new CRF(new Alphabet(), new Alphabet());
        crf.addState("S", new String[] {"S"});
        CRF.Factors factors = crf.getParameters();

        try {
            factors.getParameters(new double[1]);
            fail("Expected wrong buffer length to fail");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("Expected size"));
        }
        try {
            factors.getParameter(99);
            fail("Expected high parameter index to fail");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("index too high"));
        }
    }

    @Test
    public void copyConstructorCopiesTopologyAndSeparatesParameterValues() {
        Alphabet input = new Alphabet(new Object[] {"f"});
        Alphabet output = new Alphabet(new Object[] {"A"});
        CRF original = new CRF(new Noop(input, output), new Noop(output, output));
        original.addState("A", new String[] {"A"});
        original.setWeightsDimensionDensely();
        original.setParameter(0, 0, 0, 2.0);

        CRF copy = new CRF(original);
        copy.setParameter(0, 0, 0, 7.0);

        assertEquals(1, copy.numStates());
        assertEquals("A", copy.getState(0).getName());
        assertNotSame(original.getState(0), copy.getState(0));
        assertEquals(2.0, original.getParameter(0, 0, 0), 0.0);
        assertEquals(7.0, copy.getParameter(0, 0, 0), 0.0);
        assertNotSame(original.getParameters(), copy.getParameters());
    }

    @Test
    public void serializationRoundTripPreservesTopologyAndScores() throws Exception {
        Alphabet input = new Alphabet(new Object[] {"f"});
        CRF crf = new CRF(input, new Alphabet());
        crf.addState("S", new String[] {"S"});
        crf.setWeightsDimensionDensely();
        crf.setParameter(0, 0, 0, 6.25);
        crf.setParameter(0, 0, -1, -1.5);
        File file = File.createTempFile("crf-prompt-test", ".ser");
        file.deleteOnExit();

        crf.write(file);
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
        CRF restored = (CRF) in.readObject();
        in.close();

        assertEquals(1, restored.numStates());
        assertEquals("S", restored.getState(0).getName());
        assertSame(restored, restored.getState("S").getTransducer());
        assertEquals(6.25, restored.getParameter(0, 0, 0), 0.0);
        assertEquals(-1.5, restored.getParameter(0, 0, -1), 0.0);
    }

    @Test
    public void labeledTrainingDataSelectsOnlyObservedFeatureDimensions() {
        Alphabet input = new Alphabet(new Object[] {"f0", "f1", "f2"});
        Alphabet output = new Alphabet(new Object[] {"A"});
        CRF crf = new CRF(input, output);
        crf.addState("A", new String[] {"A"});
        FeatureVector first = new FeatureVector(input, new int[] {0, 2}, new double[] {1.0, 1.0});
        FeatureVector second = new FeatureVector(input, new int[] {2}, new double[] {1.0});
        FeatureVectorSequence data = new FeatureVectorSequence(new FeatureVector[] {first, second});
        FeatureSequence target = new FeatureSequence(output, new int[] {0, 0});
        InstanceList training = new InstanceList(input, output);
        training.add(new Instance(data, target, "example", null));

        crf.setWeightsDimensionAsIn(training);

        SparseVector weights = crf.getWeights("A->A:A");
        assertEquals(2, weights.numLocations());
        assertEquals(0, weights.indexAtLocation(0));
        assertEquals(2, weights.indexAtLocation(1));
        assertEquals(5, crf.getNumParameters());
    }

    @Test
    public void predictionUsesTransitionParametersAcrossMultipleTokens() {
        Alphabet input = new Alphabet(new Object[] {"signal"});
        Alphabet output = new Alphabet(new Object[] {"A", "B"});
        CRF crf = new CRF(input, output);
        crf.addFullyConnectedStatesForLabels();
        crf.setWeightsDimensionDensely();
        crf.setParameter(0, 0, -1, -3.0);
        crf.setParameter(0, 1, -1, 4.0);
        crf.setParameter(1, 0, -1, -3.0);
        crf.setParameter(1, 1, -1, 4.0);
        FeatureVector token = new FeatureVector(input, new int[] {0});
        FeatureVectorSequence data = new FeatureVectorSequence(new FeatureVector[] {token, token});
        FeatureSequence target = new FeatureSequence(output, new int[] {0, 0});
        InstanceList testing = new InstanceList(input, output);
        testing.add(new Instance(data, target, "sequence", null));

        Sequence[] predictions = crf.predict(testing);

        assertEquals(1, predictions.length);
        assertEquals(2, predictions[0].size());
        assertEquals("B", predictions[0].get(0));
        assertEquals("B", predictions[0].get(1));
    }

    @Test
    public void connectedAsInBuildsOnlyObservedLabelTransitions() {
        Alphabet input = new Alphabet(new Object[] {"f"});
        Alphabet output = new Alphabet(new Object[] {"A", "B", "C"});
        FeatureVector token = new FeatureVector(input, new int[] {0});
        FeatureVectorSequence data = new FeatureVectorSequence(new FeatureVector[] {token, token, token});
        FeatureSequence target = new FeatureSequence(output, new int[] {0, 1, 2});
        InstanceList training = new InstanceList(input, output);
        training.add(new Instance(data, target, "abc", null));
        CRF crf = new CRF(input, output);

        crf.addStatesForLabelsConnectedAsIn(training);

        assertEquals(3, crf.numStates());
        assertEquals(1, crf.getState("A").numDestinations());
        assertSame(crf.getState("B"), crf.getState("A").getDestinationState(0));
        assertEquals("B", crf.getState("A").getLabelName(0));
        assertEquals(1, crf.getState("B").numDestinations());
        assertSame(crf.getState("C"), crf.getState("B").getDestinationState(0));
        assertEquals(0, crf.getState("C").numDestinations());
    }

    @Test
    public void biLabelAndSelfTransitionTopologiesUseExpectedNamesAndLabels() {
        Alphabet output = new Alphabet(new Object[] {"A", "B"});
        CRF bi = new CRF(new Alphabet(), output);
        bi.addFullyConnectedStatesForBiLabels();

        assertEquals(4, bi.numStates());
        assertEquals(2, bi.getState("A,B").numDestinations());
        assertEquals("A", bi.getState("A,B").getLabelName(0));
        assertEquals("B", bi.getState("A,B").getLabelName(1));
        assertSame(bi.getState("B,A"), bi.getState("A,B").getDestinationState(0));
        assertSame(bi.getState("B,B"), bi.getState("A,B").getDestinationState(1));

        CRF self = new CRF(new Alphabet(), output);
        self.addSelfTransitioningStateForAllLabels("only");
        assertEquals(1, self.numStates());
        assertEquals(2, self.getState("only").numDestinations());
        assertSame(self.getState("only"), self.getState("only").getDestinationState(0));
        assertSame(self.getState("only"), self.getState("only").getDestinationState(1));
    }

    @Test
    public void orderNValidationAndZeroOrderTopologyAreHandled() {
        Alphabet input = new Alphabet(new Object[] {"f"});
        Alphabet output = new Alphabet(new Object[] {"A", "B"});
        InstanceList training = new InstanceList(input, output);
        CRF invalid = new CRF(input, output);

        try {
            invalid.addOrderNStates(training, new int[] {0, 1}, new boolean[] {true},
                    null, null, null, true);
            fail("Expected mismatched defaults to fail");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("Defaults"));
        }
        try {
            invalid.addOrderNStates(training, new int[] {1, 1}, null,
                    null, null, null, true);
            fail("Expected nonascending orders to fail");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("ascending"));
        }

        CRF zeroOrder = new CRF(input, output);
        assertEquals("START", zeroOrder.addOrderNStates(training, null, null,
                "START", null, null, true));
        assertEquals(3, zeroOrder.numStates());
        assertEquals(3, zeroOrder.getState("A").numDestinations());
        assertNotNull(zeroOrder.getState("START"));
        assertArrayEquals(new String[] {"A"}, zeroOrder.getState("B").getWeightNames(0));
    }

    @Test
    public void printDescribesStatesTransitionsAndWeights() throws Exception {
        Alphabet input = new Alphabet(new Object[] {"f"});
        CRF crf = new CRF(input, new Alphabet());
        crf.addState("S", new String[] {"S"});
        crf.setWeightsDimensionDensely();
        crf.setParameter(0, 0, 0, 1.25);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(bytes, true);

        crf.print(writer);
        writer.flush();
        String text = bytes.toString(StandardCharsets.UTF_8.name());

        assertTrue(text.contains("STATE NAME=\"S\""));
        assertTrue(text.contains("S->S:S"));
        assertTrue(text.contains("1.25"));
    }
}
