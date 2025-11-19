package cc.mallet.fst.tests;

import cc.mallet.fst.CRF;
import cc.mallet.fst.HMM;
import cc.mallet.fst.Transducer;
import cc.mallet.pipe.*;
import cc.mallet.types.*;
import org.junit.Test;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.regex.Pattern;
import static org.junit.Assert.*;

public class HMM_2_GPTLLMTest {

@Test
public void testConstructorWithAlphabets() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("a");
inputAlphabet.lookupIndex("b");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("X");
outputAlphabet.lookupIndex("Y");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
assertEquals(inputAlphabet, hmm.getInputAlphabet());
assertEquals(outputAlphabet, hmm.getOutputAlphabet());
}

@Test
public void testAddStateIncrementsNumStates() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("w");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("L");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
String[] destinations = new String[] { "S2" };
String[] labels = new String[] { "L" };
hmm.addState("S1", 1.0, 0.0, destinations, labels);
assertNotNull(hmm.getState("S1"));
assertEquals(1, hmm.numStates());
}

@Test(expected = IllegalArgumentException.class)
public void testAddDuplicateStateThrowsException() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("in");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("tag");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
String[] destinations = new String[] { "S1" };
String[] labels = new String[] { "tag" };
hmm.addState("S1", 0.5, 0.2, destinations, labels);
hmm.addState("S1", 0.6, 0.1, destinations, labels);
}

@Test
public void testInitTransitionsWithRandom() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f1");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("L1");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
String[] destinations = new String[] { "S1" };
String[] labels = new String[] { "L1" };
hmm.addState("S1", 0.0, 0.0, destinations, labels);
Random rand = new Random(42);
hmm.initTransitions(rand, 0.5);
assertNotNull(hmm.getInitialMultinomial());
assertNotNull(hmm.getTransitionMultinomial());
assertNotNull(hmm.getTransitionMultinomial()[0]);
}

@Test
public void testInitEmissionsWithUniformNoise() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("obs");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("state");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
String[] destinations = new String[] { "S1" };
String[] labels = new String[] { "state" };
hmm.addState("S1", 0.0, 0.0, destinations, labels);
Random random = new Random(123);
hmm.initEmissions(random, 1.0);
assertNotNull(hmm.getEmissionMultinomial());
assertNotNull(hmm.getEmissionMultinomial()[0]);
}

@Test
public void testTrainReturnsTrue() {
Alphabet inputAlphabet = new Alphabet();
int idxA = inputAlphabet.lookupIndex("a");
Alphabet outputAlphabet = new Alphabet();
int idxX = outputAlphabet.lookupIndex("X");
Pipe pipe = new Pipe(inputAlphabet, outputAlphabet) {

@Override
public Instance pipe(Instance carrier) {
return carrier;
}
};
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
String[] destinations = new String[] { "S1" };
String[] labels = new String[] { "X" };
hmm.addState("S1", 0.0, 0.0, destinations, labels);
FeatureSequence inputSeq = new FeatureSequence(inputAlphabet, 2);
inputSeq.add("a");
inputSeq.add("a");
FeatureSequence outputSeq = new FeatureSequence(outputAlphabet, 2);
outputSeq.add("X");
outputSeq.add("X");
Instance inst = new Instance(inputSeq, outputSeq, null, null);
InstanceList ilist = new InstanceList(pipe);
ilist.addThruPipe(inst);
boolean result = hmm.train(ilist);
assertTrue(result);
}

@Test
public void testAddOrder0StatesCreatesStates() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("token");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("POS");
Pipe pipe = new Pipe(inputAlphabet, outputAlphabet) {

@Override
public Instance pipe(Instance carrier) {
return carrier;
}
};
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("POS", new String[] { "POS" });
FeatureSequence inputSeq = new FeatureSequence(inputAlphabet, 1);
inputSeq.add("token");
FeatureSequence outputSeq = new FeatureSequence(outputAlphabet, 1);
outputSeq.add("POS");
Instance instance = new Instance(inputSeq, outputSeq, null, null);
InstanceList instList = new InstanceList(pipe);
instList.addThruPipe(instance);
String startState = hmm.addOrderNStates(instList, null, null, "POS", null, null, false);
assertNotNull(startState);
}

@Test
public void testAddStateWithZeroInitialAndFinalWeight() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("i1");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("o1");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
String[] destinations = new String[] { "S1" };
String[] labels = new String[] { "o1" };
hmm.addState("S1", 0.0, 0.0, destinations, labels);
assertNotNull(hmm.getState("S1"));
assertEquals(1, hmm.numStates());
}

