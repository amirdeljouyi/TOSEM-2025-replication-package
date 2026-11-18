package cc.mallet.fst;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Random;
import java.util.regex.Pattern;

import org.junit.Test;

import cc.mallet.pipe.Noop;
import cc.mallet.types.Alphabet;
import cc.mallet.types.ArraySequence;
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Multinomial;

public class HMMPromptTest {

    @Test
    public void alphabetConstructorRetainsAlphabetsAndStopsInputGrowth() {
        Alphabet inputs = new Alphabet();
        inputs.lookupIndex("word");
        Alphabet outputs = new Alphabet();
        outputs.lookupIndex("N");

        HMM hmm = new HMM(inputs, outputs);

        assertSame(inputs, hmm.getInputAlphabet());
        assertSame(outputs, hmm.getOutputAlphabet());
        assertTrue(inputs.growthStopped());
        assertFalse(outputs.growthStopped());
        assertTrue(hmm.isTrainable());
        assertEquals(0, hmm.numStates());
        assertNull(hmm.getState("missing"));
    }

    @Test
    public void pipeConstructorUsesPipeDataAndTargetAlphabets() {
        Alphabet inputs = new Alphabet();
        Alphabet outputs = new Alphabet();
        Noop inputPipe = new Noop(inputs, outputs);
        Noop outputPipe = new Noop(new Alphabet(), new Alphabet());

        HMM hmm = new HMM(inputPipe, outputPipe);

        assertSame(inputs, hmm.getInputAlphabet());
        assertSame(outputs, hmm.getOutputAlphabet());
        assertFalse(inputs.growthStopped());
    }

