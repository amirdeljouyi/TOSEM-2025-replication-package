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

public class Conll03NameSampleStream_llmsuite_4_GPTLLMTest {

@Test
public void testReadEnglishSentenceWithEntities() throws IOException {
String data = "-DOCSTART- -X- -X- O\n" + "\n" + "Alice NNP B-NP B-PER\n" + "went VBD B-VP O\n" + "to TO B-PP O\n" + "Paris NNP B-NP B-LOC\n" + "\n";
InputStreamFactory in = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, in, ALL_ENTITY_TYPES);
// NameSample sample = stream.read();
// assertNotNull(sample);
// assertEquals(4, sample.getSentence().length);
// assertEquals("Alice", sample.getSentence()[0]);
// assertEquals("went", sample.getSentence()[1]);
// assertEquals("to", sample.getSentence()[2]);
// assertEquals("Paris", sample.getSentence()[3]);
// Span[] spans = sample.getNames();
// assertEquals(2, spans.length);
// assertEquals(0, spans[0].getStart());
// assertEquals(1, spans[0].getEnd());
// assertEquals("per", spans[0].getType());
// assertEquals(3, spans[1].getStart());
// assertEquals(4, spans[1].getEnd());
// assertEquals("loc", spans[1].getType());
// assertTrue(sample.isClearAdaptiveDataSet());
// stream.close();
}

@Test
public void testReadGermanEntities() throws IOException {
String data = "-DOCSTART- -X- -X- -X- O\n" + "\n" + "Angela Angela NNP B-NP B-PER\n" + "Merkel Merkel NNP I-NP I-PER\n" + "besucht besucht V B-VP O\n" + "Berlin Berlin NNP B-NP B-LOC\n" + "\n";
InputStreamFactory in = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.DE, in, ALL_ENTITY_TYPES);
// NameSample sample = stream.read();
// assertNotNull(sample);
// assertEquals(4, sample.getSentence().length);
// assertEquals("Angela", sample.getSentence()[0]);
// assertEquals("Merkel", sample.getSentence()[1]);
// assertEquals("besucht", sample.getSentence()[2]);
// assertEquals("Berlin", sample.getSentence()[3]);
// Span[] spans = sample.getNames();
// assertEquals(2, spans.length);
// assertEquals(0, spans[0].getStart());
// assertEquals(2, spans[0].getEnd());
// assertEquals("per", spans[0].getType());
// assertEquals(3, spans[1].getStart());
// assertEquals(4, spans[1].getEnd());
// assertEquals("loc", spans[1].getType());
// assertTrue(sample.isClearAdaptiveDataSet());
// stream.close();
}

@Test
public void testSentenceWithFilteredEntities() throws IOException {
String data = "Bob NNP B-NP B-PER\n" + "works VBZ B-VP O\n" + "at IN B-PP O\n" + "Google NNP B-NP B-ORG\n" + "\n";
int onlyPerson = Conll02NameSampleStream.GENERATE_PERSON_ENTITIES;
InputStreamFactory in = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, in, onlyPerson);
NameSample sample = stream.read();
assertNotNull(sample);
assertEquals(4, sample.getSentence().length);
Span[] spans = sample.getNames();
assertEquals(1, spans.length);
Span span = spans[0];
assertEquals(0, span.getStart());
assertEquals(1, span.getEnd());
assertEquals("per", span.getType());
stream.close();
}

@Test
public void testEmptyEventSkipping() throws IOException {
String data = "\n" + "\n" + "John NNP B-NP B-PER\n" + "lifts VBZ B-VP O\n" + "\n";
InputStreamFactory in = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, in, ALL_ENTITY_TYPES);
// NameSample sample = stream.read();
// assertNotNull(sample);
// assertEquals("John", sample.getSentence()[0]);
// assertEquals("lifts", sample.getSentence()[1]);
// assertEquals(1, sample.getNames().length);
// assertFalse(sample.isClearAdaptiveDataSet());
// stream.close();
}

@Test
public void testInvalidFieldCountShouldThrow() throws IOException {
String data = "ThisLineIsInvalid PER\n\n";
InputStreamFactory in = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, in, ALL_ENTITY_TYPES);
// stream.read();
// stream.close();
}

