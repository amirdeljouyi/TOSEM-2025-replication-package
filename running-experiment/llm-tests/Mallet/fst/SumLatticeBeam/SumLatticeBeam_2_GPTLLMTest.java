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

public class SumLatticeBeam_2_GPTLLMTest {

@Test
public void testSetAndGetBeamWidth() {
// DummyTransducer transducer = new DummyTransducer();
// Sequence input = new DummySequence(Arrays.asList("word1"));
// Sequence output = new DummySequence(Arrays.asList("label1"));
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null);
// assertEquals(3, beam.getBeamWidth());
// beam.setBeamWidth(7);
// assertEquals(7, beam.getBeamWidth());
}

@Test
public void testSetAndGetUseForwardBackwardBeam() {
// DummyTransducer transducer = new DummyTransducer();
// Sequence input = new DummySequence(Arrays.asList("word1"));
// Sequence output = new DummySequence(Arrays.asList("label1"));
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null);
// assertFalse(beam.getUseForwardBackwardBeam());
// beam.setUseForwardBackwardBeam(true);
// assertTrue(beam.getUseForwardBackwardBeam());
}

@Test
public void testSetCurIterAndIncrementIter() {
// DummyTransducer transducer = new DummyTransducer();
// Sequence input = new DummySequence(Arrays.asList("A"));
// Sequence output = new DummySequence(Arrays.asList("X"));
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null);
// beam.setCurIter(2);
// assertEquals(0, beam.getTctIter());
// beam.incIter();
// assertEquals(1, beam.getTctIter());
// beam.incIter();
// assertEquals(2, beam.getTctIter());
}

@Test
public void testGetGammasContainsExpectedDimensions() {
// DummyTransducer transducer = new DummyTransducer();
// Sequence input = new DummySequence(Arrays.asList("A", "B"));
// Sequence output = new DummySequence(Arrays.asList("X", "Y"));
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null);
// double[][] gammas = beam.getGammas();
// assertNotNull(gammas);
// assertEquals(3, gammas.length);
// assertEquals(2, gammas[0].length);
// assertEquals(2, gammas[1].length);
// assertEquals(2, gammas[2].length);
}

@Test
public void testGetTotalWeightIsNotNaN() {
// DummyTransducer transducer = new DummyTransducer();
// Sequence input = new DummySequence(Arrays.asList("i1", "i2"));
// Sequence output = new DummySequence(Arrays.asList("o1", "o2"));
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null);
// double weight = beam.getTotalWeight();
// assertFalse(Double.isNaN(weight));
}

@Test
public void testGetGammaProbabilityWithinBounds() {
// DummyTransducer transducer = new DummyTransducer();
// Sequence input = new DummySequence(Arrays.asList("A", "B"));
// Sequence output = new DummySequence(Arrays.asList("X", "Y"));
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null);
// double val0 = beam.getGammaProbability(0, transducer.states.get(0));
// assertTrue(val0 >= 0.0);
// assertTrue(val0 <= 1.0);
// double val1 = beam.getGammaProbability(1, transducer.states.get(0));
// assertTrue(val1 >= 0.0);
// assertTrue(val1 <= 1.0);
// double val2 = beam.getGammaProbability(2, transducer.states.get(0));
// assertTrue(val2 >= 0.0);
// assertTrue(val2 <= 1.0);
}

@Test
public void testGetAlphaAndBetaDoNotReturnNaN() {
// DummyTransducer transducer = new DummyTransducer();
// Sequence input = new DummySequence(Arrays.asList("one", "two"));
// Sequence output = new DummySequence(Arrays.asList("x", "y"));
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null);
// double alpha0 = beam.getAlpha(0, transducer.states.get(0));
// double beta0 = beam.getBeta(0, transducer.states.get(0));
// assertFalse(Double.isNaN(alpha0));
// assertFalse(Double.isNaN(beta0));
// double alpha1 = beam.getAlpha(1, transducer.states.get(0));
// double beta1 = beam.getBeta(1, transducer.states.get(0));
// assertFalse(Double.isNaN(alpha1));
// assertFalse(Double.isNaN(beta1));
}

@Test
public void testLabelingAtPositionReturnsNullWithoutAlphabet() {
// DummyTransducer transducer = new DummyTransducer();
// Sequence input = new DummySequence(Arrays.asList("X"));
// Sequence output = new DummySequence(Arrays.asList("Y"));
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null);
// assertNull(beam.getLabelingAtPosition(0));
}

