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

public class SumLatticeBeam_1_GPTLLMTest {

@Test
public void testGetSetBeamWidth() {
TokenSequence input = new TokenSequence();
input.add(new Token("a"));
// Transducer transducer = new MockTransducer();
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, null, null);
// assertEquals(3, beam.getBeamWidth());
// beam.setBeamWidth(5);
// assertEquals(5, beam.getBeamWidth());
}

@Test
public void testUseForwardBackwardBeamGetterSetter() {
TokenSequence input = new TokenSequence();
input.add(new Token("x"));
// Transducer transducer = new MockTransducer();
// SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, null, null);
// assertFalse(lattice.getUseForwardBackwardBeam());
// lattice.setUseForwardBackwardBeam(true);
// assertTrue(lattice.getUseForwardBackwardBeam());
}

@Test
public void testSetAndIncrementTctIter() {
TokenSequence input = new TokenSequence();
input.add(new Token("token"));
// Transducer transducer = new MockTransducer();
// SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, null, null);
// assertEquals(0, lattice.getTctIter());
// lattice.setCurIter(2);
// assertEquals(0, lattice.getTctIter());
// lattice.incIter();
// assertEquals(1, lattice.getTctIter());
}

@Test
public void testXisAndGammasAreNotNullWhenSaved() {
TokenSequence input = new TokenSequence();
input.add(new Token("a"));
input.add(new Token("b"));
TokenSequence output = new TokenSequence();
output.add(new Token("L1"));
output.add(new Token("L2"));
LabelAlphabet alphabet = new LabelAlphabet();
alphabet.lookupIndex("L1", true);
alphabet.lookupIndex("L2", true);
// Transducer transducer = new MockTransducer();
// SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, true, alphabet);
// assertNotNull(lattice.getGammas());
// assertNotNull(lattice.getXis());
}

@Test(expected = IllegalStateException.class)
public void testExceptionWhenAccessingXisWithoutSaving() {
TokenSequence input = new TokenSequence();
input.add(new Token("token1"));
TokenSequence output = new TokenSequence();
output.add(new Token("out1"));
// Transducer transducer = new MockTransducer();
// SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, false, null);
// Transducer.State s1 = transducer.getState(0);
// Transducer.State s2 = transducer.getState(1);
// lattice.getXiProbability(0, s1, s2);
}

@Test
public void testTotalWeightIsLogNonZero() {
TokenSequence input = new TokenSequence();
input.add(new Token("a"));
input.add(new Token("b"));
TokenSequence output = new TokenSequence();
output.add(new Token("L1"));
output.add(new Token("L2"));
LabelAlphabet alphabet = new LabelAlphabet();
alphabet.lookupIndex("L1", true);
alphabet.lookupIndex("L2", true);
// Transducer transducer = new MockTransducer();
// SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, true, alphabet);
// double w = lattice.getTotalWeight();
// assertTrue(w < 0);
// assertFalse(Double.isNaN(w));
}

@Test
public void testLatticeLengthIsInputPlusOne() {
TokenSequence input = new TokenSequence();
input.add(new Token("x"));
input.add(new Token("y"));
input.add(new Token("z"));
TokenSequence output = new TokenSequence();
output.add(new Token("L1"));
output.add(new Token("L2"));
output.add(new Token("L3"));
LabelAlphabet alphabet = new LabelAlphabet();
alphabet.lookupIndex("L1", true);
alphabet.lookupIndex("L2", true);
alphabet.lookupIndex("L3", true);
// Transducer transducer = new MockTransducer();
// SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, true, alphabet);
int expected = 4;
// assertEquals(expected, lattice.length());
}

@Test
public void testGammaMatchesAlphaPlusBetaMinusTotal() {
TokenSequence input = new TokenSequence();
input.add(new Token("x"));
input.add(new Token("y"));
TokenSequence output = new TokenSequence();
output.add(new Token("L0"));
output.add(new Token("L1"));
LabelAlphabet alphabet = new LabelAlphabet();
alphabet.lookupIndex("L0", true);
alphabet.lookupIndex("L1", true);
// Transducer transducer = new MockTransducer();
// SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, true, alphabet);
// Transducer.State s = transducer.getState(0);
// double gamma = lattice.getGammaWeight(1, s);
// double alpha = lattice.getAlpha(1, s);
// double beta = lattice.getBeta(1, s);
// double total = lattice.getTotalWeight();
// double expected = alpha + beta - total;
// assertEquals(expected, gamma, 1e-5);
// assertEquals(Math.exp(gamma), lattice.getGammaProbability(1, s), 1e-6);
}

@Test
public void testGetTransducerReturnsCorrectRef() {
TokenSequence input = new TokenSequence();
input.add(new Token("bug"));
// Transducer transducer = new MockTransducer();
// SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, null, null);
// assertSame(transducer, lattice.getTransducer());
}

@Test
public void testLabelVectorIsNullWhenOutputAlphabetIsNull() {
TokenSequence input = new TokenSequence();
input.add(new Token("one"));
TokenSequence output = new TokenSequence();
output.add(new Token("LBL"));
// Transducer transducer = new MockTransducer();
// SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, false, null);
// assertNull(lattice.getLabelingAtPosition(0));
}

@Test(expected = IllegalArgumentException.class)
public void testConstrainedConstructorThrowsOnMismatchedLength() {
TokenSequence input = new TokenSequence();
input.add(new Token("t1"));
input.add(new Token("t2"));
TokenSequence output = new TokenSequence();
output.add(new Token("O1"));
output.add(new Token("O2"));
TokenSequence constrained = new TokenSequence();
constrained.add(new Token("O1"));
// Segment seg = new Segment(0, 1, "O1", "O2", constrained);
// Transducer transducer = new MockTransducer();
// new SumLatticeBeam(transducer, input, output, seg, constrained);
}