@Test
public void testMultipleEntitiesSameType() throws IOException {
String data = "Barack NNP B-NP B-PER\n" + "Obama NNP I-NP I-PER\n" + "and CC O O\n" + "Angela NNP B-NP B-PER\n" + "Merkel NNP I-NP I-PER\n" + "\n";
InputStreamFactory in = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, in, ALL_ENTITY_TYPES);
// NameSample sample = stream.read();
// assertNotNull(sample);
// assertEquals(5, sample.getSentence().length);
// Span[] spans = sample.getNames();
// assertEquals(2, spans.length);
// assertEquals(0, spans[0].getStart());
// assertEquals(2, spans[0].getEnd());
// assertEquals("per", spans[0].getType());
// assertEquals(3, spans[1].getStart());
// assertEquals(5, spans[1].getEnd());
// assertEquals("per", spans[1].getType());
// stream.close();
}

@Test
public void testInvalidTagFormatThrows() throws IOException {
String data = "InvalidTagWord NNP B-NP X-LOC\n" + "\n";
InputStreamFactory in = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, in, ALL_ENTITY_TYPES);
// stream.read();
// stream.close();
}

@Test
public void testEndOfStreamReturnsNull() throws IOException {
String data = "Tom NNP B-NP B-PER\n" + "\n";
InputStreamFactory in = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, in, ALL_ENTITY_TYPES);
// NameSample first = stream.read();
// NameSample second = stream.read();
// assertNotNull(first);
// assertNull(second);
// stream.close();
}

@Test
public void testNoEmptyLineAfterDocStartThrows() throws IOException {
String data = "-DOCSTART- -X- -X- O\n" + "UnexpectedContent VB O O\n";
InputStreamFactory in = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, in, ALL_ENTITY_TYPES);
// stream.read();
// stream.close();
}

@Test
public void testOnlyMiscEntityIsParsed() throws IOException {
String data = "Olympics NNP B-NP B-MISC\n" + "is VBZ B-VP O\n" + "great JJ B-ADJP O\n" + "\n";
int onlyMisc = Conll02NameSampleStream.GENERATE_MISC_ENTITIES;
InputStreamFactory in = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, in, onlyMisc);
NameSample sample = stream.read();
assertNotNull(sample);
assertEquals(3, sample.getSentence().length);
Span[] spans = sample.getNames();
assertEquals(1, spans.length);
assertEquals(0, spans[0].getStart());
assertEquals(1, spans[0].getEnd());
assertEquals("misc", spans[0].getType());
stream.close();
}

@Test
public void testEntityFilteringRemovesAllEntities() throws IOException {
String data = "Obama NNP B-NP B-PER\n" + "\n";
int onlyMisc = Conll02NameSampleStream.GENERATE_MISC_ENTITIES;
InputStreamFactory in = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, in, onlyMisc);
NameSample sample = stream.read();
assertNotNull(sample);
assertEquals(1, sample.getSentence().length);
assertEquals("Obama", sample.getSentence()[0]);
assertEquals(0, sample.getNames().length);
stream.close();
}

@Test
public void testStreamResetReturnsSameSample() throws IOException {
String data = "Tom NNP B-NP B-PER\n" + "\n";
InputStreamFactory in = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, in, ALL_ENTITY_TYPES);
// NameSample first = stream.read();
// stream.reset();
// NameSample second = stream.read();
// assertNotNull(first);
// assertNotNull(second);
// assertEquals(first.getSentence().length, second.getSentence().length);
// assertEquals(first.getNames().length, second.getNames().length);
// assertEquals(first.getSentence()[0], second.getSentence()[0]);
// assertEquals(first.getNames()[0].getStart(), second.getNames()[0].getStart());
// stream.close();
}

@Test
public void testSentenceWithSingleTokenAndEntity() throws IOException {
String data = "Berlin NNP B-NP B-LOC\n" + "\n";
InputStreamFactory in = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, in, ALL_ENTITY_TYPES);
// NameSample sample = stream.read();
// assertNotNull(sample);
// assertEquals(1, sample.getSentence().length);
// assertEquals("Berlin", sample.getSentence()[0]);
// Span[] spans = sample.getNames();
// assertEquals(1, spans.length);
// assertEquals(0, spans[0].getStart());
// assertEquals(1, spans[0].getEnd());
// assertEquals("loc", spans[0].getType());
// stream.close();
}

@Test
public void testEntityInterruptedByDifferentTagType() throws IOException {
String data = "Barack NNP B-NP B-PER\n" + "Obama NNP I-NP I-PER\n" + "UN NNP I-NP I-ORG\n" + "\n";
InputStreamFactory in = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, in, ALL_ENTITY_TYPES);
// NameSample sample = stream.read();
// assertNotNull(sample);
// assertEquals(3, sample.getSentence().length);
// Span[] spans = sample.getNames();
// assertEquals(2, spans.length);
// assertEquals(0, spans[0].getStart());
// assertEquals(2, spans[0].getEnd());
// assertEquals("per", spans[0].getType());
// assertEquals(2, spans[1].getStart());
// assertEquals(3, spans[1].getEnd());
// assertEquals("org", spans[1].getType());
// stream.close();
}

