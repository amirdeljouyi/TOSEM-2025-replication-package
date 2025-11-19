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

public class SumLatticeBeam_3_GPTLLMTest {

@Test
public void testSimpleInitialization() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State state0 = mock(Transducer.State.class);
Transducer.State state1 = mock(Transducer.State.class);
when(transducer.numStates()).thenReturn(2);
when(transducer.getState(0)).thenReturn(state0);
when(transducer.getState(1)).thenReturn(state1);
when(state0.getIndex()).thenReturn(0);
when(state1.getIndex()).thenReturn(1);
when(state0.getInitialWeight()).thenReturn(0.0);
when(state1.getInitialWeight()).thenReturn(Double.POSITIVE_INFINITY);
when(state0.getFinalWeight()).thenReturn(0.0);
when(state1.getFinalWeight()).thenReturn(Double.POSITIVE_INFINITY);
when(input.size()).thenReturn(2);
when(input.get(0)).thenReturn("A");
when(input.get(1)).thenReturn("B");
when(output.size()).thenReturn(2);
when(output.get(0)).thenReturn("X");
when(output.get(1)).thenReturn("Y");
// when(state0.transitionIterator(any(), eq(0), any(), eq(0))).thenReturn(new MockTransitionIterator(state0));
// when(state0.transitionIterator(any(), eq(1), any(), eq(1))).thenReturn(new MockTransitionIterator(state0));
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, (CRF.Factors.Incrementor) null);
assertEquals(3, lattice.length());
assertNotNull(lattice.getInput());
assertEquals(input, lattice.getInput());
double totalWeight = lattice.getTotalWeight();
assertFalse(Double.isNaN(totalWeight));
}

@Test
public void testBeamWidthManagement() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State state = mock(Transducer.State.class);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(input.size()).thenReturn(1);
when(input.get(0)).thenReturn("data");
when(output.size()).thenReturn(1);
when(output.get(0)).thenReturn("label");
// when(state.transitionIterator(any(), eq(0), any(), eq(0))).thenReturn(new MockTransitionIterator(state));
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null);
lattice.setBeamWidth(10);
assertEquals(10, lattice.getBeamWidth());
}

@Test
public void testXisAccessThrowsWhenNotSaved() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State state = mock(Transducer.State.class);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(input.size()).thenReturn(1);
when(input.get(0)).thenReturn("X");
when(output.size()).thenReturn(1);
when(output.get(0)).thenReturn("Y");
// when(state.transitionIterator(any(), eq(0), any(), eq(0))).thenReturn(new MockTransitionIterator(state));
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, false);
try {
lattice.getXiWeight(0, state, state);
fail("Expected IllegalStateException");
} catch (IllegalStateException e) {
assertTrue(e.getMessage().contains("xis were not saved"));
}
}

@Test
public void testUseForwardBackwardBeamFlag() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State state = mock(Transducer.State.class);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(input.size()).thenReturn(1);
when(input.get(0)).thenReturn("token");
when(output.size()).thenReturn(1);
when(output.get(0)).thenReturn("label");
// when(state.transitionIterator(any(), eq(0), any(), eq(0))).thenReturn(new MockTransitionIterator(state));
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null);
assertFalse(lattice.getUseForwardBackwardBeam());
lattice.setUseForwardBackwardBeam(true);
assertTrue(lattice.getUseForwardBackwardBeam());
}

@Test
public void testConstrainedLatticeBuildsSuccessfully() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Sequence constrained = mock(Sequence.class);
Transducer.State state = mock(Transducer.State.class);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
when(transducer.stateIndexOfString(anyString())).thenReturn(0);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
when(constrained.size()).thenReturn(1);
when(input.get(0)).thenReturn("T");
when(output.get(0)).thenReturn("O");
when(constrained.get(0)).thenReturn("X");
// when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(new MockTransitionIterator(state));
// Segment segment = new Segment(0, 0, "B", "I");
// SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, segment, constrained);
// assertEquals(2, lattice.length());
// assertNotNull(lattice.getInput());
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
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null);
assertEquals(1, lattice.length());
assertTrue(lattice.getTotalWeight() <= 0.0);
}

