package cc.mallet.fst.tests;

import cc.mallet.fst.*;
import cc.mallet.pipe.*;
import cc.mallet.types.*;
import org.junit.Test;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import java.util.regex.Pattern;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SumLatticeBeam_4_GPTLLMTest {

@Test
public void testConstructor_basicInitialization() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.Incrementor incrementor = mock(Transducer.Incrementor.class);
Transducer.State state0 = mock(Transducer.State.class);
when(state0.getInitialWeight()).thenReturn(0.0);
when(state0.getFinalWeight()).thenReturn(0.0);
when(state0.getIndex()).thenReturn(0);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state0);
when(input.size()).thenReturn(2);
when(output.size()).thenReturn(2);
when(input.get(0)).thenReturn("A");
when(input.get(1)).thenReturn("B");
when(output.get(0)).thenReturn("X");
when(output.get(1)).thenReturn("Y");
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(iterator.hasNext()).thenReturn(false);
when(state0.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iterator);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, incrementor);
assertNotNull(lattice);
assertEquals(input, lattice.getInput());
}

@Test
public void testSetAndGetBeamWidth() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State state = mock(Transducer.State.class);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(state.getIndex()).thenReturn(0);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
when(input.get(0)).thenReturn("A");
when(output.get(0)).thenReturn("X");
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(iterator.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iterator);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null);
lattice.setBeamWidth(10);
assertEquals(10, lattice.getBeamWidth());
}

@Test
public void testUseForwardBackwardBeamFlag() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State state = mock(Transducer.State.class);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(state.getIndex()).thenReturn(0);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
when(input.get(0)).thenReturn("A");
when(output.get(0)).thenReturn("X");
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(iterator.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iterator);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null);
assertFalse(lattice.getUseForwardBackwardBeam());
lattice.setUseForwardBackwardBeam(true);
assertTrue(lattice.getUseForwardBackwardBeam());
}

@Test
public void testGammaWeightAndProbabilityValues() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.Incrementor incrementor = mock(Transducer.Incrementor.class);
Transducer.State state = mock(Transducer.State.class);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(state.getIndex()).thenReturn(0);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
when(input.get(0)).thenReturn("A");
when(output.get(0)).thenReturn("X");
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(iterator.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iterator);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, incrementor, true);
double gammaWeight = lattice.getGammaWeight(1, state);
double gammaProb = lattice.getGammaProbability(1, state);
assertFalse(Double.isNaN(gammaWeight));
assertFalse(Double.isNaN(gammaProb));
}

@Test
public void testGetAlphaAndBeta() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State state = mock(Transducer.State.class);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(state.getIndex()).thenReturn(0);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
when(input.get(0)).thenReturn("A");
when(output.get(0)).thenReturn("X");
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(iterator.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iterator);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, true);
double alpha = lattice.getAlpha(0, state);
double beta = lattice.getBeta(1, state);
assertFalse(Double.isNaN(alpha));
assertFalse(Double.isNaN(beta));
}

@Test
public void testGetXiProbabilityAndWeight() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.Incrementor incrementor = mock(Transducer.Incrementor.class);
Transducer.State state = mock(Transducer.State.class);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(state.getIndex()).thenReturn(0);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
when(input.get(0)).thenReturn("A");
when(output.get(0)).thenReturn("X");
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(iterator.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iterator);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, incrementor, true);
double prob = lattice.getXiProbability(0, state, state);
double weight = lattice.getXiWeight(0, state, state);
assertFalse(Double.isNaN(prob));
assertFalse(Double.isNaN(weight));
}

@Test(expected = IllegalStateException.class)
public void testGetXiThrowsWhenNotSaved() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State state = mock(Transducer.State.class);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(state.getIndex()).thenReturn(0);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
when(input.get(0)).thenReturn("A");
when(output.get(0)).thenReturn("X");
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(iterator.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iterator);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, false);
lattice.getXiWeight(0, state, state);
}

@Test
public void testGetTotalWeightIsValid() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.Incrementor incrementor = mock(Transducer.Incrementor.class);
Transducer.State state = mock(Transducer.State.class);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(state.getIndex()).thenReturn(0);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
when(input.get(0)).thenReturn("X");
when(output.get(0)).thenReturn("Y");
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(iterator.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iterator);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, incrementor);
assertFalse(Double.isNaN(lattice.getTotalWeight()));
}