    @Test
    public void addStateExposesMetadataAndOnlyPossibleInitialStates() {
        Alphabet inputs = new Alphabet();
        inputs.lookupIndex("x");
        Alphabet outputs = new Alphabet();
        outputs.lookupIndex("go");
        HMM hmm = new HMM(inputs, outputs);

        hmm.addState("start", 0.0, -2.0, new String[] { "end" }, new String[] { "go" });
        hmm.addState("end", Transducer.IMPOSSIBLE_WEIGHT, 1.5, new String[] { "end" }, new String[] { "go" });

        assertEquals(2, hmm.numStates());
        HMM.State start = hmm.getState("start");
        HMM.State end = (HMM.State) hmm.getState(1);
        assertSame(start, hmm.getState(0));
        assertEquals("start", start.getName());
        assertEquals(0, start.getIndex());
        assertEquals(1, end.getIndex());
        assertSame(hmm, start.getTransducer());
        assertEquals(0.0, start.getInitialWeight(), 0.0);
        assertEquals(-2.0, start.getFinalWeight(), 0.0);
        start.setInitialWeight(-0.25);
        start.setFinalWeight(-0.75);
        assertEquals(-0.25, start.getInitialWeight(), 0.0);
        assertEquals(-0.75, start.getFinalWeight(), 0.0);
        Iterator<?> initial = hmm.initialStateIterator();
        assertTrue(initial.hasNext());
        assertSame(start, initial.next());
        assertFalse(initial.hasNext());
        assertSame(end, start.getDestinationState(0));
        assertSame(end, start.getDestinationState(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void duplicateStateNameIsRejected() {
        Alphabet inputs = new Alphabet();
        inputs.lookupIndex("x");
        Alphabet outputs = new Alphabet();
        outputs.lookupIndex("L");
        HMM hmm = new HMM(inputs, outputs);
        hmm.addState("same", new String[] { "same" });

        hmm.addState("same", new String[] { "same" });
    }

    @Test
    public void fullyConnectedLabelStatesProduceAllTransitions() {
        Alphabet inputs = new Alphabet();
        inputs.lookupIndex("x");
        Alphabet outputs = new Alphabet();
        outputs.lookupIndex("A");
        outputs.lookupIndex("B");
        HMM hmm = new HMM(inputs, outputs);
        hmm.addFullyConnectedStatesForLabels();
        hmm.initTransitions(null, 9.0);
        hmm.initEmissions(null, 9.0);
        FeatureSequence input = new FeatureSequence(inputs, new int[] { 0 });

        Transducer.TransitionIterator transitions = hmm.getState("A").transitionIterator(input, 0);

        assertEquals(2, hmm.numStates());
        assertTrue(transitions.hasNext());
        assertEquals("A", transitions.nextState().getName());
        assertEquals("A", transitions.getOutput());
        assertEquals(Integer.valueOf(0), transitions.getInput());
        assertSame(hmm.getState("A"), transitions.getSourceState());
        assertSame(hmm.getState("A"), transitions.getDestinationState());
        assertEquals(Math.log(0.5), transitions.getWeight(), 0.0000001);
        assertTrue(transitions.hasNext());
        assertEquals("B", transitions.nextState().getName());
        assertEquals("B", transitions.getOutput());
        assertEquals(Math.log(0.5), transitions.getWeight(), 0.0000001);
        assertFalse(transitions.hasNext());
    }

    @Test
    public void outputSequenceFiltersTransitionsAndPreservesIteratorMetadata() {
        Alphabet inputs = new Alphabet();
        inputs.lookupIndex("x");
        inputs.lookupIndex("y");
        Alphabet outputs = new Alphabet();
        outputs.lookupIndex("A");
        outputs.lookupIndex("B");
        HMM hmm = new HMM(inputs, outputs);
        hmm.addFullyConnectedStates(new String[] { "A", "B" });
        hmm.initTransitions(null, 0.0);
        hmm.initEmissions(null, 0.0);
        FeatureSequence input = new FeatureSequence(inputs, new int[] { 1 });
        FeatureSequence output = new FeatureSequence(outputs, new int[] { 1 });

        Transducer.TransitionIterator transitions = hmm.getState("A").transitionIterator(input, 0, output, 0);

        assertTrue(transitions.hasNext());
        assertSame(hmm.getState("B"), transitions.next());
        assertEquals(1, transitions.getIndex());
        assertEquals("B", transitions.getOutput());
        assertEquals(Integer.valueOf(1), transitions.getInput());
        assertSame(hmm, transitions.getTransducer());
        assertEquals(1, transitions.getInputPositionIncrement());
        assertEquals(1, transitions.getOutputPositionIncrement());
        assertEquals(Math.log(0.5) + Math.log(0.5), transitions.getWeight(), 0.0000001);
        assertFalse(transitions.hasNext());
        try {
            transitions.remove();
            fail("remove should be unsupported");
        } catch (UnsupportedOperationException expected) {
            assertNotNull(expected);
        }
    }

    @Test
    public void transitionIteratorRejectsUnsupportedInputsAndPositions() {
        Alphabet inputs = new Alphabet();
        inputs.lookupIndex("x");
        Alphabet outputs = new Alphabet();
        outputs.lookupIndex("A");
        HMM hmm = new HMM(inputs, outputs);
        hmm.addState("A", new String[] { "A" });
        FeatureSequence input = new FeatureSequence(inputs, new int[] { 0 });

        try {
            hmm.getState("A").transitionIterator(input, -1, null, 0);
            fail("negative positions represent unsupported epsilon transitions");
        } catch (UnsupportedOperationException expected) {
            assertTrue(expected.getMessage().contains("Epsilon"));
        }
        try {
            hmm.getState("A").transitionIterator(null, 0, null, 0);
            fail("null input requests unsupported generation");
        } catch (UnsupportedOperationException expected) {
            assertTrue(expected.getMessage().contains("generative"));
        }
        try {
            hmm.getState("A").transitionIterator(new ArraySequence<String>(new String[] { "x" }), 0, null, 0);
            fail("only feature sequences are supported");
        } catch (UnsupportedOperationException expected) {
            assertTrue(expected.getMessage().contains("FeatureSequence"));
        }
    }

    @Test
    public void connectedLabelStatesReflectOnlyObservedAdjacentLabels() {
        Alphabet inputs = new Alphabet();
        inputs.lookupIndex("x");
        Alphabet outputs = new Alphabet();
        outputs.lookupIndex("A");
        outputs.lookupIndex("B");
        outputs.lookupIndex("C");
        FeatureSequence data = new FeatureSequence(inputs, new int[] { 0, 0, 0 });
        FeatureSequence target = new FeatureSequence(outputs, new int[] { 0, 1, 2 });
        InstanceList training = new InstanceList(inputs, outputs);
        training.add(new Instance(data, target, "one", null));
        HMM hmm = new HMM(inputs, outputs);

        hmm.addStatesForLabelsConnectedAsIn(training);
        hmm.initTransitions(null, 0.0);
        hmm.initEmissions(null, 0.0);

        Transducer.TransitionIterator fromA = hmm.getState("A").transitionIterator(data, 0);
        assertTrue(fromA.hasNext());
        assertEquals("B", fromA.nextState().getName());
        assertEquals("B", fromA.getOutput());
        assertFalse(fromA.hasNext());
        Transducer.TransitionIterator fromB = hmm.getState("B").transitionIterator(data, 1);
        assertEquals("C", fromB.nextState().getName());
        assertFalse(fromB.hasNext());
        assertFalse(hmm.getState("C").transitionIterator(data, 2).hasNext());
    }

    @Test
    public void alternativeConnectedBuildersCreateEquivalentObservableTopology() {
        Alphabet inputs = new Alphabet();
        inputs.lookupIndex("x");
        Alphabet outputs = new Alphabet();
        outputs.lookupIndex("A");
        outputs.lookupIndex("B");
        FeatureSequence data = new FeatureSequence(inputs, new int[] { 0, 0 });
        FeatureSequence target = new FeatureSequence(outputs, new int[] { 0, 1 });
        InstanceList training = new InstanceList(inputs, outputs);
        training.add(new Instance(data, target, null, null));
        HMM half = new HMM(inputs, outputs);
        HMM threeQuarter = new HMM(inputs, outputs);

        half.addStatesForHalfLabelsConnectedAsIn(training);
        threeQuarter.addStatesForThreeQuarterLabelsConnectedAsIn(training);
        half.initTransitions(null, 0.0);
        half.initEmissions(null, 0.0);
        threeQuarter.initTransitions(null, 0.0);
        threeQuarter.initEmissions(null, 0.0);

        Transducer.TransitionIterator halfArc = half.getState("A").transitionIterator(data, 0);
        Transducer.TransitionIterator threeQuarterArc = threeQuarter.getState("A").transitionIterator(data, 0);
        assertEquals("B", halfArc.nextState().getName());
        assertEquals("B", halfArc.getOutput());
        assertFalse(halfArc.hasNext());
        assertEquals("B", threeQuarterArc.nextState().getName());
        assertEquals("B", threeQuarterArc.getOutput());
        assertFalse(threeQuarterArc.hasNext());
    }

    @Test
    public void higherOrderConvenienceBuildersCreateExpectedStateSpaces() {
        Alphabet inputs = new Alphabet();
        inputs.lookupIndex("x");
        Alphabet outputs = new Alphabet();
        outputs.lookupIndex("A");
        outputs.lookupIndex("B");
        HMM bi = new HMM(inputs, outputs);
        HMM tri = new HMM(inputs, outputs);

        bi.addFullyConnectedStatesForBiLabels();
        tri.addFullyConnectedStatesForTriLabels();

        assertEquals(4, bi.numStates());
        assertNotNull(bi.getState("A,A"));
        assertNotNull(bi.getState("A,B"));
        assertNotNull(bi.getState("B,A"));
        assertNotNull(bi.getState("B,B"));
        assertEquals(8, tri.numStates());
        assertNotNull(tri.getState("A,A,A"));
        assertNotNull(tri.getState("A,B,A"));
        assertNotNull(tri.getState("B,B,B"));
    }

    @Test
    public void fullyConnectedThreeQuarterBuilderCreatesLabelStatesAndArcs() {
        Alphabet inputs = new Alphabet();
        inputs.lookupIndex("x");
        Alphabet outputs = new Alphabet();
        outputs.lookupIndex("A");
        outputs.lookupIndex("B");
        InstanceList unusedTrainingArgument = new InstanceList(inputs, outputs);
        HMM hmm = new HMM(inputs, outputs);

        hmm.addFullyConnectedStatesForThreeQuarterLabels(unusedTrainingArgument);
        hmm.initTransitions(null, 0.0);
        hmm.initEmissions(null, 0.0);
        FeatureSequence data = new FeatureSequence(inputs, new int[] { 0 });

        assertEquals(2, hmm.numStates());
        Transducer.TransitionIterator arcs = hmm.getState("B").transitionIterator(data, 0);
        assertEquals("A", arcs.nextState().getName());
        assertEquals("A", arcs.getOutput());
        assertEquals("B", arcs.nextState().getName());
        assertEquals("B", arcs.getOutput());
        assertFalse(arcs.hasNext());
    }

    @Test
    public void connectedBiLabelBuilderUsesObservedSecondOrderArcs() {
        Alphabet inputs = new Alphabet();
        inputs.lookupIndex("x");
        Alphabet outputs = new Alphabet();
        outputs.lookupIndex("A");
        outputs.lookupIndex("B");
        FeatureSequence data = new FeatureSequence(inputs, new int[] { 0, 0, 0 });
        FeatureSequence target = new FeatureSequence(outputs, new int[] { 0, 1, 0 });
        InstanceList training = new InstanceList(inputs, outputs);
        training.add(new Instance(data, target, null, null));
        HMM hmm = new HMM(inputs, outputs);

        hmm.addStatesForBiLabelsConnectedAsIn(training);
        hmm.initTransitions(null, 0.0);
        hmm.initEmissions(null, 0.0);

        assertEquals(2, hmm.numStates());
        assertNull(hmm.getState("A,A"));
        Transducer.TransitionIterator abArc = hmm.getState("A,B").transitionIterator(data, 0);
        assertEquals("B,A", abArc.nextState().getName());
        assertEquals("A", abArc.getOutput());
        assertFalse(abArc.hasNext());
        Transducer.TransitionIterator baArc = hmm.getState("B,A").transitionIterator(data, 1);
        assertEquals("A,B", baArc.nextState().getName());
        assertEquals("B", baArc.getOutput());
        assertFalse(baArc.hasNext());
    }

    @Test
    public void selfTransitioningStateEmitsEveryLabelToItself() {
        Alphabet inputs = new Alphabet();
        inputs.lookupIndex("x");
        Alphabet outputs = new Alphabet();
        outputs.lookupIndex("A");
        outputs.lookupIndex("B");
        HMM hmm = new HMM(inputs, outputs);
        hmm.addSelfTransitioningStateForAllLabels("only");
        hmm.initTransitions(null, 0.0);
        hmm.initEmissions(null, 0.0);
        FeatureSequence data = new FeatureSequence(inputs, new int[] { 0 });

        Transducer.TransitionIterator arcs = hmm.getState("only").transitionIterator(data, 0);
        assertSame(hmm.getState("only"), arcs.nextState());
        assertEquals("A", arcs.getOutput());
        assertSame(hmm.getState("only"), arcs.nextState());
        assertEquals("B", arcs.getOutput());
        assertFalse(arcs.hasNext());
    }

    @Test
    public void orderZeroBuilderUsesLabelsAsFullyConnectedStates() {
        Alphabet inputs = new Alphabet();
        inputs.lookupIndex("x");
        Alphabet outputs = new Alphabet();
        outputs.lookupIndex("A");
        outputs.lookupIndex("B");
        InstanceList training = new InstanceList(inputs, outputs);
        HMM hmm = new HMM(inputs, outputs);

        String start = hmm.addOrderNStates(training, null, null, "BEGIN", null, null, false);

        assertEquals("BEGIN", start);
        assertEquals(2, hmm.numStates());
        assertNotNull(hmm.getState("A"));
        assertNotNull(hmm.getState("B"));
    }

    @Test
    public void orderTwoBuilderHonorsForbiddenTransitions() {
        Alphabet inputs = new Alphabet();
        inputs.lookupIndex("x");
        Alphabet outputs = new Alphabet();
        outputs.lookupIndex("A");
        outputs.lookupIndex("B");
        InstanceList training = new InstanceList(inputs, outputs);
        HMM hmm = new HMM(inputs, outputs);

        String start = hmm.addOrderNStates(training, new int[] { 0, 2 }, new boolean[] { false, true },
                "START", Pattern.compile("B,A"), null, true);

        assertEquals("START,START", start);
        assertEquals(3, hmm.numStates());
        assertNotNull(hmm.getState("A,A"));
        assertNotNull(hmm.getState("A,B"));
        assertNotNull(hmm.getState("B,B"));
        assertNull(hmm.getState("B,A"));
    }

    @Test
    public void orderOneBuilderCanUseOnlyTrainingConnectionsAndAllowedPattern() {
        Alphabet inputs = new Alphabet();
        inputs.lookupIndex("x");
        Alphabet outputs = new Alphabet();
        outputs.lookupIndex("A");
        outputs.lookupIndex("B");
        FeatureSequence data = new FeatureSequence(inputs, new int[] { 0, 0, 0 });
        FeatureSequence target = new FeatureSequence(outputs, new int[] { 0, 1, 0 });
        InstanceList training = new InstanceList(inputs, outputs);
        training.add(new Instance(data, target, null, null));
        HMM hmm = new HMM(inputs, outputs);

        hmm.addOrderNStates(training, new int[] { 1 }, null, "S", null,
                Pattern.compile("A,B|B,A"), false);
        hmm.initTransitions(null, 0.0);
        hmm.initEmissions(null, 0.0);

        Transducer.TransitionIterator aArc = hmm.getState("A").transitionIterator(data, 0);
        assertEquals("B", aArc.nextState().getName());
        assertFalse(aArc.hasNext());
        Transducer.TransitionIterator bArc = hmm.getState("B").transitionIterator(data, 1);
        assertEquals("A", bArc.nextState().getName());
        assertFalse(bArc.hasNext());
    }

    @Test
    public void invalidOrderSpecificationsAreRejected() {
        Alphabet inputs = new Alphabet();
        inputs.lookupIndex("x");
        Alphabet outputs = new Alphabet();
        outputs.lookupIndex("A");
        InstanceList training = new InstanceList(inputs, outputs);
        HMM hmm = new HMM(inputs, outputs);

        try {
            hmm.addOrderNStates(training, new int[] { 0, 1 }, new boolean[] { true }, "S", null, null, true);
            fail("defaults length must match orders");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("Defaults"));
        }
        try {
            hmm.addOrderNStates(training, new int[] { 1, 1 }, null, "S", null, null, true);
            fail("orders must strictly increase");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("ascending"));
        }
        try {
            hmm.addOrderNStates(training, new int[] { -1 }, null, "S", null, null, true);
            fail("orders must be non-negative");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("non-negative"));
        }
    }

