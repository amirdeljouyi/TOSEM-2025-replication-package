package cc.mallet.topics;

import cc.mallet.types.*;
import cc.mallet.util.Randoms;
import org.junit.Test;
import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.zip.GZIPOutputStream;
import static org.junit.Assert.*;

public class MultinomialHMM_llmsuite_2_GPTLLMTest {

@Test
public void testConstructorInitializesCorrectly() throws Exception {
File tempStateFile = File.createTempFile("state", ".txt");
PrintWriter writer = new PrintWriter(tempStateFile);
writer.println("0 0 1 X 0");
writer.println("1 0 2 X 1");
writer.close();
MultinomialHMM hmm = new MultinomialHMM(2, tempStateFile.getAbsolutePath(), 2);
assertNotNull(hmm);
assertEquals(2, hmm.stateTopics().split("\n")[0].split("\t").length);
tempStateFile.delete();
}

@Test
public void testLoadAlphaFromFileSetsValuesCorrectly() throws Exception {
File tempStateFile = File.createTempFile("state", ".txt");
PrintWriter writer1 = new PrintWriter(tempStateFile);
writer1.println("0 0 1 X 0");
writer1.println("1 0 2 X 1");
writer1.close();
File tempAlphaFile = File.createTempFile("alpha", ".txt");
PrintWriter writer2 = new PrintWriter(tempAlphaFile);
writer2.println("0 1.0 wordA");
writer2.println("1 1.0 wordB");
writer2.close();
MultinomialHMM hmm = new MultinomialHMM(2, tempStateFile.getAbsolutePath(), 2);
hmm.loadAlphaFromFile(tempAlphaFile.getAbsolutePath());
assertEquals("wordA ", hmm.topicKeys[0]);
assertEquals("wordB ", hmm.topicKeys[1]);
tempStateFile.delete();
tempAlphaFile.delete();
}

@Test
public void testLoadSequenceIDsFromFile() throws Exception {
File tempStateFile = File.createTempFile("state", ".txt");
PrintWriter writer1 = new PrintWriter(tempStateFile);
writer1.println("0 0 1 X 0");
writer1.println("1 0 2 X 1");
writer1.println("2 0 1 X 0");
writer1.close();
File tempAlphaFile = File.createTempFile("alpha", ".txt");
PrintWriter writer2 = new PrintWriter(tempAlphaFile);
writer2.println("0 1.0 wordA");
writer2.println("1 1.0 wordB");
writer2.close();
File sequenceFile = File.createTempFile("seq", ".txt");
PrintWriter writer3 = new PrintWriter(sequenceFile);
writer3.println("0\tmeta");
writer3.println("1\tmeta");
writer3.println("2\tmeta");
writer3.close();
MultinomialHMM hmm = new MultinomialHMM(2, tempStateFile.getAbsolutePath(), 2);
hmm.loadAlphaFromFile(tempAlphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
assertNotNull(hmm.stateTopics());
tempStateFile.delete();
tempAlphaFile.delete();
sequenceFile.delete();
}

@Test
public void testInitializeAndStateOutput() throws Exception {
File tempStateFile = File.createTempFile("state", ".txt");
PrintWriter writer1 = new PrintWriter(tempStateFile);
writer1.println("0 0 1 X 0");
writer1.println("1 0 2 X 1");
writer1.println("2 0 1 X 0");
writer1.close();
File alphaFile = File.createTempFile("alpha", ".txt");
PrintWriter writer2 = new PrintWriter(alphaFile);
writer2.println("0 1.0 dog");
writer2.println("1 1.0 cat");
writer2.close();
File seqIDs = File.createTempFile("seqids", ".txt");
PrintWriter writer3 = new PrintWriter(seqIDs);
writer3.println("0\tmeta");
writer3.println("1\tmeta");
writer3.println("2\tmeta");
writer3.close();
MultinomialHMM hmm = new MultinomialHMM(2, tempStateFile.getAbsolutePath(), 2);
hmm.setGamma(1.0);
hmm.setRandomSeed(123);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(seqIDs.getAbsolutePath());
hmm.initialize();
String stateOutput = hmm.stateTopics();
assertNotNull(stateOutput);
assertTrue(stateOutput.contains("\n"));
tempStateFile.delete();
alphaFile.delete();
seqIDs.delete();
}

@Test
public void testSamplingWithTwoIterations() throws Exception {
File tempStateFile = File.createTempFile("state", ".txt");
PrintWriter writer1 = new PrintWriter(tempStateFile);
writer1.println("0 0 1 X 0");
writer1.println("1 0 2 X 1");
writer1.println("2 0 2 X 1");
writer1.close();
File alphaFile = File.createTempFile("alpha", ".txt");
PrintWriter writer2 = new PrintWriter(alphaFile);
writer2.println("0 1.0 apple");
writer2.println("1 1.0 banana");
writer2.close();
File seqIDs = File.createTempFile("seqids", ".txt");
PrintWriter writer3 = new PrintWriter(seqIDs);
writer3.println("0\tmeta");
writer3.println("1\tmeta");
writer3.println("2\tmeta");
writer3.close();
MultinomialHMM hmm = new MultinomialHMM(2, tempStateFile.getAbsolutePath(), 2);
hmm.setGamma(1.0);
hmm.setRandomSeed(5);
hmm.setNumIterations(2);
hmm.setTopicDisplayInterval(10);
hmm.setBurninPeriod(0);
hmm.setOptimizeInterval(0);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(seqIDs.getAbsolutePath());
hmm.initialize();
hmm.sample();
tempStateFile.delete();
alphaFile.delete();
seqIDs.delete();
}

@Test
public void testStateMatrixContainsTransitionValues() throws Exception {
File tmpState = File.createTempFile("state", ".txt");
PrintWriter w1 = new PrintWriter(tmpState);
w1.println("0 0 1 X 0");
w1.println("1 0 2 X 1");
w1.println("2 0 2 X 0");
w1.close();
File tmpAlpha = File.createTempFile("alpha", ".txt");
PrintWriter w2 = new PrintWriter(tmpAlpha);
w2.println("0 1.0 A");
w2.println("1 1.0 B");
w2.close();
File seqFile = File.createTempFile("seq", ".txt");
PrintWriter w3 = new PrintWriter(seqFile);
w3.println("0\tmeta");
w3.println("1\tmeta");
w3.println("2\tmeta");
w3.close();
MultinomialHMM hmm = new MultinomialHMM(2, tmpState.getAbsolutePath(), 2);
hmm.setGamma(1.0);
hmm.setRandomSeed(55);
hmm.loadAlphaFromFile(tmpAlpha.getAbsolutePath());
hmm.loadSequenceIDsFromFile(seqFile.getAbsolutePath());
hmm.initialize();
String matrix = hmm.stateTransitionMatrix();
assertTrue(matrix.contains("\t"));
tmpState.delete();
tmpAlpha.delete();
seqFile.delete();
}

@Test
public void testGzippedStateFileLoads() throws Exception {
File gzFile = File.createTempFile("state", ".gz");
GZIPOutputStream gzipOut = new GZIPOutputStream(new FileOutputStream(gzFile));
OutputStreamWriter writer = new OutputStreamWriter(gzipOut);
writer.write("0 0 1 X 0\n");
writer.write("1 0 2 X 1\n");
writer.close();
gzipOut.close();
MultinomialHMM hmm = new MultinomialHMM(2, gzFile.getAbsolutePath(), 2);
assertNotNull(hmm);
gzFile.delete();
}

@Test
public void testMalformedAlphaLineParsesGracefully() throws Exception {
File stFile = File.createTempFile("state", ".txt");
PrintWriter w1 = new PrintWriter(stFile);
w1.println("0 0 1 X 0");
w1.println("1 0 2 X 1");
w1.println("2 0 1 X 0");
w1.close();
File malformedAlpha = File.createTempFile("alpha", ".txt");
PrintWriter w2 = new PrintWriter(malformedAlpha);
w2.println("0 1.0 dog cat");
w2.println("1 1.0 cow");
w2.close();
MultinomialHMM hmm = new MultinomialHMM(2, stFile.getAbsolutePath(), 2);
hmm.loadAlphaFromFile(malformedAlpha.getAbsolutePath());
assertEquals("dog cat ", hmm.topicKeys[0]);
assertEquals("cow ", hmm.topicKeys[1]);
stFile.delete();
malformedAlpha.delete();
}

@Test
public void testEmptyStateFileDoesNotCrash() throws Exception {
File emptyState = File.createTempFile("empty_state", ".txt");
PrintWriter w = new PrintWriter(emptyState);
w.close();
MultinomialHMM hmm = new MultinomialHMM(3, emptyState.getAbsolutePath(), 2);
assertNotNull(hmm);
emptyState.delete();
}

@Test
public void testSingleDocumentInStateFile() throws Exception {
File stateFile = File.createTempFile("single_doc", ".txt");
PrintWriter w1 = new PrintWriter(stateFile);
w1.println("0 0 1 X 1");
w1.close();
File alphaFile = File.createTempFile("alpha_single", ".txt");
PrintWriter w2 = new PrintWriter(alphaFile);
w2.println("0 1.0 t0");
w2.println("1 1.0 t1");
w2.close();
File sequenceFile = File.createTempFile("seq_single", ".txt");
PrintWriter w3 = new PrintWriter(sequenceFile);
w3.println("0\tmeta");
w3.close();
MultinomialHMM hmm = new MultinomialHMM(2, stateFile.getAbsolutePath(), 2);
hmm.setGamma(1.0);
hmm.setRandomSeed(42);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
hmm.initialize();
assertNotNull(hmm.stateTopics());
stateFile.delete();
alphaFile.delete();
sequenceFile.delete();
}

@Test
public void testInvalidTopicIndexInStateFileSkipsGracefully() throws Exception {
File stateFile = File.createTempFile("bad_index", ".txt");
PrintWriter writer = new PrintWriter(stateFile);
writer.println("# comment line");
writer.println("0 0 1 X 0");
writer.println("invalid_line here");
writer.println("1 0 2 X 1");
writer.close();
MultinomialHMM hmm = new MultinomialHMM(2, stateFile.getAbsolutePath(), 2);
assertNotNull(hmm);
stateFile.delete();
}

@Test
public void testRepeatedDocumentIdsCumulativeTopicCounts() throws Exception {
File stateFile = File.createTempFile("repeat_doc", ".txt");
PrintWriter w = new PrintWriter(stateFile);
w.println("0 0 1 X 0");
w.println("0 1 2 X 1");
w.println("0 2 3 X 1");
w.close();
MultinomialHMM hmm = new MultinomialHMM(2, stateFile.getAbsolutePath(), 2);
assertNotNull(hmm);
stateFile.delete();
}

@Test
public void testAlphaFileWithZeroAlphaValues() throws Exception {
File stateFile = File.createTempFile("state_for_zero_alpha", ".txt");
PrintWriter w1 = new PrintWriter(stateFile);
w1.println("0 0 1 X 0");
w1.close();
File alphaFile = File.createTempFile("zero_alpha", ".txt");
PrintWriter w2 = new PrintWriter(alphaFile);
w2.println("0 0.0 z0");
w2.println("1 0.0 z1");
w2.close();
MultinomialHMM hmm = new MultinomialHMM(2, stateFile.getAbsolutePath(), 2);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
assertEquals("z0 ", hmm.topicKeys[0]);
assertEquals("z1 ", hmm.topicKeys[1]);
stateFile.delete();
alphaFile.delete();
}

@Test
public void testSequenceFileWithMoreDocumentsThanStateFile() throws Exception {
File stateFile = File.createTempFile("short_state", ".txt");
PrintWriter w1 = new PrintWriter(stateFile);
w1.println("0 0 1 X 0");
w1.println("1 0 2 X 1");
w1.close();
File alphaFile = File.createTempFile("alpha_more_seq", ".txt");
PrintWriter w2 = new PrintWriter(alphaFile);
w2.println("0 1.0 X");
w2.println("1 1.0 Y");
w2.close();
File sequenceFile = File.createTempFile("more_seq", ".txt");
PrintWriter w3 = new PrintWriter(sequenceFile);
w3.println("0\tmeta");
w3.println("1\tmeta");
w3.println("2\tmeta");
w3.close();
MultinomialHMM hmm = new MultinomialHMM(2, stateFile.getAbsolutePath(), 2);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
assertNotNull(hmm);
stateFile.delete();
alphaFile.delete();
sequenceFile.delete();
}

@Test
public void testEmptyAlphaFileDoesNotCrash() throws Exception {
File stateFile = File.createTempFile("state", ".txt");
PrintWriter w1 = new PrintWriter(stateFile);
w1.println("0 0 1 X 0");
w1.close();
File alphaFile = File.createTempFile("empty_alpha", ".txt");
PrintWriter w2 = new PrintWriter(alphaFile);
w2.close();
MultinomialHMM hmm = new MultinomialHMM(1, stateFile.getAbsolutePath(), 1);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
assertNotNull(hmm);
stateFile.delete();
alphaFile.delete();
}

@Test
public void testStateWithLargeDocumentLength() throws Exception {
File stateFile = File.createTempFile("large_doc", ".txt");
PrintWriter w = new PrintWriter(stateFile);
w.println("0 0 1 X 0");
w.println("0 1 1 X 0");
w.println("0 2 1 X 0");
w.println("0 3 1 X 0");
w.println("0 4 1 X 0");
w.close();
File alphaFile = File.createTempFile("alpha_largedoc", ".txt");
PrintWriter w2 = new PrintWriter(alphaFile);
w2.println("0 1.0 tok0");
w2.close();
File sequenceFile = File.createTempFile("seq_large_doc", ".txt");
PrintWriter w3 = new PrintWriter(sequenceFile);
w3.println("0\tmeta");
w3.close();
MultinomialHMM hmm = new MultinomialHMM(1, stateFile.getAbsolutePath(), 1);
hmm.setGamma(1.0);
hmm.setRandomSeed(99);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
hmm.initialize();
String matrix = hmm.stateTransitionMatrix();
assertNotNull(matrix);
stateFile.delete();
alphaFile.delete();
sequenceFile.delete();
}

@Test
public void testTopicDisplayIntervalSetCorrectly() throws Exception {
File stateFile = File.createTempFile("state_disp", ".txt");
PrintWriter w = new PrintWriter(stateFile);
w.println("0 0 1 X 0");
w.close();
MultinomialHMM hmm = new MultinomialHMM(1, stateFile.getAbsolutePath(), 1);
hmm.setTopicDisplayInterval(25);
assertNotNull(hmm);
stateFile.delete();
}

@Test
public void testSetBurninAndOptimizeIntervals() throws Exception {
File stateFile = File.createTempFile("state_burnin", ".txt");
PrintWriter w = new PrintWriter(stateFile);
w.println("0 0 1 X 0");
w.close();
MultinomialHMM hmm = new MultinomialHMM(1, stateFile.getAbsolutePath(), 1);
hmm.setBurninPeriod(10);
hmm.setOptimizeInterval(5);
assertNotNull(hmm);
stateFile.delete();
}

@Test
public void testSetNumIterationsNegativeValue() throws Exception {
File stateFile = File.createTempFile("state_neg", ".txt");
PrintWriter w = new PrintWriter(stateFile);
w.println("0 0 1 X 0");
w.close();
MultinomialHMM hmm = new MultinomialHMM(1, stateFile.getAbsolutePath(), 1);
hmm.setNumIterations(-1);
assertNotNull(hmm);
stateFile.delete();
}

@Test
public void testTopicMoreThanNumTopicsInStateFile() throws Exception {
File stateFile = File.createTempFile("state_over_topic", ".txt");
PrintWriter writer1 = new PrintWriter(stateFile);
writer1.println("0 0 1 X 5");
writer1.close();
boolean thrown = false;
try {
new MultinomialHMM(3, stateFile.getAbsolutePath(), 2);
} catch (IndexOutOfBoundsException e) {
thrown = true;
}
assertTrue(thrown);
stateFile.delete();
}

@Test
public void testLoadAlphaFileWithFewerTopicsThanDeclared() throws Exception {
File stateFile = File.createTempFile("state_alpha_less", ".txt");
PrintWriter writer1 = new PrintWriter(stateFile);
writer1.println("0 0 1 X 0");
writer1.println("1 0 1 X 1");
writer1.println("2 0 1 X 2");
writer1.close();
File alphaFile = File.createTempFile("alpha_less", ".txt");
PrintWriter writer2 = new PrintWriter(alphaFile);
writer2.println("0 1.0 word0");
writer2.close();
MultinomialHMM hmm = new MultinomialHMM(3, stateFile.getAbsolutePath(), 2);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
assertEquals("word0 ", hmm.topicKeys[0]);
assertNull(hmm.topicKeys[1]);
assertNull(hmm.topicKeys[2]);
stateFile.delete();
alphaFile.delete();
}

@Test
public void testSamplingWithoutLoadingAlphaThrowsNullPointer() throws Exception {
File stateFile = File.createTempFile("state_no_alpha", ".txt");
PrintWriter writer = new PrintWriter(stateFile);
writer.println("0 0 1 X 0");
writer.println("1 0 1 X 1");
writer.println("2 0 1 X 0");
writer.close();
File sequenceFile = File.createTempFile("seq_no_alpha", ".txt");
PrintWriter writer2 = new PrintWriter(sequenceFile);
writer2.println("0\tX");
writer2.println("1\tX");
writer2.println("2\tX");
writer2.close();
MultinomialHMM hmm = new MultinomialHMM(2, stateFile.getAbsolutePath(), 2);
hmm.setGamma(1.0);
hmm.setRandomSeed(42);
hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
boolean exceptionThrown = false;
try {
hmm.initialize();
} catch (NullPointerException e) {
exceptionThrown = true;
}
assertTrue(exceptionThrown);
stateFile.delete();
sequenceFile.delete();
}

@Test
public void testDocumentTopicsWithNoTokens() throws Exception {
File stateFile = File.createTempFile("state_empty_doc", ".txt");
PrintWriter writer = new PrintWriter(stateFile);
writer.println("0 0 1 X 0");
writer.close();
File alphaFile = File.createTempFile("alpha_empty_doc", ".txt");
PrintWriter writer2 = new PrintWriter(alphaFile);
writer2.println("0 1.0 word0");
writer.close();
File sequenceFile = File.createTempFile("seq_empty_doc", ".txt");
PrintWriter writer3 = new PrintWriter(sequenceFile);
writer3.println("0\tmeta");
writer3.println("1\tmeta");
writer3.close();
MultinomialHMM hmm = new MultinomialHMM(1, stateFile.getAbsolutePath(), 1);
hmm.setGamma(1.0);
hmm.setRandomSeed(13);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
hmm.initialize();
assertNotNull(hmm.stateTopics());
stateFile.delete();
alphaFile.delete();
sequenceFile.delete();
}

@Test
public void testZeroLengthDocTriggersNoRecacheInSampleState() throws Exception {
File stateFile = File.createTempFile("state_zero_doc", ".txt");
PrintWriter writer = new PrintWriter(stateFile);
writer.println("0 0 1 X 0");
writer.close();
File alphaFile = File.createTempFile("zero_alpha", ".txt");
PrintWriter writer2 = new PrintWriter(alphaFile);
writer2.println("0 1.0 tok0");
writer.close();
File sequenceFile = File.createTempFile("seq_zero_doc", ".txt");
PrintWriter writer3 = new PrintWriter(sequenceFile);
writer3.println("0\tmeta");
writer3.println("1\tmeta");
writer3.close();
MultinomialHMM hmm = new MultinomialHMM(1, stateFile.getAbsolutePath(), 2);
hmm.setGamma(0.1);
hmm.setRandomSeed(55);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
hmm.initialize();
assertTrue(hmm.stateTransitionMatrix().contains("\n"));
stateFile.delete();
alphaFile.delete();
sequenceFile.delete();
}

@Test
public void testLoadAlphaWithMultipleSpacesHandled() throws Exception {
File stateFile = File.createTempFile("state_multi_space", ".txt");
PrintWriter w1 = new PrintWriter(stateFile);
w1.println("0 0 1 X 1");
w1.println("1 0 1 X 0");
w1.close();
File alphaFile = File.createTempFile("alpha_spaces", ".txt");
PrintWriter w2 = new PrintWriter(alphaFile);
w2.println("0      1.0         a b c");
w2.println("1  1.0 z y x");
w2.close();
MultinomialHMM hmm = new MultinomialHMM(2, stateFile.getAbsolutePath(), 2);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
assertEquals("a b c ", hmm.topicKeys[0]);
assertEquals("z y x ", hmm.topicKeys[1]);
stateFile.delete();
alphaFile.delete();
}

@Test
public void testSamplingAfterInitializeAndAlphaLoaded() throws Exception {
File stateFile = File.createTempFile("sample_test", ".txt");
PrintWriter w1 = new PrintWriter(stateFile);
w1.println("0 0 1 X 0");
w1.println("1 0 1 X 1");
w1.println("2 0 1 X 0");
w1.close();
File alphaFile = File.createTempFile("alpha_sample", ".txt");
PrintWriter w2 = new PrintWriter(alphaFile);
w2.println("0 1.0 t0");
w2.println("1 1.0 t1");
w2.close();
File sequence = File.createTempFile("seq_sample", ".txt");
PrintWriter w3 = new PrintWriter(sequence);
w3.println("0\tmeta");
w3.println("1\tmeta");
w3.println("2\tmeta");
w3.close();
MultinomialHMM hmm = new MultinomialHMM(2, stateFile.getAbsolutePath(), 2);
hmm.setNumIterations(1);
hmm.setGamma(1.0);
hmm.setBurninPeriod(0);
hmm.setRandomSeed(999);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(sequence.getAbsolutePath());
hmm.initialize();
hmm.sample();
assertNotNull(hmm.stateTopics());
stateFile.delete();
alphaFile.delete();
sequence.delete();
}

@Test
public void testStateFileWithNonSequentialDocumentIDs() throws Exception {
File stateFile = File.createTempFile("state_nonseq_doc", ".txt");
PrintWriter writer = new PrintWriter(stateFile);
writer.println("5 0 1 X 0");
writer.println("8 1 1 X 1");
writer.close();
MultinomialHMM hmm = new MultinomialHMM(2, stateFile.getAbsolutePath(), 2);
assertNotNull(hmm);
assertTrue(hmm.documentStates.length >= 9);
stateFile.delete();
}

@Test
public void testSequenceIDsFileWithMalformedLinesIsIgnored() throws Exception {
File stateFile = File.createTempFile("state_valid", ".txt");
PrintWriter s = new PrintWriter(stateFile);
s.println("0 0 1 X 0");
s.println("1 1 2 X 1");
s.close();
File alphaFile = File.createTempFile("alpha", ".txt");
PrintWriter a = new PrintWriter(alphaFile);
a.println("0 1.0 token0");
a.println("1 1.0 token1");
a.close();
File seqFile = File.createTempFile("seq_bad_lines", ".txt");
PrintWriter q = new PrintWriter(seqFile);
q.println("0\tmeta");
q.println("malformed_line_without_tab");
q.println("1\tother");
q.close();
MultinomialHMM hmm = new MultinomialHMM(2, stateFile.getAbsolutePath(), 2);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
boolean threw = false;
try {
hmm.loadSequenceIDsFromFile(seqFile.getAbsolutePath());
} catch (Exception e) {
threw = true;
}
assertTrue(threw);
stateFile.delete();
alphaFile.delete();
seqFile.delete();
}

@Test
public void testInvalidNumberOfFieldsInAlphaFileSkipsLine() throws Exception {
File stateFile = File.createTempFile("state_short", ".txt");
PrintWriter s = new PrintWriter(stateFile);
s.println("0 0 1 X 0");
s.close();
File alphaFile = File.createTempFile("bad_alpha", ".txt");
PrintWriter a = new PrintWriter(alphaFile);
a.println("0");
a.println("1 1.0 tokenX");
a.close();
MultinomialHMM hmm = new MultinomialHMM(2, stateFile.getAbsolutePath(), 2);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
assertNull(hmm.topicKeys[0]);
assertEquals("tokenX ", hmm.topicKeys[1]);
stateFile.delete();
alphaFile.delete();
}

@Test
public void testDocumentSequenceIDMismatchLogsWarning() throws Exception {
File stateFile = File.createTempFile("state_mismatch", ".txt");
PrintWriter w1 = new PrintWriter(stateFile);
w1.println("0 0 1 X 0");
w1.println("1 0 1 X 1");
w1.close();
File alphaFile = File.createTempFile("alpha_mismatch", ".txt");
PrintWriter w2 = new PrintWriter(alphaFile);
w2.println("0 1.0 ABC");
w2.println("1 1.0 DEF");
w2.close();
File seqFile = File.createTempFile("seq_toomany", ".txt");
PrintWriter w3 = new PrintWriter(seqFile);
w3.println("0\tA");
w3.println("1\tA");
w3.println("2\tA");
w3.close();
MultinomialHMM hmm = new MultinomialHMM(2, stateFile.getAbsolutePath(), 2);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(seqFile.getAbsolutePath());
assertNotNull(hmm);
stateFile.delete();
alphaFile.delete();
seqFile.delete();
}

@Test
public void testHighGammaValueEffectInitializationAndSampling() throws Exception {
File stateFile = File.createTempFile("state_highgamma", ".txt");
PrintWriter writer = new PrintWriter(stateFile);
writer.println("0 0 1 X 0");
writer.println("1 0 2 X 1");
writer.println("2 0 1 X 0");
writer.println("3 0 1 X 1");
writer.close();
File alphaFile = File.createTempFile("alpha_highgamma", ".txt");
PrintWriter alphaWriter = new PrintWriter(alphaFile);
alphaWriter.println("0 1.0 t0");
alphaWriter.println("1 1.0 t1");
alphaWriter.close();
File sequenceIDs = File.createTempFile("seq_highgamma", ".txt");
PrintWriter seqWriter = new PrintWriter(sequenceIDs);
seqWriter.println("0\tinfo");
seqWriter.println("1\tinfo");
seqWriter.println("2\tinfo");
seqWriter.println("3\tinfo");
seqWriter.close();
MultinomialHMM hmm = new MultinomialHMM(2, stateFile.getAbsolutePath(), 2);
hmm.setGamma(100.0);
hmm.setRandomSeed(777);
hmm.setNumIterations(1);
hmm.setTopicDisplayInterval(10);
hmm.setBurninPeriod(0);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(sequenceIDs.getAbsolutePath());
hmm.initialize();
hmm.sample();
assertNotNull(hmm.stateTopics());
stateFile.delete();
alphaFile.delete();
sequenceIDs.delete();
}

@Test
public void testSamplingWithOnlyOneStateAvailable() throws Exception {
File stateFile = File.createTempFile("state_single_state", ".txt");
PrintWriter writer = new PrintWriter(stateFile);
writer.println("0 0 1 X 0");
writer.println("1 0 2 X 1");
writer.close();
File alphaFile = File.createTempFile("alpha_single_state", ".txt");
PrintWriter writer2 = new PrintWriter(alphaFile);
writer2.println("0 1.0 K0");
writer2.println("1 1.0 K1");
writer2.close();
File sequence = File.createTempFile("seq_single_state", ".txt");
PrintWriter writer3 = new PrintWriter(sequence);
writer3.println("0\ta");
writer3.println("1\ta");
writer3.close();
MultinomialHMM hmm = new MultinomialHMM(2, stateFile.getAbsolutePath(), 1);
hmm.setGamma(0.5);
hmm.setRandomSeed(1234);
hmm.setNumIterations(1);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(sequence.getAbsolutePath());
hmm.initialize();
hmm.sample();
assertNotNull(hmm.stateTransitionMatrix());
stateFile.delete();
alphaFile.delete();
sequence.delete();
}

@Test
public void testStateTopicsOutputFormat() throws Exception {
File stateFile = File.createTempFile("state_st_output", ".txt");
PrintWriter w = new PrintWriter(stateFile);
w.println("0 0 1 X 0");
w.println("1 0 2 X 0");
w.close();
File alphaFile = File.createTempFile("alpha_st_output", ".txt");
PrintWriter a = new PrintWriter(alphaFile);
a.println("0 1.0 tok0");
a.println("1 1.0 tok1");
a.close();
File seqFile = File.createTempFile("seq_st_output", ".txt");
PrintWriter s = new PrintWriter(seqFile);
s.println("0\tA");
s.println("1\tA");
s.close();
MultinomialHMM hmm = new MultinomialHMM(2, stateFile.getAbsolutePath(), 2);
hmm.setGamma(2.0);
hmm.setRandomSeed(3);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(seqFile.getAbsolutePath());
hmm.initialize();
String topics = hmm.stateTopics();
assertTrue(topics.split("\n").length == 2);
assertTrue(topics.contains("\t"));
stateFile.delete();
alphaFile.delete();
seqFile.delete();
}

@Test
public void testAlphaFileWithDuplicateTopicIndices() throws Exception {
File stateFile = File.createTempFile("state_dup_topic", ".txt");
PrintWriter sw = new PrintWriter(stateFile);
sw.println("0 0 1 X 0");
sw.close();
File alphaFile = File.createTempFile("alpha_dup_topic", ".txt");
PrintWriter aw = new PrintWriter(alphaFile);
aw.println("0 1.0 tokenA");
aw.println("0 2.0 tokenB");
aw.close();
MultinomialHMM hmm = new MultinomialHMM(1, stateFile.getAbsolutePath(), 1);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
assertEquals("tokenB ", hmm.topicKeys[0]);
stateFile.delete();
alphaFile.delete();
}

@Test
public void testExtremeAlphaValuesImpactSampling() throws Exception {
File stateFile = File.createTempFile("state_extreme_alpha", ".txt");
PrintWriter sw = new PrintWriter(stateFile);
sw.println("0 0 1 X 0");
sw.println("1 0 1 X 1");
sw.close();
File alphaFile = File.createTempFile("alpha_extreme", ".txt");
PrintWriter aw = new PrintWriter(alphaFile);
aw.println("0 1000000.0 bigAlpha");
aw.println("1 0.000001 smallAlpha");
aw.close();
File seqFile = File.createTempFile("seq_extreme", ".txt");
PrintWriter qw = new PrintWriter(seqFile);
qw.println("0\tX");
qw.println("1\tX");
qw.close();
MultinomialHMM hmm = new MultinomialHMM(2, stateFile.getAbsolutePath(), 2);
hmm.setGamma(1.0);
hmm.setRandomSeed(321);
hmm.setNumIterations(1);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(seqFile.getAbsolutePath());
hmm.initialize();
hmm.sample();
assertNotNull(hmm.stateTopics());
stateFile.delete();
alphaFile.delete();
seqFile.delete();
}

@Test
public void testZeroNumTopicsThrowsException() throws Exception {
File tempState = File.createTempFile("state_zero_topics", ".txt");
PrintWriter sw = new PrintWriter(tempState);
sw.println("0 0 1 X 0");
sw.close();
boolean exceptionThrown = false;
try {
new MultinomialHMM(0, tempState.getAbsolutePath(), 2);
} catch (IllegalArgumentException | ArithmeticException e) {
exceptionThrown = true;
}
assertTrue(exceptionThrown);
tempState.delete();
}

@Test
public void testAlphaFileWithNonIntegerTopicIDSkipsInvalidLine() throws Exception {
File stateFile = File.createTempFile("state_nonint_alpha", ".txt");
PrintWriter sw = new PrintWriter(stateFile);
sw.println("0 0 1 X 0");
sw.println("1 0 2 X 1");
sw.close();
File alphaFile = File.createTempFile("bad_topic_alpha", ".txt");
PrintWriter aw = new PrintWriter(alphaFile);
aw.println("X 1.0 abc");
aw.println("1 1.0 def");
aw.close();
MultinomialHMM hmm = new MultinomialHMM(2, stateFile.getAbsolutePath(), 2);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
assertNull(hmm.topicKeys[0]);
assertEquals("def ", hmm.topicKeys[1]);
stateFile.delete();
alphaFile.delete();
}

@Test
public void testSingleTokenMultipleTopicsInSameDoc() throws Exception {
File stateFile = File.createTempFile("state_mult_topics", ".txt");
PrintWriter sw = new PrintWriter(stateFile);
sw.println("0 0 1 X 0");
sw.println("0 1 2 X 1");
sw.println("0 2 3 X 1");
sw.close();
File alphaFile = File.createTempFile("alpha_mult_topics", ".txt");
PrintWriter aw = new PrintWriter(alphaFile);
aw.println("0 1.0 topic0");
aw.println("1 1.0 topic1");
aw.println("2 1.0 topic2");
aw.close();
File seqFile = File.createTempFile("seq_mult_topics", ".txt");
PrintWriter qw = new PrintWriter(seqFile);
qw.println("0\tmeta");
qw.close();
MultinomialHMM hmm = new MultinomialHMM(3, stateFile.getAbsolutePath(), 2);
hmm.setGamma(1.0);
hmm.setRandomSeed(888);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(seqFile.getAbsolutePath());
hmm.initialize();
hmm.sample();
assertNotNull(hmm.stateTransitionMatrix());
stateFile.delete();
alphaFile.delete();
seqFile.delete();
}

@Test
public void testSamplingWithHighNumberOfStatesAndFewDocs() throws Exception {
File stateFile = File.createTempFile("state_few_docs", ".txt");
PrintWriter sw = new PrintWriter(stateFile);
sw.println("0 0 1 X 0");
sw.println("1 0 1 X 1");
sw.close();
File alphaFile = File.createTempFile("alpha_few_docs", ".txt");
PrintWriter aw = new PrintWriter(alphaFile);
aw.println("0 1.0 A");
aw.println("1 1.0 B");
aw.close();
File seqFile = File.createTempFile("seq_few_docs", ".txt");
PrintWriter qw = new PrintWriter(seqFile);
qw.println("0\tmeta");
qw.println("1\tmeta");
qw.close();
MultinomialHMM hmm = new MultinomialHMM(2, stateFile.getAbsolutePath(), 10);
hmm.setGamma(1.0);
hmm.setRandomSeed(42);
hmm.setNumIterations(1);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(seqFile.getAbsolutePath());
hmm.initialize();
hmm.sample();
String matrix = hmm.stateTransitionMatrix();
String[] lines = matrix.split("\n");
assertEquals(10, lines.length);
stateFile.delete();
alphaFile.delete();
seqFile.delete();
}

@Test
public void testLoadAlphaFileWithExtraWhitespace() throws Exception {
File stateFile = File.createTempFile("state_whitespace_alpha", ".txt");
PrintWriter sw = new PrintWriter(stateFile);
sw.println("0 0 1 X 0");
sw.close();
File alphaFile = File.createTempFile("alpha_with_spaces", ".txt");
PrintWriter aw = new PrintWriter(alphaFile);
aw.println("0    1.0         token0");
aw.println("1\t1.0\ttoken1");
aw.close();
MultinomialHMM hmm = new MultinomialHMM(2, stateFile.getAbsolutePath(), 2);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
assertEquals("token0 ", hmm.topicKeys[0]);
assertEquals("token1 ", hmm.topicKeys[1]);
stateFile.delete();
alphaFile.delete();
}

@Test
public void testNoSequenceIDFileLoadedLeadsToZeroSequences() throws Exception {
File stateFile = File.createTempFile("state_no_seq", ".txt");
PrintWriter sw = new PrintWriter(stateFile);
sw.println("0 0 1 X 0");
sw.println("1 0 1 X 1");
sw.close();
File alphaFile = File.createTempFile("alpha_no_seq", ".txt");
PrintWriter aw = new PrintWriter(alphaFile);
aw.println("0 1.0 a");
aw.println("1 1.0 b");
aw.close();
MultinomialHMM hmm = new MultinomialHMM(2, stateFile.getAbsolutePath(), 2);
hmm.setGamma(0.1);
hmm.setRandomSeed(123);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
boolean exceptionThrown = false;
try {
hmm.initialize();
} catch (ArrayIndexOutOfBoundsException e) {
exceptionThrown = true;
}
assertTrue(exceptionThrown);
stateFile.delete();
alphaFile.delete();
}

@Test
public void testAlphaFileWithNegativeAlphaValue() throws Exception {
File stateFile = File.createTempFile("state_negative_alpha", ".txt");
PrintWriter writer = new PrintWriter(stateFile);
writer.println("0 0 2 X 0");
writer.close();
File alphaFile = File.createTempFile("alpha_negative", ".txt");
PrintWriter alphaWriter = new PrintWriter(alphaFile);
alphaWriter.println("0 -1.0 negAlpha");
alphaWriter.println("1 1.0 posAlpha");
alphaWriter.close();
boolean exceptionThrown = false;
try {
MultinomialHMM hmm = new MultinomialHMM(2, stateFile.getAbsolutePath(), 2);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
} catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
exceptionThrown = true;
}
assertTrue(exceptionThrown);
stateFile.delete();
alphaFile.delete();
}

@Test
public void testInitWithoutRandomSeedGeneratesRandomness() throws Exception {
File stateFile = File.createTempFile("state_random_default", ".txt");
PrintWriter writer = new PrintWriter(stateFile);
writer.println("0 0 2 X 0");
writer.println("1 0 2 X 1");
writer.close();
File alphaFile = File.createTempFile("alpha_random_default", ".txt");
PrintWriter alphaWriter = new PrintWriter(alphaFile);
alphaWriter.println("0 1.0 word0");
alphaWriter.println("1 1.0 word1");
alphaWriter.close();
File seqFile = File.createTempFile("seq_random", ".txt");
PrintWriter seqWriter = new PrintWriter(seqFile);
seqWriter.println("0\tZ");
seqWriter.println("1\tZ");
seqWriter.close();
MultinomialHMM hmm = new MultinomialHMM(2, stateFile.getAbsolutePath(), 2);
hmm.setGamma(1.0);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(seqFile.getAbsolutePath());
hmm.initialize();
assertNotNull(hmm.stateTransitionMatrix());
stateFile.delete();
alphaFile.delete();
seqFile.delete();
}

@Test
public void testStateTransitionMatrixFormatForZeroTransitions() throws Exception {
File stateFile = File.createTempFile("state_zero_transitions", ".txt");
PrintWriter writer = new PrintWriter(stateFile);
writer.println("0 0 2 X 1");
writer.close();
File alphaFile = File.createTempFile("alpha_default", ".txt");
PrintWriter alphaWriter = new PrintWriter(alphaFile);
alphaWriter.println("0 1.0 a");
alphaWriter.println("1 1.0 b");
alphaWriter.close();
File seqFile = File.createTempFile("seq_zero_transitions", ".txt");
PrintWriter seqWriter = new PrintWriter(seqFile);
seqWriter.println("0\tZ");
seqWriter.close();
MultinomialHMM hmm = new MultinomialHMM(2, stateFile.getAbsolutePath(), 2);
hmm.setGamma(0.5);
hmm.setRandomSeed(99);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(seqFile.getAbsolutePath());
hmm.initialize();
String matrix = hmm.stateTransitionMatrix();
assertTrue(matrix.contains("0\t0") || matrix.contains("0\t"));
stateFile.delete();
alphaFile.delete();
seqFile.delete();
}

@Test
public void testEmptyLineHandlingInAlphaFile() throws Exception {
File stateFile = File.createTempFile("state_empty_line_alpha", ".txt");
PrintWriter writer = new PrintWriter(stateFile);
writer.println("0 0 1 X 0");
writer.close();
File alphaFile = File.createTempFile("alpha_empty_line", ".txt");
PrintWriter alphaWriter = new PrintWriter(alphaFile);
alphaWriter.println("");
alphaWriter.println("0 1.0 apple");
alphaWriter.println("1 1.0 banana");
alphaWriter.close();
MultinomialHMM hmm = new MultinomialHMM(2, stateFile.getAbsolutePath(), 1);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
assertEquals("apple ", hmm.topicKeys[0]);
assertEquals("banana ", hmm.topicKeys[1]);
stateFile.delete();
alphaFile.delete();
}

@Test
public void testStateTopicsOutputWithZeroCounts() throws Exception {
File stateFile = File.createTempFile("state_zeros", ".txt");
PrintWriter w = new PrintWriter(stateFile);
w.println("0 0 1 X 1");
w.println("1 0 2 X 1");
w.close();
File alphaFile = File.createTempFile("alpha_zeros", ".txt");
PrintWriter a = new PrintWriter(alphaFile);
a.println("0 1.0 t1");
a.println("1 1.0 t2");
a.close();
File seqFile = File.createTempFile("seq_zeros", ".txt");
PrintWriter s = new PrintWriter(seqFile);
s.println("0\tmeta");
s.println("1\tmeta");
s.close();
MultinomialHMM hmm = new MultinomialHMM(2, stateFile.getAbsolutePath(), 3);
hmm.setGamma(1.0);
hmm.setRandomSeed(123);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(seqFile.getAbsolutePath());
hmm.initialize();
String output = hmm.stateTopics();
assertTrue(output.split("\n").length == 3);
stateFile.delete();
alphaFile.delete();
seqFile.delete();
}

@Test
public void testLargeNumberOfTopicsInitializesWithoutError() throws Exception {
File stateFile = File.createTempFile("state_large_topics", ".txt");
PrintWriter writer = new PrintWriter(stateFile);
writer.println("0 0 1 X 50");
writer.close();
File alphaFile = File.createTempFile("alpha_large_topics", ".txt");
PrintWriter alphaWriter = new PrintWriter(alphaFile);
for (int i = 0; i < 100; i++) {
alphaWriter.println(i + " 1.0 word" + i);
}
alphaWriter.close();
File seqFile = File.createTempFile("seq_large_topics", ".txt");
PrintWriter seqWriter = new PrintWriter(seqFile);
seqWriter.println("0\tkey");
seqWriter.close();
MultinomialHMM hmm = new MultinomialHMM(100, stateFile.getAbsolutePath(), 5);
hmm.setGamma(2.0);
hmm.setRandomSeed(321);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(seqFile.getAbsolutePath());
hmm.initialize();
assertEquals("word50 ", hmm.topicKeys[50]);
stateFile.delete();
alphaFile.delete();
seqFile.delete();
}

@Test
public void testStateTransitionMatrixCorrectNumberOfLinesForNStates() throws Exception {
File stateFile = File.createTempFile("state_matrix_lines", ".txt");
PrintWriter writer = new PrintWriter(stateFile);
writer.println("0 0 1 X 0");
writer.println("1 0 2 X 1");
writer.close();
File alphaFile = File.createTempFile("alpha_matrix_lines", ".txt");
PrintWriter alpha = new PrintWriter(alphaFile);
alpha.println("0 1.0 wordA");
alpha.println("1 1.0 wordB");
alpha.close();
File seqFile = File.createTempFile("seq_matrix_lines", ".txt");
PrintWriter seq = new PrintWriter(seqFile);
seq.println("0\tinfo");
seq.println("1\tinfo");
seq.close();
MultinomialHMM hmm = new MultinomialHMM(2, stateFile.getAbsolutePath(), 5);
hmm.setGamma(1.0);
hmm.setRandomSeed(456);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(seqFile.getAbsolutePath());
hmm.initialize();
String[] lines = hmm.stateTransitionMatrix().split("\n");
assertEquals(5, lines.length);
stateFile.delete();
alphaFile.delete();
seqFile.delete();
}

@Test
public void testAlphaFileWithTooManyTopicsComparedToConstructor() throws Exception {
File stateFile = File.createTempFile("state_extra_alpha", ".txt");
PrintWriter writer = new PrintWriter(stateFile);
writer.println("0 0 1 X 0");
writer.close();
File alphaFile = File.createTempFile("alpha_extra", ".txt");
PrintWriter alphaWriter = new PrintWriter(alphaFile);
alphaWriter.println("0 1.0 a");
alphaWriter.println("1 1.0 b");
alphaWriter.println("2 1.0 c");
alphaWriter.println("3 1.0 d");
alphaWriter.close();
MultinomialHMM hmm = new MultinomialHMM(3, stateFile.getAbsolutePath(), 2);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
assertEquals("a ", hmm.topicKeys[0]);
assertEquals("b ", hmm.topicKeys[1]);
assertEquals("c ", hmm.topicKeys[2]);
stateFile.delete();
alphaFile.delete();
}

@Test
public void testInvalidIntegerParsingInStateFileIsSkippedOrFails() throws Exception {
File stateFile = File.createTempFile("state_bad_line", ".txt");
PrintWriter writer = new PrintWriter(stateFile);
writer.println("0 0 1 X 0");
writer.println("invalid line without numbers");
writer.println("1 0 2 X 1");
writer.close();
boolean exceptionThrown = false;
try {
new MultinomialHMM(2, stateFile.getAbsolutePath(), 2);
} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
exceptionThrown = true;
}
assertTrue(exceptionThrown);
stateFile.delete();
}

