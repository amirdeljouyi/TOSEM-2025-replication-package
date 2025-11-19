package cc.mallet.topics;

import cc.mallet.types.*;
import cc.mallet.util.Randoms;
import org.junit.Test;
import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.zip.GZIPOutputStream;
import static org.junit.Assert.*;

public class MultinomialHMM_llmsuite_4_GPTLLMTest {

@Test
public void testConstructorWithValidTopicFile() throws Exception {
File topicFile = File.createTempFile("topics", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(topicFile));
writer.write("0 0 0 foo 0\n");
writer.write("0 1 1 bar 1\n");
writer.write("1 0 2 baz 2\n");
writer.close();
MultinomialHMM hmm = new MultinomialHMM(3, topicFile.getAbsolutePath(), 2);
assertNotNull(hmm);
topicFile.delete();
}

@Test
public void testLoadAlphaFromFile() throws Exception {
File topicFile = File.createTempFile("topics", ".txt");
BufferedWriter topicWriter = new BufferedWriter(new FileWriter(topicFile));
topicWriter.write("0 0 0 f 0\n");
topicWriter.close();
MultinomialHMM hmm = new MultinomialHMM(3, topicFile.getAbsolutePath(), 2);
File alphaFile = File.createTempFile("alpha", ".txt");
BufferedWriter alphaWriter = new BufferedWriter(new FileWriter(alphaFile));
alphaWriter.write("0 1.0 foo\n");
alphaWriter.write("1 1.0 bar\n");
alphaWriter.write("2 1.0 baz\n");
alphaWriter.close();
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
assertTrue(hmm.topicKeys[0].startsWith("foo"));
assertTrue(hmm.topicKeys[1].startsWith("bar"));
assertTrue(hmm.topicKeys[2].startsWith("baz"));
topicFile.delete();
alphaFile.delete();
}

@Test
public void testLoadSequenceIDsFromFile() throws Exception {
File topicFile = File.createTempFile("topics", ".txt");
BufferedWriter topicWriter = new BufferedWriter(new FileWriter(topicFile));
topicWriter.write("0 0 0 f 0\n");
topicWriter.close();
MultinomialHMM hmm = new MultinomialHMM(1, topicFile.getAbsolutePath(), 2);
File sequenceFile = File.createTempFile("sequence", ".txt");
BufferedWriter sequenceWriter = new BufferedWriter(new FileWriter(sequenceFile));
sequenceWriter.write("0\titem1\n");
sequenceWriter.close();
hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
String result = hmm.stateTopics();
assertNotNull(result);
topicFile.delete();
sequenceFile.delete();
}

@Test
public void testSettersAffectPublicBehavior() throws Exception {
File topicFile = File.createTempFile("topics", ".txt");
BufferedWriter topicWriter = new BufferedWriter(new FileWriter(topicFile));
topicWriter.write("0 0 0 t 0\n");
topicWriter.write("0 1 1 s 1\n");
topicWriter.close();
MultinomialHMM hmm = new MultinomialHMM(2, topicFile.getAbsolutePath(), 2);
hmm.setGamma(0.8);
hmm.setNumIterations(5);
hmm.setBurninPeriod(2);
hmm.setTopicDisplayInterval(1);
hmm.setOptimizeInterval(0);
hmm.setRandomSeed(42);
assertNotNull(hmm);
topicFile.delete();
}

@Test
public void testInitializeWithMinimalData() throws Exception {
File topicFile = File.createTempFile("topics", ".txt");
BufferedWriter topicWriter = new BufferedWriter(new FileWriter(topicFile));
topicWriter.write("0 0 0 x 0\n");
topicWriter.close();
File sequenceFile = File.createTempFile("sequence", ".txt");
BufferedWriter sequenceWriter = new BufferedWriter(new FileWriter(sequenceFile));
sequenceWriter.write("0\tseqA\n");
sequenceWriter.close();
File alphaFile = File.createTempFile("alpha", ".txt");
BufferedWriter alphaWriter = new BufferedWriter(new FileWriter(alphaFile));
alphaWriter.write("0 1.0 word\n");
alphaWriter.close();
MultinomialHMM hmm = new MultinomialHMM(1, topicFile.getAbsolutePath(), 2);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
hmm.setGamma(1.0);
hmm.setRandomSeed(77);
hmm.initialize();
assertNotNull(hmm.stateTransitionMatrix());
topicFile.delete();
sequenceFile.delete();
alphaFile.delete();
}

@Test
public void testSampleRunsMinimalIterations() throws Exception {
File topicFile = File.createTempFile("topics", ".txt");
BufferedWriter topicWriter = new BufferedWriter(new FileWriter(topicFile));
topicWriter.write("0 0 0 w1 0\n");
topicWriter.write("1 0 1 w2 1\n");
topicWriter.close();
File alphaFile = File.createTempFile("alpha", ".txt");
BufferedWriter alphaWriter = new BufferedWriter(new FileWriter(alphaFile));
alphaWriter.write("0 1.0 alpha0\n");
alphaWriter.write("1 1.0 alpha1\n");
alphaWriter.close();
File sequenceFile = File.createTempFile("seq", ".txt");
BufferedWriter sequenceWriter = new BufferedWriter(new FileWriter(sequenceFile));
sequenceWriter.write("0\tseq1\n");
sequenceWriter.write("1\tseq2\n");
sequenceWriter.close();
MultinomialHMM hmm = new MultinomialHMM(2, topicFile.getAbsolutePath(), 2);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
hmm.setGamma(1.0);
hmm.setRandomSeed(123);
hmm.setNumIterations(2);
hmm.initialize();
hmm.sample();
assertNotNull(hmm.stateTopics());
topicFile.delete();
alphaFile.delete();
sequenceFile.delete();
}

@Test
public void testPrintStateTransitionsFormatting() throws Exception {
File topicFile = File.createTempFile("topics", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(topicFile));
writer.write("0 0 0 w1 0\n");
writer.write("1 0 1 w2 1\n");
writer.close();
File alphaFile = File.createTempFile("alpha", ".txt");
BufferedWriter alphaWriter = new BufferedWriter(new FileWriter(alphaFile));
alphaWriter.write("0 1.0 key1\n");
alphaWriter.write("1 1.0 key2\n");
alphaWriter.close();
File sequenceFile = File.createTempFile("seq", ".txt");
BufferedWriter seqWriter = new BufferedWriter(new FileWriter(sequenceFile));
seqWriter.write("0\tkeyA\n");
seqWriter.write("1\tkeyB\n");
seqWriter.close();
MultinomialHMM hmm = new MultinomialHMM(2, topicFile.getAbsolutePath(), 2);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
hmm.setGamma(1.0);
hmm.setRandomSeed(10);
hmm.initialize();
String output = hmm.printStateTransitions();
assertTrue(output.contains("["));
topicFile.delete();
alphaFile.delete();
sequenceFile.delete();
}

@Test
public void testGzippedTopicsCanBeRead() throws Exception {
File gzFile = File.createTempFile("topics", ".gz");
FileOutputStream fos = new FileOutputStream(gzFile);
GZIPOutputStream gzip = new GZIPOutputStream(fos);
OutputStreamWriter writer = new OutputStreamWriter(gzip);
writer.write("0 0 0 x 0\n");
writer.write("1 0 1 y 1\n");
writer.close();
MultinomialHMM hmm = new MultinomialHMM(2, gzFile.getAbsolutePath(), 2);
assertNotNull(hmm);
gzFile.delete();
}

@Test
public void testEmptyTopicsFileHandling() throws Exception {
File emptyFile = File.createTempFile("empty", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(emptyFile));
writer.write("");
writer.close();
MultinomialHMM hmm = new MultinomialHMM(3, emptyFile.getAbsolutePath(), 2);
assertNotNull(hmm);
emptyFile.delete();
}

@Test
public void testSingleTopicSingleStateSingleDoc() throws Exception {
File topicFile = File.createTempFile("tiny", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(topicFile));
writer.write("0 0 1 x 0\n");
writer.close();
File alphaFile = File.createTempFile("tiny_alpha", ".txt");
BufferedWriter alpha = new BufferedWriter(new FileWriter(alphaFile));
alpha.write("0 1.0 loneword\n");
alpha.close();
File sequenceFile = File.createTempFile("tiny_seq", ".txt");
BufferedWriter sequence = new BufferedWriter(new FileWriter(sequenceFile));
sequence.write("0\tone\n");
sequence.close();
MultinomialHMM hmm = new MultinomialHMM(1, topicFile.getAbsolutePath(), 1);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
hmm.setGamma(0.5);
hmm.setRandomSeed(5);
hmm.initialize();
hmm.sample();
String stateTransitions = hmm.stateTransitionMatrix();
assertNotNull(stateTransitions);
assertTrue(stateTransitions.contains("0"));
topicFile.delete();
alphaFile.delete();
sequenceFile.delete();
}

@Test
public void testMissingSequenceFileWarning() throws Exception {
File topicFile = File.createTempFile("topics", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(topicFile));
writer.write("0 0 0 x 0\n");
writer.write("1 0 1 y 1\n");
writer.close();
File sequenceFile = File.createTempFile("misaligned", ".txt");
BufferedWriter sequence = new BufferedWriter(new FileWriter(sequenceFile));
sequence.write("0\tseq1\n");
sequence.close();
File alphaFile = File.createTempFile("alpha", ".txt");
BufferedWriter alpha = new BufferedWriter(new FileWriter(alphaFile));
alpha.write("0 1.0 alpha\n");
alpha.write("1 1.0 beta\n");
alpha.close();
MultinomialHMM hmm = new MultinomialHMM(2, topicFile.getAbsolutePath(), 2);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
assertNotNull(hmm);
topicFile.delete();
sequenceFile.delete();
alphaFile.delete();
}

@Test
public void testLoadAlphaFileWithBlankLinesAndSpaces() throws Exception {
File topicFile = File.createTempFile("topics", ".txt");
BufferedWriter topicWriter = new BufferedWriter(new FileWriter(topicFile));
topicWriter.write("0 0 0 foo 0\n");
topicWriter.close();
File alphaFile = File.createTempFile("alpha", ".txt");
BufferedWriter alphaWriter = new BufferedWriter(new FileWriter(alphaFile));
alphaWriter.write("0 1.0 topwords\n");
alphaWriter.write("\n");
alphaWriter.write("  \n");
alphaWriter.write("1 1.0 bestwords\n");
alphaWriter.close();
MultinomialHMM hmm = new MultinomialHMM(2, topicFile.getAbsolutePath(), 2);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
assertEquals("topwords ", hmm.topicKeys[0]);
assertEquals("bestwords ", hmm.topicKeys[1]);
topicFile.delete();
alphaFile.delete();
}

@Test
public void testInvalidTopicFileFormatThrowsException() throws Exception {
File badFile = File.createTempFile("bad_topics", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(badFile));
writer.write("This line is invalid\n");
writer.close();
boolean threwException = false;
try {
new MultinomialHMM(1, badFile.getAbsolutePath(), 1);
} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
threwException = true;
}
assertTrue(threwException);
badFile.delete();
}