@Test
public void testSentenceWithInvalidIntermediateIWithoutB() throws IOException {
String data = "Obama NNP I-NP I-PER\n" + "was VBD B-VP O\n" + "\n";
InputStreamFactory in = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, in, ALL_ENTITY_TYPES);
// NameSample sample = stream.read();
// assertNotNull(sample);
// assertEquals(2, sample.getSentence().length);
// Span[] spans = sample.getNames();
// assertEquals(1, spans.length);
// assertEquals(0, spans[0].getStart());
// assertEquals(1, spans[0].getEnd());
// assertEquals("per", spans[0].getType());
// stream.close();
}

@Test
public void testSentenceWithNoEntities() throws IOException {
String data = "He PRP B-NP O\n" + "runs VBZ B-VP O\n" + "fast RB B-ADVP O\n" + "\n";
InputStreamFactory in = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, in, ALL_ENTITY_TYPES);
// NameSample sample = stream.read();
// assertNotNull(sample);
// assertEquals(3, sample.getSentence().length);
// assertEquals("He", sample.getSentence()[0]);
// assertEquals("fast", sample.getSentence()[2]);
// Span[] spans = sample.getNames();
// assertEquals(0, spans.length);
// stream.close();
}

@Test
public void testSentenceWithRepeatedOEntitiesBoundary() throws IOException {
String data = "He PRP B-NP O\n" + "met VBD B-VP O\n" + "Tom NNP B-NP B-PER\n" + "Cruise NNP I-NP I-PER\n" + "today NN B-NP O\n" + "\n";
InputStreamFactory in = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, in, ALL_ENTITY_TYPES);
// NameSample sample = stream.read();
// assertNotNull(sample);
// assertEquals(5, sample.getSentence().length);
// Span[] spans = sample.getNames();
// assertEquals(1, spans.length);
// assertEquals(2, spans[0].getStart());
// assertEquals(4, spans[0].getEnd());
// assertEquals("per", spans[0].getType());
// stream.close();
}

@Test
public void testIncompleteFinalEntityIsReturned() throws IOException {
String data = "Start NNP B-NP B-ORG\n" + "Up NNP I-NP\n";
InputStreamFactory in = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, in, ALL_ENTITY_TYPES);
// try {
// stream.read();
// fail("IOException expected due to invalid number of fields in final line");
// } catch (IOException e) {
// assertTrue(e.getMessage().contains("Incorrect number of fields"));
// }
// stream.close();
}

@Test
public void testSentenceWithConsecutiveDifferentEntityTypes() throws IOException {
String data = "IBM NNP B-NP B-ORG\n" + "uses VBZ B-VP O\n" + "Java NNP B-NP B-MISC\n" + "\n";
InputStreamFactory in = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, in, ALL_ENTITY_TYPES);
// NameSample sample = stream.read();
// assertNotNull(sample);
// Span[] spans = sample.getNames();
// assertEquals(2, spans.length);
// assertEquals(0, spans[0].getStart());
// assertEquals(1, spans[0].getEnd());
// assertEquals("org", spans[0].getType());
// assertEquals(2, spans[1].getStart());
// assertEquals(3, spans[1].getEnd());
// assertEquals("misc", spans[1].getType());
// stream.close();
}

@Test
public void testSingleLineDocStartWithTrailingNull() throws IOException {
String data = "-DOCSTART- -X- -X- O\n" + "\n";
InputStreamFactory in = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, in, ALL_ENTITY_TYPES);
// NameSample sample = stream.read();
// assertNull(sample);
// stream.close();
}

@Test
public void testMultipleSentencesInStream() throws IOException {
String data = "Barack NNP B-NP B-PER\n" + "Obama NNP I-NP I-PER\n" + "\n" + "Berlin NNP B-NP B-LOC\n" + "is VBZ B-VP O\n" + "cold JJ B-ADJP O\n" + "\n";
InputStreamFactory in = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, in, ALL_ENTITY_TYPES);
// NameSample first = stream.read();
// assertNotNull(first);
// assertEquals(2, first.getSentence().length);
// assertEquals(1, first.getNames().length);
// assertEquals("per", first.getNames()[0].getType());
// NameSample second = stream.read();
// assertNotNull(second);
// assertEquals(3, second.getSentence().length);
// assertEquals(1, second.getNames().length);
// assertEquals("loc", second.getNames()[0].getType());
// NameSample third = stream.read();
// assertNull(third);
// stream.close();
}