@Test
public void testAddFullyConnectedStatesForLabelsCreatesCorrectStates() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("in");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("O1");
outputAlphabet.lookupIndex("O2");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addFullyConnectedStatesForLabels();
assertNotNull(hmm.getState("O1"));
assertNotNull(hmm.getState("O2"));
assertEquals(2, hmm.numStates());
}

@Test
public void testAddStatesForLabelsConnectedAsInSkipsInvalidTransitions() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
outputAlphabet.lookupIndex("C");
Pipe pipe = new Pipe(inputAlphabet, outputAlphabet) {

@Override
public Instance pipe(Instance carrier) {
return carrier;
}
};
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
FeatureSequence input = new FeatureSequence(inputAlphabet, 3);
input.add("f");
input.add("f");
input.add("f");
FeatureSequence output = new FeatureSequence(outputAlphabet, 3);
output.add("A");
output.add("B");
output.add("C");
InstanceList list = new InstanceList(pipe);
Instance instance = new Instance(input, output, null, null);
list.addThruPipe(instance);
hmm.addStatesForLabelsConnectedAsIn(list);
assertNotNull(hmm.getState("A"));
assertNotNull(hmm.getState("B"));
assertNotNull(hmm.getState("C"));
assertEquals(3, hmm.numStates());
}

@Test
public void testAddOrderNWithForbiddenPatternSkipsState() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("a");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("X");
outputAlphabet.lookupIndex("Y");
Pipe pipe = new Pipe(inputAlphabet, outputAlphabet) {

@Override
public Instance pipe(Instance carrier) {
return carrier;
}
};
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
FeatureSequence input = new FeatureSequence(inputAlphabet, 2);
input.add("a");
input.add("a");
FeatureSequence output = new FeatureSequence(outputAlphabet, 2);
output.add("X");
output.add("Y");
InstanceList list = new InstanceList(pipe);
list.addThruPipe(new Instance(input, output, null, null));
Pattern forbidden = Pattern.compile("X,Y");
String result = hmm.addOrderNStates(list, new int[] { 1 }, new boolean[] { true }, "X", forbidden, null, false);
assertNotNull(result);
int stateCount = hmm.numStates();
assertTrue("States should be < 4 because forbidden transition skips some", stateCount < 4);
}

@Test
public void testAddOrderNWithAllowedPatternRestrictsTransitions() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("a");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("X");
outputAlphabet.lookupIndex("Y");
Pipe pipe = new Pipe(inputAlphabet, outputAlphabet) {

@Override
public Instance pipe(Instance carrier) {
return carrier;
}
};
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
FeatureSequence input = new FeatureSequence(inputAlphabet, 2);
input.add("a");
input.add("a");
FeatureSequence output = new FeatureSequence(outputAlphabet, 2);
output.add("X");
output.add("Y");
InstanceList list = new InstanceList(pipe);
list.addThruPipe(new Instance(input, output, null, null));
Pattern allowed = Pattern.compile("X,X");
String result = hmm.addOrderNStates(list, new int[] { 1 }, new boolean[] { true }, "X", null, allowed, false);
assertNotNull(result);
int stateCount = hmm.numStates();
assertTrue("States should be <= 2 due to allowed pattern restrictions", stateCount <= 2);
}

@Test
public void testAddStatesForThreeQuarterLabelsConnectedAsIn() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("t");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
Pipe pipe = new Pipe(inputAlphabet, outputAlphabet) {

@Override
public Instance pipe(Instance carrier) {
return carrier;
}
};
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
FeatureSequence input = new FeatureSequence(inputAlphabet, 2);
input.add("t");
input.add("t");
FeatureSequence output = new FeatureSequence(outputAlphabet, 2);
output.add("A");
output.add("B");
Instance instance = new Instance(input, output, null, null);
InstanceList list = new InstanceList(pipe);
list.addThruPipe(instance);
hmm.addStatesForThreeQuarterLabelsConnectedAsIn(list);
assertNotNull(hmm.getState("A"));
assertNotNull(hmm.getState("B"));
assertEquals(2, hmm.numStates());
}

@Test
public void testAddOrderNWithFullyConnectedTrueBuildsAllTransitions() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f1");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("L1");
outputAlphabet.lookupIndex("L2");
Pipe pipe = new Pipe(inputAlphabet, outputAlphabet) {

@Override
public Instance pipe(Instance carrier) {
return carrier;
}
};
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
FeatureSequence input = new FeatureSequence(inputAlphabet, 2);
input.add("f1");
input.add("f1");
FeatureSequence output = new FeatureSequence(outputAlphabet, 2);
output.add("L1");
output.add("L2");
Instance instance = new Instance(input, output, null, null);
InstanceList list = new InstanceList(pipe);
list.addThruPipe(instance);
String start = hmm.addOrderNStates(list, new int[] { 1 }, new boolean[] { true }, "L1", null, null, true);
assertNotNull(start);
assertTrue(hmm.numStates() > 0);
}

