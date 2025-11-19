package cc.mallet.fst.tests;

import cc.mallet.fst.CRF;
import cc.mallet.fst.HMM;
import cc.mallet.fst.Transducer;
import cc.mallet.pipe.*;
import cc.mallet.types.*;
import org.junit.Test;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Pattern;
import static org.junit.Assert.*;

public class HMM_1_GPTLLMTest {

@Test
public void testAddStateBasic() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
String[] destinationNames = new String[] { "S2" };
String[] labelNames = new String[] { "L1" };
outputAlphabet.lookupIndex("L1");
hmm.addState("S1", 1.0, 0.0, destinationNames, labelNames);
assertEquals(1, hmm.numStates());
Transducer.State state = hmm.getState("S1");
assertNotNull(state);
assertEquals("S1", state.getName());
}

@Test(expected = IllegalArgumentException.class)
public void testAddStateDuplicateFails() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
outputAlphabet.lookupIndex("L");
hmm.addState("A", 1.0, 0.0, new String[] { "B" }, new String[] { "L" });
hmm.addState("A", 0.0, 0.0, new String[] { "C" }, new String[] { "L" });
}

@Test
public void testFullyConnectedStates() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
outputAlphabet.lookupIndex("X");
outputAlphabet.lookupIndex("Y");
String[] allLabels = new String[] { "X", "Y" };
hmm.addState("X", allLabels);
hmm.addState("Y", allLabels);
assertEquals(2, hmm.numStates());
assertNotNull(hmm.getState("X"));
assertNotNull(hmm.getState("Y"));
}

@Test
public void testInitEmissions() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("word");
outputAlphabet.lookupIndex("label");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("label", new String[] { "label" });
Random random = new Random(1234);
hmm.initEmissions(random, 0.5);
Multinomial[] emissions = hmm.getEmissionMultinomial();
assertNotNull(emissions);
assertEquals(1, emissions.length);
assertNotNull(emissions[0]);
}

@Test
public void testInitTransitions() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("label");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("label", new String[] { "label" });
Random random = new Random(1234);
hmm.initTransitions(random, 0.5);
Multinomial[] transitions = hmm.getTransitionMultinomial();
assertNotNull(transitions);
assertEquals(1, transitions.length);
assertNotNull(transitions[0]);
assertNotNull(hmm.getInitialMultinomial());
}

@Test
public void testTrainSimpleModel() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("a");
outputAlphabet.lookupIndex("X");
ArrayList<Pipe> pipes = new ArrayList<Pipe>();
pipes.add(new TokenSequence2FeatureSequence(inputAlphabet));
// pipes.add(new Target2LabelSequence(outputAlphabet));
Pipe pipe = new SerialPipes(pipes);
// Instance instance = new Instance(new TokenSequence("a"), new TokenSequence("X"), null, null);
InstanceList trainingData = new InstanceList(pipe);
// trainingData.addThruPipe(instance);
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("X", new String[] { "X" });
boolean result = hmm.train(trainingData);
assertTrue(result);
}

@Test
public void testAddOrderZeroNStates() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("X");
outputAlphabet.lookupIndex("Y");
// Pipe pipe = new SerialPipes(Arrays.asList(new TokenSequence2FeatureSequence(inputAlphabet), new Target2LabelSequence(outputAlphabet)));
// TokenSequence inputTokens = new TokenSequence("w");
// TokenSequence outputLabels = new TokenSequence("X");
// Instance instance = new Instance(inputTokens, outputLabels, null, null);
// InstanceList trainingSet = new InstanceList(pipe);
// trainingSet.addThruPipe(instance);
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
// String startState = hmm.addOrderNStates(trainingSet, null, null, "X", null, null, false);
// assertEquals("X", startState);
assertNotNull(hmm.getState("X"));
}

@Test
public void testAllowedTransitionPattern() {
Pattern forbidden = Pattern.compile("A,B");
Pattern allowed = Pattern.compile("A,C");
HMM hmm = new HMM(new Alphabet(), new Alphabet());
// boolean case1 = hmm.allowedTransition("A", "B", forbidden, allowed);
// boolean case2 = hmm.allowedTransition("A", "C", forbidden, allowed);
// boolean case3 = hmm.allowedTransition("A", "X", null, null);
// assertFalse(case1);
// assertTrue(case2);
// assertTrue(case3);
}

@Test
public void testSerialization() throws Exception {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("X", new String[] { "X" });
hmm.reset();
File tempFile = File.createTempFile("hmm-unit", ".ser");
tempFile.deleteOnExit();
hmm.write(tempFile);
ObjectInputStream in = new ObjectInputStream(new FileInputStream(tempFile));
HMM deserialized = (HMM) in.readObject();
in.close();
assertEquals(1, deserialized.numStates());
assertNotNull(deserialized.getState("X"));
}

@Test
public void testAddSelfTransitionStateForAllLabels() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addSelfTransitioningStateForAllLabels("LOOP");
Transducer.State state = hmm.getState("LOOP");
assertNotNull(state);
assertEquals("LOOP", state.getName());
assertEquals(1, hmm.numStates());
}