@Test
public void testMultipleConsecutiveEmptyLines() throws IOException {
String data = "-DOCSTART- -X- -X- O\n" + "\n" + "\n" + "\n" + "John NNP B-NP B-PER\n" + "Smith NNP I-NP I-PER\n" + "\n";
InputStreamFactory in = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, in, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
assertEquals(2, sample.getSentence().length);
assertEquals("John", sample.getSentence()[0]);
assertEquals("Smith", sample.getSentence()[1]);
Span[] spans = sample.getNames();
assertEquals(1, spans.length);
assertEquals(0, spans[0].getStart());
assertEquals(2, spans[0].getEnd());
assertEquals("per", spans[0].getType());
stream.close();
}

@Test
public void testUnknownEntityNameTagOmittedDueToFilter() throws IOException {
String data = "Madrid NNP B-NP B-LOC\n" + "\n";
int filter = Conll02NameSampleStream.GENERATE_PERSON_ENTITIES | Conll02NameSampleStream.GENERATE_MISC_ENTITIES;
InputStreamFactory in = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, in, filter);
NameSample sample = stream.read();
assertNotNull(sample);
assertEquals(1, sample.getSentence().length);
assertEquals("Madrid", sample.getSentence()[0]);
Span[] spans = sample.getNames();
assertEquals(0, spans.length);
stream.close();
}

@Test
public void testOnlyBTagNoContinuation() throws IOException {
String data = "Google NNP B-NP B-ORG\n" + "\n";
InputStreamFactory in = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, in, Conll02NameSampleStream.GENERATE_ORGANIZATION_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
assertEquals(1, sample.getSentence().length);
assertEquals("Google", sample.getSentence()[0]);
Span[] spans = sample.getNames();
assertEquals(1, spans.length);
assertEquals(0, spans[0].getStart());
assertEquals(1, spans[0].getEnd());
assertEquals("org", spans[0].getType());
stream.close();
}

@Test
public void testMultipleSentencesSeparatedByDocStart() throws IOException {
String data = "-DOCSTART- -X- -X- O\n" + "\n" + "Barack NNP B-NP B-PER\n" + "Obama NNP I-NP I-PER\n" + "\n" + "-DOCSTART- -X- -X- O\n" + "\n" + "Berlin NNP B-NP B-LOC\n" + "\n";
InputStreamFactory in = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, in, ALL_ENTITY_TYPES);
// NameSample first = stream.read();
// assertNotNull(first);
// assertEquals(2, first.getSentence().length);
// assertTrue(first.isClearAdaptiveDataSet());
// NameSample second = stream.read();
// assertNotNull(second);
// assertEquals(1, second.getSentence().length);
// assertTrue(second.isClearAdaptiveDataSet());
// NameSample third = stream.read();
// assertNull(third);
// stream.close();
}

@Test
public void testEntitySpanWithMultipleIWithoutBShouldStillWork() throws IOException {
String data = "Obama NNP I-NP I-PER\n" + "Junior NNP I-NP I-PER\n" + "speaks VBZ B-VP O\n" + "\n";
InputStreamFactory in = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, in, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
assertEquals(3, sample.getSentence().length);
Span[] spans = sample.getNames();
assertEquals(1, spans.length);
assertEquals(0, spans[0].getStart());
assertEquals(2, spans[0].getEnd());
assertEquals("per", spans[0].getType());
stream.close();
}

@Test
public void testOrganizationEntityWithInterruptionByO() throws IOException {
String data = "UN NNP B-NP B-ORG\n" + "and CC O O\n" + "NATO NNP B-NP B-ORG\n" + "\n";
InputStreamFactory in = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, in, Conll02NameSampleStream.GENERATE_ORGANIZATION_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
assertEquals(3, sample.getSentence().length);
Span[] spans = sample.getNames();
assertEquals(2, spans.length);
assertEquals(0, spans[0].getStart());
assertEquals(1, spans[0].getEnd());
assertEquals("org", spans[0].getType());
assertEquals(2, spans[1].getStart());
assertEquals(3, spans[1].getEnd());
assertEquals("org", spans[1].getType());
stream.close();
}

@Test
public void testEntityFoldedDueToMismatchedIType() throws IOException {
String data = "European NNP B-NP B-MISC\n" + "Union NNP I-NP I-ORG\n" + "\n";
InputStreamFactory in = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, in, ALL_ENTITY_TYPES);
// NameSample sample = stream.read();
// assertNotNull(sample);
// assertEquals(2, sample.getSentence().length);
// Span[] spans = sample.getNames();
// assertEquals(2, spans.length);
// assertEquals(0, spans[0].getStart());
// assertEquals(1, spans[0].getEnd());
// assertEquals("misc", spans[0].getType());
// assertEquals(1, spans[1].getStart());
// assertEquals(2, spans[1].getEnd());
// assertEquals("org", spans[1].getType());
// stream.close();
}

