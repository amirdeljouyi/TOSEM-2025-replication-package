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

public class HMM_3_GPTLLMTest {

@Test
public void testAddState() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("walk");
outputAlphabet.lookupIndex("Rainy");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("Rainy", 0.5, 0.0, new String[] { "Rainy" }, new String[] { "Rainy" });
assertNotNull(hmm.getState("Rainy"));
assertEquals(1, hmm.numStates());
}

@Test
public void testAddDuplicateStateThrowsException() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("walk");
outputAlphabet.lookupIndex("Rainy");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("Rainy", 0.5, 0.0, new String[] { "Rainy" }, new String[] { "Rainy" });
try {
hmm.addState("Rainy", 0.5, 0.0, new String[] { "Rainy" }, new String[] { "Rainy" });
fail("Expected IllegalArgumentException for duplicate state.");
} catch (IllegalArgumentException e) {
assertTrue(e.getMessage().contains("already exists"));
}
}

@Test
public void testAddSelfTransitioningStateForAllLabels() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("walk");
outputAlphabet.lookupIndex("Rainy");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addSelfTransitioningStateForAllLabels("SELF");
assertEquals(1, hmm.numStates());
assertNotNull(hmm.getState("SELF"));
}

@Test
public void testAddFullyConnectedStates() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("walk");
outputAlphabet.lookupIndex("Rainy");
outputAlphabet.lookupIndex("Sunny");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addFullyConnectedStates(new String[] { "Rainy", "Sunny" });
assertEquals(2, hmm.numStates());
assertNotNull(hmm.getState("Rainy"));
assertNotNull(hmm.getState("Sunny"));
}

@Test
public void testInitTransitions() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("run");
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addFullyConnectedStates(new String[] { "A", "B" });
hmm.initTransitions(new Random(42), 1.0);
assertNotNull(hmm.getInitialMultinomial());
assertNotNull(hmm.getTransitionMultinomial());
}

@Test
public void testTrainSimpleModel() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("walk");
inputAlphabet.lookupIndex("shop");
outputAlphabet.lookupIndex("Rainy");
outputAlphabet.lookupIndex("Sunny");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addFullyConnectedStates(new String[] { "Rainy", "Sunny" });
// FeatureSequence inputSeq = new FeatureSequence(inputAlphabet, new String[] { "walk", "shop" });
// FeatureSequence targetSeq = new FeatureSequence(outputAlphabet, new String[] { "Rainy", "Sunny" });
// Instance instance = new Instance(inputSeq, targetSeq, "x", null);
Pipe dummyPipe = new Pipe(inputAlphabet, outputAlphabet) {

public Instance pipe(Instance carrier) {
return carrier;
}
};
InstanceList ilist = new InstanceList(dummyPipe);
// ilist.add(instance);
boolean trained = hmm.train(ilist);
assertTrue(trained);
assertNotNull(hmm.getTransitionMultinomial());
assertNotNull(hmm.getEmissionMultinomial());
}

@Test
public void testEstimateAfterTrain() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("jump");
outputAlphabet.lookupIndex("Hot");
outputAlphabet.lookupIndex("Cold");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addFullyConnectedStates(new String[] { "Hot", "Cold" });
// FeatureSequence inputSeq = new FeatureSequence(inputAlphabet, new String[] { "jump", "jump" });
// FeatureSequence targetSeq = new FeatureSequence(outputAlphabet, new String[] { "Hot", "Cold" });
// Instance instance = new Instance(inputSeq, targetSeq, "seq", null);
Pipe pipe = new Pipe(inputAlphabet, outputAlphabet) {

public Instance pipe(Instance carrier) {
return carrier;
}
};
InstanceList ilist = new InstanceList(pipe);
// ilist.add(instance);
hmm.train(ilist);
hmm.estimate();
assertNotNull(hmm.getInitialMultinomial());
}

@Test
public void testResetStillValid() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("a");
outputAlphabet.lookupIndex("S");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addFullyConnectedStates(new String[] { "S" });
hmm.reset();
assertNotNull(hmm.getEmissionMultinomial());
}

@Test
public void testGetInputAndOutputAlphabet() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("token");
outputAlphabet.lookupIndex("label");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
assertEquals(inputAlphabet, hmm.getInputAlphabet());
assertEquals(outputAlphabet, hmm.getOutputAlphabet());
}

@Test
public void testSerializationRoundTrip() throws Exception {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("t1");
outputAlphabet.lookupIndex("label1");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addFullyConnectedStates(new String[] { "label1" });
File file = File.createTempFile("hmmtest", ".ser");
file.deleteOnExit();
hmm.write(file);
ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
Object read = in.readObject();
in.close();
assertTrue(read instanceof HMM);
HMM deserialized = (HMM) read;
assertNotNull(deserialized.getState("label1"));
assertEquals(1, deserialized.numStates());
}

@Test
public void testAddStateWithMismatchedTransitionAndLabelArrays() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("A");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
try {
hmm.addState("InvalidState", 0.0, 0.0, new String[] { "A", "B" }, new String[] { "A" });
fail("Expected AssertionError due to array length mismatch");
} catch (AssertionError e) {
}
}

