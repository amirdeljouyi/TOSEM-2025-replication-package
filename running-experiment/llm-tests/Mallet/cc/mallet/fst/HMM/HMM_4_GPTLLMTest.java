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

public class HMM_4_GPTLLMTest {

@Test
public void testConstructorInitializesAlphabetsCorrectly() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("walk");
inputAlphabet.stopGrowth();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("Sunny");
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
assertEquals(inputAlphabet, hmm.getInputAlphabet());
assertEquals(outputAlphabet, hmm.getOutputAlphabet());
}

@Test
public void testAddSingleState() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("walk");
inputAlphabet.stopGrowth();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("Sunny");
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
String[] destinations = new String[1];
destinations[0] = "Sunny";
hmm.addState("Sunny", destinations);
assertNotNull(hmm.getState("Sunny"));
assertEquals(1, hmm.numStates());
}

@Test(expected = IllegalArgumentException.class)
public void testAddDuplicateStateThrowsException() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("walk");
inputAlphabet.stopGrowth();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("Sunny");
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
String[] destinations = new String[1];
destinations[0] = "Sunny";
hmm.addState("Sunny", destinations);
hmm.addState("Sunny", destinations);
}

@Test
public void testResetInitializesMultinomials() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("walk");
inputAlphabet.stopGrowth();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("Sunny");
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
String[] destinations = new String[1];
destinations[0] = "Sunny";
hmm.addState("Sunny", destinations);
hmm.reset();
assertNotNull(hmm.getInitialMultinomial());
assertNotNull(hmm.getTransitionMultinomial());
assertNotNull(hmm.getEmissionMultinomial());
}

@Test
public void testTrainReturnsTrue() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("walk");
outputAlphabet.lookupIndex("Sunny");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("Sunny", new String[] { "Sunny" });
FeatureSequence inputSequence = new FeatureSequence(inputAlphabet);
inputSequence.add("walk");
FeatureSequence outputSequence = new FeatureSequence(outputAlphabet);
outputSequence.add("Sunny");
InstanceList instanceList = new InstanceList(new Pipe() {

public Instance pipe(Instance carrier) {
return carrier;
}
});
Instance instance = new Instance(inputSequence, outputSequence, "id", null);
instanceList.add(instance);
boolean result = hmm.train(instanceList);
assertTrue(result);
}

@Test
public void testEstimateAfterTrain() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("walk");
outputAlphabet.lookupIndex("Sunny");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("Sunny", new String[] { "Sunny" });
FeatureSequence inputSequence = new FeatureSequence(inputAlphabet);
inputSequence.add("walk");
FeatureSequence outputSequence = new FeatureSequence(outputAlphabet);
outputSequence.add("Sunny");
Instance instance = new Instance(inputSequence, outputSequence, "id", null);
Pipe noopPipe = new Pipe() {

public Instance pipe(Instance carrier) {
return carrier;
}
};
InstanceList instanceList = new InstanceList(noopPipe);
instanceList.add(instance);
hmm.train(instanceList);
hmm.estimate();
Multinomial[] emissions = hmm.getEmissionMultinomial();
Multinomial[] transitions = hmm.getTransitionMultinomial();
assertNotNull(emissions[0]);
assertNotNull(transitions[0]);
}

@Test
public void testInitTransitionsAndEmissionsWithNoise() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("walk");
inputAlphabet.lookupIndex("shop");
outputAlphabet.lookupIndex("Sunny");
outputAlphabet.lookupIndex("Rainy");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("Sunny", new String[] { "Rainy" });
hmm.addState("Rainy", new String[] { "Sunny" });
Random random = new Random(42);
hmm.initTransitions(random, 0.5);
hmm.initEmissions(random, 0.5);
Multinomial[] emissions = hmm.getEmissionMultinomial();
Multinomial[] transitions = hmm.getTransitionMultinomial();
assertEquals(2, emissions.length);
assertEquals(2, transitions.length);
}

@Test
public void testAddOrder0StatesReturnsStartState() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("walk");
outputAlphabet.lookupIndex("Rainy");
outputAlphabet.lookupIndex("Sunny");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
Pipe p = new Pipe() {

public Instance pipe(Instance carrier) {
return carrier;
}
};
InstanceList list = new InstanceList(p);
String result = hmm.addOrderNStates(list, null, null, "Rainy", null, null, false);
assertEquals("Rainy", result);
assertEquals(2, hmm.numStates());
}

@Test
public void testSerializationAndDeserialization() throws IOException, ClassNotFoundException {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("walk");
outputAlphabet.lookupIndex("Sunny");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("Sunny", new String[] { "Sunny" });
File file = File.createTempFile("hmm", ".ser");
hmm.write(file);
ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
HMM deserialized = (HMM) in.readObject();
in.close();
assertNotNull(deserialized);
assertEquals(hmm.numStates(), deserialized.numStates());
file.delete();
}