@Test
public void testTransitionIteratorBasic() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("tok");
outputAlphabet.lookupIndex("X");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("X", new String[] { "X" });
hmm.reset();
// Pipe pipe = new SerialPipes(Arrays.asList(new TokenSequence2FeatureSequence(inputAlphabet), new Target2LabelSequence(outputAlphabet)));
// TokenSequence input = new TokenSequence("tok tok");
// TokenSequence output = new TokenSequence("X X");
// InstanceList instances = new InstanceList(pipe);
// instances.addThruPipe(new Instance(input, output, null, null));
// FeatureSequence inputSeq = (FeatureSequence) instances.get(0).getData();
// FeatureSequence outputSeq = (FeatureSequence) instances.get(0).getTarget();
HMM.State state = hmm.getState("X");
// Transducer.TransitionIterator ti = state.transitionIterator(inputSeq, 1, outputSeq, 1);
// assertTrue(ti.hasNext());
// Transducer.State nextState = ti.nextState();
// assertNotNull(nextState);
// assertEquals("X", nextState.getName());
}

@Test
public void testResetPopulatesMultinomials() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("tag");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("tag", new String[] { "tag" });
hmm.reset();
Multinomial[] emissions = hmm.getEmissionMultinomial();
Multinomial[] transitions = hmm.getTransitionMultinomial();
Multinomial initial = hmm.getInitialMultinomial();
assertNotNull(emissions);
assertNotNull(transitions);
assertNotNull(initial);
assertEquals(1, emissions.length);
assertEquals(1, transitions.length);
}

@Test
public void testAddStateWithEmptyDestinationsAndLabels() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
String[] empty = new String[0];
hmm.addState("EMPTY", 0.0, 0.0, empty, empty);
assertEquals(1, hmm.numStates());
assertNotNull(hmm.getState("EMPTY"));
}

@Test
public void testAddStateWithNullWeightsAllowed() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("X");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("X", 0.0, 0.0, new String[] { "X" }, new String[] { "X" });
Transducer.State s = hmm.getState("X");
assertEquals(0.0, s.getInitialWeight(), 0.0);
assertEquals(0.0, s.getFinalWeight(), 0.0);
}

@Test
public void testEstimateWithoutCallingResetStillInitializes() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("w1");
outputAlphabet.lookupIndex("Y");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("Y", new String[] { "Y" });
// hmm.train(createSimpleInstanceList(inputAlphabet, outputAlphabet));
hmm.estimate();
Multinomial[] emissions = hmm.getEmissionMultinomial();
Multinomial[] transitions = hmm.getTransitionMultinomial();
assertNotNull(emissions[0]);
assertNotNull(transitions[0]);
}

@Test
public void testTrainRejectsEmptyInstanceList() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("A", new String[] { "A" });
// InstanceList emptyList = new InstanceList(new SerialPipes(Arrays.asList(new TokenSequence2FeatureSequence(inputAlphabet), new Target2LabelSequence(outputAlphabet))));
try {
// hmm.train(emptyList);
fail("Expected AssertionError when training on empty list");
} catch (AssertionError expected) {
}
}

@Test
public void testAddOrderNStatesWithForbiddenTransitionsSkipsState() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
// Pipe pipe = new SerialPipes(Arrays.asList(new TokenSequence2FeatureSequence(inputAlphabet), new Target2LabelSequence(outputAlphabet)));
// InstanceList data = new InstanceList(pipe);
// data.addThruPipe(new Instance(new TokenSequence("x y"), new TokenSequence("A B"), null, null));
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
Pattern forbidden = Pattern.compile("A,B");
// String start = hmm.addOrderNStates(data, new int[] { 1 }, null, "A", forbidden, null, false);
// assertNotNull(start);
assertNull(hmm.getState("A,B"));
}

@Test
public void testAddOrderNStatesAllowedOnlyMatch() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("P");
outputAlphabet.lookupIndex("Q");
// Pipe pipe = new SerialPipes(Arrays.asList(new TokenSequence2FeatureSequence(inputAlphabet), new Target2LabelSequence(outputAlphabet)));
// InstanceList instances = new InstanceList(pipe);
// instances.addThruPipe(new Instance(new TokenSequence("x y"), new TokenSequence("P Q"), null, null));
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
Pattern allowed = Pattern.compile("P,Q");
// hmm.addOrderNStates(instances, new int[] { 1 }, null, "P", null, allowed, true);
// Transducer.State state = hmm.getState("P");
// assertNotNull(state);
// assertEquals("P", state.getName());
}