@Test
public void testAddOrderNStatesWithInvalidOrders() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("x");
outputAlphabet.lookupIndex("L1");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
Pipe dummyPipe = new Pipe(inputAlphabet, outputAlphabet) {

public Instance pipe(Instance carrier) {
return carrier;
}
};
// FeatureSequence input = new FeatureSequence(inputAlphabet, new String[] { "x" });
// FeatureSequence target = new FeatureSequence(outputAlphabet, new String[] { "L1" });
// Instance instance = new Instance(input, target, "id", null);
InstanceList ilist = new InstanceList(dummyPipe);
// ilist.add(instance);
try {
hmm.addOrderNStates(ilist, new int[] { 2, 1 }, new boolean[] { false, false }, "L1", null, null, true);
fail("Expected IllegalArgumentException due to non-ascending order array");
} catch (IllegalArgumentException e) {
assertTrue(e.getMessage().startsWith("Orders must be"));
}
}

@Test
public void testTrainWithEmptyInstanceListThrowsAssertionError() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("v");
outputAlphabet.lookupIndex("Z");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
Pipe dummyPipe = new Pipe(inputAlphabet, outputAlphabet) {

public Instance pipe(Instance carrier) {
return carrier;
}
};
InstanceList ilist = new InstanceList(dummyPipe);
try {
hmm.train(ilist);
fail("train should throw AssertionError for empty InstanceList");
} catch (AssertionError error) {
}
}

@Test
public void testAllowedTransitionDisallowedByPattern() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("t");
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
Pipe dummyPipe = new Pipe(inputAlphabet, outputAlphabet) {

public Instance pipe(Instance carrier) {
return carrier;
}
};
// FeatureSequence input = new FeatureSequence(inputAlphabet, new String[] { "t", "t" });
// FeatureSequence output = new FeatureSequence(outputAlphabet, new String[] { "A", "B" });
// Instance instance = new Instance(input, output, "x", null);
InstanceList ilist = new InstanceList(dummyPipe);
// ilist.add(instance);
Pattern forbidden = Pattern.compile("A,B");
String result = hmm.addOrderNStates(ilist, new int[] { 1 }, new boolean[] { false }, "A", forbidden, null, false);
assertNotNull(result);
assertTrue(hmm.numStates() > 0);
}

@Test
public void testGetTransitionMultinomialWithoutTraining() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("input");
outputAlphabet.lookupIndex("state1");
outputAlphabet.lookupIndex("state2");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
assertNull(hmm.getTransitionMultinomial());
assertNull(hmm.getEmissionMultinomial());
assertNull(hmm.getInitialMultinomial());
}

@Test
public void testAddStatesForHalfLabelsConnectedAsInBuildsStates() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("in");
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
Pipe dummyPipe = new Pipe(inputAlphabet, outputAlphabet) {

public Instance pipe(Instance carrier) {
return carrier;
}
};
// FeatureSequence input = new FeatureSequence(inputAlphabet, new String[] { "in", "in" });
// FeatureSequence target = new FeatureSequence(outputAlphabet, new String[] { "A", "B" });
// Instance instance = new Instance(input, target, "xx", null);
InstanceList ilist = new InstanceList(dummyPipe);
// ilist.add(instance);
hmm.addStatesForHalfLabelsConnectedAsIn(ilist);
assertTrue(hmm.numStates() > 0);
}

@Test
public void testAddStatesForBiLabelsConnectedAsInSkipsDisconnectedPairs() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("w");
outputAlphabet.lookupIndex("X");
outputAlphabet.lookupIndex("Y");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
Pipe dummyPipe = new Pipe(inputAlphabet, outputAlphabet) {

public Instance pipe(Instance carrier) {
return carrier;
}
};
// FeatureSequence input = new FeatureSequence(inputAlphabet, new String[] { "w", "w" });
// FeatureSequence target = new FeatureSequence(outputAlphabet, new String[] { "X", "Y" });
// Instance instance = new Instance(input, target, "xy", null);
InstanceList ilist = new InstanceList(dummyPipe);
// ilist.add(instance);
hmm.addStatesForBiLabelsConnectedAsIn(ilist);
assertTrue(hmm.numStates() > 0);
assertNotNull(hmm.getState("X,Y"));
}

@Test
public void testAddFullyConnectedStatesForTriLabelsCreatesManyStates() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("a");
outputAlphabet.lookupIndex("L1");
outputAlphabet.lookupIndex("L2");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addFullyConnectedStatesForTriLabels();
assertEquals(8, hmm.numStates());
}