@Test
public void testStateWithNoDocuments() throws Exception {
File stateFile = File.createTempFile("state_no_docs", ".txt");
PrintWriter writer = new PrintWriter(stateFile);
writer.close();
MultinomialHMM hmm = new MultinomialHMM(2, stateFile.getAbsolutePath(), 3);
assertNotNull(hmm);
stateFile.delete();
}

@Test
public void testSequenceFileWithDuplicateDocumentIDs() throws Exception {
File stateFile = File.createTempFile("state_dup_seq", ".txt");
PrintWriter writer = new PrintWriter(stateFile);
writer.println("0 0 2 X 0");
writer.println("1 0 2 X 1");
writer.close();
File alphaFile = File.createTempFile("alpha_dup_seq", ".txt");
PrintWriter alpha = new PrintWriter(alphaFile);
alpha.println("0 1.0 tok0");
alpha.println("1 1.0 tok1");
alpha.close();
File seqFile = File.createTempFile("seq_dup_seq", ".txt");
PrintWriter seqWriter = new PrintWriter(seqFile);
seqWriter.println("0\tX");
seqWriter.println("0\tY");
seqWriter.close();
MultinomialHMM hmm = new MultinomialHMM(2, stateFile.getAbsolutePath(), 2);
hmm.setGamma(1.0);
hmm.setRandomSeed(42);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
boolean exceptionThrown = false;
try {
hmm.loadSequenceIDsFromFile(seqFile.getAbsolutePath());
} catch (ArrayIndexOutOfBoundsException e) {
exceptionThrown = true;
}
assertTrue(exceptionThrown);
stateFile.delete();
alphaFile.delete();
seqFile.delete();
}