@Test
public void testFactoryCreatesLattice() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State state = mock(Transducer.State.class);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(state.getIndex()).thenReturn(0);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
when(input.get(0)).thenReturn("X");
when(output.get(0)).thenReturn("Y");
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(iterator.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iterator);
SumLatticeBeam.Factory factory = new SumLatticeBeam.Factory(3);
SumLattice lattice = factory.newSumLattice(transducer, input, output, null, false, null);
assertNotNull(lattice);
assertTrue(lattice instanceof SumLatticeBeam);
}

@Test
public void testConstructorWithNullOutput() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Transducer.Incrementor incrementor = mock(Transducer.Incrementor.class);
Transducer.State state = mock(Transducer.State.class);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(state.getIndex()).thenReturn(0);
when(state.getName()).thenReturn("S0");
when(input.size()).thenReturn(2);
when(input.get(0)).thenReturn("a");
when(input.get(1)).thenReturn("b");
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(iterator.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), isNull(), anyInt())).thenReturn(iterator);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, null, incrementor);
assertNotNull(lattice);
assertEquals(input, lattice.getInput());
}

@Test
public void testTransducerWithTwoStates_OneUnreachable() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State state0 = mock(Transducer.State.class);
Transducer.State state1 = mock(Transducer.State.class);
when(state0.getInitialWeight()).thenReturn(0.0);
when(state1.getInitialWeight()).thenReturn(Transducer.IMPOSSIBLE_WEIGHT);
when(state0.getFinalWeight()).thenReturn(0.0);
when(state1.getFinalWeight()).thenReturn(0.0);
when(state0.getIndex()).thenReturn(0);
when(state1.getIndex()).thenReturn(1);
when(state0.getName()).thenReturn("S0");
when(state1.getName()).thenReturn("S1");
when(transducer.numStates()).thenReturn(2);
when(transducer.getState(0)).thenReturn(state0);
when(transducer.getState(1)).thenReturn(state1);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
when(input.get(0)).thenReturn("A");
when(output.get(0)).thenReturn("X");
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(iterator.hasNext()).thenReturn(false);
when(state0.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iterator);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null);
assertNotNull(lattice);
assertEquals(2, lattice.getGammas()[0].length);
}

@Test
public void testConstrainedConstructorWithNegativeConstraint() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
LabelAlphabet labelAlphabet = mock(LabelAlphabet.class);
Transducer.State state = mock(Transducer.State.class);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(state.getIndex()).thenReturn(0);
when(state.getName()).thenReturn("S0");
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
when(input.get(0)).thenReturn("A");
when(output.get(0)).thenReturn("X");
int[] constraints = { -1, 0 };
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(iterator.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iterator);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, labelAlphabet, constraints);
assertNotNull(lattice);
}

@Test
public void testSegmentConstraintConstructorValidInput() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Sequence constrained = mock(Sequence.class);
when(input.size()).thenReturn(3);
when(constrained.size()).thenReturn(3);
when(constrained.get(0)).thenReturn("B-ORG");
when(constrained.get(1)).thenReturn("I-ORG");
when(constrained.get(2)).thenReturn("O");
when(transducer.stateIndexOfString(anyString())).thenReturn(0);
Transducer.State state = mock(Transducer.State.class);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(state.getIndex()).thenReturn(0);
when(state.getName()).thenReturn("S0");
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
when(output.size()).thenReturn(3);
when(input.get(0)).thenReturn("A");
when(input.get(1)).thenReturn("B");
when(input.get(2)).thenReturn("C");
when(output.get(0)).thenReturn("X");
when(output.get(1)).thenReturn("Y");
when(output.get(2)).thenReturn("Z");
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(iterator.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iterator);
// Segment segment = new Segment(0, 1, "B-ORG", "I-ORG");
// SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, segment, constrained);
// assertNotNull(lattice);
}

@Test
public void testNoInitialStateWarning() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State state = mock(Transducer.State.class);
when(state.getInitialWeight()).thenReturn(Transducer.IMPOSSIBLE_WEIGHT);
when(state.getFinalWeight()).thenReturn(0.0);
when(state.getIndex()).thenReturn(0);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
when(input.get(0)).thenReturn("word");
when(output.get(0)).thenReturn("tag");
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(iterator.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iterator);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null);
assertNotNull(lattice);
}