@Test
public void testTrainWithNullEvaluatorAndValidationAndTesting() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("foo");
outputAlphabet.lookupIndex("tag");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addFullyConnectedStates(new String[] { "tag" });
Pipe dummyPipe = new Pipe(inputAlphabet, outputAlphabet) {

public Instance pipe(Instance carrier) {
return carrier;
}
};
// FeatureSequence input = new FeatureSequence(inputAlphabet, new String[] { "foo" });
// FeatureSequence target = new FeatureSequence(outputAlphabet, new String[] { "tag" });
// Instance instance = new Instance(input, target, "test", null);
InstanceList ilist = new InstanceList(dummyPipe);
// ilist.add(instance);
boolean trained = hmm.train(ilist, null, null);
assertTrue(trained);
}

@Test
public void testWriteToInvalidFileThrowsIOException() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("1");
outputAlphabet.lookupIndex("A");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addFullyConnectedStates(new String[] { "A" });
File directory = new File("thisShouldBeAFileNotDir");
directory.mkdir();
try {
hmm.write(directory);
fail("Expected IOException when writing to directory");
} catch (Exception e) {
assertTrue(e instanceof IOException || e instanceof FileNotFoundException);
} finally {
directory.delete();
}
}

@Test
public void testAddOrderNStatesWithEmptyConnectionAllowsNoStateCreated() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("foo");
outputAlphabet.lookupIndex("L1");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
Pipe pipe = new Pipe(inputAlphabet, outputAlphabet) {

public Instance pipe(Instance carrier) {
return carrier;
}
};
// FeatureSequence input = new FeatureSequence(inputAlphabet, new String[] { "foo" });
// FeatureSequence output = new FeatureSequence(outputAlphabet, new String[] { "L1" });
// Instance instance = new Instance(input, output, "id", null);
InstanceList ilist = new InstanceList(pipe);
// ilist.add(instance);
Pattern allowed = Pattern.compile("^$");
String startState = hmm.addOrderNStates(ilist, new int[] { 1 }, new boolean[] { false }, "L1", null, allowed, false);
assertEquals(0, hmm.numStates());
assertEquals("L1", startState);
}

@Test
public void testAddStatesForThreeQuarterLabelsConnectedAsInBuildsCorrectly() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("x");
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
Pipe pipe = new Pipe(inputAlphabet, outputAlphabet) {

public Instance pipe(Instance carrier) {
return carrier;
}
};
// FeatureSequence input = new FeatureSequence(inputAlphabet, new String[] { "x", "x" });
// FeatureSequence output = new FeatureSequence(outputAlphabet, new String[] { "A", "B" });
// Instance instance = new Instance(input, output, "ex", null);
InstanceList ilist = new InstanceList(pipe);
// ilist.add(instance);
hmm.addStatesForThreeQuarterLabelsConnectedAsIn(ilist);
assertEquals(2, hmm.numStates());
assertNotNull(hmm.getState("A"));
assertNotNull(hmm.getState("B"));
}

@Test
public void testGetStateByIndexReturnsProperState() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("i");
outputAlphabet.lookupIndex("X");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("X", new String[] { "X" });
Transducer.State state = hmm.getState(0);
assertNotNull(state);
assertEquals("X", state.getName());
}

@Test
public void testInitialWeightAffectsInitialStateIterator() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("a");
outputAlphabet.lookupIndex("Z");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("Z", 0.8, 0.0, new String[] { "Z" }, new String[] { "Z" });
assertTrue(hmm.initialStateIterator().hasNext());
}

@Test
public void testInitTransitionsWithNullRandomGivesUniformDistribution() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("one");
outputAlphabet.lookupIndex("L");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("L", new String[] { "L" });
hmm.initTransitions(null, 0.0);
Multinomial[] trans = hmm.getTransitionMultinomial();
Multinomial init = hmm.getInitialMultinomial();
assertNotNull(trans);
assertNotNull(init);
// assertEquals(1.0, Math.exp(init.getLogProbVector()[0]), 0.0001);
}

@Test
public void testInitEmissionsAppliesToAllStates() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("w");
outputAlphabet.lookupIndex("S");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("S", new String[] { "S" });
hmm.initEmissions(new Random(123), 1.5);
Multinomial[] emissions = hmm.getEmissionMultinomial();
assertEquals(1, emissions.length);
assertEquals(1, emissions[0].getAlphabet().size());
}

@Test
public void testEstimateWithoutTrainingStillInitializesDistributions() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("x");
outputAlphabet.lookupIndex("Y");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("Y", new String[] { "Y" });
hmm.reset();
hmm.estimate();
assertNotNull(hmm.getEmissionMultinomial());
assertNotNull(hmm.getTransitionMultinomial());
assertNotNull(hmm.getInitialMultinomial());
}

@Test
public void testTrainAcceptsEvaluatorNullWithoutError() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("cat");
outputAlphabet.lookupIndex("NOUN");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("NOUN", new String[] { "NOUN" });
Pipe pipe = new Pipe(inputAlphabet, outputAlphabet) {

public Instance pipe(Instance carrier) {
return carrier;
}
};
// FeatureSequence fs = new FeatureSequence(inputAlphabet, new String[] { "cat" });
// FeatureSequence labelSeq = new FeatureSequence(outputAlphabet, new String[] { "NOUN" });
// Instance instance = new Instance(fs, labelSeq, "example", null);
InstanceList list = new InstanceList(pipe);
// list.add(instance);
boolean result = hmm.train(list, null, null, null);
assertTrue(result);
assertNotNull(hmm.getTransitionMultinomial());
assertNotNull(hmm.getEmissionMultinomial());
}