    @Test
    public void resetEstimateAndRandomInitializationProduceNormalizedModels() {
        Alphabet inputs = new Alphabet();
        inputs.lookupIndex("x");
        inputs.lookupIndex("y");
        Alphabet outputs = new Alphabet();
        outputs.lookupIndex("A");
        outputs.lookupIndex("B");
        HMM hmm = new HMM(inputs, outputs);
        hmm.addFullyConnectedStatesForLabels();

        hmm.reset();
        assertEquals(2, hmm.getEmissionMultinomial().length);
        assertEquals(2, hmm.getTransitionMultinomial().length);
        assertEquals(0.5, hmm.getEmissionMultinomial()[0].probability("x"), 0.0000001);
        assertEquals(0.5, hmm.getTransitionMultinomial()[1].probability("B"), 0.0000001);
        assertEquals(0.5, hmm.getInitialMultinomial().probability("A"), 0.0000001);

        hmm.estimate();
        assertEquals(0.5, hmm.getEmissionMultinomial()[1].probability("y"), 0.0000001);
        assertEquals(Math.log(0.5), hmm.getState("A").getInitialWeight(), 0.0000001);

        hmm.initTransitions(new Random(17), 2.0);
        hmm.initEmissions(new Random(23), 2.0);
        Multinomial initial = hmm.getInitialMultinomial();
        assertEquals(1.0, initial.probability("A") + initial.probability("B"), 0.0000001);
        assertEquals(1.0, hmm.getEmissionMultinomial()[0].probability("x")
                + hmm.getEmissionMultinomial()[0].probability("y"), 0.0000001);
        assertNotEquals(hmm.getEmissionMultinomial()[0].probability("x"),
                hmm.getEmissionMultinomial()[0].probability("y"), 0.0000001);
    }