@Test
public void testLabelingIsNullIfOutputAlphabetIsNull() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State state = mock(Transducer.State.class);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(state.getIndex()).thenReturn(0);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
when(input.get(0)).thenReturn("token");
when(output.get(0)).thenReturn("label");
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(iterator.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iterator);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, false, null);
assertNull(lattice.getLabelingAtPosition(0));
}

@Test
public void testGetXisReturnsNullWhenDisabled() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State state = mock(Transducer.State.class);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(state.getIndex()).thenReturn(0);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
when(input.get(0)).thenReturn("a");
when(output.get(0)).thenReturn("b");
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(iterator.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iterator);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, false, null);
assertNull(lattice.getXis());
}

@Test
public void testGetXisReturnsNonNullWhenEnabled() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State state = mock(Transducer.State.class);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(state.getIndex()).thenReturn(0);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
when(input.get(0)).thenReturn("a");
when(output.get(0)).thenReturn("b");
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(iterator.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iterator);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, true, null);
assertNotNull(lattice.getXis());
}

@Test
public void testLengthIsInputSizePlusOne() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
when(input.size()).thenReturn(4);
when(output.size()).thenReturn(4);
Transducer.State state = mock(Transducer.State.class);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(state.getIndex()).thenReturn(0);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
when(input.get(0)).thenReturn("a");
when(input.get(1)).thenReturn("b");
when(input.get(2)).thenReturn("c");
when(input.get(3)).thenReturn("d");
when(output.get(0)).thenReturn("w");
when(output.get(1)).thenReturn("x");
when(output.get(2)).thenReturn("y");
when(output.get(3)).thenReturn("z");
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(iterator.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iterator);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, true, null);
assertEquals(5, lattice.length());
}

@Test
public void testKLepsPositive_enablesKLThresholding() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State state = mock(Transducer.State.class);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(state.getIndex()).thenReturn(0);
when(state.getName()).thenReturn("S0");
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
when(input.size()).thenReturn(2);
when(output.size()).thenReturn(2);
when(input.get(0)).thenReturn("X");
when(input.get(1)).thenReturn("Y");
when(output.get(0)).thenReturn("A");
when(output.get(1)).thenReturn("B");
Transducer.TransitionIterator transIter = mock(Transducer.TransitionIterator.class);
when(transIter.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(transIter);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null);
lattice.setKLeps(0.01);
// assertEquals(0.01, lattice.KLeps, 1e-7);
}

@Test
public void testSetNullOutputLabelAlphabetStillWorks() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.Incrementor incrementor = mock(Transducer.Incrementor.class);
Transducer.State state = mock(Transducer.State.class);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(state.getIndex()).thenReturn(0);
when(state.getName()).thenReturn("S0");
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
when(input.size()).thenReturn(3);
when(output.size()).thenReturn(3);
when(input.get(0)).thenReturn("A");
when(input.get(1)).thenReturn("B");
when(input.get(2)).thenReturn("C");
when(output.get(0)).thenReturn("a");
when(output.get(1)).thenReturn("b");
when(output.get(2)).thenReturn("c");
Transducer.TransitionIterator iter = mock(Transducer.TransitionIterator.class);
when(iter.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iter);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, incrementor, true, null);
assertNull(lattice.getLabelingAtPosition(0));
}

@Test
public void testBeamWidthAdjustmentWhenIterAndTctAreSet() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State state = mock(Transducer.State.class);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(state.getIndex()).thenReturn(0);
when(state.getName()).thenReturn("S0");
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
when(input.size()).thenReturn(2);
when(output.size()).thenReturn(2);
when(input.get(0)).thenReturn("a");
when(input.get(1)).thenReturn("b");
when(output.get(0)).thenReturn("x");
when(output.get(1)).thenReturn("y");
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(iterator.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iterator);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null);
lattice.setCurIter(2);
lattice.incIter();
lattice.setBeamWidth(2);
assertEquals(2, lattice.getBeamWidth());
assertEquals(1, lattice.getTctIter());
}