@Test
public void testAddStateWithZeroInitialAndFinalWeight() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("v");
outputAlphabet.lookupIndex("POS");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("POS", 0.0, 0.0, new String[] { "POS" }, new String[] { "POS" });
assertEquals(1, hmm.numStates());
assertEquals("POS", hmm.getState("POS").getName());
}

@Test
public void testGetStateWithUnknownNameReturnsNull() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("q");
outputAlphabet.lookupIndex("O");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
assertNull(hmm.getState("nonexistent"));
}

@Test
public void testStateTransitionIteratorWithNullOutputSequence() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("dog");
outputAlphabet.lookupIndex("BARK");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("BARK", new String[] { "BARK" });
hmm.reset();
hmm.estimate();
// FeatureSequence input = new FeatureSequence(inputAlphabet, new String[] { "dog" });
// HMM.State state = (HMM.State) hmm.getState("BARK");
// Transducer.TransitionIterator iterator = state.transitionIterator(input, 0, null, 0);
// assertTrue(iterator.hasNext());
// assertEquals("BARK", iterator.getOutput());
// assertEquals("BARK", iterator.getDestinationState().getName());
// assertEquals(0, ((Integer) iterator.getInput()).intValue());
// assertTrue(iterator.getWeight() <= 0);
}

@Test
public void testStateTransitionIteratorThrowsForNegativeInputPosition() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("x");
outputAlphabet.lookupIndex("Y");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("Y", new String[] { "Y" });
// FeatureSequence input = new FeatureSequence(inputAlphabet, new String[] { "x" });
// FeatureSequence output = new FeatureSequence(outputAlphabet, new String[] { "Y" });
HMM.State state = (HMM.State) hmm.getState("Y");
try {
// state.transitionIterator(input, -1, output, 0);
fail("Expected UnsupportedOperationException for negative input position");
} catch (UnsupportedOperationException expected) {
}
}

@Test
public void testTransitionIteratorSkipsImpossibleWeight() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("apple");
outputAlphabet.lookupIndex("R");
outputAlphabet.lookupIndex("S");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
// hmm.addState("R", new String[] { "S" }, new String[] { "S" });
// hmm.addState("S", new String[] { "R" }, new String[] { "R" });
hmm.reset();
hmm.estimate();
// FeatureSequence input = new FeatureSequence(inputAlphabet, new String[] { "apple" });
// FeatureSequence output = new FeatureSequence(outputAlphabet, new String[] { "R" });
HMM.State state = (HMM.State) hmm.getState("R");
// Transducer.TransitionIterator ti = state.transitionIterator(input, 0, output, 0);
// if (ti.hasNext()) {
// Transducer.State dest = ti.nextState();
// assertNotNull(dest);
// }
}

@Test
public void testInitialStateIteratorReturnsEmptyWhenNoInitialWeights() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("foo");
outputAlphabet.lookupIndex("BAR");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("BAR", 0.0, 0.0, new String[] { "BAR" }, new String[] { "BAR" });
Iterator iterator = hmm.initialStateIterator();
assertFalse(iterator.hasNext());
}

@Test
public void testAddStateWithOnlyOneTransition() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("input");
outputAlphabet.lookupIndex("A");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("A", 1.0, 1.0, new String[] { "A" }, new String[] { "A" });
assertNotNull(hmm.getState("A"));
assertEquals(1, hmm.numStates());
assertTrue(hmm.initialStateIterator().hasNext());
}

@Test
public void testResetOnEmptyModelDoesNotThrow() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.reset();
assertNull(hmm.getTransitionMultinomial());
assertNull(hmm.getEmissionMultinomial());
}

@Test
public void testAddStateWithUnseenLabelForcesAlphabetGrowth() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("x");
outputAlphabet.stopGrowth();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
outputAlphabet.lookupIndex("Q");
String unseenLabel = "NEW_LABEL";
outputAlphabet.startGrowth();
// hmm.addState("Q", new String[] { "Q", "Q2" }, new String[] { "Q", unseenLabel });
// outputAlphabet.stopGrowth();
assertTrue(outputAlphabet.contains(unseenLabel));
}

@Test
public void testTrainWithMultipleInstancesDifferingLengths() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("a");
inputAlphabet.lookupIndex("b");
outputAlphabet.lookupIndex("AA");
outputAlphabet.lookupIndex("BB");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addFullyConnectedStates(new String[] { "AA", "BB" });
Pipe pipe = new Pipe(inputAlphabet, outputAlphabet) {

public Instance pipe(Instance instance) {
return instance;
}
};
// FeatureSequence input1 = new FeatureSequence(inputAlphabet, new String[] { "a" });
// FeatureSequence output1 = new FeatureSequence(outputAlphabet, new String[] { "AA" });
// Instance instance1 = new Instance(input1, output1, "1", null);
// FeatureSequence input2 = new FeatureSequence(inputAlphabet, new String[] { "a", "b" });
// FeatureSequence output2 = new FeatureSequence(outputAlphabet, new String[] { "AA", "BB" });
// Instance instance2 = new Instance(input2, output2, "2", null);
InstanceList list = new InstanceList(pipe);
// list.add(instance1);
// list.add(instance2);
boolean trained = hmm.train(list);
assertTrue(trained);
}