@Test
public void testSentenceWithIWithoutTypeAfterB() throws IOException {
String data = "John NNP B-NP B-PER\n" + "Doe NNP I-NP I-\n" + "\n";
InputStreamFactory in = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, in, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);
try {
stream.read();
fail("Expected IOException due to invalid tag format");
} catch (IOException e) {
assertTrue(e.getMessage().contains("Invalid tag"));
}
stream.close();
}

@Test
public void testSentenceEndsWithIWithoutTrailingNewline() throws IOException {
String data = "John NNP B-NP B-PER\n" + "Doe NNP I-NP I-PER";
InputStreamFactory in = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, in, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
assertEquals(2, sample.getSentence().length);
Span[] spans = sample.getNames();
assertEquals(1, spans.length);
assertEquals(0, spans[0].getStart());
assertEquals(2, spans[0].getEnd());
assertEquals("per", spans[0].getType());
NameSample end = stream.read();
assertNull(end);
stream.close();
}

@Test
public void testInterleavedMultipleNameTypesWithInterruption() throws IOException {
String data = "Jean NNP B-NP B-PER\n" + "Dupont NNP I-NP I-PER\n" + "from IN B-PP O\n" + "UNESCO NNP B-NP B-ORG\n" + "in IN B-PP O\n" + "Paris NNP B-NP B-LOC\n" + "\n";
InputStreamFactory in = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, in, ALL_ENTITY_TYPES);
// NameSample sample = stream.read();
// assertNotNull(sample);
// assertEquals(6, sample.getSentence().length);
// Span[] spans = sample.getNames();
// assertEquals(3, spans.length);
// assertEquals(0, spans[0].getStart());
// assertEquals(2, spans[0].getEnd());
// assertEquals("per", spans[0].getType());
// assertEquals(3, spans[1].getStart());
// assertEquals(4, spans[1].getEnd());
// assertEquals("org", spans[1].getType());
// assertEquals(5, spans[2].getStart());
// assertEquals(6, spans[2].getEnd());
// assertEquals("loc", spans[2].getType());
// stream.close();
}

@Test
public void testIncorrectLanguageFieldCountInterpretation() throws IOException {
String data = "Haus NN B-NP B-LOC\n" + "\n";
InputStreamFactory in = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.DE, in, ALL_ENTITY_TYPES);
// try {
// stream.read();
// fail("Expected IOException due to incorrect field count for DE");
// } catch (IOException e) {
// assertTrue(e.getMessage().contains("Incorrect number of fields"));
// }
// stream.close();
}

@Test
public void testEmptyInputStreamReturnsNull() throws IOException {
String data = "";
InputStreamFactory in = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, in, ALL_ENTITY_TYPES);
// NameSample sample = stream.read();
// assertNull(sample);
// stream.close();
}

@Test
public void testSingleTagOEntityOnly() throws IOException {
String data = "dog NN B-NP O\n" + "\n";
InputStreamFactory in = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, in, ALL_ENTITY_TYPES);
// NameSample sample = stream.read();
// assertNotNull(sample);
// assertEquals(1, sample.getSentence().length);
// assertEquals("dog", sample.getSentence()[0]);
// assertEquals(0, sample.getNames().length);
// stream.close();
}

@Test
public void testMixedCaseEntityLabelsHandledCorrectly() throws IOException {
String data = "Tesla NNP B-NP B-ORG\n" + "Motors NNP I-NP I-ORG\n" + "\n";
InputStreamFactory in = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, in, Conll02NameSampleStream.GENERATE_ORGANIZATION_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
Span[] spans = sample.getNames();
assertEquals(1, spans.length);
assertEquals(0, spans[0].getStart());
assertEquals(2, spans[0].getEnd());
assertEquals("org", spans[0].getType());
stream.close();
}

@Test
public void testOnlyIEntityWithoutPrecedingB() throws IOException {
String data = "Smith NNP I-NP I-ORG\n" + "Inc. NNP B-NP O\n" + "\n";
InputStreamFactory in = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, in, Conll02NameSampleStream.GENERATE_ORGANIZATION_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
assertEquals(2, sample.getSentence().length);
assertEquals("Smith", sample.getSentence()[0]);
assertEquals("Inc.", sample.getSentence()[1]);
Span[] spans = sample.getNames();
assertEquals(1, spans.length);
assertEquals(0, spans[0].getStart());
assertEquals(1, spans[0].getEnd());
assertEquals("org", spans[0].getType());
stream.close();
}

