package opennlp.tools.formats;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import opennlp.tools.ml.BeamSearch;
import opennlp.tools.ml.EventTrainer;
import opennlp.tools.ml.SequenceTrainer;
import opennlp.tools.ml.TrainerFactory;
import opennlp.tools.ml.model.Event;
import opennlp.tools.ml.model.MaxentModel;
import opennlp.tools.ml.model.SequenceClassificationModel;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.util.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
// import opennlp.tools.formats.ResourceAsStreamFactory;
import opennlp.tools.namefind.NameFinderME;
import static opennlp.tools.formats.Conll02NameSampleStream.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class Conll03NameSampleStream_llmsuite_3_GPTLLMTest {

@Test
public void testReadEnglishSampleWithPersonEntity() throws IOException {
String data = DOCSTART + " -X- -X- O\n\n" + "John NNP B-NP B-PER\n" + "Doe NNP I-NP I-PER\n\n";
InputStreamFactory inFactory = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, inFactory, GENERATE_PERSON_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
assertEquals(2, sample.getSentence().length);
assertEquals("John", sample.getSentence()[0]);
assertEquals("Doe", sample.getSentence()[1]);
Span[] spans = sample.getNames();
assertEquals(1, spans.length);
assertEquals(new Span(0, 2, "PER"), spans[0]);
assertTrue(sample.isClearAdaptiveDataSet());
stream.close();
}

@Test
public void testReadEmptyInputReturnsNull() throws IOException {
InputStreamFactory inFactory = () -> new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8));
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, inFactory, GENERATE_PERSON_ENTITIES);
NameSample sample = stream.read();
assertNull(sample);
stream.close();
}

@Test
public void testInvalidFieldCountThrowsIOException() throws IOException {
String invalid = "John NNP";
InputStreamFactory inFactory = () -> new ByteArrayInputStream(invalid.getBytes(StandardCharsets.UTF_8));
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, inFactory, GENERATE_PERSON_ENTITIES);
try {
stream.read();
fail("Expected IOException due to incorrect number of fields.");
} catch (IOException e) {
assertTrue(e.getMessage().contains("Incorrect number of fields"));
}
stream.close();
}

@Test
public void testInvalidTagThrowsIOException() throws IOException {
String invalid = "John NNP B-NP Z-XXX\n";
InputStreamFactory inFactory = () -> new ByteArrayInputStream(invalid.getBytes(StandardCharsets.UTF_8));
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, inFactory, GENERATE_PERSON_ENTITIES);
try {
stream.read();
fail("Expected IOException due to invalid tag.");
} catch (IOException e) {
assertTrue(e.getMessage().contains("Invalid tag"));
}
stream.close();
}

@Test
public void testMultipleEntityTypes() throws IOException {
String data = "Alice NNP B-NP B-PER\n" + "Corp NNP B-NP B-ORG\n" + "London NNP B-NP B-LOC\n" + "Euro NNP B-NP B-MISC\n\n";
int allTypes = GENERATE_PERSON_ENTITIES | GENERATE_ORGANIZATION_ENTITIES | GENERATE_LOCATION_ENTITIES | GENERATE_MISC_ENTITIES;
InputStreamFactory inFactory = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, inFactory, allTypes);
NameSample sample = stream.read();
assertNotNull(sample);
assertEquals(4, sample.getSentence().length);
Span[] spans = sample.getNames();
assertEquals(4, spans.length);
assertEquals(new Span(0, 1, "PER"), spans[0]);
assertEquals(new Span(1, 2, "ORG"), spans[1]);
assertEquals(new Span(2, 3, "LOC"), spans[2]);
assertEquals(new Span(3, 4, "MISC"), spans[3]);
stream.close();
}

@Test
public void testEntityTypeFilterRemovesUnwantedEntities() throws IOException {
String data = "Bob NNP B-NP B-PER\n" + "UN NNP B-NP B-ORG\n" + "Paris NNP B-NP B-LOC\n\n";
int onlyPerson = GENERATE_PERSON_ENTITIES;
InputStreamFactory inFactory = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, inFactory, onlyPerson);
NameSample sample = stream.read();
assertNotNull(sample);
Span[] spans = sample.getNames();
assertEquals(1, spans.length);
assertEquals("PER", spans[0].getType());
assertEquals(0, spans[0].getStart());
assertEquals(1, spans[0].getEnd());
stream.close();
}

@Test
public void testSentenceWithNoEntities() throws IOException {
String data = "The DT B-NP O\n" + "weather NN I-NP O\n\n";
InputStreamFactory inFactory = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, inFactory, GENERATE_PERSON_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
assertEquals(2, sample.getSentence().length);
assertEquals(0, sample.getNames().length);
stream.close();
}

@Test
public void testTrailingBeginSpanCreatesSpan() throws IOException {
String data = "Emma NNP B-NP B-PER\n" + "Watson NNP I-NP I-PER\n";
InputStreamFactory inFactory = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, inFactory, GENERATE_PERSON_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
Span[] spans = sample.getNames();
assertEquals(1, spans.length);
assertEquals(new Span(0, 2, "PER"), spans[0]);
stream.close();
}