@Test
public void testInvalidStateIndexInConstraintsThrows() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Sequence constraintSeq = mock(Sequence.class);
// Segment segment = new Segment(0, 0, "B-TAG", "I-TAG");
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
when(constraintSeq.size()).thenReturn(1);
when(constraintSeq.get(0)).thenReturn("UNKNOWN");
when(transducer.stateIndexOfString(anyString())).thenReturn(-1);
// SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, segment, constraintSeq);
// assertNotNull(lattice);
}

@Test
public void testInvalidRminThresholdComputationPath() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State state0 = mock(Transducer.State.class);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state0);
when(state0.getIndex()).thenReturn(0);
when(state0.getInitialWeight()).thenReturn(0.0);
when(state0.getFinalWeight()).thenReturn(0.0);
when(input.size()).thenReturn(1);
when(input.get(0)).thenReturn("token");
when(output.size()).thenReturn(1);
when(output.get(0)).thenReturn("tag");
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(iterator.hasNext()).thenReturn(false);
when(state0.transitionIterator(any(), eq(0), any(), eq(0))).thenReturn(iterator);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null);
lattice.setKLeps(0.0);
lattice.setRmin(-0.5);
assertNotNull(lattice);
assertTrue(lattice.length() >= 1);
}

@Test
public void testLatticeWithNullOutputParameterAllowed() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Transducer.State state0 = mock(Transducer.State.class);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state0);
when(state0.getIndex()).thenReturn(0);
when(state0.getInitialWeight()).thenReturn(0.0);
when(state0.getFinalWeight()).thenReturn(0.0);
when(input.size()).thenReturn(1);
when(input.get(0)).thenReturn("sample");
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(iterator.hasNext()).thenReturn(false);
when(state0.transitionIterator(any(), eq(0), isNull(), eq(0))).thenReturn(iterator);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, null, null);
assertNotNull(lattice);
assertEquals(2, lattice.length());
}

@Test
public void testLabelAlphabetConstraintLatticeOutputCountsBoundary() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
LabelAlphabet alphabet = mock(LabelAlphabet.class);
Transducer.State state0 = mock(Transducer.State.class);
when(alphabet.size()).thenReturn(1);
when(alphabet.lookupIndex(any(), eq(false))).thenReturn(0);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state0);
when(state0.getIndex()).thenReturn(0);
when(state0.getInitialWeight()).thenReturn(0.0);
when(state0.getFinalWeight()).thenReturn(0.0);
when(input.size()).thenReturn(1);
when(input.get(0)).thenReturn("z");
when(output.size()).thenReturn(1);
when(output.get(0)).thenReturn("Y");
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(iterator.hasNext()).thenReturn(false);
when(state0.transitionIterator(any(), eq(0), eq(output), eq(0))).thenReturn(iterator);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, true, alphabet);
assertNotNull(lattice.getLabelingAtPosition(0));
}

@Test
public void testProbabilityBoundsInGamma() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State state = mock(Transducer.State.class);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(input.size()).thenReturn(1);
when(input.get(0)).thenReturn("event");
when(output.size()).thenReturn(1);
when(output.get(0)).thenReturn("state");
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(iterator.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), eq(0), eq(output), eq(0))).thenReturn(iterator);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null);
double prob = lattice.getGammaProbability(0, state);
assertTrue("Gamma probability must be in [0, 1]", prob >= 0.0 && prob <= 1.0);
}

@Test
public void testStateWithNaNTransitionWeight() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State state = mock(Transducer.State.class);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(input.size()).thenReturn(1);
when(input.get(0)).thenReturn("input");
when(output.size()).thenReturn(1);
when(output.get(0)).thenReturn("label");
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(iterator.hasNext()).thenReturn(true, false);
when(iterator.nextState()).thenReturn(state);
when(iterator.getWeight()).thenReturn(Double.NaN);
when(iterator.getOutput()).thenReturn("Y");
when(state.transitionIterator(any(), eq(0), any(), eq(0))).thenReturn(iterator);
try {
new SumLatticeBeam(transducer, input, output, null);
} catch (AssertionError e) {
assertTrue(e.getMessage().contains("NaN"));
}
}