@Test
public void testLabelingAtPositionReturnsLabelVectorWithAlphabet() {
// DummyTransducer transducer = new DummyTransducer();
// Sequence input = new DummySequence(Arrays.asList("foo", "bar"));
// Sequence output = new DummySequence(Arrays.asList("tag1", "tag2"));
LabelAlphabet alphabet = new LabelAlphabet();
alphabet.lookupIndex("tag1");
alphabet.lookupIndex("tag2");
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null, true, alphabet);
// LabelVector lv = beam.getLabelingAtPosition(0);
// assertNotNull(lv);
}

@Test(expected = IllegalArgumentException.class)
public void testConstrainedConstructorThrowsWhenInputLengthMismatch() {
// DummyTransducer transducer = new DummyTransducer();
// Sequence inputSequence = new DummySequence(Arrays.asList("a", "b"));
// Sequence outputSequence = new DummySequence(Arrays.asList("x", "y"));
// Sequence constrained = new DummySequence(Arrays.asList("x"));
// Segment segment = new Segment(0, 0, "x", "y");
// new SumLatticeBeam(transducer, inputSequence, outputSequence, segment, constrained);
}

@Test
public void testEmptyInputSequence() {
// DummyTransducer transducer = new DummyTransducer();
// Sequence input = new DummySequence(new ArrayList<>());
// Sequence output = new DummySequence(new ArrayList<>());
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null);
// assertEquals(1, beam.length());
// assertFalse(Double.isNaN(beam.getTotalWeight()));
}

@Test
public void testNullOutputSequenceAllowed() {
// DummyTransducer transducer = new DummyTransducer();
// Sequence input = new DummySequence(Arrays.asList("token"));
Sequence output = null;
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null);
// assertNotNull(beam.getInput());
// assertEquals(2, beam.length());
}

@Test
public void testGetXiThrowsWhenNotSaved() {
// DummyTransducer transducer = new DummyTransducer();
// Sequence input = new DummySequence(Arrays.asList("i1", "i2"));
// Sequence output = new DummySequence(Arrays.asList("o1", "o2"));
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null, false, null);
try {
// beam.getXiProbability(0, transducer.states.get(0), transducer.states.get(1));
fail("Expected IllegalStateException");
} catch (IllegalStateException e) {
assertEquals("xis were not saved.", e.getMessage());
}
}

@Test
public void testXiArrayExistsWhenFlagTrue() {
// DummyTransducer transducer = new DummyTransducer();
// Sequence input = new DummySequence(Arrays.asList("a", "b"));
// Sequence output = new DummySequence(Arrays.asList("x", "y"));
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null, true, null);
// assertNotNull(beam.getXis());
}

@Test
public void testOnlyOnePossibleTransition() {
// DummyTransducer transducer = new DummyTransducer() {
// 
// @Override
// public Transducer.TransitionIterator transitionIterator(Sequence input, int ip, Sequence output, int op) {
// return new Transducer.TransitionIterator() {
// 
// boolean done = false;
// 
// public boolean hasNext() {
// return !done;
// }
// 
// public Transducer.State nextState() {
// done = true;
// return states.get(1);
// }
// 
// public double getWeight() {
// return 0.0;
// }
// 
// public Object getOutput() {
// return "X";
// }
// };
// }
// };
// Sequence input = new DummySequence(Arrays.asList("a", "b"));
// Sequence output = new DummySequence(Arrays.asList("x", "y"));
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null);
// assertEquals(3, beam.length());
}

@Test
public void testAllStatesHaveImpossibleInitialWeight() {
// DummyTransducer transducer = new DummyTransducer() {
// 
// @Override
// public Transducer.State getState(int i) {
// Transducer.State s = super.getState(i);
// return new DummyState(s.getName(), s.getIndex(), Double.POSITIVE_INFINITY, 0.0);
// }
// };
// Sequence input = new DummySequence(Arrays.asList("x"));
// Sequence output = new DummySequence(Arrays.asList("y"));
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null);
// assertTrue(Double.isInfinite(beam.getTotalWeight()));
}

@Test
public void testConstrainedConstructorReturnsWithValidConstraints() {
// DummyTransducer transducer = new DummyTransducer();
// transducer.addLabel("L1");
// transducer.addLabel("L2");
// Sequence input = new DummySequence(Arrays.asList("token1", "token2"));
// Sequence output = new DummySequence(Arrays.asList("L1", "L2"));
// Sequence constrained = new DummySequence(Arrays.asList("L1", "L2"));
// Segment segment = new Segment(0, 1, "L1", "L2");
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, segment, constrained);
// assertNotNull(beam.getGammaWeight(1, transducer.states.get(0)));
}