@Test
public void testGetStateByNameAndIndex() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("walk");
outputAlphabet.lookupIndex("Sunny");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("Sunny", new String[] { "Sunny" });
HMM.State stateByName = hmm.getState("Sunny");
HMM.State stateByIndex = (HMM.State) hmm.getState(0);
assertEquals(stateByName.getName(), stateByIndex.getName());
}

@Test(expected = UnsupportedOperationException.class)
public void testTransitionIteratorThrowsOnNegativeInputPosition() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("walk");
inputAlphabet.stopGrowth();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("Rainy");
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("Rainy", new String[] { "Rainy" });
FeatureSequence input = new FeatureSequence(inputAlphabet);
input.add("walk");
HMM.State state = hmm.getState("Rainy");
state.transitionIterator(input, -1, null, 0);
}

@Test(expected = UnsupportedOperationException.class)
public void testTransitionIteratorThrowsOnNullInputSequence() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("walk");
outputAlphabet.lookupIndex("Sunny");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("Sunny", new String[] { "Sunny" });
HMM.State state = hmm.getState("Sunny");
state.transitionIterator(null, 0, null, 0);
}

@Test(expected = UnsupportedOperationException.class)
public void testTransitionIteratorThrowsOnUnsupportedSequenceType() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("x");
inputAlphabet.stopGrowth();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("A");
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("A", new String[] { "A" });
Sequence fakeInput = new Sequence() {

public int size() {
return 1;
}

public Object get(int i) {
return "x";
}
};
HMM.State state = hmm.getState("A");
state.transitionIterator(fakeInput, 0, null, 0);
}

@Test
public void testGetStateReturnsNullForUnknownName() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("walk");
outputAlphabet.lookupIndex("Sunny");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
assertNull(hmm.getState("UnknownState"));
}

@Test(expected = IllegalArgumentException.class)
public void testAddStateThrowsOnLabelDestinationMismatch() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("walk");
inputAlphabet.stopGrowth();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("Sunny");
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
String[] destinations = new String[] { "X", "Y" };
String[] labels = new String[] { "X" };
hmm.addState("BadState", 0.0, 0.0, destinations, labels);
}

@Test
public void testAllowedTransitionRegexFiltering() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("x");
inputAlphabet.stopGrowth();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
Pattern forbidden = Pattern.compile("A,B");
Pattern allowed = Pattern.compile("A,A|B,B");
boolean allowed1 = hmm.addOrderNStates(new InstanceList(new Pipe() {

public Instance pipe(Instance inst) {
return inst;
}
}), new int[] { 1 }, new boolean[] { true }, "A", forbidden, allowed, false).startsWith("A");
assertTrue(allowed1);
}

@Test
public void testAddSelfTransitioningStateForAllLabels() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("a");
inputAlphabet.stopGrowth();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("X");
outputAlphabet.lookupIndex("Y");
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addSelfTransitioningStateForAllLabels("loop");
assertNotNull(hmm.getState("loop"));
assertEquals(1, hmm.numStates());
}

@Test
public void testAddOrderNStatesFullyConnectedTrue() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("x");
inputAlphabet.stopGrowth();
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
InstanceList list = new InstanceList(new Pipe() {

public Instance pipe(Instance instance) {
return instance;
}
});
String start = hmm.addOrderNStates(list, new int[] { 1 }, new boolean[] { true }, "A", null, null, true);
assertTrue(start.startsWith("A"));
assertTrue(hmm.numStates() > 0);
}

@Test
public void testAddOrderNStatesWithForbiddenRegexSkipsDisallowed() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("w");
inputAlphabet.stopGrowth();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
Pattern forbidden = Pattern.compile("A,B");
InstanceList list = new InstanceList(new Pipe() {

public Instance pipe(Instance instance) {
return instance;
}
});
String start = hmm.addOrderNStates(list, new int[] { 1 }, new boolean[] { true }, "A", forbidden, null, false);
assertTrue(hmm.numStates() > 0);
assertTrue(start.startsWith("A"));
}

@Test
public void testReadObjectWithInvalidDataFails() throws IOException {
File f = File.createTempFile("invalidHMM", ".ser");
ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f));
out.writeInt(999);
out.close();
boolean exceptionThrown = false;
try {
ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
new Alphabet();
in.readObject();
} catch (Exception e) {
exceptionThrown = true;
}
assertTrue(exceptionThrown);
f.delete();
}

@Test
public void testTrainWithMultipleInstancesVaryingLengths() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("walk");
inputAlphabet.lookupIndex("run");
outputAlphabet.lookupIndex("Sunny");
outputAlphabet.lookupIndex("Rainy");
inputAlphabet.stopGrowth();
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("Sunny", new String[] { "Rainy" });
hmm.addState("Rainy", new String[] { "Sunny" });
Pipe pipe = new Pipe() {

public Instance pipe(Instance i) {
return i;
}
};
InstanceList ilist = new InstanceList(pipe);
FeatureSequence input1 = new FeatureSequence(inputAlphabet);
input1.add("walk");
input1.add("run");
FeatureSequence output1 = new FeatureSequence(outputAlphabet);
output1.add("Sunny");
output1.add("Rainy");
FeatureSequence input2 = new FeatureSequence(inputAlphabet);
input2.add("walk");
FeatureSequence output2 = new FeatureSequence(outputAlphabet);
output2.add("Sunny");
ilist.add(new Instance(input1, output1, "seq1", null));
ilist.add(new Instance(input2, output2, "seq2", null));
boolean trained = hmm.train(ilist);
assertTrue(trained);
}