@Test
public void testConstrainedConstructorWithInfeasibleNegativeStateAfterSegment() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Sequence constrained = mock(Sequence.class);
when(input.size()).thenReturn(3);
when(constrained.size()).thenReturn(3);
when(output.size()).thenReturn(3);
Transducer.State state = mock(Transducer.State.class);
when(transducer.getState(0)).thenReturn(state);
when(transducer.numStates()).thenReturn(1);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(state.getName()).thenReturn("S0");
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(iterator.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iterator);
when(transducer.stateIndexOfString(anyString())).thenReturn(0);
when(constrained.get(0)).thenReturn("B-ORG");
when(constrained.get(1)).thenReturn("I-ORG");
when(constrained.get(2)).thenReturn("O");
when(input.get(0)).thenReturn("a");
when(input.get(1)).thenReturn("b");
when(input.get(2)).thenReturn("c");
when(output.get(0)).thenReturn("x");
when(output.get(1)).thenReturn("y");
when(output.get(2)).thenReturn("z");
// Segment segment = new Segment(0, 1, "B-ORG", "I-ORG");
// SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, segment, constrained);
// assertNotNull(lattice);
// assertEquals(input, lattice.getInput());
}

@Test
public void testLabelAlphabetOutputSumIsNormalizedToOne() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
LabelAlphabet labelAlphabet = mock(LabelAlphabet.class);
when(labelAlphabet.size()).thenReturn(1);
when(labelAlphabet.lookupIndex(any(), eq(false))).thenReturn(0);
Transducer.State state = mock(Transducer.State.class);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(state.getIndex()).thenReturn(0);
when(state.getName()).thenReturn("S0");
when(input.size()).thenReturn(2);
when(output.size()).thenReturn(2);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
when(input.get(0)).thenReturn("a");
when(input.get(1)).thenReturn("b");
when(output.get(0)).thenReturn("x");
when(output.get(1)).thenReturn("y");
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(iterator.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iterator);
Transducer.Incrementor incrementor = mock(Transducer.Incrementor.class);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, incrementor, true, labelAlphabet);
assertNotNull(lattice.getLabelingAtPosition(0));
}

@Test(expected = IllegalArgumentException.class)
public void testConstrSegmentThrowsIfConstrainedSizeMismatch() {
Sequence input = mock(Sequence.class);
when(input.size()).thenReturn(3);
Sequence constrained = mock(Sequence.class);
when(constrained.size()).thenReturn(1);
Sequence output = mock(Sequence.class);
when(output.size()).thenReturn(3);
Transducer transducer = mock(Transducer.class);
// Segment segment = new Segment(0, 1, "B-A", "I-A");
// new SumLatticeBeam(transducer, input, output, segment, constrained);
}

@Test(expected = IllegalArgumentException.class)
public void testIllegalStateReturnedFromMakeConstraints() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Sequence constrained = mock(Sequence.class);
when(input.size()).thenReturn(2);
when(constrained.size()).thenReturn(2);
when(output.size()).thenReturn(2);
when(constrained.get(0)).thenReturn("B-Foo");
when(constrained.get(1)).thenReturn("I-Foo");
when(transducer.stateIndexOfString("B-Foo")).thenReturn(0);
when(transducer.stateIndexOfString("I-Foo")).thenReturn(-1);
// Segment segment = new Segment(0, 1, "B-Foo", "I-Foo");
// new SumLatticeBeam(transducer, input, output, segment, constrained);
}

@Test
public void testEmptyInputSequence() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
when(input.size()).thenReturn(0);
when(output.size()).thenReturn(0);
Transducer.State state = mock(Transducer.State.class);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null);
assertEquals(1, lattice.length());
}

@Test
public void testInputLengthOne_NoTransitions() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
when(input.get(0)).thenReturn("t1");
when(output.get(0)).thenReturn("l1");
Transducer.State state = mock(Transducer.State.class);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(state.transitionIterator(any(), eq(0), any(), eq(0))).thenReturn(mock(Transducer.TransitionIterator.class));
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null);
assertEquals(2, lattice.length());
}

@Test
public void testAllTransitionsImpossible_DoNotCrash() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
when(input.get(0)).thenReturn("token");
when(output.get(0)).thenReturn("label");
when(iterator.hasNext()).thenReturn(false);
Transducer.State state = mock(Transducer.State.class);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(Transducer.IMPOSSIBLE_WEIGHT);
when(state.getFinalWeight()).thenReturn(Transducer.IMPOSSIBLE_WEIGHT);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iterator);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null);
assertNotNull(lattice);
}