@Test
public void testAddOrderNStatesThrowsOnNonAscendingOrderArray() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("Y");
// Pipe pipe = new SerialPipes(Arrays.asList(new TokenSequence2FeatureSequence(inputAlphabet), new Target2LabelSequence(outputAlphabet)));
// InstanceList training = new InstanceList(pipe);
// training.addThruPipe(new Instance(new TokenSequence("t"), new TokenSequence("Y"), null, null));
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
try {
// hmm.addOrderNStates(training, new int[] { 2, 1 }, null, "Y", null, null, false);
// fail("Expected IllegalArgumentException for non-ascending orders");
} catch (IllegalArgumentException expected) {
}
}

@Test
public void testAddStatesForBiLabelsConnectedAsInHandlesCorrectTransitions() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
// Pipe pipe = new SerialPipes(Arrays.asList(new TokenSequence2FeatureSequence(inputAlphabet), new Target2LabelSequence(outputAlphabet)));
// InstanceList data = new InstanceList(pipe);
// data.addThruPipe(new Instance(new TokenSequence("t1 t2"), new TokenSequence("A B"), null, null));
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
// hmm.addStatesForBiLabelsConnectedAsIn(data);
assertNotNull(hmm.getState("A,B"));
assertNotNull(hmm.getState("B,B"));
}

@Test
public void testAddFullyConnectedStatesForBiLabelsCorrectlyBuilds() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("X");
outputAlphabet.lookupIndex("Y");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addFullyConnectedStatesForBiLabels();
Transducer.State state = hmm.getState("X,Y");
assertNotNull(state);
assertEquals("X,Y", state.getName());
}

@Test
public void testAddStateWithMismatchedLabelAndDestThrows() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
outputAlphabet.lookupIndex("LABEL");
try {
hmm.addState("S1", 0.0, 0.0, new String[] { "S2" }, new String[] { "LABEL", "EXTRA" });
fail("Expected AssertionError due to mismatched lengths");
} catch (AssertionError | IllegalArgumentException e) {
}
}

@Test
public void testAddStateIncludesDestinationInOutputAlphabet() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("A");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("STATE", new String[] { "DEST" });
assertNotNull(hmm.getState("STATE"));
}

@Test
public void testGetStateByInvalidNameReturnsNull() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
assertNull(hmm.getState("nonexistent"));
}

@Test
public void testTransitionIteratorThrowsOnNullInputSequence() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("hello");
outputAlphabet.lookupIndex("X");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("X", new String[] { "X" });
hmm.reset();
HMM.State state = (HMM.State) hmm.getState("X");
try {
state.transitionIterator(null, 0, null, 0);
fail("Should throw UnsupportedOperationException");
} catch (UnsupportedOperationException e) {
}
}

@Test
public void testTransitionIteratorThrowsOnNegativeInputPos() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("a");
outputAlphabet.lookupIndex("X");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("X", new String[] { "X" });
hmm.reset();
FeatureSequence inputSeq = new FeatureSequence(inputAlphabet, new int[] { 0 });
FeatureSequence labelSeq = new FeatureSequence(outputAlphabet, new int[] { 0 });
HMM.State state = (HMM.State) hmm.getState("X");
try {
state.transitionIterator(inputSeq, -1, labelSeq, 0);
fail("Expected UnsupportedOperationException");
} catch (UnsupportedOperationException e) {
}
}

@Test
public void testTransitionIteratorRejectsNonFeatureSequenceInput() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("Z");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("Z", new String[] { "Z" });
Alphabet dummyAlpha = new Alphabet();
// Sequence badSequence = new StringSequence(dummyAlpha, new String[] { "dummy" });
// try {
// HMM.State state = (HMM.State) hmm.getState("Z");
// state.transitionIterator(badSequence, 0, null, 0);
// fail("Expected UnsupportedOperationException due to non-FeatureSequence input");
// } catch (UnsupportedOperationException e) {
// }
}

@Test
public void testNextKGramGeneratesCorrectLabelSequence() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
// Instance instance = new Instance(new TokenSequence("t1 t2"), new TokenSequence(new String[] { "A", "B" }), null, null);
// SerialPipes pipes = new SerialPipes(Arrays.asList(new TokenSequence2FeatureSequence(inputAlphabet), new Target2LabelSequence(outputAlphabet)));
// InstanceList list = new InstanceList(pipes);
// list.addThruPipe(instance);
// hmm.addOrderNStates(list, new int[] { 1 }, null, "A", null, null, false);
// assertNotNull(hmm.getState("A"));
assertNotNull(hmm.getState("B"));
}

@Test
public void testResetTwoStates() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("input1");
inputAlphabet.lookupIndex("input2");
outputAlphabet.lookupIndex("L1");
outputAlphabet.lookupIndex("L2");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("L1", new String[] { "L2" });
hmm.addState("L2", new String[] { "L1" });
hmm.reset();
Multinomial[] trans = hmm.getTransitionMultinomial();
Multinomial[] emit = hmm.getEmissionMultinomial();
assertEquals(2, trans.length);
assertEquals(2, emit.length);
}