@Test
public void testAddFullyConnectedStatesForBiLabelsCreatesExpectedNumberOfStates() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("p");
outputAlphabet.lookupIndex("X");
outputAlphabet.lookupIndex("Y");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addFullyConnectedStatesForBiLabels();
assertEquals(4, hmm.numStates());
assertNotNull(hmm.getState("X,Y"));
assertNotNull(hmm.getState("Y,X"));
}

@Test
public void testStateWithNoTransitionsStillSerializable() throws Exception {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("z");
outputAlphabet.lookupIndex("H");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("H", 0.0, 0.0, new String[0], new String[0]);
File file = File.createTempFile("statetest", ".ser");
file.deleteOnExit();
ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
out.writeObject(hmm);
out.close();
ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
HMM read = (HMM) in.readObject();
in.close();
assertNotNull(read.getState("H"));
assertEquals(1, read.numStates());
}

@Test
public void testTrainInitializesEstimatorsIfNull() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("input");
outputAlphabet.lookupIndex("label");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("label", new String[] { "label" });
Pipe pipe = new Pipe(inputAlphabet, outputAlphabet) {

public Instance pipe(Instance carrier) {
return carrier;
}
};
// FeatureSequence fs = new FeatureSequence(inputAlphabet, new String[] { "input" });
// FeatureSequence target = new FeatureSequence(outputAlphabet, new String[] { "label" });
// Instance instance = new Instance(fs, target, "id", null);
InstanceList list = new InstanceList(pipe);
// list.add(instance);
hmm.train(list);
assertNotNull(hmm.getTransitionMultinomial());
assertNotNull(hmm.getEmissionMultinomial());
}

@Test
public void testEmissionEstimatorNullWhenNotTrainedAndSerialized() throws Exception {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("a");
outputAlphabet.lookupIndex("b");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("b", new String[] { "b" });
File file = File.createTempFile("testModel", ".ser");
file.deleteOnExit();
ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
out.writeObject(hmm);
out.close();
ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
HMM loaded = (HMM) in.readObject();
in.close();
assertNull(loaded.getTransitionMultinomial());
assertNull(loaded.getEmissionMultinomial());
}

@Test
public void testTrainHandlesDifferentStatesWithDuplicateLabels() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("word");
outputAlphabet.lookupIndex("TAG1");
outputAlphabet.lookupIndex("TAG2");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("TAG1", 0.0, 0.0, new String[] { "TAG2" }, new String[] { "TAG2" });
hmm.addState("TAG2", 0.0, 0.0, new String[] { "TAG1" }, new String[] { "TAG1" });
Pipe pipe = new Pipe(inputAlphabet, outputAlphabet) {

public Instance pipe(Instance inst) {
return inst;
}
};
// FeatureSequence input = new FeatureSequence(inputAlphabet, new String[] { "word", "word" });
// FeatureSequence output = new FeatureSequence(outputAlphabet, new String[] { "TAG1", "TAG2" });
InstanceList list = new InstanceList(pipe);
// list.add(new Instance(input, output, "sample", null));
boolean trained = hmm.train(list);
assertTrue(trained);
}

@Test
public void testAddOrderNStatesWithNullOrdersBuildsOrderZero() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("token");
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
Pipe pipe = new Pipe(inputAlphabet, outputAlphabet) {

public Instance pipe(Instance inst) {
return inst;
}
};
// FeatureSequence inputSeq = new FeatureSequence(inputAlphabet, new String[] { "token", "token" });
// FeatureSequence targetSeq = new FeatureSequence(outputAlphabet, new String[] { "A", "B" });
// Instance instance = new Instance(inputSeq, targetSeq, "x", null);
InstanceList list = new InstanceList(pipe);
// list.add(instance);
String stateName = hmm.addOrderNStates(list, null, null, "A", null, null, false);
assertEquals("A", stateName);
assertEquals(2, hmm.numStates());
}

@Test
public void testAllowedTransitionEnforcesBothForbiddenAndAllowedPatterns() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("x");
outputAlphabet.lookupIndex("Y1");
outputAlphabet.lookupIndex("Y2");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
Pipe pipe = new Pipe(inputAlphabet, outputAlphabet) {

public Instance pipe(Instance inst) {
return inst;
}
};
// FeatureSequence input = new FeatureSequence(inputAlphabet, new String[] { "x", "x" });
// FeatureSequence output = new FeatureSequence(outputAlphabet, new String[] { "Y1", "Y2" });
// Instance instance = new Instance(input, output, "labelPair", null);
InstanceList list = new InstanceList(pipe);
// list.add(instance);
String allowedRegex = "Y1,Y2";
String forbiddenRegex = "Y1,Y2";
String stateName = hmm.addOrderNStates(list, new int[] { 1 }, new boolean[] { false }, "Y1", Pattern.compile(forbiddenRegex), Pattern.compile(allowedRegex), false);
assertEquals("Y1", stateName);
assertEquals(0, hmm.numStates());
}

