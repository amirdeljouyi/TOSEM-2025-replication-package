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
import static org.mockito.Mockito.*;

public class SumLatticeBeam_5_GPTLLMTest {

@Test
public void testConstructorWithoutXisAndOutputAlphabet() {
Transducer mockTransducer = mock(Transducer.class);
Sequence mockInput = mock(Sequence.class);
Sequence mockOutput = mock(Sequence.class);
Transducer.Incrementor mockIncrementor = mock(Transducer.Incrementor.class);
Transducer.State state0 = mock(Transducer.State.class);
Transducer.State state1 = mock(Transducer.State.class);
when(mockInput.size()).thenReturn(2);
when(mockOutput.size()).thenReturn(2);
when(mockTransducer.numStates()).thenReturn(2);
when(mockTransducer.getState(0)).thenReturn(state0);
when(mockTransducer.getState(1)).thenReturn(state1);
when(state0.getIndex()).thenReturn(0);
when(state1.getIndex()).thenReturn(1);
when(state0.getInitialWeight()).thenReturn(0.0);
when(state0.getFinalWeight()).thenReturn(0.0);
when(state1.getInitialWeight()).thenReturn(Double.POSITIVE_INFINITY);
when(state1.getFinalWeight()).thenReturn(Double.POSITIVE_INFINITY);
Transducer.TransitionIterator emptyIter = mock(Transducer.TransitionIterator.class);
when(emptyIter.hasNext()).thenReturn(false);
when(state0.transitionIterator(eq(mockInput), anyInt(), eq(mockOutput), anyInt())).thenReturn(emptyIter);
when(state1.transitionIterator(eq(mockInput), anyInt(), eq(mockOutput), anyInt())).thenReturn(emptyIter);
SumLatticeBeam lattice = new SumLatticeBeam(mockTransducer, mockInput, mockOutput, mockIncrementor);
assertNotNull(lattice.getInput());
assertFalse(lattice.getUseForwardBackwardBeam());
assertEquals(3, lattice.getBeamWidth());
}

@Test
public void testConstructorWithXisAndOutputAlphabet() {
Transducer mockTransducer = mock(Transducer.class);
Sequence mockInput = mock(Sequence.class);
Sequence mockOutput = mock(Sequence.class);
Transducer.Incrementor incrementor = mock(Transducer.Incrementor.class);
LabelAlphabet mockAlphabet = mock(LabelAlphabet.class);
Transducer.State state0 = mock(Transducer.State.class);
Transducer.State state1 = mock(Transducer.State.class);
when(mockInput.size()).thenReturn(2);
when(mockOutput.size()).thenReturn(2);
when(mockAlphabet.size()).thenReturn(1);
when(mockTransducer.numStates()).thenReturn(2);
when(mockTransducer.getState(0)).thenReturn(state0);
when(mockTransducer.getState(1)).thenReturn(state1);
when(state0.getIndex()).thenReturn(0);
when(state1.getIndex()).thenReturn(1);
when(state0.getInitialWeight()).thenReturn(0.0);
when(state1.getInitialWeight()).thenReturn(Double.POSITIVE_INFINITY);
when(state0.getFinalWeight()).thenReturn(0.0);
when(state1.getFinalWeight()).thenReturn(Double.POSITIVE_INFINITY);
Transducer.TransitionIterator emptyIter = mock(Transducer.TransitionIterator.class);
when(emptyIter.hasNext()).thenReturn(false);
when(state0.transitionIterator(any(Sequence.class), anyInt(), any(Sequence.class), anyInt())).thenReturn(emptyIter);
when(state1.transitionIterator(any(Sequence.class), anyInt(), any(Sequence.class), anyInt())).thenReturn(emptyIter);
// when(mockUnicode(mockAlphabet.lookupIndex(any(), eq(false)))).thenReturn(0);
when(mockInput.get(0)).thenReturn("x1");
when(mockInput.get(1)).thenReturn("x2");
when(mockOutput.get(0)).thenReturn("y1");
when(mockOutput.get(1)).thenReturn("y2");
SumLatticeBeam lattice = new SumLatticeBeam(mockTransducer, mockInput, mockOutput, incrementor, true, mockAlphabet);
assertNotNull(lattice.getGammas());
assertNotNull(lattice.getXis());
assertTrue(lattice.getXis().length > 0);
assertTrue(lattice.getTotalWeight() != Double.NaN);
}

@Test
public void testSetterAndGetterMethods() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State s0 = mock(Transducer.State.class);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(s0);
when(s0.getIndex()).thenReturn(0);
when(s0.getInitialWeight()).thenReturn(0.0);
when(s0.getFinalWeight()).thenReturn(0.0);
Transducer.TransitionIterator it = mock(Transducer.TransitionIterator.class);
when(it.hasNext()).thenReturn(false);
when(s0.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(it);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null);
lattice.setBeamWidth(7);
assertEquals(7, lattice.getBeamWidth());
lattice.setCurIter(5);
assertEquals(0, lattice.getTctIter());
lattice.incIter();
lattice.incIter();
assertEquals(2, lattice.getTctIter());
lattice.setUseForwardBackwardBeam(true);
assertTrue(lattice.getUseForwardBackwardBeam());
lattice.setKLeps(0.03);
lattice.setRmin(0.2);
}