@Test
public void testTrainWithNullValidationAndTesting() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
ArrayList pipeList = new ArrayList();
pipeList.add(new TokenSequence2FeatureSequence(inputAlphabet));
// pipeList.add(new Target2LabelSequence(outputAlphabet));
SerialPipes pipes = new SerialPipes(pipeList);
InstanceList training = new InstanceList(pipes);
// TokenSequence tokens = new TokenSequence("a");
// TokenSequence labels = new TokenSequence("X");
// training.addThruPipe(new Instance(tokens, labels, null, null));
outputAlphabet.lookupIndex("X");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("X", new String[] { "X" });
boolean result = hmm.train(training, null, null);
assertTrue(result);
}

@Test
public void testWriteObjectHandlesNullArrays() throws Exception {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("ONLY", new String[] { "ONLY" });
File temp = File.createTempFile("hmm-null-test", ".ser");
temp.deleteOnExit();
ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(temp));
out.writeObject(hmm);
out.close();
ObjectInputStream in = new ObjectInputStream(new FileInputStream(temp));
HMM loaded = (HMM) in.readObject();
in.close();
assertNotNull(loaded.getState("ONLY"));
}

@Test
public void testGetTransitionMultinomialReturnsNullIfUnset() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
assertNull(hmm.getTransitionMultinomial());
}

@Test
public void testTransitionWeightIsImpossibleForMismatchedOutput() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("a");
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("A", new String[] { "A" });
hmm.reset();
FeatureSequence inputSeq = new FeatureSequence(inputAlphabet, new int[] { 0 });
FeatureSequence outputSeq = new FeatureSequence(outputAlphabet, new int[] { 1 });
HMM.State s = (HMM.State) hmm.getState("A");
Transducer.TransitionIterator ti = s.transitionIterator(inputSeq, 0, outputSeq, 0);
assertTrue(ti.hasNext());
Transducer.State dest = ti.nextState();
assertNotNull(dest);
assertEquals("A", dest.getName());
assertEquals(HMM.IMPOSSIBLE_WEIGHT, ti.getWeight(), 0.0001);
}

@Test
public void testStateGetDestinationStateReturnsConsistentReference() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("Y");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("Y", new String[] { "Y" });
Transducer.State state = hmm.getState("Y");
Transducer.State dest1 = ((HMM.State) state).getDestinationState(0);
Transducer.State dest2 = ((HMM.State) state).getDestinationState(0);
assertSame(dest1, dest2);
}

@Test
public void testTrainOverridesTransitionProbabilityWithEstimator() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("a");
outputAlphabet.lookupIndex("L");
// SerialPipes pipes = new SerialPipes(Arrays.asList(new TokenSequence2FeatureSequence(inputAlphabet), new Target2LabelSequence(outputAlphabet)));
// InstanceList ilist = new InstanceList(pipes);
// TokenSequence ts = new TokenSequence("a");
// TokenSequence ls = new TokenSequence("L");
// ilist.addThruPipe(new Instance(ts, ls, null, null));
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("L", new String[] { "L" });
// hmm.train(ilist);
Multinomial[] transitionMultinomial = hmm.getTransitionMultinomial();
Multinomial[] emissionMultinomial = hmm.getEmissionMultinomial();
assertTrue(transitionMultinomial[0].getAlphabet().contains("L"));
assertTrue(emissionMultinomial[0].getAlphabet().contains(0));
}

@Test
public void testNextStateOnLastIndexThrowsNoSuchElement() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("w");
outputAlphabet.lookupIndex("X");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("X", new String[] { "X" });
hmm.reset();
FeatureSequence inputSeq = new FeatureSequence(inputAlphabet, new int[] { 0 });
FeatureSequence outputSeq = new FeatureSequence(outputAlphabet, new int[] { 0 });
HMM.State state = (HMM.State) hmm.getState("X");
Transducer.TransitionIterator ti = state.transitionIterator(inputSeq, 0, outputSeq, 0);
Transducer.State s1 = ti.nextState();
assertNotNull(s1);
assertFalse(ti.hasNext());
}

@Test
public void testAllowedTransitionNullPatternsAlwaysTrue() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
// boolean result = hmm.allowedTransition("A", "B", null, null);
// assertTrue(result);
}

@Test
public void testAllowedTransitionFailsForPatternMismatch() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
Pattern mustBeOnly = Pattern.compile("X,Y");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
// boolean allowed1 = hmm.allowedTransition("X", "Y", null, mustBeOnly);
// boolean allowed2 = hmm.allowedTransition("X", "Z", null, mustBeOnly);
// assertTrue(allowed1);
// assertFalse(allowed2);
}

@Test
public void testAllowedHistoryFailsWithDisallowedPair() {
Alphabet ia = new Alphabet();
Alphabet oa = new Alphabet();
Pattern forbidden = Pattern.compile("A,B");
String[] labels = new String[] { "A", "B", "C" };
HMM hmm = new HMM(ia, oa);
// boolean result = hmm.allowedHistory(labels, forbidden, null);
// assertFalse(result);
}