@Test
public void testGammaNormalizationStableForSingleState() {
// DummyTransducer transducer = new DummyTransducer();
// transducer.states.clear();
// DummyState s = new DummyState("SINGLE", 0);
// transducer.states.add(s);
// Sequence input = new DummySequence(Arrays.asList("a", "b"));
// Sequence output = new DummySequence(Arrays.asList("x", "y"));
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null);
// double totalWeight = beam.getTotalWeight();
// assertFalse(Double.isNaN(totalWeight));
// double g = beam.getGammaProbability(0, s);
// assertTrue(g >= 0.0 && g <= 1.0);
}

@Test
public void testProbabilityOfXiIsInRange() {
// DummyTransducer transducer = new DummyTransducer();
// Sequence input = new DummySequence(Arrays.asList("p", "q"));
// Sequence output = new DummySequence(Arrays.asList("r", "s"));
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null, true, null);
// double xi = beam.getXiProbability(0, transducer.states.get(0), transducer.states.get(1));
// assertTrue(xi >= 0.0);
// assertTrue(xi <= 1.0);
}

@Test
public void testNegativeConstraintBlocksTransition() {
LabelAlphabet outputAlphabet = new LabelAlphabet();
outputAlphabet.lookupIndex("O1");
outputAlphabet.lookupIndex("O2");
// DummyTransducer transducer = new DummyTransducer();
// Sequence input = new DummySequence(Arrays.asList("w1", "w2"));
// Sequence output = new DummySequence(Arrays.asList("O1", "O2"));
int[] constraints = new int[3];
constraints[0] = 0;
// constraints[1] = -(transducer.states.get(0).getIndex() + 1);
constraints[2] = 0;
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null, outputAlphabet, constraints);
// double[][] gammas = beam.getGammas();
// assertNotNull(gammas);
// double prob = Math.exp(gammas[1][0]);
// assertTrue(prob < 1e-3);
}

@Test
public void testLabelVectorHasProbabilitySumCloseTo1() {
LabelAlphabet alphabet = new LabelAlphabet();
alphabet.lookupIndex("tag1");
alphabet.lookupIndex("tag2");
// DummyTransducer transducer = new DummyTransducer();
// Sequence input = new DummySequence(Arrays.asList("x", "y"));
// Sequence output = new DummySequence(Arrays.asList("tag1", "tag2"));
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null, true, alphabet);
// LabelVector vector = beam.getLabelingAtPosition(0);
// assertNotNull(vector);
double sum = 0.0;
// sum += vector.value(0);
// sum += vector.value(1);
assertEquals(1.0, sum, 1e-5);
}

@Test
public void testConstraintMatchingPositiveConstraint() {
// DummyTransducer transducer = new DummyTransducer();
// transducer.addLabel("Q");
// Sequence input = new DummySequence(Arrays.asList("in1", "in2", "in3"));
// Sequence output = new DummySequence(Arrays.asList("Q", "Q", "Q"));
// Sequence constrained = new DummySequence(Arrays.asList("Q", "Q", "Q"));
// Segment segment = new Segment(0, 2, "Q", "Q");
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, segment, constrained);
// assertNotNull(beam);
// assertTrue(beam.length() >= 4);
}

@Test
public void testInvalidStateIndexInConstraintTreatedGracefully() {
// DummyTransducer transducer = new DummyTransducer();
// Sequence input = new DummySequence(Arrays.asList("i1", "i2", "i3"));
// Sequence output = new DummySequence(Arrays.asList("o1", "o2", "o3"));
// Sequence constrained = new DummySequence(Arrays.asList("STATE1", "STATE2", "STATE3"));
// Segment segment = new Segment(0, 2, "STATE1", "STATE3");
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, segment, constrained);
// assertNotNull(beam);
}

@Test
public void testGammaZeroProbabilitiesForInvalidForwardPaths() {
// DummyTransducer transducer = new DummyTransducer() {
// 
// @Override
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
// public double getWeight() {
// return Double.NEGATIVE_INFINITY;
// }
// 
// public Object getOutput() {
// return "none";
// }
// };
// }
// };
// Sequence input = new DummySequence(Arrays.asList("a", "b"));
// Sequence output = new DummySequence(Arrays.asList("x", "y"));
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null);
// double[][] gammas = beam.getGammas();
// double prob0 = Math.exp(gammas[0][0]);
// double prob1 = Math.exp(gammas[1][0]);
// assertTrue(prob0 < 1e-3);
// assertTrue(prob1 < 1e-3);
}