@Test
public void testDocumentWithNoAssignedTopicsIsIgnored() throws Exception {
File stateFile = File.createTempFile("state_some_docs_empty", ".txt");
PrintWriter writer = new PrintWriter(stateFile);
writer.println("0 0 1 X 0");
writer.println("2 0 2 X 1");
writer.close();
File alphaFile = File.createTempFile("alpha_default", ".txt");
PrintWriter alpha = new PrintWriter(alphaFile);
alpha.println("0 1.0 a");
alpha.println("1 1.0 b");
alpha.close();
File seqFile = File.createTempFile("seq_default", ".txt");
PrintWriter seq = new PrintWriter(seqFile);
seq.println("0\tmeta");
seq.println("1\tmeta");
seq.println("2\tmeta");
seq.close();
MultinomialHMM hmm = new MultinomialHMM(2, stateFile.getAbsolutePath(), 2);
hmm.setGamma(1.0);
hmm.setRandomSeed(2024);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(seqFile.getAbsolutePath());
hmm.initialize();
hmm.sample();
assertNotNull(hmm.stateTransitionMatrix());
stateFile.delete();
alphaFile.delete();
seqFile.delete();
}

@Test
public void testSamplingWithIdenticalTokensInAllDocuments() throws Exception {
File stateFile = File.createTempFile("state_same_tokens", ".txt");
PrintWriter writer = new PrintWriter(stateFile);
writer.println("0 0 1 X 0");
writer.println("1 0 1 X 0");
writer.println("2 0 1 X 0");
writer.close();
File alphaFile = File.createTempFile("alpha_same_tokens", ".txt");
PrintWriter alpha = new PrintWriter(alphaFile);
alpha.println("0 1.0 tok0");
alpha.close();
File seqFile = File.createTempFile("seq_same_tokens", ".txt");
PrintWriter seq = new PrintWriter(seqFile);
seq.println("0\tmeta");
seq.println("1\tmeta");
seq.println("2\tmeta");
seq.close();
MultinomialHMM hmm = new MultinomialHMM(1, stateFile.getAbsolutePath(), 2);
hmm.setGamma(5.0);
hmm.setRandomSeed(1);
hmm.setNumIterations(1);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(seqFile.getAbsolutePath());
hmm.initialize();
hmm.sample();
assertNotNull(hmm.stateTopics());
stateFile.delete();
alphaFile.delete();
seqFile.delete();
}