@Test(expected = IllegalStateException.class)
public void testGetXiWeightThrowsIfXisNotSaved() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State s = mock(Transducer.State.class);
when(s.getIndex()).thenReturn(0);
when(s.getInitialWeight()).thenReturn(0.0);
when(s.getFinalWeight()).thenReturn(0.0);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(s);
Transducer.TransitionIterator it = mock(Transducer.TransitionIterator.class);
when(it.hasNext()).thenReturn(false);
when(s.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(it);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, false, null);
lattice.getXiWeight(0, s, s);
}

@Test
public void testFactoryBuildsLatticeWithCorrectBeamWidth() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State state = mock(Transducer.State.class);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
Transducer.TransitionIterator it = mock(Transducer.TransitionIterator.class);
when(it.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(it);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
SumLatticeBeam.Factory factory = new SumLatticeBeam.Factory(8);
SumLattice lattice = factory.newSumLattice(transducer, input, output, null, false, null);
assertTrue(lattice instanceof SumLatticeBeam);
SumLatticeBeam beam = (SumLatticeBeam) lattice;
assertEquals(8, beam.getBeamWidth());
}

@Test
public void testZeroLengthInputSequence() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State state = mock(Transducer.State.class);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(input.size()).thenReturn(0);
when(output.size()).thenReturn(0);
Transducer.TransitionIterator iter = mock(Transducer.TransitionIterator.class);
when(iter.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iter);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null);
assertEquals(1, lattice.length());
double weight = lattice.getTotalWeight();
assertTrue(weight <= 0);
}

@Test
public void testAllInitialWeightsImpossible() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State state = mock(Transducer.State.class);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(Transducer.IMPOSSIBLE_WEIGHT);
when(state.getFinalWeight()).thenReturn(0.0);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
Transducer.TransitionIterator iter = mock(Transducer.TransitionIterator.class);
when(iter.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iter);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null);
assertEquals(Double.NEGATIVE_INFINITY, lattice.getTotalWeight(), 0.0001);
}

@Test(expected = IllegalArgumentException.class)
public void testConstrainedConstructorThrowsOnMismatchedSizes() {
Transducer t = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Sequence constrainedSeq = mock(Sequence.class);
Segment segment = mock(Segment.class);
when(input.size()).thenReturn(2);
when(output.size()).thenReturn(2);
when(constrainedSeq.size()).thenReturn(1);
when(segment.getStart()).thenReturn(0);
when(segment.getEnd()).thenReturn(0);
when(segment.getInTag()).thenReturn("TAG");
// new SumLatticeBeam(t, input, output, segment, constrainedSeq);
}

@Test
public void testOutputAlphabetReturnsNegativeIndex() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.Incrementor incrementor = mock(Transducer.Incrementor.class);
LabelAlphabet alpha = mock(LabelAlphabet.class);
Transducer.State state = mock(Transducer.State.class);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
when(alpha.size()).thenReturn(1);
when(alpha.lookupIndex(any(), eq(false))).thenReturn(-1);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
Transducer.TransitionIterator iter = mock(Transducer.TransitionIterator.class);
when(iter.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iter);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, incrementor, true, alpha);
assertNotNull(lattice.getInput());
assertNotNull(lattice.getGammas());
}