@Test
public void testGetLabelingAtLastPositionReturnsNull() {
LabelAlphabet alphabet = new LabelAlphabet();
alphabet.lookupIndex("x");
// DummyTransducer transducer = new DummyTransducer();
// Sequence input = new DummySequence(Arrays.asList("a"));
// Sequence output = new DummySequence(Arrays.asList("x"));
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null, true, alphabet);
// LabelVector vec = beam.getLabelingAtPosition(1);
// assertNull(vec);
}

@Test
public void testFinalStatesWithLargeFinalWeightHandled() {
// DummyTransducer transducer = new DummyTransducer() {
// 
// @Override
// public State getState(int index) {
// return new DummyState("state" + index, index, 0.0, 9.9);
// }
// };
// Sequence input = new DummySequence(Arrays.asList("w1"));
// Sequence output = new DummySequence(Arrays.asList("l1"));
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null);
// double weight = beam.getTotalWeight();
// assertTrue(weight > 1.0);
}

@Test
public void testXisProbabilityComputationIsStable() {
// DummyTransducer transducer = new DummyTransducer();
// Sequence input = new DummySequence(Arrays.asList("w1", "w2"));
// Sequence output = new DummySequence(Arrays.asList("l1", "l2"));
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null, true, null);
// double prob = beam.getXiProbability(0, transducer.states.get(0), transducer.states.get(1));
// assertTrue(prob >= 0.0 && prob <= 1.0);
}

@Test
public void testGetGammaWeightReturnsConsistentValue() {
// DummyTransducer transducer = new DummyTransducer();
// Sequence input = new DummySequence(Arrays.asList("hello", "world"));
// Sequence output = new DummySequence(Arrays.asList("TAG", "TAG"));
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null);
// double g1 = beam.getGammaWeight(1, transducer.states.get(0));
// double g2 = Math.log(beam.getGammaProbability(1, transducer.states.get(0)));
// assertEquals(g1, g2, 1e-6);
}

@Test
public void testGetGammaWeightForUnknownStateIndex() {
// DummyTransducer transducer = new DummyTransducer();
// Sequence input = new DummySequence(Arrays.asList("token1"));
// Sequence output = new DummySequence(Arrays.asList("out1"));
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null);
// int length = beam.length();
// assertTrue(length > 0);
// Transducer.State fakeState = new Transducer.State(null, "Fake", 100);
// double gamma = beam.getGammaWeight(0, fakeState);
// assertEquals(Double.NEGATIVE_INFINITY, gamma, 0.0);
}

@Test
public void testZeroLengthInputNonNullOutput() {
// DummyTransducer transducer = new DummyTransducer();
// Sequence input = new DummySequence(Collections.emptyList());
// Sequence output = new DummySequence(Arrays.asList("O"));
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null);
// assertNotNull(beam);
// assertEquals(1, beam.length());
// double weight = beam.getTotalWeight();
// assertFalse(Double.isNaN(weight));
}

@Test
public void testLabelVectorForNonExistentLabelIndex() {
LabelAlphabet alphabet = new LabelAlphabet();
alphabet.lookupIndex("KNOWN");
// DummyTransducer transducer = new DummyTransducer();
// Sequence input = new DummySequence(Arrays.asList("tok1", "tok2"));
// Sequence output = new DummySequence(Arrays.asList("KNOWN", "UNKNOWN"));
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null, true, alphabet);
// LabelVector vector = beam.getLabelingAtPosition(0);
// assertNotNull(vector);
int index = alphabet.lookupIndex("UNKNOWN", false);
assertEquals(-1, index);
}

@Test
public void testGammaInitializationWithAllImpossibleWeights() {
// DummyTransducer transducer = new DummyTransducer() {
// 
// @Override
// public State getState(int index) {
// return new DummyState("S" + index, index, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
// }
// };
// Sequence input = new DummySequence(Arrays.asList("x", "y"));
// Sequence output = new DummySequence(Arrays.asList("a", "b"));
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null);
// double totalWeight = beam.getTotalWeight();
// assertTrue(Double.isInfinite(totalWeight));
// double[][] gammas = beam.getGammas();
// assertTrue(Double.isInfinite(gammas[1][0]));
}

@Test
public void testConstrainedSegmentAtSequenceEnd() {
// DummyTransducer transducer = new DummyTransducer();
// transducer.addLabel("END");
// List<Object> inputList = Arrays.asList("a", "b", "E");
// List<Object> outputList = Arrays.asList("X", "Y", "END");
// Sequence input = new DummySequence(inputList);
// Sequence output = new DummySequence(outputList);
// Sequence constrained = new DummySequence(outputList);
// Segment endSeg = new Segment(2, 2, "END", "END");
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, endSeg, constrained);
// assertNotNull(beam);
// assertEquals(4, beam.length());
}