@Test
public void testGermanInput() throws IOException {
String data = "Angela x NNP B-NP B-PER\n" + "Merkel x NNP I-NP I-PER\n\n";
InputStreamFactory inFactory = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.DE, inFactory, GENERATE_PERSON_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
assertEquals(2, sample.getSentence().length);
Span[] spans = sample.getNames();
assertEquals(1, spans.length);
assertEquals(new Span(0, 2, "PER"), spans[0]);
stream.close();
}

@Test
public void testDocStartWithoutBlankLineThrowsIOException() throws IOException {
String data = DOCSTART + " -X- -X- O\nNotBlank\n";
InputStreamFactory inFactory = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, inFactory, GENERATE_PERSON_ENTITIES);
try {
stream.read();
fail("Expected IOException due to missing blank line after DOCSTART");
} catch (IOException e) {
assertTrue(e.getMessage().contains("Empty line after -DOCSTART- not empty"));
}
stream.close();
}

@Test
public void testResetSupport() throws IOException {
String sampleLine = "Tom NNP B-NP B-PER\n\n";
byte[] data = sampleLine.getBytes(StandardCharsets.UTF_8);
InputStreamFactory inFactory = () -> new ByteArrayInputStream(data);
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, inFactory, GENERATE_PERSON_ENTITIES);
NameSample sample1 = stream.read();
assertNotNull(sample1);
stream.reset();
NameSample sample2 = stream.read();
assertNotNull(sample2);
assertEquals(sample1.getSentence()[0], sample2.getSentence()[0]);
stream.close();
}

@Test
public void testUnresettableStreamThrowsOnReset() throws IOException {
ObjectStream<String> unresettable = new ObjectStream<String>() {

public String read() {
return null;
}

public void reset() {
throw new UnsupportedOperationException();
}

public void close() {
}
};
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, unresettable, GENERATE_PERSON_ENTITIES);
try {
stream.reset();
fail("Expected UnsupportedOperationException");
} catch (UnsupportedOperationException e) {
}
stream.close();
}

@Test
public void testEmptyLineBetweenSentences() throws IOException {
String content = "Alice NNP B-NP B-PER\n" + "\n" + "\n" + "IBM NNP B-NP B-ORG\n\n";
InputStreamFactory inFactory = () -> new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
int types = GENERATE_PERSON_ENTITIES | GENERATE_ORGANIZATION_ENTITIES;
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, inFactory, types);
NameSample ns1 = stream.read();
assertNotNull(ns1);
assertEquals("Alice", ns1.getSentence()[0]);
assertEquals("PER", ns1.getNames()[0].getType());
NameSample ns2 = stream.read();
assertNotNull(ns2);
assertEquals("IBM", ns2.getSentence()[0]);
assertEquals("ORG", ns2.getNames()[0].getType());
NameSample ns3 = stream.read();
assertNull(ns3);
stream.close();
}

@Test
public void testIWithoutPrecedingBCreatesSpan() throws IOException {
String content = "Obama NNP B-NP I-PER\n\n";
InputStreamFactory inFactory = () -> new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
int types = GENERATE_PERSON_ENTITIES;
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, inFactory, types);
NameSample ns = stream.read();
assertNotNull(ns);
assertEquals(1, ns.getNames().length);
assertEquals(new Span(0, 1, "PER"), ns.getNames()[0]);
stream.close();
}

@Test
public void testConsecutiveBEntitiesOfSameType() throws IOException {
String content = "Jim NNP B-NP B-PER\n" + "Bezos NNP I-NP I-PER\n" + "Elon NNP B-NP B-PER\n" + "Musk NNP I-NP I-PER\n\n";
InputStreamFactory inFactory = () -> new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
int types = GENERATE_PERSON_ENTITIES;
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, inFactory, types);
NameSample ns = stream.read();
assertNotNull(ns);
assertEquals(2, ns.getNames().length);
assertEquals(new Span(0, 2, "PER"), ns.getNames()[0]);
assertEquals(new Span(2, 4, "PER"), ns.getNames()[1]);
stream.close();
}

@Test
public void testTagSwitchBetweenDifferentTypes() throws IOException {
String content = "UN NNP B-NP B-ORG\n" + "Germany NNP I-NP I-LOC\n" + "Red NNP B-NP B-MISC\n" + "Cross NNP I-NP I-MISC\n\n";
InputStreamFactory inFactory = () -> new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
int types = GENERATE_ORGANIZATION_ENTITIES | GENERATE_LOCATION_ENTITIES | GENERATE_MISC_ENTITIES;
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, inFactory, types);
NameSample ns = stream.read();
assertNotNull(ns);
assertEquals(3, ns.getNames().length);
assertEquals(new Span(0, 1, "ORG"), ns.getNames()[0]);
assertEquals(new Span(1, 2, "LOC"), ns.getNames()[1]);
assertEquals(new Span(2, 4, "MISC"), ns.getNames()[2]);
stream.close();
}