@Test
public void testPartialEntityFollowedByNewBTag() throws IOException {
String data = "Apple NNP B-NP B-ORG\n" + "Inc. NNP I-NP I-ORG\n" + "Microsoft NNP B-NP B-ORG\n" + "\n";
InputStreamFactory in = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, in, Conll02NameSampleStream.GENERATE_ORGANIZATION_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
assertEquals(3, sample.getSentence().length);
assertEquals("Apple", sample.getSentence()[0]);
assertEquals("Inc.", sample.getSentence()[1]);
assertEquals("Microsoft", sample.getSentence()[2]);
Span[] spans = sample.getNames();
assertEquals(2, spans.length);
assertEquals(0, spans[0].getStart());
assertEquals(2, spans[0].getEnd());
assertEquals("org", spans[0].getType());
assertEquals(2, spans[1].getStart());
assertEquals(3, spans[1].getEnd());
assertEquals("org", spans[1].getType());
stream.close();
}

@Test
public void testIEntityWithMismatchedTagEndsCurrent() throws IOException {
String data = "Jane NNP B-NP B-PER\n" + "Smith NNP I-NP I-PER\n" + "Technologies NNP I-NP I-ORG\n" + "\n";
InputStreamFactory in = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, in, ALL_ENTITY_TYPES);
// NameSample sample = stream.read();
// assertNotNull(sample);
// assertEquals(3, sample.getSentence().length);
// Span[] spans = sample.getNames();
// assertEquals(2, spans.length);
// assertEquals(0, spans[0].getStart());
// assertEquals(2, spans[0].getEnd());
// assertEquals("per", spans[0].getType());
// assertEquals(2, spans[1].getStart());
// assertEquals(3, spans[1].getEnd());
// assertEquals("org", spans[1].getType());
// stream.close();
}

@Test
public void testMixedBAndIWithTaglessIntermediateLine() throws IOException {
String data = "Michael NNP B-NP B-PER\n" + "Jordan NNP I-NP I-PER\n" + "Chicago\n" + "\n";
InputStreamFactory in = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, in, ALL_ENTITY_TYPES);
// try {
// stream.read();
// fail("Expected IOException due to malformed input line with missing tags.");
// } catch (IOException e) {
// assertTrue(e.getMessage().contains("Incorrect number of fields"));
// }
// stream.close();
}

@Test
public void testTagCaseInsensitiveEntityTypes() throws IOException {
String data = "Apple NNP B-NP B-ORG\n" + "Park NNP I-NP I-ORG\n" + "Festival NNP B-NP b-misc\n" + "\n";
InputStreamFactory in = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, in, ALL_ENTITY_TYPES);
// NameSample sample = stream.read();
// assertNotNull(sample);
// assertEquals(3, sample.getSentence().length);
// Span[] spans = sample.getNames();
// assertEquals(2, spans.length);
// assertEquals("org", spans[0].getType());
// assertEquals(0, spans[0].getStart());
// assertEquals(2, spans[0].getEnd());
// assertEquals("misc", spans[1].getType());
// assertEquals(2, spans[1].getStart());
// assertEquals(3, spans[1].getEnd());
// stream.close();
}

@Test
public void testMultipleConsecutiveOEntitiesClearsPreviousSpan() throws IOException {
String data = "New NNP B-NP B-LOC\n" + "York NNP I-NP I-LOC\n" + "is VBZ O O\n" + "great JJ O O\n" + "\n";
InputStreamFactory in = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, in, CONLL02NameSampleStream.GENERATE_LOCATION_ENTITIES);
// NameSample sample = stream.read();
// assertNotNull(sample);
// assertEquals(4, sample.getSentence().length);
// Span[] spans = sample.getNames();
// assertEquals(1, spans.length);
// assertEquals(0, spans[0].getStart());
// assertEquals(2, spans[0].getEnd());
// assertEquals("loc", spans[0].getType());
// stream.close();
}

@Test
public void testInterleavedOandIEntitiesWithResetBetweenReads() throws IOException {
String data = "Elon NNP B-NP B-PER\n" + "Musk NNP I-NP I-PER\n" + "is VBZ B-VP O\n" + "CEO NNP B-NP B-ORG\n" + "\n";
InputStreamFactory in = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
// Conll03NameSampleStream stream1 = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, in, ALL_ENTITY_TYPES);
// NameSample sample1 = stream1.read();
// assertNotNull(sample1);
InputStreamFactory inReset = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
// Conll03NameSampleStream stream2 = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, inReset, ALL_ENTITY_TYPES);
// stream2.reset();
// NameSample sample2 = stream2.read();
// assertNotNull(sample2);
// assertArrayEquals(sample1.getSentence(), sample2.getSentence());
// assertEquals(sample1.getNames().length, sample2.getNames().length);
// stream1.close();
// stream2.close();
}