@Test(expected = IllegalArgumentException.class)
public void testIllegalInTagStateIndexForConstraints() {
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Sequence constrainedSeq = mock(Sequence.class);
Segment segment = mock(Segment.class);
Transducer trans = mock(Transducer.class);
when(input.size()).thenReturn(3);
when(output.size()).thenReturn(3);
when(constrainedSeq.size()).thenReturn(3);
when(constrainedSeq.get(0)).thenReturn("X");
when(constrainedSeq.get(1)).thenReturn("Y");
when(constrainedSeq.get(2)).thenReturn("Z");
when(segment.getStart()).thenReturn(0);
when(segment.getEnd()).thenReturn(1);
when(segment.getInTag()).thenReturn("Z");
when(trans.stateIndexOfString("X")).thenReturn(0);
when(trans.stateIndexOfString("Y")).thenReturn(1);
when(trans.stateIndexOfString("Z")).thenReturn(-1);
// new SumLatticeBeam(trans, input, output, segment, constrainedSeq);
}

@Test
public void testSingleStateAllValidProbabilities() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.Incrementor inc = mock(Transducer.Incrementor.class);
Transducer.State state = mock(Transducer.State.class);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
Transducer.TransitionIterator iter = mock(Transducer.TransitionIterator.class);
when(iter.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iter);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, inc, true, null);
double gamma = lattice.getGammaProbability(0, state);
assertTrue(gamma >= 0.0 && gamma <= 1.0);
double prob = lattice.getXiProbability(0, state, state);
assertTrue(prob >= 0.0);
}

@Test(expected = IllegalArgumentException.class)
public void testConstructorWithNullInTagThrows() {
Transducer trans = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Sequence constrainedSeq = mock(Sequence.class);
Segment segment = mock(Segment.class);
when(input.size()).thenReturn(3);
when(output.size()).thenReturn(3);
when(constrainedSeq.size()).thenReturn(3);
when(segment.getStart()).thenReturn(0);
when(segment.getEnd()).thenReturn(1);
when(segment.getInTag()).thenReturn(null);
// new SumLatticeBeam(trans, input, output, segment, constrainedSeq);
}

@Test
public void testGetLabelingAtPositionBeyondBoundsReturnsNull() {
Transducer trans = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
Transducer.State state = mock(Transducer.State.class);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(trans.numStates()).thenReturn(1);
when(trans.getState(0)).thenReturn(state);
Transducer.TransitionIterator iter = mock(Transducer.TransitionIterator.class);
when(iter.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iter);
SumLatticeBeam lattice = new SumLatticeBeam(trans, input, output, null, false, null);
LabelVector labelAt0 = lattice.getLabelingAtPosition(0);
assertNull(labelAt0);
LabelVector labelOutOfBounds = lattice.getLabelingAtPosition(10);
assertNull(labelOutOfBounds);
}

@Test
public void testConstraintPositiveMatchAllowsTransition() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
LabelAlphabet alphabet = mock(LabelAlphabet.class);
when(input.size()).thenReturn(2);
when(output.size()).thenReturn(2);
when(alphabet.size()).thenReturn(1);
Transducer.State state = mock(Transducer.State.class);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(state.getName()).thenReturn("S0");
Transducer.TransitionIterator iter = mock(Transducer.TransitionIterator.class);
when(iter.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iter);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
int[] constraints = new int[] { 1, 1, 1 };
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, alphabet, constraints);
assertTrue(lattice.getTotalWeight() < Double.POSITIVE_INFINITY);
assertTrue(lattice.getTotalWeight() > Transducer.IMPOSSIBLE_WEIGHT);
}

@Test
public void testConstraintNegativeBlocksTransition() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
LabelAlphabet alphabet = mock(LabelAlphabet.class);
when(input.size()).thenReturn(2);
when(output.size()).thenReturn(2);
when(alphabet.size()).thenReturn(1);
Transducer.State state = mock(Transducer.State.class);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(state.getName()).thenReturn("S0");
Transducer.TransitionIterator iter = mock(Transducer.TransitionIterator.class);
when(iter.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iter);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
int[] constraints = new int[] { 0, -1, -1 };
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, alphabet, constraints);
assertEquals(Double.NEGATIVE_INFINITY, lattice.getTotalWeight(), 0.00001);
}

