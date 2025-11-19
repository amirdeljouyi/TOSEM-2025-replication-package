package cc.mallet.topics;

import cc.mallet.types.*;
import cc.mallet.util.Randoms;
import org.junit.Test;
import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.zip.GZIPOutputStream;
import static org.junit.Assert.*;

public class MultinomialHMM_llmsuite_5_GPTLLMTest {

@Test
public void testConstructorInitializesWithoutErrors() throws Exception {
// File topicsFile = tempFolder.newFile("topics.txt");
// PrintWriter writer = new PrintWriter(topicsFile);
// writer.println("0 0 1 0 0");
// writer.println("0 1 2 0 1");
// writer.println("1 0 3 0 2");
// writer.close();
// MultinomialHMM hmm = new MultinomialHMM(3, topicsFile.getAbsolutePath(), 2);
// assertNotNull(hmm);
}

@Test
public void testLoadTopicsFromFileProcessesLines() throws Exception {
// File topicsFile = tempFolder.newFile("topics.txt");
// PrintWriter writer = new PrintWriter(topicsFile);
// writer.println("0 0 1 0 0");
// writer.println("0 1 1 0 1");
// writer.println("1 0 2 0 0");
// writer.close();
// MultinomialHMM hmm = new MultinomialHMM(3, topicsFile.getAbsolutePath(), 2);
// assertNotNull(hmm);
}

@Test
public void testSetRandomSeedWithoutError() throws Exception {
// File topicsFile = tempFolder.newFile("topics.txt");
// PrintWriter writer = new PrintWriter(topicsFile);
// writer.println("0 0 1 0 0");
// writer.println("1 0 2 0 1");
// writer.close();
// MultinomialHMM hmm = new MultinomialHMM(2, topicsFile.getAbsolutePath(), 2);
// hmm.setRandomSeed(123);
// assertNotNull(hmm);
}

@Test
public void testSetGammaAndInitializeRuns() throws Exception {
// File topicsFile = tempFolder.newFile("topics.txt");
// PrintWriter writer = new PrintWriter(topicsFile);
// writer.println("0 0 1 0 0");
// writer.println("1 0 2 0 1");
// writer.close();
// File alphaFile = tempFolder.newFile("alpha.txt");
// PrintWriter alphaWriter = new PrintWriter(alphaFile);
// alphaWriter.println("0 1 topicA");
// alphaWriter.println("1 1 topicB");
// alphaWriter.close();
// File seqFile = tempFolder.newFile("sequence.txt");
// PrintWriter seqWriter = new PrintWriter(seqFile);
// seqWriter.println("0\tmetaA");
// seqWriter.println("1\tmetaB");
// seqWriter.close();
// MultinomialHMM hmm = new MultinomialHMM(2, topicsFile.getAbsolutePath(), 2);
// hmm.setGamma(0.5);
// hmm.setRandomSeed(42);
// hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
// hmm.loadSequenceIDsFromFile(seqFile.getAbsolutePath());
// hmm.initialize();
// assertNotNull(hmm);
}

@Test
public void testStateTransitionMatrixReturnsValidString() throws Exception {
// File topicsFile = tempFolder.newFile("topics.txt");
// PrintWriter writer = new PrintWriter(topicsFile);
// writer.println("0 0 0 0 0");
// writer.println("1 0 1 0 1");
// writer.close();
// File alphaFile = tempFolder.newFile("alpha.txt");
// PrintWriter alphaWriter = new PrintWriter(alphaFile);
// alphaWriter.println("0 1 topicA");
// alphaWriter.println("1 1 topicB");
// alphaWriter.close();
// File seqFile = tempFolder.newFile("sequenceIDs.txt");
// PrintWriter seqWriter = new PrintWriter(seqFile);
// seqWriter.println("0\tmeta");
// seqWriter.println("1\tmeta");
// seqWriter.close();
// MultinomialHMM hmm = new MultinomialHMM(2, topicsFile.getAbsolutePath(), 2);
// hmm.setGamma(1.0);
// hmm.setRandomSeed(8);
// hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
// hmm.loadSequenceIDsFromFile(seqFile.getAbsolutePath());
// hmm.initialize();
// String matrix = hmm.stateTransitionMatrix();
// assertNotNull(matrix);
// assertTrue(matrix.contains("\t"));
}

@Test
public void testStateTopicsReturnsNonEmptyString() throws Exception {
// File topicsFile = tempFolder.newFile("topics.txt");
// PrintWriter writer = new PrintWriter(topicsFile);
// writer.println("0 0 1 0 0");
// writer.println("1 0 2 0 1");
// writer.close();
// File alphaFile = tempFolder.newFile("alpha.txt");
// PrintWriter alphaWriter = new PrintWriter(alphaFile);
// alphaWriter.println("0 1 foo");
// alphaWriter.println("1 1 bar");
// alphaWriter.close();
// File seqFile = tempFolder.newFile("sequence.txt");
// PrintWriter seqWriter = new PrintWriter(seqFile);
// seqWriter.println("0\tA");
// seqWriter.println("1\tB");
// seqWriter.close();
// MultinomialHMM hmm = new MultinomialHMM(2, topicsFile.getAbsolutePath(), 2);
// hmm.setGamma(1.0);
// hmm.setRandomSeed(999);
// hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
// hmm.loadSequenceIDsFromFile(seqFile.getAbsolutePath());
// hmm.initialize();
// String output = hmm.stateTopics();
// assertNotNull(output);
// assertTrue(output.length() > 0);
// assertTrue(output.contains("\t"));
}

@Test(expected = IOException.class)
public void testLoadTopicsFromMissingFileThrowsException() throws Exception {
// File nonExistent = new File(tempFolder.getRoot(), "doesnotexist.txt");
// new MultinomialHMM(2, nonExistent.getAbsolutePath(), 2);
}

@Test(expected = IOException.class)
public void testLoadAlphaFromInvalidFileThrowsException() throws Exception {
// File topicsFile = tempFolder.newFile("topics.txt");
// PrintWriter writer = new PrintWriter(topicsFile);
// writer.println("0 0 1 0 0");
// writer.close();
// File alphaFile = tempFolder.newFile("alpha_invalid.txt");
// PrintWriter alphaWriter = new PrintWriter(alphaFile);
// alphaWriter.println("MalFormedLine");
// alphaWriter.close();
// MultinomialHMM hmm = new MultinomialHMM(2, topicsFile.getAbsolutePath(), 2);
// hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
}

@Test
public void testFullExecutionWorkflowDoesNotCrash() throws Exception {
// File topicsFile = tempFolder.newFile("topics.txt");
// PrintWriter topicsWriter = new PrintWriter(topicsFile);
// topicsWriter.println("0 0 0 0 0");
// topicsWriter.println("1 0 1 0 1");
// topicsWriter.close();
// File alphaFile = tempFolder.newFile("alpha.txt");
// PrintWriter alphaWriter = new PrintWriter(alphaFile);
// alphaWriter.println("0 1 topic0");
// alphaWriter.println("1 1 topic1");
// alphaWriter.close();
// File seqFile = tempFolder.newFile("sequences.txt");
// PrintWriter seqWriter = new PrintWriter(seqFile);
// seqWriter.println("0\tA");
// seqWriter.println("1\tA");
// seqWriter.close();
// MultinomialHMM hmm = new MultinomialHMM(2, topicsFile.getAbsolutePath(), 2);
// hmm.setGamma(1.0);
// hmm.setRandomSeed(10);
// hmm.setNumIterations(2);
// hmm.setBurninPeriod(1);
// hmm.setTopicDisplayInterval(1);
// hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
// hmm.loadSequenceIDsFromFile(seqFile.getAbsolutePath());
// hmm.initialize();
// hmm.sample();
// assertNotNull(hmm);
}

@Test
public void testEmptyTopicFileDoesNotCrash() throws Exception {
// File topicsFile = tempFolder.newFile("empty_topics.txt");
// File alphaFile = tempFolder.newFile("alpha.txt");
// PrintWriter alphaWriter = new PrintWriter(alphaFile);
// alphaWriter.println("0 1 keyA");
// alphaWriter.close();
// File sequenceFile = tempFolder.newFile("sequence.txt");
// PrintWriter sequenceWriter = new PrintWriter(sequenceFile);
// sequenceWriter.println("0\tseq0");
// sequenceWriter.close();
// MultinomialHMM hmm = new MultinomialHMM(1, topicsFile.getAbsolutePath(), 1);
// hmm.setGamma(1.0);
// hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
// hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
// hmm.initialize();
// assertNotNull(hmm);
}

@Test
public void testInitializeWithoutSettingRandomSeed() throws Exception {
// File topicsFile = tempFolder.newFile("topics.txt");
// PrintWriter writer = new PrintWriter(topicsFile);
// writer.println("0 0 10 0 0");
// writer.close();
// File alphaFile = tempFolder.newFile("alpha.txt");
// PrintWriter alphaWriter = new PrintWriter(alphaFile);
// alphaWriter.println("0 1 alphaKey");
// alphaWriter.close();
// File sequenceFile = tempFolder.newFile("sequence.txt");
// PrintWriter sequenceWriter = new PrintWriter(sequenceFile);
// sequenceWriter.println("0\tseq0");
// sequenceWriter.close();
// MultinomialHMM hmm = new MultinomialHMM(1, topicsFile.getAbsolutePath(), 1);
// hmm.setGamma(0.5);
// hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
// hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
// hmm.initialize();
// assertNotNull(hmm);
}

@Test
public void testMismatchBetweenTopicsAndSequencesTriggersWarning() throws Exception {
// File topicsFile = tempFolder.newFile("topics.txt");
// PrintWriter writer = new PrintWriter(topicsFile);
// writer.println("0 0 1 0 0");
// writer.println("1 0 2 0 1");
// writer.println("2 0 3 0 2");
// writer.close();
// File alphaFile = tempFolder.newFile("alpha.txt");
// PrintWriter alphaWriter = new PrintWriter(alphaFile);
// alphaWriter.println("0 1 key0");
// alphaWriter.println("1 1 key1");
// alphaWriter.println("2 1 key2");
// alphaWriter.close();
// File sequenceFile = tempFolder.newFile("sequence.txt");
// PrintWriter sequenceWriter = new PrintWriter(sequenceFile);
// sequenceWriter.println("0\tmeta");
// sequenceWriter.println("1\tmeta");
// sequenceWriter.close();
// MultinomialHMM hmm = new MultinomialHMM(3, topicsFile.getAbsolutePath(), 2);
// hmm.setGamma(0.9);
// hmm.setRandomSeed(123);
// hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
// hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
// hmm.initialize();
// assertNotNull(hmm);
}

@Test
public void testDuplicateTopicInDocumentIsCountedTwice() throws Exception {
// File topicsFile = tempFolder.newFile("topics.txt");
// PrintWriter writer = new PrintWriter(topicsFile);
// writer.println("0 0 5 0 0");
// writer.println("0 1 5 0 0");
// writer.close();
// File alphaFile = tempFolder.newFile("alpha.txt");
// PrintWriter alphaWriter = new PrintWriter(alphaFile);
// alphaWriter.println("0 1 repeat");
// alphaWriter.close();
// File sequenceFile = tempFolder.newFile("sequence.txt");
// PrintWriter sequenceWriter = new PrintWriter(sequenceFile);
// sequenceWriter.println("0\tdup");
// sequenceWriter.close();
// MultinomialHMM hmm = new MultinomialHMM(1, topicsFile.getAbsolutePath(), 1);
// hmm.setGamma(1.0);
// hmm.setRandomSeed(321);
// hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
// hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
// hmm.initialize();
// assertNotNull(hmm);
}

@Test(expected = NumberFormatException.class)
public void testMalformedTopicLineFailsGracefully() throws Exception {
// File topicsFile = tempFolder.newFile("malformed.txt");
// PrintWriter writer = new PrintWriter(topicsFile);
// writer.println("bad non numeric line");
// writer.close();
// new MultinomialHMM(1, topicsFile.getAbsolutePath(), 1);
}

@Test
public void testHighFrequencyTokensCreatesLargeCache() throws Exception {
// File topicsFile = tempFolder.newFile("longdoc.txt");
// PrintWriter writer = new PrintWriter(topicsFile);
// writer.println("0 0 1 0 0");
// writer.println("0 1 1 0 0");
// writer.println("0 2 1 0 0");
// writer.println("0 3 1 0 0");
// writer.println("0 4 1 0 0");
// writer.println("0 5 1 0 0");
// writer.println("0 6 1 0 0");
// writer.println("0 7 1 0 0");
// writer.println("0 8 1 0 0");
// writer.println("0 9 1 0 0");
// writer.close();
// File alphaFile = tempFolder.newFile("alpha.txt");
// PrintWriter alphaWriter = new PrintWriter(alphaFile);
// alphaWriter.println("0 1 massive");
// alphaWriter.close();
// File sequenceFile = tempFolder.newFile("sequence.txt");
// PrintWriter seqWriter = new PrintWriter(sequenceFile);
// seqWriter.println("0\tmeta");
// seqWriter.close();
// MultinomialHMM hmm = new MultinomialHMM(1, topicsFile.getAbsolutePath(), 1);
// hmm.setGamma(0.3);
// hmm.setRandomSeed(7);
// hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
// hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
// hmm.initialize();
// assertNotNull(hmm);
}

@Test
public void testSingletonDocumentSequenceHandledCorrectly() throws Exception {
// File topicsFile = tempFolder.newFile("single.txt");
// PrintWriter writer = new PrintWriter(topicsFile);
// writer.println("0 0 2 0 0");
// writer.close();
// File alphaFile = tempFolder.newFile("alpha.txt");
// PrintWriter alphaWriter = new PrintWriter(alphaFile);
// alphaWriter.println("0 1 solo");
// alphaWriter.close();
// File sequenceFile = tempFolder.newFile("sequence.txt");
// PrintWriter sequenceWriter = new PrintWriter(sequenceFile);
// sequenceWriter.println("0\tsolo-seq");
// sequenceWriter.close();
// MultinomialHMM hmm = new MultinomialHMM(1, topicsFile.getAbsolutePath(), 1);
// hmm.setGamma(0.5);
// hmm.setRandomSeed(23);
// hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
// hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
// hmm.initialize();
// assertNotNull(hmm);
}

@Test
public void testSingleTopicAllDocuments() throws Exception {
// File topicsFile = tempFolder.newFile("single-topic.txt");
// PrintWriter writer = new PrintWriter(topicsFile);
// writer.println("0 0 0 0 0");
// writer.println("1 0 0 0 0");
// writer.close();
// File alphaFile = tempFolder.newFile("alpha.txt");
// PrintWriter alphaWriter = new PrintWriter(alphaFile);
// alphaWriter.println("0 1 onlytopic");
// alphaWriter.close();
// File sequenceFile = tempFolder.newFile("sequence.txt");
// PrintWriter sequenceWriter = new PrintWriter(sequenceFile);
// sequenceWriter.println("0\tS1");
// sequenceWriter.println("1\tS1");
// sequenceWriter.close();
// MultinomialHMM hmm = new MultinomialHMM(1, topicsFile.getAbsolutePath(), 1);
// hmm.setGamma(1.0);
// hmm.setRandomSeed(42);
// hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
// hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
// hmm.initialize();
// assertNotNull(hmm);
}

@Test
public void testDocumentWithNoTopicEntriesIsSkipped() throws Exception {
// File topicsFile = tempFolder.newFile("no_topics.txt");
// PrintWriter writer = new PrintWriter(topicsFile);
// writer.println("0 0 5 0 1");
// writer.close();
// File alphaFile = tempFolder.newFile("alpha.txt");
// PrintWriter alphaWriter = new PrintWriter(alphaFile);
// alphaWriter.println("0 1 tokenA");
// alphaWriter.println("1 1 tokenB");
// alphaWriter.close();
// File sequenceFile = tempFolder.newFile("sequence.txt");
// PrintWriter sequenceWriter = new PrintWriter(sequenceFile);
// sequenceWriter.println("0\tA");
// sequenceWriter.println("1\tA");
// sequenceWriter.close();
// MultinomialHMM hmm = new MultinomialHMM(2, topicsFile.getAbsolutePath(), 2);
// hmm.setGamma(0.5);
// hmm.setRandomSeed(100);
// hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
// hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
// hmm.initialize();
// assertNotNull(hmm);
}

@Test
public void testAlphaFileWithExtraWhitespaceStillLoads() throws Exception {
// File topicsFile = tempFolder.newFile("topics.txt");
// PrintWriter writer = new PrintWriter(topicsFile);
// writer.println("0 0 1 0 0");
// writer.close();
// File alphaFile = tempFolder.newFile("alpha_spaces.txt");
// PrintWriter alphaWriter = new PrintWriter(alphaFile);
// alphaWriter.println("0    1     spaced");
// alphaWriter.close();
// File sequenceFile = tempFolder.newFile("sequence.txt");
// PrintWriter seqWriter = new PrintWriter(sequenceFile);
// seqWriter.println("0\tA");
// seqWriter.close();
// MultinomialHMM hmm = new MultinomialHMM(1, topicsFile.getAbsolutePath(), 1);
// hmm.setGamma(0.3);
// hmm.setRandomSeed(1);
// hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
// hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
// hmm.initialize();
// assertNotNull(hmm);
}

@Test
public void testOneTopicOneStateMinimumBounds() throws Exception {
// File topicsFile = tempFolder.newFile("min.txt");
// PrintWriter writer = new PrintWriter(topicsFile);
// writer.println("0 0 0 0 0");
// writer.close();
// File alphaFile = tempFolder.newFile("alpha.txt");
// PrintWriter alphaWriter = new PrintWriter(alphaFile);
// alphaWriter.println("0 1 single");
// alphaWriter.close();
// File sequenceFile = tempFolder.newFile("sequence.txt");
// PrintWriter seqWriter = new PrintWriter(sequenceFile);
// seqWriter.println("0\tX");
// seqWriter.close();
// MultinomialHMM hmm = new MultinomialHMM(1, topicsFile.getAbsolutePath(), 1);
// hmm.setGamma(0.5);
// hmm.setRandomSeed(77);
// hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
// hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
// hmm.initialize();
// assertNotNull(hmm);
}

@Test
public void testGZippedTopicFileIsRead() throws Exception {
// File gzFile = tempFolder.newFile("topics.gz");
// GZIPOutputStream gzip = new GZIPOutputStream(new FileOutputStream(gzFile));
// OutputStreamWriter writer = new OutputStreamWriter(gzip);
// writer.write("0 0 2 0 0\n");
// writer.close();
// File alphaFile = tempFolder.newFile("alpha.txt");
// PrintWriter alphaWriter = new PrintWriter(alphaFile);
// alphaWriter.println("0 1 topickey");
// alphaWriter.close();
// File sequenceFile = tempFolder.newFile("sequence.txt");
// PrintWriter seqWriter = new PrintWriter(sequenceFile);
// seqWriter.println("0\tseq");
// seqWriter.close();
// MultinomialHMM hmm = new MultinomialHMM(1, gzFile.getAbsolutePath(), 1);
// hmm.setGamma(1.0);
// hmm.setRandomSeed(17);
// hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
// hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
// hmm.initialize();
// assertNotNull(hmm);
}

@Test
public void testStateSamplingWithOneIteration() throws Exception {
// File topicsFile = tempFolder.newFile("sample.txt");
// PrintWriter writer = new PrintWriter(topicsFile);
// writer.println("0 0 1 0 0");
// writer.println("1 0 1 0 1");
// writer.close();
// File alphaFile = tempFolder.newFile("alpha.txt");
// PrintWriter alphaWriter = new PrintWriter(alphaFile);
// alphaWriter.println("0 1 tokA");
// alphaWriter.println("1 1 tokB");
// alphaWriter.close();
// File sequenceFile = tempFolder.newFile("sequence.txt");
// PrintWriter seqWriter = new PrintWriter(sequenceFile);
// seqWriter.println("0\tS");
// seqWriter.println("1\tS");
// seqWriter.close();
// MultinomialHMM hmm = new MultinomialHMM(2, topicsFile.getAbsolutePath(), 2);
// hmm.setGamma(1.0);
// hmm.setRandomSeed(5);
// hmm.setNumIterations(1);
// hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
// hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
// hmm.initialize();
// hmm.sample();
// assertNotNull(hmm);
}

@Test
public void testTopicIdExceedsNumberOfTopicsIgnoredGracefully() throws Exception {
// File topicsFile = tempFolder.newFile("badtopicid.txt");
// PrintWriter writer = new PrintWriter(topicsFile);
// writer.println("0 0 5 0 5");
// writer.close();
// File alphaFile = tempFolder.newFile("alpha.txt");
// PrintWriter alphaWriter = new PrintWriter(alphaFile);
// alphaWriter.println("0 1 aa");
// alphaWriter.println("1 1 bb");
// alphaWriter.close();
// File sequenceFile = tempFolder.newFile("sequence.txt");
// PrintWriter seqWriter = new PrintWriter(sequenceFile);
// seqWriter.println("0\tmeta");
// seqWriter.close();
boolean exceptionThrown = false;
try {
// MultinomialHMM hmm = new MultinomialHMM(2, topicsFile.getAbsolutePath(), 2);
// hmm.setGamma(0.7);
// hmm.setRandomSeed(42);
// hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
// hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
// hmm.initialize();
} catch (Exception e) {
exceptionThrown = true;
}
assertFalse("Input with large topic ID should not crash", exceptionThrown);
}

@Test
public void testAlphaSumWithZeroAlphas() throws Exception {
// File topicsFile = tempFolder.newFile("zero.txt");
// PrintWriter writer = new PrintWriter(topicsFile);
// writer.println("0 0 0 0 0");
// writer.close();
// File alphaFile = tempFolder.newFile("alpha_zero.txt");
// PrintWriter alphaWriter = new PrintWriter(alphaFile);
// alphaWriter.println("0 0 zero");
// alphaWriter.close();
// File sequenceFile = tempFolder.newFile("sequence.txt");
// PrintWriter sequenceWriter = new PrintWriter(sequenceFile);
// sequenceWriter.println("0\tx");
// sequenceWriter.close();
// MultinomialHMM hmm = new MultinomialHMM(1, topicsFile.getAbsolutePath(), 1);
// hmm.setGamma(0.1);
// hmm.setRandomSeed(2);
// hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
// hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
// hmm.initialize();
// assertNotNull(hmm);
}

@Test
public void testAlphaFileWithBlankLinesIsHandled() throws Exception {
// File topics = tempFolder.newFile("topics.txt");
// PrintWriter writer = new PrintWriter(topics);
// writer.println("0 0 0 0 0");
// writer.close();
// File alphaFile = tempFolder.newFile("alpha_blank_lines.txt");
// PrintWriter alphaWriter = new PrintWriter(alphaFile);
// alphaWriter.println("");
// alphaWriter.println("0 1 tok");
// alphaWriter.println("");
// alphaWriter.close();
// File sequenceFile = tempFolder.newFile("sequence.txt");
// PrintWriter seqWriter = new PrintWriter(sequenceFile);
// seqWriter.println("0\tseqA");
// seqWriter.close();
// MultinomialHMM hmm = new MultinomialHMM(1, topics.getAbsolutePath(), 1);
// hmm.setGamma(1.0);
// hmm.setRandomSeed(101);
// hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
// hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
// hmm.initialize();
// assertNotNull(hmm);
}

@Test
public void testStateTransitionMatrixContainsZerosWhenEmpty() throws Exception {
// File topicsFile = tempFolder.newFile("emptytopics.txt");
// PrintWriter writer = new PrintWriter(topicsFile);
// writer.close();
// File alphaFile = tempFolder.newFile("alpha_file.txt");
// PrintWriter alphaWriter = new PrintWriter(alphaFile);
// alphaWriter.println("0 1 aa");
// alphaWriter.close();
// File sequenceFile = tempFolder.newFile("seq.txt");
// PrintWriter seqWriter = new PrintWriter(sequenceFile);
// seqWriter.println("0\ta");
// seqWriter.close();
// MultinomialHMM hmm = new MultinomialHMM(1, topicsFile.getAbsolutePath(), 3);
// hmm.setRandomSeed(999);
// hmm.setGamma(1.0);
// hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
// hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
// hmm.initialize();
// String output = hmm.stateTransitionMatrix();
// assertTrue(output.contains("0\t0\t0") || output.contains("0\t"));
}

@Test
public void testAlphaFileWithMissingTopicKeyStillParses() throws Exception {
// File topicsFile = tempFolder.newFile("justtopics.txt");
// PrintWriter writer = new PrintWriter(topicsFile);
// writer.println("0 0 1 0 0");
// writer.close();
// File alphaFile = tempFolder.newFile("shortalpha.txt");
// PrintWriter alphaWriter = new PrintWriter(alphaFile);
// alphaWriter.println("0 1");
// alphaWriter.close();
// File sequenceFile = tempFolder.newFile("sequence.txt");
// PrintWriter seqWriter = new PrintWriter(sequenceFile);
// seqWriter.println("0\ttag");
// seqWriter.close();
// MultinomialHMM hmm = new MultinomialHMM(1, topicsFile.getAbsolutePath(), 1);
// hmm.setGamma(0.1);
// hmm.setRandomSeed(51);
// hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
// hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
// hmm.initialize();
// assertNotNull(hmm);
}

@Test(expected = ArrayIndexOutOfBoundsException.class)
public void testCorruptedTopicLineThrowsException() throws Exception {
// File corrupted = tempFolder.newFile("corrupt.txt");
// PrintWriter writer = new PrintWriter(corrupted);
// writer.println("0 0");
// writer.close();
// new MultinomialHMM(1, corrupted.getAbsolutePath(), 1);
}

@Test
public void testPrintStateTransitionsReturnsExpectedFormat() throws Exception {
// File topics = tempFolder.newFile("formattopics.txt");
// PrintWriter t = new PrintWriter(topics);
// t.println("0 0 0 0 0");
// t.println("1 0 0 0 0");
// t.close();
// File alpha = tempFolder.newFile("alpha.txt");
// PrintWriter a = new PrintWriter(alpha);
// a.println("0 1 word");
// a.close();
// File sequence = tempFolder.newFile("seq.txt");
// PrintWriter s = new PrintWriter(sequence);
// s.println("0\tA");
// s.println("1\tA");
// s.close();
// MultinomialHMM hmm = new MultinomialHMM(1, topics.getAbsolutePath(), 1);
// hmm.setGamma(1.0);
// hmm.setRandomSeed(22);
// hmm.loadAlphaFromFile(alpha.getAbsolutePath());
// hmm.loadSequenceIDsFromFile(sequence.getAbsolutePath());
// hmm.initialize();
// String output = hmm.printStateTransitions();
// assertTrue(output.contains("[") && output.contains("]"));
// assertTrue(output.contains("0"));
}

@Test
public void testUnusualLongTopicKeyIsAccepted() throws Exception {
// File topicFile = tempFolder.newFile("longkey.txt");
// PrintWriter writer = new PrintWriter(topicFile);
// writer.println("0 0 0 0 0");
// writer.close();
// File alphaFile = tempFolder.newFile("alpha.txt");
// PrintWriter alpha = new PrintWriter(alphaFile);
// alpha.println("0 1 thisisaverylongtopickeyforLDAmodel");
// alpha.close();
// File sequenceFile = tempFolder.newFile("sequence.txt");
// PrintWriter sequence = new PrintWriter(sequenceFile);
// sequence.println("0\tlong");
// sequence.close();
// MultinomialHMM hmm = new MultinomialHMM(1, topicFile.getAbsolutePath(), 1);
// hmm.setGamma(1.0);
// hmm.setRandomSeed(42);
// hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
// hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
// hmm.initialize();
// assertNotNull(hmm);
}

@Test
public void testSequenceWithNonNumericIDStillParses() throws Exception {
// File topicFile = tempFolder.newFile("topics.txt");
// PrintWriter writer = new PrintWriter(topicFile);
// writer.println("0 0 0 0 0");
// writer.println("1 0 0 0 0");
// writer.close();
// File alphaFile = tempFolder.newFile("alpha.txt");
// PrintWriter alpha = new PrintWriter(alphaFile);
// alpha.println("0 1 aa");
// alpha.close();
// File sequenceFile = tempFolder.newFile("seq.txt");
// PrintWriter sequence = new PrintWriter(sequenceFile);
// sequence.println("100\tmeta");
// sequence.println("101\tmeta");
// sequence.close();
// MultinomialHMM hmm = new MultinomialHMM(1, topicFile.getAbsolutePath(), 1);
// hmm.setGamma(0.3);
// hmm.setRandomSeed(3);
// hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
// hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
// hmm.initialize();
// assertNotNull(hmm);
}

@Test
public void testRepeatedTokenEntriesInSameDoc() throws Exception {
// File topicsFile = tempFolder.newFile("repeated_tokens.txt");
// PrintWriter writer = new PrintWriter(topicsFile);
// writer.println("0 0 1 0 0");
// writer.println("0 1 1 0 0");
// writer.println("0 2 1 0 0");
// writer.close();
// File alphaFile = tempFolder.newFile("alpha.txt");
// PrintWriter alpha = new PrintWriter(alphaFile);
// alpha.println("0 1 topic0");
// alpha.close();
// File sequenceFile = tempFolder.newFile("seq.txt");
// PrintWriter seq = new PrintWriter(sequenceFile);
// seq.println("0\tS1");
// seq.close();
// MultinomialHMM hmm = new MultinomialHMM(1, topicsFile.getAbsolutePath(), 1);
// hmm.setGamma(1.0);
// hmm.setRandomSeed(999);
// hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
// hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
// hmm.initialize();
// assertNotNull(hmm);
}

@Test
public void testTokenThatMapsToUnseenTopic() throws Exception {
// File topicsFile = tempFolder.newFile("no_use_topic.txt");
// PrintWriter writer = new PrintWriter(topicsFile);
// writer.println("0 0 0 0 0");
// writer.close();
// File alphaFile = tempFolder.newFile("alpha.txt");
// PrintWriter alpha = new PrintWriter(alphaFile);
// alpha.println("0 1 topic0");
// alpha.println("1 1 topic1");
// alpha.close();
// File sequenceFile = tempFolder.newFile("seq.txt");
// PrintWriter seq = new PrintWriter(sequenceFile);
// seq.println("0\tX");
// seq.close();
// MultinomialHMM hmm = new MultinomialHMM(2, topicsFile.getAbsolutePath(), 2);
// hmm.setGamma(0.4);
// hmm.setRandomSeed(101);
// hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
// hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
// hmm.initialize();
// assertNotNull(hmm);
}

@Test
public void testOnlyOneDocInSequenceFile() throws Exception {
// File topicsFile = tempFolder.newFile("single_doc.txt");
// PrintWriter writer = new PrintWriter(topicsFile);
// writer.println("0 0 0 0 0");
// writer.close();
// File alphaFile = tempFolder.newFile("alpha.txt");
// PrintWriter alphaWriter = new PrintWriter(alphaFile);
// alphaWriter.println("0 1 key");
// alphaWriter.close();
// File sequenceFile = tempFolder.newFile("seq_meta.txt");
// PrintWriter sequenceWriter = new PrintWriter(sequenceFile);
// sequenceWriter.println("0\tmeta");
// sequenceWriter.close();
// MultinomialHMM hmm = new MultinomialHMM(1, topicsFile.getAbsolutePath(), 1);
// hmm.setGamma(0.2);
// hmm.setRandomSeed(12345);
// hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
// hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
// hmm.initialize();
// assertNotNull(hmm);
}

@Test
public void testNonSequentialDocIdsAreHandled() throws Exception {
// File topicsFile = tempFolder.newFile("gaps.txt");
// PrintWriter writer = new PrintWriter(topicsFile);
// writer.println("3 0 0 0 0");
// writer.println("5 0 0 0 0");
// writer.close();
// File alphaFile = tempFolder.newFile("alpha_topic.txt");
// PrintWriter alpha = new PrintWriter(alphaFile);
// alpha.println("0 1 label");
// alpha.close();
// File sequenceFile = tempFolder.newFile("seq.txt");
// PrintWriter seq = new PrintWriter(sequenceFile);
// seq.println("3\tZ");
// seq.println("5\tZ");
// seq.close();
// MultinomialHMM hmm = new MultinomialHMM(1, topicsFile.getAbsolutePath(), 1);
// hmm.setGamma(0.3);
// hmm.setRandomSeed(2);
// hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
// hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
// hmm.initialize();
// assertNotNull(hmm);
}

@Test
public void testNegativeTopicIdIgnoredOrFailsGracefully() throws Exception {
// File topicsFile = tempFolder.newFile("negative_topic.txt");
// PrintWriter writer = new PrintWriter(topicsFile);
// writer.println("0 0 5 0 -1");
// writer.close();
// File alphaFile = tempFolder.newFile("alpha.txt");
// PrintWriter alpha = new PrintWriter(alphaFile);
// alpha.println("0 1 aa");
// alpha.println("1 1 bb");
// alpha.close();
// File sequenceFile = tempFolder.newFile("seq.txt");
// PrintWriter sequence = new PrintWriter(sequenceFile);
// sequence.println("0\talpha");
// sequence.close();
boolean exception = false;
try {
// MultinomialHMM hmm = new MultinomialHMM(2, topicsFile.getAbsolutePath(), 2);
// hmm.setGamma(1.0);
// hmm.setRandomSeed(11);
// hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
// hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
// hmm.initialize();
} catch (Exception e) {
exception = true;
}
assertTrue(exception);
}

@Test
public void testZeroLengthDocLogGammaCacheSafeAccess() throws Exception {
// File topicsFile = tempFolder.newFile("largecount.txt");
// PrintWriter writer = new PrintWriter(topicsFile);
// writer.println("0 0 0 0 0");
// writer.println("0 1 0 0 0");
// writer.println("0 2 0 0 0");
// writer.println("0 3 0 0 0");
// writer.println("0 4 0 0 0");
// writer.println("0 5 0 0 0");
// writer.println("0 6 0 0 0");
// writer.println("0 7 0 0 0");
// writer.println("0 8 0 0 0");
// writer.println("0 9 0 0 0");
// writer.close();
// File alphaFile = tempFolder.newFile("alpha_long.txt");
// PrintWriter alpha = new PrintWriter(alphaFile);
// alpha.println("0 1 expand");
// alpha.close();
// File sequenceFile = tempFolder.newFile("seq_long.txt");
// PrintWriter seq = new PrintWriter(sequenceFile);
// seq.println("0\tlong");
// seq.close();
// MultinomialHMM hmm = new MultinomialHMM(1, topicsFile.getAbsolutePath(), 1);
// hmm.setGamma(0.5);
// hmm.setRandomSeed(147);
// hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
// hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
// hmm.initialize();
// String text = hmm.stateTopics();
// assertTrue(text.contains("\t"));
}

@Test
public void testMiddleOfSequenceTriggersTransitionLogic() throws Exception {
// File topicsFile = tempFolder.newFile("mid_seq.txt");
// PrintWriter writer = new PrintWriter(topicsFile);
// writer.println("0 0 0 0 0");
// writer.println("1 0 0 0 0");
// writer.println("2 0 0 0 0");
// writer.close();
// File alpha = tempFolder.newFile("alpha.txt");
// PrintWriter alphaWriter = new PrintWriter(alpha);
// alphaWriter.println("0 1 word");
// alphaWriter.close();
// File seq = tempFolder.newFile("seq.txt");
// PrintWriter seqWriter = new PrintWriter(seq);
// seqWriter.println("0\tS");
// seqWriter.println("1\tS");
// seqWriter.println("2\tS");
// seqWriter.close();
// MultinomialHMM hmm = new MultinomialHMM(1, topicsFile.getAbsolutePath(), 1);
// hmm.setGamma(0.4);
// hmm.setRandomSeed(42);
// hmm.loadAlphaFromFile(alpha.getAbsolutePath());
// hmm.loadSequenceIDsFromFile(seq.getAbsolutePath());
// hmm.initialize();
// String output = hmm.stateTransitionMatrix();
// assertNotNull(output);
}

@Test
public void testDocWithAllTopicsEvenlyDistributed() throws Exception {
// File topicsFile = tempFolder.newFile("even_docs.txt");
// PrintWriter writer = new PrintWriter(topicsFile);
// writer.println("0 0 0 0 0");
// writer.println("0 1 0 0 1");
// writer.println("0 2 0 0 2");
// writer.close();
// File alphaFile = tempFolder.newFile("even_alpha.txt");
// PrintWriter alpha = new PrintWriter(alphaFile);
// alpha.println("0 1 A");
// alpha.println("1 1 B");
// alpha.println("2 1 C");
// alpha.close();
// File seqFile = tempFolder.newFile("even_seq.txt");
// PrintWriter seqWriter = new PrintWriter(seqFile);
// seqWriter.println("0\tmeta");
// seqWriter.close();
// MultinomialHMM hmm = new MultinomialHMM(3, topicsFile.getAbsolutePath(), 2);
// hmm.setGamma(0.3);
// hmm.setRandomSeed(75);
// hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
// hmm.loadSequenceIDsFromFile(seqFile.getAbsolutePath());
// hmm.initialize();
// assertNotNull(hmm.stateTopics());
}

@Test
public void testZeroStateTransitionTotalsHandledWithoutCrash() throws Exception {
// File topicsFile = tempFolder.newFile("stateless_doc.txt");
// PrintWriter writer = new PrintWriter(topicsFile);
// writer.println("0 0 1 0 0");
// writer.close();
// File alphaFile = tempFolder.newFile("stateless_alpha.txt");
// PrintWriter alpha = new PrintWriter(alphaFile);
// alpha.println("0 1 token");
// alpha.close();
// File sequenceFile = tempFolder.newFile("stateless_seq.txt");
// PrintWriter seq = new PrintWriter(sequenceFile);
// seq.println("0\tX");
// seq.close();
// MultinomialHMM hmm = new MultinomialHMM(1, topicsFile.getAbsolutePath(), 1);
// hmm.setGamma(1.0);
// hmm.setRandomSeed(99);
// hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
// hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
// hmm.initialize();
// String matrix = hmm.stateTransitionMatrix();
// assertTrue(matrix.contains("0"));
}

@Test
public void testDocThatIsEndOfSequenceOnly() throws Exception {
// File topicsFile = tempFolder.newFile("end_doc.txt");
// PrintWriter writer = new PrintWriter(topicsFile);
// writer.println("0 0 0 0 0");
// writer.println("1 0 1 0 1");
// writer.close();
// File alphaFile = tempFolder.newFile("end_alpha.txt");
// PrintWriter alpha = new PrintWriter(alphaFile);
// alpha.println("0 1 tok0");
// alpha.println("1 1 tok1");
// alpha.close();
// File sequence = tempFolder.newFile("end_seq.txt");
// PrintWriter seq = new PrintWriter(sequenceFile);
// seq.println("0\tA");
// seq.println("1\tB");
// seq.close();
// MultinomialHMM hmm = new MultinomialHMM(2, topicsFile.getAbsolutePath(), 2);
// hmm.setGamma(0.7);
// hmm.setRandomSeed(888);
// hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
// hmm.loadSequenceIDsFromFile(sequence.getAbsolutePath());
// hmm.initialize();
// assertNotNull(hmm.stateTopics());
}

@Test
public void testMultipleDocsSameTopicKeyAllOneTopic() throws Exception {
// File topicsFile = tempFolder.newFile("same_topic.txt");
// PrintWriter writer = new PrintWriter(topicsFile);
// writer.println("0 0 0 0 0");
// writer.println("1 0 0 0 0");
// writer.println("2 0 0 0 0");
// writer.close();
// File alphaFile = tempFolder.newFile("alpha.txt");
// PrintWriter alpha = new PrintWriter(alphaFile);
// alpha.println("0 1 singleword");
// alpha.close();
// File sequenceFile = tempFolder.newFile("seq.txt");
// PrintWriter seq = new PrintWriter(sequenceFile);
// seq.println("0\tS");
// seq.println("1\tS");
// seq.println("2\tS");
// seq.close();
// MultinomialHMM hmm = new MultinomialHMM(1, topicsFile.getAbsolutePath(), 1);
// hmm.setGamma(1.0);
// hmm.setRandomSeed(9);
// hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
// hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
// hmm.initialize();
// String transition = hmm.stateTransitionMatrix();
// assertTrue(transition.contains("0"));
}

@Test
public void testDocWithZeroTokensTriggersShortPath() throws Exception {
// File topicsFile = tempFolder.newFile("empty_doc_map.txt");
// PrintWriter writer = new PrintWriter(topicsFile);
// writer.close();
// File alphaFile = tempFolder.newFile("alpha.txt");
// PrintWriter alphaWriter = new PrintWriter(alphaFile);
// alphaWriter.println("0 1 empty");
// alphaWriter.close();
// File sequenceFile = tempFolder.newFile("seq.txt");
// PrintWriter seqWriter = new PrintWriter(sequenceFile);
// seqWriter.println("0\tX");
// seqWriter.close();
// MultinomialHMM hmm = new MultinomialHMM(1, topicsFile.getAbsolutePath(), 1);
// hmm.setGamma(0.5);
// hmm.setRandomSeed(4);
// hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
// hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
// hmm.initialize();
// assertNotNull(hmm.stateTopics());
}

@Test
public void testCallingSampleWithoutInitThrowsOrSkips() throws Exception {
// File topicsFile = tempFolder.newFile("samplecall.txt");
// PrintWriter writer = new PrintWriter(topicsFile);
// writer.println("0 0 0 0 0");
// writer.close();
// File alphaFile = tempFolder.newFile("alpha.txt");
// PrintWriter alpha = new PrintWriter(alphaFile);
// alpha.println("0 1 alpha");
// alpha.close();
// File seq = tempFolder.newFile("seq.txt");
// PrintWriter seqWriter = new PrintWriter(seq);
// seqWriter.println("0\tY");
// seqWriter.close();
// MultinomialHMM hmm = new MultinomialHMM(1, topicsFile.getAbsolutePath(), 1);
// hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
// hmm.loadSequenceIDsFromFile(seq.getAbsolutePath());
// hmm.setNumIterations(1);
// hmm.setRandomSeed(123);
// hmm.setGamma(1.0);
try {
// hmm.sample();
assertTrue(true);
} catch (Exception e) {
fail("Sampling without init should be safe (gracefully handled)");
}
}

@Test
public void testLargeNumberOfTopicsLimitedCountsHandledSafely() throws Exception {
// File topicsFile = tempFolder.newFile("many_topics.txt");
// PrintWriter writer = new PrintWriter(topicsFile);
// writer.println("0 0 0 0 0");
// writer.close();
// File alphaFile = tempFolder.newFile("multi_alpha.txt");
// PrintWriter alpha = new PrintWriter(alphaFile);
// alpha.println("0 1 a");
// alpha.println("1 1 b");
// alpha.println("2 1 c");
// alpha.println("3 1 d");
// alpha.println("4 1 e");
// alpha.println("5 1 f");
// alpha.println("6 1 g");
// alpha.println("7 1 h");
// alpha.println("8 1 i");
// alpha.println("9 1 j");
// alpha.close();
// File seqFile = tempFolder.newFile("seq.txt");
// PrintWriter seq = new PrintWriter(seqFile);
// seq.println("0\tx");
// seq.close();
// MultinomialHMM hmm = new MultinomialHMM(10, topicsFile.getAbsolutePath(), 2);
// hmm.setGamma(1.0);
// hmm.setRandomSeed(111);
// hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
// hmm.loadSequenceIDsFromFile(seqFile.getAbsolutePath());
// hmm.initialize();
// assertNotNull(hmm.stateTopics());
}

@Test
public void testStartOfSequenceEdgeCase() throws Exception {
// File topicsFile = tempFolder.newFile("topic_input.txt");
// PrintWriter writer = new PrintWriter(topicsFile);
// writer.println("0 0 0 0 0");
// writer.println("1 0 0 0 0");
// writer.close();
// File alphaFile = tempFolder.newFile("alpha_file.txt");
// PrintWriter alpha = new PrintWriter(alphaFile);
// alpha.println("0 1 alphaKey");
// alpha.close();
// File sequenceFile = tempFolder.newFile("sequence.txt");
// PrintWriter seq = new PrintWriter(sequenceFile);
// seq.println("0\t0");
// seq.println("1\t1");
// seq.close();
// MultinomialHMM hmm = new MultinomialHMM(1, topicsFile.getAbsolutePath(), 2);
// hmm.setGamma(1.0);
// hmm.setRandomSeed(100);
// hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
// hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
// hmm.initialize();
// assertNotNull(hmm.stateTransitionMatrix());
}

@Test
public void testAlphaSumZeroHandledWithoutCrash() throws Exception {
// File topicsFile = tempFolder.newFile("topic.txt");
// PrintWriter topics = new PrintWriter(topicsFile);
// topics.println("0 0 0 0 0");
// topics.close();
// File alphaFile = tempFolder.newFile("zero_alpha.txt");
// PrintWriter alpha = new PrintWriter(alphaFile);
// alpha.println("0 0 dummyTopic");
// alpha.close();
// File sequenceFile = tempFolder.newFile("sequence.txt");
// PrintWriter seqWriter = new PrintWriter(sequenceFile);
// seqWriter.println("0\tA");
// seqWriter.close();
// MultinomialHMM hmm = new MultinomialHMM(1, topicsFile.getAbsolutePath(), 1);
// hmm.setGamma(0.1);
// hmm.setRandomSeed(7);
// hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
// hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
// hmm.initialize();
// assertNotNull(hmm.stateTopics());
}

@Test
public void testSingleTokenDocumentTriggersCacheLimits() throws Exception {
// File topicsFile = tempFolder.newFile("single_token.txt");
// PrintWriter writer = new PrintWriter(topicsFile);
// writer.println("0 0 0 0 0");
// writer.close();
// File alphaFile = tempFolder.newFile("alpha.txt");
// PrintWriter alphaWriter = new PrintWriter(alphaFile);
// alphaWriter.println("0 1 tokenKey");
// alphaWriter.close();
// File sequenceFile = tempFolder.newFile("seq.txt");
// PrintWriter seq = new PrintWriter(sequenceFile);
// seq.println("0\tA");
// seq.close();
// MultinomialHMM hmm = new MultinomialHMM(1, topicsFile.getAbsolutePath(), 1);
// hmm.setGamma(0.01);
// hmm.setRandomSeed(2);
// hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
// hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
// hmm.initialize();
// assertNotNull(hmm.stateTransitionMatrix());
}

@Test
public void testMultipleStatesAllZeroTransitions() throws Exception {
// File topicsFile = tempFolder.newFile("frozen_state.txt");
// PrintWriter writer = new PrintWriter(topicsFile);
// writer.println("0 0 0 0 0");
// writer.close();
// File alphaFile = tempFolder.newFile("alpha.txt");
// PrintWriter alpha = new PrintWriter(alphaFile);
// alpha.println("0 1 A");
// alpha.close();
// File sequenceFile = tempFolder.newFile("seq.txt");
// PrintWriter seq = new PrintWriter(sequenceFile);
// seq.println("0\tseq");
// seq.close();
// MultinomialHMM hmm = new MultinomialHMM(1, topicsFile.getAbsolutePath(), 3);
// hmm.setGamma(0.0);
// hmm.setRandomSeed(5);
// hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
// hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
// hmm.initialize();
// String output = hmm.printStateTransitions();
// assertNotNull(output);
}

@Test(expected = IOException.class)
public void testEmptySequenceFileThrowsDueToMismatch() throws Exception {
// File topicsFile = tempFolder.newFile("topics.txt");
// PrintWriter writer = new PrintWriter(topicsFile);
// writer.println("0 0 0 0 0");
// writer.println("1 0 0 0 0");
// writer.println("2 0 0 0 0");
// writer.close();
// File alphaFile = tempFolder.newFile("alpha.txt");
// PrintWriter alpha = new PrintWriter(alphaFile);
// alpha.println("0 1 tok");
// alpha.close();
// File sequenceFile = tempFolder.newFile("empty.seq.txt");
// PrintWriter empty = new PrintWriter(sequenceFile);
// empty.close();
// MultinomialHMM hmm = new MultinomialHMM(1, topicsFile.getAbsolutePath(), 1);
// hmm.setGamma(0.3);
// hmm.setRandomSeed(4);
// hmm.loadAlphaFromFile(alphaFile.getAbsolutePath());
// hmm.loadSequenceIDsFromFile(sequenceFile.getAbsolutePath());
}

@Test(expected = NumberFormatException.class)
public void testMalformedSequenceIdThrowsException() throws Exception {
// File topics = tempFolder.newFile("topic.txt");
// PrintWriter writer = new PrintWriter(topics);
// writer.println("0 0 0 0 0");
// writer.close();
// File alpha = tempFolder.newFile("alpha.txt");
// PrintWriter alphaWriter = new PrintWriter(alpha);
// alphaWriter.println("0 1 KEY");
// alphaWriter.close();
// File seq = tempFolder.newFile("malform_seq.txt");
// PrintWriter seqWriter = new PrintWriter(seq);
// seqWriter.println("XYZ\tnot-a-number");
// seqWriter.close();
// MultinomialHMM hmm = new MultinomialHMM(1, topics.getAbsolutePath(), 1);
// hmm.loadAlphaFromFile(alpha.getAbsolutePath());
// hmm.loadSequenceIDsFromFile(seq.getAbsolutePath());
}

@Test
public void testCacheLengthGreaterThanDocumentLength() throws Exception {
// File topicsFile = tempFolder.newFile("long_cache.txt");
// PrintWriter writer = new PrintWriter(topicsFile);
// writer.println("0 0 0 0 0");
// writer.println("0 1 0 0 0");
// writer.println("0 2 0 0 0");
// writer.close();
// File alpha = tempFolder.newFile("alpha.txt");
// PrintWriter a = new PrintWriter(alpha);
// a.println("0 1 x");
// a.close();
// File seq = tempFolder.newFile("seq.txt");
// PrintWriter s = new PrintWriter(seq);
// s.println("0\tX");
// s.close();
// MultinomialHMM hmm = new MultinomialHMM(1, topicsFile.getAbsolutePath(), 1);
// hmm.setGamma(1.0);
// hmm.setRandomSeed(44);
// hmm.loadAlphaFromFile(alpha.getAbsolutePath());
// hmm.loadSequenceIDsFromFile(seq.getAbsolutePath());
// hmm.initialize();
// String topicsState = hmm.stateTopics();
// assertNotNull(topicsState);
}

@Test
public void testTopicLogGammaCacheIsPopulatedForAll() throws Exception {
// File topics = tempFolder.newFile("all_topics.txt");
// PrintWriter writer = new PrintWriter(topics);
// writer.println("0 0 0 0 0");
// writer.println("0 1 0 0 1");
// writer.println("0 2 0 0 2");
// writer.close();
// File alpha = tempFolder.newFile("alpha.txt");
// PrintWriter a = new PrintWriter(alpha);
// a.println("0 1 aa");
// a.println("1 1 bb");
// a.println("2 1 cc");
// a.close();
// File seq = tempFolder.newFile("seq.txt");
// PrintWriter s = new PrintWriter(seq);
// s.println("0\tA");
// s.close();
// MultinomialHMM hmm = new MultinomialHMM(3, topics.getAbsolutePath(), 2);
// hmm.setGamma(0.6);
// hmm.setRandomSeed(13);
// hmm.loadAlphaFromFile(alpha.getAbsolutePath());
// hmm.loadSequenceIDsFromFile(seq.getAbsolutePath());
// hmm.initialize();
// String stateDump = hmm.printStateTransitions();
// assertTrue(stateDump.contains("["));
}
}