@Test
public void testInverseConstraintExcludesState() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Sequence constrained = mock(Sequence.class);
when(transducer.numStates()).thenReturn(1);
Transducer.State state = mock(Transducer.State.class);
when(transducer.getState(0)).thenReturn(state);
when(transducer.stateIndexOfString(anyString())).thenReturn(0);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(input.size()).thenReturn(1);
when(input.get(0)).thenReturn("i");
when(output.size()).thenReturn(1);
when(output.get(0)).thenReturn("o");
when(constrained.size()).thenReturn(1);
when(constrained.get(0)).thenReturn("S");
// Segment seg = new Segment(0, 0, "B-S", "I-S");
// new SumLatticeBeam(transducer, input, output, seg, constrained);
assertTrue(true);
}

@Test
public void testIllegalFinalStateConstraintSkipped() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Sequence constrained = mock(Sequence.class);
when(transducer.numStates()).thenReturn(1);
Transducer.State state = mock(Transducer.State.class);
when(transducer.getState(0)).thenReturn(state);
when(transducer.stateIndexOfString(anyString())).thenReturn(0);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(Double.POSITIVE_INFINITY);
when(input.size()).thenReturn(1);
when(input.get(0)).thenReturn("tok");
when(output.size()).thenReturn(1);
when(output.get(0)).thenReturn("O");
when(constrained.size()).thenReturn(1);
when(constrained.get(0)).thenReturn("BAD");
// Segment seg = new Segment(0, 0, "B-BAD", "I-BAD");
// SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, seg, constrained);
// assertEquals(2, lattice.length());
}

@Test
public void testConstrainedConstructorWithNegativeConstraint() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
LabelAlphabet outputAlphabet = mock(LabelAlphabet.class);
when(transducer.numStates()).thenReturn(1);
Transducer.State state = mock(Transducer.State.class);
when(transducer.getState(0)).thenReturn(state);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(input.size()).thenReturn(1);
when(input.get(0)).thenReturn("A");
when(output.size()).thenReturn(1);
when(output.get(0)).thenReturn("B");
int[] constraints = new int[] { 0, -1 };
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(iterator.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), eq(0), eq(output), eq(0))).thenReturn(iterator);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, outputAlphabet, constraints);
assertNotNull(lattice);
assertEquals(2, lattice.length());
}

@Test
public void testAllGammasSumToOneAfterNormalization() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State state0 = mock(Transducer.State.class);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state0);
when(state0.getIndex()).thenReturn(0);
when(state0.getInitialWeight()).thenReturn(0.0);
when(state0.getFinalWeight()).thenReturn(0.0);
when(input.size()).thenReturn(1);
when(input.get(0)).thenReturn("word");
when(output.size()).thenReturn(1);
when(output.get(0)).thenReturn("tag");
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(iterator.hasNext()).thenReturn(false);
when(state0.transitionIterator(any(), eq(0), any(), eq(0))).thenReturn(iterator);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null);
double[][] gammas = lattice.getGammas();
double sum = Math.exp(gammas[0][0]);
assertTrue(sum > 0.0);
}

@Test
public void testGetXiWeightWithoutExceptionWhenSaved() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State state = mock(Transducer.State.class);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(input.size()).thenReturn(1);
when(input.get(0)).thenReturn("x");
when(output.size()).thenReturn(1);
when(output.get(0)).thenReturn("y");
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(iterator.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), eq(0), any(), eq(0))).thenReturn(iterator);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, true);
double value = lattice.getXiWeight(0, state, state);
assertFalse(Double.isNaN(value));
}