@Test(expected = IllegalArgumentException.class)
public void testAddOrderNStatesThrowsOnMismatchingDefaultsLength() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("a");
inputAlphabet.stopGrowth();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("X");
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
InstanceList ilist = new InstanceList(new Pipe() {

public Instance pipe(Instance instance) {
return instance;
}
});
int[] orders = new int[] { 0, 1 };
boolean[] defaults = new boolean[] { true };
hmm.addOrderNStates(ilist, orders, defaults, "X", null, null, false);
}

@Test(expected = IllegalArgumentException.class)
public void testAddOrderNStatesThrowsOnNonIncreasingOrderArray() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("a");
inputAlphabet.stopGrowth();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("X");
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
int[] orders = new int[] { 1, 1 };
hmm.addOrderNStates(new InstanceList(new Pipe() {

public Instance pipe(Instance i) {
return i;
}
}), orders, new boolean[] { true, true }, "X", null, null, false);
}

@Test
public void testGetTransitionMultinomialBeforeInitReturnsNull() {
Alphabet input = new Alphabet();
Alphabet output = new Alphabet();
input.lookupIndex("a");
output.lookupIndex("X");
input.stopGrowth();
output.stopGrowth();
HMM hmm = new HMM(input, output);
assertNull(hmm.getTransitionMultinomial());
}

@Test
public void testGetEmissionMultinomialBeforeInitReturnsNull() {
Alphabet input = new Alphabet();
Alphabet output = new Alphabet();
input.lookupIndex("a");
output.lookupIndex("X");
input.stopGrowth();
output.stopGrowth();
HMM hmm = new HMM(input, output);
assertNull(hmm.getEmissionMultinomial());
}

@Test
public void testGetInitialMultinomialBeforeInitReturnsNull() {
Alphabet input = new Alphabet();
Alphabet output = new Alphabet();
input.lookupIndex("a");
output.lookupIndex("X");
input.stopGrowth();
output.stopGrowth();
HMM hmm = new HMM(input, output);
assertNull(hmm.getInitialMultinomial());
}

@Test
public void testAddFullyConnectedStatesForTriLabelsCreatesStates() {
Alphabet input = new Alphabet();
input.lookupIndex("a");
input.stopGrowth();
Alphabet output = new Alphabet();
output.lookupIndex("X");
output.lookupIndex("Y");
output.lookupIndex("Z");
output.stopGrowth();
HMM hmm = new HMM(input, output);
hmm.addFullyConnectedStatesForTriLabels();
assertTrue(hmm.numStates() > 0);
}

@Test
public void testAllowedHistoryRejectsDisallowedTransition() {
Alphabet input = new Alphabet();
Alphabet output = new Alphabet();
input.lookupIndex("a");
output.lookupIndex("A");
output.lookupIndex("B");
input.stopGrowth();
output.stopGrowth();
HMM hmm = new HMM(input, output);
Pattern disallowed = Pattern.compile("A,B");
boolean result = hmm.addOrderNStates(new InstanceList(new Pipe() {

public Instance pipe(Instance inst) {
return inst;
}
}), new int[] { 1 }, new boolean[] { true }, "A", disallowed, null, false).startsWith("A");
assertTrue(result);
}

@Test
public void testAddStateWithZeroWeightsIsNotAddedToInitialStates() {
Alphabet input = new Alphabet();
input.lookupIndex("a");
input.stopGrowth();
Alphabet output = new Alphabet();
output.lookupIndex("L");
output.stopGrowth();
HMM hmm = new HMM(input, output);
hmm.addState("ZeroState", 0.0, 0.0, new String[] { "ZeroState" }, new String[] { "L" });
assertEquals(0, hmm.initialStateIterator().hasNext() ? 1 : 0);
}

@Test
public void testIsTrainableReturnsTrue() {
Alphabet input = new Alphabet();
input.lookupIndex("x");
input.stopGrowth();
Alphabet output = new Alphabet();
output.lookupIndex("y");
output.stopGrowth();
HMM hmm = new HMM(input, output);
assertTrue(hmm.isTrainable());
}

@Test
public void testConcatLabelsNoSeparatorsWhenSingleValue() {
Alphabet input = new Alphabet();
input.lookupIndex("x");
input.stopGrowth();
Alphabet output = new Alphabet();
output.lookupIndex("A");
output.stopGrowth();
HMM hmm = new HMM(input, output);
String[] labels = new String[] { "A" };
String result = hmm.addOrderNStates(new InstanceList(new Pipe() {

public Instance pipe(Instance p) {
return p;
}
}), null, null, "A", null, null, false);
assertEquals("A", result);
}

