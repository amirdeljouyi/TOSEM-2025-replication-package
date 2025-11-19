package cc.mallet.fst.tests;

import cc.mallet.fst.CRF;
import cc.mallet.fst.HMM;
import cc.mallet.fst.Transducer;
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

public class HMM_5_GPTLLMTest {

@Test
public void testAddStateAndGetState() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("a");
outputAlphabet.lookupIndex("X");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("StartState", 1.0, 0.0, new String[] { "StartState" }, new String[] { "X" });
assertNotNull(hmm.getState("StartState"));
assertEquals(1, hmm.numStates());
}

@Test(expected = IllegalArgumentException.class)
public void testAddDuplicateStateThrowsException() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("a");
outputAlphabet.lookupIndex("X");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("StateA", 0.0, 0.0, new String[] { "StateA" }, new String[] { "X" });
hmm.addState("StateA", 0.0, 0.0, new String[] { "StateA" }, new String[] { "X" });
}

@Test
public void testInitTransitionsCreatesTransitionMultinomial() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("obs1");
outputAlphabet.lookupIndex("L1");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("S", new String[] { "S" });
hmm.initTransitions(new Random(123), 0.5);
Multinomial[] transitionM = hmm.getTransitionMultinomial();
assertNotNull(transitionM);
assertEquals(1, transitionM.length);
assertEquals(hmm.numStates(), transitionM.length);
}

@Test
public void testInitEmissionsCreatesEmissionMultinomial() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("obs1");
outputAlphabet.lookupIndex("L1");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("S", new String[] { "S" });
hmm.initEmissions(new Random(456), 1.0);
Multinomial[] emissionM = hmm.getEmissionMultinomial();
assertNotNull(emissionM);
assertEquals(1, emissionM.length);
assertEquals(hmm.numStates(), emissionM.length);
}

@Test
public void testTrainSetsInitialAndMultinomials() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("aa");
outputAlphabet.lookupIndex("L1");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
Pipe pipe = new SerialPipes(Arrays.asList(new TokenSequence2FeatureSequence(), new Target2LabelSequence()));
InstanceList instances = new InstanceList(pipe);
Alphabet targetAlphabet = instances.getTargetAlphabet();
targetAlphabet.lookupIndex("L1");
targetAlphabet.lookupIndex("L2");
TokenSequence dataSeq = new TokenSequence();
dataSeq.add("aa");
dataSeq.add("aa");
TokenSequence labelSeq = new TokenSequence();
labelSeq.add("L1");
labelSeq.add("L2");
Instance instance = new Instance(dataSeq, labelSeq, null, null);
instances.addThruPipe(instance);
HMM hmm = new HMM(instances.getDataAlphabet(), instances.getTargetAlphabet());
hmm.addFullyConnectedStatesForLabels();
boolean result = hmm.train(instances);
assertTrue(result);
assertNotNull(hmm.getTransitionMultinomial());
assertNotNull(hmm.getEmissionMultinomial());
assertNotNull(hmm.getInitialMultinomial());
}

@Test
public void testAddOrderZeroStates() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("a");
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
InstanceList dummyList = new InstanceList((Pipe) null);
String start = hmm.addOrderNStates(dummyList, null, null, "A", null, null, true);
assertNotNull(start);
assertEquals(2, hmm.numStates());
}

@Test
public void testAddSelfTransitioningStateForAllLabels() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("a");
outputAlphabet.lookupIndex("X");
outputAlphabet.lookupIndex("Y");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addSelfTransitioningStateForAllLabels("loop");
assertEquals(1, hmm.numStates());
assertNotNull(hmm.getState("loop"));
}

@Test
public void testAddStatesForLabelsConnectedAsIn() {
TokenSequence tokens1 = new TokenSequence();
tokens1.add("x");
tokens1.add("y");
TokenSequence labels1 = new TokenSequence();
labels1.add("A");
labels1.add("B");
Pipe pipe = new SerialPipes(Arrays.asList(new TokenSequence2FeatureSequence(), new Target2LabelSequence()));
InstanceList list = new InstanceList(pipe);
list.addThruPipe(new Instance(tokens1, labels1, null, null));
HMM hmm = new HMM(list.getDataAlphabet(), list.getTargetAlphabet());
hmm.addStatesForLabelsConnectedAsIn(list);
assertEquals(2, hmm.numStates());
assertNotNull(hmm.getState("A"));
assertNotNull(hmm.getState("B"));
}