@Test
public void testOutputCountsNormalizationAndLabelingStorage() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
LabelAlphabet alphabet = mock(LabelAlphabet.class);
Transducer.Incrementor incrementor = mock(Transducer.Incrementor.class);
when(input.size()).thenReturn(2);
when(output.size()).thenReturn(2);
Transducer.State state = mock(Transducer.State.class);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(state.getName()).thenReturn("S0");
Transducer.TransitionIterator iter = mock(Transducer.TransitionIterator.class);
when(iter.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iter);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
when(alphabet.size()).thenReturn(3);
when(alphabet.lookupIndex(any(), eq(false))).thenReturn(0);
when(input.get(0)).thenReturn("A");
when(input.get(1)).thenReturn("B");
when(output.get(0)).thenReturn("y1");
when(output.get(1)).thenReturn("y2");
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, incrementor, true, alphabet);
LabelVector vec = lattice.getLabelingAtPosition(0);
assertNotNull(vec);
assertEquals(3, vec.getAlphabet().size());
}

@Test
public void testIncrementorInvokedForInitialAndFinalStates() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.Incrementor inc = mock(Transducer.Incrementor.class);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
Transducer.State state = mock(Transducer.State.class);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
Transducer.TransitionIterator iter = mock(Transducer.TransitionIterator.class);
when(iter.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iter);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
new SumLatticeBeam(transducer, input, output, inc, true, null);
verify(inc, atLeastOnce()).incrementInitialState(eq(state), anyDouble());
verify(inc, atLeastOnce()).incrementFinalState(eq(state), anyDouble());
}

@Test
public void testGetGammaProbabilityReturnsValueInValidRange() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.Incrementor inc = mock(Transducer.Incrementor.class);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
Transducer.State state = mock(Transducer.State.class);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(state.getName()).thenReturn("S0");
Transducer.TransitionIterator iter = mock(Transducer.TransitionIterator.class);
when(iter.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iter);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, inc, true, null);
double gamma = lattice.getGammaProbability(0, state);
assertTrue(gamma >= 0.0);
assertTrue(gamma <= 1.0);
}

@Test
public void testConstructorWithSingleConstraintFinalStateAllowed() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
LabelAlphabet alphabet = mock(LabelAlphabet.class);
when(input.size()).thenReturn(2);
when(output.size()).thenReturn(2);
when(alphabet.size()).thenReturn(1);
Transducer.State state = mock(Transducer.State.class);
when(state.getIndex()).thenReturn(0);
when(state.getName()).thenReturn("S0");
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(iterator.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iterator);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
int[] constraints = new int[] { 1, 1, 1 };
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, alphabet, constraints);
assertTrue(lattice.getTotalWeight() > Transducer.IMPOSSIBLE_WEIGHT);
}

@Test
public void testNullOutputSequenceInConstructor() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
when(input.size()).thenReturn(2);
Transducer.State state0 = mock(Transducer.State.class);
when(state0.getIndex()).thenReturn(0);
when(state0.getInitialWeight()).thenReturn(0.0);
when(state0.getFinalWeight()).thenReturn(0.0);
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(iterator.hasNext()).thenReturn(false);
when(state0.transitionIterator(any(), anyInt(), isNull(), anyInt())).thenReturn(iterator);
when(transducer.getState(0)).thenReturn(state0);
when(transducer.numStates()).thenReturn(1);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, null, null);
assertNotNull(lattice.getInput());
}

@Test
public void testTransitionWithOutputNonNull() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State s0 = mock(Transducer.State.class);
Transducer.State s1 = mock(Transducer.State.class);
when(s0.getIndex()).thenReturn(0);
when(s1.getIndex()).thenReturn(1);
when(s0.getInitialWeight()).thenReturn(0.0);
when(s1.getInitialWeight()).thenReturn(Double.POSITIVE_INFINITY);
when(s0.getFinalWeight()).thenReturn(0.0);
when(s1.getFinalWeight()).thenReturn(0.0);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(iterator.hasNext()).thenReturn(true).thenReturn(false);
when(iterator.nextState()).thenReturn(s1);
when(iterator.getWeight()).thenReturn(0.0);
when(iterator.getOutput()).thenReturn("Y");
when(s0.transitionIterator(eq(input), anyInt(), eq(output), anyInt())).thenReturn(iterator);
when(s1.transitionIterator(eq(input), anyInt(), eq(output), anyInt())).thenReturn(iterator);
when(transducer.numStates()).thenReturn(2);
when(transducer.getState(0)).thenReturn(s0);
when(transducer.getState(1)).thenReturn(s1);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null);
assertTrue(lattice.getTotalWeight() <= 0);
}