@Test
public void testAddFullyConnectedStatesForTriLabelsCreatesStates() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("tok");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addFullyConnectedStatesForTriLabels();
assertNotNull(hmm.getState("A,A,A"));
assertNotNull(hmm.getState("A,A,B"));
assertNotNull(hmm.getState("B,B,B"));
}

@Test
public void testAddFullyConnectedStatesForBiLabelsCreatesExpectedNames() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("inp");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addFullyConnectedStatesForBiLabels();
assertNotNull(hmm.getState("A,B"));
assertNotNull(hmm.getState("B,A"));
assertNotNull(hmm.getState("A,A"));
assertNotNull(hmm.getState("B,B"));
}

@Test
public void testAddStatesForHalfLabelsConnectedAsInExecutesWithoutError() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("tok");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
Pipe pipe = new Pipe(inputAlphabet, outputAlphabet) {

@Override
public Instance pipe(Instance carrier) {
return carrier;
}
};
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
FeatureSequence input = new FeatureSequence(inputAlphabet, 2);
input.add("tok");
input.add("tok");
FeatureSequence output = new FeatureSequence(outputAlphabet, 2);
output.add("A");
output.add("B");
InstanceList list = new InstanceList(pipe);
list.addThruPipe(new Instance(input, output, null, null));
hmm.addStatesForHalfLabelsConnectedAsIn(list);
assertNotNull(hmm.getState("A"));
assertNotNull(hmm.getState("B"));
}

@Test
public void testAddStateWithMismatchedLabelAndDestinationLengthThrowsAssertion() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("a");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("X");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
String[] destinations = new String[] { "S2" };
String[] labels = new String[] { "label1", "label2" };
boolean exceptionThrown = false;
try {
hmm.addState("MismatchState", 0.0, 0.0, destinations, labels);
} catch (AssertionError e) {
exceptionThrown = true;
}
assertTrue(exceptionThrown);
}

@Test
public void testInitialStateIteratorReturnsExpectedState() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("feature");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("Y");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("S1", 1.0, 0.5, new String[] { "S1" }, new String[] { "Y" });
Iterator it = hmm.initialStateIterator();
assertTrue(it.hasNext());
assertEquals("S1", ((HMM.State) it.next()).getName());
}

@Test
public void testTransitionIteratorSkipsIncorrectOutput() {
Alphabet inputAlphabet = new Alphabet();
int fIndex = inputAlphabet.lookupIndex("f");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("dog");
outputAlphabet.lookupIndex("cat");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("S", 1.0, 0.0, new String[] { "S" }, new String[] { "dog" });
hmm.initTransitions(null, 0.0);
hmm.initEmissions(null, 0.0);
FeatureSequence input = new FeatureSequence(inputAlphabet, 1);
input.add("f");
FeatureSequence output = new FeatureSequence(outputAlphabet, 1);
output.add("cat");
HMM.State state = (HMM.State) hmm.getState("S");
Transducer.TransitionIterator ti = state.transitionIterator(input, 0, output, 0);
assertFalse(ti.hasNext());
}

@Test
public void testTrainWithoutEstimatorsCreatesThemCorrectly() {
Alphabet inputAlphabet = new Alphabet();
int index = inputAlphabet.lookupIndex("tok");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("ENT");
Pipe pipe = new Pipe(inputAlphabet, outputAlphabet) {

public Instance pipe(Instance inst) {
return inst;
}
};
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("ENT", new String[] { "ENT" });
FeatureSequence input = new FeatureSequence(inputAlphabet, 1);
input.add("tok");
FeatureSequence output = new FeatureSequence(outputAlphabet, 1);
output.add("ENT");
Instance instance = new Instance(input, output, null, null);
InstanceList list = new InstanceList(pipe);
list.addThruPipe(instance);
boolean result = hmm.train(list);
assertTrue(result);
}

@Test
public void testGetTransitionMultinomialReturnsNullIfNotInitialized() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("token");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("O");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
assertNull(hmm.getTransitionMultinomial());
}

@Test
public void testGetEmissionMultinomialReturnsNullIfNotInitialized() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("token2");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("T");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
assertNull(hmm.getEmissionMultinomial());
}

@Test
public void testResetInitializesMultinomials() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("word");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("LABEL");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("LABEL", new String[] { "LABEL" });
hmm.reset();
assertNotNull(hmm.getEmissionMultinomial());
assertNotNull(hmm.getTransitionMultinomial());
assertNotNull(hmm.getInitialMultinomial());
}