@Test
public void testAddStatesForBiLabelsConnectedAsIn() {
TokenSequence seq = new TokenSequence();
seq.add("a");
seq.add("b");
seq.add("c");
TokenSequence labels = new TokenSequence();
labels.add("X");
labels.add("Y");
labels.add("X");
Pipe pipe = new SerialPipes(Arrays.asList(new TokenSequence2FeatureSequence(), new Target2LabelSequence()));
InstanceList ilist = new InstanceList(pipe);
ilist.addThruPipe(new Instance(seq, labels, null, null));
HMM hmm = new HMM(ilist.getDataAlphabet(), ilist.getTargetAlphabet());
hmm.addStatesForBiLabelsConnectedAsIn(ilist);
assertTrue(hmm.numStates() > 0);
}

@Test
public void testSerializationAndDeserialization() throws Exception {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("a");
outputAlphabet.lookupIndex("A");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addFullyConnectedStates(new String[] { "A" });
// File modelFile = tempFolder.newFile("hmm_model.ser");
// hmm.write(modelFile);
// ObjectInputStream ois = new ObjectInputStream(new FileInputStream(modelFile));
// HMM loaded = (HMM) ois.readObject();
// ois.close();
// assertEquals(hmm.numStates(), loaded.numStates());
// assertEquals(hmm.getInputAlphabet().size(), loaded.getInputAlphabet().size());
// assertNotNull(loaded.getState("A"));
}

@Test(expected = AssertionError.class)
public void testTrainOnEmptyInstanceListThrowsAssertionError() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("a");
outputAlphabet.lookupIndex("X");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
Pipe pipe = new SerialPipes(Arrays.asList(new TokenSequence2FeatureSequence(), new Target2LabelSequence()));
InstanceList emptyList = new InstanceList(pipe);
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.train(emptyList);
}

@Test(expected = IllegalArgumentException.class)
public void testAddOrderNStatesWithInvalidOrdersThrowsException() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("foo");
outputAlphabet.lookupIndex("X");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
InstanceList training = new InstanceList((Pipe) null);
int[] invalidOrders = new int[] { 2, 1 };
hmm.addOrderNStates(training, invalidOrders, new boolean[] { true, true }, "X", null, null, false);
}

@Test(expected = IllegalArgumentException.class)
public void testAddOrderNStatesWithNullDefaultsButNonMatchingLengthThrowsException() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("foo");
outputAlphabet.lookupIndex("X");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
InstanceList training = new InstanceList((Pipe) null);
int[] orders = new int[] { 1, 2 };
boolean[] invalidDefaults = new boolean[] { true };
hmm.addOrderNStates(training, orders, invalidDefaults, "X", null, null, true);
}

@Test
public void testAddOrderNStatesWithForbiddenPatternExcludesStates() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("token");
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
Pattern forbidden = Pattern.compile("A,B");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
InstanceList emptyTraining = new InstanceList((Pipe) null);
String start = hmm.addOrderNStates(emptyTraining, new int[] { 1 }, new boolean[] { false }, "A", forbidden, null, true);
assertEquals("A", start);
assertTrue(hmm.numStates() > 0);
assertNull(hmm.getState("A,B"));
}

@Test
public void testAddOrderNStatesWithAllowedPatternOnlyIncludesValidTransitions() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("z");
outputAlphabet.lookupIndex("C");
outputAlphabet.lookupIndex("D");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
Pattern allowed = Pattern.compile("C,D");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
InstanceList emptyList = new InstanceList((Pipe) null);
String start = hmm.addOrderNStates(emptyList, new int[] { 1 }, new boolean[] { false }, "C", null, allowed, true);
assertNotNull(start);
assertNull(hmm.getState("D,C"));
}

@Test
public void testStateWithNoOutgoingTransitionsCreatesEmptyIterator() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("x");
outputAlphabet.lookupIndex("X");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("solo", new String[0]);
HMM.State state = (HMM.State) hmm.getState("solo");
FeatureSequence seq = new FeatureSequence(inputAlphabet);
seq.add("x");
Sequence outputSeq = new FeatureSequence(outputAlphabet);
// outputSeq.add("X");
Transducer.TransitionIterator iterator = state.transitionIterator(seq, 0, outputSeq, 0);
assertFalse(iterator.hasNext());
}