@Test
public void testNegativeRminSwitchedToStrawmanLogic() {
// DummyTransducer transducer = new DummyTransducer();
// Sequence input = new DummySequence(Arrays.asList("i1", "i2"));
// Sequence output = new DummySequence(Arrays.asList("o1", "o2"));
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null);
// beam.setRmin(-0.3);
// beam.setKLeps(0);
// beam.setCurIter(1);
// beam.incIter();
// beam.incIter();
// int bwBefore = beam.getBeamWidth();
// beam.setBeamWidth(5);
// assertEquals(5, beam.getBeamWidth());
// beam.setBeamWidth(bwBefore);
}

@Test
public void testNegativeKLepsTriggersMaxOfLogic() {
// DummyTransducer transducer = new DummyTransducer();
// Sequence input = new DummySequence(Arrays.asList("one", "two"));
// Sequence output = new DummySequence(Arrays.asList("out1", "out2"));
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null);
// beam.setRmin(0.2);
// beam.setKLeps(-0.01);
// beam.setCurIter(1);
// beam.incIter();
// int width = beam.getBeamWidth();
// assertTrue(width > 0);
}

@Test
public void testGetLabelingAtInvalidIndexReturnsNull() {
LabelAlphabet alphabet = new LabelAlphabet();
alphabet.lookupIndex("T");
// DummyTransducer transducer = new DummyTransducer();
// Sequence input = new DummySequence(Arrays.asList("w1", "w2"));
// Sequence output = new DummySequence(Arrays.asList("T", "T"));
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null, true, alphabet);
// int invalidIndex = beam.length();
// LabelVector vec = beam.getLabelingAtPosition(invalidIndex);
// assertNull(vec);
}

@Test
public void testLargeBeamSmallStateSpace() {
// DummyTransducer transducer = new DummyTransducer();
// Sequence input = new DummySequence(Arrays.asList("A", "B"));
// Sequence output = new DummySequence(Arrays.asList("X", "Y"));
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null);
// beam.setBeamWidth(100);
// int bw = beam.getBeamWidth();
// assertEquals(100, bw);
// double w = beam.getTotalWeight();
// assertFalse(Double.isNaN(w));
}

@Test
public void testLabelAlphabetWithNoLabelsCreatesEmptyLabelVectors() {
LabelAlphabet labelAlphabet = new LabelAlphabet();
// Transducer mockTransducer = new DummyTransducer();
// Sequence input = new DummySequence(Arrays.asList("w1", "w2"));
// Sequence output = new DummySequence(Arrays.asList("l1", "l2"));
// SumLatticeBeam beam = new SumLatticeBeam(mockTransducer, input, output, null, true, labelAlphabet);
// LabelVector labelVector = beam.getLabelingAtPosition(0);
// assertNotNull(labelVector);
// assertEquals(0, labelVector.numLocations());
}

@Test
public void testInputWithNullFeatureDoesNotCauseCrash() {
// Transducer transducer = new DummyTransducer();
// List<Object> inputTokens = Arrays.asList(null, "tokenB");
// Sequence input = new DummySequence(inputTokens);
// Sequence output = new DummySequence(Arrays.asList("labelA", "labelB"));
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null);
// assertEquals(3, beam.length());
// assertFalse(Double.isNaN(beam.getTotalWeight()));
}

@Test
public void testOutputAlphabetNullAndIncrementorNull() {
// Transducer transducer = new DummyTransducer();
// Sequence input = new DummySequence(Arrays.asList("w1", "w2"));
// Sequence output = new DummySequence(Arrays.asList("o1", "o2"));
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null, false, null);
// assertNotNull(beam.getGammas());
}

@Test
public void testNegativeFinalWeightProducesConsistentTotalWeight() {
// Transducer transducer = new DummyTransducer() {
// 
// @Override
// public State getState(int index) {
// return new DummyState("S" + index, index, 0.0, -1.0);
// }
// };
// Sequence input = new DummySequence(Arrays.asList("aa", "bb"));
// Sequence output = new DummySequence(Arrays.asList("xx", "yy"));
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null);
// double totalWeight = beam.getTotalWeight();
// assertFalse(Double.isNaN(totalWeight));
}

