package cc.mallet.topics;

import cc.mallet.types.*;
import cc.mallet.util.Randoms;
import org.junit.Test;
import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.zip.GZIPOutputStream;
import static org.junit.Assert.*;

public class MultinomialHMM_llmsuite_3_GPTLLMTest {

@Test
public void testConstructorInitializesFieldsCorrectly() throws IOException {
File stateFile = new File("temp.state");
PrintWriter writer = new PrintWriter(stateFile);
writer.println("0 0 0 X 0");
writer.println("1 0 0 X 1");
writer.println("2 0 0 X 2");
writer.close();
MultinomialHMM model = new MultinomialHMM(3, stateFile.getAbsolutePath(), 2);
assertNotNull(model);
assertEquals(3, model.numTopics);
assertEquals(2, model.numStates);
assertTrue(model.maxTokensPerTopic.length == 3);
assertNotNull(model.docLogGammaCache);
assertTrue(model.topicLogGammaCache.length == 2);
stateFile.delete();
}

@Test
public void testLoadAlphaFromFileSetsAlphaCorrectly() throws IOException {
File stateFile = new File("temp.state");
PrintWriter stateWriter = new PrintWriter(stateFile);
stateWriter.println("0 0 0 X 0");
stateWriter.println("1 0 0 X 1");
stateWriter.println("2 0 0 X 2");
stateWriter.close();
File alphaFile = new File("temp.alpha");
PrintWriter alphaWriter = new PrintWriter(alphaFile);
alphaWriter.println("0 1.0 topic_one");
alphaWriter.println("1 1.0 topic_two");
alphaWriter.println("2 1.0 topic_three");
alphaWriter.close();
MultinomialHMM model = new MultinomialHMM(3, stateFile.getAbsolutePath(), 2);
model.loadAlphaFromFile(alphaFile.getAbsolutePath());
assertEquals(3, model.alpha.length);
assertEquals(1.0, model.alpha[0], 0.0001);
assertEquals(1.0, model.alpha[1], 0.0001);
assertEquals(1.0, model.alpha[2], 0.0001);
assertNotNull(model.topicKeys[0]);
assertTrue(model.alphaSum > 0.0);
stateFile.delete();
alphaFile.delete();
}

@Test
public void testLoadSequenceIDsCorrectly() throws IOException {
File stateFile = new File("temp.state");
PrintWriter stateWriter = new PrintWriter(stateFile);
stateWriter.println("0 0 0 X 0");
stateWriter.println("1 0 0 X 1");
stateWriter.println("2 0 0 X 2");
stateWriter.close();
File alphaFile = new File("temp.alpha");
PrintWriter alphaWriter = new PrintWriter(alphaFile);
alphaWriter.println("0 1.0 key0");
alphaWriter.println("1 1.0 key1");
alphaWriter.println("2 1.0 key2");
alphaWriter.close();
File seqFile = new File("temp.seq");
PrintWriter seqWriter = new PrintWriter(seqFile);
seqWriter.println("0\tseqA");
seqWriter.println("1\tseqA");
seqWriter.println("2\tseqB");
seqWriter.close();
MultinomialHMM model = new MultinomialHMM(3, stateFile.getAbsolutePath(), 2);
model.loadAlphaFromFile(alphaFile.getAbsolutePath());
model.loadSequenceIDsFromFile(seqFile.getAbsolutePath());
assertEquals(3, model.documentSequenceIDs.length);
stateFile.delete();
alphaFile.delete();
seqFile.delete();
}

@Test
public void testInitializeExecutesSuccessfully() throws IOException {
File stateFile = new File("temp.state");
PrintWriter stateWriter = new PrintWriter(stateFile);
stateWriter.println("0 0 0 X 0");
stateWriter.println("1 0 0 X 1");
stateWriter.println("2 0 0 X 2");
stateWriter.close();
File alphaFile = new File("temp.alpha");
PrintWriter alphaWriter = new PrintWriter(alphaFile);
alphaWriter.println("0 1.0 k0");
alphaWriter.println("1 1.0 k1");
alphaWriter.println("2 1.0 k2");
alphaWriter.close();
File seqFile = new File("temp.seq");
PrintWriter seqWriter = new PrintWriter(seqFile);
seqWriter.println("0\t0");
seqWriter.println("1\t0");
seqWriter.println("2\t1");
seqWriter.close();
MultinomialHMM model = new MultinomialHMM(3, stateFile.getAbsolutePath(), 2);
model.setGamma(1.0);
model.setRandomSeed(123);
model.loadAlphaFromFile(alphaFile.getAbsolutePath());
model.loadSequenceIDsFromFile(seqFile.getAbsolutePath());
model.initialize();
assertNotNull(model.stateTopicCounts);
assertEquals(2, model.stateTopicCounts.length);
assertEquals(3, model.stateTopicCounts[0].length);
stateFile.delete();
alphaFile.delete();
seqFile.delete();
}

@Test
public void testSampleRunsWithoutError() throws IOException {
File stateFile = new File("temp.state");
PrintWriter stateWriter = new PrintWriter(stateFile);
stateWriter.println("0 0 0 X 0");
stateWriter.println("1 0 0 X 1");
stateWriter.println("2 0 0 X 2");
stateWriter.close();
File alphaFile = new File("temp.alpha");
PrintWriter alphaWriter = new PrintWriter(alphaFile);
alphaWriter.println("0 1.0 k0");
alphaWriter.println("1 1.0 k1");
alphaWriter.println("2 1.0 k2");
alphaWriter.close();
File seqFile = new File("temp.seq");
PrintWriter seqWriter = new PrintWriter(seqFile);
seqWriter.println("0\t0");
seqWriter.println("1\t0");
seqWriter.println("2\t1");
seqWriter.close();
MultinomialHMM model = new MultinomialHMM(3, stateFile.getAbsolutePath(), 2);
model.setGamma(1.0);
model.setRandomSeed(77);
model.setNumIterations(1);
model.loadAlphaFromFile(alphaFile.getAbsolutePath());
model.loadSequenceIDsFromFile(seqFile.getAbsolutePath());
model.initialize();
model.sample();
assertNotNull(model.documentStates);
assertEquals(3, model.documentStates.length);
stateFile.delete();
alphaFile.delete();
seqFile.delete();
new File("state_state_matrix.1").delete();
new File("state_topics.1").delete();
new File("states.1").delete();
}

@Test
public void testStateTransitionMatrixFormatIsValid() throws IOException {
File stateFile = new File("temp.state");
PrintWriter writer = new PrintWriter(stateFile);
writer.println("0 0 0 X 0");
writer.println("1 0 0 X 1");
writer.println("2 0 0 X 0");
writer.close();
File alphaFile = new File("temp.alpha");
PrintWriter alphaWriter = new PrintWriter(alphaFile);
alphaWriter.println("0 1.0 a");
alphaWriter.println("1 1.0 b");
alphaWriter.println("2 1.0 c");
alphaWriter.close();
File seqFile = new File("temp.seq");
PrintWriter seqWriter = new PrintWriter(seqFile);
seqWriter.println("0\t0");
seqWriter.println("1\t0");
seqWriter.println("2\t1");
seqWriter.close();
MultinomialHMM model = new MultinomialHMM(3, stateFile.getAbsolutePath(), 2);
model.setRandomSeed(1);
model.setGamma(1.0);
model.loadAlphaFromFile(alphaFile.getAbsolutePath());
model.loadSequenceIDsFromFile(seqFile.getAbsolutePath());
model.initialize();
String matrix = model.stateTransitionMatrix();
assertNotNull(matrix);
assertTrue(matrix.contains("\n"));
stateFile.delete();
alphaFile.delete();
seqFile.delete();
}

@Test
public void testStateTopicsOutputIsValid() throws IOException {
File stateFile = new File("temp.state");
PrintWriter writer = new PrintWriter(stateFile);
writer.println("0 0 0 X 0");
writer.println("1 0 0 X 1");
writer.println("2 0 0 X 1");
writer.close();
File alphaFile = new File("temp.alpha");
PrintWriter alphaWriter = new PrintWriter(alphaFile);
alphaWriter.println("0 1.0 t0");
alphaWriter.println("1 1.0 t1");
alphaWriter.println("2 1.0 t2");
alphaWriter.close();
File seqFile = new File("temp.seq");
PrintWriter seqWriter = new PrintWriter(seqFile);
seqWriter.println("0\t0");
seqWriter.println("1\t0");
seqWriter.println("2\t1");
seqWriter.close();
MultinomialHMM model = new MultinomialHMM(3, stateFile.getAbsolutePath(), 2);
model.setGamma(1.0);
model.setRandomSeed(2);
model.loadAlphaFromFile(alphaFile.getAbsolutePath());
model.loadSequenceIDsFromFile(seqFile.getAbsolutePath());
model.initialize();
String result = model.stateTopics();
assertNotNull(result);
assertTrue(result.length() > 0);
stateFile.delete();
alphaFile.delete();
seqFile.delete();
}

@Test
public void testEmptyStateFileDoesNotCrash() throws IOException {
File emptyState = new File("empty_state.test");
new PrintWriter(emptyState).close();
MultinomialHMM model = new MultinomialHMM(3, emptyState.getAbsolutePath(), 2);
assertTrue(model.documentTopics.isEmpty());
emptyState.delete();
}

@Test
public void testEmptyAlphaFileLeavesDefaults() throws IOException {
File state = new File("state_data.test");
PrintWriter w1 = new PrintWriter(state);
w1.println("0 0 0 X 0");
w1.close();
File emptyAlpha = new File("empty_alpha.test");
new PrintWriter(emptyAlpha).close();
MultinomialHMM model = new MultinomialHMM(3, state.getAbsolutePath(), 2);
model.loadAlphaFromFile(emptyAlpha.getAbsolutePath());
assertEquals(3, model.alpha.length);
assertEquals(0.0, model.alphaSum, 0.0001);
emptyAlpha.delete();
state.delete();
}

@Test
public void testInvalidAlphaFileLineFormatSkipsLine() throws IOException {
File state = new File("state_data.test");
PrintWriter w1 = new PrintWriter(state);
w1.println("0 0 0 X 0");
w1.close();
File alpha = new File("bada_alpha.test");
PrintWriter w2 = new PrintWriter(alpha);
w2.println("invalid line without enough fields");
w2.println("1 1.0 topicValid");
w2.close();
MultinomialHMM model = new MultinomialHMM(3, state.getAbsolutePath(), 2);
model.loadAlphaFromFile(alpha.getAbsolutePath());
assertEquals(1.0, model.alpha[1], 0.0001);
alpha.delete();
state.delete();
}

@Test
public void testLoadSequenceIDs_WithMismatchCounts_WarnsUser() throws IOException {
File state = new File("state_data.test");
PrintWriter w1 = new PrintWriter(state);
w1.println("0 0 0 X 0");
w1.println("1 0 0 X 1");
w1.close();
File alpha = new File("alpha_file.test");
PrintWriter w2 = new PrintWriter(alpha);
w2.println("0 1.0 key");
w2.println("1 1.0 key2");
w2.println("2 1.0 key3");
w2.close();
File seqFile = new File("seq_mismatch.test");
PrintWriter w3 = new PrintWriter(seqFile);
w3.println("0\tA");
w3.close();
MultinomialHMM model = new MultinomialHMM(3, state.getAbsolutePath(), 2);
model.loadAlphaFromFile(alpha.getAbsolutePath());
model.loadSequenceIDsFromFile(seqFile.getAbsolutePath());
assertTrue(model.documentSequenceIDs.length >= 1);
state.delete();
alpha.delete();
seqFile.delete();
}

@Test
public void testSetGammaAffectsGammaSum() throws IOException {
File state = new File("state_data.test");
PrintWriter writer = new PrintWriter(state);
writer.println("0 0 0 X 0");
writer.close();
MultinomialHMM model = new MultinomialHMM(3, state.getAbsolutePath(), 2);
model.setGamma(0.5);
model.setRandomSeed(42);
File alpha = new File("alpha_file.test");
PrintWriter alphaWriter = new PrintWriter(alpha);
alphaWriter.println("0 1.0 a");
alphaWriter.println("1 1.0 b");
alphaWriter.println("2 1.0 c");
alphaWriter.close();
File seqFile = new File("temp.seq");
PrintWriter seqWriter = new PrintWriter(seqFile);
seqWriter.println("0\t0");
seqWriter.close();
model.loadAlphaFromFile(alpha.getAbsolutePath());
model.loadSequenceIDsFromFile(seqFile.getAbsolutePath());
model.initialize();
assertEquals(1.0, model.gammaSum, 0.0001);
state.delete();
alpha.delete();
seqFile.delete();
}

@Test
public void testSamplingWithNoDocumentTopicsDoesNotCrash() throws IOException {
File state = new File("empty_topics_state.test");
PrintWriter w = new PrintWriter(state);
w.close();
File alpha = new File("alpha_file.test");
PrintWriter aw = new PrintWriter(alpha);
aw.println("0 1.0 a");
aw.println("1 1.0 b");
aw.println("2 1.0 c");
aw.close();
File seqFile = new File("seq_file.test");
PrintWriter sw = new PrintWriter(seqFile);
sw.println("0\t0");
sw.close();
MultinomialHMM model = new MultinomialHMM(3, state.getAbsolutePath(), 2);
model.setRandomSeed(123);
model.setGamma(1.0);
model.loadAlphaFromFile(alpha.getAbsolutePath());
model.loadSequenceIDsFromFile(seqFile.getAbsolutePath());
model.initialize();
model.sample();
state.delete();
alpha.delete();
seqFile.delete();
File stateOut = new File("state_state_matrix.1");
File topicOut = new File("state_topics.1");
File states = new File("states.1");
stateOut.delete();
topicOut.delete();
states.delete();
}

@Test
public void testLoadTopicsFromFileWithCompressedGzip() throws IOException {
File state = new File("state_file.txt");
PrintWriter w = new PrintWriter(state);
w.println("0 0 1 X 0");
w.println("1 0 1 X 1");
w.close();
File gzipped = new File("state_file.txt.gz");
FileInputStream fis = new FileInputStream(state);
FileOutputStream fos = new FileOutputStream(gzipped);
BufferedOutputStream bos = new BufferedOutputStream(fos);
GZIPOutputStream gzipOut = new GZIPOutputStream(bos);
byte[] buffer = new byte[1024];
int len;
while ((len = fis.read(buffer)) > 0) {
gzipOut.write(buffer, 0, len);
}
gzipOut.close();
bos.close();
fos.close();
fis.close();
MultinomialHMM model = new MultinomialHMM(3, gzipped.getAbsolutePath(), 2);
assertEquals(2, model.numDocs);
state.delete();
gzipped.delete();
}

@Test
public void testRecacheWithZeroCountsCachesZerosCorrectly() throws IOException {
File state = new File("recache_state.test");
PrintWriter writer = new PrintWriter(state);
writer.println("0 0 0 X 0");
writer.close();
File alpha = new File("recache_alpha.test");
PrintWriter aw = new PrintWriter(alpha);
aw.println("0 1.0 topic");
aw.println("1 1.0 topic2");
aw.println("2 1.0 topic3");
aw.close();
File seqFile = new File("recache_seq.test");
PrintWriter sw = new PrintWriter(seqFile);
sw.println("0\t0");
sw.close();
MultinomialHMM model = new MultinomialHMM(3, state.getAbsolutePath(), 2);
model.setGamma(1.0);
model.setRandomSeed(222);
model.loadAlphaFromFile(alpha.getAbsolutePath());
model.loadSequenceIDsFromFile(seqFile.getAbsolutePath());
model.initialize();
double first = model.docLogGammaCache[0][0];
double second = model.docLogGammaCache[0][1];
assertEquals(0.0, first, 0.0001);
assertTrue(second > first);
state.delete();
alpha.delete();
seqFile.delete();
}

@Test
public void testLoadTopics_FileWithOnlyComments_SkipsAllLines() throws IOException {
File stateFile = new File("comment_only_state.txt");
PrintWriter writer = new PrintWriter(stateFile);
writer.println("# this is a comment");
writer.println("# another comment");
writer.close();
MultinomialHMM model = new MultinomialHMM(3, stateFile.getAbsolutePath(), 2);
assertTrue(model.documentTopics.isEmpty());
stateFile.delete();
}

@Test
public void testAllDocumentsSameTopicValues() throws IOException {
File stateFile = new File("same_topic_state.txt");
PrintWriter writer = new PrintWriter(stateFile);
writer.println("0 0 0 X 1");
writer.println("1 0 0 X 1");
writer.println("2 0 0 X 1");
writer.close();
File alphaFile = new File("default_alpha.txt");
PrintWriter alphaWriter = new PrintWriter(alphaFile);
alphaWriter.println("0 1.0 A");
alphaWriter.println("1 1.0 B");
alphaWriter.println("2 1.0 C");
alphaWriter.close();
File seqFile = new File("same_seq.txt");
PrintWriter seqWriter = new PrintWriter(seqFile);
seqWriter.println("0\t0");
seqWriter.println("1\t0");
seqWriter.println("2\t0");
seqWriter.close();
MultinomialHMM model = new MultinomialHMM(3, stateFile.getAbsolutePath(), 2);
model.setGamma(1.0);
model.setRandomSeed(7);
model.loadAlphaFromFile(alphaFile.getAbsolutePath());
model.loadSequenceIDsFromFile(seqFile.getAbsolutePath());
model.initialize();
model.sample();
assertEquals(3, model.documentStates.length);
stateFile.delete();
alphaFile.delete();
seqFile.delete();
new File("state_state_matrix.1").delete();
new File("state_topics.1").delete();
new File("states.1").delete();
}

@Test
public void testVeryLargeTopicIndexHandling() throws IOException {
File stateFile = new File("large_topic_state.txt");
PrintWriter writer = new PrintWriter(stateFile);
writer.println("0 0 0 X 999");
writer.close();
MultinomialHMM model = new MultinomialHMM(1000, stateFile.getAbsolutePath(), 2);
assertTrue(model.maxTokensPerTopic[999] > 0);
stateFile.delete();
}

@Test
public void testZeroTopicDocumentsHandledGracefully() throws IOException {
File stateFile = new File("zero_topic_state.txt");
PrintWriter writer = new PrintWriter(stateFile);
writer.println("0 0 0 X  ");
writer.close();
MultinomialHMM model = new MultinomialHMM(3, stateFile.getAbsolutePath(), 2);
assertEquals(1, model.numDocs);
stateFile.delete();
}

@Test
public void testMinLengthDocLogGammaCacheGeneratedProperly() throws IOException {
File stateFile = new File("simple_state.txt");
PrintWriter writer = new PrintWriter(stateFile);
writer.println("0 0 0 X 0");
writer.close();
MultinomialHMM model = new MultinomialHMM(3, stateFile.getAbsolutePath(), 2);
assertEquals(1, model.maxDocLength);
assertEquals(2, model.docLogGammaCache.length);
assertEquals(2, model.docLogGammaCache[0].length);
stateFile.delete();
}

@Test
public void testStateTransitionMatrixReturnsExpectedLineCount() throws IOException {
File stateFile = new File("transmat_state.txt");
PrintWriter writer = new PrintWriter(stateFile);
writer.println("0 0 0 X 0");
writer.println("1 0 0 X 1");
writer.close();
File alphaFile = new File("transmat_alpha.txt");
PrintWriter alphaWriter = new PrintWriter(alphaFile);
alphaWriter.println("0 1.0 A");
alphaWriter.println("1 1.0 B");
alphaWriter.println("2 1.0 C");
alphaWriter.close();
File seqFile = new File("transmat_seq.txt");
PrintWriter seqWriter = new PrintWriter(seqFile);
seqWriter.println("0\t0");
seqWriter.println("1\t1");
seqWriter.close();
MultinomialHMM model = new MultinomialHMM(3, stateFile.getAbsolutePath(), 4);
model.setGamma(2.5);
model.setRandomSeed(15);
model.loadAlphaFromFile(alphaFile.getAbsolutePath());
model.loadSequenceIDsFromFile(seqFile.getAbsolutePath());
model.initialize();
String result = model.stateTransitionMatrix();
String[] lines = result.split("\n");
assertEquals(4, lines.length);
stateFile.delete();
alphaFile.delete();
seqFile.delete();
}

@Test
public void testSingletonSequenceClassificationPath() throws IOException {
File stateFile = new File("singleton_state.txt");
PrintWriter writer = new PrintWriter(stateFile);
writer.println("0 0 0 X 0");
writer.close();
File alphaFile = new File("singleton_alpha.txt");
PrintWriter alphaWriter = new PrintWriter(alphaFile);
alphaWriter.println("0 1.0 A");
alphaWriter.println("1 1.0 B");
alphaWriter.println("2 1.0 C");
alphaWriter.close();
File seqFile = new File("singleton_seq.txt");
PrintWriter seqWriter = new PrintWriter(seqFile);
seqWriter.println("0\t1");
seqWriter.close();
MultinomialHMM model = new MultinomialHMM(3, stateFile.getAbsolutePath(), 2);
model.setRandomSeed(100);
model.setGamma(0.8);
model.loadAlphaFromFile(alphaFile.getAbsolutePath());
model.loadSequenceIDsFromFile(seqFile.getAbsolutePath());
model.initialize();
model.sample();
assertEquals(1, model.documentStates.length);
stateFile.delete();
alphaFile.delete();
seqFile.delete();
new File("state_state_matrix.1").delete();
new File("state_topics.1").delete();
new File("states.1").delete();
}

@Test
public void testMultipleStateCountArraysHaveCorrectDimensions() throws IOException {
File stateFile = new File("dimensions_state.txt");
PrintWriter writer = new PrintWriter(stateFile);
writer.println("0 0 0 X 1");
writer.println("1 0 0 X 2");
writer.close();
MultinomialHMM model = new MultinomialHMM(5, stateFile.getAbsolutePath(), 4);
assertEquals(4, model.topicLogGammaCache.length);
assertEquals(5, model.topicLogGammaCache[0].length);
stateFile.delete();
}

@Test
public void testSingleTopicAndSingleDocument() throws IOException {
File state = new File("single_topic_state.txt");
PrintWriter writer = new PrintWriter(state);
writer.println("0 0 0 X 0");
writer.close();
File alpha = new File("alpha_one_topic.txt");
PrintWriter alphaWriter = new PrintWriter(alpha);
alphaWriter.println("0 1.0 topic0");
alphaWriter.close();
File seqFile = new File("seq_one.txt");
PrintWriter seqWriter = new PrintWriter(seqFile);
seqWriter.println("0\t0");
seqWriter.close();
MultinomialHMM model = new MultinomialHMM(1, state.getAbsolutePath(), 1);
model.setRandomSeed(1);
model.setGamma(0.5);
model.loadAlphaFromFile(alpha.getAbsolutePath());
model.loadSequenceIDsFromFile(seqFile.getAbsolutePath());
model.initialize();
model.sample();
assertEquals(1, model.documentStates.length);
state.delete();
alpha.delete();
seqFile.delete();
new File("state_state_matrix.1").delete();
new File("state_topics.1").delete();
new File("states.1").delete();
}

@Test
public void testMalformedTopicLineMissingField() throws IOException {
File state = new File("malformed_state.txt");
PrintWriter writer = new PrintWriter(state);
writer.println("0 0 1");
writer.close();
MultinomialHMM model = new MultinomialHMM(3, state.getAbsolutePath(), 2);
assertEquals(0, model.numDocs);
state.delete();
}

@Test
public void testNegativeTopicIndexIgnored() throws IOException {
File state = new File("negative_topic_state.txt");
PrintWriter writer = new PrintWriter(state);
writer.println("0 0 0 X -1");
writer.close();
try {
new MultinomialHMM(3, state.getAbsolutePath(), 2);
} catch (Exception e) {
fail("Negative topic index caused a crash");
}
state.delete();
}

@Test
public void testMultipleDocumentsWithRepeatedTopicCounts() throws IOException {
File state = new File("repeated_topic_state.txt");
PrintWriter writer = new PrintWriter(state);
writer.println("0 0 0 X 1");
writer.println("0 1 0 X 1");
writer.println("1 0 0 X 1");
writer.println("1 1 0 X 1");
writer.println("2 0 0 X 1");
writer.println("2 1 0 X 1");
writer.close();
File alpha = new File("topics_alpha_repeat.txt");
PrintWriter alphaWriter = new PrintWriter(alpha);
alphaWriter.println("0 1.0 topic0");
alphaWriter.println("1 1.0 topic1");
alphaWriter.println("2 1.0 topic2");
alphaWriter.close();
File seqFile = new File("repeat_seq.txt");
PrintWriter seqWriter = new PrintWriter(seqFile);
seqWriter.println("0\t0");
seqWriter.println("1\t0");
seqWriter.println("2\t0");
seqWriter.close();
MultinomialHMM model = new MultinomialHMM(3, state.getAbsolutePath(), 3);
model.setRandomSeed(999);
model.setGamma(1.0);
model.loadAlphaFromFile(alpha.getAbsolutePath());
model.loadSequenceIDsFromFile(seqFile.getAbsolutePath());
model.initialize();
model.sample();
assertEquals(3, model.documentStates.length);
state.delete();
alpha.delete();
seqFile.delete();
new File("state_state_matrix.1").delete();
new File("state_topics.1").delete();
new File("states.1").delete();
}

@Test
public void testShortSequenceSingleTransitionSegment() throws IOException {
File state = new File("transition_state.txt");
PrintWriter writer = new PrintWriter(state);
writer.println("0 0 0 X 0");
writer.println("1 0 0 X 1");
writer.close();
File alpha = new File("alpha_segment.txt");
PrintWriter alphaWriter = new PrintWriter(alpha);
alphaWriter.println("0 1.0 A");
alphaWriter.println("1 1.0 B");
alphaWriter.println("2 1.0 C");
alphaWriter.close();
File seqFile = new File("seq_segment.txt");
PrintWriter seqWriter = new PrintWriter(seqFile);
seqWriter.println("0\t1");
seqWriter.println("1\t1");
seqWriter.close();
MultinomialHMM model = new MultinomialHMM(3, state.getAbsolutePath(), 2);
model.setRandomSeed(999);
model.setGamma(1.5);
model.loadAlphaFromFile(alpha.getAbsolutePath());
model.loadSequenceIDsFromFile(seqFile.getAbsolutePath());
model.initialize();
model.sample();
assertEquals(2, model.documentStates.length);
state.delete();
alpha.delete();
seqFile.delete();
new File("state_state_matrix.1").delete();
new File("state_topics.1").delete();
new File("states.1").delete();
}

@Test
public void testReinitializeRestartsStateAssignments() throws IOException {
File stateFile = new File("restart_state.txt");
PrintWriter writer = new PrintWriter(stateFile);
writer.println("0 0 0 X 0");
writer.close();
File alpha = new File("restart_alpha.txt");
PrintWriter alphaWriter = new PrintWriter(alpha);
alphaWriter.println("0 1.0 z");
alphaWriter.println("1 1.0 y");
alphaWriter.println("2 1.0 x");
alphaWriter.close();
File seqFile = new File("restart_seq.txt");
PrintWriter seqWriter = new PrintWriter(seqFile);
seqWriter.println("0\t1");
seqWriter.close();
MultinomialHMM model = new MultinomialHMM(3, stateFile.getAbsolutePath(), 2);
model.setGamma(1.0);
model.setRandomSeed(101);
model.loadAlphaFromFile(alpha.getAbsolutePath());
model.loadSequenceIDsFromFile(seqFile.getAbsolutePath());
model.initialize();
int before = model.documentStates[0];
model.initialize();
int after = model.documentStates[0];
assertTrue(before >= 0);
assertTrue(after >= 0);
stateFile.delete();
alpha.delete();
seqFile.delete();
}

@Test
public void testZeroLengthDocumentDoesNotCrashSampling() throws IOException {
File stateFile = new File("zero_len_state.txt");
PrintWriter writer = new PrintWriter(stateFile);
writer.println("0 0 0 X 0");
writer.close();
File alphaFile = new File("zero_len_alpha.txt");
PrintWriter alphaWriter = new PrintWriter(alphaFile);
alphaWriter.println("0 1.0 zz");
alphaWriter.println("1 1.0 yy");
alphaWriter.println("2 1.0 xx");
alphaWriter.close();
File seqFile = new File("zero_len_seq.txt");
PrintWriter seqWriter = new PrintWriter(seqFile);
seqWriter.println("0\t3");
seqWriter.close();
MultinomialHMM model = new MultinomialHMM(3, stateFile.getAbsolutePath(), 2);
model.setGamma(0.1);
model.setRandomSeed(7);
model.loadAlphaFromFile(alphaFile.getAbsolutePath());
model.loadSequenceIDsFromFile(seqFile.getAbsolutePath());
model.initialize();
model.documentTopics.remove(0);
model.sample();
assertEquals(1, model.documentStates.length);
stateFile.delete();
alphaFile.delete();
seqFile.delete();
new File("state_state_matrix.1").delete();
new File("state_topics.1").delete();
new File("states.1").delete();
}

@Test
public void testAlphaSumZero_NoCrash() throws IOException {
File state = new File("test_state_zero_alpha.txt");
PrintWriter writer = new PrintWriter(state);
writer.println("0 0 0 X 0");
writer.close();
File alpha = new File("test_alpha_zero.txt");
PrintWriter alphaWriter = new PrintWriter(alpha);
alphaWriter.println("0 0.0 topic0");
alphaWriter.println("1 0.0 topic1");
alphaWriter.println("2 0.0 topic2");
alphaWriter.close();
File seqFile = new File("test_seq_zero_alpha.txt");
PrintWriter seqWriter = new PrintWriter(seqFile);
seqWriter.println("0\t0");
seqWriter.close();
MultinomialHMM model = new MultinomialHMM(3, state.getAbsolutePath(), 2);
model.setRandomSeed(42);
model.setGamma(1.0);
model.loadAlphaFromFile(alpha.getAbsolutePath());
model.loadSequenceIDsFromFile(seqFile.getAbsolutePath());
model.initialize();
model.sample();
assertTrue(model.alphaSum == 0.0);
state.delete();
alpha.delete();
seqFile.delete();
new File("state_state_matrix.1").delete();
new File("state_topics.1").delete();
new File("states.1").delete();
}

@Test
public void testMaxTokensPerTopicBoundarySetToZero() throws IOException {
File state = new File("test_state_zero_tokens.txt");
PrintWriter writer = new PrintWriter(state);
writer.close();
MultinomialHMM model = new MultinomialHMM(3, state.getAbsolutePath(), 2);
assertEquals(3, model.maxTokensPerTopic.length);
assertEquals(0, model.maxTokensPerTopic[0]);
assertEquals(0, model.maxTokensPerTopic[1]);
assertEquals(0, model.maxTokensPerTopic[2]);
state.delete();
}

@Test
public void testLoadAlphaWithExtraWhitespaceLinesIgnored() throws IOException {
File state = new File("test_state_alpha_ws.txt");
PrintWriter stWriter = new PrintWriter(state);
stWriter.println("0 0 0 X 0");
stWriter.println("1 0 0 X 1");
stWriter.close();
File alpha = new File("test_alpha_with_ws.txt");
PrintWriter alphaWriter = new PrintWriter(alpha);
alphaWriter.println("0 1.0 topic0");
alphaWriter.println("1 2.0 topic1");
alphaWriter.println("");
alphaWriter.println("   ");
alphaWriter.println("2 3.0 topic2");
alphaWriter.close();
MultinomialHMM model = new MultinomialHMM(3, state.getAbsolutePath(), 2);
model.loadAlphaFromFile(alpha.getAbsolutePath());
assertEquals(6.0, model.alphaSum, 0.0001);
assertEquals(2.0, model.alpha[1], 0.0001);
state.delete();
alpha.delete();
}

@Test
public void testTopicIndexCollisionInDocumentTopics() throws IOException {
File state = new File("collision_state.txt");
PrintWriter writer = new PrintWriter(state);
writer.println("0 0 0 X 1");
writer.println("0 1 0 X 1");
writer.close();
MultinomialHMM model = new MultinomialHMM(2, state.getAbsolutePath(), 2);
assertTrue(model.documentTopics.containsKey(0));
assertEquals(1, model.documentTopics.get(0).size());
assertEquals(2, model.documentTopics.get(0).get(1));
state.delete();
}

@Test
public void testMissingTopicFieldSkipsLine() throws IOException {
File state = new File("missing_topic_field.txt");
PrintWriter writer = new PrintWriter(state);
writer.println("0 0 0");
writer.println("1 0 0 X 1");
writer.close();
MultinomialHMM model = new MultinomialHMM(2, state.getAbsolutePath(), 1);
assertEquals(1, model.numDocs);
state.delete();
}

@Test
public void testLargeNumberOfDocsAndTopicsHighMemory() throws IOException {
File state = new File("large_state.txt");
PrintWriter writer = new PrintWriter(state);
writer.println("0 0 0 X 0");
writer.println("1 0 0 X 1");
writer.println("2 0 0 X 2");
writer.println("3 0 0 X 3");
writer.println("4 0 0 X 4");
writer.close();
int largeTopicCount = 1000;
MultinomialHMM model = new MultinomialHMM(largeTopicCount, state.getAbsolutePath(), 3);
assertEquals(5, model.numDocs);
assertEquals(largeTopicCount, model.alpha.length);
assertEquals(largeTopicCount, model.maxTokensPerTopic.length);
state.delete();
}

@Test
public void testDocumentWithMultipleIdenticalTopicAssignments() throws IOException {
File state = new File("duplicate_topics_state.txt");
PrintWriter writer = new PrintWriter(state);
writer.println("0 0 0 X 1");
writer.println("0 1 0 X 1");
writer.println("0 2 0 X 1");
writer.close();
MultinomialHMM model = new MultinomialHMM(3, state.getAbsolutePath(), 2);
assertEquals(1, model.documentTopics.size());
assertEquals(1, model.documentTopics.get(0).size());
assertEquals(3, model.documentTopics.get(0).get(1));
state.delete();
}

@Test
public void testDocumentWithNoValidTopicLineIsIgnored() throws IOException {
File state = new File("no_valid_topic.txt");
PrintWriter writer = new PrintWriter(state);
writer.println("Invalid line");
writer.println("Another bad line");
writer.close();
MultinomialHMM model = new MultinomialHMM(3, state.getAbsolutePath(), 2);
assertEquals(0, model.numDocs);
assertTrue(model.documentTopics.isEmpty());
state.delete();
}

@Test
public void testLogGammaCacheLengthAtLeastOne() throws IOException {
File state = new File("gamma_cache_len.txt");
PrintWriter writer = new PrintWriter(state);
writer.println("0 0 0 X 1");
writer.close();
MultinomialHMM model = new MultinomialHMM(2, state.getAbsolutePath(), 2);
int len = model.topicLogGammaCache[0][1].length;
assertTrue(len >= 1);
state.delete();
}

@Test
public void testExtremeGammaValueSetAndRecache() throws IOException {
File state = new File("extreme_gamma_state.txt");
PrintWriter w = new PrintWriter(state);
w.println("0 0 0 X 1");
w.close();
MultinomialHMM model = new MultinomialHMM(2, state.getAbsolutePath(), 2);
model.setGamma(Double.MAX_VALUE);
model.setRandomSeed(11);
File alphaFile = new File("extreme_gamma_alpha.txt");
PrintWriter aw = new PrintWriter(alphaFile);
aw.println("0 1.0 foo");
aw.println("1 1.0 bar");
aw.close();
File seqFile = new File("extreme_gamma_seq.txt");
PrintWriter sw = new PrintWriter(seqFile);
sw.println("0\t0");
sw.close();
model.loadAlphaFromFile(alphaFile.getAbsolutePath());
model.loadSequenceIDsFromFile(seqFile.getAbsolutePath());
model.initialize();
model.sample();
assertTrue(model.gamma > 1000000000);
state.delete();
alphaFile.delete();
seqFile.delete();
new File("state_state_matrix.1").delete();
new File("state_topics.1").delete();
new File("states.1").delete();
}

@Test
public void testAlphaFileContainsNegativeAlphaAcceptsAndSums() throws IOException {
File stateFile = new File("state_negative_alpha.txt");
PrintWriter w = new PrintWriter(stateFile);
w.println("0 0 0 X 1");
w.close();
File alphaFile = new File("negative_alpha.alpha");
PrintWriter aw = new PrintWriter(alphaFile);
aw.println("0 -1.0 foo");
aw.println("1 2.0 bar");
aw.println("2 1.0 baz");
aw.close();
MultinomialHMM model = new MultinomialHMM(3, stateFile.getAbsolutePath(), 2);
model.loadAlphaFromFile(alphaFile.getAbsolutePath());
assertEquals(-1.0, model.alpha[0], 0.0001);
assertEquals(2.0, model.alpha[1], 0.0001);
assertEquals(1.0, model.alpha[2], 0.0001);
assertEquals(2.0, model.alphaSum, 0.0001);
stateFile.delete();
alphaFile.delete();
}

@Test
public void testTopicLineWithNonIntegerFieldSkipsLine() throws IOException {
File stateFile = new File("non_integer_topic.test");
PrintWriter w = new PrintWriter(stateFile);
w.println("0 0 X X 1");
w.println("1 0 0 X 2");
w.close();
MultinomialHMM model = new MultinomialHMM(3, stateFile.getAbsolutePath(), 2);
assertEquals(2, model.numDocs);
assertEquals(1, model.documentTopics.size());
stateFile.delete();
}

@Test
public void testStateWithNoTopics_RemainsZeroInTopicCounts() throws IOException {
File state = new File("state_zero_topic_counts.txt");
PrintWriter writer = new PrintWriter(state);
writer.println("0 0 0 X 0");
writer.println("1 0 0 X 0");
writer.println("2 0 0 X 0");
writer.close();
File alphaFile = new File("zero_topic_alpha.txt");
PrintWriter alphaWriter = new PrintWriter(alphaFile);
alphaWriter.println("0 1.0 a");
alphaWriter.println("1 1.0 b");
alphaWriter.println("2 1.0 c");
alphaWriter.close();
File seq = new File("zero_topic_seq.txt");
PrintWriter sw = new PrintWriter(seq);
sw.println("0\t0");
sw.println("1\t1");
sw.println("2\t2");
sw.close();
MultinomialHMM model = new MultinomialHMM(3, state.getAbsolutePath(), 3);
model.setRandomSeed(99);
model.setGamma(0.5);
model.loadAlphaFromFile(alphaFile.getAbsolutePath());
model.loadSequenceIDsFromFile(seq.getAbsolutePath());
model.initialize();
for (int i = 0; i < 3; i++) {
for (int val : model.stateTopicCounts[i]) {
assertTrue(val >= 0);
}
}
state.delete();
alphaFile.delete();
seq.delete();
}

@Test
public void testSamplingTransitionsFromSameToSameState() throws IOException {
File state = new File("loop_transitions_state.txt");
PrintWriter w = new PrintWriter(state);
w.println("0 0 0 X 2");
w.println("1 0 0 X 1");
w.println("2 0 0 X 2");
w.close();
File alphaFile = new File("loop_transitions_alpha.txt");
PrintWriter aw = new PrintWriter(alphaFile);
aw.println("0 1.0 alpha0");
aw.println("1 1.0 alpha1");
aw.println("2 1.0 alpha2");
aw.close();
File seqFile = new File("loop_transitions_seq.txt");
PrintWriter sw = new PrintWriter(seqFile);
sw.println("0\t10");
sw.println("1\t10");
sw.println("2\t10");
sw.close();
MultinomialHMM model = new MultinomialHMM(3, state.getAbsolutePath(), 3);
model.setGamma(1.0);
model.setRandomSeed(123);
model.loadAlphaFromFile(alphaFile.getAbsolutePath());
model.loadSequenceIDsFromFile(seqFile.getAbsolutePath());
model.initialize();
model.sample();
assertEquals(3, model.documentStates.length);
state.delete();
alphaFile.delete();
seqFile.delete();
new File("state_state_matrix.1").delete();
new File("state_topics.1").delete();
new File("states.1").delete();
}

@Test
public void testEmptyDocumentTopicMapSkipsSamplingLogic() throws IOException {
File state = new File("empty_doc_topic_state.txt");
PrintWriter writer = new PrintWriter(state);
writer.println("3 0 0 X 1");
writer.close();
File alphaFile = new File("empty_doc_topic_alpha.txt");
PrintWriter alphaWriter = new PrintWriter(alphaFile);
alphaWriter.println("0 1.0 word0");
alphaWriter.println("1 1.0 word1");
alphaWriter.println("2 1.0 word2");
alphaWriter.close();
File seqFile = new File("empty_doc_seq.txt");
PrintWriter sw = new PrintWriter(seqFile);
sw.println("3\t8");
sw.close();
MultinomialHMM model = new MultinomialHMM(3, state.getAbsolutePath(), 2);
model.setRandomSeed(42);
model.setGamma(1.0);
model.loadAlphaFromFile(alphaFile.getAbsolutePath());
model.loadSequenceIDsFromFile(seqFile.getAbsolutePath());
model.documentTopics.remove(3);
model.initialize();
model.sample();
assertEquals(4, model.documentStates.length);
state.delete();
alphaFile.delete();
seqFile.delete();
new File("state_state_matrix.1").delete();
new File("state_topics.1").delete();
new File("states.1").delete();
}

@Test
public void testInvalidSequenceIdFormatCausesException() throws IOException {
File stateFile = new File("invalid_seq_state.txt");
PrintWriter writer = new PrintWriter(stateFile);
writer.println("0 0 0 X 0");
writer.close();
File alpha = new File("invalid_seq_alpha.txt");
PrintWriter aw = new PrintWriter(alpha);
aw.println("0 1.0 foo");
aw.println("1 1.0 bar");
aw.println("2 1.0 baz");
aw.close();
File seqFile = new File("invalid_seq.txt");
PrintWriter sw = new PrintWriter(seqFile);
sw.println("bad_line_with_no_tab");
sw.close();
MultinomialHMM model = new MultinomialHMM(3, stateFile.getAbsolutePath(), 2);
model.loadAlphaFromFile(alpha.getAbsolutePath());
boolean threw = false;
try {
model.loadSequenceIDsFromFile(seqFile.getAbsolutePath());
} catch (Exception e) {
threw = true;
}
assertTrue(threw);
stateFile.delete();
alpha.delete();
seqFile.delete();
}

@Test
public void testStateFileWithGapsInDocIdsCreatesSparseArray() throws IOException {
File stateFile = new File("sparse_docs_state.txt");
PrintWriter writer = new PrintWriter(stateFile);
writer.println("0 0 0 X 0");
writer.println("50 0 0 X 1");
writer.println("100 0 0 X 2");
writer.println("150 0 0 X 1");
writer.close();
MultinomialHMM model = new MultinomialHMM(3, stateFile.getAbsolutePath(), 2);
assertEquals(151, model.numDocs);
assertTrue(model.documentTopics.containsKey(0));
assertTrue(model.documentTopics.containsKey(50));
assertTrue(model.documentTopics.containsKey(100));
assertTrue(model.documentTopics.containsKey(150));
stateFile.delete();
}

@Test
public void testTopicIndexOutOfBoundsExceedsDeclaredTopicCountSilentlyDropped() throws IOException {
File stateFile = new File("bad_topic_index_state.txt");
PrintWriter writer = new PrintWriter(stateFile);
writer.println("0 0 0 X 999");
writer.close();
MultinomialHMM model = new MultinomialHMM(3, stateFile.getAbsolutePath(), 2);
assertEquals(1, model.numDocs);
assertTrue(model.maxTokensPerTopic.length == 3);
stateFile.delete();
}

@Test
public void testSequenceFileShorterThanTopicFileTriggersDocMismatchWarning() throws IOException {
File stateFile = new File("mismatch_length_state.txt");
PrintWriter writer = new PrintWriter(stateFile);
writer.println("0 0 0 X 0");
writer.println("1 0 0 X 1");
writer.close();
File seqFile = new File("short_seq.txt");
PrintWriter sw = new PrintWriter(seqFile);
sw.println("0\t0");
sw.close();
File alphaFile = new File("alpha_warn.txt");
PrintWriter aw = new PrintWriter(alphaFile);
aw.println("0 1.0 a");
aw.println("1 1.0 b");
aw.println("2 1.0 c");
aw.close();
MultinomialHMM model = new MultinomialHMM(3, stateFile.getAbsolutePath(), 2);
model.setRandomSeed(3);
model.setGamma(0.5);
model.loadAlphaFromFile(alphaFile.getAbsolutePath());
model.loadSequenceIDsFromFile(seqFile.getAbsolutePath());
assertEquals(2, model.numDocs);
assertEquals(1, model.documentSequenceIDs[0]);
stateFile.delete();
seqFile.delete();
alphaFile.delete();
}

@Test
public void testInputWithMultipleSpacesAndExtraTabsInSequenceFile() throws IOException {
File stateFile = new File("spacing_state.txt");
PrintWriter st = new PrintWriter(stateFile);
st.println("0 0 0 X 1");
st.close();
File seqFile = new File("extra_tab_seq.txt");
PrintWriter sw = new PrintWriter(seqFile);
sw.println("0\t\t0\t");
sw.close();
File alpha = new File("spacing_alpha.txt");
PrintWriter aw = new PrintWriter(alpha);
aw.println("0 1.0 t0");
aw.println("1 1.0 t1");
aw.println("2 1.0 t2");
aw.close();
MultinomialHMM model = new MultinomialHMM(3, stateFile.getAbsolutePath(), 2);
model.loadAlphaFromFile(alpha.getAbsolutePath());
try {
model.loadSequenceIDsFromFile(seqFile.getAbsolutePath());
assertEquals(1, model.documentSequenceIDs.length);
} catch (Exception e) {
fail("Failed to support spacing/tabs in sequence line.");
}
stateFile.delete();
seqFile.delete();
alpha.delete();
}

@Test
public void testGammaSumComputationAfterZeroGamma() throws IOException {
File state = new File("zero_gamma_state.txt");
PrintWriter writer = new PrintWriter(state);
writer.println("0 0 0 X 1");
writer.close();
MultinomialHMM model = new MultinomialHMM(3, state.getAbsolutePath(), 4);
model.setGamma(0.0);
model.setRandomSeed(1);
File alphaFile = new File("zero_gamma_alpha.txt");
PrintWriter alpha = new PrintWriter(alphaFile);
alpha.println("0 1.0 A");
alpha.println("1 1.0 B");
alpha.println("2 1.0 C");
alpha.close();
File seqFile = new File("zero_gamma_seq.txt");
PrintWriter seq = new PrintWriter(seqFile);
seq.println("0\t0");
seq.close();
model.loadAlphaFromFile(alphaFile.getAbsolutePath());
model.loadSequenceIDsFromFile(seqFile.getAbsolutePath());
model.initialize();
assertEquals(0.0, model.gammaSum, 0.0);
state.delete();
alphaFile.delete();
seqFile.delete();
}

@Test
public void testOutputContainsStateCountAndMatrixLabels() throws IOException {
File state = new File("print_matrix_test.txt");
PrintWriter writer = new PrintWriter(state);
writer.println("0 0 0 X 0");
writer.println("1 0 0 X 1");
writer.close();
File alpha = new File("print_matrix_alpha.txt");
PrintWriter printAlpha = new PrintWriter(alpha);
printAlpha.println("0 1.0 alphaA");
printAlpha.println("1 1.0 alphaB");
printAlpha.println("2 1.0 alphaC");
printAlpha.close();
File seq = new File("print_matrix_seq.txt");
PrintWriter sw = new PrintWriter(seq);
sw.println("0\t0");
sw.println("1\t0");
sw.close();
MultinomialHMM model = new MultinomialHMM(3, state.getAbsolutePath(), 2);
model.setGamma(1.0);
model.setRandomSeed(11);
model.loadAlphaFromFile(alpha.getAbsolutePath());
model.loadSequenceIDsFromFile(seq.getAbsolutePath());
model.initialize();
String output = model.printStateTransitions();
assertTrue(output.contains("["));
assertTrue(output.contains("\t"));
assertTrue(output.contains("\n"));
state.delete();
alpha.delete();
seq.delete();
}

@Test
public void testLoadAlphaWithEmptyLinesBetweenValidData() throws IOException {
File alphaFile = new File("alpha_with_gaps.alpha");
PrintWriter w = new PrintWriter(alphaFile);
w.println("0 1.0 A");
w.println("");
w.println("1 1.0 B");
w.println(" ");
w.println("2 1.0 C");
w.close();
File stateFile = new File("alpha_gaps_state.txt");
PrintWriter s = new PrintWriter(stateFile);
s.println("0 0 0 X 0");
s.close();
MultinomialHMM model = new MultinomialHMM(3, stateFile.getAbsolutePath(), 2);
model.loadAlphaFromFile(alphaFile.getAbsolutePath());
assertEquals(3.0, model.alphaSum, 0.0001);
alphaFile.delete();
stateFile.delete();
}
}