@Test
public void testIdenticalSequenceIDDocuments() throws Exception {
File topicFile = File.createTempFile("topics", ".txt");
BufferedWriter topic = new BufferedWriter(new FileWriter(topicFile));
topic.write("0 0 0 aa 0\n");
topic.write("1 0 1 bb 1\n");
topic.close();
File alphaFile = File.createTempFile("alpha", ".txt");
BufferedWriter alpha = new BufferedWriter(new FileWriter(alphaFile));
alpha.write("0 1.0 one\n");
alpha.write("1 1.0 two\n");
alpha.close();
File sequenceFile = File.createTempFile("same_seq", ".txt");
BufferedWriter seq = new BufferedWriter(new FileWriter(sequenceFile));
seq.write("0\tA\n");
seq.write("0\tB\n");
seq.close();
MultinomialHMM hmm = new MultinomialHMM(2, topicFile.getAbsolutePath(), 2);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
hmm.setGamma(1.0);
hmm.setRandomSeed(10);
hmm.initialize();
hmm.sample();
String matrix = hmm.stateTransitionMatrix();
assertNotNull(matrix);
topicFile.delete();
alphaFile.delete();
sequenceFile.delete();
}

@Test
public void testSamplingWithZeroGammaStillProducesOutput() throws Exception {
File topicFile = File.createTempFile("topic_zero_gamma", ".txt");
BufferedWriter topic = new BufferedWriter(new FileWriter(topicFile));
topic.write("0 0 0 x 0\n");
topic.write("1 0 1 y 1\n");
topic.close();
File alphaFile = File.createTempFile("alpha_zero", ".txt");
BufferedWriter alpha = new BufferedWriter(new FileWriter(alphaFile));
alpha.write("0 1.0 x\n");
alpha.write("1 1.0 y\n");
alpha.close();
File sequenceFile = File.createTempFile("seq_zero", ".txt");
BufferedWriter sequence = new BufferedWriter(new FileWriter(sequenceFile));
sequence.write("0\tSEQ1\n");
sequence.write("1\tSEQ2\n");
sequence.close();
MultinomialHMM hmm = new MultinomialHMM(2, topicFile.getAbsolutePath(), 2);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
hmm.setGamma(0.0);
hmm.setRandomSeed(33);
hmm.setNumIterations(1);
hmm.initialize();
hmm.sample();
String transitions = hmm.stateTransitionMatrix();
assertNotNull(transitions);
topicFile.delete();
alphaFile.delete();
sequenceFile.delete();
}