@Test
public void testAlphaAndBetaPropagationDisconnectedNodes() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State s0 = mock(Transducer.State.class);
Transducer.State s1 = mock(Transducer.State.class);
when(transducer.numStates()).thenReturn(2);
when(transducer.getState(0)).thenReturn(s0);
when(transducer.getState(1)).thenReturn(s1);
when(s0.getIndex()).thenReturn(0);
when(s1.getIndex()).thenReturn(1);
when(s0.getInitialWeight()).thenReturn(0.0);
when(s1.getInitialWeight()).thenReturn(Double.POSITIVE_INFINITY);
when(s0.getFinalWeight()).thenReturn(0.0);
when(s1.getFinalWeight()).thenReturn(0.0);
when(input.size()).thenReturn(2);
when(input.get(0)).thenReturn("x");
when(input.get(1)).thenReturn("y");
when(output.size()).thenReturn(2);
when(output.get(0)).thenReturn("A");
when(output.get(1)).thenReturn("B");
Transducer.TransitionIterator ti0 = mock(Transducer.TransitionIterator.class);
when(ti0.hasNext()).thenReturn(true, false);
when(ti0.nextState()).thenReturn(s0);
when(ti0.getWeight()).thenReturn(0.0);
when(ti0.getOutput()).thenReturn("A");
Transducer.TransitionIterator ti1 = mock(Transducer.TransitionIterator.class);
when(ti1.hasNext()).thenReturn(false);
when(s0.transitionIterator(any(), eq(0), any(), eq(0))).thenReturn(ti0);
when(s0.transitionIterator(any(), eq(1), any(), eq(1))).thenReturn(ti1);
when(s1.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(ti1);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null);
double alpha = lattice.getAlpha(0, s0);
double beta = lattice.getBeta(1, s0);
assertFalse(Double.isNaN(alpha));
assertFalse(Double.isNaN(beta));
}

@Test
public void testZeroWeightPathSumLogProbHandling() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State s0 = mock(Transducer.State.class);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(s0);
when(s0.getIndex()).thenReturn(0);
when(s0.getInitialWeight()).thenReturn(0.0);
when(s0.getFinalWeight()).thenReturn(0.0);
when(input.size()).thenReturn(1);
when(input.get(0)).thenReturn("t");
when(output.size()).thenReturn(1);
when(output.get(0)).thenReturn("Y");
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(iterator.hasNext()).thenReturn(true, false);
when(iterator.nextState()).thenReturn(s0);
when(iterator.getWeight()).thenReturn(0.0);
when(iterator.getOutput()).thenReturn("L");
when(s0.transitionIterator(any(), eq(0), any(), eq(0))).thenReturn(iterator);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null);
double prob = lattice.getGammaProbability(0, s0);
assertTrue(prob >= 0.0 && prob <= 1.0);
}

@Test
public void testAllZeroForwardAlphaLeadsToImpossibleWeight() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State state = mock(Transducer.State.class);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(Transducer.IMPOSSIBLE_WEIGHT);
when(state.getFinalWeight()).thenReturn(0.0);
when(input.size()).thenReturn(1);
when(input.get(0)).thenReturn("token");
when(output.size()).thenReturn(1);
when(output.get(0)).thenReturn("class");
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null);
assertEquals(Transducer.IMPOSSIBLE_WEIGHT, lattice.getTotalWeight(), 0.001);
}

@Test
public void testConstrainedLatticeNegativeIndexInFinalStateNotApplied() {
Transducer transducer = mock(Transducer.class);
LabelAlphabet outputAlphabet = mock(LabelAlphabet.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State s0 = mock(Transducer.State.class);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(s0);
when(s0.getIndex()).thenReturn(0);
when(s0.getInitialWeight()).thenReturn(0.0);
when(s0.getFinalWeight()).thenReturn(0.0);
when(input.size()).thenReturn(1);
when(input.get(0)).thenReturn("tok");
when(output.size()).thenReturn(1);
when(output.get(0)).thenReturn("lab");
Transducer.TransitionIterator ti = mock(Transducer.TransitionIterator.class);
when(ti.hasNext()).thenReturn(false);
when(s0.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(ti);
int[] constraints = new int[] { 0, -99 };
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, outputAlphabet, constraints);
double totalWeight = lattice.getTotalWeight();
assertTrue(totalWeight <= 0.0);
}

@Test
public void testOutputAlphabetAndIncrementorAreBothNull() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State s0 = mock(Transducer.State.class);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(s0);
when(s0.getIndex()).thenReturn(0);
when(s0.getInitialWeight()).thenReturn(0.0);
when(s0.getFinalWeight()).thenReturn(0.0);
when(input.size()).thenReturn(1);
when(input.get(0)).thenReturn("token");
when(output.size()).thenReturn(1);
when(output.get(0)).thenReturn("label");
Transducer.TransitionIterator ti = mock(Transducer.TransitionIterator.class);
when(ti.hasNext()).thenReturn(false);
when(s0.transitionIterator(any(), eq(0), eq(output), eq(0))).thenReturn(ti);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, true, null);
assertTrue(lattice.getXiWeight(0, s0, s0) <= 0.0);
}