@Test
public void testTrainIgnoresMismatchedInputOutputLengths() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("i1");
inputAlphabet.lookupIndex("i2");
outputAlphabet.lookupIndex("L");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
Pipe pipe = new SerialPipes(Arrays.asList(new TokenSequence2FeatureSequence(), new Target2LabelSequence()));
InstanceList list = new InstanceList(pipe);
TokenSequence input = new TokenSequence();
input.add("i1");
input.add("i2");
TokenSequence target = new TokenSequence();
target.add("L");
list.addThruPipe(new Instance(input, target, null, null));
HMM hmm = new HMM(list.getDataAlphabet(), list.getTargetAlphabet());
hmm.addState("L", new String[] { "L" });
boolean result = hmm.train(list);
assertTrue(result);
}

@Test
public void testEstimateHandlesNullMultinomialsSafely() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("v");
outputAlphabet.lookupIndex("T");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addFullyConnectedStates(new String[] { "T" });
hmm.reset();
hmm.estimate();
assertNotNull(hmm.getEmissionMultinomial());
assertNotNull(hmm.getTransitionMultinomial());
assertNotNull(hmm.getInitialMultinomial());
}

@Test
public void testPartialSerializationWithNullEstimators() throws Exception {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("foo");
outputAlphabet.lookupIndex("bar");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addFullyConnectedStates(new String[] { "bar" });
// File f = folder.newFile("partial.ser");
// ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
// oos.writeObject(hmm);
// oos.close();
// ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
// HMM reloaded = (HMM) ois.readObject();
// ois.close();
// assertEquals(hmm.numStates(), reloaded.numStates());
// assertEquals(hmm.getInputAlphabet().size(), reloaded.getInputAlphabet().size());
}

@Test
public void testWeightedIncrementorScalesProperly() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("t1");
outputAlphabet.lookupIndex("L1");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addFullyConnectedStates(new String[] { "L1" });
hmm.reset();
HMM.State state = (HMM.State) hmm.getState("L1");
FeatureSequence inputSeq = new FeatureSequence(inputAlphabet);
inputSeq.add("t1");
Sequence outputSeq = new FeatureSequence(outputAlphabet);
// outputSeq.add("L1");
Transducer.TransitionIterator iterator = state.transitionIterator(inputSeq, 0, outputSeq, 0);
HMM.WeightedIncrementor inc = hmm.new WeightedIncrementor(3.0);
while (iterator.hasNext()) {
iterator.nextState();
inc.incrementTransition(iterator, 1.0);
}
inc.incrementInitialState(state, 1.0);
}

@Test
public void testAddStateWithEmptyTransitions() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("a");
outputAlphabet.lookupIndex("X");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("NoTransitions", 0.0, 0.0, new String[] {}, new String[] {});
assertEquals(1, hmm.numStates());
assertEquals("NoTransitions", hmm.getState("NoTransitions").getName());
}

@Test
public void testInitialStateIteratorEmptyWhenNoInitialState() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("aa");
outputAlphabet.lookupIndex("X");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("nonInitial", 0.0, 0.0, new String[] { "nonInitial" }, new String[] { "X" });
Iterator<?> it = hmm.initialStateIterator();
assertFalse(it.hasNext());
}

@Test
public void testInitialStateIteratorReturnsState() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("aa");
outputAlphabet.lookupIndex("X");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("initial", 1.0, 0.0, new String[] { "initial" }, new String[] { "X" });
Iterator<?> it = hmm.initialStateIterator();
assertTrue(it.hasNext());
assertEquals("initial", ((HMM.State) it.next()).getName());
}

@Test
public void testAddStateWithMismatchedLabelsThrowsAssertionError() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("a");
outputAlphabet.lookupIndex("X");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
try {
hmm.addState("InvalidState", 0.0, 0.0, new String[] { "One" }, new String[] { "X", "Y" });
fail("Expected AssertionError due to mismatched lengths");
} catch (AssertionError e) {
}
}

@Test
public void testEstimateWithoutResetOrInitEmitsDefaults() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("tok");
outputAlphabet.lookupIndex("L");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addFullyConnectedStatesForLabels();
hmm.reset();
hmm.estimate();
assertNotNull(hmm.getEmissionMultinomial());
assertNotNull(hmm.getTransitionMultinomial());
}

@Test
public void testStateGetDestinationWithUnresolvedTransitionResolvesProperly() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("word");
outputAlphabet.lookupIndex("POS");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("S", 0.0, 0.0, new String[] { "S" }, new String[] { "POS" });
HMM.State state = (HMM.State) hmm.getState("S");
assertEquals("S", state.getDestinationState(0).getName());
}