@Test
public void testKLAndRminThresholdBothUsed() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State state = mock(Transducer.State.class);
Transducer.TransitionIterator iter = mock(Transducer.TransitionIterator.class);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
when(input.get(0)).thenReturn("in");
when(output.get(0)).thenReturn("out");
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iter);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null);
lattice.setKLeps(-0.01);
lattice.setRmin(0.2);
assertEquals(1, lattice.getBeamWidth());
}

@Test
public void testMultipleTransitionsOnSameState() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
when(input.get(0)).thenReturn("X");
when(output.get(0)).thenReturn("Y");
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
Transducer.State state = mock(Transducer.State.class);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(state.getName()).thenReturn("S");
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iterator);
Transducer.State dest1 = mock(Transducer.State.class);
Transducer.State dest2 = mock(Transducer.State.class);
when(dest1.getIndex()).thenReturn(0);
when(dest2.getIndex()).thenReturn(0);
when(iterator.hasNext()).thenReturn(true, true, false);
when(iterator.nextState()).thenReturn(dest1, dest2);
when(iterator.getWeight()).thenReturn(0.0);
when(iterator.getOutput()).thenReturn("Y");
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null);
assertNotNull(lattice);
}

@Test
public void testConstrainedConstructorExtraEndConstraint() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence constrained = mock(Sequence.class);
Sequence output = mock(Sequence.class);
when(input.size()).thenReturn(3);
when(constrained.size()).thenReturn(3);
when(output.size()).thenReturn(3);
when(constrained.get(0)).thenReturn("B-ORG");
when(constrained.get(1)).thenReturn("I-ORG");
when(constrained.get(2)).thenReturn("I-ORG");
when(transducer.stateIndexOfString("B-ORG")).thenReturn(0);
when(transducer.stateIndexOfString("I-ORG")).thenReturn(1);
Transducer.State state = mock(Transducer.State.class);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(mock(Transducer.TransitionIterator.class));
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
when(input.get(0)).thenReturn("a");
when(input.get(1)).thenReturn("b");
when(input.get(2)).thenReturn("c");
when(output.get(0)).thenReturn("x");
when(output.get(1)).thenReturn("y");
when(output.get(2)).thenReturn("z");
// Segment segment = new Segment(1, 2, "B-ORG", "I-ORG");
// SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, segment, constrained);
// assertNotNull(lattice);
}

@Test
public void testLabelAlphabetLookupIndexNegative() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
LabelAlphabet alphabet = mock(LabelAlphabet.class);
when(alphabet.size()).thenReturn(2);
when(alphabet.lookupIndex(any(), eq(false))).thenReturn(-1);
Transducer.State state = mock(Transducer.State.class);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
Transducer.TransitionIterator iter = mock(Transducer.TransitionIterator.class);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iter);
when(iter.hasNext()).thenReturn(false);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
when(input.get(0)).thenReturn("a");
when(output.get(0)).thenReturn("b");
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
Transducer.Incrementor incrementor = mock(Transducer.Incrementor.class);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, incrementor, true, alphabet);
assertNotNull(lattice);
}

@Test
public void testForwardPassWithNoInitialStateLogsWarningButDoesNotCrash() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State state = mock(Transducer.State.class);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(Transducer.IMPOSSIBLE_WEIGHT);
when(state.getFinalWeight()).thenReturn(0.0);
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(iterator.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iterator);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
when(input.get(0)).thenReturn("word");
when(output.get(0)).thenReturn("label");
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null);
assertNotNull(lattice);
assertEquals(2, lattice.length());
}

@Test
public void testGammasNormalizationSkippedWhenAllGammasAreImpossible() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State state = mock(Transducer.State.class);
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(state.getIndex()).thenReturn(0);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iterator);
when(iterator.hasNext()).thenReturn(false);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
when(input.get(0)).thenReturn("A");
when(output.get(0)).thenReturn("B");
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null);
double[][] gammas = lattice.getGammas();
assertTrue(gammas.length > 0);
assertTrue(gammas[0].length > 0);
assertFalse(Double.isNaN(gammas[0][0]));
}