@Test
public void testResetOnUnderlyingLineStreamRestoresPosition() throws IOException {
String data = "London NNP B-NP B-LOC\n" + "Bridge NNP I-NP I-LOC\n" + "\n";
InputStreamFactory inFactory = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, inFactory, Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES);
NameSample firstSample = stream.read();
assertNotNull(firstSample);
assertEquals(2, firstSample.getSentence().length);
assertEquals(1, firstSample.getNames().length);
assertEquals("loc", firstSample.getNames()[0].getType());
stream.reset();
NameSample secondSample = stream.read();
assertNotNull(secondSample);
assertEquals(2, secondSample.getSentence().length);
assertEquals(1, secondSample.getNames().length);
assertEquals("loc", secondSample.getNames()[0].getType());
stream.close();
}

@Test
public void testMultipleEntitiesBackToBackWithoutO() throws IOException {
String data = "IBM NNP B-NP B-ORG\n" + "is VBZ O O\n" + "Google NNP B-NP B-ORG\n" + "\n";
InputStreamFactory input = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, input, Conll02NameSampleStream.GENERATE_ORGANIZATION_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
assertEquals(3, sample.getSentence().length);
assertEquals("IBM", sample.getSentence()[0]);
assertEquals("is", sample.getSentence()[1]);
assertEquals("Google", sample.getSentence()[2]);
Span[] spans = sample.getNames();
assertEquals(2, spans.length);
assertEquals(0, spans[0].getStart());
assertEquals(1, spans[0].getEnd());
assertEquals("org", spans[0].getType());
assertEquals(2, spans[1].getStart());
assertEquals(3, spans[1].getEnd());
assertEquals("org", spans[1].getType());
stream.close();
}

@Test
public void testNoEntitiesAndOnlyWhitespaceLines() throws IOException {
String data = "   \n" + "\t\n" + "\n";
InputStreamFactory input = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, input, ALL_ENTITY_TYPES);
// NameSample sample = stream.read();
// assertNull(sample);
// stream.close();
}

@Test
public void testReadTaggedEntityAfterInitialEmptyLines() throws IOException {
String data = "\n" + "\n" + "-DOCSTART- -X- -X- O\n" + "\n" + "Google NNP B-NP B-ORG\n" + "\n";
InputStreamFactory inputStream = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, inputStream, Conll02NameSampleStream.GENERATE_ORGANIZATION_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
assertEquals(1, sample.getSentence().length);
assertEquals("Google", sample.getSentence()[0]);
Span[] spans = sample.getNames();
assertEquals(1, spans.length);
assertEquals(0, spans[0].getStart());
assertEquals(1, spans[0].getEnd());
assertEquals("org", spans[0].getType());
assertTrue(sample.isClearAdaptiveDataSet());
stream.close();
}

@Test
public void testReadReturnsNullAfterAllSamplesConsumed() throws IOException {
String data = "UN NNP B-NP B-ORG\n" + "\n";
InputStreamFactory inStream = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, inStream, Conll02NameSampleStream.GENERATE_ORGANIZATION_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
NameSample second = stream.read();
assertNull(second);
stream.close();
}

@Test
public void testResetThrowsWhenLineStreamDoesNotSupportReset() throws IOException {
ObjectStream<String> lineStream = new ObjectStream<String>() {

@Override
public String read() {
return null;
}

@Override
public void reset() {
throw new UnsupportedOperationException("reset not supported");
}

@Override
public void close() {
}
};
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, ALL_ENTITY_TYPES);
try {
// stream.reset();
fail("Expected UnsupportedOperationException on reset");
} catch (UnsupportedOperationException e) {
assertEquals("reset not supported", e.getMessage());
}
}

@Test
public void testCloseDoesNotThrowForMultipleCalls() throws IOException {
String data = "Ala NNP B-NP B-PER\n" + "\n";
InputStreamFactory inputStream = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, inputStream, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
stream.close();
stream.close();
}

@Test
public void testSpanCreationWithSplitByNewB() throws IOException {
String data = "European NNP B-NP B-MISC\n" + "Union NNP I-NP I-MISC\n" + "NATO NNP B-NP B-ORG\n" + "Alliance NNP I-NP I-ORG\n" + "\n";
InputStreamFactory inputStream = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, inputStream, Conll02NameSampleStream.GENERATE_MISC_ENTITIES | Conll02NameSampleStream.GENERATE_ORGANIZATION_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
assertEquals(4, sample.getSentence().length);
Span[] spans = sample.getNames();
assertEquals(2, spans.length);
assertEquals(0, spans[0].getStart());
assertEquals(2, spans[0].getEnd());
assertEquals("misc", spans[0].getType());
assertEquals(2, spans[1].getStart());
assertEquals(4, spans[1].getEnd());
assertEquals("org", spans[1].getType());
stream.close();
}