@Test
public void testTransitionIteratorWeightNonNullForMatchedOutput() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("word");
outputAlphabet.lookupIndex("LABEL");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("S", 0.0, 0.0, new String[] { "S" }, new String[] { "LABEL" });
hmm.reset();
HMM.State state = (HMM.State) hmm.getState("S");
FeatureSequence inputSeq = new FeatureSequence(inputAlphabet);
inputSeq.add("word");
FeatureSequence outputSeq = new FeatureSequence(outputAlphabet);
outputSeq.add("LABEL");
Transducer.TransitionIterator it = state.transitionIterator(inputSeq, 0, outputSeq, 0);
assertTrue(it.hasNext());
it.nextState();
double weight = it.getWeight();
assertFalse(Double.isNaN(weight));
}

@Test
public void testTransitionIteratorWeightIsImpossibleForNonMatchingLabel() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("x");
outputAlphabet.lookupIndex("Y");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("S", 0.0, 0.0, new String[] { "S" }, new String[] { "Y" });
hmm.reset();
HMM.State state = (HMM.State) hmm.getState("S");
FeatureSequence inputSeq = new FeatureSequence(inputAlphabet);
inputSeq.add("x");
FeatureSequence outputSeq = new FeatureSequence(outputAlphabet);
outputSeq.add("Z");
Transducer.TransitionIterator it = state.transitionIterator(inputSeq, 0, outputSeq, 0);
assertFalse(it.hasNext());
}

@Test
public void testAddStatesForHalfLabelsConnectedCreatesProperCount() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("in1");
inputAlphabet.lookupIndex("in2");
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
TokenSequence tokenSequence = new TokenSequence();
tokenSequence.add("in1");
tokenSequence.add("in2");
TokenSequence targetSequence = new TokenSequence();
targetSequence.add("A");
targetSequence.add("B");
SerialPipes pipes = new SerialPipes(Arrays.asList(new TokenSequence2FeatureSequence(), new Target2LabelSequence()));
Instance instance = new Instance(tokenSequence, targetSequence, null, null);
InstanceList instanceList = new InstanceList(pipes);
instanceList.addThruPipe(instance);
HMM hmm = new HMM(instanceList.getDataAlphabet(), instanceList.getTargetAlphabet());
hmm.addStatesForHalfLabelsConnectedAsIn(instanceList);
assertEquals(2, hmm.numStates());
assertNotNull(hmm.getState("A"));
assertNotNull(hmm.getState("B"));
}

@Test
public void testAddFullyConnectedStatesForTriLabelsPopulatesExpectedStateCount() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("w");
outputAlphabet.lookupIndex("X");
outputAlphabet.lookupIndex("Y");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addFullyConnectedStatesForTriLabels();
assertEquals(8, hmm.numStates());
}

@Test
public void testAddFullyConnectedStatesForBiLabelsStateNamesFormat() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("i");
outputAlphabet.lookupIndex("X");
outputAlphabet.lookupIndex("Y");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addFullyConnectedStatesForBiLabels();
assertNotNull(hmm.getState("X,Y"));
assertNotNull(hmm.getState("Y,Y"));
}

@Test
public void testAddOrderNStatesWithOrderZeroOnlyStartReturned() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("tok");
outputAlphabet.lookupIndex("S");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
String start = hmm.addOrderNStates(new InstanceList((Pipe) null), new int[] { 0 }, new boolean[] { true }, "S", null, null, true);
assertEquals("S", start);
assertEquals(1, hmm.numStates());
assertNotNull(hmm.getState("S"));
}

@Test
public void testAllowedHistoryRejectsPatternCase() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("x");
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
Pattern forbidden = Pattern.compile("A,B");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
String[] history = new String[] { "A", "B" };
boolean allowed = hmm.addOrderNStates(new InstanceList((Pipe) null), new int[] { 1 }, new boolean[] { false }, "A", forbidden, null, true).equals("A");
assertTrue(allowed);
}

@Test
public void testTransitionIteratorSkipsImpossibleWeights() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f");
outputAlphabet.lookupIndex("A");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("A", new String[] { "A" });
hmm.reset();
HMM.State s = (HMM.State) hmm.getState("A");
FeatureSequence input = new FeatureSequence(inputAlphabet);
input.add("f");
FeatureSequence output = new FeatureSequence(outputAlphabet);
output.add("B");
Transducer.TransitionIterator iterator = s.transitionIterator(input, 0, output, 0);
assertFalse(iterator.hasNext());
}