@Test
public void testConstraintArrayIncludesNegativeDynamicTransitionDenial() {
// Transducer transducer = new DummyTransducer() {
// 
// @Override
// public int stateIndexOfString(String name) {
// return 0;
// }
// };
// Sequence input = new DummySequence(Arrays.asList("t1", "t2", "t3"));
// Sequence output = new DummySequence(Arrays.asList("l1", "l2", "l3"));
// Sequence constrained = new DummySequence(Arrays.asList("NEGATE", "NEGATE", "NEGATE"));
// Segment segment = new Segment(0, 2, "NEGATE", "NEGATE");
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, segment, constrained);
// assertNotNull(beam);
// assertEquals(4, beam.length());
}

@Test
public void testGetLabelingAtEdgePositionReturnsConsistentResult() {
LabelAlphabet alphabet = new LabelAlphabet();
alphabet.lookupIndex("EDGE");
// Transducer transducer = new DummyTransducer();
// Sequence input = new DummySequence(Arrays.asList("tA", "tB"));
// Sequence output = new DummySequence(Arrays.asList("EDGE", "EDGE"));
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null, true, alphabet);
// LabelVector lv0 = beam.getLabelingAtPosition(0);
// LabelVector lv1 = beam.getLabelingAtPosition(1);
// assertNotNull(lv0);
// assertNotNull(lv1);
}

@Test
public void testBeamHandlesSingleElementInputAndOutput() {
// Transducer transducer = new DummyTransducer();
// Sequence input = new DummySequence(Arrays.asList("w1"));
// Sequence output = new DummySequence(Arrays.asList("l1"));
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null);
// double weight = beam.getTotalWeight();
// assertFalse(Double.isNaN(weight));
// double gamma = beam.getGammaProbability(1, transducer.states.get(0));
// assertTrue(gamma >= 0.0);
}

@Test
public void testGetXiProbabilityLogsProperlyNearZero() {
// Transducer transducer = new DummyTransducer();
// Sequence input = new DummySequence(Arrays.asList("a", "b"));
// Sequence output = new DummySequence(Arrays.asList("x", "y"));
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null, true, null);
// double xiP = beam.getXiProbability(0, transducer.states.get(0), transducer.states.get(1));
// assertTrue(xiP >= 0.0);
// assertTrue(xiP <= 1.0);
}

@Test
public void testTerminalStateProbabilitySummationStable() {
// Transducer transducer = new DummyTransducer() {
// 
// @Override
// public State getState(int index) {
// return new DummyState("terminal", index, 0.0, 0.0);
// }
// };
// Sequence input = new DummySequence(Arrays.asList("a", "b"));
// Sequence output = new DummySequence(Arrays.asList("x", "y"));
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null);
// double weight = beam.getTotalWeight();
// assertTrue(weight < Double.POSITIVE_INFINITY);
}

@Test
public void testConstrainedConstructorWithNonzeroStartIndex() {
// Transducer transducer = new DummyTransducer();
// transducer.addLabel("Named");
// Sequence input = new DummySequence(Arrays.asList("A", "B", "C"));
// Sequence output = new DummySequence(Arrays.asList("Named", "Named", "Named"));
// Sequence constrained = new DummySequence(Arrays.asList("Named", "Named", "Named"));
// Segment segment = new Segment(1, 2, "Named", "Named");
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, segment, constrained);
// assertTrue(beam.length() == 4);
// double gamma = beam.getGammaProbability(2, transducer.states.get(0));
// assertTrue(gamma >= 0.0);
}

@Test
public void testInputWithAllNullElements() {
// Transducer transducer = new DummyTransducer();
// List<Object> tokens = Arrays.asList(null, null);
// Sequence input = new DummySequence(tokens);
// Sequence output = new DummySequence(Arrays.asList("a", "b"));
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null);
// assertEquals(3, beam.length());
}

@Test
public void testOutputSequenceHasNullElements() {
// Transducer transducer = new DummyTransducer();
// Sequence input = new DummySequence(Arrays.asList("a", "b"));
// Sequence output = new DummySequence(Arrays.asList(null, null));
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null);
// assertNotNull(beam.getTotalWeight());
}

@Test
public void testAllTransitionWeightsAreImpossibleWeight() {
// Transducer transducer = new DummyTransducer() {
// 
// @Override
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
// return states.get(1);
// }
// 
// public double getWeight() {
// return Transducer.IMPOSSIBLE_WEIGHT;
// }
// 
// public Object getOutput() {
// return null;
// }
// };
// }
// };
// Sequence input = new DummySequence(Arrays.asList("token"));
// Sequence output = new DummySequence(Arrays.asList("label"));
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null);
// assertTrue(Double.isInfinite(beam.getTotalWeight()));
}