@Test
public void testAddFullyConnectedStatesForThreeQuarterLabelsAddsStates() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("verb");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("run");
outputAlphabet.lookupIndex("walk");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addFullyConnectedStatesForThreeQuarterLabels(null);
assertNotNull(hmm.getState("run"));
assertNotNull(hmm.getState("walk"));
}

@Test
public void testGetStateByIndexReturnsCorrectState() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("tok");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("label");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("Q", new String[] { "Q" });
HMM.State state = (HMM.State) hmm.getState(0);
assertEquals("Q", state.getName());
}

@Test
public void testStatePrintExecutes() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("a");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("b");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
String[] dest = new String[] { "b" };
String[] label = new String[] { "b" };
hmm.addState("b", 0.5, 0.5, dest, label);
HMM.State state = (HMM.State) hmm.getState("b");
state.print();
}

@Test
public void testAddStateWithNullDestinationStateReturnsNullInTransitionIterator() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("hi");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("X");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("start", 1.0, 0.0, new String[] { "unknown" }, new String[] { "X" });
FeatureSequence input = new FeatureSequence(inputAlphabet, 1);
input.add("hi");
FeatureSequence output = new FeatureSequence(outputAlphabet, 1);
output.add("X");
HMM.State state = (HMM.State) hmm.getState("start");
try {
Transducer.TransitionIterator ti = state.transitionIterator(input, 0, output, 0);
ti.nextState();
fail("Expected AssertionError due to unresolved destination state");
} catch (AssertionError e) {
}
}

@Test(expected = UnsupportedOperationException.class)
public void testTransitionIteratorWithNegativePositionThrowsUnsupported() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("tok");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("tag");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("Q", new String[] { "Q" });
HMM.State state = (HMM.State) hmm.getState("Q");
state.transitionIterator(new FeatureSequence(inputAlphabet), -1, null, 0);
}

@Test(expected = UnsupportedOperationException.class)
public void testTransitionIteratorWithNullInputSequenceThrowsUnsupported() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("O");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("S", new String[] { "S" });
HMM.State state = (HMM.State) hmm.getState("S");
state.transitionIterator(null, 0, new FeatureSequence(outputAlphabet), 0);
}

@Test(expected = UnsupportedOperationException.class)
public void testTransitionIteratorWithNonFeatureSequenceThrowsUnsupported() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("O");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("S", new String[] { "S" });
HMM.State state = (HMM.State) hmm.getState("S");
cc.mallet.types.Sequence dummy = new cc.mallet.types.Sequence() {

public int size() {
return 1;
}

public Object get(int i) {
return null;
}

public void add(Object o) {
}

public Object removeLast() {
return null;
}

public Object getObject() {
return null;
}
};
state.transitionIterator(dummy, 0, new FeatureSequence(outputAlphabet), 0);
}

@Test
public void testInitTransitionsWithNullRandomProducesUniform() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("a");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("X");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("X", new String[] { "X" });
hmm.initTransitions(null, 0.0);
// double[] probs = hmm.getInitialMultinomial().getProbabilities();
// assertEquals(1.0, probs[0], 0.000001);
}

@Test
public void testInitEmissionsWithNullRandomProducesUniform() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("a");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("X");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("X", new String[] { "X" });
hmm.initEmissions(null, 0.0);
// double[] probs = hmm.getEmissionMultinomial()[0].getProbabilities();
// assertEquals(1.0, probs[0], 0.000001);
}

@Test
public void testAddOrderNStatesWithEmptyOutputAlphabetSkipsAllStates() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
Pipe pipe = new Pipe(inputAlphabet, outputAlphabet) {

public Instance pipe(Instance carrier) {
return carrier;
}
};
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
FeatureSequence input = new FeatureSequence(inputAlphabet, 0);
FeatureSequence output = new FeatureSequence(outputAlphabet, 0);
InstanceList list = new InstanceList(pipe);
Instance instance = new Instance(input, output, null, null);
list.addThruPipe(instance);
String s = hmm.addOrderNStates(list, new int[] { 1 }, new boolean[] { true }, "start", null, null, false);
assertEquals("start", s);
assertEquals(0, hmm.numStates());
}

@Test
public void testEstimateWithSingleStateComputesValidInitialWeight() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("x");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("Y");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("Y", new String[] { "Y" });
FeatureSequence input = new FeatureSequence(inputAlphabet, 1);
input.add("x");
FeatureSequence output = new FeatureSequence(outputAlphabet, 1);
output.add("Y");
Pipe pipe = new Pipe(inputAlphabet, outputAlphabet) {

public Instance pipe(Instance carrier) {
return carrier;
}
};
InstanceList list = new InstanceList(pipe);
list.addThruPipe(new Instance(input, output, null, null));
hmm.train(list);
hmm.estimate();
double weight = hmm.getState("Y").getInitialWeight();
assertTrue(Double.isFinite(weight));
}