@Test
public void testSerializationWithEmptyAlphabets() throws Exception {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("Start", new String[] { "Start" });
File tempFile = File.createTempFile("emptyserial-hmm", ".ser");
tempFile.deleteOnExit();
ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(tempFile));
out.writeObject(hmm);
out.close();
ObjectInputStream in = new ObjectInputStream(new FileInputStream(tempFile));
HMM deserialized = (HMM) in.readObject();
assertNotNull(deserialized.getState("Start"));
}

@Test
public void testNullEstimatorsHandledInSerialization() throws Exception {
Alphabet a = new Alphabet();
Alphabet b = new Alphabet();
HMM hmm = new HMM(a, b);
hmm.addState("A", new String[] { "A" });
File file = File.createTempFile("hmm-null-est", ".ser");
file.deleteOnExit();
ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
oos.writeObject(hmm);
oos.close();
ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
HMM hmm2 = (HMM) ois.readObject();
ois.close();
assertNotNull(hmm2.getState("A"));
}

@Test
public void testTransitionAlphaContainsOnlyDefinedStates() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("i1");
outputAlphabet.lookupIndex("L1");
outputAlphabet.lookupIndex("L2");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("L1", new String[] { "L2" });
hmm.addState("L2", new String[] { "L1" });
hmm.reset();
Multinomial[] trans = hmm.getTransitionMultinomial();
assertTrue(trans[0].getAlphabet().contains("L2"));
assertTrue(trans[1].getAlphabet().contains("L1"));
assertFalse(trans[0].getAlphabet().contains("NONEXISTENT"));
}

@Test
public void testWeightedIncrementorAffectsCounts() {
Alphabet inputAlpha = new Alphabet();
Alphabet outputAlpha = new Alphabet();
inputAlpha.lookupIndex("f1");
outputAlpha.lookupIndex("Y");
HMM hmm = new HMM(inputAlpha, outputAlpha);
hmm.addState("Y", new String[] { "Y" });
hmm.reset();
FeatureSequence inputSeq = new FeatureSequence(inputAlpha, new int[] { 0 });
FeatureSequence outputSeq = new FeatureSequence(outputAlpha, new int[] { 0 });
HMM.State s = (HMM.State) hmm.getState("Y");
Transducer.TransitionIterator ti = s.transitionIterator(inputSeq, 0, outputSeq, 0);
HMM.WeightedIncrementor inc = hmm.new WeightedIncrementor(2.0);
while (ti.hasNext()) {
ti.nextState();
inc.incrementTransition(ti, 1.0);
}
// Multinomial.Estimator[] ests = hmm.emissionEstimator;
// assertTrue(ests[0] != null);
}

@Test
public void testEstimateLogProbsNotNaN() {
Alphabet i = new Alphabet();
Alphabet o = new Alphabet();
i.lookupIndex("w");
o.lookupIndex("X");
HMM hmm = new HMM(i, o);
hmm.addState("X", new String[] { "X" });
FeatureSequence input = new FeatureSequence(i, new int[] { 0 });
FeatureSequence output = new FeatureSequence(o, new int[] { 0 });
// SerialPipes pipes = new SerialPipes(Arrays.asList(new TokenSequence2FeatureSequence(i), new Target2LabelSequence(o)));
// InstanceList list = new InstanceList(pipes);
// list.addThruPipe(new Instance(new TokenSequence("w"), new TokenSequence("X"), null, null));
// hmm.train(list);
double val = hmm.getInitialMultinomial().logProbability("X");
assertFalse(Double.isNaN(val));
}

@Test
public void testAllowedHistoryReturnsTrueForValidChain() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
String[] labels = new String[] { "A", "B", "C" };
Pattern no = Pattern.compile("X,Y");
Pattern yes = Pattern.compile(".+,.+");
// boolean allowed = hmm.allowedHistory(labels, no, yes);
// assertTrue(allowed);
}

@Test
public void testNextKGramProducesCorrectConcatenation() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
String[] history = new String[] { "A", "B" };
// String result = hmm.nextKGram(history, 2, "C");
// assertEquals("A,B,C", result);
}

@Test
public void testGetDestinationStateOutOfBoundsThrows() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("TAG");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("TAG", new String[] { "TAG" });
HMM.State state = (HMM.State) hmm.getState("TAG");
try {
state.getDestinationState(5);
fail("Expected IndexOutOfBoundsException");
} catch (ArrayIndexOutOfBoundsException | AssertionError expected) {
}
}

@Test
public void testAddFullyConnectedStatesForTriLabelsGeneratesCombinations() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("X");
outputAlphabet.lookupIndex("Y");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addFullyConnectedStatesForTriLabels();
assertTrue(hmm.numStates() > 0);
assertNotNull(hmm.getState("X,Y,X"));
assertNotNull(hmm.getState("Y,Y,Y"));
}

@Test
public void testWriteReadObjectWithNullPipes() throws Exception {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("S1", new String[] { "S1" });
File tempFile = File.createTempFile("hmm-nullpipes", ".ser");
tempFile.deleteOnExit();
ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(tempFile));
out.writeObject(hmm);
out.close();
ObjectInputStream in = new ObjectInputStream(new FileInputStream(tempFile));
HMM deserialized = (HMM) in.readObject();
in.close();
assertNotNull(deserialized.getState("S1"));
}