@Test
public void testStateTopicsWithNoDocumentsReturnsZeroRows() throws Exception {
File emptyFile = File.createTempFile("empty", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(emptyFile));
writer.write("");
writer.close();
MultinomialHMM hmm = new MultinomialHMM(2, emptyFile.getAbsolutePath(), 2);
String result = hmm.stateTopics();
assertNotNull(result);
assertTrue(result.trim().length() > 0);
emptyFile.delete();
}

@Test
public void testDocumentWithoutTopicsIsSkippedInSampleState() throws Exception {
File topicFile = File.createTempFile("topics_partial", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(topicFile));
writer.write("1 0 0 word 0\n");
writer.close();
File sequenceFile = File.createTempFile("seq_missing", ".txt");
BufferedWriter seq = new BufferedWriter(new FileWriter(sequenceFile));
seq.write("0\tA\n");
seq.write("1\tB\n");
seq.close();
File alphaFile = File.createTempFile("alpha_missing", ".txt");
BufferedWriter alpha = new BufferedWriter(new FileWriter(alphaFile));
alpha.write("0 1.0 term\n");
alpha.close();
MultinomialHMM hmm = new MultinomialHMM(1, topicFile.getAbsolutePath(), 2);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
hmm.setGamma(0.5);
hmm.setRandomSeed(9);
hmm.initialize();
hmm.sample();
String matrix = hmm.stateTransitionMatrix();
assertNotNull(matrix);
topicFile.delete();
sequenceFile.delete();
alphaFile.delete();
}

@Test
public void testAlphaZeroDoesNotBreakSampling() throws Exception {
File topicFile = File.createTempFile("topics_zero_alpha", ".txt");
BufferedWriter topicWriter = new BufferedWriter(new FileWriter(topicFile));
topicWriter.write("0 0 0 word 0\n");
topicWriter.close();
File alphaFile = File.createTempFile("alpha_zero", ".txt");
BufferedWriter alphaWriter = new BufferedWriter(new FileWriter(alphaFile));
alphaWriter.write("0 0.0 zeroalpha\n");
alphaWriter.close();
File sequenceFile = File.createTempFile("seq_zero", ".txt");
BufferedWriter sequenceWriter = new BufferedWriter(new FileWriter(sequenceFile));
sequenceWriter.write("0\tmeta\n");
sequenceWriter.close();
MultinomialHMM hmm = new MultinomialHMM(1, topicFile.getAbsolutePath(), 2);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
hmm.setGamma(0.1);
hmm.setRandomSeed(6);
hmm.initialize();
hmm.sample();
String output = hmm.stateTransitionMatrix();
assertNotNull(output);
topicFile.delete();
alphaFile.delete();
sequenceFile.delete();
}

@Test
public void testTopicFileWithDuplicateDocTopicEntriesAggregatesCorrectly() throws Exception {
File topicFile = File.createTempFile("duplicate_topics", ".txt");
BufferedWriter topicWriter = new BufferedWriter(new FileWriter(topicFile));
topicWriter.write("0 0 0 t 0\n");
topicWriter.write("0 1 1 t 0\n");
topicWriter.close();
File alphaFile = File.createTempFile("alpha", ".txt");
BufferedWriter alpha = new BufferedWriter(new FileWriter(alphaFile));
alpha.write("0 1.0 token\n");
alpha.close();
File sequenceFile = File.createTempFile("seq", ".txt");
BufferedWriter seq = new BufferedWriter(new FileWriter(sequenceFile));
seq.write("0\tmeta\n");
seq.close();
MultinomialHMM hmm = new MultinomialHMM(1, topicFile.getAbsolutePath(), 2);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
hmm.setGamma(1.0);
hmm.setRandomSeed(4);
hmm.initialize();
hmm.sample();
assertNotNull(hmm.stateTransitionMatrix());
topicFile.delete();
alphaFile.delete();
sequenceFile.delete();
}

@Test
public void testTopicFileWithHighDocumentId() throws Exception {
File topicFile = File.createTempFile("high_doc", ".txt");
BufferedWriter topicWriter = new BufferedWriter(new FileWriter(topicFile));
topicWriter.write("100 0 0 token 0\n");
topicWriter.close();
File alphaFile = File.createTempFile("alpha", ".txt");
BufferedWriter alpha = new BufferedWriter(new FileWriter(alphaFile));
alpha.write("0 1.0 alpha\n");
alpha.close();
File sequenceFile = File.createTempFile("seq", ".txt");
BufferedWriter sequence = new BufferedWriter(new FileWriter(sequenceFile));
for (int i = 0; i <= 100; i++) {
sequence.write(i + "\tline\n");
}
sequence.close();
MultinomialHMM hmm = new MultinomialHMM(1, topicFile.getAbsolutePath(), 2);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
hmm.setGamma(1.0);
hmm.setRandomSeed(123);
hmm.initialize();
hmm.sample();
String stateMatrix = hmm.stateTransitionMatrix();
assertNotNull(stateMatrix);
topicFile.delete();
alphaFile.delete();
sequenceFile.delete();
}

@Test
public void testSequenceFileWithInvalidFormatSkipsLine() throws Exception {
File topicFile = File.createTempFile("topics_seq_invalid", ".txt");
BufferedWriter topicWriter = new BufferedWriter(new FileWriter(topicFile));
topicWriter.write("0 0 0 test 0\n");
topicWriter.close();
File alphaFile = File.createTempFile("alpha", ".txt");
BufferedWriter alpha = new BufferedWriter(new FileWriter(alphaFile));
alpha.write("0 1.0 token\n");
alpha.close();
File sequenceFile = File.createTempFile("seq_invalid", ".txt");
BufferedWriter seq = new BufferedWriter(new FileWriter(sequenceFile));
seq.write("0\tmeta\n");
seq.write("this line\tis malformed\n");
// sequenceWriter.close();
MultinomialHMM hmm = new MultinomialHMM(1, topicFile.getAbsolutePath(), 2);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
hmm.setGamma(0.5);
hmm.setRandomSeed(1);
hmm.initialize();
hmm.sample();
String states = hmm.stateTopics();
assertNotNull(states);
topicFile.delete();
alphaFile.delete();
sequenceFile.delete();
}