@Test
public void testNoEntitiesAndOnlyOtags() throws IOException {
String content = "random NN B-NP O\n" + "words VBZ I-NP O\n\n";
InputStreamFactory inFactory = () -> new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
int types = GENERATE_PERSON_ENTITIES | GENERATE_ORGANIZATION_ENTITIES;
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, inFactory, types);
NameSample ns = stream.read();
assertNotNull(ns);
assertEquals(2, ns.getSentence().length);
assertEquals(0, ns.getNames().length);
stream.close();
}

@Test
public void testDifferentIEntityTypeBreaksSpan() throws IOException {
String content = "Google NNP B-NP B-ORG\n" + "Seattle NNP I-NP I-LOC\n" + "Alphabet NNP B-NP B-ORG\n\n";
InputStreamFactory inFactory = () -> new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
int types = GENERATE_ORGANIZATION_ENTITIES | GENERATE_LOCATION_ENTITIES;
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, inFactory, types);
NameSample ns = stream.read();
assertNotNull(ns);
assertEquals(3, ns.getSentence().length);
assertEquals(3, ns.getNames().length);
assertEquals("ORG", ns.getNames()[0].getType());
assertEquals("LOC", ns.getNames()[1].getType());
assertEquals("ORG", ns.getNames()[2].getType());
stream.close();
}

@Test
public void testSingleWordEntitySpan() throws IOException {
String content = "Amazon NNP B-NP B-ORG\n\n";
InputStreamFactory inFactory = () -> new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
int types = GENERATE_ORGANIZATION_ENTITIES;
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, inFactory, types);
NameSample ns = stream.read();
assertNotNull(ns);
assertEquals(1, ns.getNames().length);
assertEquals(new Span(0, 1, "ORG"), ns.getNames()[0]);
stream.close();
}

@Test
public void testLongEntitySpanWithCorrectIConsistency() throws IOException {
String content = "International NNP B-NP B-ORG\n" + "Criminal NNP I-NP I-ORG\n" + "Court NNP I-NP I-ORG\n\n";
InputStreamFactory inFactory = () -> new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
int types = GENERATE_ORGANIZATION_ENTITIES;
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, inFactory, types);
NameSample ns = stream.read();
assertNotNull(ns);
assertEquals(1, ns.getNames().length);
assertEquals(new Span(0, 3, "ORG"), ns.getNames()[0]);
stream.close();
}

@Test
public void testInconsistentITypeClosesPreviousSpan() throws IOException {
String content = "UN NNP B-NP B-ORG\n" + "Paris NNP I-NP I-LOC\n\n";
InputStreamFactory inFactory = () -> new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
int types = GENERATE_ORGANIZATION_ENTITIES | GENERATE_LOCATION_ENTITIES;
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, inFactory, types);
NameSample ns = stream.read();
assertNotNull(ns);
assertEquals(2, ns.getNames().length);
assertEquals("ORG", ns.getNames()[0].getType());
assertEquals("LOC", ns.getNames()[1].getType());
stream.close();
}

@Test
public void testInvalidLanguageFieldCountThrowsIOException() throws IOException {
String content = "Wort1 Wort2 Wort3\n";
InputStreamFactory inFactory = () -> new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, inFactory, GENERATE_PERSON_ENTITIES);
try {
stream.read();
fail("Expected IOException due to invalid field count for EN.");
} catch (IOException e) {
assertTrue(e.getMessage().contains("Incorrect number of fields"));
}
stream.close();
}

@Test
public void testBlankAfterEntityIgnoresExtraEmptyLine() throws IOException {
String content = "Tom NNP B-NP B-PER\n\n\n";
InputStreamFactory inFactory = () -> new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
int types = GENERATE_PERSON_ENTITIES;
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, inFactory, types);
NameSample sample1 = stream.read();
assertNotNull(sample1);
assertEquals(1, sample1.getNames().length);
assertEquals("PER", sample1.getNames()[0].getType());
NameSample sample2 = stream.read();
assertNull(sample2);
stream.close();
}

@Test
public void testTwoEntitiesWithInterleavedO() throws IOException {
String content = "Paris NNP B-NP B-LOC\n" + "is VBZ B-VP O\n" + "beautiful JJ B-ADJ O\n" + "France NNP B-NP B-LOC\n\n";
InputStreamFactory inFactory = () -> new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
int types = GENERATE_LOCATION_ENTITIES;
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, inFactory, types);
NameSample sample = stream.read();
assertNotNull(sample);
assertEquals(2, sample.getNames().length);
assertEquals(new Span(0, 1, "LOC"), sample.getNames()[0]);
assertEquals(new Span(3, 4, "LOC"), sample.getNames()[1]);
stream.close();
}

@Test
public void testMultipleEmptySentencesFilteredOut() throws IOException {
String content = "\n\n\n\nJohn NNP B-NP B-PER\n\n";
InputStreamFactory inFactory = () -> new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
int types = GENERATE_PERSON_ENTITIES;
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, inFactory, types);
NameSample sample = stream.read();
assertNotNull(sample);
assertEquals(1, sample.getNames().length);
assertEquals("John", sample.getSentence()[0]);
NameSample second = stream.read();
assertNull(second);
stream.close();
}