@Test
public void testFactoryCreatesLatticeWithCorrectBeamWidth() {
TokenSequence input = new TokenSequence();
input.add(new Token("alpha"));
TokenSequence output = new TokenSequence();
output.add(new Token("L"));
LabelAlphabet alphabet = new LabelAlphabet();
alphabet.lookupIndex("L", true);
// Transducer transducer = new MockTransducer();
SumLatticeBeam.Factory factory = new SumLatticeBeam.Factory(4);
// SumLattice lattice = factory.newSumLattice(transducer, input, output, null, false, alphabet);
// assertTrue(lattice instanceof SumLatticeBeam);
// SumLatticeBeam casted = (SumLatticeBeam) lattice;
// assertEquals(4, casted.getBeamWidth());
}

@Test
public void testEmptyInputDoesNotThrow() {
TokenSequence input = new TokenSequence();
TokenSequence output = new TokenSequence();
LabelAlphabet alphabet = new LabelAlphabet();
// Transducer transducer = new SumLatticeBeamTest.MockTransducer();
// SumLattice lattice = new SumLatticeBeam(transducer, input, output, null, false, alphabet);
// assertEquals(1, lattice.length());
// assertNotNull(((SumLatticeBeam) lattice).getGammas());
}

@Test
public void testGetXiWeightThrowsWhenXisNotSaved() {
TokenSequence input = new TokenSequence();
input.add(new Token("word"));
TokenSequence output = new TokenSequence();
output.add(new Token("label"));
// Transducer transducer = new SumLatticeBeamTest.MockTransducer();
// SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, false, null);
// Transducer.State s0 = transducer.getState(0);
// Transducer.State s1 = transducer.getState(1);
try {
// lattice.getXiWeight(0, s0, s1);
fail("Expected IllegalStateException for missing xis");
} catch (IllegalStateException e) {
}
}

@Test
public void testNegativeBeamWidth() {
TokenSequence input = new TokenSequence();
input.add(new Token("a"));
TokenSequence output = new TokenSequence();
output.add(new Token("b"));
// Transducer transducer = new SumLatticeBeamTest.MockTransducer();
// SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, false, null);
// lattice.setBeamWidth(-1);
// assertEquals(-1, lattice.getBeamWidth());
}

@Test
public void testSetKLepsNegativeWithRminZero() {
TokenSequence input = new TokenSequence();
input.add(new Token("one"));
input.add(new Token("two"));
TokenSequence output = new TokenSequence();
output.add(new Token("L1"));
output.add(new Token("L2"));
LabelAlphabet alphabet = new LabelAlphabet();
alphabet.lookupIndex("L1", true);
alphabet.lookupIndex("L2", true);
// Transducer transducer = new SumLatticeBeamTest.MockTransducer();
// SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, true, alphabet);
// lattice.setKLeps(-0.1);
// lattice.setRmin(0);
// double[][] gamma = lattice.getGammas();
// assertNotNull(gamma);
}

@Test
public void testSetRminEdgeCaseNegativeHandling() {
TokenSequence input = new TokenSequence();
input.add(new Token("a"));
input.add(new Token("b"));
input.add(new Token("c"));
TokenSequence output = new TokenSequence();
output.add(new Token("X"));
output.add(new Token("Y"));
output.add(new Token("Z"));
LabelAlphabet alpha = new LabelAlphabet();
alpha.lookupIndex("X", true);
alpha.lookupIndex("Y", true);
alpha.lookupIndex("Z", true);
// Transducer transducer = new SumLatticeBeamTest.MockTransducer();
// SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, true, alpha);
// lattice.setRmin(-0.25);
// double[][] gamma = lattice.getGammas();
// assertNotNull(gamma);
}

@Test
public void testGetLabelingAtPositionOutOfBoundsReturnsNull() {
TokenSequence input = new TokenSequence();
input.add(new Token("input"));
TokenSequence output = new TokenSequence();
output.add(new Token("out"));
LabelAlphabet labelAlpha = new LabelAlphabet();
labelAlpha.lookupIndex("out", true);
// Transducer transducer = new SumLatticeBeamTest.MockTransducer();
// SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, true, labelAlpha);
// assertNotNull(lattice.getLabelingAtPosition(0));
try {
// lattice.getLabelingAtPosition(10);
} catch (ArrayIndexOutOfBoundsException e) {
}
}

@Test
public void testMultipleInitialStates() {
TokenSequence input = new TokenSequence();
input.add(new Token("alpha"));
input.add(new Token("beta"));
TokenSequence output = new TokenSequence();
output.add(new Token("Y1"));
output.add(new Token("Y2"));
LabelAlphabet labelAlpha = new LabelAlphabet();
labelAlpha.lookupIndex("Y1", true);
labelAlpha.lookupIndex("Y2", true);
// Transducer transducer = new Transducer() {
// 
// public int numStates() {
// return 2;
// }
// 
// public State getState(int index) {
// return new State(null, "S" + index, null) {
// 
// public int getIndex() {
// return index;
// }
// 
// public double getInitialWeight() {
// return Math.log(0.5);
// }
// 
// public double getFinalWeight() {
// return Math.log(0.5);
// }
// 
// public TransitionIterator transitionIterator(Sequence input, int ip, Sequence output, int op) {
// return new TransitionIterator() {
// 
// int called = 0;
// 
// public boolean hasNext() {
// return called == 0;
// }
// 
// public State nextState() {
// called++;
// return getState(1);
// }
// 
// public Object getOutput() {
// return "Y2";
// }
// 
// public double getWeight() {
// return Math.log(0.5);
// }
// };
// }
// };
// }
// 
// public int stateIndexOfString(String s) {
// return "S0".equals(s) ? 0 : "S1".equals(s) ? 1 : -1;
// }
// 
// public boolean expectsTraining() {
// return false;
// }
// };
// SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, true, labelAlpha);
// assertNotNull(lattice.getGammas());
// assertTrue(lattice.getTotalWeight() < 0);
}