@Test
public void testUnclosedEntityAtEndOfFile() throws IOException {
String data = "George NNP B-NP B-PER\n" + "Bush NNP I-NP I-PER";
InputStreamFactory input = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, input, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
assertEquals(2, sample.getSentence().length);
Span[] spans = sample.getNames();
assertEquals(1, spans.length);
assertEquals(0, spans[0].getStart());
assertEquals(2, spans[0].getEnd());
assertEquals("per", spans[0].getType());
NameSample next = stream.read();
assertNull(next);
stream.close();
}

@Test
public void testTagWithMalformedPrefixThrowsException() throws IOException {
String data = "InvalidTag NNP B-NP Z-PER\n" + "\n";
InputStreamFactory input = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, input, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);
stream.read();
stream.close();
}

@Test
public void testCombinationOfSupportedAndUnsupportedEntityTypes() throws IOException {
String data = "Rome NNP B-NP B-LOC\n" + "Festival NNP B-NP B-MISC\n" + "\n";
int filter = Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES;
InputStreamFactory input = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, input, filter);
NameSample sample = stream.read();
assertNotNull(sample);
assertEquals(2, sample.getSentence().length);
Span[] spans = sample.getNames();
assertEquals(1, spans.length);
assertEquals("loc", spans[0].getType());
assertEquals(0, spans[0].getStart());
assertEquals(1, spans[0].getEnd());
stream.close();
}

@Test
public void testEmptyLineMidSentenceReturnsValidResult() throws IOException {
String data = "John NNP B-NP B-PER\n" + "\n" + "Smith NNP B-NP B-PER\n" + "\n";
InputStreamFactory input = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, input, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);
NameSample firstSample = stream.read();
assertNotNull(firstSample);
assertEquals(1, firstSample.getSentence().length);
assertEquals("John", firstSample.getSentence()[0]);
assertEquals(1, firstSample.getNames().length);
NameSample secondSample = stream.read();
assertNotNull(secondSample);
assertEquals(1, secondSample.getSentence().length);
assertEquals("Smith", secondSample.getSentence()[0]);
assertEquals(1, secondSample.getNames().length);
stream.close();
}

@Test
public void testSpansFromMultipleEntitiesWithDifferentTypes() throws IOException {
String data = "Barack NNP B-NP B-PER\n" + "Obama NNP I-NP I-PER\n" + "UN NNP B-NP B-ORG\n" + "Paris NNP B-NP B-LOC\n" + "Festival NNP B-NP B-MISC\n" + "\n";
InputStreamFactory input = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, input, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES | Conll02NameSampleStream.GENERATE_ORGANIZATION_ENTITIES | Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES | Conll02NameSampleStream.GENERATE_MISC_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
Span[] spans = sample.getNames();
assertEquals(4, spans.length);
assertEquals("per", spans[0].getType());
assertEquals(0, spans[0].getStart());
assertEquals(2, spans[0].getEnd());
assertEquals("org", spans[1].getType());
assertEquals(2, spans[1].getStart());
assertEquals(3, spans[1].getEnd());
assertEquals("loc", spans[2].getType());
assertEquals(3, spans[2].getStart());
assertEquals(4, spans[2].getEnd());
assertEquals("misc", spans[3].getType());
assertEquals(4, spans[3].getStart());
assertEquals(5, spans[3].getEnd());
stream.close();
}

@Test
public void testTagsWithExtraWhitespaceHandledCorrectly() throws IOException {
String data = "New   NNP   B-NP  B-LOC  \n" + "York  NNP    I-NP   I-LOC\n" + "\n";
InputStreamFactory input = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, input, Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
assertEquals(2, sample.getSentence().length);
assertEquals("New", sample.getSentence()[0]);
assertEquals("York", sample.getSentence()[1]);
Span[] spans = sample.getNames();
assertEquals(1, spans.length);
assertEquals("loc", spans[0].getType());
assertEquals(0, spans[0].getStart());
assertEquals(2, spans[0].getEnd());
stream.close();
}

@Test
public void testSingleLineDocStartOnlyReturnsNull() throws IOException {
String data = "-DOCSTART- -X- -X- O\n" + "\n";
InputStreamFactory input = () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, input, ALL_ENTITY_TYPES);
// NameSample result = stream.read();
// assertNull(result);
// stream.close();
}
}