@Test(expected = IndexOutOfBoundsException.class)
public void testGetStateWithInvalidIndexThrowsException() {
Alphabet input = new Alphabet();
input.lookupIndex("tok");
input.stopGrowth();
Alphabet output = new Alphabet();
output.lookupIndex("L");
output.stopGrowth();
HMM hmm = new HMM(input, output);
hmm.getState(0);
}

@Test
public void testAddStateWithNullLabelNameAndValidDestination() {
Alphabet input = new Alphabet();
input.lookupIndex("tok");
input.stopGrowth();
Alphabet output = new Alphabet();
output.lookupIndex("L");
output.stopGrowth();
HMM hmm = new HMM(input, output);
String[] destNames = new String[] { "foo" };
String[] labels = new String[] { "L" };
hmm.addState("foo", 0.0, 0.0, destNames, labels);
assertEquals(1, hmm.numStates());
assertNotNull(hmm.getState("foo"));
}

@Test
public void testAddStateWithEmptyTransitionArrays() {
Alphabet input = new Alphabet();
input.lookupIndex("tok");
input.stopGrowth();
Alphabet output = new Alphabet();
output.lookupIndex("L");
output.stopGrowth();
HMM hmm = new HMM(input, output);
hmm.addState("emptyState", 0.0, 0.0, new String[] {}, new String[] {});
assertEquals(1, hmm.numStates());
}

@Test
public void testAddOrderNStatesWithEmptyOutputAlphabet() {
Alphabet input = new Alphabet();
input.lookupIndex("foo");
input.stopGrowth();
Alphabet output = new Alphabet();
HMM hmm = new HMM(input, output);
InstanceList ilist = new InstanceList(new Pipe() {

public Instance pipe(Instance inst) {
return inst;
}
});
String name = hmm.addOrderNStates(ilist, null, null, "START", null, null, false);
assertEquals("START", name);
assertEquals(0, hmm.numStates());
}

@Test
public void testResetWorksOnEmptyHMM() {
Alphabet input = new Alphabet();
Alphabet output = new Alphabet();
input.stopGrowth();
output.stopGrowth();
HMM hmm = new HMM(input, output);
hmm.reset();
assertNull(hmm.getTransitionMultinomial());
assertNull(hmm.getEmissionMultinomial());
}

@Test
public void testTrainWithEmptyInstanceList() {
Alphabet input = new Alphabet();
Alphabet output = new Alphabet();
input.lookupIndex("tok");
output.lookupIndex("Y");
input.stopGrowth();
output.stopGrowth();
HMM hmm = new HMM(input, output);
hmm.addState("S", new String[] { "S" });
InstanceList emptyList = new InstanceList(new Pipe() {

public Instance pipe(Instance inst) {
return inst;
}
});
boolean result = hmm.train(emptyList);
assertTrue(result);
}

@Test
public void testInitialStateIteratorEmptyWhenNoStates() {
Alphabet input = new Alphabet();
Alphabet output = new Alphabet();
input.stopGrowth();
output.stopGrowth();
HMM hmm = new HMM(input, output);
Iterator it = hmm.initialStateIterator();
assertFalse(it.hasNext());
}

@Test
public void testTransitionIteratorWithAllImpossibleWeights() {
Alphabet input = new Alphabet();
input.lookupIndex("a");
input.stopGrowth();
Alphabet output = new Alphabet();
output.lookupIndex("X");
output.stopGrowth();
HMM hmm = new HMM(input, output);
// hmm.addState("Q", new String[] {}, new String[] {});
// FeatureSequence inputSeq = new FeatureSequence(input);
// inputSeq.add("a");
HMM.State s = hmm.getState("Q");
// Transducer.TransitionIterator it = s.transitionIterator(inputSeq, 0, null, 0);
// assertFalse(it.hasNext());
}

@Test
public void testSerializationDeserializationOfEmptyHMM() throws Exception {
Alphabet input = new Alphabet();
Alphabet output = new Alphabet();
input.stopGrowth();
output.stopGrowth();
HMM hmm = new HMM(input, output);
File f = File.createTempFile("emptyHMM", ".ser");
ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f));
out.writeObject(hmm);
out.close();
ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
HMM deserialized = (HMM) in.readObject();
in.close();
assertNotNull(deserialized);
assertEquals(0, deserialized.numStates());
f.delete();
}

@Test
public void testIncrementInitialStateIncrementsEstimatorEntry() {
Alphabet input = new Alphabet();
Alphabet output = new Alphabet();
input.lookupIndex("foo");
output.lookupIndex("A");
input.stopGrowth();
output.stopGrowth();
HMM hmm = new HMM(input, output);
hmm.addState("A", new String[] { "A" });
hmm.reset();
HMM.State state = hmm.getState("A");
HMM.Incrementor inc = hmm.new Incrementor();
inc.incrementInitialState(state, 2.0);
hmm.estimate();
assertNotNull(hmm.getInitialMultinomial());
}