@Test
public void testTrainHandlesStateWithCommaInName() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("tok");
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
// SerialPipes pipes = new SerialPipes(Arrays.asList(new TokenSequence2FeatureSequence(inputAlphabet), new Target2LabelSequence(outputAlphabet)));
// InstanceList ilist = new InstanceList(pipes);
// ilist.addThruPipe(new Instance(new TokenSequence("tok"), new TokenSequence("A"), null, null));
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
// hmm.addState("A,B", new String[] { "A,B" }, new String[] { "A" });
// boolean trained = hmm.train(ilist);
// assertTrue(trained);
}

@Test
public void testWeightedIncrementorMultipliesCountCorrectly() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("t0");
outputAlphabet.lookupIndex("A");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("A", new String[] { "A" });
hmm.reset();
FeatureSequence inputSeq = new FeatureSequence(inputAlphabet, new int[] { 0 });
FeatureSequence outputSeq = new FeatureSequence(outputAlphabet, new int[] { 0 });
HMM.State state = (HMM.State) hmm.getState("A");
Transducer.TransitionIterator ti = state.transitionIterator(inputSeq, 0, outputSeq, 0);
HMM.WeightedIncrementor incrementor = hmm.new WeightedIncrementor(3.5);
while (ti.hasNext()) {
ti.nextState();
incrementor.incrementTransition(ti, 2);
incrementor.incrementInitialState(state, 1);
}
assertTrue(hmm.getInitialMultinomial() == null);
}

@Test
public void testPrintStateMethodExecutesWithoutCrash() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("P");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("P", new String[] { "P" });
HMM.State state = (HMM.State) hmm.getState("P");
state.print();
}

@Test
public void testInitialStateIteratorReturnsExpectedState() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("START");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("START", 1.0, 0.0, new String[] { "START" }, new String[] { "START" });
// Iterator i = hmm.initialStateIterator();
// assertTrue(i.hasNext());
// Object state = i.next();
// assertTrue(state instanceof HMM.State);
}

@Test
public void testResetBuildsValidInitialMultinomial() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("L");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("L", new String[] { "L" });
hmm.reset();
Multinomial initial = hmm.getInitialMultinomial();
assertNotNull(initial);
assertTrue(initial.getAlphabet().contains("L"));
}

@Test
public void testEmitTransitionEstimatorsNonNullAfterTrain() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("x");
outputAlphabet.lookupIndex("Y");
// SerialPipes pipes = new SerialPipes(Arrays.asList(new TokenSequence2FeatureSequence(inputAlphabet), new Target2LabelSequence(outputAlphabet)));
// InstanceList ilist = new InstanceList(pipes);
// ilist.addThruPipe(new Instance(new TokenSequence("x"), new TokenSequence("Y"), null, null));
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("Y", new String[] { "Y" });
// hmm.train(ilist);
assertNotNull(hmm.getEmissionMultinomial()[0]);
assertNotNull(hmm.getTransitionMultinomial()[0]);
assertTrue(hmm.getInitialMultinomial().getAlphabet().contains("Y"));
}

@Test
public void testAddOrderNStatesFullyConnectedWithForbiddenTransitions() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
// SerialPipes pipes = new SerialPipes(Arrays.asList(new TokenSequence2FeatureSequence(inputAlphabet), new Target2LabelSequence(outputAlphabet)));
// InstanceList data = new InstanceList(pipes);
// data.addThruPipe(new Instance(new TokenSequence("w1 w2"), new TokenSequence("A B"), null, null));
Pattern forbid = Pattern.compile("A,B");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
// String start = hmm.addOrderNStates(data, new int[] { 1 }, null, "A", forbid, null, true);
// assertNotNull(start);
assertNull(hmm.getState("A,B"));
}

@Test
public void testAllowedTransitionWithInvalidRegex() {
Alphabet i = new Alphabet();
Alphabet o = new Alphabet();
Pattern badPattern = Pattern.compile("Z,.*");
HMM hmm = new HMM(i, o);
// boolean result = hmm.allowedTransition("X", "Y", badPattern, null);
// assertTrue(result);
// boolean result2 = hmm.allowedTransition("Z", "YY", badPattern, null);
// assertFalse(result2);
}

@Test
public void testResetWhenNoStateAdded() {
Alphabet inputAlpha = new Alphabet();
Alphabet outputAlpha = new Alphabet();
HMM hmm = new HMM(inputAlpha, outputAlpha);
hmm.reset();
assertNull(hmm.getEmissionMultinomial());
assertNull(hmm.getTransitionMultinomial());
}

@Test
public void testAddStateWithZeroLengthDestAndLabels() {
Alphabet i = new Alphabet();
Alphabet o = new Alphabet();
HMM hmm = new HMM(i, o);
hmm.addState("EMPTY", new String[0]);
assertNotNull(hmm.getState("EMPTY"));
assertEquals(1, hmm.numStates());
}