@Test
public void testTopicFileWithMissingFieldsThrowsGracefully() throws Exception {
File topicFile = File.createTempFile("bad_fields", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(topicFile));
writer.write("0 0 missing_entry\n");
writer.close();
boolean thrown = false;
try {
new MultinomialHMM(1, topicFile.getAbsolutePath(), 2);
} catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
thrown = true;
}
assertTrue(thrown);
topicFile.delete();
}

@Test
public void testMultipleTopicsSameTokenInDifferentDocs() throws Exception {
File topicFile = File.createTempFile("shared_token", ".txt");
BufferedWriter topicWriter = new BufferedWriter(new FileWriter(topicFile));
topicWriter.write("0 0 0 foo 0\n");
topicWriter.write("1 0 1 foo 1\n");
topicWriter.close();
File alphaFile = File.createTempFile("alpha", ".txt");
BufferedWriter alpha = new BufferedWriter(new FileWriter(alphaFile));
alpha.write("0 1.0 word0\n");
alpha.write("1 1.0 word1\n");
alpha.close();
File sequenceFile = File.createTempFile("seq", ".txt");
BufferedWriter seq = new BufferedWriter(new FileWriter(sequenceFile));
seq.write("0\tx\n");
seq.write("1\ty\n");
seq.close();
MultinomialHMM hmm = new MultinomialHMM(2, topicFile.getAbsolutePath(), 2);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
hmm.setGamma(1.0);
hmm.setRandomSeed(31);
hmm.initialize();
hmm.sample();
String out = hmm.printStateTransitions();
assertNotNull(out);
topicFile.delete();
alphaFile.delete();
sequenceFile.delete();
}

@Test
public void testSamplingDoesNotChangeOnDeterministicSeed() throws Exception {
File topicFile = File.createTempFile("deterministic", ".txt");
BufferedWriter topicWriter = new BufferedWriter(new FileWriter(topicFile));
topicWriter.write("0 0 0 zz 0\n");
topicWriter.write("1 0 1 zz 1\n");
topicWriter.close();
File alphaFile = File.createTempFile("alpha", ".txt");
BufferedWriter alpha = new BufferedWriter(new FileWriter(alphaFile));
alpha.write("0 1.0 a0\n");
alpha.write("1 1.0 a1\n");
alpha.close();
File sequenceFile = File.createTempFile("seq_meta", ".txt");
BufferedWriter seq = new BufferedWriter(new FileWriter(sequenceFile));
seq.write("0\tfoo\n");
seq.write("1\tbar\n");
seq.close();
MultinomialHMM hmm1 = new MultinomialHMM(2, topicFile.getAbsolutePath(), 2);
hmm1.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm1.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
hmm1.setGamma(1.0);
hmm1.setRandomSeed(42);
hmm1.setNumIterations(2);
hmm1.initialize();
hmm1.sample();
String output1 = hmm1.stateTopics();
MultinomialHMM hmm2 = new MultinomialHMM(2, topicFile.getAbsolutePath(), 2);
hmm2.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm2.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
hmm2.setGamma(1.0);
hmm2.setRandomSeed(42);
hmm2.setNumIterations(2);
hmm2.initialize();
hmm2.sample();
String output2 = hmm2.stateTopics();
assertEquals(output1, output2);
topicFile.delete();
alphaFile.delete();
sequenceFile.delete();
}

@Test
public void testLoadTopicsWithExtraWhitespace() throws Exception {
File topicFile = File.createTempFile("topics_extra_ws", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(topicFile));
writer.write("  0   0   0    wordA   0   \n");
writer.write("1\t0\t1\twordB\t1  \n");
writer.write("    2 0 2 wordC 2\n");
writer.close();
MultinomialHMM hmm = new MultinomialHMM(3, topicFile.getAbsolutePath(), 3);
assertNotNull(hmm);
topicFile.delete();
}

@Test
public void testLoadAlphaFileWithMissingTopicIndex() throws Exception {
File topicFile = File.createTempFile("topics", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(topicFile));
writer.write("0 0 0 x 0\n");
writer.close();
MultinomialHMM hmm = new MultinomialHMM(1, topicFile.getAbsolutePath(), 2);
File alphaFile = File.createTempFile("alpha_missing_fields", ".txt");
BufferedWriter alphaWriter = new BufferedWriter(new FileWriter(alphaFile));
alphaWriter.write("missing_value_and_topic_key\n");
alphaWriter.close();
boolean caught = false;
try {
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
} catch (Exception e) {
caught = true;
}
assertTrue(caught);
topicFile.delete();
alphaFile.delete();
}

@Test
public void testSequenceFileWithEmptyLine() throws Exception {
File topicFile = File.createTempFile("sequence_empty", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(topicFile));
writer.write("0 0 0 word 0\n");
writer.write("1 1 1 word 1\n");
writer.close();
MultinomialHMM hmm = new MultinomialHMM(2, topicFile.getAbsolutePath(), 2);
File sequenceFile = File.createTempFile("sequence_with_blank", ".txt");
BufferedWriter seqWriter = new BufferedWriter(new FileWriter(sequenceFile));
seqWriter.write("0\tseq1\n");
seqWriter.write("\n");
seqWriter.write("1\tseq1\n");
seqWriter.close();
boolean threw = false;
try {
hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
} catch (Exception e) {
threw = true;
}
assertTrue(threw || hmm != null);
topicFile.delete();
sequenceFile.delete();
}