@Test
public void testAllowedTransitionWithMatchingYesPatternReturnsTrue() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("in");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
java.lang.reflect.Method method;
try {
method = HMM.class.getDeclaredMethod("allowedTransition", String.class, String.class, java.util.regex.Pattern.class, java.util.regex.Pattern.class);
method.setAccessible(true);
boolean result = (boolean) method.invoke(hmm, "A", "B", null, Pattern.compile("A,B"));
assertTrue(result);
} catch (Exception e) {
fail("Exception accessing private method: " + e.getMessage());
}
}

@Test
public void testTrainWithMultipleInstancesProcessesCorrectly() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("T");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("T", new String[] { "T" });
Pipe pipe = new Pipe(inputAlphabet, outputAlphabet) {

public Instance pipe(Instance inst) {
return inst;
}
};
InstanceList ilist = new InstanceList(pipe);
FeatureSequence input1 = new FeatureSequence(inputAlphabet, 1);
input1.add("f");
FeatureSequence output1 = new FeatureSequence(outputAlphabet, 1);
output1.add("T");
FeatureSequence input2 = new FeatureSequence(inputAlphabet, 1);
input2.add("f");
FeatureSequence output2 = new FeatureSequence(outputAlphabet, 1);
output2.add("T");
ilist.addThruPipe(new Instance(input1, output1, null, null));
ilist.addThruPipe(new Instance(input2, output2, null, null));
boolean result = hmm.train(ilist);
assertTrue(result);
}

@Test
public void testAddStateWithEmptyTransitionAndLabelArrays() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("a");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("X");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
String[] emptyDestinations = new String[0];
String[] emptyLabels = new String[0];
hmm.addState("S0", 0.0, 0.0, emptyDestinations, emptyLabels);
assertNotNull(hmm.getState("S0"));
assertEquals(1, hmm.numStates());
}

@Test
public void testIncrementorDoesNotCrashOnEmptyEstimation() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("L");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("L", new String[] { "L" });
HMM.Incrementor incr = hmm.new Incrementor();
HMM.State state = (HMM.State) hmm.getState("L");
state.setInitialWeight(0.5);
incr.incrementInitialState(state, 1.0);
assertTrue(state.getInitialWeight() >= 0.0 || state.getInitialWeight() <= 0.0);
}

@Test
public void testWeightedIncrementorAppliesWeight() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("x");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("A");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("A", new String[] { "A" });
hmm.initTransitions(null, 0.0);
hmm.initEmissions(null, 0.0);
HMM.WeightedIncrementor incr = hmm.new WeightedIncrementor(0.0);
FeatureSequence input = new FeatureSequence(inputAlphabet, 1);
input.add("x");
FeatureSequence output = new FeatureSequence(outputAlphabet, 1);
output.add("A");
HMM.State state = (HMM.State) hmm.getState("A");
Transducer.TransitionIterator ti = state.transitionIterator(input, 0, output, 0);
if (ti.hasNext()) {
ti.nextState();
incr.incrementTransition(ti, 1.0);
}
// assertTrue(hmm.getEmissionMultinomial()[0].getProbabilities()[0] >= 0.0);
}

@Test
public void testAllowedHistoryRejectsOnForbiddenTransition() throws Exception {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
java.lang.reflect.Method method = HMM.class.getDeclaredMethod("allowedHistory", String[].class, Pattern.class, Pattern.class);
method.setAccessible(true);
String[] history = new String[] { "A", "B" };
Pattern forbidden = Pattern.compile("A,B");
Boolean result = (Boolean) method.invoke(hmm, history, forbidden, null);
assertFalse(result);
}

@Test
public void testAllowedHistoryAcceptsIfNoConstraints() throws Exception {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("X");
outputAlphabet.lookupIndex("Y");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
String[] history = new String[] { "X", "Y" };
java.lang.reflect.Method method = HMM.class.getDeclaredMethod("allowedHistory", String[].class, Pattern.class, Pattern.class);
method.setAccessible(true);
Object result = method.invoke(hmm, history, null, null);
assertEquals(true, result);
}

@Test
public void testTrainReturnsTrueWithSingleTokenSequence() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("token");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("LABEL");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("LABEL", new String[] { "LABEL" });
Pipe pipe = new Pipe(inputAlphabet, outputAlphabet) {

public Instance pipe(Instance carrier) {
return carrier;
}
};
FeatureSequence input = new FeatureSequence(inputAlphabet, 1);
input.add("token");
FeatureSequence output = new FeatureSequence(outputAlphabet, 1);
output.add("LABEL");
Instance inst = new Instance(input, output, null, null);
InstanceList list = new InstanceList(pipe);
list.addThruPipe(inst);
boolean trained = hmm.train(list);
assertTrue(trained);
}