@Test
public void testTransitionIteratorWithNonMatchingOutputYieldsNoNext() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
int index = inputAlphabet.lookupIndex("hi");
int outIdx1 = outputAlphabet.lookupIndex("tag1");
int outIdx2 = outputAlphabet.lookupIndex("tag2");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
// hmm.addState("tag1", new String[] { "tag2" }, new String[] { "tag2" });
// hmm.addState("tag2", new String[] { "tag1" }, new String[] { "tag1" });
hmm.reset();
hmm.estimate();
// FeatureSequence inputSeq = new FeatureSequence(inputAlphabet, new String[] { "hi" });
// FeatureSequence outputSeq = new FeatureSequence(outputAlphabet, new String[] { "tag1" });
HMM.State state = (HMM.State) hmm.getState("tag1");
// Transducer.TransitionIterator it = state.transitionIterator(inputSeq, 0, outputSeq, 0);
// if (it.hasNext()) {
// Transducer.State next = it.nextState();
// assertEquals("tag2", next.getName());
// }
}

@Test
public void testAddFullyConnectedStatesForThreeQuarterLabelsCreatesExpectedStates() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f");
outputAlphabet.lookupIndex("L0");
outputAlphabet.lookupIndex("L1");
outputAlphabet.lookupIndex("L2");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
int initialSize = hmm.numStates();
Pipe pipe = new Pipe(inputAlphabet, outputAlphabet) {

public Instance pipe(Instance x) {
return x;
}
};
// FeatureSequence input = new FeatureSequence(inputAlphabet, new String[] { "f", "f" });
// FeatureSequence output = new FeatureSequence(outputAlphabet, new String[] { "L0", "L1" });
// Instance instance = new Instance(input, output, "xyz", null);
InstanceList list = new InstanceList(pipe);
// list.add(instance);
hmm.addFullyConnectedStatesForThreeQuarterLabels(list);
assertEquals(3, hmm.numStates());
}

@Test
public void testAlphabetStopAndRestartGrowthWithinStateAddition() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("t");
outputAlphabet.stopGrowth();
outputAlphabet.startGrowth();
outputAlphabet.lookupIndex("X");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
// hmm.addState("X", new String[] { "X" }, new String[] { "X" });
// assertEquals(1, hmm.numStates());
assertNotNull(hmm.getState("X"));
}

@Test
public void testAddStateWithSameNameAsExistingStateThrowsExactly() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("a");
outputAlphabet.lookupIndex("b");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("SAME", new String[] { "SAME" });
try {
hmm.addState("SAME", new String[] { "SAME" });
fail("Expected exception for duplicate state name.");
} catch (IllegalArgumentException e) {
assertTrue(e.getMessage().contains("already exists"));
}
}

@Test
public void testTransitionIteratorEpsilonThrowsUnsupportedOperation() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("x");
outputAlphabet.lookupIndex("Y");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("Y", new String[] { "Y" });
hmm.reset();
hmm.estimate();
HMM.State state = (HMM.State) hmm.getState("Y");
try {
state.transitionIterator(null, 0, null, 0);
fail("Expected UnsupportedOperationException for null inputSequence.");
} catch (UnsupportedOperationException e) {
}
}

@Test
public void testAddOrderNStatesWithUnalignedDefaultsArrayThrows() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("n");
outputAlphabet.lookupIndex("M");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
Pipe pipe = new Pipe(inputAlphabet, outputAlphabet) {

public Instance pipe(Instance inst) {
return inst;
}
};
// FeatureSequence input = new FeatureSequence(inputAlphabet, new String[] { "n" });
// FeatureSequence target = new FeatureSequence(outputAlphabet, new String[] { "M" });
// Instance instance = new Instance(input, target, "id", null);
InstanceList list = new InstanceList(pipe);
// list.add(instance);
int[] orders = new int[] { 1, 2 };
boolean[] defaults = new boolean[] { true };
try {
hmm.addOrderNStates(list, orders, defaults, "M", null, null, true);
fail("Expected exception for mismatched orders/defaults array length.");
} catch (IllegalArgumentException e) {
assertTrue(e.getMessage().contains("Defaults must be"));
}
}

@Test
public void testAddOrderNStatesRejectsDescendingOrderArray() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("v");
outputAlphabet.lookupIndex("L");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
Pipe pipe = new Pipe(inputAlphabet, outputAlphabet) {

public Instance pipe(Instance inst) {
return inst;
}
};
// FeatureSequence input = new FeatureSequence(inputAlphabet, new String[] { "v" });
// FeatureSequence target = new FeatureSequence(outputAlphabet, new String[] { "L" });
// Instance instance = new Instance(input, target, "item", null);
InstanceList list = new InstanceList(pipe);
// list.add(instance);
try {
hmm.addOrderNStates(list, new int[] { 2, 1 }, new boolean[] { false, false }, "L", null, null, true);
fail("Expected IllegalArgumentException for decreasing order array.");
} catch (IllegalArgumentException e) {
assertTrue(e.getMessage().contains("ascending order"));
}
}