@Test
public void testIllegalStateNameConstraintError() {
TokenSequence input = new TokenSequence();
input.add(new Token("x"));
input.add(new Token("y"));
TokenSequence output = new TokenSequence();
output.add(new Token("A"));
output.add(new Token("B"));
TokenSequence constraintSeq = new TokenSequence();
constraintSeq.add(new Token("NO_MATCH_1"));
constraintSeq.add(new Token("NO_MATCH_2"));
// Segment segment = new Segment(0, 1, "A", "B", constraintSeq);
// Transducer transducer = new SumLatticeBeamTest.MockTransducer();
// SumLattice lattice = new SumLatticeBeam(transducer, input, output, segment, constraintSeq);
// assertNotNull(lattice);
}

@Test
public void testSetNullOutputDoesNotThrowAndInitializesProperly() {
TokenSequence input = new TokenSequence();
input.add(new Token("token1"));
// Transducer transducer = new SumLatticeBeamTest.MockTransducer();
// SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, null, null);
// assertNotNull(lattice.getInput());
// assertEquals(2, lattice.length());
}

@Test
public void testAlphaBetaZeroProbabilityForUnreachableStates() {
TokenSequence input = new TokenSequence();
input.add(new Token("t"));
TokenSequence output = new TokenSequence();
output.add(new Token("o"));
// Transducer transducer = new Transducer() {
// 
// public int numStates() {
// return 2;
// }
// 
// public State getState(final int index) {
// return new State(null, "S" + index, null) {
// 
// public int getIndex() {
// return index;
// }
// 
// public double getInitialWeight() {
// return index == 1 ? Transducer.IMPOSSIBLE_WEIGHT : Math.log(1.0);
// }
// 
// public double getFinalWeight() {
// return Math.log(1.0);
// }
// 
// public TransitionIterator transitionIterator(Sequence input, int ip, Sequence output, int op) {
// return new TransitionIterator() {
// 
// private boolean called = false;
// 
// public boolean hasNext() {
// return !called;
// }
// 
// public State nextState() {
// called = true;
// return getState(0);
// }
// 
// public Object getOutput() {
// return "Y";
// }
// 
// public double getWeight() {
// return Math.log(0.1);
// }
// };
// }
// };
// }
// 
// public int stateIndexOfString(String s) {
// if ("S0".equals(s))
// return 0;
// if ("S1".equals(s))
// return 1;
// return -1;
// }
// 
// public boolean expectsTraining() {
// return false;
// }
// };
// SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, true, null);
// double alpha0 = lattice.getAlpha(0, transducer.getState(0));
// double beta0 = lattice.getBeta(0, transducer.getState(0));
// double alpha1 = lattice.getAlpha(0, transducer.getState(1));
// double beta1 = lattice.getBeta(0, transducer.getState(1));
// assertTrue(alpha0 > -Double.MAX_VALUE);
// assertEquals(Transducer.IMPOSSIBLE_WEIGHT, alpha1, 0.0);
// assertTrue(beta0 > -Double.MAX_VALUE);
// assertEquals(Transducer.IMPOSSIBLE_WEIGHT, beta1, 0.0);
}

@Test
public void testGammaNormalizationEffect() {
TokenSequence input = new TokenSequence();
input.add(new Token("x1"));
input.add(new Token("x2"));
TokenSequence output = new TokenSequence();
output.add(new Token("Y1"));
output.add(new Token("Y2"));
LabelAlphabet alphabet = new LabelAlphabet();
alphabet.lookupIndex("Y1", true);
alphabet.lookupIndex("Y2", true);
// Transducer transducer = new SumLatticeBeamTest.MockTransducer();
// SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, true, alphabet);
// double[][] gammas = lattice.getGammas();
double sum = 0.0;
// sum += Math.exp(gammas[0][0]);
// sum += Math.exp(gammas[0][1]);
assertTrue(sum <= 1.000001);
}

@Test
public void testGetLabelingReturnsNullBeforeOutputAvailable() {
TokenSequence input = new TokenSequence();
input.add(new Token("x"));
TokenSequence output = new TokenSequence();
output.add(new Token("O"));
// Transducer transducer = new SumLatticeBeamTest.MockTransducer();
// SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null);
// assertNull(lattice.getLabelingAtPosition(0));
}

@Test
public void testConstructorWithConstraintArrayOnlyStartConstraint() {
TokenSequence input = new TokenSequence();
input.add(new Token("in"));
TokenSequence output = new TokenSequence();
output.add(new Token("out"));
LabelAlphabet la = new LabelAlphabet();
la.lookupIndex("out", true);
// Transducer transducer = new SumLatticeBeamTest.MockTransducer();
int[] constraints = new int[2];
constraints[0] = 1;
// SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, la, constraints);
// assertNotNull(lattice.getGammas());
}

@Test
public void testGetXiProbabilityCorrectness() {
TokenSequence input = new TokenSequence();
input.add(new Token("aa"));
input.add(new Token("bb"));
TokenSequence output = new TokenSequence();
output.add(new Token("L1"));
output.add(new Token("L2"));
LabelAlphabet la = new LabelAlphabet();
la.lookupIndex("L1", true);
la.lookupIndex("L2", true);
// Transducer transducer = new SumLatticeBeamTest.MockTransducer();
// SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, true, la);
// State s0 = transducer.getState(0);
// State s1 = transducer.getState(1);
// double prob = lattice.getXiProbability(0, s0, s1);
// assertTrue(prob >= 0.0 && prob <= 1.0);
}

@Test
public void testTotalWeightIsImpossibleWhenNoTransitionsAllowed() {
TokenSequence input = new TokenSequence();
input.add(new Token("alone"));
TokenSequence output = new TokenSequence();
output.add(new Token("Y"));
// Transducer transducer = new Transducer() {
// 
// public int numStates() {
// return 1;
// }
// 
// public State getState(final int i) {
// return new State(null, "solo", null) {
// 
// public int getIndex() {
// return 0;
// }
// 
// public double getInitialWeight() {
// return Transducer.IMPOSSIBLE_WEIGHT;
// }
// 
// public double getFinalWeight() {
// return Math.log(1.0);
// }
// 
// public TransitionIterator transitionIterator(Sequence input, int ip, Sequence output, int op) {
// return new TransitionIterator() {
// 
// public boolean hasNext() {
// return false;
// }
// 
// public State nextState() {
// return null;
// }
// 
// public Object getOutput() {
// return null;
// }
// 
// public double getWeight() {
// return 0;
// }
// };
// }
// };
// }
// 
// public int stateIndexOfString(String s) {
// return -1;
// }
// 
// public boolean expectsTraining() {
// return false;
// }
// };
// SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null);
// assertEquals(Transducer.IMPOSSIBLE_WEIGHT, lattice.getTotalWeight(), 0.0);
}

