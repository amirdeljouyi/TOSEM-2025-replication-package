package cc.mallet.fst;

import cc.mallet.types.Alphabet;
import cc.mallet.types.ArraySequence;
import cc.mallet.types.LabelAlphabet;
import cc.mallet.types.LabelVector;
import cc.mallet.types.Sequence;
import org.junit.Test;

import static org.junit.Assert.*;

public class SumLatticeBeamPromptTest {

    @Test
    public void constrainedDeterministicPathExposesBasicLatticeValues() {
        Alphabet inputs = new Alphabet();
        LabelAlphabet outputs = new LabelAlphabet();
        FeatureTransducer transducer = new FeatureTransducer(inputs, outputs);
        transducer.addState("only", 0.0, 0.0,
                new Object[] {"token"}, new Object[] {"label"},
                new double[] {0.0}, new String[] {"only"});
        Sequence<String> input = new ArraySequence<>(new String[] {"token", "token"});

        SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, null, null,
                outputs, new int[] {0, 0, 0});
        Transducer.State state = transducer.getState(0);

        assertSame(input, lattice.getInput());
        assertSame(transducer, lattice.getTransducer());
        assertEquals(3, lattice.length());
        assertEquals(0.0, lattice.getTotalWeight(), 1.0e-10);
        assertEquals(0.0, lattice.getAlpha(0, state), 1.0e-10);
        assertEquals(0.0, lattice.getAlpha(1, state), 1.0e-10);
        assertEquals(0.0, lattice.getAlpha(2, state), 1.0e-10);
        assertEquals(0.0, lattice.getBeta(0, state), 1.0e-10);
        assertEquals(0.0, lattice.getBeta(1, state), 1.0e-10);
        assertEquals(0.0, lattice.getBeta(2, state), 1.0e-10);
        assertEquals(1.0, lattice.getGammaProbability(1, state), 1.0e-10);
    }

    @Test
    public void constrainedBranchingPathComputesForwardBackwardMarginals() {
        Alphabet inputs = new Alphabet();
        LabelAlphabet outputs = new LabelAlphabet();
        FeatureTransducer transducer = new FeatureTransducer(inputs, outputs);
        transducer.addState("A", Math.log(0.6), 0.0,
                new Object[] {"x", "x"}, new Object[] {"A-out", "B-out"},
                new double[] {Math.log(0.7), Math.log(0.3)}, new String[] {"A", "B"});
        transducer.addState("B", Math.log(0.4), 0.0,
                new Object[] {"x", "x"}, new Object[] {"A-out", "B-out"},
                new double[] {Math.log(0.2), Math.log(0.8)}, new String[] {"A", "B"});
        Sequence<String> input = new ArraySequence<>(new String[] {"x"});

        SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, null, null,
                null, new int[] {0, 0});
        Transducer.State a = transducer.getState(0);
        Transducer.State b = transducer.getState(1);

        assertEquals(0.0, lattice.getTotalWeight(), 1.0e-10);
        assertEquals(0.6, lattice.getGammaProbability(0, a), 1.0e-10);
        assertEquals(0.4, lattice.getGammaProbability(0, b), 1.0e-10);
        assertEquals(0.5, lattice.getGammaProbability(1, a), 1.0e-10);
        assertEquals(0.5, lattice.getGammaProbability(1, b), 1.0e-10);
        assertEquals(Math.log(0.5), lattice.getAlpha(1, a), 1.0e-10);
        assertEquals(Math.log(0.5), lattice.getAlpha(1, b), 1.0e-10);
        assertEquals(0.0, lattice.getBeta(0, a), 1.0e-10);
        assertEquals(0.0, lattice.getBeta(0, b), 1.0e-10);
    }

    @Test
    public void constrainedTwoStepPathCombinesCallsAcrossStatesAndTransitions() {
        Alphabet inputs = new Alphabet();
        LabelAlphabet outputs = new LabelAlphabet();
        FeatureTransducer transducer = new FeatureTransducer(inputs, outputs);
        transducer.addState("A", Math.log(0.6), 0.0,
                new Object[] {"x", "x"}, new Object[] {"A-out", "B-out"},
                new double[] {Math.log(0.7), Math.log(0.3)}, new String[] {"A", "B"});
        transducer.addState("B", Math.log(0.4), 0.0,
                new Object[] {"x", "x"}, new Object[] {"A-out", "B-out"},
                new double[] {Math.log(0.2), Math.log(0.8)}, new String[] {"A", "B"});
        Sequence<String> input = new ArraySequence<>(new String[] {"x", "x"});

        SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, null, null,
                null, new int[] {0, 0, 0});
        Transducer.State a = transducer.getState(0);
        Transducer.State b = transducer.getState(1);

        assertEquals(0.0, lattice.getTotalWeight(), 1.0e-10);
        assertEquals(0.5, lattice.getGammaProbability(1, a), 1.0e-10);
        assertEquals(0.5, lattice.getGammaProbability(1, b), 1.0e-10);
        assertEquals(0.45, lattice.getGammaProbability(2, a), 1.0e-10);
        assertEquals(0.55, lattice.getGammaProbability(2, b), 1.0e-10);
        assertEquals(Math.log(0.45), lattice.getAlpha(2, a), 1.0e-10);
        assertEquals(Math.log(0.55), lattice.getAlpha(2, b), 1.0e-10);
    }

    @Test
    public void positiveIntermediateConstraintRestrictsTotalPathWeight() {
        Alphabet inputs = new Alphabet();
        FeatureTransducer transducer = new FeatureTransducer(inputs, new LabelAlphabet());
        transducer.addState("A", Math.log(0.6), 0.0,
                new Object[] {"x", "x"}, new Object[] {"A", "B"},
                new double[] {Math.log(0.7), Math.log(0.3)}, new String[] {"A", "B"});
        transducer.addState("B", Math.log(0.4), 0.0,
                new Object[] {"x", "x"}, new Object[] {"A", "B"},
                new double[] {Math.log(0.2), Math.log(0.8)}, new String[] {"A", "B"});

        SumLatticeBeam lattice = new SumLatticeBeam(transducer,
                new ArraySequence<>(new String[] {"x", "x"}), null, null,
                null, new int[] {0, 2, 0});

        assertEquals(Math.log(0.5), lattice.getTotalWeight(), 1.0e-10);
        assertEquals(1.0, lattice.getGammaProbability(1, transducer.getState(1)), 1.0e-10);
        assertEquals(0.0, lattice.getGammaProbability(1, transducer.getState(0)), 1.0e-10);
    }

    @Test
    public void negativeIntermediateConstraintExcludesNamedState() {
        Alphabet inputs = new Alphabet();
        FeatureTransducer transducer = new FeatureTransducer(inputs, new LabelAlphabet());
        transducer.addState("A", Math.log(0.6), 0.0,
                new Object[] {"x", "x"}, new Object[] {"A", "B"},
                new double[] {Math.log(0.7), Math.log(0.3)}, new String[] {"A", "B"});
        transducer.addState("B", Math.log(0.4), 0.0,
                new Object[] {"x", "x"}, new Object[] {"A", "B"},
                new double[] {Math.log(0.2), Math.log(0.8)}, new String[] {"A", "B"});

        SumLatticeBeam lattice = new SumLatticeBeam(transducer,
                new ArraySequence<>(new String[] {"x", "x"}), null, null,
                null, new int[] {0, -1, 0});

        assertEquals(Math.log(0.5), lattice.getTotalWeight(), 1.0e-10);
        assertEquals(1.0, lattice.getGammaProbability(1, transducer.getState(1)), 1.0e-10);
        assertEquals(0.0, lattice.getGammaProbability(1, transducer.getState(0)), 1.0e-10);
    }

    @Test
    public void outputAlphabetProducesNormalizedLabelVectors() {
        Alphabet inputs = new Alphabet();
        LabelAlphabet outputs = new LabelAlphabet();
        FeatureTransducer transducer = new FeatureTransducer(inputs, outputs);
        transducer.addState("A", Math.log(0.6), 0.0,
                new Object[] {"x", "x"}, new Object[] {"to-A", "to-B"},
                new double[] {Math.log(0.7), Math.log(0.3)}, new String[] {"A", "B"});
        transducer.addState("B", Math.log(0.4), 0.0,
                new Object[] {"x", "x"}, new Object[] {"to-A", "to-B"},
                new double[] {Math.log(0.2), Math.log(0.8)}, new String[] {"A", "B"});

        SumLatticeBeam lattice = new SumLatticeBeam(transducer,
                new ArraySequence<>(new String[] {"x"}), null, null,
                outputs, new int[] {0, 0});
        LabelVector labeling = lattice.getLabelingAtPosition(0);

        assertNotNull(labeling);
        assertSame(outputs, labeling.getLabelAlphabet());
        assertEquals(0.5, labeling.value(outputs.lookupLabel("to-A")), 1.0e-10);
        assertEquals(0.5, labeling.value(outputs.lookupLabel("to-B")), 1.0e-10);
        assertNull(lattice.getLabelingAtPosition(1));
    }

    @Test
    public void nullOutputAlphabetLeavesLabelingsUnavailable() {
        Alphabet inputs = new Alphabet();
        FeatureTransducer transducer = new FeatureTransducer(inputs, new Alphabet());
        transducer.addState("only", 0.0, 0.0,
                new Object[] {"x"}, new Object[] {"y"},
                new double[] {0.0}, new String[] {"only"});
        SumLatticeBeam lattice = new SumLatticeBeam(transducer,
                new ArraySequence<>(new String[] {"x"}), null, null,
                null, new int[] {0, 0});

        assertNull(lattice.getLabelingAtPosition(0));
    }

    @Test
    public void constrainedConstructorDoesNotAllocateXiStorage() {
        FeatureTransducer transducer = new FeatureTransducer();
        transducer.addState("only", 0.0, 0.0,
                new Object[] {"x"}, new Object[] {"y"},
                new double[] {0.0}, new String[] {"only"});
        SumLatticeBeam lattice = new SumLatticeBeam(transducer,
                new ArraySequence<>(new String[] {"x"}), null, null,
                null, new int[] {0, 0});

        assertNull(lattice.getXis());
    }

    @Test(expected = IllegalStateException.class)
    public void xiProbabilityFailsWhenXiStorageWasNotRequested() {
        FeatureTransducer transducer = new FeatureTransducer();
        transducer.addState("only", 0.0, 0.0,
                new Object[] {"x"}, new Object[] {"y"},
                new double[] {0.0}, new String[] {"only"});
        SumLatticeBeam lattice = new SumLatticeBeam(transducer,
                new ArraySequence<>(new String[] {"x"}), null, null,
                null, new int[] {0, 0});

        lattice.getXiProbability(0, transducer.getState(0), transducer.getState(0));
    }

    @Test(expected = IllegalStateException.class)
    public void xiWeightFailsWhenXiStorageWasNotRequested() {
        FeatureTransducer transducer = new FeatureTransducer();
        transducer.addState("only", 0.0, 0.0,
                new Object[] {"x"}, new Object[] {"y"},
                new double[] {0.0}, new String[] {"only"});
        SumLatticeBeam lattice = new SumLatticeBeam(transducer,
                new ArraySequence<>(new String[] {"x"}), null, null,
                null, new int[] {0, 0});

        lattice.getXiWeight(0, transducer.getState(0), transducer.getState(0));
    }

    @Test
    public void impossibleConstraintReturnsNegativeInfinityAndZeroMarginals() {
        FeatureTransducer transducer = new FeatureTransducer();
        transducer.addState("only", 0.0, 0.0,
                new Object[] {"x"}, new Object[] {"y"},
                new double[] {0.0}, new String[] {"only"});
        SumLatticeBeam lattice = new SumLatticeBeam(transducer,
                new ArraySequence<>(new String[] {"x"}), null, null,
                null, new int[] {0, -1});

        assertEquals(Double.NEGATIVE_INFINITY, lattice.getTotalWeight(), 0.0);
        assertEquals(Double.NEGATIVE_INFINITY,
                lattice.getGammaWeight(1, transducer.getState(0)), 0.0);
        assertEquals(0.0, lattice.getGammaProbability(1, transducer.getState(0)), 0.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void unknownInputSymbolPropagatesFromTransducer() {
        FeatureTransducer transducer = new FeatureTransducer();
        transducer.addState("only", 0.0, 0.0,
                new Object[] {"known"}, new Object[] {"y"},
                new double[] {0.0}, new String[] {"only"});

        new SumLatticeBeam(transducer, new ArraySequence<>(new String[] {"unknown"}),
                null, null, null, new int[] {0, 0});
    }

    @Test(expected = UnsupportedOperationException.class)
    public void unsupportedObservedOutputPropagatesFromFeatureTransducer() {
        FeatureTransducer transducer = new FeatureTransducer();
        transducer.addState("only", 0.0, 0.0,
                new Object[] {"x"}, new Object[] {"y"},
                new double[] {0.0}, new String[] {"only"});

        new SumLatticeBeam(transducer, new ArraySequence<>(new String[] {"x"}),
                new ArraySequence<>(new String[] {"y"}), null, null, new int[] {0, 0});
    }

    @Test
    public void emptyInputUsesSinglePositionAndImpossibleBeamWeight() {
        FeatureTransducer transducer = new FeatureTransducer();
        transducer.addState("only", 0.0, 0.0,
                new Object[] {"x"}, new Object[] {"y"},
                new double[] {0.0}, new String[] {"only"});
        Sequence<String> empty = new ArraySequence<>(new String[] {});

        SumLatticeBeam lattice = new SumLatticeBeam(transducer, empty, null, null);

        assertEquals(1, lattice.length());
        assertEquals(Double.NEGATIVE_INFINITY, lattice.getTotalWeight(), 0.0);
        assertEquals(Double.NEGATIVE_INFINITY,
                lattice.getGammaWeight(0, transducer.getState(0)), 0.0);
        assertArrayEquals(new double[] {0.0}, lattice.getNstatesExpl(), 0.0);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void ordinaryBeamConstructorRejectsNonEmptyPathWithNoRecognizedStartNode() {
        FeatureTransducer transducer = new FeatureTransducer();
        transducer.addState("only", 0.0, 0.0,
                new Object[] {"x"}, new Object[] {"y"},
                new double[] {0.0}, new String[] {"only"});

        new SumLatticeBeam(transducer, new ArraySequence<>(new String[] {"x"}), null, null);
    }

    @Test
    public void configurationMethodsRoundTripAndIterationCounterResets() {
        FeatureTransducer transducer = new FeatureTransducer();
        transducer.addState("only", 0.0, 0.0,
                new Object[] {"x"}, new Object[] {"y"},
                new double[] {0.0}, new String[] {"only"});
        SumLatticeBeam lattice = new SumLatticeBeam(transducer,
                new ArraySequence<>(new String[] {}), null, null);

        lattice.setBeamWidth(7);
        lattice.setUseForwardBackwardBeam(true);
        lattice.setKLeps(-0.25);
        lattice.setRmin(-0.5);
        lattice.incIter();
        lattice.incIter();

        assertEquals(7, lattice.getBeamWidth());
        assertTrue(lattice.getUseForwardBackwardBeam());
        assertEquals(2, lattice.getTctIter());
        lattice.setCurIter(4);
        assertEquals(0, lattice.getTctIter());
        lattice.setUseForwardBackwardBeam(false);
        assertFalse(lattice.getUseForwardBackwardBeam());
    }

    @Test
    public void factoryCreatesBeamLatticeAndAppliesConfiguredWidth() {
        FeatureTransducer transducer = new FeatureTransducer();
        transducer.addState("only", 0.0, 0.0,
                new Object[] {"x"}, new Object[] {"y"},
                new double[] {0.0}, new String[] {"only"});
        SumLatticeBeam.Factory factory = new SumLatticeBeam.Factory(11);

        SumLattice created = factory.newSumLattice(transducer,
                new ArraySequence<>(new String[] {}), null, null, false, null);

        assertTrue(created instanceof SumLatticeBeam);
        assertEquals(11, ((SumLatticeBeam) created).getBeamWidth());
        assertSame(transducer, created.getTransducer());
        assertEquals(1, created.length());
    }

    @Test
    public void returnedGammaArrayMatchesAccessorValues() {
        FeatureTransducer transducer = new FeatureTransducer();
        transducer.addState("only", 0.0, 0.0,
                new Object[] {"x"}, new Object[] {"y"},
                new double[] {0.0}, new String[] {"only"});
        SumLatticeBeam lattice = new SumLatticeBeam(transducer,
                new ArraySequence<>(new String[] {"x"}), null, null,
                null, new int[] {0, 0});

        double[][] gammas = lattice.getGammas();

        assertEquals(2, gammas.length);
        assertEquals(1, gammas[0].length);
        assertEquals(lattice.getGammaWeight(0, transducer.getState(0)), gammas[0][0], 0.0);
        assertEquals(lattice.getGammaWeight(1, transducer.getState(0)), gammas[1][0], 0.0);
    }
}