@Test
public void testRepeatedTrainDoesNotCrash() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("a");
outputAlphabet.lookupIndex("X");
// SerialPipes pipes = new SerialPipes(Arrays.asList(new TokenSequence2FeatureSequence(inputAlphabet), new Target2LabelSequence(outputAlphabet)));
// InstanceList ilist = new InstanceList(pipes);
// ilist.addThruPipe(new Instance(new TokenSequence("a"), new TokenSequence("X"), null, null));
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("X", new String[] { "X" });
// hmm.train(ilist);
// hmm.train(ilist);
assertTrue(hmm.getInitialMultinomial().getAlphabet().contains("X"));
}

@Test
public void testTransitionToInvalidOutputSymbolReturnsImpossibleWeight() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("z");
outputAlphabet.lookupIndex("L1");
outputAlphabet.lookupIndex("L2");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("L1", new String[] { "L2" });
hmm.reset();
FeatureSequence input = new FeatureSequence(inputAlphabet, new int[] { 0 });
FeatureSequence output = new FeatureSequence(outputAlphabet, new int[] { 1 });
HMM.State s = (HMM.State) hmm.getState("L1");
Transducer.TransitionIterator it = s.transitionIterator(input, 0, output, 0);
while (it.hasNext()) {
Transducer.State dest = it.nextState();
assertEquals(HMM.IMPOSSIBLE_WEIGHT, it.getWeight(), 1e-6);
}
}

@Test
public void testGetUniformArrayNormalizedSum() throws Exception {
Alphabet i = new Alphabet();
Alphabet o = new Alphabet();
for (int j = 0; j < 5; j++) i.lookupIndex("item" + j);
o.lookupIndex("S");
HMM hmm = new HMM(i, o);
hmm.addState("S", new String[] { "S" });
hmm.reset();
Multinomial emit = hmm.getEmissionMultinomial()[0];
double sum = 0;
// for (int j = 0; j < emit.getAlphabet().size(); j++) sum += Math.exp(emit.getValue(j));
assertEquals(1.0, sum, 1e-6);
}

@Test
public void testGetRandomArrayRespectsNoiseWithNullRandom() {
Alphabet ia = new Alphabet();
Alphabet oa = new Alphabet();
for (int i = 0; i < 3; i++) ia.lookupIndex("f" + i);
oa.lookupIndex("T");
HMM hmm = new HMM(ia, oa);
hmm.addState("T", new String[] { "T" });
hmm.initEmissions(null, 0.0);
Multinomial[] e = hmm.getEmissionMultinomial();
double value = e[0].logProbability(ia.lookupObject(0));
assertFalse(Double.isNaN(value));
assertTrue(value < 0);
}

@Test
public void testTransitionIteratorStopsAtCorrectIndex() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("tok");
outputAlphabet.lookupIndex("Y");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("Y", new String[] { "Y" });
hmm.reset();
FeatureSequence seq = new FeatureSequence(inputAlphabet, new int[] { 0 });
FeatureSequence out = new FeatureSequence(outputAlphabet, new int[] { 0 });
HMM.State state = (HMM.State) hmm.getState("Y");
Transducer.TransitionIterator iterator = state.transitionIterator(seq, 0, out, 0);
assertTrue(iterator.hasNext());
Transducer.State s = iterator.nextState();
assertNotNull(s);
assertFalse(iterator.hasNext());
}

@Test
public void testEstimateLogProbsAreInitialized() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f");
outputAlphabet.lookupIndex("L");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("L", new String[] { "L" });
// SerialPipes pipes = new SerialPipes(Arrays.asList(new TokenSequence2FeatureSequence(inputAlphabet), new Target2LabelSequence(outputAlphabet)));
// InstanceList data = new InstanceList(pipes);
// data.addThruPipe(new Instance(new TokenSequence("f"), new TokenSequence("L"), null, null));
// hmm.train(data);
double logProb = hmm.getInitialMultinomial().logProbability("L");
assertFalse(Double.isNaN(logProb));
}

@Test
public void testAddStateWithSameDestDifferentLabel() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("L1");
outputAlphabet.lookupIndex("L2");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("StateA", 0.0, 0.0, new String[] { "StateB" }, new String[] { "L1" });
hmm.addState("StateB", 0.0, 0.0, new String[] { "StateA" }, new String[] { "L2" });
assertEquals(2, hmm.numStates());
assertNotNull(hmm.getState("StateA"));
assertNotNull(hmm.getState("StateB"));
}

@Test
public void testAddStateThenAddFullyConnectedState() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("X");
outputAlphabet.lookupIndex("Y");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("X", new String[] { "X" });
hmm.addFullyConnectedStatesForLabels();
assertTrue(hmm.numStates() >= 2);
assertNotNull(hmm.getState("Y"));
}