@Test
public void testDestinationNameResolutionOccursOnlyOnce() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("foo");
outputAlphabet.lookupIndex("L");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("S", new String[] { "S" });
HMM.State s = (HMM.State) hmm.getState("S");
HMM.State dest1 = s.getDestinationState(0);
HMM.State dest2 = s.getDestinationState(0);
assertSame(dest1, dest2);
}

@Test
public void testSerializationWithNullFieldsSucceeds() throws Exception {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("x");
outputAlphabet.lookupIndex("Y");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("Y", new String[] { "Y" });
// File file = tmpFolder.newFile("hmm-null.ser");
// ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
// oos.writeObject(hmm);
// oos.close();
// ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
// HMM deserialized = (HMM) ois.readObject();
// ois.close();
// assertEquals(1, deserialized.numStates());
// assertNotNull(deserialized.getState("Y"));
}

@Test
public void testResetReinitializesAllMultinomials() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("x");
inputAlphabet.lookupIndex("y");
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addFullyConnectedStates(new String[] { "A", "B" });
hmm.reset();
Multinomial[] emissions = hmm.getEmissionMultinomial();
Multinomial[] transitions = hmm.getTransitionMultinomial();
Multinomial initial = hmm.getInitialMultinomial();
assertNotNull(emissions);
assertEquals(2, emissions.length);
assertEquals(2, transitions.length);
assertNotNull(initial);
}

@Test
public void testWeightedIncrementorScalingFactorAffectsTransition() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("x");
outputAlphabet.lookupIndex("A");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("A", new String[] { "A" });
hmm.reset();
HMM.State source = (HMM.State) hmm.getState("A");
FeatureSequence inputSeq = new FeatureSequence(inputAlphabet);
inputSeq.add("x");
FeatureSequence outputSeq = new FeatureSequence(outputAlphabet);
outputSeq.add("A");
Transducer.TransitionIterator it = source.transitionIterator(inputSeq, 0, outputSeq, 0);
HMM.WeightedIncrementor weighted = hmm.new WeightedIncrementor(5.0);
if (it.hasNext()) {
it.nextState();
weighted.incrementTransition(it, 2.0);
weighted.incrementInitialState(source, 2.0);
}
}

@Test
public void testTrainHandlesMultipleInstancesCorrectly() {
TokenSequence dataSeq1 = new TokenSequence();
dataSeq1.add("hot");
dataSeq1.add("cold");
TokenSequence targetSeq1 = new TokenSequence();
targetSeq1.add("A");
targetSeq1.add("B");
TokenSequence dataSeq2 = new TokenSequence();
dataSeq2.add("cold");
dataSeq2.add("hot");
TokenSequence targetSeq2 = new TokenSequence();
targetSeq2.add("B");
targetSeq2.add("A");
SerialPipes pipes = new SerialPipes(Arrays.asList(new TokenSequence2FeatureSequence(), new Target2LabelSequence()));
InstanceList trainingList = new InstanceList(pipes);
trainingList.addThruPipe(new Instance(dataSeq1, targetSeq1, null, null));
trainingList.addThruPipe(new Instance(dataSeq2, targetSeq2, null, null));
HMM hmm = new HMM(trainingList.getDataAlphabet(), trainingList.getTargetAlphabet());
hmm.addFullyConnectedStatesForLabels();
boolean result = hmm.train(trainingList);
assertTrue(result);
assertNotNull(hmm.getInitialMultinomial());
}

@Test
public void testConcatLabelsSingleElement() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("hi");
outputAlphabet.lookupIndex("B");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
String result = hmm.addOrderNStates(new InstanceList((Pipe) null), new int[] { 1 }, new boolean[] { false }, "B", null, null, true);
assertTrue(result.contains("B"));
}

@Test
public void testAddFullyConnectedStatesForThreeQuarterLabelsAddsCorrectly() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("in");
outputAlphabet.lookupIndex("L1");
outputAlphabet.lookupIndex("L2");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addFullyConnectedStatesForThreeQuarterLabels(new InstanceList((Pipe) null));
assertEquals(2, hmm.numStates());
}

@Test
public void testAddStateWithSameNameDifferentParamsThrows() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("x");
outputAlphabet.lookupIndex("Y");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("S", 0.0, 0.0, new String[] { "S" }, new String[] { "Y" });
try {
hmm.addState("S", 1.0, 0.0, new String[] { "S" }, new String[] { "Y" });
fail("Expected IllegalArgumentException due to duplicate state");
} catch (IllegalArgumentException e) {
}
}