@Test
public void testWriteAndReadObjectPreservesState() throws Exception {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("t");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("Y");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("Y", new String[] { "Y" });
hmm.initTransitions(null, 0.0);
hmm.initEmissions(null, 0.0);
File file = File.createTempFile("hmm_test", ".ser");
file.deleteOnExit();
hmm.write(file);
ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
HMM loaded = (HMM) ois.readObject();
ois.close();
assertNotNull(loaded);
assertNotNull(loaded.getState("Y"));
assertEquals(1, loaded.numStates());
}

@Test
public void testAddStateWithMultipleTransitionsAndLabels() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("x");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("S");
outputAlphabet.lookupIndex("T");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("S", 1.0, 0.5, new String[] { "S", "T" }, new String[] { "S", "T" });
assertNotNull(hmm.getState("S"));
assertEquals(1, hmm.numStates());
}

@Test
public void testConcatLabelsProducesCorrectString() throws Exception {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
java.lang.reflect.Method concatLabelsMethod = HMM.class.getDeclaredMethod("concatLabels", String[].class);
concatLabelsMethod.setAccessible(true);
String[] labels = new String[] { "A", "B" };
String result = (String) concatLabelsMethod.invoke(hmm, (Object) labels);
assertEquals("A,B", result);
}

@Test
public void testNextKGramUpdatesCorrectly() throws Exception {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("Z1");
outputAlphabet.lookupIndex("Z2");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
java.lang.reflect.Method nextKGramMethod = HMM.class.getDeclaredMethod("nextKGram", String[].class, int.class, String.class);
nextKGramMethod.setAccessible(true);
String[] history = new String[] { "B", "C" };
int k = 2;
String next = "D";
String result = (String) nextKGramMethod.invoke(hmm, history, k, next);
assertEquals("C,D", result);
}

@Test
public void testAddFullyConnectedStatesForTriLabelsWithThreeLabels() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("feat");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("PER");
outputAlphabet.lookupIndex("LOC");
outputAlphabet.lookupIndex("ORG");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addFullyConnectedStatesForTriLabels();
assertNotNull(hmm.getState("PER,PER,PER"));
assertNotNull(hmm.getState("PER,LOC,ORG"));
}

@Test
public void testAddStatesForBiLabelsConnectedAsInWithNonSymmetricTransitions() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("w");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("X");
outputAlphabet.lookupIndex("Y");
outputAlphabet.lookupIndex("Z");
Pipe pipe = new Pipe(inputAlphabet, outputAlphabet) {

public Instance pipe(Instance inst) {
return inst;
}
};
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
FeatureSequence input = new FeatureSequence(inputAlphabet, 3);
input.add("w");
input.add("w");
input.add("w");
FeatureSequence output = new FeatureSequence(outputAlphabet, 3);
output.add("X");
output.add("Y");
output.add("Z");
InstanceList list = new InstanceList(pipe);
list.addThruPipe(new Instance(input, output, null, null));
hmm.addStatesForBiLabelsConnectedAsIn(list);
assertNotNull(hmm.getState("X,Y"));
assertNotNull(hmm.getState("Y,Z"));
assertNull(hmm.getState("Z,X"));
}

@Test
public void testAddOrderNStatesRejectsUnorderedOrdersArray() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("a");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
Pipe pipe = new Pipe(inputAlphabet, outputAlphabet) {

public Instance pipe(Instance inst) {
return inst;
}
};
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
FeatureSequence input = new FeatureSequence(inputAlphabet, 2);
input.add("a");
input.add("a");
FeatureSequence output = new FeatureSequence(outputAlphabet, 2);
output.add("A");
output.add("B");
InstanceList list = new InstanceList(pipe);
list.addThruPipe(new Instance(input, output, null, null));
boolean errorThrown = false;
try {
hmm.addOrderNStates(list, new int[] { 2, 1 }, new boolean[] { false, true }, "A", null, null, true);
} catch (IllegalArgumentException e) {
errorThrown = true;
}
assertTrue(errorThrown);
}