@Test
public void testSentenceWithOnlyInvalidTagThrows() throws IOException {
String content = "Person NNP B-NP Q-LOC\n\n";
InputStreamFactory inFactory = () -> new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
int types = GENERATE_LOCATION_ENTITIES;
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, inFactory, types);
try {
stream.read();
fail("Expected IOException for invalid tag.");
} catch (IOException e) {
assertTrue(e.getMessage().contains("Invalid tag"));
}
stream.close();
}

@Test
public void testEntityFollowedImmediatelyByDifferentEntityClosesPrevious() throws IOException {
String content = "Red NNP B-NP B-MISC\n" + "Cross NNP I-NP I-MISC\n" + "Berlin NNP B-NP B-LOC\n\n";
InputStreamFactory inFactory = () -> new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
int types = GENERATE_MISC_ENTITIES | GENERATE_LOCATION_ENTITIES;
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, inFactory, types);
NameSample sample = stream.read();
assertNotNull(sample);
assertEquals(2, sample.getNames().length);
assertEquals("MISC", sample.getNames()[0].getType());
assertEquals("LOC", sample.getNames()[1].getType());
stream.close();
}

@Test
public void testIWithDifferentTypeTriggersSpanSplit() throws IOException {
String content = "UN NNP B-NP B-ORG\n" + "Headquarters NNP I-NP I-LOC\n\n";
InputStreamFactory inFactory = () -> new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
int types = GENERATE_ORGANIZATION_ENTITIES | GENERATE_LOCATION_ENTITIES;
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, inFactory, types);
NameSample sample = stream.read();
assertNotNull(sample);
assertEquals(2, sample.getNames().length);
assertEquals("ORG", sample.getNames()[0].getType());
assertEquals("LOC", sample.getNames()[1].getType());
stream.close();
}

@Test
public void testSentenceThatEndsWithO() throws IOException {
String content = "Bob NNP B-NP B-PER\n" + "and CC B-CONJ O\n" + "team NNP I-NP O\n\n";
InputStreamFactory inFactory = () -> new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
int types = GENERATE_PERSON_ENTITIES;
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, inFactory, types);
NameSample sample = stream.read();
assertNotNull(sample);
assertEquals(1, sample.getNames().length);
assertEquals(new Span(0, 1, "PER"), sample.getNames()[0]);
stream.close();
}

@Test
public void testSentenceWithSingleIWithoutTypeMatch() throws IOException {
String content = "Foo NNP B-NP I-MISC\n\n";
InputStreamFactory inFactory = () -> new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
int types = GENERATE_MISC_ENTITIES;
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, inFactory, types);
NameSample sample = stream.read();
assertNotNull(sample);
assertEquals(1, sample.getNames().length);
assertEquals(new Span(0, 1, "MISC"), sample.getNames()[0]);
stream.close();
}

@Test
public void testShortLineAfterDocStartThrows() throws IOException {
String content = "-DOCSTART- -X- -X- O\nSomeText\n";
InputStreamFactory inFactory = () -> new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
int types = GENERATE_PERSON_ENTITIES;
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, inFactory, types);
try {
stream.read();
fail("Expected IOException due to missing blank line after DOCSTART");
} catch (IOException e) {
assertTrue(e.getMessage().contains("Empty line after -DOCSTART- not empty"));
}
stream.close();
}

@Test
public void testGermanLineWithIncorrectFieldCountThrows() throws IOException {
String content = "Merkel x NNP B-NP\n";
InputStreamFactory inFactory = () -> new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.DE, inFactory, GENERATE_PERSON_ENTITIES);
try {
stream.read();
fail("Expected IOException due to invalid field count for DE");
} catch (IOException e) {
assertTrue(e.getMessage().contains("Incorrect number of fields"));
}
stream.close();
}

@Test
public void testEmptySentenceFollowedByValidSentence() throws IOException {
String content = "\n\n" + "Apple NNP B-NP B-ORG\n" + "Inc. NNP I-NP I-ORG\n\n";
InputStreamFactory inFactory = () -> new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
int types = GENERATE_ORGANIZATION_ENTITIES;
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, inFactory, types);
NameSample sample1 = stream.read();
assertNotNull(sample1);
assertEquals(1, sample1.getNames().length);
assertEquals("ORG", sample1.getNames()[0].getType());
assertEquals(0, sample1.getNames()[0].getStart());
assertEquals(2, sample1.getNames()[0].getEnd());
NameSample sample2 = stream.read();
assertNull(sample2);
stream.close();
}

@Test
public void testMultipleIEntitiesWithoutStartCreatesMultipleSpans() throws IOException {
String content = "Google NNP B-NP I-ORG\n" + "Berlin NNP I-NP I-LOC\n" + "Misc NNP I-NP I-MISC\n\n";
InputStreamFactory inFactory = () -> new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
int types = GENERATE_ORGANIZATION_ENTITIES | GENERATE_LOCATION_ENTITIES | GENERATE_MISC_ENTITIES;
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, inFactory, types);
NameSample sample = stream.read();
assertNotNull(sample);
assertEquals(3, sample.getNames().length);
assertEquals("ORG", sample.getNames()[0].getType());
assertEquals("LOC", sample.getNames()[1].getType());
assertEquals("MISC", sample.getNames()[2].getType());
stream.close();
}