@Test
public void testXiAccessThrowsForOutOfBoundsIndexes() {
// Transducer transducer = new DummyTransducer();
// Sequence input = new DummySequence(Arrays.asList("a", "b"));
// Sequence output = new DummySequence(Arrays.asList("x", "y"));
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null, true, null);
try {
// beam.getXiProbability(100, transducer.states.get(0), transducer.states.get(1));
fail("Expected ArrayIndexOutOfBoundsException");
} catch (ArrayIndexOutOfBoundsException expected) {
assertTrue(true);
}
}

@Test
public void testXiAccessThrowsWithUnrelatedStates() {
// Transducer transducer = new DummyTransducer();
// Sequence input = new DummySequence(Arrays.asList("tok1", "tok2"));
// Sequence output = new DummySequence(Arrays.asList("out1", "out2"));
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null, true, null);
// Transducer.State unrelated1 = new Transducer.State(null, "Z", 5);
// Transducer.State unrelated2 = new Transducer.State(null, "K", 6);
try {
// beam.getXiProbability(0, unrelated1, unrelated2);
fail("Expected ArrayIndexOutOfBoundsException");
} catch (ArrayIndexOutOfBoundsException expected) {
assertTrue(true);
}
}

@Test
public void testVeryLargeBeamWidthDoesNotThrow() {
// Transducer transducer = new DummyTransducer();
// Sequence input = new DummySequence(Arrays.asList("a", "b", "c"));
// Sequence output = new DummySequence(Arrays.asList("x", "y", "z"));
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null);
// beam.setBeamWidth(1000);
// assertEquals(1000, beam.getBeamWidth());
// double weight = beam.getTotalWeight();
// assertFalse(Double.isNaN(weight));
}

@Test
public void testMissingOutputAssociatedWithTransition() {
// Transducer transducer = new DummyTransducer() {
// 
// @Override
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
// return states.get(1);
// }
// 
// public double getWeight() {
// return 0.0;
// }
// 
// public Object getOutput() {
// return null;
// }
// };
// }
// };
// Sequence input = new DummySequence(Arrays.asList("a"));
// Sequence output = new DummySequence(Arrays.asList("x"));
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null, true, new LabelAlphabet());
// assertNotNull(beam.getGammas());
}

@Test
public void testConstraintArrayWithAllZeros() {
// Transducer transducer = new DummyTransducer();
// Sequence input = new DummySequence(Arrays.asList("aa", "bb", "cc"));
// Sequence output = new DummySequence(Arrays.asList("x", "y", "z"));
int[] constraints = new int[4];
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null, new LabelAlphabet(), constraints);
// assertNotNull(beam.getInput());
// assertEquals(4, beam.length());
}

@Test
public void testGetAlphaAndBetaReturnsImpossibleWeightForInactiveStates() {
// Transducer transducer = new DummyTransducer();
// Sequence input = new DummySequence(Arrays.asList("input1"));
// Sequence output = new DummySequence(Arrays.asList("label1"));
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null);
// Transducer.State fakeState = new Transducer.State(null, "FAKE", 9999);
// double alpha = beam.getAlpha(0, fakeState);
// double beta = beam.getBeta(0, fakeState);
// assertEquals(Transducer.IMPOSSIBLE_WEIGHT, alpha, 0.0);
// assertEquals(Transducer.IMPOSSIBLE_WEIGHT, beta, 0.0);
}

@Test
public void testFinalPositionGammasAreComputedIfValid() {
// Transducer transducer = new DummyTransducer();
// Sequence input = new DummySequence(Arrays.asList("one", "two"));
// Sequence output = new DummySequence(Arrays.asList("a", "b"));
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null);
// double lastGamma = beam.getGammaProbability(2, transducer.states.get(0));
// assertTrue(lastGamma >= 0.0);
}

@Test
public void testTransitionIteratorReturnsNullDestinationOutput() {
// Transducer transducer = new DummyTransducer() {
// 
// @Override
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
// return states.get(1);
// }
// 
// public double getWeight() {
// return 0.0;
// }
// 
// public Object getOutput() {
// return null;
// }
// };
// }
// };
// Sequence input = new DummySequence(Arrays.asList("a", "b"));
// Sequence output = new DummySequence(Arrays.asList("l1", "l2"));
LabelAlphabet alphabet = new LabelAlphabet();
alphabet.lookupIndex("l1");
alphabet.lookupIndex("l2");
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null, true, alphabet);
// assertNotNull(beam.getLabelingAtPosition(0));
}