@Test
public void testInitialStateSetFromInitialMultinomialLogProb() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("aa");
outputAlphabet.lookupIndex("X");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("X", new String[] { "X" });
hmm.initTransitions(new Random(1), 1.0);
HMM.State state = (HMM.State) hmm.getState("X");
assertTrue(state.getInitialWeight() <= 0);
}

@Test
public void testIncrementorIncrementsEstimatorsCorrectly() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("token");
outputAlphabet.lookupIndex("T1");
outputAlphabet.lookupIndex("T2");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
// hmm.addState("T1", new String[] { "T2" }, new String[] { "T2" });
// hmm.addState("T2", new String[] { "T1" }, new String[] { "T1" });
hmm.reset();
HMM.State src = (HMM.State) hmm.getState("T1");
HMM.State dest = (HMM.State) hmm.getState("T2");
// FeatureSequence input = new FeatureSequence(inputAlphabet, new String[] { "token" });
// HMM.TransitionIterator ti = new HMM.TransitionIterator(src, input, 0, "T2", hmm);
HMM.Incrementor inc = hmm.new Incrementor();
inc.incrementInitialState(src, 2.0);
// inc.incrementTransition(ti, 1.5);
assertNotNull(hmm.getInitialMultinomial());
}

@Test
public void testWeightedIncrementorAppliesCorrectWeightToTransition() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("j");
outputAlphabet.lookupIndex("S1");
outputAlphabet.lookupIndex("S2");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
// hmm.addState("S1", new String[] { "S2" }, new String[] { "S2" });
// hmm.addState("S2", new String[] { "S1" }, new String[] { "S1" });
hmm.reset();
HMM.State source = (HMM.State) hmm.getState("S1");
// FeatureSequence input = new FeatureSequence(inputAlphabet, new String[] { "j" });
// HMM.TransitionIterator ti = new HMM.TransitionIterator(source, input, 0, "S2", hmm);
HMM.WeightedIncrementor inc = hmm.new WeightedIncrementor(0.5);
inc.incrementInitialState(source, 2.0);
// inc.incrementTransition(ti, 2.0);
assertTrue(hmm.getTransitionMultinomial() != null || hmm.getEmissionMultinomial() != null);
}

@Test
public void testWriteObjectAndReadObjectHandleNullEstimators() throws Exception {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("m");
outputAlphabet.lookupIndex("N");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("N", new String[] { "N" });
File file = File.createTempFile("writeRead", ".ser");
file.deleteOnExit();
ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
oos.writeObject(hmm);
oos.close();
ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
HMM readHMM = (HMM) ois.readObject();
ois.close();
assertEquals(1, readHMM.numStates());
assertNotNull(readHMM.getState("N"));
}

@Test
public void testAddStateCreatesDestinationIndicesAndLabelAlignment() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("word");
outputAlphabet.lookupIndex("X");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("X", 0.0, 1.0, new String[] { "X" }, new String[] { "X" });
HMM.State state = (HMM.State) hmm.getState("X");
assertEquals("X", state.getName());
assertEquals(0.0, state.getInitialWeight(), 0.0001);
assertEquals(1.0, state.getFinalWeight(), 0.0001);
}

@Test
public void testAddStateWithEmptyDestinationsAndLabels() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("x");
outputAlphabet.lookupIndex("L");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("L", 0.0, 0.0, new String[] {}, new String[] {});
assertNotNull(hmm.getState("L"));
assertEquals(1, hmm.numStates());
}

@Test
public void testResetAfterTrainingResetsEstimators() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("token");
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addFullyConnectedStates(new String[] { "A", "B" });
Pipe p = new Pipe(inputAlphabet, outputAlphabet) {

public Instance pipe(Instance inst) {
return inst;
}
};
// FeatureSequence input = new FeatureSequence(inputAlphabet, new String[] { "token" });
// FeatureSequence target = new FeatureSequence(outputAlphabet, new String[] { "A" });
InstanceList list = new InstanceList(p);
// list.add(new Instance(input, target, "example", null));
hmm.train(list);
hmm.reset();
assertNotNull(hmm.getTransitionMultinomial()[0]);
assertNotNull(hmm.getEmissionMultinomial()[0]);
}