@Test
public void testNegativeKLepsBeamWidthEnforced() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State s0 = mock(Transducer.State.class);
when(s0.getIndex()).thenReturn(0);
when(s0.getInitialWeight()).thenReturn(0.0);
when(s0.getFinalWeight()).thenReturn(0.0);
when(input.size()).thenReturn(2);
when(output.size()).thenReturn(2);
Transducer.TransitionIterator iter = mock(Transducer.TransitionIterator.class);
when(iter.hasNext()).thenReturn(false);
when(s0.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iter);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(s0);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null);
lattice.setKLeps(-0.01);
lattice.setRmin(0.5);
assertNotNull(lattice.getNstatesExpl());
}

@Test
public void testTransitionToNullDestinationNode() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State s0 = mock(Transducer.State.class);
Transducer.State s1 = mock(Transducer.State.class);
when(s0.getIndex()).thenReturn(0);
when(s1.getIndex()).thenReturn(1);
when(s0.getInitialWeight()).thenReturn(0.0);
when(s0.getFinalWeight()).thenReturn(0.0);
when(s1.getInitialWeight()).thenReturn(Double.POSITIVE_INFINITY);
when(s1.getFinalWeight()).thenReturn(Double.POSITIVE_INFINITY);
when(input.size()).thenReturn(2);
when(output.size()).thenReturn(2);
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(iterator.hasNext()).thenReturn(true).thenReturn(false);
when(iterator.nextState()).thenReturn(s1);
when(iterator.getWeight()).thenReturn(0.0);
when(iterator.getOutput()).thenReturn("Y");
when(s0.transitionIterator(eq(input), anyInt(), eq(output), anyInt())).thenReturn(iterator);
when(s1.transitionIterator(eq(input), anyInt(), eq(output), anyInt())).thenReturn(iterator);
when(transducer.numStates()).thenReturn(2);
when(transducer.getState(0)).thenReturn(s0);
when(transducer.getState(1)).thenReturn(s1);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null);
double weight = lattice.getTotalWeight();
assertFalse(Double.isNaN(weight));
}

@Test
public void testXiProbabilityConsistentOverCalls() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State s0 = mock(Transducer.State.class);
when(s0.getIndex()).thenReturn(0);
when(s0.getInitialWeight()).thenReturn(0.0);
when(s0.getFinalWeight()).thenReturn(0.0);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
Transducer.TransitionIterator iter = mock(Transducer.TransitionIterator.class);
when(iter.hasNext()).thenReturn(false);
when(s0.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iter);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(s0);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, true, null);
double p1 = lattice.getXiProbability(0, s0, s0);
double p2 = lattice.getXiProbability(0, s0, s0);
assertEquals(p1, p2, 0.000001);
}

@Test
public void testMaxImpossibleTransitionBehavior() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
Transducer.State state = mock(Transducer.State.class);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(Transducer.IMPOSSIBLE_WEIGHT);
when(state.getFinalWeight()).thenReturn(Transducer.IMPOSSIBLE_WEIGHT);
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(iterator.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iterator);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null);
double total = lattice.getTotalWeight();
assertEquals(Double.NEGATIVE_INFINITY, total, 0.0001);
}

@Test
public void testTransducerWithNoInitialStates() {
Transducer transducer = mock(Transducer.class);
Transducer.State state = mock(Transducer.State.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(Double.POSITIVE_INFINITY);
when(state.getFinalWeight()).thenReturn(Double.POSITIVE_INFINITY);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
Transducer.TransitionIterator iter = mock(Transducer.TransitionIterator.class);
when(iter.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iter);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null);
assertEquals(Double.NEGATIVE_INFINITY, lattice.getTotalWeight(), 0.001);
}

@Test
public void testOutputAlphabetLookupReturnsNegativeIndex() {
Transducer transducer = mock(Transducer.class);
Transducer.State state = mock(Transducer.State.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
LabelAlphabet outputAlphabet = mock(LabelAlphabet.class);
when(input.size()).thenReturn(2);
when(output.size()).thenReturn(2);
when(outputAlphabet.size()).thenReturn(1);
when(outputAlphabet.lookupIndex(any(), eq(false))).thenReturn(-1);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
Transducer.TransitionIterator iter = mock(Transducer.TransitionIterator.class);
when(iter.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iter);
when(input.get(0)).thenReturn("x1");
when(input.get(1)).thenReturn("x2");
when(output.get(0)).thenReturn("y1");
when(output.get(1)).thenReturn("y2");
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, true, outputAlphabet);
LabelVector label = lattice.getLabelingAtPosition(0);
assertNotNull(label);
}