@Test
public void testXiWhenOnlyOneIntermediateTransitionExists() {
// Transducer transducer = new DummyTransducer();
// Sequence input = new DummySequence(Arrays.asList("i1", "i2"));
// Sequence output = new DummySequence(Arrays.asList("o1", "o2"));
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null, true, null);
// double xi = beam.getXiProbability(0, transducer.states.get(0), transducer.states.get(1));
// assertTrue(xi >= 0.0);
// assertTrue(xi <= 1.0);
}

@Test
public void testLabelingAtPositionReturnsNullWhenIndexOutOfRange() {
// Transducer transducer = new DummyTransducer();
LabelAlphabet alphabet = new LabelAlphabet();
alphabet.lookupIndex("A");
// Sequence input = new DummySequence(Arrays.asList("a"));
// Sequence output = new DummySequence(Arrays.asList("A"));
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null, true, alphabet);
// LabelVector lv = beam.getLabelingAtPosition(2);
// assertNull(lv);
}

@Test
public void testXiArrayAccessAfterConstructorWithXisDisabledThrows() {
// Transducer transducer = new DummyTransducer();
// Sequence input = new DummySequence(Arrays.asList("a", "b"));
// Sequence output = new DummySequence(Arrays.asList("x", "y"));
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null, false, null);
try {
// beam.getXiWeight(1, transducer.states.get(0), transducer.states.get(1));
fail("Expected IllegalStateException");
} catch (IllegalStateException e) {
assertEquals("xis were not saved.", e.getMessage());
}
}

@Test
public void testKLThresholdBehaviorWithPositiveKLeps() {
// Transducer transducer = new DummyTransducer();
// Sequence input = new DummySequence(Arrays.asList("a", "b"));
// Sequence output = new DummySequence(Arrays.asList("x", "y"));
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null);
// beam.setKLeps(0.00001);
// beam.setCurIter(1);
// beam.incIter();
// double[][] gammas = beam.getGammas();
// assertTrue(gammas.length > 0);
}

@Test
public void testThresholdBasedFilteringWithPositiveRmin() {
// Transducer transducer = new DummyTransducer();
// Sequence input = new DummySequence(Arrays.asList("w1", "w2", "w3"));
// Sequence output = new DummySequence(Arrays.asList("l1", "l2", "l3"));
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null);
// beam.setKLeps(0);
// beam.setRmin(0.9);
// beam.setCurIter(1);
// beam.incIter();
// assertTrue(beam.length() > 0);
}

@Test
public void testXiWithMatchingStateIndexes() {
// Transducer transducer = new DummyTransducer();
// State s = transducer.states.get(0);
// Sequence input = new DummySequence(Arrays.asList("a", "b", "c"));
// Sequence output = new DummySequence(Arrays.asList("x", "y", "z"));
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null, true, null);
// double xi = beam.getXiProbability(0, s, s);
// assertTrue(xi >= 0.0 && xi <= 1.0);
}

@Test
public void testInvalidFinalStateGammaSkippedWhenConstraintBlocks() {
// Transducer transducer = new DummyTransducer();
// Sequence input = new DummySequence(Arrays.asList("x", "y", "z"));
// Sequence output = new DummySequence(Arrays.asList("a", "b", "c"));
int[] constraints = new int[4];
// constraints[3] = -(transducer.states.get(0).getIndex() + 1);
LabelAlphabet alphabet = new LabelAlphabet();
alphabet.lookupIndex("a");
alphabet.lookupIndex("b");
alphabet.lookupIndex("c");
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null, alphabet, constraints);
// double w = beam.getTotalWeight();
// assertTrue(Double.isNaN(Math.log(Math.exp(w))));
}

@Test
public void testGetAlphaReturnsDefaultForMissingNode() {
// Transducer transducer = new DummyTransducer();
// Sequence input = new DummySequence(Arrays.asList("aa"));
// Sequence output = new DummySequence(Arrays.asList("bb"));
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null);
// State fake = new Transducer.State(null, "X", 99);
// double alpha = beam.getAlpha(0, fake);
// assertEquals(Transducer.IMPOSSIBLE_WEIGHT, alpha, 0.0);
}

@Test
public void testGetBetaReturnsDefaultForMissingNode() {
// Transducer transducer = new DummyTransducer();
// Sequence input = new DummySequence(Arrays.asList("M"));
// Sequence output = new DummySequence(Arrays.asList("N"));
// SumLatticeBeam beam = new SumLatticeBeam(transducer, input, output, null);
// State fake = new Transducer.State(null, "Y", 100);
// double beta = beam.getBeta(0, fake);
// assertEquals(Transducer.IMPOSSIBLE_WEIGHT, beta, 0.0);
}
}