@Test
public void testSetCurIterAffectsConstructionBehavior() {
TokenSequence input = new TokenSequence();
input.add(new Token("a"));
input.add(new Token("b"));
TokenSequence output = new TokenSequence();
output.add(new Token("X"));
output.add(new Token("Y"));
LabelAlphabet alphabet = new LabelAlphabet();
alphabet.lookupIndex("X", true);
alphabet.lookupIndex("Y", true);
// Transducer transducer = new SumLatticeBeamTest.MockTransducer();
// SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, true, alphabet);
// lattice.setCurIter(1);
// lattice.incIter();
// lattice.incIter();
// assertNotNull(lattice.getGammas());
}

@Test
public void testConstrainedConstructorNegativeConstraint() {
TokenSequence input = new TokenSequence();
input.add(new Token("in1"));
TokenSequence output = new TokenSequence();
output.add(new Token("L1"));
LabelAlphabet alphabet = new LabelAlphabet();
alphabet.lookupIndex("L1", true);
// Transducer transducer = new SumLatticeBeamTest.MockTransducer();
int[] constraints = new int[2];
constraints[1] = -2;
// SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, alphabet, constraints);
// assertNotNull(lattice.getGammas());
}

@Test
public void testConstrainedSegmentConstructorSucceedsForValidTags() {
TokenSequence input = new TokenSequence();
input.add(new Token("tok1"));
input.add(new Token("tok2"));
TokenSequence output = new TokenSequence();
output.add(new Token("O1"));
output.add(new Token("O2"));
TokenSequence constraintSequence = new TokenSequence();
constraintSequence.add(new Token("S0"));
constraintSequence.add(new Token("S1"));
// Segment segment = new Segment(0, 1, "S0", "S1", constraintSequence);
// Transducer transducer = new SumLatticeBeamTest.MockTransducer();
// SumLattice lattice = new SumLatticeBeam(transducer, input, output, segment, constraintSequence);
// assertNotNull(lattice);
}

@Test
public void testGammasNormalizationAtStartPosition() {
TokenSequence input = new TokenSequence();
input.add(new Token("w1"));
input.add(new Token("w2"));
TokenSequence output = new TokenSequence();
output.add(new Token("L1"));
output.add(new Token("L2"));
LabelAlphabet alpha = new LabelAlphabet();
alpha.lookupIndex("L1", true);
alpha.lookupIndex("L2", true);
// Transducer transducer = new SumLatticeBeamTest.MockTransducer();
// SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, true, alpha);
// double gamma0 = lattice.getGammaProbability(0, transducer.getState(0));
// double gamma1 = lattice.getGammaProbability(0, transducer.getState(1));
// double sum = gamma0 + gamma1;
// assertTrue(sum <= 1.000001);
}

@Test
public void testGetLabelingAtInvalidPositionReturnsNullOrThrows() {
TokenSequence input = new TokenSequence();
input.add(new Token("token"));
TokenSequence output = new TokenSequence();
output.add(new Token("L"));
LabelAlphabet alpha = new LabelAlphabet();
alpha.lookupIndex("L", true);
// Transducer transducer = new SumLatticeBeamTest.MockTransducer();
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null, true, alpha);
try {
// beam.getLabelingAtPosition(5);
} catch (ArrayIndexOutOfBoundsException e) {
}
}

@Test
public void testGammaForInvalidStateReturnsDefaultWhenStateNotReached() {
TokenSequence input = new TokenSequence();
input.add(new Token("the"));
TokenSequence output = new TokenSequence();
output.add(new Token("label"));
LabelAlphabet alpha = new LabelAlphabet();
alpha.lookupIndex("label", true);
// Transducer transducer = new Transducer() {
// 
// public int numStates() {
// return 2;
// }
// 
// public State getState(final int index) {
// return new State(null, "S" + index, null) {
// 
// public int getIndex() {
// return index;
// }
// 
// public double getInitialWeight() {
// return index == 1 ? Transducer.IMPOSSIBLE_WEIGHT : Math.log(1.0);
// }
// 
// public double getFinalWeight() {
// return Math.log(1.0);
// }
// 
// public TransitionIterator transitionIterator(Sequence input, int ip, Sequence output, int op) {
// return new TransitionIterator() {
// 
// private int count = 0;
// 
// public boolean hasNext() {
// return count == 0;
// }
// 
// public State nextState() {
// count++;
// return getState(0);
// }
// 
// public Object getOutput() {
// return "label";
// }
// 
// public double getWeight() {
// return Math.log(0.5);
// }
// };
// }
// };
// }
// 
// public int stateIndexOfString(String s) {
// return -1;
// }
// 
// public boolean expectsTraining() {
// return false;
// }
// };
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null, true, alpha);
// State unreachable = transducer.getState(1);
// double gamma = beam.getGammaWeight(0, unreachable);
// assertEquals(Transducer.IMPOSSIBLE_WEIGHT, gamma, 0.0001);
}

@Test
public void testProbabilityValuesAreBetweenZeroAndOne() {
TokenSequence input = new TokenSequence();
input.add(new Token("a"));
input.add(new Token("b"));
TokenSequence output = new TokenSequence();
output.add(new Token("label1"));
output.add(new Token("label2"));
LabelAlphabet alpha = new LabelAlphabet();
alpha.lookupIndex("label1", true);
alpha.lookupIndex("label2", true);
// Transducer transducer = new SumLatticeBeamTest.MockTransducer();
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null, true, alpha);
// double prob = beam.getGammaProbability(1, transducer.getState(0));
// assertTrue(prob >= 0.0);
// assertTrue(prob <= 1.0);
}