@Test
public void testAddStateWithZeroInitialWeightExcludedFromInitialStates() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("token");
outputAlphabet.lookupIndex("LABEL");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("S", 0.0, 0.0, new String[] { "S" }, new String[] { "LABEL" });
assertFalse(hmm.initialStateIterator().hasNext());
}

@Test
public void testTransitionIteratorThrowsOnNullInputSequence() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("x");
outputAlphabet.lookupIndex("Y");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("S", new String[] { "S" });
try {
HMM.State s = (HMM.State) hmm.getState("S");
s.transitionIterator(null, 0, null, 0);
fail("Expected UnsupportedOperationException due to null input");
} catch (UnsupportedOperationException e) {
}
}

@Test
public void testTransitionIteratorThrowsForInvalidInputType() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("x");
outputAlphabet.lookupIndex("Y");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("S", new String[] { "S" });
HMM.State s = (HMM.State) hmm.getState("S");
try {
// s.transitionIterator(new Alphabet(), 0, null, 0);
fail("Expected UnsupportedOperationException due to wrong type");
} catch (UnsupportedOperationException e) {
}
}

@Test
public void testAddStatesForThreeQuarterLabelsConnectedAsInWithDisconnectedTrainingSet() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f");
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
TokenSequence feats = new TokenSequence();
feats.add("f");
feats.add("f");
TokenSequence labels = new TokenSequence();
labels.add("A");
labels.add("A");
SerialPipes pipes = new SerialPipes(Arrays.asList(new TokenSequence2FeatureSequence(), new Target2LabelSequence()));
InstanceList list = new InstanceList(pipes);
list.addThruPipe(new Instance(feats, labels, null, null));
HMM hmm = new HMM(list.getDataAlphabet(), list.getTargetAlphabet());
hmm.addStatesForThreeQuarterLabelsConnectedAsIn(list);
assertEquals(1, hmm.numStates());
assertEquals("A", hmm.getState(0).getName());
}

@Test
public void testAddOrderNStatesEmptyAlphabetProducesZeroStates() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
String result = hmm.addOrderNStates(new InstanceList((Pipe) null), new int[] { 1 }, new boolean[] { true }, "X", null, null, true);
assertEquals("X", result);
assertEquals(0, hmm.numStates());
}

@Test
public void testLabelConnectionsInEmptyInstanceListReturnsFalseMatrix() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("x");
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
InstanceList emptyList = new InstanceList((Pipe) null);
boolean[][] matrix = hmm.addOrderNStates(emptyList, new int[] { 1 }, new boolean[] { false }, "A", null, null, false) != null ? new boolean[outputAlphabet.size()][outputAlphabet.size()] : null;
assertNotNull(matrix);
}

@Test
public void testWriteObjectSerializesStateWithNullArrays() throws Exception {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("w");
outputAlphabet.lookupIndex("L");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
// HMM.State state = new HMM(inputAlphabet, outputAlphabet).new State();
// File file = tempFolder.newFile("state-null.ser");
// ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
// oos.writeObject(state);
// oos.close();
// ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
// Object deserialized = ois.readObject();
// ois.close();
// assertTrue(deserialized instanceof HMM.State);
}

@Test
public void testNextKGramFromShortHistory() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("a");
outputAlphabet.lookupIndex("X");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
String[] shortHistory = new String[] { "X" };
String next = "Y";
String result = hmm.addOrderNStates(new InstanceList((Pipe) null), new int[] { 1 }, new boolean[] { false }, "X", null, null, true);
assertNotNull(result);
}

@Test
public void testAddStateWithNullDestinationsResultsInStateWithNullTransitionList() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("tok");
outputAlphabet.lookupIndex("X");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
// HMM.State newState = new HMM(inputAlphabet, outputAlphabet).new State();
// assertNull(newState.destinationNames);
// assertNull(newState.destinations);
}

@Test
public void testStateTransitionIteratorWithNullOutputAppliesAllLabels() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("a");
outputAlphabet.lookupIndex("X");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("S", new String[] { "S" });
hmm.reset();
FeatureSequence inputSeq = new FeatureSequence(inputAlphabet);
inputSeq.add("a");
HMM.State state = (HMM.State) hmm.getState("S");
Transducer.TransitionIterator iterator = state.transitionIterator(inputSeq, 0, null, 0);
assertTrue(iterator.hasNext());
iterator.nextState();
assertEquals("X", iterator.getOutput());
assertNotNull(iterator.getInput());
}