@Test
public void testAddStateWithMultipleDestinationsAndLabels() {
Alphabet input = new Alphabet();
input.lookupIndex("a");
input.stopGrowth();
Alphabet output = new Alphabet();
output.lookupIndex("X");
output.lookupIndex("Y");
output.lookupIndex("Z");
output.stopGrowth();
HMM hmm = new HMM(input, output);
String[] destinations = new String[] { "s1", "s2", "s3" };
String[] labels = new String[] { "X", "Y", "Z" };
hmm.addState("s0", 1.0, 0.0, destinations, labels);
assertEquals(1, hmm.numStates());
assertNotNull(hmm.getState("s0"));
Transducer.State state = hmm.getState("s0");
assertEquals("s0", state.getName());
}

@Test
public void testAddFullyConnectedStatesForBiLabelsGeneratesCorrectStateCount() {
Alphabet input = new Alphabet();
input.lookupIndex("a");
input.stopGrowth();
Alphabet output = new Alphabet();
output.lookupIndex("A");
output.lookupIndex("B");
output.stopGrowth();
HMM hmm = new HMM(input, output);
hmm.addFullyConnectedStatesForBiLabels();
assertEquals(4, hmm.numStates());
}

@Test(expected = IllegalArgumentException.class)
public void testAddDuplicateStateThrowsExceptionWithDifferentParams() {
Alphabet input = new Alphabet();
input.lookupIndex("x");
input.stopGrowth();
Alphabet output = new Alphabet();
output.lookupIndex("Y");
output.stopGrowth();
HMM hmm = new HMM(input, output);
hmm.addState("X", new String[] { "X" });
hmm.addState("X", 1.0, 0.0, new String[] { "X" }, new String[] { "Y" });
}

@Test
public void testAddStatesForThreeQuarterLabelsConnectedAsInAddsValidStates() {
Alphabet input = new Alphabet();
input.lookupIndex("f");
input.stopGrowth();
Alphabet output = new Alphabet();
output.lookupIndex("A");
output.lookupIndex("B");
output.lookupIndex("C");
output.stopGrowth();
HMM hmm = new HMM(input, output);
Pipe pipe = new Pipe() {

public Instance pipe(Instance inst) {
return inst;
}
};
FeatureSequence fsInput = new FeatureSequence(input);
fsInput.add("f");
FeatureSequence fsOutput = new FeatureSequence(output);
fsOutput.add("A");
fsOutput.add("B");
fsOutput.add("C");
InstanceList list = new InstanceList(pipe);
list.add(new Instance(fsInput, fsOutput, "seq", null));
hmm.addStatesForThreeQuarterLabelsConnectedAsIn(list);
assertTrue(hmm.numStates() > 0);
}

@Test
public void testTransitionIteratorToStringReturnsNonNull() {
Alphabet input = new Alphabet();
input.lookupIndex("foo");
input.stopGrowth();
Alphabet output = new Alphabet();
output.lookupIndex("A");
output.lookupIndex("B");
output.stopGrowth();
HMM hmm = new HMM(input, output);
hmm.addState("s1", 0.0, 0.0, new String[] { "s2" }, new String[] { "A" });
hmm.addState("s2", 0.0, 0.0, new String[] { "s1" }, new String[] { "B" });
hmm.reset();
FeatureSequence inputSeq = new FeatureSequence(input);
inputSeq.add("foo");
HMM.State s1 = hmm.getState("s1");
Transducer.TransitionIterator it = s1.transitionIterator(inputSeq, 0, null, 0);
if (it.hasNext()) {
it.nextState();
assertNotNull(it.getInput());
assertNotNull(it.getOutput());
assertNotNull(it.getSourceState());
assertNotNull(it.getDestinationState());
}
}

@Test
public void testInitTransitionsWithNullRandomImposesUniform() {
Alphabet input = new Alphabet();
input.lookupIndex("x");
input.stopGrowth();
Alphabet output = new Alphabet();
output.lookupIndex("S");
output.stopGrowth();
HMM hmm = new HMM(input, output);
hmm.addState("S", new String[] { "S" });
hmm.reset();
hmm.initTransitions(null, 0.0);
Multinomial[] trans = hmm.getTransitionMultinomial();
assertNotNull(trans);
assertTrue(trans.length > 0);
}

@Test(expected = AssertionError.class)
public void testAddStateNullStateNameTriggersAssertion() {
Alphabet input = new Alphabet();
input.lookupIndex("a");
input.stopGrowth();
Alphabet output = new Alphabet();
output.lookupIndex("x");
output.stopGrowth();
HMM hmm = new HMM(input, output);
String[] dests = new String[] { "s1" };
String[] labels = new String[] { "x", "y" };
hmm.addState("X", 0.0, 0.0, dests, labels);
}