@Test
public void testOnlyDocstartReturnsNullAfterRead() throws IOException {
String content = "-DOCSTART- -X- -X- O\n\n";
InputStreamFactory inFactory = () -> new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
int types = GENERATE_PERSON_ENTITIES;
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, inFactory, types);
NameSample sample = stream.read();
assertNull(sample);
stream.close();
}

@Test
public void testEntityNotInTypesIsFilteredToO() throws IOException {
String content = "Berlin NNP B-NP B-LOC\n" + "EU NNP B-NP B-ORG\n" + "Festival NNP B-NP B-MISC\n" + "Tom NNP B-NP B-PER\n\n";
InputStreamFactory inFactory = () -> new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
int types = GENERATE_PERSON_ENTITIES;
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, inFactory, types);
NameSample sample = stream.read();
assertNotNull(sample);
assertEquals(1, sample.getNames().length);
assertEquals("PER", sample.getNames()[0].getType());
assertEquals(3, sample.getNames()[0].getStart());
stream.close();
}

@Test
public void testResetFunctionalityPreservesData() throws IOException {
String content = "Alice NNP B-NP B-PER\n\n";
byte[] data = content.getBytes(StandardCharsets.UTF_8);
InputStreamFactory inFactory = () -> new ByteArrayInputStream(data);
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, inFactory, GENERATE_PERSON_ENTITIES);
NameSample sample1 = stream.read();
assertNotNull(sample1);
assertEquals(1, sample1.getNames().length);
stream.reset();
NameSample sample2 = stream.read();
assertNotNull(sample2);
assertEquals("Alice", sample2.getSentence()[0]);
stream.close();
}

@Test
public void testSentenceWithoutEntityButCorrectFields() throws IOException {
String content = "hello UH B-NP O\n" + "world NN I-NP O\n\n";
InputStreamFactory inFactory = () -> new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
int types = GENERATE_PERSON_ENTITIES | GENERATE_ORGANIZATION_ENTITIES;
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, inFactory, types);
NameSample sample = stream.read();
assertNotNull(sample);
assertEquals(2, sample.getSentence().length);
assertEquals(0, sample.getNames().length);
stream.close();
}

@Test
public void testTagMismatchAfterSpanCausesSplit() throws IOException {
String content = "Alpha NNP B-NP B-ORG\n" + "Beta NNP I-NP I-LOC\n\n";
InputStreamFactory inFactory = () -> new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
int types = GENERATE_LOCATION_ENTITIES | GENERATE_ORGANIZATION_ENTITIES;
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, inFactory, types);
NameSample sample = stream.read();
assertNotNull(sample);
assertEquals(2, sample.getNames().length);
assertEquals("ORG", sample.getNames()[0].getType());
assertEquals("LOC", sample.getNames()[1].getType());
stream.close();
}

@Test
public void testMixedValidAndInvalidTagsWithRecovery() throws IOException {
String content = "John NNP B-NP B-PER\n" + "This NNP I-NP INVALID_TAG\n" + "Mary NNP B-NP B-PER\n\n";
InputStreamFactory inFactory = () -> new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, inFactory, GENERATE_PERSON_ENTITIES);
try {
stream.read();
fail("Expected IOException due to INVALID_TAG");
} catch (IOException e) {
assertTrue(e.getMessage().contains("Invalid tag"));
}
stream.close();
}

@Test
public void testSentenceWithAllEntitiesButAllFilteredByType() throws IOException {
String content = "Jack NNP B-NP B-PER\n" + "IBM NNP B-NP B-ORG\n" + "Paris NNP B-NP B-LOC\n\n";
InputStreamFactory inFactory = () -> new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
int types = 0;
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, inFactory, types);
NameSample sample = stream.read();
assertNotNull(sample);
assertEquals(3, sample.getSentence().length);
assertEquals(0, sample.getNames().length);
stream.close();
}

@Test
public void testMultipleSpansWithSharedLabel() throws IOException {
String content = "Barack NNP B-NP B-PER\n" + "Obama NNP I-NP I-PER\n" + "Joe NNP B-NP B-PER\n" + "Biden NNP I-NP I-PER\n\n";
InputStreamFactory inFactory = () -> new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
int types = GENERATE_PERSON_ENTITIES;
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, inFactory, types);
NameSample sample = stream.read();
assertNotNull(sample);
assertEquals(2, sample.getNames().length);
assertEquals("PER", sample.getNames()[0].getType());
assertEquals("PER", sample.getNames()[1].getType());
stream.close();
}

@Test
public void testMultipleConsecutiveOtagsEndsSpanCorrectly() throws IOException {
String content = "John NNP B-NP B-PER\n" + "Smith NNP I-NP I-PER\n" + "is VBZ B-VP O\n" + "a DT B-NP O\n" + "CEO NNP I-NP O\n\n";
InputStreamFactory input = () -> new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
int types = GENERATE_PERSON_ENTITIES;
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, input, types);
NameSample sample = stream.read();
assertNotNull(sample);
Span[] spans = sample.getNames();
assertEquals(1, spans.length);
assertEquals(new Span(0, 2, "PER"), spans[0]);
stream.close();
}