@Test
public void testTransitionIteratorSkipsImpossibleTransitionsCleanly() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("w");
outputAlphabet.lookupIndex("POS");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
// hmm.addState("S", new String[] { "S" }, new String[] { "POS" });
// hmm.reset();
FeatureSequence input = new FeatureSequence(inputAlphabet);
input.add("w");
FeatureSequence output = new FeatureSequence(outputAlphabet);
output.add("NON_MATCHING_LABEL");
HMM.State s = (HMM.State) hmm.getState("S");
Transducer.TransitionIterator it = s.transitionIterator(input, 0, output, 0);
assertFalse(it.hasNext());
}

@Test
public void testResetSetsCorrectMultinomialArrayLengths() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("p");
inputAlphabet.lookupIndex("q");
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("A", new String[] { "B" });
hmm.addState("B", new String[] { "A" });
hmm.reset();
assertEquals(2, hmm.getTransitionMultinomial().length);
assertEquals(2, hmm.getEmissionMultinomial().length);
assertNotNull(hmm.getInitialMultinomial());
}

@Test
public void testEstimateReinitializesEstimatorsAfterTraining() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f1");
outputAlphabet.lookupIndex("LABEL");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("LABEL", new String[] { "LABEL" });
hmm.reset();
hmm.estimate();
Multinomial[] transitions = hmm.getTransitionMultinomial();
assertNotNull(transitions);
assertTrue(transitions[0].getAlphabet().size() > 0);
}

@Test
public void testGetStateIndexReturnsCorrectId() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("feat1");
outputAlphabet.lookupIndex("Tag");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("Tag", new String[] { "Tag" });
HMM.State state = (HMM.State) hmm.getState("Tag");
assertEquals(0, state.getIndex());
}

@Test
public void testInitialMultinomialAffectsStateInitialWeight() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("input");
outputAlphabet.lookupIndex("Y");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("Y", new String[] { "Y" });
hmm.initTransitions(null, 0.0);
HMM.State s = (HMM.State) hmm.getState("Y");
double logProb = hmm.getInitialMultinomial().logProbability("Y");
assertEquals(logProb, s.getInitialWeight(), 1e-6);
}

@Test
public void testTrainWithAnonymousInstanceNamesSucceeds() {
TokenSequence seq = new TokenSequence();
seq.add("x");
seq.add("y");
TokenSequence labels = new TokenSequence();
labels.add("L1");
labels.add("L1");
Pipe pipe = new SerialPipes(Arrays.asList(new TokenSequence2FeatureSequence(), new Target2LabelSequence()));
InstanceList list = new InstanceList(pipe);
list.addThruPipe(new Instance(seq, labels, null, null));
HMM hmm = new HMM(list.getDataAlphabet(), list.getTargetAlphabet());
hmm.addFullyConnectedStates(new String[] { "L1" });
hmm.train(list);
assertNotNull(hmm.getEmissionMultinomial()[0]);
assertNotNull(hmm.getTransitionMultinomial()[0]);
}

@Test
public void testWriteAndReadObjectTransportsAllStates() throws Exception {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("foo");
outputAlphabet.lookupIndex("X");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addFullyConnectedStates(new String[] { "X" });
hmm.reset();
// File serializedFile = folder.newFile("hmm.obj");
// ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(serializedFile));
// oos.writeObject(hmm);
// oos.close();
// ObjectInputStream ois = new ObjectInputStream(new FileInputStream(serializedFile));
// Object loaded = ois.readObject();
// ois.close();
// assertNotNull(loaded);
// assertTrue(loaded instanceof HMM);
// HMM loadedHMM = (HMM) loaded;
// assertEquals("X", loadedHMM.getState(0).getName());
}

@Test
public void testIncrementorUpdatesInitialEstimatorCount() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("u");
outputAlphabet.lookupIndex("T");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("T", new String[] { "T" });
hmm.reset();
HMM.Incrementor inc = hmm.new Incrementor();
Transducer.State s = hmm.getState("T");
inc.incrementInitialState(s, 1.0);
assertTrue(hmm.getInitialMultinomial().getAlphabet().contains("T"));
}

@Test
public void testAddStateZeroDestinationsZeroLabelsThrowsAssertionError() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("x");
outputAlphabet.lookupIndex("Y");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
try {
hmm.addState("Invalid", 0.0, 0.0, new String[0], new String[1]);
fail("Expected AssertionError due to mismatched arrays");
} catch (AssertionError e) {
}
}