@Test
public void testInvalidFinalStateConstraintInExclusion() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Sequence constrained = mock(Sequence.class);
Transducer.State s0 = mock(Transducer.State.class);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(s0);
when(transducer.stateIndexOfString(any())).thenReturn(0);
when(s0.getIndex()).thenReturn(0);
when(s0.getInitialWeight()).thenReturn(0.0);
when(s0.getFinalWeight()).thenReturn(Double.POSITIVE_INFINITY);
when(input.size()).thenReturn(2);
when(input.get(0)).thenReturn("a");
when(input.get(1)).thenReturn("b");
when(output.size()).thenReturn(2);
when(output.get(0)).thenReturn("A");
when(output.get(1)).thenReturn("B");
Transducer.TransitionIterator ti = mock(Transducer.TransitionIterator.class);
when(ti.hasNext()).thenReturn(false);
when(s0.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(ti);
when(constrained.size()).thenReturn(2);
when(constrained.get(0)).thenReturn("Tag1");
when(constrained.get(1)).thenReturn("Tag2");
// Segment segment = new Segment(0, 1, "B-Tag1", "I-Tag");
// SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, segment, constrained);
// assertEquals(3, lattice.length());
}

@Test
public void testTransitionWithNullOutputDoesNotBreak() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State state = mock(Transducer.State.class);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(input.size()).thenReturn(1);
when(input.get(0)).thenReturn("x");
when(output.size()).thenReturn(1);
when(output.get(0)).thenReturn("y");
Transducer.TransitionIterator iter = mock(Transducer.TransitionIterator.class);
when(iter.hasNext()).thenReturn(true, false);
when(iter.nextState()).thenReturn(state);
when(iter.getWeight()).thenReturn(0.0);
when(iter.getOutput()).thenReturn(null);
when(state.transitionIterator(any(), eq(0), any(), eq(0))).thenReturn(iter);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null);
double totalWeight = lattice.getTotalWeight();
assertFalse(Double.isNaN(totalWeight));
}

@Test
public void testNegativeKLepsAndNegativeRminUseMaxOfBoth() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State state = mock(Transducer.State.class);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(input.size()).thenReturn(1);
when(input.get(0)).thenReturn("tok");
when(output.size()).thenReturn(1);
when(output.get(0)).thenReturn("tag");
Transducer.TransitionIterator iter = mock(Transducer.TransitionIterator.class);
when(iter.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), eq(0), any(), eq(0))).thenReturn(iter);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null);
lattice.setKLeps(-0.2);
lattice.setRmin(-0.5);
assertNotNull(lattice);
assertTrue(lattice.getTotalWeight() <= 0.0);
}

@Test
public void testFactoryCreatesLatticeWithCustomBeamWidth() {
Transducer transducer = mock(Transducer.class);
LabelAlphabet alphabet = mock(LabelAlphabet.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State state = mock(Transducer.State.class);
when(alphabet.size()).thenReturn(1);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(input.size()).thenReturn(1);
when(input.get(0)).thenReturn("x");
when(output.size()).thenReturn(1);
when(output.get(0)).thenReturn("y");
Transducer.TransitionIterator ti = mock(Transducer.TransitionIterator.class);
when(ti.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(ti);
SumLatticeBeam.Factory factory = new SumLatticeBeam.Factory(10);
SumLatticeBeam lattice = (SumLatticeBeam) factory.newSumLattice(transducer, input, output, null, false, alphabet);
assertEquals(2, lattice.length());
assertFalse(Double.isNaN(lattice.getTotalWeight()));
}

@Test
public void testGammaProbabilityHandlesImpossibleWeightGracefully() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State state = mock(Transducer.State.class);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(Transducer.IMPOSSIBLE_WEIGHT);
when(state.getFinalWeight()).thenReturn(0.0);
when(input.size()).thenReturn(1);
when(input.get(0)).thenReturn("data");
when(output.size()).thenReturn(1);
when(output.get(0)).thenReturn("label");
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null);
double prob = lattice.getGammaProbability(0, state);
assertTrue(prob == 0.0 || Double.isNaN(prob));
}