    @Test
    public void trainingCountsInitialTransitionsAndDestinationEmissions() {
        Alphabet inputs = new Alphabet();
        inputs.lookupIndex("x");
        inputs.lookupIndex("y");
        Alphabet outputs = new Alphabet();
        outputs.lookupIndex("A");
        outputs.lookupIndex("B");
        FeatureSequence dataOne = new FeatureSequence(inputs, new int[] { 0, 1 });
        FeatureSequence targetOne = new FeatureSequence(outputs, new int[] { 0, 1 });
        FeatureSequence dataTwo = new FeatureSequence(inputs, new int[] { 0, 0 });
        FeatureSequence targetTwo = new FeatureSequence(outputs, new int[] { 0, 0 });
        InstanceList training = new InstanceList(inputs, outputs);
        training.add(new Instance(dataOne, targetOne, "one", null));
        training.add(new Instance(dataTwo, targetTwo, "two", null));
        HMM hmm = new HMM(inputs, outputs);
        hmm.addFullyConnectedStatesForLabels();

        assertTrue(hmm.train(training));

        assertNotNull(hmm.getInitialMultinomial());
        assertNotNull(hmm.getEmissionMultinomial());
        assertNotNull(hmm.getTransitionMultinomial());
        assertEquals(0.5, hmm.getInitialMultinomial().probability("A"), 0.0000001);
        assertEquals(0.5, hmm.getInitialMultinomial().probability("B"), 0.0000001);
        assertTrue(hmm.getTransitionMultinomial()[0].probability("A")
                > hmm.getTransitionMultinomial()[0].probability("B"));
        assertTrue(hmm.getEmissionMultinomial()[0].probability("x")
                > hmm.getEmissionMultinomial()[0].probability("y"));
        assertTrue(hmm.train(training, null, null));
        assertTrue(hmm.train(training, null, null, null));
    }