@Test
public void testSentenceWithOnlyOtagsNoEntityCreation() throws IOException {
String content = "Hello UH B-NP O\n" + "my PRP B-NP O\n" + "friend NN I-NP O\n\n";
InputStreamFactory input = () -> new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
int types = GENERATE_PERSON_ENTITIES | GENERATE_ORGANIZATION_ENTITIES;
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, input, types);
NameSample sample = stream.read();
assertNotNull(sample);
assertEquals(3, sample.getSentence().length);
assertEquals(0, sample.getNames().length);
stream.close();
}

@Test
public void testResetOnFreshObjectStreamProducesSameSample() throws IOException {
String content = "Anna NNP B-NP B-PER\n\n";
byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
InputStreamFactory inputFactory = () -> new ByteArrayInputStream(bytes);
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, inputFactory, GENERATE_PERSON_ENTITIES);
NameSample first = stream.read();
stream.reset();
NameSample second = stream.read();
assertNotNull(first);
assertNotNull(second);
assertEquals(first.getSentence()[0], second.getSentence()[0]);
assertEquals(first.getNames()[0], second.getNames()[0]);
stream.close();
}

@Test
public void testLastSpanIsClosedEvenWithoutOTag() throws IOException {
String content = "Obama NNP B-NP B-PER\n" + "Barack NNP I-NP I-PER\n";
InputStreamFactory input = () -> new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
int types = GENERATE_PERSON_ENTITIES;
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, input, types);
NameSample sample = stream.read();
assertNotNull(sample);
Span[] spans = sample.getNames();
assertEquals(1, spans.length);
assertEquals(new Span(0, 2, "PER"), spans[0]);
stream.close();
}

@Test
public void testInvalidTagFormatWithoutPrefixThrows() throws IOException {
String content = "Paris NNP B-NP LOC\n\n";
InputStreamFactory input = () -> new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
int types = GENERATE_LOCATION_ENTITIES;
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, input, types);
try {
stream.read();
fail("Expected IOException for invalid tag format");
} catch (IOException e) {
assertTrue(e.getMessage().contains("Invalid tag"));
}
stream.close();
}

@Test
public void testEmptyInputReturnsNull() throws IOException {
InputStreamFactory input = () -> new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8));
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, input, GENERATE_PERSON_ENTITIES);
NameSample sample = stream.read();
assertNull(sample);
stream.close();
}

@Test
public void testSentenceDetectedAfterMultipleEmptyLines() throws IOException {
String content = "\n\n\nJohn NNP B-NP B-PER\n\n";
InputStreamFactory input = () -> new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
int types = GENERATE_PERSON_ENTITIES;
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, input, types);
NameSample sample = stream.read();
assertNotNull(sample);
assertEquals("John", sample.getSentence()[0]);
assertEquals("PER", sample.getNames()[0].getType());
stream.close();
}

@Test
public void testDOCSTARTIsNotTreatedAsData() throws IOException {
String content = "-DOCSTART- -X- -X- O\n\n" + "Jane NNP B-NP B-PER\n\n";
InputStreamFactory input = () -> new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
int types = GENERATE_PERSON_ENTITIES;
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, input, types);
NameSample sample = stream.read();
assertNotNull(sample);
assertTrue(sample.isClearAdaptiveDataSet());
assertEquals("Jane", sample.getSentence()[0]);
assertEquals(1, sample.getNames().length);
assertEquals("PER", sample.getNames()[0].getType());
stream.close();
}

@Test
public void testSentenceWithOnlyOneLineBEntityCreatesSingleTokenSpan() throws IOException {
String content = "London NNP B-NP B-LOC\n\n";
InputStreamFactory input = () -> new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
int types = GENERATE_LOCATION_ENTITIES;
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, input, types);
NameSample sample = stream.read();
assertNotNull(sample);
assertEquals(1, sample.getSentence().length);
assertEquals(new Span(0, 1, "LOC"), sample.getNames()[0]);
stream.close();
}

@Test
public void testIEntityDifferentFromBClosesPrevious() throws IOException {
String content = "UN NNP B-NP B-ORG\n" + "Hague NNP I-NP I-LOC\n" + "Palace NNP I-NP I-LOC\n\n";
InputStreamFactory input = () -> new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
int types = GENERATE_ORGANIZATION_ENTITIES | GENERATE_LOCATION_ENTITIES;
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, input, types);
NameSample sample = stream.read();
assertNotNull(sample);
Span[] spans = sample.getNames();
assertEquals(2, spans.length);
assertEquals("ORG", spans[0].getType());
assertEquals("LOC", spans[1].getType());
stream.close();
}

@Test
public void testDocstartFollowedByEOFThrowsIOException() throws IOException {
String content = "-DOCSTART- -X- -X- O";
InputStreamFactory input = () -> new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, input, GENERATE_PERSON_ENTITIES);
try {
stream.read();
fail("Expected IOException due to EOF right after DOCSTART");
} catch (IOException e) {
assertTrue(e.getMessage().contains("Empty line after -DOCSTART- not empty"));
}
stream.close();
}