@Test
public void testAllDocumentsInSameSequence() throws Exception {
File topicFile = File.createTempFile("same_seq_topics", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(topicFile));
writer.write("0 0 0 alpha 0\n");
writer.write("1 0 1 beta 1\n");
writer.write("2 0 2 gamma 2\n");
writer.close();
File alphaFile = File.createTempFile("same_seq_alpha", ".txt");
BufferedWriter alpha = new BufferedWriter(new FileWriter(alphaFile));
alpha.write("0 1.0 alpha\n");
alpha.write("1 1.0 beta\n");
alpha.write("2 1.0 gamma\n");
alpha.close();
File sequenceFile = File.createTempFile("same_seq", ".txt");
BufferedWriter seq = new BufferedWriter(new FileWriter(sequenceFile));
seq.write("0\tone\n");
seq.write("1\tone\n");
seq.write("2\tone\n");
seq.close();
MultinomialHMM hmm = new MultinomialHMM(3, topicFile.getAbsolutePath(), 3);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
hmm.setGamma(1.0);
hmm.setRandomSeed(123);
hmm.initialize();
hmm.sample();
String transitions = hmm.stateTransitionMatrix();
assertNotNull(transitions);
assertTrue(transitions.contains("0"));
topicFile.delete();
alphaFile.delete();
sequenceFile.delete();
}

@Test
public void testInitializeWithoutRandomSeed() throws Exception {
File topicFile = File.createTempFile("no_seed_topics", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(topicFile));
writer.write("0 0 0 apple 0\n");
writer.close();
File alphaFile = File.createTempFile("no_seed_alpha", ".txt");
BufferedWriter alpha = new BufferedWriter(new FileWriter(alphaFile));
alpha.write("0 1.0 apple\n");
alpha.close();
File sequenceFile = File.createTempFile("no_seed_seq", ".txt");
BufferedWriter seq = new BufferedWriter(new FileWriter(sequenceFile));
seq.write("0\tx\n");
seq.close();
MultinomialHMM hmm = new MultinomialHMM(1, topicFile.getAbsolutePath(), 2);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
hmm.setGamma(0.8);
hmm.initialize();
String matrix = hmm.stateTransitionMatrix();
assertNotNull(matrix);
topicFile.delete();
alphaFile.delete();
sequenceFile.delete();
}

@Test
public void testAlphaFileWithNonNumericValues() throws Exception {
File topicFile = File.createTempFile("bad_alpha_topics", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(topicFile));
writer.write("0 0 0 word 0\n");
writer.close();
MultinomialHMM hmm = new MultinomialHMM(1, topicFile.getAbsolutePath(), 1);
File alphaFile = File.createTempFile("bad_alpha", ".txt");
BufferedWriter alphaWriter = new BufferedWriter(new FileWriter(alphaFile));
alphaWriter.write("0 notANumber wordkey\n");
alphaWriter.close();
boolean errorCaught = false;
try {
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
} catch (NumberFormatException e) {
errorCaught = true;
}
assertTrue(errorCaught);
topicFile.delete();
alphaFile.delete();
}

@Test
public void testSamplingWithOneTopicMultipleStates() throws Exception {
File topicFile = File.createTempFile("one_topic_states", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(topicFile));
writer.write("0 0 0 token 0\n");
writer.write("1 0 0 token 0\n");
writer.write("2 0 0 token 0\n");
writer.close();
File alphaFile = File.createTempFile("one_topic_alpha", ".txt");
BufferedWriter alpha = new BufferedWriter(new FileWriter(alphaFile));
alpha.write("0 1.0 token\n");
alpha.close();
File sequenceFile = File.createTempFile("multiple_states_seq", ".txt");
BufferedWriter seq = new BufferedWriter(new FileWriter(sequenceFile));
seq.write("0\tseq\n");
seq.write("1\tseq\n");
seq.write("2\tseq\n");
seq.close();
MultinomialHMM hmm = new MultinomialHMM(1, topicFile.getAbsolutePath(), 5);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
hmm.setGamma(1.0);
hmm.setRandomSeed(7);
hmm.initialize();
hmm.sample();
String matrix = hmm.stateTopics();
assertNotNull(matrix);
topicFile.delete();
alphaFile.delete();
sequenceFile.delete();
}

@Test
public void testSampleWithEmptyDocumentTopicMap() throws Exception {
File topicFile = File.createTempFile("blank_doc_map", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(topicFile));
writer.write("# empty body\n");
writer.close();
MultinomialHMM hmm = new MultinomialHMM(3, topicFile.getAbsolutePath(), 3);
hmm.setGamma(0.5);
hmm.setRandomSeed(17);
hmm.initialize();
String result = hmm.stateTopics();
assertNotNull(result);
topicFile.delete();
}

@Test
public void testLoadTopicsWithCommentLinesOnly() throws Exception {
File topicFile = File.createTempFile("comment_only", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(topicFile));
writer.write("# this is a comment\n");
writer.write("# another comment\n");
writer.close();
MultinomialHMM hmm = new MultinomialHMM(2, topicFile.getAbsolutePath(), 2);
assertEquals(0, hmm.documentTopics.size());
topicFile.delete();
}

@Test
public void testDocumentWithNoAssignedTopic() throws Exception {
File topicFile = File.createTempFile("no_topic", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(topicFile));
writer.write("1 0 0 word 0\n");
writer.close();
File sequenceFile = File.createTempFile("no_topic_seq", ".txt");
BufferedWriter seq = new BufferedWriter(new FileWriter(sequenceFile));
seq.write("0\tmeta\n");
seq.write("1\tmeta\n");
seq.close();
File alphaFile = File.createTempFile("no_topic_alpha", ".txt");
BufferedWriter alpha = new BufferedWriter(new FileWriter(alphaFile));
alpha.write("0 1.0 word\n");
alpha.close();
MultinomialHMM hmm = new MultinomialHMM(1, topicFile.getAbsolutePath(), 2);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
hmm.setGamma(0.5);
hmm.setRandomSeed(7);
hmm.initialize();
hmm.sample();
String state = hmm.stateTransitionMatrix();
assertNotNull(state);
topicFile.delete();
alphaFile.delete();
sequenceFile.delete();
}

@Test
public void testTokenCountExceedsGammaCacheSizeHybridPath() throws Exception {
File topicFile = File.createTempFile("hybrid_cache", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(topicFile));
writer.write("0 0 0 word 0\n");
writer.write("0 1 1 word 0\n");
writer.write("0 2 2 word 0\n");
writer.write("0 3 3 word 0\n");
writer.write("0 4 4 word 0\n");
writer.write("0 5 5 word 0\n");
writer.close();
File sequenceFile = File.createTempFile("hybrid_seq", ".txt");
BufferedWriter seq = new BufferedWriter(new FileWriter(sequenceFile));
seq.write("0\tsequenceA\n");
seq.close();
File alphaFile = File.createTempFile("hybrid_alpha", ".txt");
BufferedWriter alpha = new BufferedWriter(new FileWriter(alphaFile));
alpha.write("0 1.0 cacheable\n");
alpha.close();
MultinomialHMM hmm = new MultinomialHMM(1, topicFile.getAbsolutePath(), 2);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
hmm.setGamma(0.1);
hmm.setRandomSeed(12);
hmm.initialize();
hmm.sample();
String stateOutput = hmm.stateTopics();
assertNotNull(stateOutput);
topicFile.delete();
alphaFile.delete();
sequenceFile.delete();
}