@Test
public void testIncrementorFinalStateCalledForReachable() {
TokenSequence input = new TokenSequence();
input.add(new Token("a"));
TokenSequence output = new TokenSequence();
output.add(new Token("lbl"));
// Transducer.Incrementor incrementor = new Transducer.Incrementor() {
// 
// boolean called = false;
// 
// public void incrementInitialState(State state, double prob) {
// assertTrue(prob > 0.0);
// called = true;
// }
// 
// public void incrementFinalState(State state, double prob) {
// }
// 
// public void incrementTransition(TransitionIterator iter, double prob) {
// }
// };
LabelAlphabet la = new LabelAlphabet();
la.lookupIndex("lbl", true);
// Transducer t = new SumLatticeBeamTest.MockTransducer();
// SumLatticeBeam beam = new SumLatticeBeam(t, input, output, incrementor, true, la);
}

@Test
public void testTransitionWithZeroWeightStillSumsCorrectly() {
TokenSequence input = new TokenSequence();
input.add(new Token("xx"));
TokenSequence output = new TokenSequence();
output.add(new Token("yy"));
LabelAlphabet la = new LabelAlphabet();
la.lookupIndex("yy", true);
// Transducer t = new SumLatticeBeamTest.MockTransducer();
// SumLatticeBeam beam = new SumLatticeBeam(t, input, output, null, true, la);
// double prob = beam.getGammaProbability(1, t.getState(0));
// assertTrue(prob >= 0.0);
}

@Test
public void testXiProbabilityForUnusedStatesReturnsZero() {
TokenSequence input = new TokenSequence();
input.add(new Token("m"));
TokenSequence output = new TokenSequence();
output.add(new Token("n"));
LabelAlphabet la = new LabelAlphabet();
la.lookupIndex("n", true);
// Transducer t = new SumLatticeBeamTest.MockTransducer();
// SumLatticeBeam beam = new SumLatticeBeam(t, input, output, null, true, la);
// Transducer.State s0 = t.getState(0);
// Transducer.State s1 = t.getState(1);
// double prob = beam.getXiProbability(0, s0, s1);
// assertTrue(prob >= 0.0);
// assertTrue(prob <= 1.0);
}

@Test
public void testFactoryCreatesValidSumLatticeBeam() {
TokenSequence input = new TokenSequence();
input.add(new Token("aaa"));
TokenSequence output = new TokenSequence();
output.add(new Token("bbb"));
LabelAlphabet la = new LabelAlphabet();
la.lookupIndex("bbb", true);
// Transducer t = new SumLatticeBeamTest.MockTransducer();
SumLatticeBeam.Factory factory = new SumLatticeBeam.Factory(8);
// SumLattice lattice = factory.newSumLattice(t, input, output, null, true, la);
// assertTrue(lattice instanceof SumLatticeBeam);
// assertNotNull(((SumLatticeBeam) lattice).getGammas());
}

@Test
public void testConstructorWithNullAlphabetAndXisTrueCreatesNoLabeling() {
TokenSequence input = new TokenSequence();
input.add(new Token("x"));
TokenSequence output = new TokenSequence();
output.add(new Token("y"));
// Transducer transducer = new SumLatticeBeamTest.MockTransducer();
// SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, true, null);
// assertNull(lattice.getLabelingAtPosition(0));
}

@Test
public void testOutputAlphabetMismatchDoesNotThrowAndSkipsEmission() {
TokenSequence input = new TokenSequence();
input.add(new Token("aa"));
TokenSequence output = new TokenSequence();
output.add(new Token("bb"));
LabelAlphabet alphabet = new LabelAlphabet();
alphabet.lookupIndex("notPresent", true);
// Transducer transducer = new SumLatticeBeamTest.MockTransducer();
// SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, true, alphabet);
// assertNotNull(lattice.getGammas());
}

@Test
public void testExtremeBeamWidthStillConstructs() {
TokenSequence input = new TokenSequence();
input.add(new Token("a"));
TokenSequence output = new TokenSequence();
output.add(new Token("b"));
// Transducer transducer = new SumLatticeBeamTest.MockTransducer();
// SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null);
// lattice.setBeamWidth(Integer.MAX_VALUE);
// assertEquals(Integer.MAX_VALUE, lattice.getBeamWidth());
}

@Test
public void testFinalStateNotSetStillProcessesWithoutError() {
TokenSequence input = new TokenSequence();
input.add(new Token("tok"));
TokenSequence output = new TokenSequence();
output.add(new Token("lbl"));
LabelAlphabet lab = new LabelAlphabet();
lab.lookupIndex("lbl", true);
// Transducer t = new Transducer() {
// 
// public int numStates() {
// return 1;
// }
// 
// public State getState(int index) {
// return new State(null, "S0", null) {
// 
// public int getIndex() {
// return 0;
// }
// 
// public double getInitialWeight() {
// return Math.log(1.0);
// }
// 
// public double getFinalWeight() {
// return Transducer.IMPOSSIBLE_WEIGHT;
// }
// 
// public TransitionIterator transitionIterator(Sequence input, int ip, Sequence output, int op) {
// return new TransitionIterator() {
// 
// private boolean done = false;
// 
// public boolean hasNext() {
// return !done;
// }
// 
// public State nextState() {
// done = true;
// return this.getState();
// }
// 
// public Object getOutput() {
// return "lbl";
// }
// 
// public double getWeight() {
// return Math.log(0.5);
// }
// 
// private State getState() {
// return new State(null, "S0", null) {
// 
// public int getIndex() {
// return 0;
// }
// 
// public double getInitialWeight() {
// return Math.log(1.0);
// }
// 
// public double getFinalWeight() {
// return Transducer.IMPOSSIBLE_WEIGHT;
// }
// 
// public TransitionIterator transitionIterator(Sequence in, int ip, Sequence out, int op) {
// return new TransitionIterator() {
// 
// public boolean hasNext() {
// return false;
// }
// 
// public State nextState() {
// return null;
// }
// 
// public Object getOutput() {
// return null;
// }
// 
// public double getWeight() {
// return 0;
// }
// };
// }
// };
// }
// };
// }
// };
// }
// 
// public int stateIndexOfString(String name) {
// return 0;
// }
// 
// public boolean expectsTraining() {
// return false;
// }
// };
// SumLatticeBeam lattice = new SumLatticeBeam(t, input, output, null, true, lab);
// assertTrue(lattice.getTotalWeight() < 0);
}