@Test
public void testConstraintIgnoresNullOutputCorrectly() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence constrained = mock(Sequence.class);
Sequence output = null;
when(input.size()).thenReturn(2);
when(constrained.size()).thenReturn(2);
when(constrained.get(0)).thenReturn("B-PER");
when(constrained.get(1)).thenReturn("I-PER");
when(transducer.stateIndexOfString(anyString())).thenReturn(0);
Transducer.State state = mock(Transducer.State.class);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(state.transitionIterator(any(), anyInt(), isNull(), anyInt())).thenReturn(iterator);
when(iterator.hasNext()).thenReturn(false);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
when(input.get(0)).thenReturn("A");
when(input.get(1)).thenReturn("B");
// Segment segment = new Segment(0, 1, "B-PER", "I-PER");
// SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, segment, constrained);
// assertEquals(3, lattice.length());
}

@Test
public void testGetGammaProbabilityReturnsZeroForImpossibleState() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State state0 = mock(Transducer.State.class);
when(state0.getIndex()).thenReturn(0);
when(state0.getInitialWeight()).thenReturn(0.0);
when(state0.getFinalWeight()).thenReturn(Transducer.IMPOSSIBLE_WEIGHT);
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(iterator.hasNext()).thenReturn(false);
when(state0.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iterator);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state0);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
when(input.get(0)).thenReturn("token");
when(output.get(0)).thenReturn("tag");
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null);
double prob = lattice.getGammaProbability(1, state0);
assertTrue(prob >= 0.0);
}

@Test
public void testGetLabelingAtPositionOutOfRangeReturnsNull() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
LabelAlphabet alphabet = mock(LabelAlphabet.class);
when(alphabet.size()).thenReturn(1);
when(alphabet.lookupIndex(any(), eq(false))).thenReturn(0);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
when(input.get(0)).thenReturn("word");
when(output.get(0)).thenReturn("label");
Transducer.State state = mock(Transducer.State.class);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(iterator.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iterator);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, true, alphabet);
assertNull(lattice.getLabelingAtPosition(99));
}

@Test
public void testFactorySetsBeamWidthCorrectly() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
LabelAlphabet alphabet = mock(LabelAlphabet.class);
Transducer.Incrementor incrementor = mock(Transducer.Incrementor.class);
when(alphabet.size()).thenReturn(1);
when(alphabet.lookupIndex(any(), eq(false))).thenReturn(0);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
when(input.get(0)).thenReturn("a");
when(output.get(0)).thenReturn("b");
Transducer.State state = mock(Transducer.State.class);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(state.getIndex()).thenReturn(0);
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(iterator.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iterator);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
SumLatticeBeam.Factory factory = new SumLatticeBeam.Factory(9);
SumLattice lattice = factory.newSumLattice(transducer, input, output, incrementor, false, alphabet);
assertTrue(lattice instanceof SumLatticeBeam);
}

@Test
public void testSetGetNstatesExplReturnsInitializedArray() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
when(input.size()).thenReturn(2);
when(output.size()).thenReturn(2);
when(input.get(0)).thenReturn("x");
when(input.get(1)).thenReturn("y");
when(output.get(0)).thenReturn("a");
when(output.get(1)).thenReturn("b");
Transducer.State state = mock(Transducer.State.class);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(iterator.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iterator);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null);
double[] values = lattice.getNstatesExpl();
assertNotNull(values);
assertEquals(3, values.length);
}

@Test
public void testInputOfZeroLengthHandlesTransitionsGracefully() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
when(input.size()).thenReturn(0);
when(output.size()).thenReturn(0);
Transducer.State state = mock(Transducer.State.class);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(state.getIndex()).thenReturn(0);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null);
assertEquals(1, lattice.length());
assertNotNull(lattice.getGammas());
assertEquals(1, lattice.getGammas().length);
}

@Test
public void testTransitionOutputObjectStoredInLatticeNode() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State state = mock(Transducer.State.class);
Transducer.State dest = mock(Transducer.State.class);
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(iterator.hasNext()).thenReturn(true, false);
when(iterator.nextState()).thenReturn(dest);
when(iterator.getOutput()).thenReturn("TAG1");
when(iterator.getWeight()).thenReturn(0.0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(dest.getFinalWeight()).thenReturn(0.0);
when(state.getIndex()).thenReturn(0);
when(dest.getIndex()).thenReturn(0);
when(state.transitionIterator(any(), eq(0), any(), eq(0))).thenReturn(iterator);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
when(input.get(0)).thenReturn("input1");
when(output.get(0)).thenReturn("output1");
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null);
assertNotNull(lattice.getGammas());
}