@Test
public void testXiProbabilityHandlesNaNInputsSafely() {
Transducer transducer = mock(Transducer.class);
Transducer.State state = mock(Transducer.State.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
Transducer.TransitionIterator iter = mock(Transducer.TransitionIterator.class);
when(iter.hasNext()).thenReturn(true).thenReturn(false);
when(iter.nextState()).thenReturn(state);
when(iter.getWeight()).thenReturn(Double.NaN);
when(iter.getOutput()).thenReturn("Y");
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iter);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, true, null);
double val = lattice.getXiProbability(0, state, state);
assertFalse(Double.isNaN(val));
}

@Test
public void testConstrainedPathRejectsSomeTransitionsGracefully() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
LabelAlphabet alphabet = mock(LabelAlphabet.class);
when(input.size()).thenReturn(2);
when(output.size()).thenReturn(2);
when(alphabet.size()).thenReturn(1);
Transducer.State state0 = mock(Transducer.State.class);
when(state0.getIndex()).thenReturn(0);
when(state0.getName()).thenReturn("S0");
when(state0.getInitialWeight()).thenReturn(0.0);
when(state0.getFinalWeight()).thenReturn(0.0);
Transducer.TransitionIterator iter = mock(Transducer.TransitionIterator.class);
when(iter.hasNext()).thenReturn(false);
when(state0.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iter);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state0);
int[] constraints = new int[] { 0, -1, 1 };
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, alphabet, constraints);
assertTrue(lattice.getTotalWeight() <= 0.0);
}

@Test
public void testLabelingAtPositionOutOfBoundsReturnsNull() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
LabelAlphabet alphabet = mock(LabelAlphabet.class);
when(input.size()).thenReturn(2);
when(output.size()).thenReturn(2);
when(alphabet.size()).thenReturn(1);
Transducer.State state = mock(Transducer.State.class);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
Transducer.TransitionIterator iter = mock(Transducer.TransitionIterator.class);
when(iter.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iter);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
when(alphabet.lookupIndex(any(), eq(false))).thenReturn(0);
when(input.get(0)).thenReturn("a");
when(input.get(1)).thenReturn("b");
when(output.get(0)).thenReturn("x");
when(output.get(1)).thenReturn("y");
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, true, alphabet);
LabelVector label = lattice.getLabelingAtPosition(100);
assertNull(label);
}

@Test
public void testBeamFilterPrunesExtraHypotheses() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State state0 = mock(Transducer.State.class);
Transducer.State state1 = mock(Transducer.State.class);
when(state0.getIndex()).thenReturn(0);
when(state1.getIndex()).thenReturn(1);
when(state0.getInitialWeight()).thenReturn(0.0);
when(state1.getInitialWeight()).thenReturn(1.0);
when(state0.getFinalWeight()).thenReturn(0.0);
when(state1.getFinalWeight()).thenReturn(0.0);
when(input.size()).thenReturn(2);
when(output.size()).thenReturn(2);
Transducer.TransitionIterator noTransitions = mock(Transducer.TransitionIterator.class);
when(noTransitions.hasNext()).thenReturn(false);
when(state0.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(noTransitions);
when(state1.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(noTransitions);
when(transducer.numStates()).thenReturn(2);
when(transducer.getState(0)).thenReturn(state0);
when(transducer.getState(1)).thenReturn(state1);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null);
assertTrue(lattice.getTotalWeight() > Transducer.IMPOSSIBLE_WEIGHT);
}

@Test(expected = IllegalArgumentException.class)
public void testConstraintArrayTooShort_throwsException() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Segment segment = mock(Segment.class);
Sequence constrained = mock(Sequence.class);
when(input.size()).thenReturn(3);
when(constrained.size()).thenReturn(2);
when(segment.getStart()).thenReturn(0);
when(segment.getEnd()).thenReturn(1);
when(segment.getInTag()).thenReturn("I");
// new SumLatticeBeam(transducer, input, output, segment, constrained);
}