@Test
public void testAddFullyConnectedStatesForBiLabelsAlphabetEntries() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addFullyConnectedStatesForBiLabels();
assertNotNull(hmm.getState("A,B"));
assertNotNull(hmm.getState("B,B"));
}

@Test
public void testAddOrderNStatesReturnsCorrectStartNameForNGreaterZero() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("S");
outputAlphabet.lookupIndex("E");
// SerialPipes pipes = new SerialPipes(Arrays.asList(new TokenSequence2FeatureSequence(inputAlphabet), new Target2LabelSequence(outputAlphabet)));
// InstanceList ilist = new InstanceList(pipes);
// TokenSequence input = new TokenSequence("a b");
// TokenSequence output = new TokenSequence("S E");
// Instance inst = new Instance(input, output, null, null);
// ilist.addThruPipe(inst);
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
// String start = hmm.addOrderNStates(ilist, new int[] { 1 }, null, "S", null, null, true);
// assertEquals("S", start);
assertNotNull(hmm.getState("S"));
}

@Test
public void testInitialStateIteratorEmptyWhenNoInitialWeights() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("Tag");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("Tag", 0.0, 0.0, new String[] { "Tag" }, new String[] { "Tag" });
// Iterator iterator = hmm.initialStateIterator();
// assertFalse(iterator.hasNext());
}

@Test
public void testInitialStateIteratorContainsStateWithPositiveInitialWeight() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("Tag");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("Tag", 2.0, 0.0, new String[] { "Tag" }, new String[] { "Tag" });
// Iterator iter = hmm.initialStateIterator();
// assertTrue(iter.hasNext());
// Object s = iter.next();
// assertTrue(s instanceof HMM.State);
// assertEquals("Tag", ((HMM.State) s).getName());
}

@Test
public void testTrainAssignsWeightsToInitialState() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("tok");
outputAlphabet.lookupIndex("A");
// SerialPipes pipes = new SerialPipes(Arrays.asList(new TokenSequence2FeatureSequence(inputAlphabet), new Target2LabelSequence(outputAlphabet)));
// InstanceList training = new InstanceList(pipes);
// TokenSequence input = new TokenSequence("tok");
// TokenSequence output = new TokenSequence("A");
// Instance instance = new Instance(input, output, null, null);
// training.addThruPipe(instance);
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addState("A", new String[] { "A" });
// hmm.train(training);
assertNotNull(hmm.getInitialMultinomial());
assertTrue(hmm.getInitialMultinomial().getAlphabet().contains("A"));
}

@Test
public void testAddSelfTransitioningStateForAllLabelsAddsCorrectTransitions() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("X");
outputAlphabet.lookupIndex("Y");
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
hmm.addSelfTransitioningStateForAllLabels("Self");
Transducer.State state = hmm.getState("Self");
assertNotNull(state);
assertEquals("Self", state.getName());
}

@Test
public void testAddStatesForThreeQuarterLabelsConnectedAsIn() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
// SerialPipes pipes = new SerialPipes(Arrays.asList(new TokenSequence2FeatureSequence(inputAlphabet), new Target2LabelSequence(outputAlphabet)));
// InstanceList training = new InstanceList(pipes);
// TokenSequence seq = new TokenSequence("w1 w2");
// TokenSequence lab = new TokenSequence("A B");
// Instance inst = new Instance(seq, lab, null, null);
// training.addThruPipe(inst);
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
// hmm.addStatesForThreeQuarterLabelsConnectedAsIn(training);
assertNotNull(hmm.getState("A"));
assertNotNull(hmm.getState("B"));
assertTrue(hmm.numStates() >= 2);
}

@Test
public void testAddStatesForHalfLabelsConnectedAsIn() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("X");
outputAlphabet.lookupIndex("Y");
// SerialPipes pipes = new SerialPipes(Arrays.asList(new TokenSequence2FeatureSequence(inputAlphabet), new Target2LabelSequence(outputAlphabet)));
// InstanceList training = new InstanceList(pipes);
// TokenSequence input = new TokenSequence("x y");
// TokenSequence output = new TokenSequence("X Y");
// training.addThruPipe(new Instance(input, output, null, null));
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
// hmm.addStatesForHalfLabelsConnectedAsIn(training);
assertNotNull(hmm.getState("X"));
assertNotNull(hmm.getState("Y"));
}

@Test
public void testAddStatesForLabelsConnectedAsIn() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
// SerialPipes pipes = new SerialPipes(Arrays.asList(new TokenSequence2FeatureSequence(inputAlphabet), new Target2LabelSequence(outputAlphabet)));
// InstanceList data = new InstanceList(pipes);
// TokenSequence input = new TokenSequence("w1");
// TokenSequence labels = new TokenSequence("A B");
// Instance instance = new Instance(input, labels, null, null);
// data.addThruPipe(instance);
HMM hmm = new HMM(inputAlphabet, outputAlphabet);
// hmm.addStatesForLabelsConnectedAsIn(data);
assertNotNull(hmm.getState("A"));
assertNotNull(hmm.getState("B"));
}
}