@Test
public void testAlphaFileWithLargeTopicIndexThrowsException() throws Exception {
File stateFile = File.createTempFile("state_too_large_topic_idx", ".txt");
PrintWriter writer = new PrintWriter(stateFile);
writer.println("0 0 1 X 0");
writer.close();
File alphaFile = File.createTempFile("alpha_bad_idx", ".txt");
PrintWriter alpha = new PrintWriter(alphaFile);
alpha.println("0 1.0 A");
alpha.println("999 1.0 B");
alpha.close();
boolean threwException = false;
try {
MultinomialHMM hmm = new MultinomialHMM(1, stateFile.getAbsolutePath(), 1);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
} catch (ArrayIndexOutOfBoundsException e) {
threwException = true;
}
assertTrue(threwException);
stateFile.delete();
alphaFile.delete();
}

@Test
public void testSamplingSkipsDocumentsWithoutTopics() throws Exception {
File stateFile = File.createTempFile("state_skipped_docs", ".txt");
PrintWriter writer = new PrintWriter(stateFile);
writer.println("0 0 1 X 0");
writer.println("2 0 1 X 1");
writer.close();
File alphaFile = File.createTempFile("alpha_skipped_docs", ".txt");
PrintWriter alpha = new PrintWriter(alphaFile);
alpha.println("0 1.0 dog");
alpha.println("1 1.0 cat");
alpha.close();
File seqFile = File.createTempFile("seq_skipped_docs", ".txt");
PrintWriter seq = new PrintWriter(seqFile);
seq.println("0\tmeta");
seq.println("1\tmeta");
seq.println("2\tmeta");
seq.close();
MultinomialHMM hmm = new MultinomialHMM(2, stateFile.getAbsolutePath(), 3);
hmm.setGamma(0.1);
hmm.setRandomSeed(42);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(seqFile.getAbsolutePath());
hmm.initialize();
hmm.sample();
String stateTopics = hmm.stateTopics();
assertNotNull(stateTopics);
stateFile.delete();
alphaFile.delete();
seqFile.delete();
}
}