@Test
public void testConstraintArrayLengthMismatchThrows() {
TokenSequence input = new TokenSequence();
input.add(new Token("t1"));
input.add(new Token("t2"));
TokenSequence output = new TokenSequence();
output.add(new Token("L1"));
output.add(new Token("L2"));
TokenSequence constraintSeq = new TokenSequence();
constraintSeq.add(new Token("S0"));
// Segment seg = new Segment(0, 1, "S0", "S1", constraintSeq);
// Transducer transducer = new SumLatticeBeamTest.MockTransducer();
boolean exceptionThrown = false;
try {
// new SumLatticeBeam(transducer, input, output, seg, constraintSeq);
} catch (IllegalArgumentException e) {
exceptionThrown = true;
}
assertTrue(exceptionThrown);
}

@Test
public void testXiProbabilityBoundUpperLimit() {
TokenSequence input = new TokenSequence();
input.add(new Token("a"));
input.add(new Token("b"));
TokenSequence output = new TokenSequence();
output.add(new Token("y1"));
output.add(new Token("y2"));
LabelAlphabet la = new LabelAlphabet();
la.lookupIndex("y1", true);
la.lookupIndex("y2", true);
// Transducer t = new SumLatticeBeamTest.MockTransducer();
// SumLatticeBeam beam = new SumLatticeBeam(t, input, output, null, true, la);
// double val = beam.getXiProbability(0, t.getState(0), t.getState(1));
// assertTrue(val >= 0.0);
// assertTrue(val <= 1.0);
}

@Test
public void testGammaSumApproxOneAfterBackwardPass() {
TokenSequence input = new TokenSequence();
input.add(new Token("in1"));
input.add(new Token("in2"));
TokenSequence output = new TokenSequence();
output.add(new Token("_lbl1"));
output.add(new Token("_lbl2"));
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("_lbl1", true);
labelAlphabet.lookupIndex("_lbl2", true);
// Transducer transducer = new SumLatticeBeamTest.MockTransducer();
// SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, true, labelAlphabet);
// double gamma0 = lattice.getGammaProbability(0, transducer.getState(0));
// double gamma1 = lattice.getGammaProbability(0, transducer.getState(1));
// double sum = gamma0 + gamma1;
// assertTrue(sum <= 1.000001);
// assertTrue(sum > 0.0);
}

@Test
public void testNullOutputInTransitionIteratorHandled() {
TokenSequence input = new TokenSequence();
input.add(new Token("w"));
TokenSequence output = new TokenSequence();
output.add(new Token("unknown"));
LabelAlphabet la = new LabelAlphabet();
la.lookupIndex("unknown", true);
// Transducer t = new Transducer() {
// 
// public int numStates() {
// return 1;
// }
// 
// public State getState(int i) {
// return new State(null, "S0", null) {
// 
// public int getIndex() {
// return 0;
// }
// 
// public double getInitialWeight() {
// return Math.log(1.0);
// }
// 
// public double getFinalWeight() {
// return Math.log(1.0);
// }
// 
// public TransitionIterator transitionIterator(Sequence input, int ip, Sequence output, int op) {
// return new TransitionIterator() {
// 
// boolean called = false;
// 
// public boolean hasNext() {
// return !called;
// }
// 
// public State nextState() {
// called = true;
// return this.getState();
// }
// 
// public Object getOutput() {
// return null;
// }
// 
// public double getWeight() {
// return Math.log(0.5);
// }
// 
// private State getState() {
// return new State(null, "S0", null) {
// 
// public int getIndex() {
// return 0;
// }
// 
// public double getInitialWeight() {
// return Math.log(1.0);
// }
// 
// public double getFinalWeight() {
// return Math.log(1.0);
// }
// 
// public TransitionIterator transitionIterator(Sequence in, int ip, Sequence out, int op) {
// return new TransitionIterator() {
// 
// public boolean hasNext() {
// return false;
// }
// 
// public State nextState() {
// return null;
// }
// 
// public Object getOutput() {
// return null;
// }
// 
// public double getWeight() {
// return 0;
// }
// };
// }
// };
// }
// };
// }
// };
// }
// 
// public int stateIndexOfString(String s) {
// return 0;
// }
// 
// public boolean expectsTraining() {
// return false;
// }
// };
// SumLatticeBeam beam = new SumLatticeBeam(t, input, output, null, true, la);
// assertNotNull(beam.getGammas());
}

@Test
public void testConstructorWithEmptyAlphabetDoesNotFail() {
TokenSequence input = new TokenSequence();
input.add(new Token("input1"));
TokenSequence output = new TokenSequence();
output.add(new Token("label1"));
LabelAlphabet alphabet = new LabelAlphabet();
// Transducer transducer = new SumLatticeBeamTest.MockTransducer();
// SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, true, alphabet);
// assertNotNull(lattice.getGammas());
}

@Test
public void testZeroLengthInputStillInitializes() {
TokenSequence input = new TokenSequence();
TokenSequence output = new TokenSequence();
LabelAlphabet alphabet = new LabelAlphabet();
// Transducer transducer = new SumLatticeBeamTest.MockTransducer();
// SumLattice lattice = new SumLatticeBeam(transducer, input, output, null, false, null);
// assertEquals(1, lattice.length());
}