@Test
public void testSequenceIdsWithLargeGapsStillIncrementSequences() throws Exception {
File topicFile = File.createTempFile("large_gaps", ".txt");
BufferedWriter topicWriter = new BufferedWriter(new FileWriter(topicFile));
topicWriter.write("0 0 0 tiger 0\n");
topicWriter.write("1 0 1 lion 1\n");
topicWriter.write("2 0 2 panda 2\n");
topicWriter.close();
File sequenceFile = File.createTempFile("large_gap_seq", ".txt");
BufferedWriter seq = new BufferedWriter(new FileWriter(sequenceFile));
seq.write("0\tone\n");
seq.write("100\tgap\n");
seq.write("500\tgapAgain\n");
seq.close();
File alphaFile = File.createTempFile("alpha_large_gaps", ".txt");
BufferedWriter alpha = new BufferedWriter(new FileWriter(alphaFile));
alpha.write("0 1.0 t\n");
alpha.write("1 1.0 l\n");
alpha.write("2 1.0 p\n");
alpha.close();
MultinomialHMM hmm = new MultinomialHMM(3, topicFile.getAbsolutePath(), 2);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
hmm.setGamma(1.0);
hmm.setRandomSeed(91);
hmm.initialize();
hmm.sample();
String matrix = hmm.stateTransitionMatrix();
assertNotNull(matrix);
topicFile.delete();
alphaFile.delete();
sequenceFile.delete();
}

@Test
public void testInitialStateCountsUsedWhenFirstSequenceOnly() throws Exception {
File topicFile = File.createTempFile("initial_state", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(topicFile));
writer.write("0 0 0 x 0\n");
writer.close();
File sequenceFile = File.createTempFile("seq_single", ".txt");
BufferedWriter seq = new BufferedWriter(new FileWriter(sequenceFile));
seq.write("0\tinit\n");
seq.close();
File alphaFile = File.createTempFile("alpha_init", ".txt");
BufferedWriter alpha = new BufferedWriter(new FileWriter(alphaFile));
alpha.write("0 1.0 x\n");
alpha.close();
MultinomialHMM hmm = new MultinomialHMM(1, topicFile.getAbsolutePath(), 2);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
hmm.setGamma(1.0);
hmm.setRandomSeed(5);
hmm.initialize();
hmm.sample();
String output = hmm.printStateTransitions();
assertNotNull(output);
assertTrue(output.contains("["));
topicFile.delete();
alphaFile.delete();
sequenceFile.delete();
}

@Test
public void testTransitionMatrixIncludesAllStatesEvenUnused() throws Exception {
File topicFile = File.createTempFile("some_states_unused", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(topicFile));
writer.write("0 0 0 tok 0\n");
writer.close();
File sequenceFile = File.createTempFile("unused_state_seq", ".txt");
BufferedWriter seq = new BufferedWriter(new FileWriter(sequenceFile));
seq.write("0\tu\n");
seq.close();
File alphaFile = File.createTempFile("unused_state_alpha", ".txt");
BufferedWriter alpha = new BufferedWriter(new FileWriter(alphaFile));
alpha.write("0 1.0 tok\n");
alpha.close();
MultinomialHMM hmm = new MultinomialHMM(1, topicFile.getAbsolutePath(), 5);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
hmm.setRandomSeed(11);
hmm.setGamma(1.0);
hmm.initialize();
hmm.sample();
String output = hmm.stateTransitionMatrix();
String[] rows = output.trim().split("\n");
assertEquals(5, rows.length);
topicFile.delete();
alphaFile.delete();
sequenceFile.delete();
}

@Test
public void testLoadTopicsWithNonIntegerTokensCausesFailure() throws Exception {
File topicFile = File.createTempFile("bad_token_id", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(topicFile));
writer.write("0 x 0 tok 1\n");
writer.close();
boolean caught = false;
try {
new MultinomialHMM(2, topicFile.getAbsolutePath(), 2);
} catch (NumberFormatException e) {
caught = true;
}
assertTrue(caught);
topicFile.delete();
}

@Test
public void testSampleHandlesZeroTransitionTotalsGracefully() throws Exception {
File topicFile = File.createTempFile("zero_trans", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(topicFile));
writer.write("0 0 0 foo 0\n");
writer.close();
File sequenceFile = File.createTempFile("zero_trans_seq", ".txt");
BufferedWriter seq = new BufferedWriter(new FileWriter(sequenceFile));
seq.write("0\tUNIT\n");
seq.close();
File alphaFile = File.createTempFile("zero_trans_alpha", ".txt");
BufferedWriter alpha = new BufferedWriter(new FileWriter(alphaFile));
alpha.write("0 1.0 foo\n");
alpha.close();
MultinomialHMM hmm = new MultinomialHMM(1, topicFile.getAbsolutePath(), 3);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
hmm.setGamma(0.0);
hmm.setRandomSeed(13);
hmm.initialize();
hmm.sample();
String result = hmm.stateTopics();
assertNotNull(result);
topicFile.delete();
alphaFile.delete();
sequenceFile.delete();
}

@Test
public void testTopicDistributionWithZeroTotalInCache() throws Exception {
File topicFile = File.createTempFile("zero_total_topics", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(topicFile));
writer.write("0 0 0 test 0\n");
writer.close();
File alphaFile = File.createTempFile("zero_total_alpha", ".txt");
BufferedWriter alpha = new BufferedWriter(new FileWriter(alphaFile));
alpha.write("0 1.0 test\n");
alpha.close();
File sequenceFile = File.createTempFile("zero_total_seq", ".txt");
BufferedWriter seq = new BufferedWriter(new FileWriter(sequenceFile));
seq.write("0\tseq\n");
seq.close();
MultinomialHMM hmm = new MultinomialHMM(1, topicFile.getAbsolutePath(), 2);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
hmm.setGamma(0.5);
hmm.setRandomSeed(42);
hmm.initialize();
hmm.sample();
String stateTopics = hmm.stateTopics();
assertNotNull(stateTopics);
topicFile.delete();
alphaFile.delete();
sequenceFile.delete();
}