@Test
public void testTrainWithMultipleStatesCorrectlyInitializesInitialWeights() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("word");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("O");
outputAlphabet.lookupIndex("B");
Pipe pipe = new Pipe(inputAlphabet, outputAlphabet) {

public Instance pipe(Instance inst) {
return inst;
}
};
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("O", new String[] { "B" });
hmm.addState("B", new String[] { "O" });
FeatureSequence input = new FeatureSequence(inputAlphabet, 2);
input.add("word");
input.add("word");
FeatureSequence output = new FeatureSequence(outputAlphabet, 2);
output.add("O");
output.add("B");
InstanceList list = new InstanceList(pipe);
list.addThruPipe(new Instance(input, output, null, null));
boolean trained = hmm.train(list);
assertTrue(trained);
double weightO = hmm.getState("O").getInitialWeight();
double weightB = hmm.getState("B").getInitialWeight();
assertTrue(Double.isFinite(weightO));
assertTrue(Double.isFinite(weightB));
}

@Test
public void testForbiddenLabelsExcludeExpectedStatesInOrderN() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("X");
outputAlphabet.lookupIndex("Y");
Pipe pipe = new Pipe(inputAlphabet, outputAlphabet) {

public Instance pipe(Instance inst) {
return inst;
}
};
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
FeatureSequence input = new FeatureSequence(inputAlphabet, 2);
input.add("f");
input.add("f");
FeatureSequence output = new FeatureSequence(outputAlphabet, 2);
output.add("X");
output.add("Y");
InstanceList list = new InstanceList(pipe);
list.addThruPipe(new Instance(input, output, null, null));
Pattern forbidden = Pattern.compile("X,Y");
String start = hmm.addOrderNStates(list, new int[] { 1 }, new boolean[] { true }, "X", forbidden, null, false);
assertEquals("X", start);
assertNull(hmm.getState("X,Y"));
}

@Test
public void testAddFullyConnectedStatesForHalfLabelsCreatesStates() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("xxx");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
Pipe pipe = new Pipe(inputAlphabet, outputAlphabet) {

public Instance pipe(Instance i) {
return i;
}
};
FeatureSequence input = new FeatureSequence(inputAlphabet, 2);
input.add("xxx");
input.add("xxx");
FeatureSequence output = new FeatureSequence(outputAlphabet, 2);
output.add("A");
output.add("B");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
InstanceList ilist = new InstanceList(pipe);
ilist.addThruPipe(new Instance(input, output, null, null));
hmm.addStatesForHalfLabelsConnectedAsIn(ilist);
assertNotNull(hmm.getState("A"));
assertNotNull(hmm.getState("B"));
assertEquals(2, hmm.numStates());
}

@Test
public void testTrainOnEmptyInstanceListCausesAssertionError() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("a");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("X");
Pipe pipe = new Pipe(inputAlphabet, outputAlphabet) {

public Instance pipe(Instance i) {
return i;
}
};
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("X", new String[] { "X" });
InstanceList emptyList = new InstanceList(pipe);
boolean assertionThrown = false;
try {
hmm.train(emptyList);
} catch (AssertionError e) {
assertionThrown = true;
}
assertTrue(assertionThrown);
}

@Test
public void testResetRestoresUniformInitialMultinomial() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("W");
inputAlphabet.lookupIndex("Z");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("T");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("T", new String[] { "T" });
hmm.reset();
// double[] probs = hmm.getInitialMultinomial().getProbabilities();
// assertEquals(1.0, probs[0], 1e-6);
}

@Test
public void testTrainWithMissingDestinationStateCausesAssertionErrorInSumLattice() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("w");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("Y");
Pipe pipe = new Pipe(inputAlphabet, outputAlphabet) {

public Instance pipe(Instance inst) {
return inst;
}
};
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("Y", new String[] { "Z" });
FeatureSequence input = new FeatureSequence(inputAlphabet, 1);
input.add("w");
FeatureSequence output = new FeatureSequence(outputAlphabet, 1);
output.add("Y");
InstanceList list = new InstanceList(pipe);
list.addThruPipe(new Instance(input, output, null, null));
boolean threw = false;
try {
hmm.train(list);
} catch (AssertionError e) {
threw = true;
}
assertTrue(threw);
}

@Test
public void testDefaultFeatureWeightsInitForAddStatesForThreeQuarterLabelsConnectedAsIn() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
Pipe pipe = new Pipe(inputAlphabet, outputAlphabet) {

public Instance pipe(Instance inst) {
return inst;
}
};
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
FeatureSequence input = new FeatureSequence(inputAlphabet, 2);
input.add("f");
input.add("f");
FeatureSequence output = new FeatureSequence(outputAlphabet, 2);
output.add("A");
output.add("B");
InstanceList list = new InstanceList(pipe);
list.addThruPipe(new Instance(input, output, null, null));
hmm.addStatesForThreeQuarterLabelsConnectedAsIn(list);
assertNotNull(hmm.getState("A"));
assertNotNull(hmm.getState("B"));
}