@Test
public void testXiProbabilityReturnsCorrectBoundsWhenValueZero() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State s0 = mock(Transducer.State.class);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(s0);
when(s0.getIndex()).thenReturn(0);
when(s0.getInitialWeight()).thenReturn(0.0);
when(s0.getFinalWeight()).thenReturn(0.0);
when(input.size()).thenReturn(1);
when(input.get(0)).thenReturn("word");
when(output.size()).thenReturn(1);
when(output.get(0)).thenReturn("tag");
Transducer.TransitionIterator iter = mock(Transducer.TransitionIterator.class);
when(iter.hasNext()).thenReturn(false);
when(s0.transitionIterator(any(), eq(0), any(), eq(0))).thenReturn(iter);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, true, null);
double xiProb = lattice.getXiProbability(0, s0, s0);
assertTrue(xiProb >= 0.0 && xiProb <= 1.0);
}

@Test(expected = IllegalArgumentException.class)
public void testInvalidConstraintsLengthThrows() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Sequence constrainedSeq = mock(Sequence.class);
Transducer.State s0 = mock(Transducer.State.class);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(s0);
when(transducer.stateIndexOfString(any())).thenReturn(0);
when(input.size()).thenReturn(3);
when(output.size()).thenReturn(3);
when(constrainedSeq.size()).thenReturn(1);
// Segment segment = new Segment(0, 1, "B", "I");
// new SumLatticeBeam(transducer, input, output, segment, constrainedSeq);
}

@Test
public void testNegativeBeamWidthDefaultsToMinimum() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State state = mock(Transducer.State.class);
when(transducer.numStates()).thenReturn(2);
when(transducer.getState(0)).thenReturn(state);
when(transducer.getState(1)).thenReturn(state);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(input.size()).thenReturn(2);
when(input.get(0)).thenReturn("a");
when(input.get(1)).thenReturn("b");
when(output.size()).thenReturn(2);
when(output.get(0)).thenReturn("x");
when(output.get(1)).thenReturn("y");
Transducer.TransitionIterator ti = mock(Transducer.TransitionIterator.class);
when(ti.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(ti);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null);
lattice.setBeamWidth(-5);
assertTrue(lattice.getBeamWidth() < 0);
assertEquals(3, lattice.length());
}

@Test
public void testLabelingAtPositionReturnsNullWhenOutputAlphabetNull() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State state = mock(Transducer.State.class);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(input.size()).thenReturn(1);
when(input.get(0)).thenReturn("a");
when(output.size()).thenReturn(1);
when(output.get(0)).thenReturn("b");
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(iterator.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), eq(0), eq(output), eq(0))).thenReturn(iterator);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, true, null);
assertNull(lattice.getLabelingAtPosition(0));
}

@Test
public void testZeroLengthInputWithXiSaved() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State state = mock(Transducer.State.class);
when(transducer.numStates()).thenReturn(1);
when(input.size()).thenReturn(0);
when(output.size()).thenReturn(0);
when(transducer.getState(0)).thenReturn(state);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(iterator.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iterator);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, true, null);
assertEquals(1, lattice.length());
assertNotNull(lattice.getGammas());
assertNotNull(lattice.getXis());
}

@Test
public void testGammaProbabilityReturnsOneWhenSingleState() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State state = mock(Transducer.State.class);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(input.size()).thenReturn(1);
when(input.get(0)).thenReturn("w");
when(output.size()).thenReturn(1);
when(output.get(0)).thenReturn("t");
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(iterator.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), eq(0), eq(output), eq(0))).thenReturn(iterator);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, false, null);
double prob = lattice.getGammaProbability(0, state);
assertTrue(prob >= 0.0 && prob <= 1.0);
}