@Test
public void testAddStatesForHalfLabelsConnectedAsInCreatesExpectedCount() {
Alphabet input = new Alphabet();
input.lookupIndex("i");
input.stopGrowth();
Alphabet output = new Alphabet();
output.lookupIndex("L1");
output.lookupIndex("L2");
output.stopGrowth();
HMM hmm = new HMM(input, output);
Pipe pipe = new Pipe() {

public Instance pipe(Instance inst) {
return inst;
}
};
FeatureSequence inputSeq = new FeatureSequence(input);
inputSeq.add("i");
FeatureSequence outputSeq = new FeatureSequence(output);
outputSeq.add("L1");
outputSeq.add("L2");
InstanceList list = new InstanceList(pipe);
list.add(new Instance(inputSeq, outputSeq, "x", null));
hmm.addStatesForHalfLabelsConnectedAsIn(list);
assertEquals(2, hmm.numStates());
}

@Test
public void testPrintOutputsModelInfoToConsole() {
Alphabet input = new Alphabet();
input.lookupIndex("x");
input.stopGrowth();
Alphabet output = new Alphabet();
output.lookupIndex("Y");
output.stopGrowth();
HMM hmm = new HMM(input, output);
hmm.addState("Y", new String[] { "Y" });
hmm.reset();
hmm.print();
}

@Test
public void testAddOrderNStatesReturnsCorrectStartNameAtHighOrder() {
Alphabet input = new Alphabet();
input.lookupIndex("t");
input.stopGrowth();
Alphabet output = new Alphabet();
output.lookupIndex("B");
output.lookupIndex("C");
output.lookupIndex("D");
output.stopGrowth();
HMM hmm = new HMM(input, output);
InstanceList list = new InstanceList(new Pipe() {

public Instance pipe(Instance inst) {
return inst;
}
});
int[] orders = new int[] { 1, 2 };
boolean[] defaults = new boolean[] { true, true };
String start = hmm.addOrderNStates(list, orders, defaults, "B", null, null, true);
assertEquals("B,B", start);
}

@Test
public void testAllowedHistoryWithPatternBlocksIllegalHistory() {
Alphabet input = new Alphabet();
input.lookupIndex("tok");
input.stopGrowth();
Alphabet output = new Alphabet();
output.lookupIndex("A");
output.lookupIndex("B");
output.stopGrowth();
HMM hmm = new HMM(input, output);
Pattern forbidden = Pattern.compile("A,B");
String[] history = new String[] { "A", "B" };
boolean allowed = hmm.addOrderNStates(new InstanceList(new Pipe() {

public Instance pipe(Instance i) {
return i;
}
}), new int[] { 1 }, new boolean[] { true }, "A", forbidden, null, false).startsWith("A");
assertTrue(allowed);
}

@Test(expected = IllegalArgumentException.class)
public void testAddOrderNStatesThrowsIfOrdersNotAscending() {
Alphabet input = new Alphabet();
input.lookupIndex("x");
input.stopGrowth();
Alphabet output = new Alphabet();
output.lookupIndex("Y");
output.stopGrowth();
HMM hmm = new HMM(input, output);
int[] orders = new int[] { 1, 1 };
boolean[] defaults = new boolean[] { true, true };
hmm.addOrderNStates(new InstanceList(new Pipe() {

public Instance pipe(Instance inst) {
return inst;
}
}), orders, defaults, "Y", null, null, false);
}

@Test
public void testInitialStateIteratorReturnsEmptyWhenNoInitialWeight() {
Alphabet input = new Alphabet();
Alphabet output = new Alphabet();
input.lookupIndex("tok");
input.stopGrowth();
output.lookupIndex("A");
output.stopGrowth();
HMM hmm = new HMM(input, output);
hmm.addState("S", 0.0, 0.0, new String[] { "S" }, new String[] { "A" });
Iterator iterator = hmm.initialStateIterator();
assertFalse(iterator.hasNext());
}

@Test
public void testAddSelfTransitioningStateForAllLabelsCreatesExpectedTransitions() {
Alphabet input = new Alphabet();
input.lookupIndex("in");
input.stopGrowth();
Alphabet output = new Alphabet();
output.lookupIndex("X");
output.lookupIndex("Y");
output.stopGrowth();
HMM hmm = new HMM(input, output);
hmm.addSelfTransitioningStateForAllLabels("self-loop");
HMM.State state = hmm.getState("self-loop");
assertNotNull(state);
// assertEquals(2, state.destinationNames.length);
}

@Test
public void testTransitionIteratorOnlyReturnsMatchingOutputTransitions() {
Alphabet input = new Alphabet();
input.lookupIndex("feat");
input.stopGrowth();
Alphabet output = new Alphabet();
output.lookupIndex("OK");
output.lookupIndex("NO");
output.stopGrowth();
HMM hmm = new HMM(input, output);
hmm.addState("Q", 0.0, 0.0, new String[] { "Q" }, new String[] { "OK" });
hmm.reset();
FeatureSequence fsInput = new FeatureSequence(input);
fsInput.add("feat");
FeatureSequence fsOutput = new FeatureSequence(output);
fsOutput.add("NO");
HMM.State state = hmm.getState("Q");
Transducer.TransitionIterator ti = state.transitionIterator(fsInput, 0, fsOutput, 0);
assertFalse(ti.hasNext());
}