@Test
public void testTransitionViolatedByNegativeConstraint_skipsApplication() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
when(input.size()).thenReturn(2);
when(output.size()).thenReturn(2);
Transducer.State state0 = mock(Transducer.State.class);
Transducer.State state1 = mock(Transducer.State.class);
when(state0.getIndex()).thenReturn(0);
when(state1.getIndex()).thenReturn(1);
when(state0.getInitialWeight()).thenReturn(0.0);
when(state0.getFinalWeight()).thenReturn(0.0);
when(state1.getInitialWeight()).thenReturn(0.0);
when(state1.getFinalWeight()).thenReturn(0.0);
Transducer.TransitionIterator iter = mock(Transducer.TransitionIterator.class);
when(iter.hasNext()).thenReturn(true).thenReturn(false);
when(iter.nextState()).thenReturn(state1);
when(iter.getWeight()).thenReturn(0.0);
when(iter.getOutput()).thenReturn("O");
when(state0.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iter);
when(state1.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iter);
when(transducer.numStates()).thenReturn(2);
when(transducer.getState(0)).thenReturn(state0);
when(transducer.getState(1)).thenReturn(state1);
int[] constraints = new int[] { 0, -2, 0 };
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, null, constraints);
double weight = lattice.getTotalWeight();
assertTrue(weight <= 0);
}

@Test
public void testNegativeRminTriggersStrawmanThreshold() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
when(input.size()).thenReturn(2);
when(output.size()).thenReturn(2);
Transducer.State state = mock(Transducer.State.class);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
Transducer.TransitionIterator iter = mock(Transducer.TransitionIterator.class);
when(iter.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iter);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, false, null);
lattice.setKLeps(0.0);
lattice.setRmin(-0.5);
assertNotNull(lattice.getNstatesExpl());
}

@Test
public void testCurIterAndTctIterInfluenceBeamWidth() {
Transducer transducer = mock(Transducer.class);
Transducer.State state = mock(Transducer.State.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
when(input.size()).thenReturn(2);
when(output.size()).thenReturn(2);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(transducer.getState(0)).thenReturn(state);
when(transducer.numStates()).thenReturn(1);
Transducer.TransitionIterator iter = mock(Transducer.TransitionIterator.class);
when(iter.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iter);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, false, null);
lattice.setCurIter(1);
lattice.incIter();
lattice.incIter();
lattice.setBeamWidth(2);
assertEquals(2, lattice.getBeamWidth());
}

@Test
public void testLabelAlphabetSizeZeroProducesEmptyVectors() {
Transducer transducer = mock(Transducer.class);
Transducer.State state = mock(Transducer.State.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
LabelAlphabet alphabet = mock(LabelAlphabet.class);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
when(alphabet.size()).thenReturn(0);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
Transducer.TransitionIterator iter = mock(Transducer.TransitionIterator.class);
when(iter.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iter);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, true, alphabet);
LabelVector vector = lattice.getLabelingAtPosition(0);
assertEquals(0, vector.numLocations());
}

@Test
public void testGammasAreNormalizedToOne() {
Transducer transducer = mock(Transducer.class);
Transducer.State state = mock(Transducer.State.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
when(input.size()).thenReturn(2);
when(output.size()).thenReturn(2);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
Transducer.TransitionIterator iter = mock(Transducer.TransitionIterator.class);
when(iter.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iter);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, true, null);
double[][] gammas = lattice.getGammas();
double sum = 0.0;
for (int i = 0; i < gammas.length; i++) {
sum += Math.exp(gammas[i][0]);
}
assertTrue(sum >= 0.99 && sum <= 1.01);
}

@Test
public void testMultipleTransitionsFromSameStateHandled() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State s0 = mock(Transducer.State.class);
Transducer.State s1 = mock(Transducer.State.class);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
when(transducer.numStates()).thenReturn(2);
when(transducer.getState(0)).thenReturn(s0);
when(transducer.getState(1)).thenReturn(s1);
when(s0.getIndex()).thenReturn(0);
when(s1.getIndex()).thenReturn(1);
when(s0.getInitialWeight()).thenReturn(0.0);
when(s0.getFinalWeight()).thenReturn(0.0);
when(s1.getInitialWeight()).thenReturn(Double.POSITIVE_INFINITY);
when(s1.getFinalWeight()).thenReturn(0.0);
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(iterator.hasNext()).thenReturn(true, true, false);
when(iterator.nextState()).thenReturn(s1).thenReturn(s1);
when(iterator.getWeight()).thenReturn(0.0);
when(iterator.getOutput()).thenReturn("X");
when(s0.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iterator);
when(s1.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iterator);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, true, null);
assertTrue(lattice.getGammas().length > 0);
}

@Test
public void testZeroStates_noInitialPaths_possible() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
when(transducer.numStates()).thenReturn(0);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null);
assertEquals(Double.NEGATIVE_INFINITY, lattice.getTotalWeight(), 0.0001);
}