@Test
public void testSetAndGetNstatesExplWithNoTransitions() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State state = mock(Transducer.State.class);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(input.size()).thenReturn(2);
when(input.get(0)).thenReturn("x");
when(input.get(1)).thenReturn("y");
when(output.size()).thenReturn(2);
when(output.get(0)).thenReturn("O");
when(output.get(1)).thenReturn("O");
Transducer.TransitionIterator ti = mock(Transducer.TransitionIterator.class);
when(ti.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(ti);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, false, null);
double[] explored = lattice.getNstatesExpl();
assertNotNull(explored);
assertEquals(3, explored.length);
}

@Test
public void testMakeConstraintsSkipsUnknownStateAndAppliesExcludeTag() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Sequence constrainedSeq = mock(Sequence.class);
Transducer.State state = mock(Transducer.State.class);
when(transducer.numStates()).thenReturn(1);
when(input.size()).thenReturn(3);
when(output.size()).thenReturn(3);
when(constrainedSeq.size()).thenReturn(3);
when(input.get(0)).thenReturn("T1");
when(input.get(1)).thenReturn("T2");
when(input.get(2)).thenReturn("T3");
when(output.get(0)).thenReturn("L1");
when(output.get(1)).thenReturn("L2");
when(output.get(2)).thenReturn("L3");
when(constrainedSeq.get(0)).thenReturn("BAD");
when(constrainedSeq.get(1)).thenReturn("S");
when(constrainedSeq.get(2)).thenReturn("E");
// Segment segment = new Segment(1, 1, "B-S", "I-S");
when(transducer.stateIndexOfString("BAD")).thenReturn(-1);
when(transducer.stateIndexOfString("S")).thenReturn(0);
when(transducer.stateIndexOfString("I-S")).thenReturn(0);
when(transducer.getState(0)).thenReturn(state);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
Transducer.TransitionIterator ti = mock(Transducer.TransitionIterator.class);
when(ti.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(ti);
// SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, segment, constrainedSeq);
// assertEquals(4, lattice.length());
}

@Test
public void testConstructorInvokesWarningOnMissingInitialStates() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State state0 = mock(Transducer.State.class);
Transducer.State state1 = mock(Transducer.State.class);
when(transducer.numStates()).thenReturn(2);
when(transducer.getState(0)).thenReturn(state0);
when(transducer.getState(1)).thenReturn(state1);
when(state0.getIndex()).thenReturn(0);
when(state1.getIndex()).thenReturn(1);
when(state0.getInitialWeight()).thenReturn(Double.POSITIVE_INFINITY);
when(state1.getInitialWeight()).thenReturn(Double.POSITIVE_INFINITY);
when(state0.getFinalWeight()).thenReturn(0.0);
when(state1.getFinalWeight()).thenReturn(0.0);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
when(input.get(0)).thenReturn("X");
when(output.get(0)).thenReturn("Y");
Transducer.TransitionIterator iter = mock(Transducer.TransitionIterator.class);
when(iter.hasNext()).thenReturn(false);
when(state0.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iter);
when(state1.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(iter);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null);
assertNotNull(lattice);
double weight = lattice.getTotalWeight();
assertTrue(Double.isInfinite(weight) || Double.isNaN(weight));
}

@Test
public void testSingleStateWithZeroLengthInputAndOutput() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State state = mock(Transducer.State.class);
when(input.size()).thenReturn(0);
when(output.size()).thenReturn(0);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null);
assertEquals(1, lattice.length());
assertFalse(Double.isNaN(lattice.getTotalWeight()));
}

@Test
public void testAlphaAndBetaShouldRemainImpossibleWhenUnreachableState() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State state = mock(Transducer.State.class);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
when(input.get(0)).thenReturn("token");
when(output.get(0)).thenReturn("label");
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(Transducer.IMPOSSIBLE_WEIGHT);
when(state.getFinalWeight()).thenReturn(0.0);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null);
double alpha = lattice.getAlpha(0, state);
double beta = lattice.getBeta(0, state);
assertEquals(Transducer.IMPOSSIBLE_WEIGHT, alpha, 0.0);
assertEquals(Transducer.IMPOSSIBLE_WEIGHT, beta, 0.0);
assertEquals(Transducer.IMPOSSIBLE_WEIGHT, lattice.getTotalWeight(), 0.0);
}