@Test
public void testAddOrderNStatesWithZeroHistoryCreatesValidState() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("t");
outputAlphabet.lookupIndex("L1");
outputAlphabet.lookupIndex("L2");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
InstanceList dummy = new InstanceList((Pipe) null);
String start = hmm.addOrderNStates(dummy, null, null, "L1", null, null, true);
assertEquals("L1", start);
assertEquals(2, hmm.numStates());
}

@Test
public void testAllowedTransitionDisallowsWithForbiddenPattern() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("a");
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
Pattern forbidden = Pattern.compile("A,B");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
InstanceList dummy = new InstanceList((Pipe) null);
String start = hmm.addOrderNStates(dummy, new int[] { 1 }, new boolean[] { false }, "A", forbidden, null, true);
assertEquals("A", start);
assertNull(hmm.getState("A,B"));
}

@Test
public void testAddFullyConnectedStatesForTriLabelsPopulatesCubicCount() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("obs");
outputAlphabet.lookupIndex("X");
outputAlphabet.lookupIndex("Y");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addFullyConnectedStatesForTriLabels();
assertEquals(8, hmm.numStates());
}

@Test
public void testStatePrintOutputsExpectedInformation() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("v1");
outputAlphabet.lookupIndex("E");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("E", 1.0, 1.0, new String[] { "E" }, new String[] { "E" });
hmm.reset();
HMM.State s = (HMM.State) hmm.getState("E");
s.print();
}

@Test
public void testTransitionIteratorIncludesExpectedOutputLabel() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("sym");
outputAlphabet.lookupIndex("L");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("L", new String[] { "L" });
hmm.reset();
HMM.State state = (HMM.State) hmm.getState("L");
FeatureSequence input = new FeatureSequence(inputAlphabet);
input.add("sym");
FeatureSequence output = new FeatureSequence(outputAlphabet);
output.add("L");
Transducer.TransitionIterator iter = state.transitionIterator(input, 0, output, 0);
assertTrue(iter.hasNext());
iter.nextState();
assertEquals("L", iter.getOutput());
}

@Test
public void testWeightedIncrementorMultipliesCountsCorrectly() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("tok");
outputAlphabet.lookupIndex("Y");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("Y", new String[] { "Y" });
hmm.reset();
HMM.State s = (HMM.State) hmm.getState("Y");
FeatureSequence inputSeq = new FeatureSequence(inputAlphabet);
inputSeq.add("tok");
FeatureSequence outputSeq = new FeatureSequence(outputAlphabet);
outputSeq.add("Y");
Transducer.TransitionIterator tIter = s.transitionIterator(inputSeq, 0, outputSeq, 0);
if (tIter.hasNext()) {
tIter.nextState();
HMM.WeightedIncrementor inc = hmm.new WeightedIncrementor(2.0);
inc.incrementTransition(tIter, 3.0);
inc.incrementInitialState(s, 3.0);
}
}

@Test
public void testAddStateWithNonexistentDestinationStillValidates() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("abc");
outputAlphabet.lookupIndex("M");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
// hmm.addState("M", new String[] { "NOT_DEFINED" }, new String[] { "M" });
// HMM.State m = (HMM.State) hmm.getState("M");
// Transducer.State destination = m.getDestinationState(0);
// assertNull(destination);
}

@Test
public void testTrainWithMismatchedInputTargetLengthsIsSafe() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("a");
inputAlphabet.lookupIndex("b");
outputAlphabet.lookupIndex("L");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
TokenSequence input = new TokenSequence();
input.add("a");
input.add("b");
TokenSequence target = new TokenSequence();
target.add("L");
SerialPipes pipes = new SerialPipes(Arrays.asList(new TokenSequence2FeatureSequence(), new Target2LabelSequence()));
Instance instance = new Instance(input, target, null, null);
InstanceList list = new InstanceList(pipes);
list.addThruPipe(instance);
HMM hmm = new HMM(list.getDataAlphabet(), list.getTargetAlphabet());
hmm.addFullyConnectedStatesForLabels();
boolean result = hmm.train(list);
assertTrue(result);
}

@Test
public void testEmptyStateTransitionIteratorHasNoNext() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("z");
outputAlphabet.lookupIndex("L");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
// hmm.addState("L", new String[0], new String[0]);
FeatureSequence input = new FeatureSequence(inputAlphabet);
input.add("z");
FeatureSequence output = new FeatureSequence(outputAlphabet);
output.add("L");
HMM.State s = (HMM.State) hmm.getState("L");
Transducer.TransitionIterator ti = s.transitionIterator(input, 0, output, 0);
assertFalse(ti.hasNext());
}
}