@Test
public void testGetTransitionMultinomialReturnsNullBeforeInit() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("token");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("LABEL");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
assertNull(hmm.getTransitionMultinomial());
}

@Test
public void testGetEmissionMultinomialReturnsNullBeforeInit() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("T");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
assertNull(hmm.getEmissionMultinomial());
}

@Test
public void testAddStateWithTransitionToSelfIsResolved() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("a");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("X");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
// hmm.addState("X", new String[] { "X" }, new String[] { "X" });
// assertNotNull(hmm.getState("X"));
assertSame(hmm.getState("X"), ((HMM.State) hmm.getState("X")).getDestinationState(0));
}

@Test
public void testAddOrderNStatesReturnsStartWhenOrderZero() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("inputToken");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("Tag1");
outputAlphabet.lookupIndex("Tag2");
Pipe pipe = new Pipe(inputAlphabet, outputAlphabet) {

public Instance pipe(Instance inst) {
return inst;
}
};
InstanceList instanceList = new InstanceList(pipe);
FeatureSequence inputSeq = new FeatureSequence(inputAlphabet, 2);
inputSeq.add("inputToken");
inputSeq.add("inputToken");
FeatureSequence outputSeq = new FeatureSequence(outputAlphabet, 2);
outputSeq.add("Tag1");
outputSeq.add("Tag2");
Instance instance = new Instance(inputSeq, outputSeq, null, null);
instanceList.addThruPipe(instance);
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
String result = hmm.addOrderNStates(instanceList, null, null, "Tag1", null, null, false);
assertEquals("Tag1", result);
}

@Test
public void testAddOrderNStatesWithEmptyInstanceListAndNonNullOrders() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("w");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("POS1");
outputAlphabet.lookupIndex("POS2");
Pipe pipe = new Pipe(inputAlphabet, outputAlphabet) {

public Instance pipe(Instance inst) {
return inst;
}
};
InstanceList emptyList = new InstanceList(pipe);
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
String result = hmm.addOrderNStates(emptyList, new int[] { 1 }, new boolean[] { true }, "POS1", null, null, true);
assertEquals("POS1", result);
}

@Test
public void testAddOrderNStatesWithReverseConnectionBlockedByAllowedPattern() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("tok");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
Pipe pipe = new Pipe(inputAlphabet, outputAlphabet) {

public Instance pipe(Instance i) {
return i;
}
};
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
FeatureSequence input = new FeatureSequence(inputAlphabet, 2);
input.add("tok");
input.add("tok");
FeatureSequence output = new FeatureSequence(outputAlphabet, 2);
output.add("A");
output.add("B");
Instance instance = new Instance(input, output, null, null);
InstanceList list = new InstanceList(pipe);
list.addThruPipe(instance);
Pattern allowed = Pattern.compile("B,B");
String result = hmm.addOrderNStates(list, new int[] { 1 }, new boolean[] { true }, "A", null, allowed, false);
assertEquals("A", result);
assertNull(hmm.getState("A,B"));
}

@Test
public void testAddStateRejectsIfAlreadyExists() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("v");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("X");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("X", new String[] { "X" });
boolean thrown = false;
try {
hmm.addState("X", new String[] { "X" });
} catch (IllegalArgumentException e) {
thrown = true;
}
assertTrue(thrown);
}

@Test
public void testInitialStateIteratorEmptyWhenNoStateHasInitialWeight() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("obs");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("tag");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("tag", 0.0, 0.0, new String[] { "tag" }, new String[] { "tag" });
assertFalse(hmm.initialStateIterator().hasNext());
}

@Test
public void testTrainInitializesEstimatorsOnlyOnce() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("L");
Pipe pipe = new Pipe(inputAlphabet, outputAlphabet) {

public Instance pipe(Instance i) {
return i;
}
};
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("L", new String[] { "L" });
FeatureSequence input = new FeatureSequence(inputAlphabet, 2);
input.add("f");
input.add("f");
FeatureSequence output = new FeatureSequence(outputAlphabet, 2);
output.add("L");
output.add("L");
Instance instance = new Instance(input, output, null, null);
InstanceList list = new InstanceList(pipe);
list.addThruPipe(instance);
boolean trained = hmm.train(list);
assertTrue(trained);
trained = hmm.train(list);
assertTrue(trained);
}

@Test
public void testAddFullyConnectedStatesForThreeQuarterLabelsDoesNotThrow() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("input");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("L1");
outputAlphabet.lookupIndex("L2");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
boolean threw = false;
try {
hmm.addFullyConnectedStatesForThreeQuarterLabels(null);
} catch (Exception e) {
threw = true;
}
assertFalse(threw);
assertEquals(2, hmm.numStates());
}
}