    @Test
    public void serializationRoundTripPreservesTopologyAndInitializedModels() throws Exception {
        Alphabet inputs = new Alphabet();
        inputs.lookupIndex("x");
        Alphabet outputs = new Alphabet();
        outputs.lookupIndex("A");
        outputs.lookupIndex("B");
        HMM original = new HMM(inputs, outputs);
        original.addFullyConnectedStatesForLabels();
        original.reset();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ObjectOutputStream output = new ObjectOutputStream(bytes);
        output.writeObject(original);
        output.close();

        ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()));
        HMM restored = (HMM) input.readObject();
        input.close();

        assertEquals(2, restored.numStates());
        assertEquals("A", restored.getState(0).getName());
        assertEquals("B", restored.getState(1).getName());
        assertSame(restored, restored.getState("A").getTransducer());
        assertSame(restored.getState("B"), restored.getState("A").getDestinationState(1));
        assertEquals(2, restored.getEmissionMultinomial().length);
        assertEquals(2, restored.getTransitionMultinomial().length);
        assertEquals(1.0, restored.getEmissionMultinomial()[0].probability("x"), 0.0000001);
    }

    @Test
    public void writeProducesReadableSerializedHmm() throws Exception {
        Alphabet inputs = new Alphabet();
        inputs.lookupIndex("x");
        Alphabet outputs = new Alphabet();
        outputs.lookupIndex("A");
        HMM original = new HMM(inputs, outputs);
        original.addState("A", new String[] { "A" });
        original.reset();
        Path destination = Files.createTempFile("hmm-prompt-test", ".ser");

        original.write(destination.toFile());

        assertTrue(Files.size(destination) > 0L);
        ObjectInputStream stream = new ObjectInputStream(Files.newInputStream(destination));
        HMM restored = (HMM) stream.readObject();
        stream.close();
        assertEquals(1, restored.numStates());
        assertEquals("A", restored.getState(0).getName());
        assertEquals(1.0, restored.getTransitionMultinomial()[0].probability("A"), 0.0000001);
        Files.delete(destination);
    }

    @Test
    public void printIncludesStateAndDistributionDescriptions() throws Exception {
        Alphabet inputs = new Alphabet();
        inputs.lookupIndex("x");
        Alphabet outputs = new Alphabet();
        outputs.lookupIndex("A");
        HMM hmm = new HMM(inputs, outputs);
        hmm.addState("A", new String[] { "A" });
        hmm.reset();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;
        PrintStream capturedOutput = new PrintStream(bytes, true, "UTF-8");

        try {
            System.setOut(capturedOutput);
            hmm.print();
        } finally {
            System.setOut(originalOutput);
            capturedOutput.close();
        }

        String printed = bytes.toString("UTF-8");
        assertTrue(printed.contains("STATE NAME=\"A\""));
        assertTrue(printed.contains("1 outgoing transitions"));
        assertTrue(printed.contains("Emission distribution"));
        assertTrue(printed.contains("Transition distribution"));
    }
}