@Test
public void testMixedCaseTagLabelIsStillRecognized() throws IOException {
String content = "Barack NNP B-NP b-PER\n";
InputStreamFactory input = () -> new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, input, GENERATE_PERSON_ENTITIES);
try {
stream.read();
fail("Expected IOException due to invalid tag format (mixed case)");
} catch (IOException e) {
assertTrue(e.getMessage().contains("Invalid tag"));
}
stream.close();
}

@Test
public void testUnrecognizedTagPrefixThrowsIOException() throws IOException {
String content = "United NNP B-NP Z-ORG\n";
InputStreamFactory input = () -> new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, input, GENERATE_ORGANIZATION_ENTITIES);
try {
stream.read();
fail("Expected IOException due to unrecognized tag prefix");
} catch (IOException e) {
assertTrue(e.getMessage().contains("Invalid tag"));
}
stream.close();
}

@Test
public void testGermanInputCorrectFieldCountCreatesEntity() throws IOException {
String content = "Angela lemma TAG NP B-PER\n" + "Merkel lemma TAG NP I-PER\n\n";
InputStreamFactory inputFactory = () -> new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.DE, inputFactory, GENERATE_PERSON_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
assertEquals(2, sample.getSentence().length);
assertEquals("Angela", sample.getSentence()[0]);
assertEquals("Merkel", sample.getSentence()[1]);
assertEquals(1, sample.getNames().length);
assertEquals(new Span(0, 2, "PER"), sample.getNames()[0]);
stream.close();
}

@Test
public void testSwitchingLanguagesRequiresDifferentFieldCounts() throws IOException {
String content = "Harry NNP B-NP B-PER\n" + "Potter lemma POS NP B-PER\n\n";
InputStreamFactory input = () -> new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
Conll03NameSampleStream streamEN = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, input, GENERATE_PERSON_ENTITIES);
try {
streamEN.read();
streamEN.read();
fail("Expected IOException due to invalid number of fields for EN");
} catch (IOException e) {
assertTrue(e.getMessage().contains("Incorrect number of fields"));
}
streamEN.close();
}

@Test
public void testSentenceWithOnlyIPrefixAndNoBStartsNewSpan() throws IOException {
String content = "Nike NNP B-NP I-ORG\n\n";
InputStreamFactory input = () -> new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, input, GENERATE_ORGANIZATION_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
assertEquals(1, sample.getNames().length);
assertEquals(new Span(0, 1, "ORG"), sample.getNames()[0]);
stream.close();
}

@Test
public void testMultipleEntitiesSameTypeWithoutOInBetween() throws IOException {
String content = "Bill NNP B-NP B-PER\n" + "Gates NNP I-NP I-PER\n" + "Elon NNP B-NP B-PER\n" + "Musk NNP I-NP I-PER\n\n";
InputStreamFactory input = () -> new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, input, GENERATE_PERSON_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
Span[] spans = sample.getNames();
assertEquals(2, spans.length);
assertEquals(new Span(0, 2, "PER"), spans[0]);
assertEquals(new Span(2, 4, "PER"), spans[1]);
stream.close();
}

@Test
public void testResetAfterStreamExhaustionGivesSameOutput() throws IOException {
String content = "Alice NNP B-NP B-PER\n\n";
byte[] data = content.getBytes(StandardCharsets.UTF_8);
InputStreamFactory input = () -> new ByteArrayInputStream(data);
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, input, GENERATE_PERSON_ENTITIES);
NameSample first = stream.read();
assertNotNull(first);
NameSample end = stream.read();
assertNull(end);
stream.reset();
NameSample again = stream.read();
assertNotNull(again);
assertEquals("Alice", again.getSentence()[0]);
assertEquals("PER", again.getNames()[0].getType());
stream.close();
}

@Test
public void testMultipleIWithoutBCreatesMultipleSingleLengthSpans() throws IOException {
String conll = "Foo NNP B-NP I-PER\n" + "Bar NNP I-NP I-PER\n\n";
InputStreamFactory input = () -> new ByteArrayInputStream(conll.getBytes(StandardCharsets.UTF_8));
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, input, GENERATE_PERSON_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
Span[] spans = sample.getNames();
assertEquals(2, spans.length);
assertEquals(new Span(0, 1, "PER"), spans[0]);
assertEquals(new Span(1, 2, "PER"), spans[1]);
stream.close();
}

@Test
public void testIWithDifferentEndingsClosesPreviousAndOpensNewSpan() throws IOException {
String conll = "Alpha NNP B-NP I-ORG\n" + "Beta NNP I-NP I-LOC\n" + "Gamma NNP I-NP I-MISC\n\n";
InputStreamFactory input = () -> new ByteArrayInputStream(conll.getBytes(StandardCharsets.UTF_8));
int types = GENERATE_LOCATION_ENTITIES | GENERATE_ORGANIZATION_ENTITIES | GENERATE_MISC_ENTITIES;
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, input, types);
NameSample sample = stream.read();
assertNotNull(sample);
Span[] spans = sample.getNames();
assertEquals(3, spans.length);
assertEquals("ORG", spans[0].getType());
assertEquals("LOC", spans[1].getType());
assertEquals("MISC", spans[2].getType());
stream.close();
}