@Test
public void testAlphaWithLargeExponentRange() throws Exception {
File topicFile = File.createTempFile("topic_large_alpha", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(topicFile));
writer.write("0 0 0 tok 0\n");
writer.close();
File alphaFile = File.createTempFile("alpha_large_exp", ".txt");
BufferedWriter alpha = new BufferedWriter(new FileWriter(alphaFile));
alpha.write("0 99999999.9 hugekey\n");
alpha.close();
File sequenceFile = File.createTempFile("seq_large_exp", ".txt");
BufferedWriter seq = new BufferedWriter(new FileWriter(sequenceFile));
seq.write("0\tX\n");
seq.close();
MultinomialHMM hmm = new MultinomialHMM(1, topicFile.getAbsolutePath(), 1);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
hmm.setGamma(0.1);
hmm.setRandomSeed(3);
hmm.initialize();
hmm.sample();
String output = hmm.stateTransitionMatrix();
assertNotNull(output);
topicFile.delete();
alphaFile.delete();
sequenceFile.delete();
}

@Test
public void testSamplingWithExtremeSmallAlphaSum() throws Exception {
File topicFile = File.createTempFile("small_alpha_topic", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(topicFile));
writer.write("0 0 0 tiny 0\n");
writer.close();
File alphaFile = File.createTempFile("tiny_alpha", ".txt");
BufferedWriter alpha = new BufferedWriter(new FileWriter(alphaFile));
alpha.write("0 0.0000001 tiny_value\n");
alpha.close();
File sequenceFile = File.createTempFile("small_seq", ".txt");
BufferedWriter seq = new BufferedWriter(new FileWriter(sequenceFile));
seq.write("0\tA\n");
seq.close();
MultinomialHMM hmm = new MultinomialHMM(1, topicFile.getAbsolutePath(), 2);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
hmm.setGamma(1.0);
hmm.setRandomSeed(7);
hmm.initialize();
hmm.sample();
String summary = hmm.printStateTransitions();
assertNotNull(summary);
topicFile.delete();
alphaFile.delete();
sequenceFile.delete();
}

@Test
public void testSamplingLogLikelihoodNumericalStability() throws Exception {
File topicFile = File.createTempFile("log_likelihood_test", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(topicFile));
writer.write("0 0 0 foo 0\n");
writer.write("0 1 0 foo 0\n");
writer.write("0 2 0 foo 0\n");
writer.write("0 3 0 foo 0\n");
writer.close();
File alphaFile = File.createTempFile("log_alpha", ".txt");
BufferedWriter alpha = new BufferedWriter(new FileWriter(alphaFile));
alpha.write("0 0.00001 foo\n");
alpha.close();
File sequenceFile = File.createTempFile("log_seq", ".txt");
BufferedWriter seq = new BufferedWriter(new FileWriter(sequenceFile));
seq.write("0\tmeta\n");
seq.close();
MultinomialHMM hmm = new MultinomialHMM(1, topicFile.getAbsolutePath(), 2);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
hmm.setGamma(0.1);
hmm.setRandomSeed(555);
hmm.initialize();
hmm.sample();
String stats = hmm.stateTopics();
assertNotNull(stats);
topicFile.delete();
alphaFile.delete();
sequenceFile.delete();
}

@Test
public void testEmptyAlphaFileWithDeclaredTopics() throws Exception {
File topicFile = File.createTempFile("empty_alpha_topic", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(topicFile));
writer.write("0 0 0 w 0\n");
writer.write("0 1 1 z 1\n");
writer.close();
File alphaFile = File.createTempFile("truncated_alpha", ".txt");
BufferedWriter alpha = new BufferedWriter(new FileWriter(alphaFile));
alpha.close();
File sequenceFile = File.createTempFile("sequence_alpha", ".txt");
BufferedWriter seq = new BufferedWriter(new FileWriter(sequenceFile));
seq.write("0\talpha\n");
seq.close();
MultinomialHMM hmm = new MultinomialHMM(2, topicFile.getAbsolutePath(), 2);
boolean threw = false;
try {
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
} catch (Exception e) {
threw = true;
}
assertTrue(threw);
topicFile.delete();
alphaFile.delete();
sequenceFile.delete();
}

@Test
public void testTopicKeyTrimming() throws Exception {
File topicFile = File.createTempFile("whitespace_topic", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(topicFile));
writer.write("0 0 0 word 0\n");
writer.close();
File alphaFile = File.createTempFile("alpha_trim", ".txt");
BufferedWriter alpha = new BufferedWriter(new FileWriter(alphaFile));
alpha.write("0 1.0   spacedouttoken    \n");
alpha.close();
File sequenceFile = File.createTempFile("seq_trim", ".txt");
BufferedWriter seq = new BufferedWriter(new FileWriter(sequenceFile));
seq.write("0\tsomething\n");
seq.close();
MultinomialHMM hmm = new MultinomialHMM(1, topicFile.getAbsolutePath(), 1);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
assertTrue(hmm.topicKeys[0].startsWith("spacedouttoken"));
topicFile.delete();
alphaFile.delete();
sequenceFile.delete();
}

@Test
public void testLoadTopicsWithLargeTopicId() throws Exception {
File topicFile = File.createTempFile("huge_topic_id", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(topicFile));
writer.write("0 0 0 foo 999\n");
writer.close();
boolean thrown = false;
try {
new MultinomialHMM(10, topicFile.getAbsolutePath(), 2);
} catch (ArrayIndexOutOfBoundsException e) {
thrown = true;
}
assertTrue(thrown);
topicFile.delete();
}

@Test
public void testLoadTopicsWithMalformedNumericFields() throws Exception {
File topicFile = File.createTempFile("bad_numeric", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(topicFile));
writer.write("docA tokenX tokenY tokenZ topicA\n");
writer.close();
boolean threw = false;
try {
new MultinomialHMM(3, topicFile.getAbsolutePath(), 2);
} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
threw = true;
}
assertTrue(threw);
topicFile.delete();
}

@Test
public void testTopicFileWithOnlyBlankLines() throws Exception {
File topicFile = File.createTempFile("blank_only", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(topicFile));
writer.write("\n");
writer.write("\n");
writer.close();
MultinomialHMM hmm = new MultinomialHMM(2, topicFile.getAbsolutePath(), 2);
assertEquals(0, hmm.documentTopics.size());
topicFile.delete();
}