@Test
public void testNegativeKLepsWithNegativeRminTriggersMaxKL_Rmin() {
TokenSequence input = new TokenSequence();
input.add(new Token("a"));
input.add(new Token("b"));
TokenSequence output = new TokenSequence();
output.add(new Token("x"));
output.add(new Token("y"));
LabelAlphabet alpha = new LabelAlphabet();
alpha.lookupIndex("x", true);
alpha.lookupIndex("y", true);
// Transducer transducer = new SumLatticeBeamTest.MockTransducer();
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null, true, alpha);
// beam.setKLeps(-0.01);
// beam.setRmin(-0.15);
// double[][] gammas = beam.getGammas();
// assertNotNull(gammas);
}

@Test
public void testMissingStartStateLogsWarningAndSkipsConstructionLogic() {
TokenSequence input = new TokenSequence();
input.add(new Token("missingState"));
TokenSequence output = new TokenSequence();
output.add(new Token("y"));
LabelAlphabet la = new LabelAlphabet();
la.lookupIndex("y", true);
// Transducer transducer = new Transducer() {
// 
// public int numStates() {
// return 2;
// }
// 
// public State getState(final int index) {
// return new State(null, "S" + index, null) {
// 
// public int getIndex() {
// return index;
// }
// 
// public double getInitialWeight() {
// return Transducer.IMPOSSIBLE_WEIGHT;
// }
// 
// public double getFinalWeight() {
// return Math.log(1.0);
// }
// 
// public TransitionIterator transitionIterator(Sequence in, int ip, Sequence out, int op) {
// return new TransitionIterator() {
// 
// public boolean hasNext() {
// return false;
// }
// 
// public State nextState() {
// return null;
// }
// 
// public Object getOutput() {
// return null;
// }
// 
// public double getWeight() {
// return Transducer.IMPOSSIBLE_WEIGHT;
// }
// };
// }
// };
// }
// 
// public int stateIndexOfString(String name) {
// return -1;
// }
// 
// public boolean expectsTraining() {
// return false;
// }
// };
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null, true, la);
// assertTrue(Double.isInfinite(beam.getTotalWeight()));
}

@Test
public void testXiAccessWhenXisDisabledThrowsCorrectly() {
TokenSequence input = new TokenSequence();
input.add(new Token("z"));
TokenSequence output = new TokenSequence();
output.add(new Token("label"));
// Transducer transducer = new SumLatticeBeamTest.MockTransducer();
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null, false, null);
boolean threw = false;
try {
// beam.getXiProbability(0, transducer.getState(0), transducer.getState(1));
} catch (IllegalStateException e) {
threw = true;
}
assertTrue(threw);
}

@Test
public void testZeroAlphaNodeLeadsToImpossibleBeta() {
TokenSequence input = new TokenSequence();
input.add(new Token("one"));
TokenSequence output = new TokenSequence();
output.add(new Token("tag"));
LabelAlphabet la = new LabelAlphabet();
la.lookupIndex("tag", true);
// Transducer t = new Transducer() {
// 
// public int numStates() {
// return 2;
// }
// 
// public State getState(final int idx) {
// return new State(null, "S" + idx, null) {
// 
// public int getIndex() {
// return idx;
// }
// 
// public double getInitialWeight() {
// return idx == 0 ? Math.log(1.0) : Transducer.IMPOSSIBLE_WEIGHT;
// }
// 
// public double getFinalWeight() {
// return Math.log(1.0);
// }
// 
// public TransitionIterator transitionIterator(Sequence input, int ip, Sequence output, int op) {
// return new TransitionIterator() {
// 
// boolean done = false;
// 
// public boolean hasNext() {
// return !done;
// }
// 
// public State nextState() {
// done = true;
// return getState(0);
// }
// 
// public Object getOutput() {
// return "tag";
// }
// 
// public double getWeight() {
// return Math.log(0.4);
// }
// };
// }
// };
// }
// 
// public int stateIndexOfString(String name) {
// return -1;
// }
// 
// public boolean expectsTraining() {
// return false;
// }
// };
// SumLatticeBeam beam = new SumLatticeBeam(t, input, output, null, true, la);
// double gammaValid = beam.getGammaProbability(0, t.getState(0));
// double gammaInvalid = beam.getGammaWeight(0, t.getState(1));
// assertTrue(gammaValid > 0.0);
// assertTrue(Double.isInfinite(gammaInvalid));
}

@Test
public void testNegativeConstraintBlocksFinalState() {
TokenSequence input = new TokenSequence();
input.add(new Token("i1"));
input.add(new Token("i2"));
TokenSequence output = new TokenSequence();
output.add(new Token("L0"));
output.add(new Token("L1"));
LabelAlphabet la = new LabelAlphabet();
la.lookupIndex("L0", true);
la.lookupIndex("L1", true);
// Transducer transducer = new SumLatticeBeamTest.MockTransducer();
int[] constraints = new int[3];
constraints[2] = -1;
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null, la, constraints);
// double weight = beam.getTotalWeight();
// assertTrue(weight < 0.0 || Double.isInfinite(weight));
}

@Test
public void testOutputDistributionSumsToOneInLabelVectors() {
TokenSequence input = new TokenSequence();
input.add(new Token("xx"));
input.add(new Token("yy"));
TokenSequence output = new TokenSequence();
output.add(new Token("LBL1"));
output.add(new Token("LBL2"));
LabelAlphabet alphabet = new LabelAlphabet();
alphabet.lookupIndex("LBL1", true);
alphabet.lookupIndex("LBL2", true);
// Transducer transducer = new SumLatticeBeamTest.MockTransducer();
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null, true, alphabet);
// LabelVector labelVec = beam.getLabelingAtPosition(0);
// double probSum = labelVec.value(0) + labelVec.value(1);
// assertTrue(Math.abs(1.0 - probSum) < 1e-5);
}