@Test
public void testConstraintDisallowsAllStates_noValidPath() {
Transducer transducer = mock(Transducer.class);
Transducer.State state = mock(Transducer.State.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(transducer.getState(0)).thenReturn(state);
when(transducer.numStates()).thenReturn(1);
Transducer.TransitionIterator it = mock(Transducer.TransitionIterator.class);
when(it.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(it);
int[] constraints = new int[] { 0, -1 };
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, null, constraints);
assertEquals(Double.NEGATIVE_INFINITY, lattice.getTotalWeight(), 0.0001);
}

@Test
public void testReturnEarlyOnImpossibleTotalWeight() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State state = mock(Transducer.State.class);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(Double.POSITIVE_INFINITY);
when(state.getFinalWeight()).thenReturn(0.0);
Transducer.TransitionIterator it = mock(Transducer.TransitionIterator.class);
when(it.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(it);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null);
double weight = lattice.getTotalWeight();
assertEquals(Transducer.IMPOSSIBLE_WEIGHT, weight, 0.00001);
}

@Test
public void testTransitionWeightWithNaN_skipped() {
Transducer transducer = mock(Transducer.class);
Transducer.State state = mock(Transducer.State.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(transducer.getState(0)).thenReturn(state);
when(transducer.numStates()).thenReturn(1);
Transducer.TransitionIterator iter = mock(Transducer.TransitionIterator.class);
when(iter.hasNext()).thenReturn(true).thenReturn(false);
when(iter.nextState()).thenReturn(state);
when(iter.getWeight()).thenReturn(Double.NaN);
when(iter.getOutput()).thenReturn("LABEL");
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iter);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, true, null);
double val = lattice.getGammaProbability(0, state);
assertTrue(val >= 0.0);
}

@Test
public void testOutputAlphabetHasUnknownValue() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State state = mock(Transducer.State.class);
LabelAlphabet alphabet = new LabelAlphabet();
Transducer.Incrementor incrementor = mock(Transducer.Incrementor.class);
int idx1 = alphabet.lookupIndex("O", true);
int idx2 = alphabet.lookupIndex("UNK", true);
assertEquals(1, idx2);
when(input.size()).thenReturn(2);
when(output.size()).thenReturn(2);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(transducer.getState(0)).thenReturn(state);
when(transducer.numStates()).thenReturn(1);
Transducer.TransitionIterator iter = mock(Transducer.TransitionIterator.class);
when(iter.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iter);
when(input.get(0)).thenReturn("X");
when(input.get(1)).thenReturn("Y");
when(output.get(0)).thenReturn("O");
when(output.get(1)).thenReturn("UNK");
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, incrementor, true, alphabet);
LabelVector lv = lattice.getLabelingAtPosition(0);
assertNotNull(lv);
assertEquals(2, lv.getAlphabet().size());
}

@Test(expected = IllegalStateException.class)
public void testGetXiProbabilityThrowsWithoutSaveXis() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State state = mock(Transducer.State.class);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
Transducer.TransitionIterator iter = mock(Transducer.TransitionIterator.class);
when(iter.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iter);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, false, null);
lattice.getXiProbability(0, state, state);
}

@Test
public void testNegativeFinalConstraint_disallowsFinalState() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
LabelAlphabet alphabet = mock(LabelAlphabet.class);
Transducer.State state = mock(Transducer.State.class);
when(input.size()).thenReturn(2);
when(output.size()).thenReturn(2);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(state.getName()).thenReturn("S0");
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
Transducer.TransitionIterator transitionIterator = mock(Transducer.TransitionIterator.class);
when(transitionIterator.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(transitionIterator);
int[] constraints = new int[] { 0, 1, -1 };
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, alphabet, constraints);
double weight = lattice.getTotalWeight();
assertEquals(Transducer.IMPOSSIBLE_WEIGHT, weight, 0.0001);
}

@Test
public void testGammasStayFiniteAndValidRange() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State state = mock(Transducer.State.class);
when(input.size()).thenReturn(2);
when(output.size()).thenReturn(2);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(iterator.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iterator);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, true, null);
double[][] gammas = lattice.getGammas();
for (int t = 0; t < gammas.length; t++) {
assertFalse(Double.isNaN(gammas[t][0]));
double prob = Math.exp(gammas[t][0]);
assertTrue(prob >= 0.0);
assertTrue(prob <= 1.0);
}
}
}