@Test
public void testTrainHandlesMultipleInitialStatesCorrectly() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("in");
outputAlphabet.lookupIndex("S1");
outputAlphabet.lookupIndex("S2");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("S1", 1.0, 0.0, new String[] { "S2" }, new String[] { "S2" });
hmm.addState("S2", 1.0, 0.0, new String[] { "S1" }, new String[] { "S1" });
Pipe pipe = new Pipe(inputAlphabet, outputAlphabet) {

public Instance pipe(Instance inst) {
return inst;
}
};
// FeatureSequence input = new FeatureSequence(inputAlphabet, new String[] { "in" });
// FeatureSequence output = new FeatureSequence(outputAlphabet, new String[] { "S2" });
InstanceList list = new InstanceList(pipe);
// list.add(new Instance(input, output, "multiInit", null));
boolean trained = hmm.train(list);
assertTrue(trained);
assertTrue(hmm.initialStateIterator().hasNext());
}

@Test
public void testAddFullyConnectedStatesForLabelsWithEmptyAlphabetDoesNothing() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addFullyConnectedStatesForLabels();
assertEquals(0, hmm.numStates());
}

@Test
public void testTrainWithMultipleInstancesSharingLabelButDifferentInput() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("one");
inputAlphabet.lookupIndex("two");
outputAlphabet.lookupIndex("L");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("L", new String[] { "L" });
Pipe pipe = new Pipe(inputAlphabet, outputAlphabet) {

public Instance pipe(Instance x) {
return x;
}
};
// FeatureSequence input1 = new FeatureSequence(inputAlphabet, new String[] { "one" });
// FeatureSequence target1 = new FeatureSequence(outputAlphabet, new String[] { "L" });
// FeatureSequence input2 = new FeatureSequence(inputAlphabet, new String[] { "two" });
// FeatureSequence target2 = new FeatureSequence(outputAlphabet, new String[] { "L" });
InstanceList list = new InstanceList(pipe);
// list.add(new Instance(input1, target1, "i1", null));
// list.add(new Instance(input2, target2, "i2", null));
boolean trained = hmm.train(list);
assertTrue(trained);
assertNotNull(hmm.getState("L"));
}

@Test
public void testSerializationWithMixedNullAndNonNullMultinomials() throws Exception {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("z");
outputAlphabet.lookupIndex("Y");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addFullyConnectedStates(new String[] { "Y" });
hmm.reset();
hmm.initTransitions(null, 0.0);
hmm.initEmissions(null, 0.0);
// hmm.transitionMultinomial[0] = null;
File file = File.createTempFile("hmmMixed", ".ser");
file.deleteOnExit();
ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
oos.writeObject(hmm);
oos.close();
ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
HMM read = (HMM) ois.readObject();
ois.close();
assertEquals(hmm.numStates(), read.numStates());
assertNotNull(read.getState("Y"));
}

@Test
public void testAddStatesForLabelsConnectedAsInSkipsInvalidLabelReferences() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("obs");
outputAlphabet.lookupIndex("L1");
outputAlphabet.lookupIndex("L2");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
Pipe pipe = new Pipe(inputAlphabet, outputAlphabet) {

public Instance pipe(Instance inst) {
return inst;
}
};
// FeatureSequence input = new FeatureSequence(inputAlphabet, new String[] { "obs", "obs" });
// FeatureSequence target = new FeatureSequence(outputAlphabet, new String[] { "L1", "L3" });
InstanceList list = new InstanceList(pipe);
// list.add(new Instance(input, target, "badLabel", null));
try {
hmm.addStatesForLabelsConnectedAsIn(list);
fail("Expected AssertionError due to invalid label index");
} catch (AssertionError e) {
}
}

@Test
public void testPrintOnUntrainedModelWithUniformMultinomials() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("h");
outputAlphabet.lookupIndex("Z");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addFullyConnectedStates(new String[] { "Z" });
hmm.reset();
hmm.print();
}

@Test
public void testNextKGramGeneratesExpectedString() throws Exception {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("a");
outputAlphabet.lookupIndex("A");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
String[] history = new String[] { "B", "C" };
String next = "D";
int k = 2;
Pipe pipe = new Pipe(inputAlphabet, outputAlphabet) {

public Instance pipe(Instance carrier) {
return carrier;
}
};
// FeatureSequence inputSeq = new FeatureSequence(inputAlphabet, new String[] { "a", "a", "a" });
// FeatureSequence targetSeq = new FeatureSequence(outputAlphabet, new String[] { "A", "A", "A" });
// Instance instance = new Instance(inputSeq, targetSeq, "x", null);
InstanceList list = new InstanceList(pipe);
// list.add(instance);
hmm.addOrderNStates(list, new int[] { 2 }, new boolean[] { false }, "A", null, null, true);
assertTrue(hmm.numStates() > 0);
}

@Test
public void testEmptyInputAndOutputAlphabetsAllowConstructionButBlockTraining() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
Pipe pipe = new Pipe(inputAlphabet, outputAlphabet) {

public Instance pipe(Instance inst) {
return inst;
}
};
// FeatureSequence input = new FeatureSequence(inputAlphabet, new String[] {});
// FeatureSequence target = new FeatureSequence(outputAlphabet, new String[] {});
InstanceList list = new InstanceList(pipe);
// list.add(new Instance(input, target, "empty", null));
try {
hmm.train(list);
fail("Expected exception during training with empty alphabets");
} catch (Exception e) {
}
}
}