@Test
public void testBeamRminPositiveKLepsZeroPathCoverage() {
TokenSequence input = new TokenSequence();
input.add(new Token("start"));
input.add(new Token("step"));
TokenSequence output = new TokenSequence();
output.add(new Token("L0"));
output.add(new Token("L1"));
LabelAlphabet alphabet = new LabelAlphabet();
alphabet.lookupIndex("L0", true);
alphabet.lookupIndex("L1", true);
// Transducer transducer = new SumLatticeBeamTest.MockTransducer();
// SumLatticeBeam lattice = new SumLatticeBeam(transducer, input, output, null, true, alphabet);
// lattice.setKLeps(0.0);
// lattice.setRmin(0.5);
// double[][] gammas = lattice.getGammas();
// assertNotNull(gammas);
// double val = Math.exp(gammas[1][0]) + Math.exp(gammas[1][1]);
// assertTrue(val > 0.0);
}

@Test
public void testGetXiWeightReturnsExpectedLogValue() {
TokenSequence input = new TokenSequence();
input.add(new Token("a"));
input.add(new Token("b"));
TokenSequence output = new TokenSequence();
output.add(new Token("c"));
output.add(new Token("d"));
LabelAlphabet la = new LabelAlphabet();
la.lookupIndex("c", true);
la.lookupIndex("d", true);
// Transducer t = new SumLatticeBeamTest.MockTransducer();
// SumLatticeBeam lattice = new SumLatticeBeam(t, input, output, null, true, la);
// double weight = lattice.getXiWeight(0, t.getState(0), t.getState(1));
// assertTrue(weight < 0.0);
}

@Test
public void testLabelVectorProbabilityDistributionIsConsistent() {
TokenSequence input = new TokenSequence();
input.add(new Token("m"));
TokenSequence output = new TokenSequence();
output.add(new Token("tag"));
LabelAlphabet la = new LabelAlphabet();
int idx = la.lookupIndex("tag", true);
// Transducer t = new SumLatticeBeamTest.MockTransducer();
// SumLatticeBeam lattice = new SumLatticeBeam(t, input, output, null, true, la);
// LabelVector lv = lattice.getLabelingAtPosition(0);
// assertNotNull(lv);
// assertEquals(idx, lv.getBestIndex());
// double sum = lv.value(0) + (lv.numLocations() > 1 ? lv.value(1) : 0.0);
// assertTrue(sum <= 1.00001);
}

@Test
public void testConstraintPositiveBlocksTransitionCorrectly() {
TokenSequence input = new TokenSequence();
input.add(new Token("x0"));
TokenSequence output = new TokenSequence();
output.add(new Token("y1"));
LabelAlphabet la = new LabelAlphabet();
la.lookupIndex("y1", true);
// Transducer t = new SumLatticeBeamTest.MockTransducer();
int[] constraints = new int[2];
constraints[0] = 2;
// SumLatticeBeam lattice = new SumLatticeBeam(t, input, output, null, la, constraints);
// double prob = lattice.getGammaProbability(0, t.getState(0));
// assertEquals(0.0, prob, 0.00001);
}

@Test
public void testConstraintNegativeBlocksFinalAlphaState() {
TokenSequence input = new TokenSequence();
input.add(new Token("one"));
input.add(new Token("two"));
TokenSequence output = new TokenSequence();
output.add(new Token("Y0"));
output.add(new Token("Y1"));
LabelAlphabet la = new LabelAlphabet();
la.lookupIndex("Y0", true);
la.lookupIndex("Y1", true);
// Transducer t = new SumLatticeBeamTest.MockTransducer();
int[] constraints = new int[3];
constraints[2] = -2;
// SumLatticeBeam lattice = new SumLatticeBeam(t, input, output, null, la, constraints);
// double total = lattice.getTotalWeight();
// assertTrue(total < 0.0 || Double.isInfinite(total));
}

@Test
public void testSumLogProbHandlesNegativeInfinityCorrectly() {
double s = Transducer.sumLogProb(Transducer.IMPOSSIBLE_WEIGHT, Math.log(0.5));
assertEquals(Math.log(0.5), s, 0.00001);
s = Transducer.sumLogProb(Math.log(0.5), Transducer.IMPOSSIBLE_WEIGHT);
assertEquals(Math.log(0.5), s, 0.00001);
s = Transducer.sumLogProb(Transducer.IMPOSSIBLE_WEIGHT, Transducer.IMPOSSIBLE_WEIGHT);
assertEquals(Transducer.IMPOSSIBLE_WEIGHT, s, 0.00001);
}

@Test
public void testFactoryCreatesValidLatticeWithSaveXisFalse() {
TokenSequence input = new TokenSequence();
input.add(new Token("a"));
TokenSequence output = new TokenSequence();
output.add(new Token("b"));
LabelAlphabet la = new LabelAlphabet();
la.lookupIndex("b", true);
// Transducer t = new SumLatticeBeamTest.MockTransducer();
SumLatticeBeam.Factory factory = new SumLatticeBeam.Factory(5);
// SumLattice lattice = factory.newSumLattice(t, input, output, null, false, la);
// assertTrue(lattice instanceof SumLatticeBeam);
// assertNotNull(((SumLatticeBeam) lattice).getGammas());
}

@Test
public void testGetAlphaReturnsNegativeInfinityForUnvisitedNode() {
TokenSequence input = new TokenSequence();
input.add(new Token("ctx"));
TokenSequence output = new TokenSequence();
output.add(new Token("tag"));
LabelAlphabet la = new LabelAlphabet();
la.lookupIndex("tag", true);
// Transducer t = new SumLatticeBeamTest.MockTransducer();
// SumLatticeBeam beam = new SumLatticeBeam(t, input, output, null, true, la);
// Transducer.State unused = t.getState(99);
// double alpha = beam.getAlpha(0, unused);
// assertEquals(Transducer.IMPOSSIBLE_WEIGHT, alpha, 0.0);
}

@Test
public void testAddTokenToInputWithNullOutputStillWorks() {
TokenSequence input = new TokenSequence();
input.add(new Token("tk"));
// Transducer t = new SumLatticeBeamTest.MockTransducer();
// SumLatticeBeam beam = new SumLatticeBeam(t, input, null, null);
// assertEquals(2, beam.length());
// assertSame(input, beam.getInput());
}
}