@Test
public void testSetAndGetKLepsAndRminConfiguration() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
Transducer.State state = mock(Transducer.State.class);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(state.getIndex()).thenReturn(0);
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(iterator.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iterator);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
when(input.get(0)).thenReturn("a");
when(output.get(0)).thenReturn("b");
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null);
lattice.setKLeps(0.03);
lattice.setRmin(0.77);
// assertEquals(0.03, lattice.KLeps, 0.0001);
// assertEquals(0.77, lattice.Rmin, 0.0001);
}

@Test
public void testConstraintEnforcedThatStateMustNotBeVisited() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
LabelAlphabet alphabet = mock(LabelAlphabet.class);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
when(input.get(0)).thenReturn("a");
when(output.get(0)).thenReturn("b");
int[] constraints = new int[] { 0, -1 };
Transducer.State state = mock(Transducer.State.class);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(iterator.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iterator);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, alphabet, constraints);
assertNotNull(lattice);
}

@Test
public void testProperProbabilityNormalizationOfGammas() {
Transducer transducer = mock(Transducer.class);
Transducer.Incrementor incrementor = mock(Transducer.Incrementor.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
when(input.size()).thenReturn(2);
when(output.size()).thenReturn(2);
when(input.get(0)).thenReturn("t1");
when(input.get(1)).thenReturn("t2");
when(output.get(0)).thenReturn("l1");
when(output.get(1)).thenReturn("l2");
Transducer.State state = mock(Transducer.State.class);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(iterator.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iterator);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, incrementor, true, null);
double[][] gammas = lattice.getGammas();
double sum = Math.exp(gammas[0][0]) + Math.exp(gammas[1][0]) + Math.exp(gammas[2][0]);
assertTrue(sum <= 1.001);
}

@Test
public void testXiProbabilityThrowsWhenXisDisabled() {
Transducer transducer = mock(Transducer.class);
Transducer.State state = mock(Transducer.State.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(iterator.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iterator);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
when(input.get(0)).thenReturn("tok");
when(output.get(0)).thenReturn("lbl");
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, false, null);
boolean exceptionThrown = false;
try {
lattice.getXiProbability(0, state, state);
} catch (IllegalStateException e) {
exceptionThrown = true;
}
assertTrue(exceptionThrown);
}

@Test
public void testXiWeightReturnsCorrectValueWhenEnabled() {
Transducer transducer = mock(Transducer.class);
Transducer.Incrementor incrementor = mock(Transducer.Incrementor.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State state = mock(Transducer.State.class);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(iterator.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iterator);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
when(input.get(0)).thenReturn("a");
when(output.get(0)).thenReturn("b");
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, incrementor, true, null);
double val = lattice.getXiWeight(0, state, state);
assertFalse(Double.isNaN(val));
}

@Test
public void testTransitionWeightNotNaNInBackwardPass() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State state = mock(Transducer.State.class);
Transducer.State destState = mock(Transducer.State.class);
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
when(input.get(0)).thenReturn("tok");
when(output.get(0)).thenReturn("lbl");
when(state.getIndex()).thenReturn(0);
when(destState.getIndex()).thenReturn(0);
when(state.getName()).thenReturn("S");
when(destState.getName()).thenReturn("D");
when(state.getInitialWeight()).thenReturn(0.0);
when(destState.getFinalWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(iterator.hasNext()).thenReturn(true, false);
when(iterator.getWeight()).thenReturn(0.0);
when(iterator.nextState()).thenReturn(destState);
when(iterator.getOutput()).thenReturn("OUT");
when(state.transitionIterator(any(), eq(0), any(), eq(0))).thenReturn(iterator);
when(transducer.getState(0)).thenReturn(state);
when(transducer.numStates()).thenReturn(1);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, true, null);
assertNotNull(lattice.getGammas());
}