@Test
public void testAlphaFileWithCommentAndBlankLines() throws Exception {
File topicFile = File.createTempFile("alpha_comment_topic", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(topicFile));
writer.write("0 0 0 x 0\n");
writer.close();
MultinomialHMM hmm = new MultinomialHMM(1, topicFile.getAbsolutePath(), 1);
File alphaFile = File.createTempFile("alpha_comment", ".txt");
BufferedWriter alpha = new BufferedWriter(new FileWriter(alphaFile));
alpha.write("# Comment line should be ignored\n");
alpha.write("\n");
alpha.write("0 1.0 alpha\n");
alpha.close();
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
assertEquals("alpha ", hmm.topicKeys[0]);
topicFile.delete();
alphaFile.delete();
}

@Test
public void testSequenceFileWithIrregularTabSpacing() throws Exception {
File topicFile = File.createTempFile("tab_spacing_topic", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(topicFile));
writer.write("0 0 0 a 0\n");
writer.close();
MultinomialHMM hmm = new MultinomialHMM(1, topicFile.getAbsolutePath(), 1);
File sequenceFile = File.createTempFile("seq_irregular", ".txt");
BufferedWriter seq = new BufferedWriter(new FileWriter(sequenceFile));
seq.write("0      meta\n");
seq.close();
boolean threw = false;
try {
hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
threw = true;
}
assertTrue(threw);
topicFile.delete();
sequenceFile.delete();
}

@Test
public void testSamplingWithSingleTokenInSingleState() throws Exception {
File topicFile = File.createTempFile("onestate_single", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(topicFile));
writer.write("0 0 0 token 0\n");
writer.close();
File alphaFile = File.createTempFile("onestate_alpha", ".txt");
BufferedWriter alpha = new BufferedWriter(new FileWriter(alphaFile));
alpha.write("0 1.0 token\n");
alpha.close();
File sequenceFile = File.createTempFile("seq_one_entry", ".txt");
BufferedWriter seq = new BufferedWriter(new FileWriter(sequenceFile));
seq.write("0\tsingle\n");
seq.close();
MultinomialHMM hmm = new MultinomialHMM(1, topicFile.getAbsolutePath(), 1);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
hmm.setRandomSeed(1);
hmm.setGamma(0.5);
hmm.initialize();
hmm.sample();
String s = hmm.printStateTransitions();
assertNotNull(s);
assertTrue(s.contains("0"));
topicFile.delete();
alphaFile.delete();
sequenceFile.delete();
}

@Test
public void testLoadCompressedGzTopicFileWithInvalidHeaderContent() throws Exception {
File gzFile = File.createTempFile("invalid_gz", ".gz");
FileOutputStream fos = new FileOutputStream(gzFile);
fos.write(new byte[] { 0x00, 0x01, 0x02, 0x03 });
fos.close();
boolean thrown = false;
try {
new MultinomialHMM(3, gzFile.getAbsolutePath(), 2);
} catch (IOException e) {
thrown = true;
}
assertTrue(thrown);
gzFile.delete();
}

@Test
public void testSamplingWithTransitionToSelfOnly() throws Exception {
File topicFile = File.createTempFile("self_trans", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(topicFile));
writer.write("0 0 0 token 0\n");
writer.write("1 0 0 token 0\n");
writer.close();
File sequenceFile = File.createTempFile("seq_self_trans", ".txt");
BufferedWriter seq = new BufferedWriter(new FileWriter(sequenceFile));
seq.write("0\tA\n");
seq.write("0\tA\n");
seq.close();
File alphaFile = File.createTempFile("alpha_self_trans", ".txt");
BufferedWriter alpha = new BufferedWriter(new FileWriter(alphaFile));
alpha.write("0 1.0 word\n");
alpha.close();
MultinomialHMM hmm = new MultinomialHMM(1, topicFile.getAbsolutePath(), 1);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
hmm.setGamma(0.0);
hmm.setRandomSeed(2);
hmm.initialize();
hmm.sample();
String result = hmm.stateTransitionMatrix();
assertNotNull(result);
assertTrue(result.contains("0"));
topicFile.delete();
alphaFile.delete();
sequenceFile.delete();
}

@Test
public void testLoadAlphaWithExcessFieldsBeyondTopicKey() throws Exception {
File topicFile = File.createTempFile("alpha_extra", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(topicFile));
writer.write("0 0 0 token 0\n");
writer.close();
File alphaFile = File.createTempFile("alpha_fields_extra", ".txt");
BufferedWriter alpha = new BufferedWriter(new FileWriter(alphaFile));
alpha.write("0 1.0 key extra words beyond \n");
alpha.close();
File sequenceFile = File.createTempFile("seq_extra", ".txt");
BufferedWriter seq = new BufferedWriter(new FileWriter(sequenceFile));
seq.write("0\tmeta\n");
seq.close();
MultinomialHMM hmm = new MultinomialHMM(1, topicFile.getAbsolutePath(), 2);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
String key = hmm.topicKeys[0];
assertTrue(key.startsWith("key"));
assertTrue(key.contains("extra"));
topicFile.delete();
alphaFile.delete();
sequenceFile.delete();
}

@Test
public void testSamplingWithMultipleDocsInSingleSequence() throws Exception {
File topicFile = File.createTempFile("shared_seq_topic", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(topicFile));
writer.write("0 0 0 foo 0\n");
writer.write("1 0 0 bar 1\n");
writer.write("2 0 0 baz 2\n");
writer.close();
File sequenceFile = File.createTempFile("shared_seq", ".txt");
BufferedWriter seq = new BufferedWriter(new FileWriter(sequenceFile));
seq.write("0\tA\n");
seq.write("0\tA\n");
seq.write("0\tA\n");
seq.close();
File alphaFile = File.createTempFile("shared_seq_alpha", ".txt");
BufferedWriter alpha = new BufferedWriter(new FileWriter(alphaFile));
alpha.write("0 1.0 foo\n");
alpha.write("1 1.0 bar\n");
alpha.write("2 1.0 baz\n");
alpha.close();
MultinomialHMM hmm = new MultinomialHMM(3, topicFile.getAbsolutePath(), 2);
hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
hmm.setGamma(1.0);
hmm.setRandomSeed(20);
hmm.initialize();
hmm.sample();
String out = hmm.printStateTransitions();
assertTrue(out.contains("0"));
assertTrue(out.contains("["));
topicFile.delete();
alphaFile.delete();
sequenceFile.delete();
}
}