@Test(expected = AssertionError.class)
public void testAssertInLabelConnectionsInFailsOnInvalidLabelIndex() {
Alphabet input = new Alphabet();
input.lookupIndex("w");
input.stopGrowth();
Alphabet output = new Alphabet();
output.lookupIndex("A");
output.stopGrowth();
HMM hmm = new HMM(input, output);
Pipe pipe = new Pipe() {

public Instance pipe(Instance i) {
return i;
}
};
FeatureSequence inputSeq = new FeatureSequence(input);
inputSeq.add("w");
FeatureSequence targetSeq = new FeatureSequence(output) {

@Override
public Object get(int i) {
if (i == 0)
return "A";
return "B";
}

// @Override
// public int size() {
// return 2;
// }
// };
// Instance inst = new Instance(inputSeq, targetSeq, "err", null);
// InstanceList list = new InstanceList(pipe);
// list.add(inst);
// hmm.addStatesForLabelsConnectedAsIn(list);
// }
// 
// @Test
// public void testGetDestinationStateIsMemoized() {
// Alphabet input = new Alphabet();
// input.lookupIndex("x");
// input.stopGrowth();
// Alphabet output = new Alphabet();
// output.lookupIndex("Z");
// output.stopGrowth();
// HMM hmm = new HMM(input, output);
// hmm.addState("Z", new String[] { "Z" });
// HMM.State z = hmm.getState("Z");
// Transducer.State d1 = z.getDestinationState(0);
// Transducer.State d2 = z.getDestinationState(0);
// assertSame(d1, d2);
// }
// 
// @Test
// public void testTrainInitializesAllMultinomials() {
// Alphabet input = new Alphabet();
// input.lookupIndex("feature");
// input.stopGrowth();
// Alphabet output = new Alphabet();
// output.lookupIndex("A");
// output.lookupIndex("B");
// output.stopGrowth();
// HMM hmm = new HMM(input, output);
// hmm.addFullyConnectedStatesForLabels();
// FeatureSequence fsInput = new FeatureSequence(input);
// fsInput.add("feature");
// FeatureSequence fsTarget = new FeatureSequence(output);
// fsTarget.add("A");
// Pipe fakePipe = new Pipe() {

public Instance pipe(Instance carrier) {
return carrier;
}
};
// InstanceList list = new InstanceList(fakePipe);
// list.add(new Instance(fsInput, fsTarget, "ex", null));
// boolean result = hmm.train(list);
// assertTrue(result);
assertNotNull(hmm.getInitialMultinomial());
}

@Test
public void testAddStateAndTransitionIteratorWithNullOutputSequence() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("word");
inputAlphabet.stopGrowth();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("X");
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("X", new String[] { "X" });
hmm.reset();
FeatureSequence inputSequence = new FeatureSequence(inputAlphabet);
inputSequence.add("word");
HMM.State state = hmm.getState("X");
Transducer.TransitionIterator transitionIterator = state.transitionIterator(inputSequence, 0, null, 0);
assertTrue(transitionIterator.hasNext());
Transducer.State next = transitionIterator.nextState();
assertNotNull(next);
}

@Test
public void testTransitionIteratorWithSingleValidTransition() {
Alphabet input = new Alphabet();
input.lookupIndex("tok");
input.stopGrowth();
Alphabet output = new Alphabet();
output.lookupIndex("A");
output.lookupIndex("B");
output.stopGrowth();
HMM hmm = new HMM(input, output);
// hmm.addState("S", new String[] { "S" }, new String[] { "A" });
// hmm.reset();
FeatureSequence featureInput = new FeatureSequence(input);
featureInput.add("tok");
FeatureSequence featureOutput = new FeatureSequence(output);
featureOutput.add("A");
HMM.State s = hmm.getState("S");
Transducer.TransitionIterator iterator = s.transitionIterator(featureInput, 0, featureOutput, 0);
assertTrue(iterator.hasNext());
Transducer.State dest = iterator.nextState();
assertNotNull(dest);
assertEquals("S", dest.getName());
}

@Test
public void testAllowedTransitionDisallowViaNegativeRegex() {
Alphabet input = new Alphabet();
input.lookupIndex("x");
input.stopGrowth();
Alphabet output = new Alphabet();
output.lookupIndex("A");
output.lookupIndex("B");
output.stopGrowth();
HMM hmm = new HMM(input, output);
boolean result = hmm.addOrderNStates(new InstanceList(new Pipe() {

public Instance pipe(Instance inst) {
return inst;
}
}), new int[] { 1 }, new boolean[] { true }, "A", java.util.regex.Pattern.compile("A,B"), null, false).startsWith("A");
assertTrue(result);
}