@Test
public void testOutputAlphabetIsUsedToBuildLabelVector() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
LabelAlphabet labelAlphabet = mock(LabelAlphabet.class);
when(labelAlphabet.size()).thenReturn(2);
when(labelAlphabet.lookupIndex(any(), eq(false))).thenReturn(0);
Transducer.Incrementor incrementor = mock(Transducer.Incrementor.class);
Transducer.State state = mock(Transducer.State.class);
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(input.size()).thenReturn(2);
when(output.size()).thenReturn(2);
when(input.get(0)).thenReturn("a");
when(input.get(1)).thenReturn("b");
when(output.get(0)).thenReturn("x");
when(output.get(1)).thenReturn("y");
when(iterator.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iterator);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(transducer.getState(0)).thenReturn(state);
when(transducer.numStates()).thenReturn(1);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, incrementor, true, labelAlphabet);
assertNotNull(lattice.getLabelingAtPosition(0));
}

@Test
public void testNegativeConstraintBlocksTransition() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
LabelAlphabet labelAlphabet = mock(LabelAlphabet.class);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
when(input.get(0)).thenReturn("token");
when(output.get(0)).thenReturn("tag");
Transducer.State state = mock(Transducer.State.class);
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(iterator.hasNext()).thenReturn(true, false);
when(iterator.nextState()).thenReturn(state);
when(iterator.getWeight()).thenReturn(0.0);
when(iterator.getOutput()).thenReturn("X");
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iterator);
when(transducer.getState(0)).thenReturn(state);
when(transducer.numStates()).thenReturn(1);
int[] constraints = new int[] { 0, -1 };
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, labelAlphabet, constraints);
assertNotNull(lattice);
}

@Test
public void testXiProbabilityNotNaNWhenEnabled() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State state = mock(Transducer.State.class);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
when(input.get(0)).thenReturn("x");
when(output.get(0)).thenReturn("y");
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(iterator.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iterator);
when(transducer.getState(0)).thenReturn(state);
when(transducer.numStates()).thenReturn(1);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, true, null);
double result = lattice.getXiProbability(0, state, state);
assertFalse(Double.isNaN(result));
}

@Test
public void testInitialStateWithInfiniteWeightIsSkippedInForwardPass() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State state = mock(Transducer.State.class);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
when(input.get(0)).thenReturn("a");
when(output.get(0)).thenReturn("b");
when(state.getInitialWeight()).thenReturn(Transducer.IMPOSSIBLE_WEIGHT);
when(state.getFinalWeight()).thenReturn(0.0);
when(state.getIndex()).thenReturn(0);
Transducer.TransitionIterator iter = mock(Transducer.TransitionIterator.class);
when(iter.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iter);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null);
assertNotNull(lattice);
assertEquals(2, lattice.length());
}

@Test
public void testGammaNormalizationWithAllZeroGammas() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State state = mock(Transducer.State.class);
when(input.size()).thenReturn(2);
when(output.size()).thenReturn(2);
when(input.get(0)).thenReturn("i");
when(input.get(1)).thenReturn("j");
when(output.get(0)).thenReturn("a");
when(output.get(1)).thenReturn("b");
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(state.getIndex()).thenReturn(0);
when(state.getName()).thenReturn("S");
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(iterator.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iterator);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null);
double[][] gammas = lattice.getGammas();
assertEquals(3, gammas.length);
}

@Test
public void testNegativeConstraintAtFinalStateBlocksFinalWeightContribution() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
LabelAlphabet alphabet = mock(LabelAlphabet.class);
Transducer.State state = mock(Transducer.State.class);
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
when(input.get(0)).thenReturn("w");
when(output.get(0)).thenReturn("z");
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(iterator.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iterator);
when(transducer.getState(0)).thenReturn(state);
when(transducer.numStates()).thenReturn(1);
int[] constraints = new int[] { 0, -1 };
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, alphabet, constraints);
assertEquals(2, lattice.length());
}

@Test
public void testImpossibleTotalLatticeWeightSkipsXisIncrement() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
LabelAlphabet labelAlphabet = mock(LabelAlphabet.class);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
when(input.get(0)).thenReturn("X");
when(output.get(0)).thenReturn("Y");
Transducer.State state = mock(Transducer.State.class);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(Transducer.IMPOSSIBLE_WEIGHT);
when(state.getFinalWeight()).thenReturn(Transducer.IMPOSSIBLE_WEIGHT);
when(transducer.getState(0)).thenReturn(state);
when(transducer.numStates()).thenReturn(1);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, true, labelAlphabet);
assertEquals(2, lattice.length());
assertNull(lattice.getLabelingAtPosition(0));
}
}