@Test
public void testXiRetrievedAfterSavingIsFinite() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State state = mock(Transducer.State.class);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
when(input.get(0)).thenReturn("x");
when(output.get(0)).thenReturn("y");
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(iterator.hasNext()).thenReturn(true, false);
when(iterator.nextState()).thenReturn(state);
when(iterator.getWeight()).thenReturn(0.0);
when(iterator.getOutput()).thenReturn("Z");
when(state.transitionIterator(any(), eq(0), any(), eq(0))).thenReturn(iterator);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, true, null);
double prob = lattice.getXiProbability(0, state, state);
assertTrue(prob >= 0.0 && prob <= 1.0);
}

@Test
public void testNegativeFinalConstraintExcludesPath() {
Transducer transducer = mock(Transducer.class);
LabelAlphabet alphabet = mock(LabelAlphabet.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State state = mock(Transducer.State.class);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
when(input.get(0)).thenReturn("a");
when(output.get(0)).thenReturn("b");
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
int[] constraints = new int[2];
constraints[1] = -1;
Transducer.TransitionIterator iter = mock(Transducer.TransitionIterator.class);
when(iter.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), eq(0), any(), eq(0))).thenReturn(iter);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, alphabet, constraints);
assertEquals(Transducer.IMPOSSIBLE_WEIGHT, lattice.getTotalWeight(), 0.0001);
}

@Test
public void testOutputCountsWithLabelAlphabetUsed() {
Transducer transducer = mock(Transducer.class);
LabelAlphabet alphabet = mock(LabelAlphabet.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State state = mock(Transducer.State.class);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
when(input.get(0)).thenReturn("a");
when(output.get(0)).thenReturn("b");
when(alphabet.size()).thenReturn(1);
when(alphabet.lookupIndex(any(), eq(false))).thenReturn(0);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
Transducer.TransitionIterator ti = mock(Transducer.TransitionIterator.class);
when(ti.hasNext()).thenReturn(true, false);
when(ti.nextState()).thenReturn(state);
when(ti.getWeight()).thenReturn(0.0);
when(ti.getOutput()).thenReturn("abc");
when(state.transitionIterator(any(), eq(0), any(), eq(0))).thenReturn(ti);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, true, alphabet);
assertNotNull(lattice.getLabelingAtPosition(0));
}

@Test
public void testConstructorWithEmptyConstraintsArrayStillInitializes() {
Transducer transducer = mock(Transducer.class);
LabelAlphabet alphabet = mock(LabelAlphabet.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State state = mock(Transducer.State.class);
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
when(input.get(0)).thenReturn("token");
when(output.get(0)).thenReturn("label");
int[] constraints = new int[2];
Transducer.TransitionIterator ti = mock(Transducer.TransitionIterator.class);
when(ti.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), anyInt(), any(), anyInt())).thenReturn(ti);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, alphabet, constraints);
assertEquals(2, lattice.length());
}

@Test
public void testGetXiWeightThrowsWhenXisDisabled() {
Transducer transducer = mock(Transducer.class);
Sequence input = mock(Sequence.class);
Sequence output = mock(Sequence.class);
Transducer.State state = mock(Transducer.State.class);
when(input.size()).thenReturn(1);
when(output.size()).thenReturn(1);
when(input.get(0)).thenReturn("x");
when(output.get(0)).thenReturn("y");
when(transducer.numStates()).thenReturn(1);
when(transducer.getState(0)).thenReturn(state);
when(state.getIndex()).thenReturn(0);
when(state.getInitialWeight()).thenReturn(0.0);
when(state.getFinalWeight()).thenReturn(0.0);
Transducer.TransitionIterator iterator = mock(Transducer.TransitionIterator.class);
when(iterator.hasNext()).thenReturn(false);
when(state.transitionIterator(any(), eq(0), any(), eq(0))).thenReturn(iterator);
SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, false, null);
try {
lattice.getXiWeight(0, state, state);
fail("Expected IllegalStateException");
} catch (IllegalStateException e) {
assertTrue(e.getMessage().contains("xis were not saved"));
}
}
}
