package cc.mallet.topics;

import cc.mallet.types.*;
import cc.mallet.util.Randoms;
import org.junit.Test;
import java.io.*;
import java.util.Collections;
import java.util.List;
import static org.junit.Assert.*;

public class MultinomialHMM_llmsuite_1_GPTLLMTest {

@Test
public void testConstructorLoadsTopicsCorrectly() throws IOException {
File ldaStateFile = File.createTempFile("lda_state", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(ldaStateFile));
writer.write("0 0 5 0 0\n");
writer.write("0 1 7 0 1\n");
writer.write("1 0 6 0 2\n");
writer.close();
MultinomialHMM hmm = new MultinomialHMM(3, ldaStateFile.getAbsolutePath(), 2);
assertEquals(3, hmm.alpha.length);
assertEquals(2, hmm.documentStates.length);
assertTrue(hmm.maxDocLength >= 2);
assertTrue(hmm.maxTokensPerTopic[0] >= 1);
assertTrue(hmm.maxTokensPerTopic[1] >= 1);
assertTrue(hmm.maxTokensPerTopic[2] >= 1);
ldaStateFile.delete();
}

@Test
public void testSetGammaAndOtherSetters() throws IOException {
File ldaStateFile = File.createTempFile("lda_state", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(ldaStateFile));
writer.write("0 0 1 0 0\n");
writer.close();
MultinomialHMM hmm = new MultinomialHMM(2, ldaStateFile.getAbsolutePath(), 2);
hmm.setGamma(0.75);
hmm.setNumIterations(50);
hmm.setBurninPeriod(10);
hmm.setTopicDisplayInterval(5);
hmm.setOptimizeInterval(20);
hmm.setRandomSeed(42);
assertEquals(0.75, hmm.gamma, 0.0001);
assertEquals(50, hmm.numIterations);
assertEquals(10, hmm.burninPeriod);
assertEquals(5, hmm.showTopicsInterval);
assertEquals(20, hmm.optimizeInterval);
assertNotNull(hmm);
ldaStateFile.delete();
}

@Test
public void testLoadAlphaFromFileSetsTopicKeysAndAlpha() throws IOException {
File ldaStateFile = File.createTempFile("lda_state", ".txt");
File ldaAlphaFile = File.createTempFile("lda_alpha", ".txt");
BufferedWriter ldaWriter = new BufferedWriter(new FileWriter(ldaStateFile));
ldaWriter.write("0 0 5 0 0\n");
ldaWriter.write("1 0 6 0 2\n");
ldaWriter.close();
BufferedWriter alphaWriter = new BufferedWriter(new FileWriter(ldaAlphaFile));
alphaWriter.write("0 1.0 word0 word1\n");
alphaWriter.write("1 1.0 word2\n");
alphaWriter.write("2 1.0 word3\n");
alphaWriter.close();
MultinomialHMM hmm = new MultinomialHMM(3, ldaStateFile.getAbsolutePath(), 2);
hmm.loadAlphaFromFile(ldaAlphaFile.getAbsolutePath());
assertEquals("word0 word1 ", hmm.topicKeys[0]);
assertEquals("word2 ", hmm.topicKeys[1]);
assertEquals("word3 ", hmm.topicKeys[2]);
assertEquals(3.0, hmm.alphaSum, 0.0001);
assertEquals(1.0, hmm.alpha[0], 0.0001);
assertEquals(1.0, hmm.alpha[1], 0.0001);
assertEquals(1.0, hmm.alpha[2], 0.0001);
ldaStateFile.delete();
ldaAlphaFile.delete();
}

@Test
public void testLoadSequenceIDsFileIncrementsnumSequences() throws IOException {
File ldaStateFile = File.createTempFile("lda_state", ".txt");
File sequenceFile = File.createTempFile("sequence", ".txt");
BufferedWriter ldaWriter = new BufferedWriter(new FileWriter(ldaStateFile));
ldaWriter.write("0 0 5 0 0\n");
ldaWriter.write("1 0 4 0 1\n");
ldaWriter.close();
BufferedWriter seqWriter = new BufferedWriter(new FileWriter(sequenceFile));
seqWriter.write("0\tseq0\n");
seqWriter.write("1\tseq1\n");
seqWriter.close();
MultinomialHMM hmm = new MultinomialHMM(3, ldaStateFile.getAbsolutePath(), 2);
hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
assertEquals(2, hmm.documentSequenceIDs.length);
assertEquals(2, hmm.numSequences);
ldaStateFile.delete();
sequenceFile.delete();
}

@Test
public void testInitializePopulatesStatesCorrectly() throws IOException {
File ldaStateFile = File.createTempFile("lda_state", ".txt");
File alphaFile = File.createTempFile("lda_alpha", ".txt");
File sequenceFile = File.createTempFile("sequence", ".txt");
BufferedWriter ldaWriter = new BufferedWriter(new FileWriter(ldaStateFile));
ldaWriter.write("0 0 5 0 0\n");
ldaWriter.write("1 0 6 0 2\n");
ldaWriter.close();
BufferedWriter alphaWriter = new BufferedWriter(new FileWriter(alphaFile));
alphaWriter.write("0 1.0 word0\n");
alphaWriter.write("1 1.0 word1\n");
alphaWriter.write("2 1.0 word2\n");
alphaWriter.close();
BufferedWriter seqWriter = new BufferedWriter(new FileWriter(sequenceFile));
seqWriter.write("0\tseq0\n");
seqWriter.write("1\tseq1\n");
seqWriter.close();
MultinomialHMM hmm = new MultinomialHMM(3, ldaStateFile.getAbsolutePath(), 2);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
hmm.setGamma(1.0);
hmm.setRandomSeed(123);
hmm.initialize();
int state0 = hmm.documentStates[0];
int state1 = hmm.documentStates[1];
assertTrue(state0 >= 0 && state0 < 2);
assertTrue(state1 >= 0 && state1 < 2);
ldaStateFile.delete();
alphaFile.delete();
sequenceFile.delete();
}

@Test
public void testStateTransitionMatrixFormat() throws IOException {
File ldaStateFile = File.createTempFile("lda_state", ".txt");
File alphaFile = File.createTempFile("lda_alpha", ".txt");
File sequenceFile = File.createTempFile("sequence", ".txt");
BufferedWriter ldaWriter = new BufferedWriter(new FileWriter(ldaStateFile));
ldaWriter.write("0 0 5 0 0\n");
ldaWriter.write("1 0 6 0 2\n");
ldaWriter.close();
BufferedWriter alphaWriter = new BufferedWriter(new FileWriter(alphaFile));
alphaWriter.write("0 1.0 word0\n");
alphaWriter.write("1 1.0 word1\n");
alphaWriter.write("2 1.0 word2\n");
alphaWriter.close();
BufferedWriter sequenceWriter = new BufferedWriter(new FileWriter(sequenceFile));
sequenceWriter.write("0\tseq0\n");
sequenceWriter.write("1\tseq1\n");
sequenceWriter.close();
MultinomialHMM hmm = new MultinomialHMM(3, ldaStateFile.getAbsolutePath(), 2);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
hmm.setRandomSeed(1);
hmm.setGamma(1.0);
hmm.initialize();
String matrix = hmm.stateTransitionMatrix();
String[] rows = matrix.trim().split("\n");
assertEquals(2, rows.length);
assertTrue(rows[0].split("\t").length == 2);
assertTrue(rows[1].split("\t").length == 2);
ldaStateFile.delete();
alphaFile.delete();
sequenceFile.delete();
}

@Test(expected = IOException.class)
public void testInvalidFilePathThrowsIOException() throws IOException {
new MultinomialHMM(3, "non_existent_file.txt", 2);
}

@Test(expected = IOException.class)
public void testInvalidAlphaPathThrowsIOException() throws IOException {
File ldaStateFile = File.createTempFile("lda_state", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(ldaStateFile));
writer.write("0 0 5 0 0\n");
writer.close();
MultinomialHMM hmm = new MultinomialHMM(3, ldaStateFile.getAbsolutePath(), 2);
hmm.loadAlphaFromFile("invalid_alpha.txt");
ldaStateFile.delete();
}

@Test(expected = IOException.class)
public void testInvalidSequencePathThrowsIOException() throws IOException {
File ldaStateFile = File.createTempFile("lda_state", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(ldaStateFile));
writer.write("0 0 5 0 0\n");
writer.close();
MultinomialHMM hmm = new MultinomialHMM(3, ldaStateFile.getAbsolutePath(), 2);
hmm.loadSequenceIDsFromFile("invalid_seq.txt");
ldaStateFile.delete();
}

@Test
public void testLoadTopicsFromFileIgnoresCommentAndBlankLines() throws IOException {
File file = File.createTempFile("state_file", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(file));
writer.write("# This is a comment line\n");
writer.write("\n");
writer.write("0 0 1 0 0\n");
writer.write("0 1 2 0 1\n");
writer.write("1 0 3 0 2\n");
writer.close();
MultinomialHMM hmm = new MultinomialHMM(3, file.getAbsolutePath(), 2);
assertEquals(2, hmm.documentStates.length);
file.delete();
}

@Test
public void testLoadTopicsFromFileHandlesDuplicateTopicCounts() throws IOException {
File file = File.createTempFile("state_file", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(file));
writer.write("0 0 1 0 2\n");
writer.write("0 1 1 0 2\n");
writer.write("1 0 3 0 1\n");
writer.close();
MultinomialHMM hmm = new MultinomialHMM(3, file.getAbsolutePath(), 2);
assertEquals(2, hmm.documentStates.length);
file.delete();
}

@Test
public void testLoadSequenceIDsWithRepeatedSequences() throws IOException {
File stateFile = File.createTempFile("lda_state", ".txt");
BufferedWriter sWriter = new BufferedWriter(new FileWriter(stateFile));
sWriter.write("0 0 1 0 0\n");
sWriter.write("1 0 2 0 1\n");
sWriter.write("2 0 3 0 2\n");
sWriter.close();
File sequenceFile = File.createTempFile("seq", ".txt");
BufferedWriter seqWriter = new BufferedWriter(new FileWriter(sequenceFile));
seqWriter.write("0\tS\n");
seqWriter.write("1\tS\n");
seqWriter.write("2\tT\n");
seqWriter.close();
MultinomialHMM hmm = new MultinomialHMM(3, stateFile.getAbsolutePath(), 2);
hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
assertEquals(2, hmm.numSequences);
stateFile.delete();
sequenceFile.delete();
}

@Test
public void testBlankAlphaFileSkipsBlankLines() throws IOException {
File ldaStateFile = File.createTempFile("lda", ".txt");
BufferedWriter sWriter = new BufferedWriter(new FileWriter(ldaStateFile));
sWriter.write("0 0 1 0 0\n");
sWriter.close();
File alphaFile = File.createTempFile("alpha", ".txt");
BufferedWriter aWriter = new BufferedWriter(new FileWriter(alphaFile));
aWriter.write("0 1.0 word0\n\n");
aWriter.write("1 1.0 word1\n");
aWriter.write("2 1.0 word2\n");
aWriter.close();
MultinomialHMM hmm = new MultinomialHMM(3, ldaStateFile.getAbsolutePath(), 2);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
assertEquals("word1 ", hmm.topicKeys[1]);
assertEquals(3.0, hmm.alphaSum, 0.01);
ldaStateFile.delete();
alphaFile.delete();
}

@Test(expected = NumberFormatException.class)
public void testInvalidTokenThrowsFormatExceptionInStateFile() throws IOException {
File file = File.createTempFile("invalid", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(file));
writer.write("0 A B C D\n");
writer.close();
new MultinomialHMM(3, file.getAbsolutePath(), 2);
file.delete();
}

@Test
public void testInitializeWithRandomUnsetAutomaticallyCreatesRandom() throws IOException {
File file = File.createTempFile("lda_state", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(file));
writer.write("0 0 1 0 0\n");
writer.write("1 0 1 0 1\n");
writer.close();
File alphaFile = File.createTempFile("alpha", ".txt");
BufferedWriter aWriter = new BufferedWriter(new FileWriter(alphaFile));
aWriter.write("0 1.0 word0\n");
aWriter.write("1 1.0 word1\n");
aWriter.write("2 1.0 word2\n");
aWriter.close();
File sequenceFile = File.createTempFile("seq", ".txt");
BufferedWriter sWriter = new BufferedWriter(new FileWriter(sequenceFile));
sWriter.write("0\tS1\n");
sWriter.write("1\tS2\n");
sWriter.close();
MultinomialHMM hmm = new MultinomialHMM(3, file.getAbsolutePath(), 2);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
hmm.setGamma(1.0);
hmm.initialize();
assertEquals(2, hmm.documentStates.length);
file.delete();
alphaFile.delete();
sequenceFile.delete();
}

@Test
public void testStateTransitionMatrixOnEmptyCounts() throws IOException {
File file = File.createTempFile("lda_state", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(file));
writer.write("0 0 1 0 0\n");
writer.write("1 0 1 0 1\n");
writer.close();
MultinomialHMM hmm = new MultinomialHMM(2, file.getAbsolutePath(), 2);
String matrix = hmm.stateTransitionMatrix();
String[] parts = matrix.split("\n");
assertEquals(2, parts.length);
assertTrue(parts[0].trim().equals("0\t0"));
assertTrue(parts[1].trim().equals("0\t0"));
file.delete();
}

@Test
public void testSamplingDistributionWithZeroProbabilitiesDoesNotCrash() throws IOException {
File stateFile = File.createTempFile("lda_state", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(stateFile));
writer.write("0 0 1 0 0\n");
writer.close();
File alphaFile = File.createTempFile("alpha", ".txt");
BufferedWriter aWriter = new BufferedWriter(new FileWriter(alphaFile));
aWriter.write("0 1.0 word0\n");
aWriter.close();
File seqFile = File.createTempFile("seq", ".txt");
BufferedWriter sWriter = new BufferedWriter(new FileWriter(seqFile));
sWriter.write("0\tS0\n");
sWriter.close();
MultinomialHMM hmm = new MultinomialHMM(1, stateFile.getAbsolutePath(), 1);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(seqFile.getAbsolutePath());
hmm.setGamma(0.0);
hmm.setRandomSeed(123);
hmm.initialize();
assertTrue(hmm.documentStates[0] >= 0);
stateFile.delete();
alphaFile.delete();
seqFile.delete();
}

@Test
public void testNonexistentDocumentTopicIsIgnoredGracefullyDuringSampling() throws IOException {
File ldaFile = File.createTempFile("state_file", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(ldaFile));
writer.write("1 0 3 0 2\n");
writer.close();
File alpha = File.createTempFile("alpha_file", ".txt");
BufferedWriter alphaWriter = new BufferedWriter(new FileWriter(alpha));
alphaWriter.write("0 1.0 w0\n");
alphaWriter.write("1 1.0 w1\n");
alphaWriter.write("2 1.0 w2\n");
alphaWriter.close();
File seqFile = File.createTempFile("seq", ".txt");
BufferedWriter seqWriter = new BufferedWriter(new FileWriter(seqFile));
seqWriter.write("0\tA\n");
seqWriter.write("1\tB\n");
seqWriter.close();
MultinomialHMM hmm = new MultinomialHMM(3, ldaFile.getAbsolutePath(), 2);
hmm.loadAlphaFromFile(alpha.getAbsolutePath());
hmm.loadSequenceIDsFromFile(seqFile.getAbsolutePath());
hmm.setGamma(1.0);
hmm.setRandomSeed(2);
hmm.initialize();
assertNotNull(hmm);
ldaFile.delete();
alpha.delete();
seqFile.delete();
}

@Test
public void testAlphaFileWithExtraWhitespaceLinesIsHandled() throws IOException {
File ldaFile = File.createTempFile("lda", ".txt");
BufferedWriter out1 = new BufferedWriter(new FileWriter(ldaFile));
out1.write("0 0 5 0 0\n");
out1.close();
File alphaFile = File.createTempFile("alpha", ".txt");
BufferedWriter out2 = new BufferedWriter(new FileWriter(alphaFile));
out2.write("0 1.0 word0\n");
out2.write("   \n");
out2.write("\n");
out2.write("1 1.0 word1\n");
out2.write("2 1.0 word2 word3\n");
out2.close();
MultinomialHMM hmm = new MultinomialHMM(3, ldaFile.getAbsolutePath(), 2);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
assertEquals("word2 word3 ", hmm.topicKeys[2]);
ldaFile.delete();
alphaFile.delete();
}

@Test
public void testLoadTopicsFileIncrementsNumDocsCorrectly() throws IOException {
File file = File.createTempFile("state", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(file));
writer.write("0 0 1 0 0\n");
writer.write("3 0 2 0 2\n");
writer.close();
MultinomialHMM hmm = new MultinomialHMM(3, file.getAbsolutePath(), 2);
assertEquals(4, hmm.numDocs);
file.delete();
}

@Test
public void testLoadSequenceIDsFileMismatchDocCountWarns() throws IOException {
File lda = File.createTempFile("lda", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(lda));
writer.write("0 0 5 0 0\n");
writer.write("1 0 4 0 2\n");
writer.write("2 0 3 0 1\n");
writer.write("3 0 6 0 1\n");
writer.close();
File sequence = File.createTempFile("seq", ".txt");
BufferedWriter sWriter = new BufferedWriter(new FileWriter(sequence));
sWriter.write("0\tS0\n");
sWriter.write("1\tS0\n");
sWriter.write("2\tS1\n");
sWriter.close();
MultinomialHMM hmm = new MultinomialHMM(3, lda.getAbsolutePath(), 3);
hmm.loadSequenceIDsFromFile(sequence.getAbsolutePath());
assertEquals(2, hmm.numSequences);
assertEquals(3, hmm.documentSequenceIDs.length);
lda.delete();
sequence.delete();
}

@Test
public void testInitializeHandlesOneDocOneStateCorrectly() throws IOException {
File f1 = File.createTempFile("lda", ".txt");
BufferedWriter o1 = new BufferedWriter(new FileWriter(f1));
o1.write("0 0 5 0 0\n");
o1.close();
File alphaFile = File.createTempFile("alpha_file", ".txt");
BufferedWriter o2 = new BufferedWriter(new FileWriter(alphaFile));
o2.write("0 1.0 apple\n");
o2.close();
File seq = File.createTempFile("seq", ".txt");
BufferedWriter o3 = new BufferedWriter(new FileWriter(seq));
o3.write("0\tSEQ1\n");
o3.close();
MultinomialHMM hmm = new MultinomialHMM(1, f1.getAbsolutePath(), 1);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(seq.getAbsolutePath());
hmm.setGamma(1.0);
hmm.initialize();
assertEquals(1, hmm.documentStates[0]);
f1.delete();
alphaFile.delete();
seq.delete();
}

@Test
public void testStateTopicsPrintsCorrectlyWhenEmpty() throws IOException {
File file = File.createTempFile("lda_empty", ".txt");
BufferedWriter w = new BufferedWriter(new FileWriter(file));
w.write("0 0 1 0 0\n");
w.close();
MultinomialHMM hmm = new MultinomialHMM(2, file.getAbsolutePath(), 2);
String result = hmm.stateTopics();
assertTrue(result.split("\n").length == 2);
file.delete();
}

@Test
public void testAlphaParsingWithMultipleWordKeys() throws IOException {
File state = File.createTempFile("lda", ".txt");
BufferedWriter sWriter = new BufferedWriter(new FileWriter(state));
sWriter.write("0 0 5 0 0\n");
sWriter.close();
File alpha = File.createTempFile("alpha_multi", ".txt");
BufferedWriter aWriter = new BufferedWriter(new FileWriter(alpha));
aWriter.write("0 1.0 dog cat fish\n");
aWriter.write("1 1.0 jump run\n");
aWriter.write("2 1.0 walk\n");
aWriter.close();
MultinomialHMM hmm = new MultinomialHMM(3, state.getAbsolutePath(), 2);
hmm.loadAlphaFromFile(alpha.getAbsolutePath());
assertEquals("dog cat fish ", hmm.topicKeys[0]);
assertEquals("jump run ", hmm.topicKeys[1]);
assertEquals("walk ", hmm.topicKeys[2]);
state.delete();
alpha.delete();
}

@Test
public void testEdgeCaseOnlyOneTopicAcrossDocuments() throws IOException {
File state = File.createTempFile("lda", ".txt");
BufferedWriter sw = new BufferedWriter(new FileWriter(state));
sw.write("0 0 3 0 0\n");
sw.write("1 0 3 0 0\n");
sw.write("2 0 3 0 0\n");
sw.write("3 0 3 0 0\n");
sw.close();
File alphaFile = File.createTempFile("alpha", ".txt");
BufferedWriter aw = new BufferedWriter(new FileWriter(alphaFile));
aw.write("0 1.0 abc\n");
aw.close();
File seqFile = File.createTempFile("seq", ".txt");
BufferedWriter bw = new BufferedWriter(new FileWriter(seqFile));
bw.write("0\tS1\n");
bw.write("1\tS1\n");
bw.write("2\tS1\n");
bw.write("3\tS1\n");
bw.close();
MultinomialHMM hmm = new MultinomialHMM(1, state.getAbsolutePath(), 1);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(seqFile.getAbsolutePath());
hmm.setGamma(1.0);
hmm.setRandomSeed(7);
hmm.initialize();
assertEquals(4, hmm.documentStates.length);
state.delete();
alphaFile.delete();
seqFile.delete();
}

@Test
public void testIgnoresEmptyDocumentWithNoTokens() throws IOException {
File state = File.createTempFile("lda", ".txt");
BufferedWriter sw = new BufferedWriter(new FileWriter(state));
sw.write("1 0 2 0 0\n");
sw.close();
File alphaFile = File.createTempFile("alpha", ".txt");
BufferedWriter aw = new BufferedWriter(new FileWriter(alphaFile));
aw.write("0 1.0 foo\n");
aw.close();
File seqFile = File.createTempFile("seq", ".txt");
BufferedWriter qw = new BufferedWriter(new FileWriter(seqFile));
qw.write("0\tSEQ\n");
qw.write("1\tSEQ\n");
qw.close();
MultinomialHMM hmm = new MultinomialHMM(1, state.getAbsolutePath(), 1);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(seqFile.getAbsolutePath());
hmm.setGamma(1.0);
hmm.setRandomSeed(5);
hmm.initialize();
assertNotEquals(-1, hmm.documentStates[1]);
state.delete();
alphaFile.delete();
seqFile.delete();
}

@Test(expected = ArrayIndexOutOfBoundsException.class)
public void testMissingFieldsInStateFileThrowsException() throws IOException {
File s = File.createTempFile("bad_state", ".txt");
BufferedWriter b = new BufferedWriter(new FileWriter(s));
b.write("0 0 3 0\n");
b.close();
new MultinomialHMM(2, s.getAbsolutePath(), 1);
s.delete();
}

@Test(expected = IOException.class)
public void testLoadGzipTopicsFromFileThrowsIOExceptionIfCorrupt() throws IOException {
File gzipFile = File.createTempFile("lda_state", ".gz");
FileOutputStream fos = new FileOutputStream(gzipFile);
fos.write("Not a gzip\n".getBytes());
fos.close();
new MultinomialHMM(3, gzipFile.getAbsolutePath(), 2);
gzipFile.delete();
}

@Test(expected = IOException.class)
public void testLoadAlphaMalformedLineThrowsExceptionOnMissingFields() throws IOException {
File stateFile = File.createTempFile("lda_state", ".txt");
BufferedWriter w1 = new BufferedWriter(new FileWriter(stateFile));
w1.write("0 0 1 0 1\n");
w1.close();
File alphaFile = File.createTempFile("alpha_bad", ".txt");
BufferedWriter w2 = new BufferedWriter(new FileWriter(alphaFile));
w2.write("0\n");
w2.close();
MultinomialHMM hmm = new MultinomialHMM(2, stateFile.getAbsolutePath(), 1);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
stateFile.delete();
alphaFile.delete();
}

@Test
public void testLoadAlphaSkipsEmptyOrBadLineGracefully() throws IOException {
File stateFile = File.createTempFile("lda", ".txt");
BufferedWriter sWriter = new BufferedWriter(new FileWriter(stateFile));
sWriter.write("0 0 5 0 0\n");
sWriter.close();
File alphaFile = File.createTempFile("alpha_file", ".txt");
BufferedWriter aWriter = new BufferedWriter(new FileWriter(alphaFile));
aWriter.write("\n");
aWriter.write("0 1.0 topic0 word0\n");
aWriter.write("INVALID LINE\n");
aWriter.write("1 1.0 topic1 word1\n");
aWriter.write("\n");
aWriter.write("2 1.0 topic2 word2\n");
aWriter.close();
MultinomialHMM hmm = new MultinomialHMM(3, stateFile.getAbsolutePath(), 2);
try {
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
} catch (Exception e) {
fail("Should not throw on invalid alpha lines if not crashing directly");
}
assertEquals("topic0 word0 ", hmm.topicKeys[0]);
assertEquals("topic1 word1 ", hmm.topicKeys[1]);
assertEquals("topic2 word2 ", hmm.topicKeys[2]);
stateFile.delete();
alphaFile.delete();
}

@Test
public void testLargeTopicCountsResizedCacheCorrectly() throws IOException {
File topicFile = File.createTempFile("lda_large_count", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(topicFile));
writer.write("0 0 1 0 0\n");
writer.write("0 1 5 0 0\n");
writer.write("0 2 10 0 0\n");
writer.write("0 3 50 0 0\n");
writer.write("0 4 75 0 0\n");
writer.close();
MultinomialHMM hmm = new MultinomialHMM(1, topicFile.getAbsolutePath(), 2);
assertTrue(hmm.maxTokensPerTopic[0] >= 75);
topicFile.delete();
}

@Test
public void testAlphaSumIsCalculatedCorrectly() throws IOException {
File ldaFile = File.createTempFile("lda_basic", ".txt");
BufferedWriter ldaWriter = new BufferedWriter(new FileWriter(ldaFile));
ldaWriter.write("0 0 5 0 0\n");
ldaWriter.close();
File alphaFile = File.createTempFile("alpha_weights", ".txt");
BufferedWriter alphaWriter = new BufferedWriter(new FileWriter(alphaFile));
alphaWriter.write("0 1.0 A\n");
alphaWriter.write("1 2.0 B\n");
alphaWriter.write("2 3.0 C\n");
alphaWriter.close();
MultinomialHMM hmm = new MultinomialHMM(3, ldaFile.getAbsolutePath(), 2);
hmm.alpha[0] = 1.0;
hmm.alpha[1] = 2.0;
hmm.alpha[2] = 3.0;
hmm.alphaSum = hmm.alpha[0] + hmm.alpha[1] + hmm.alpha[2];
assertEquals(6.0, hmm.alphaSum, 0.0001);
ldaFile.delete();
alphaFile.delete();
}

@Test
public void testStateTopicMatrixValuesRemainZeroWhenUninitialized() throws IOException {
File lda = File.createTempFile("lda_empty", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(lda));
writer.write("0 0 1 0 0\n");
writer.close();
MultinomialHMM hmm = new MultinomialHMM(2, lda.getAbsolutePath(), 2);
String matrix = hmm.stateTopics();
String[] lines = matrix.split("\n");
assertEquals(2, lines.length);
int val1 = Integer.parseInt(lines[0].split("\t")[0]);
int val2 = Integer.parseInt(lines[1].split("\t")[0]);
assertEquals(0, val1);
assertEquals(0, val2);
lda.delete();
}

@Test
public void testAllZerosInTransitionMatrixBeforeInitialization() throws IOException {
File f = File.createTempFile("lda", ".txt");
BufferedWriter out = new BufferedWriter(new FileWriter(f));
out.write("0 0 3 0 0\n");
out.close();
MultinomialHMM hmm = new MultinomialHMM(1, f.getAbsolutePath(), 3);
String matrix = hmm.stateTransitionMatrix();
assertTrue(matrix.contains("0\t0\t0"));
f.delete();
}

@Test
public void testStateTopicsHandlesEmptyStatesSafely() throws IOException {
File ldaFile = File.createTempFile("lda_doc", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(ldaFile));
writer.write("1 0 1 0 0\n");
writer.close();
MultinomialHMM hmm = new MultinomialHMM(2, ldaFile.getAbsolutePath(), 2);
String topics = hmm.stateTopics();
String[] lines = topics.split("\n");
String[] values = lines[0].split("\t");
assertEquals("0", values[0].trim());
ldaFile.delete();
}

@Test
public void testLoadTopicsHandlesNonSequentialDocIds() throws IOException {
File file = File.createTempFile("lda_state", ".txt");
BufferedWriter out = new BufferedWriter(new FileWriter(file));
out.write("0 0 1 0 0\n");
out.write("10 0 2 0 0\n");
out.close();
MultinomialHMM hmm = new MultinomialHMM(2, file.getAbsolutePath(), 2);
assertEquals(11, hmm.numDocs);
file.delete();
}

@Test
public void testInitializeWithOnlyOneDocumentSequenceWorks() throws IOException {
File stateFile = File.createTempFile("lda", ".txt");
BufferedWriter w1 = new BufferedWriter(new FileWriter(stateFile));
w1.write("0 0 1 0 0\n");
w1.close();
File alphaFile = File.createTempFile("alpha", ".txt");
BufferedWriter w2 = new BufferedWriter(new FileWriter(alphaFile));
w2.write("0 1.0 lone\n");
w2.close();
File sequenceFile = File.createTempFile("seq_single", ".txt");
BufferedWriter w3 = new BufferedWriter(new FileWriter(sequenceFile));
w3.write("0\tSINGLE\n");
w3.close();
MultinomialHMM hmm = new MultinomialHMM(1, stateFile.getAbsolutePath(), 1);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
hmm.setRandomSeed(42);
hmm.setGamma(1.0);
hmm.initialize();
assertEquals(1, hmm.numSequences);
assertTrue(hmm.documentStates[0] >= 0);
stateFile.delete();
alphaFile.delete();
sequenceFile.delete();
}

@Test
public void testSampleStateWithSingleTokenDifferentPrevNextSequence() throws IOException {
File stateFile = File.createTempFile("lda", ".txt");
BufferedWriter sw = new BufferedWriter(new FileWriter(stateFile));
sw.write("0 0 3 0 0\n");
sw.write("1 0 3 0 1\n");
sw.write("2 0 3 0 2\n");
sw.write("3 0 3 0 0\n");
sw.close();
File alphaFile = File.createTempFile("alpha", ".txt");
BufferedWriter aw = new BufferedWriter(new FileWriter(alphaFile));
aw.write("0 1.0 alpha\n");
aw.close();
File sequenceFile = File.createTempFile("seq", ".txt");
BufferedWriter q = new BufferedWriter(new FileWriter(sequenceFile));
q.write("0\tS1\n");
q.write("1\tS2\n");
q.write("2\tS2\n");
q.write("3\tS3\n");
q.close();
MultinomialHMM hmm = new MultinomialHMM(1, stateFile.getAbsolutePath(), 2);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
hmm.setGamma(1.0);
hmm.setRandomSeed(2024);
hmm.initialize();
assertEquals(4, hmm.documentStates.length);
stateFile.delete();
alphaFile.delete();
sequenceFile.delete();
}

@Test
public void testDocumentWithoutMatchingSequenceID() throws IOException {
File stateFile = File.createTempFile("lda", ".txt");
BufferedWriter w = new BufferedWriter(new FileWriter(stateFile));
w.write("0 0 3 0 0\n");
w.write("1 0 3 0 1\n");
w.write("2 0 3 0 2\n");
w.write("3 0 3 0 0\n");
w.close();
File sequenceFile = File.createTempFile("seq", ".txt");
BufferedWriter s = new BufferedWriter(new FileWriter(sequenceFile));
s.write("0\tA\n");
s.write("1\tB\n");
s.write("2\tC\n");
s.close();
MultinomialHMM hmm = new MultinomialHMM(1, stateFile.getAbsolutePath(), 2);
try {
hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
fail("Expected a runtime error due to sequence size mismatch");
} catch (IOException e) {
assertTrue(e.getMessage() == null || e.getMessage().isEmpty());
}
stateFile.delete();
sequenceFile.delete();
}

@Test
public void testStateTransitionMatrixFormatWithOneState() throws IOException {
File file = File.createTempFile("lda_state", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(file));
writer.write("0 0 3 0 0\n");
writer.close();
MultinomialHMM hmm = new MultinomialHMM(1, file.getAbsolutePath(), 1);
String matrix = hmm.stateTransitionMatrix();
assertTrue(matrix.trim().equals("0"));
file.delete();
}

@Test
public void testAlphaFileWithLongTopicNameIsParsed() throws IOException {
File stateFile = File.createTempFile("lda", ".txt");
BufferedWriter w1 = new BufferedWriter(new FileWriter(stateFile));
w1.write("0 0 2 0 0\n");
w1.write("1 0 3 0 1\n");
w1.close();
File alphaFile = File.createTempFile("alpha", ".txt");
BufferedWriter w2 = new BufferedWriter(new FileWriter(alphaFile));
w2.write("0 1.0 highly_descriptive_topic_name_1 anotherToken1\n");
w2.write("1 1.0 another_lengthy_topic_key hereMore\n");
w2.close();
MultinomialHMM hmm = new MultinomialHMM(2, stateFile.getAbsolutePath(), 2);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
assertEquals("highly_descriptive_topic_name_1 anotherToken1 ", hmm.topicKeys[0]);
assertEquals("another_lengthy_topic_key hereMore ", hmm.topicKeys[1]);
stateFile.delete();
alphaFile.delete();
}

@Test
public void testTokenCountExceedsCacheCapacityIsHandled() throws IOException {
File lda = File.createTempFile("lda", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(lda));
writer.write("0 0 100 0 0\n");
writer.close();
MultinomialHMM hmm = new MultinomialHMM(1, lda.getAbsolutePath(), 1);
assertTrue(hmm.maxTokensPerTopic[0] >= 100);
lda.delete();
}

@Test
public void testAllDocumentsFromSameSequenceAreHandledCorrectly() throws IOException {
File state = File.createTempFile("lda_seq", ".txt");
BufferedWriter sw = new BufferedWriter(new FileWriter(state));
sw.write("0 0 1 0 0\n");
sw.write("1 0 1 0 1\n");
sw.write("2 0 1 0 2\n");
sw.write("3 0 1 0 1\n");
sw.write("4 0 1 0 2\n");
sw.close();
File seq = File.createTempFile("sequence", ".txt");
BufferedWriter seqW = new BufferedWriter(new FileWriter(seq));
seqW.write("0\tSAME\n");
seqW.write("1\tSAME\n");
seqW.write("2\tSAME\n");
seqW.write("3\tSAME\n");
seqW.write("4\tSAME\n");
seqW.close();
File alpha = File.createTempFile("alpha_seq", ".txt");
BufferedWriter aWriter = new BufferedWriter(new FileWriter(alpha));
aWriter.write("0 1.0 tok\n");
aWriter.close();
MultinomialHMM hmm = new MultinomialHMM(1, state.getAbsolutePath(), 1);
hmm.loadSequenceIDsFromFile(seq.getAbsolutePath());
hmm.loadAlphaFromFile(alpha.getAbsolutePath());
hmm.setGamma(1.0);
hmm.setRandomSeed(77);
hmm.initialize();
assertEquals(5, hmm.documentStates.length);
state.delete();
seq.delete();
alpha.delete();
}

@Test
public void testSamplingWithTopicNotInAlphaCache() throws IOException {
File lda = File.createTempFile("lda_missing_topic", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(lda));
writer.write("0 0 1 0 0\n");
writer.write("1 0 1 0 4\n");
writer.close();
File alpha = File.createTempFile("alpha_truncated", ".txt");
BufferedWriter alphaWriter = new BufferedWriter(new FileWriter(alpha));
alphaWriter.write("0 1.0 tok0\n");
alphaWriter.write("1 1.0 tok1\n");
alphaWriter.write("2 1.0 tok2\n");
alphaWriter.close();
File seq = File.createTempFile("seq_truncated", ".txt");
BufferedWriter sWriter = new BufferedWriter(new FileWriter(seq));
sWriter.write("0\tSEQ\n");
sWriter.write("1\tSEQ\n");
sWriter.close();
MultinomialHMM hmm = new MultinomialHMM(5, lda.getAbsolutePath(), 1);
hmm.loadAlphaFromFile(alpha.getAbsolutePath());
hmm.loadSequenceIDsFromFile(seq.getAbsolutePath());
hmm.setGamma(1.0);
hmm.setRandomSeed(10);
hmm.initialize();
assertEquals(2, hmm.documentStates.length);
lda.delete();
alpha.delete();
seq.delete();
}

@Test
public void testTopicLogGammaCacheAllocationWithVariousTokens() throws IOException {
File stateFile = File.createTempFile("lda_loggamma", ".txt");
BufferedWriter w = new BufferedWriter(new FileWriter(stateFile));
w.write("0 0 1 0 0\n");
w.write("0 0 2 0 0\n");
w.write("0 0 3 0 1\n");
w.close();
MultinomialHMM hmm = new MultinomialHMM(2, stateFile.getAbsolutePath(), 3);
assertTrue(hmm.topicLogGammaCache.length == 3);
assertTrue(hmm.topicLogGammaCache[0].length == 2);
assertTrue(hmm.topicLogGammaCache[0][0].length > 0);
stateFile.delete();
}

@Test
public void testDocLogGammaCacheHasValidStructure() throws IOException {
File file = File.createTempFile("lda_cache", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(file));
writer.write("0 0 2 0 0\n");
writer.write("0 1 2 0 1\n");
writer.write("0 2 1 0 1\n");
writer.close();
MultinomialHMM hmm = new MultinomialHMM(2, file.getAbsolutePath(), 2);
assertTrue(hmm.docLogGammaCache.length == 2);
assertTrue(hmm.docLogGammaCache[0].length == hmm.maxDocLength + 1);
file.delete();
}

@Test
public void testLoadTopicsSkipsLineWithFewerThanFiveFields() throws IOException {
File file = File.createTempFile("lda_truncated", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(file));
writer.write("0 0 1 0\n");
writer.write("1 0 2 0 1\n");
writer.close();
MultinomialHMM hmm = new MultinomialHMM(2, file.getAbsolutePath(), 2);
assertEquals(2, hmm.numDocs);
file.delete();
}

@Test
public void testSamplingHandlesNoTopicsInDocumentGracefully() throws IOException {
File state = File.createTempFile("lda_missingdoc", ".txt");
BufferedWriter w = new BufferedWriter(new FileWriter(state));
w.write("1 0 1 0 0\n");
w.write("1 1 1 0 1\n");
w.close();
File seq = File.createTempFile("seq", ".txt");
BufferedWriter s = new BufferedWriter(new FileWriter(seq));
s.write("0\tX\n");
s.write("1\tX\n");
s.close();
File alpha = File.createTempFile("alpha", ".txt");
BufferedWriter a = new BufferedWriter(new FileWriter(alpha));
a.write("0 1.0 a\n");
a.write("1 1.0 b\n");
a.close();
MultinomialHMM hmm = new MultinomialHMM(2, state.getAbsolutePath(), 2);
hmm.loadAlphaFromFile(alpha.getAbsolutePath());
hmm.loadSequenceIDsFromFile(seq.getAbsolutePath());
hmm.setGamma(1.0);
hmm.setRandomSeed(111);
hmm.initialize();
assertEquals(2, hmm.documentStates.length);
state.delete();
alpha.delete();
seq.delete();
}

@Test
public void testAlphaLineWithOnlyTopicIdAndNoWords() throws IOException {
File lda = File.createTempFile("lda", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(lda));
writer.write("0 0 1 0 0\n");
writer.close();
File alpha = File.createTempFile("alpha_emptykey", ".txt");
BufferedWriter a = new BufferedWriter(new FileWriter(alpha));
a.write("0 1.0\n");
a.write("1 1.0 keyword\n");
a.close();
MultinomialHMM hmm = new MultinomialHMM(2, lda.getAbsolutePath(), 2);
hmm.loadAlphaFromFile(alpha.getAbsolutePath());
assertEquals("", hmm.topicKeys[0]);
assertEquals("keyword ", hmm.topicKeys[1]);
lda.delete();
alpha.delete();
}

@Test
public void testSamplingSingleDocumentMultipleStates() throws IOException {
File state = File.createTempFile("lda", ".txt");
BufferedWriter sw = new BufferedWriter(new FileWriter(state));
sw.write("0 0 1 0 0\n");
sw.write("0 1 1 0 1\n");
sw.close();
File sequence = File.createTempFile("seq", ".txt");
BufferedWriter sWriter = new BufferedWriter(new FileWriter(sequence));
sWriter.write("0\tS\n");
sWriter.close();
File alpha = File.createTempFile("alpha", ".txt");
BufferedWriter aWriter = new BufferedWriter(new FileWriter(alpha));
aWriter.write("0 1.0 w0\n");
aWriter.write("1 1.0 w1\n");
aWriter.close();
MultinomialHMM hmm = new MultinomialHMM(2, state.getAbsolutePath(), 3);
hmm.loadSequenceIDsFromFile(sequence.getAbsolutePath());
hmm.loadAlphaFromFile(alpha.getAbsolutePath());
hmm.setGamma(0.1);
hmm.setRandomSeed(7);
hmm.initialize();
assertTrue(hmm.documentStates[0] >= 0 && hmm.documentStates[0] < 3);
state.delete();
sequence.delete();
alpha.delete();
}

@Test
public void testAlphaFileWithLargeValuesIsParsedCorrectly() throws IOException {
File lda = File.createTempFile("lda_large", ".txt");
BufferedWriter w = new BufferedWriter(new FileWriter(lda));
w.write("0 0 1 0 0\n");
w.close();
File alpha = File.createTempFile("alpha_large", ".txt");
BufferedWriter aWriter = new BufferedWriter(new FileWriter(alpha));
aWriter.write("0 99999.9 alphaBig\n");
aWriter.write("1 88888.88 betaBig\n");
aWriter.close();
MultinomialHMM hmm = new MultinomialHMM(2, lda.getAbsolutePath(), 2);
hmm.loadAlphaFromFile(alpha.getAbsolutePath());
assertEquals("alphaBig ", hmm.topicKeys[0]);
assertEquals("betaBig ", hmm.topicKeys[1]);
assertEquals(1.0, hmm.alpha[0], 0.0001);
lda.delete();
alpha.delete();
}

@Test
public void testDocumentStatesArraySizeMatchesNumDocs() throws IOException {
File state = File.createTempFile("lda", ".txt");
BufferedWriter sw = new BufferedWriter(new FileWriter(state));
sw.write("0 0 1 0 0\n");
sw.write("4 0 2 0 1\n");
sw.close();
MultinomialHMM hmm = new MultinomialHMM(2, state.getAbsolutePath(), 2);
assertEquals(5, hmm.documentStates.length);
assertEquals(5, hmm.documentSequenceIDs.length);
state.delete();
}

@Test
public void testSamplingHandlesAlphaSumZeroGracefully() throws IOException {
File stateFile = File.createTempFile("lda", ".txt");
BufferedWriter w = new BufferedWriter(new FileWriter(stateFile));
w.write("0 0 1 0 0\n");
w.close();
File alphaFile = File.createTempFile("alpha_zerosum", ".txt");
BufferedWriter a = new BufferedWriter(new FileWriter(alphaFile));
a.write("0 0.0 zero\n");
a.write("1 0.0 none\n");
a.close();
File seqFile = File.createTempFile("seq", ".txt");
BufferedWriter s = new BufferedWriter(new FileWriter(seqFile));
s.write("0\tSEQ\n");
s.close();
MultinomialHMM hmm = new MultinomialHMM(2, stateFile.getAbsolutePath(), 1);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.alpha[0] = 0.0;
hmm.alpha[1] = 0.0;
hmm.alphaSum = 0.0;
hmm.loadSequenceIDsFromFile(seqFile.getAbsolutePath());
hmm.setGamma(1.0);
hmm.setRandomSeed(1);
hmm.initialize();
assertEquals(1, hmm.documentStates.length);
stateFile.delete();
alphaFile.delete();
seqFile.delete();
}

@Test
public void testDocumentWithAllTopicsTheSameStillInitializes() throws IOException {
File lda = File.createTempFile("lda", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(lda));
writer.write("0 0 5 0 1\n");
writer.write("0 1 5 0 1\n");
writer.close();
File alpha = File.createTempFile("alpha_same", ".txt");
BufferedWriter alphaWriter = new BufferedWriter(new FileWriter(alpha));
alphaWriter.write("0 1.0 same\n");
alphaWriter.write("1 1.0 same\n");
alphaWriter.close();
File seq = File.createTempFile("seq", ".txt");
BufferedWriter seqWriter = new BufferedWriter(new FileWriter(seq));
seqWriter.write("0\tX\n");
seqWriter.close();
MultinomialHMM hmm = new MultinomialHMM(2, lda.getAbsolutePath(), 1);
hmm.loadAlphaFromFile(alpha.getAbsolutePath());
hmm.loadSequenceIDsFromFile(seq.getAbsolutePath());
hmm.setGamma(1.0);
hmm.setRandomSeed(999);
hmm.initialize();
assertTrue(hmm.documentStates[0] >= 0);
lda.delete();
alpha.delete();
seq.delete();
}

@Test
public void testEmptyAlphaFileResultsInZeroAlphaSum() throws IOException {
File stateFile = File.createTempFile("lda_empty_alpha", ".txt");
BufferedWriter stateWriter = new BufferedWriter(new FileWriter(stateFile));
stateWriter.write("0 0 1 0 0\n");
stateWriter.close();
File alphaFile = File.createTempFile("alpha_empty", ".txt");
BufferedWriter alphaWriter = new BufferedWriter(new FileWriter(alphaFile));
alphaWriter.close();
MultinomialHMM hmm = new MultinomialHMM(3, stateFile.getAbsolutePath(), 2);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
assertEquals(0.0, hmm.alphaSum, 0.0001);
stateFile.delete();
alphaFile.delete();
}

@Test
public void testSingleDocumentSingleTopicSingleState() throws IOException {
File stateFile = File.createTempFile("lda_single", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(stateFile));
writer.write("0 0 3 0 0\n");
writer.close();
File alphaFile = File.createTempFile("alpha_single", ".txt");
BufferedWriter alphaWriter = new BufferedWriter(new FileWriter(alphaFile));
alphaWriter.write("0 1.0 tokA\n");
alphaWriter.close();
File seqFile = File.createTempFile("seq_single", ".txt");
BufferedWriter seqWriter = new BufferedWriter(new FileWriter(seqFile));
seqWriter.write("0\tS0\n");
seqWriter.close();
MultinomialHMM hmm = new MultinomialHMM(1, stateFile.getAbsolutePath(), 1);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(seqFile.getAbsolutePath());
hmm.setGamma(0.5);
hmm.setRandomSeed(123);
hmm.initialize();
assertEquals(1, hmm.documentStates.length);
assertEquals(1, hmm.initialStateCounts.length);
assertEquals(1, hmm.numSequences);
stateFile.delete();
alphaFile.delete();
seqFile.delete();
}

@Test
public void testNonConsecutiveDocumentIdsHandleCorrectly() throws IOException {
File stateFile = File.createTempFile("lda_gap", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(stateFile));
writer.write("2 0 1 0 0\n");
writer.write("5 0 1 0 1\n");
writer.close();
MultinomialHMM hmm = new MultinomialHMM(2, stateFile.getAbsolutePath(), 2);
assertEquals(6, hmm.numDocs);
assertEquals(6, hmm.documentStates.length);
assertEquals(6, hmm.documentSequenceIDs.length);
stateFile.delete();
}

@Test
public void testLoadAlphaSkipsLineWithTooFewFields() throws IOException {
File state = File.createTempFile("lda_valid", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(state));
writer.write("0 0 5 0 0\n");
writer.close();
File alpha = File.createTempFile("alpha_invalid", ".txt");
BufferedWriter aw = new BufferedWriter(new FileWriter(alpha));
aw.write("0 1.0\n");
aw.write("1\n");
aw.write("2 1.0 tokA\n");
aw.close();
MultinomialHMM hmm = new MultinomialHMM(3, state.getAbsolutePath(), 2);
try {
hmm.loadAlphaFromFile(alpha.getAbsolutePath());
} catch (Exception e) {
fail("Should not throw on line with too few fields when parsing alpha");
}
assertEquals(3, hmm.topicKeys.length);
state.delete();
alpha.delete();
}

@Test
public void testShortAlphaDoesNotFailForExtraTopics() throws IOException {
File lda = File.createTempFile("lda_has3topics", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(lda));
writer.write("0 0 1 0 0\n");
writer.write("0 1 1 0 1\n");
writer.write("0 2 1 0 2\n");
writer.close();
File alpha = File.createTempFile("alpha_too_short", ".txt");
BufferedWriter aw = new BufferedWriter(new FileWriter(alpha));
aw.write("0 1.0 one\n");
aw.write("1 1.0 two\n");
aw.close();
MultinomialHMM hmm = new MultinomialHMM(3, lda.getAbsolutePath(), 1);
hmm.loadAlphaFromFile(alpha.getAbsolutePath());
assertNotNull(hmm.alpha);
assertNull(hmm.topicKeys[2]);
lda.delete();
alpha.delete();
}

@Test
public void testStateTransitionMatrixWithZeroTransitions() throws IOException {
File lda = File.createTempFile("lda_zero_transitions", ".txt");
BufferedWriter w = new BufferedWriter(new FileWriter(lda));
w.write("0 0 2 0 0\n");
w.close();
MultinomialHMM hmm = new MultinomialHMM(2, lda.getAbsolutePath(), 2);
String matrix = hmm.stateTransitionMatrix();
String[] lines = matrix.trim().split("\n");
assertEquals(2, lines.length);
assertTrue(lines[0].trim().equals("0\t0"));
assertTrue(lines[1].trim().equals("0\t0"));
lda.delete();
}

@Test
public void testSingleTransitionIsCountedCorrectly() throws IOException {
File state = File.createTempFile("lda_tran", ".txt");
BufferedWriter s = new BufferedWriter(new FileWriter(state));
s.write("0 0 1 0 0\n");
s.write("1 0 1 0 1\n");
s.close();
File alpha = File.createTempFile("alpha", ".txt");
BufferedWriter a = new BufferedWriter(new FileWriter(alpha));
a.write("0 1.0 word\n");
a.close();
File seq = File.createTempFile("seq", ".txt");
BufferedWriter q = new BufferedWriter(new FileWriter(seq));
q.write("0\tSEQ1\n");
q.write("1\tSEQ1\n");
q.close();
MultinomialHMM hmm = new MultinomialHMM(2, state.getAbsolutePath(), 2);
hmm.loadAlphaFromFile(alpha.getAbsolutePath());
hmm.loadSequenceIDsFromFile(seq.getAbsolutePath());
hmm.setRandomSeed(1234);
hmm.setGamma(0.5);
hmm.initialize();
String transition = hmm.stateTransitionMatrix();
assertTrue(transition.contains("1") || transition.contains("0\t1"));
state.delete();
alpha.delete();
seq.delete();
}

@Test
public void testStateTopicsWithOnlyOneTopic() throws IOException {
File lda = File.createTempFile("lda_singletopic", ".txt");
BufferedWriter w = new BufferedWriter(new FileWriter(lda));
w.write("0 0 3 0 0\n");
w.write("1 0 3 0 0\n");
w.close();
MultinomialHMM hmm = new MultinomialHMM(1, lda.getAbsolutePath(), 1);
String topics = hmm.stateTopics();
String[] lines = topics.trim().split("\n");
assertEquals(1, lines.length);
assertTrue(lines[0].matches("\\d+"));
lda.delete();
}

@Test
public void testPrintStateTransitionsContentFormat() throws IOException {
File lda = File.createTempFile("lda_print", ".txt");
BufferedWriter w = new BufferedWriter(new FileWriter(lda));
w.write("0 0 1 0 0\n");
w.write("1 0 1 0 1\n");
w.close();
File alphaFile = File.createTempFile("alpha_print", ".txt");
BufferedWriter a = new BufferedWriter(new FileWriter(alphaFile));
a.write("0 1.0 hello\n");
a.write("1 1.0 world\n");
a.close();
File seqFile = File.createTempFile("seq_print", ".txt");
BufferedWriter q = new BufferedWriter(new FileWriter(seqFile));
q.write("0\tA\n");
q.write("1\tA\n");
q.close();
MultinomialHMM hmm = new MultinomialHMM(2, lda.getAbsolutePath(), 2);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(seqFile.getAbsolutePath());
hmm.setRandomSeed(99);
hmm.setGamma(1.0);
hmm.initialize();
String result = hmm.printStateTransitions();
assertTrue(result.contains("[0/"));
assertTrue(result.contains("hello ") || result.contains("world "));
lda.delete();
alphaFile.delete();
seqFile.delete();
}
}