@Test
public void testBEntityImmediatelyFollowsAnotherWithoutOBetween() throws IOException {
String conll = "Red NNP B-NP B-MISC\n" + "Cross NNP I-NP I-MISC\n" + "United NNP B-NP B-ORG\n" + "Nations NNP I-NP I-ORG\n\n";
InputStreamFactory input = () -> new ByteArrayInputStream(conll.getBytes(StandardCharsets.UTF_8));
int types = GENERATE_MISC_ENTITIES | GENERATE_ORGANIZATION_ENTITIES;
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, input, types);
NameSample sample = stream.read();
assertNotNull(sample);
Span[] spans = sample.getNames();
assertEquals(2, spans.length);
assertEquals("MISC", spans[0].getType());
assertEquals(new Span(0, 2, "MISC"), spans[0]);
assertEquals("ORG", spans[1].getType());
assertEquals(new Span(2, 4, "ORG"), spans[1]);
stream.close();
}

@Test
public void testTagFilteredOutCompletelyDropsEntity() throws IOException {
String conll = "Stanford NNP B-NP B-ORG\n" + "University NNP I-NP I-ORG\n\n";
InputStreamFactory input = () -> new ByteArrayInputStream(conll.getBytes(StandardCharsets.UTF_8));
int types = GENERATE_PERSON_ENTITIES;
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, input, types);
NameSample sample = stream.read();
assertNotNull(sample);
assertEquals(2, sample.getSentence().length);
assertEquals(0, sample.getNames().length);
stream.close();
}

@Test
public void testSentenceEndsWithOClosesPreviousSpan() throws IOException {
String conll = "Tesla NNP B-NP B-ORG\n" + "Motors NNP I-NP I-ORG\n" + "was VBD B-VP O\n\n";
InputStreamFactory input = () -> new ByteArrayInputStream(conll.getBytes(StandardCharsets.UTF_8));
int types = GENERATE_ORGANIZATION_ENTITIES;
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, input, types);
NameSample sample = stream.read();
assertNotNull(sample);
assertEquals(1, sample.getNames().length);
assertEquals(new Span(0, 2, "ORG"), sample.getNames()[0]);
stream.close();
}

@Test
public void testTagWithNoValidEntityTypeIsFilteredOut() throws IOException {
String conll = "MiscToken NNP B-NP B-MISC\n\n";
InputStreamFactory input = () -> new ByteArrayInputStream(conll.getBytes(StandardCharsets.UTF_8));
int types = 0;
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, input, types);
NameSample sample = stream.read();
assertNotNull(sample);
assertEquals(1, sample.getSentence().length);
assertEquals(0, sample.getNames().length);
stream.close();
}

@Test
public void testMultipleEmptyLinesBeforeValidContentIsIgnored() throws IOException {
String conll = "\n\n\n" + "Obama NNP B-NP B-PER\n\n";
InputStreamFactory input = () -> new ByteArrayInputStream(conll.getBytes(StandardCharsets.UTF_8));
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, input, GENERATE_PERSON_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
assertEquals(1, sample.getNames().length);
assertEquals("PER", sample.getNames()[0].getType());
stream.close();
}

@Test
public void testSingleValidLineWithoutNewlineAtEndReturnsValidSample() throws IOException {
String conll = "IBM NNP B-NP B-ORG";
InputStreamFactory input = () -> new ByteArrayInputStream(conll.getBytes(StandardCharsets.UTF_8));
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, input, GENERATE_ORGANIZATION_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
assertEquals("IBM", sample.getSentence()[0]);
assertEquals(1, sample.getNames().length);
assertEquals("ORG", sample.getNames()[0].getType());
NameSample next = stream.read();
assertNull(next);
stream.close();
}

@Test
public void testResetOnUnresettableStreamThrowsException() throws IOException {
ObjectStream<String> unresettable = new ObjectStream<String>() {

public String read() {
return null;
}

public void reset() throws IOException {
throw new UnsupportedOperationException("reset not supported");
}

public void close() {
}
};
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, unresettable, GENERATE_PERSON_ENTITIES);
try {
stream.reset();
fail("Expected UnsupportedOperationException");
} catch (UnsupportedOperationException e) {
assertEquals("reset not supported", e.getMessage());
}
}

@Test
public void testIImmediatelyDifferentTypeAfterBCreatesNewSpan() throws IOException {
String conll = "A NNP B-NP B-PER\n" + "B NNP I-NP I-ORG\n" + "C NNP I-NP I-PER\n\n";
InputStreamFactory input = () -> new ByteArrayInputStream(conll.getBytes(StandardCharsets.UTF_8));
int types = GENERATE_PERSON_ENTITIES | GENERATE_ORGANIZATION_ENTITIES;
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, input, types);
NameSample sample = stream.read();
assertNotNull(sample);
Span[] spans = sample.getNames();
assertEquals(3, spans.length);
assertEquals("PER", spans[0].getType());
assertEquals("ORG", spans[1].getType());
assertEquals("PER", spans[2].getType());
stream.close();
}
}