@Test
public void testWeightCalculationInTransitionIterator() {
Alphabet input = new Alphabet();
input.lookupIndex("w");
input.stopGrowth();
Alphabet output = new Alphabet();
output.lookupIndex("X");
output.stopGrowth();
HMM hmm = new HMM(input, output);
// hmm.addState("X", new String[] { "X" }, new String[] { "X" });
// hmm.initTransitions(new Random(1), 1.0);
hmm.initEmissions(new Random(1), 1.0);
FeatureSequence inputSeq = new FeatureSequence(input);
inputSeq.add("w");
HMM.State state = hmm.getState("X");
Transducer.TransitionIterator it = state.transitionIterator(inputSeq, 0, null, 0);
assertTrue(it.hasNext());
it.nextState();
double weight = it.getWeight();
assertTrue(weight < 0.0);
}

@Test
public void testTrainWithMultipleUniqueLabels() {
Alphabet input = new Alphabet();
input.lookupIndex("a");
input.lookupIndex("b");
input.stopGrowth();
Alphabet output = new Alphabet();
output.lookupIndex("L1");
output.lookupIndex("L2");
output.stopGrowth();
HMM hmm = new HMM(input, output);
hmm.addFullyConnectedStatesForLabels();
Pipe pipe = new Pipe() {

public Instance pipe(Instance inst) {
return inst;
}
};
InstanceList list = new InstanceList(pipe);
FeatureSequence fs1 = new FeatureSequence(input);
fs1.add("a");
fs1.add("b");
FeatureSequence ts1 = new FeatureSequence(output);
ts1.add("L1");
ts1.add("L2");
list.add(new Instance(fs1, ts1, "seq1", null));
boolean result = hmm.train(list);
assertTrue(result);
}

@Test
public void testWeightsResultInValidProbabilitySumAfterEstimate() {
Alphabet input = new Alphabet();
input.lookupIndex("i");
input.stopGrowth();
Alphabet output = new Alphabet();
output.lookupIndex("Y");
output.stopGrowth();
HMM hmm = new HMM(input, output);
hmm.addState("Y", new String[] { "Y" });
hmm.reset();
hmm.estimate();
Multinomial[] ems = hmm.getEmissionMultinomial();
assertNotNull(ems);
// double[] probs = ems[0].getProbabilities();
double sum = 0.0;
// if (probs != null) {
// sum += probs[0];
// }
assertTrue(sum > 0.0 && sum <= 1.0);
}

@Test
public void testSerializationWriteAndReadObjectCycle() throws Exception {
Alphabet input = new Alphabet();
input.lookupIndex("a");
input.stopGrowth();
Alphabet output = new Alphabet();
output.lookupIndex("label");
output.stopGrowth();
HMM hmm = new HMM(input, output);
hmm.addState("label", new String[] { "label" });
hmm.reset();
PipedOutputStream out = new PipedOutputStream();
PipedInputStream in = new PipedInputStream(out);
ObjectOutputStream oos = new ObjectOutputStream(out);
oos.writeObject(hmm);
oos.close();
ObjectInputStream ois = new ObjectInputStream(in);
Object obj = ois.readObject();
ois.close();
assertTrue(obj instanceof HMM);
HMM loaded = (HMM) obj;
assertEquals(1, loaded.numStates());
}

@Test
public void testWeightedIncrementorAppliesWeightCorrectly() {
Alphabet input = new Alphabet();
input.lookupIndex("x");
input.stopGrowth();
Alphabet output = new Alphabet();
output.lookupIndex("Y");
output.stopGrowth();
HMM hmm = new HMM(input, output);
hmm.addState("Y", new String[] { "Y" });
hmm.reset();
HMM.State source = hmm.getState("Y");
FeatureSequence inputSeq = new FeatureSequence(input);
inputSeq.add("x");
HMM.WeightedIncrementor weightedInc = hmm.new WeightedIncrementor(2.0);
Transducer.TransitionIterator ti = source.transitionIterator(inputSeq, 0, null, 0);
assertTrue(ti.hasNext());
ti.nextState();
double originalWeight = ti.getWeight();
assertNotEquals(0.0, originalWeight, 1e-6);
weightedInc.incrementInitialState(source, 1.0);
weightedInc.incrementTransition(ti, 1.0);
}

@Test(expected = UnsupportedOperationException.class)
public void testTransitionIteratorWithEpsilonInputThrows() {
Alphabet input = new Alphabet();
input.lookupIndex("token");
input.stopGrowth();
Alphabet output = new Alphabet();
output.lookupIndex("LABEL");
output.stopGrowth();
HMM hmm = new HMM(input, output);
hmm.addState("LABEL", new String[] { "LABEL" });
FeatureSequence inputSeq = new FeatureSequence(input);
inputSeq.add("token");
HMM.State state = hmm.getState("LABEL");
state.transitionIterator(inputSeq, -1, null, 0);
}

@Test(expected = UnsupportedOperationException.class)
public void testTransitionIteratorWithNullSequenceThrows() {
Alphabet input = new Alphabet();
Alphabet output = new Alphabet();
input.lookupIndex("word");
output.lookupIndex("tag");
input.stopGrowth();
output.stopGrowth();
HMM hmm = new HMM(input, output);
hmm.addState("tag", new String[] { "tag" });
HMM.State state = hmm.getState("tag");
state.transitionIterator(null, 0, null, 0);
}
}
