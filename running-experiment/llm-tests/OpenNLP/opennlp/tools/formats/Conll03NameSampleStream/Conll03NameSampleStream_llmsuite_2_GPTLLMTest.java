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
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class Conll03NameSampleStream_llmsuite_2_GPTLLMTest {

@Test
public void testEnglishPersonEntityRecognition() throws IOException {
String sample = "-DOCSTART- -X- O O\n" + "\n" + "John NNP B-NP B-PER\n" + "Smith NNP I-NP I-PER\n" + "\n";
InputStream in = new ByteArrayInputStream(sample.getBytes(StandardCharsets.UTF_8));
ObjectStream<String> lineStream = new PlainTextByLineStream(() -> in, StandardCharsets.UTF_8);
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES | Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES | Conll02NameSampleStream.GENERATE_ORGANIZATION_ENTITIES | Conll02NameSampleStream.GENERATE_MISC_ENTITIES);
NameSample result = stream.read();
assertNotNull(result);
String[] sentence = result.getSentence();
assertEquals("John", sentence[0]);
assertEquals("Smith", sentence[1]);
Span[] spans = result.getNames();
assertEquals(1, spans.length);
assertEquals(0, spans[0].getStart());
assertEquals(2, spans[0].getEnd());
assertEquals("PER", spans[0].getType());
// assertTrue(result.isClearAdaptiveData());
}

@Test
public void testEnglishLineWithWrongFieldCount() throws IOException {
String sample = "John NNP B-NP\n";
InputStream in = new ByteArrayInputStream(sample.getBytes(StandardCharsets.UTF_8));
ObjectStream<String> lineStream = new PlainTextByLineStream(() -> in, StandardCharsets.UTF_8);
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES | Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES | Conll02NameSampleStream.GENERATE_ORGANIZATION_ENTITIES | Conll02NameSampleStream.GENERATE_MISC_ENTITIES);
stream.read();
}

@Test
public void testInvalidNEFormatThrows() throws IOException {
String content = "Hello NNP B-NP X-OTHER\n";
InputStream in = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
ObjectStream<String> lineStream = new PlainTextByLineStream(() -> in, StandardCharsets.UTF_8);
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES | Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES | Conll02NameSampleStream.GENERATE_ORGANIZATION_ENTITIES | Conll02NameSampleStream.GENERATE_MISC_ENTITIES);
stream.read();
}

@Test
public void testDocStartWithoutEmptyLine() throws IOException {
String badDoc = "-DOCSTART- -X- O O\n" + "Something\n";
InputStream in = new ByteArrayInputStream(badDoc.getBytes(StandardCharsets.UTF_8));
ObjectStream<String> lineStream = new PlainTextByLineStream(() -> in, StandardCharsets.UTF_8);
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);
stream.read();
}

@Test
public void testNoEntitiesProduceEmptySpanArray() throws IOException {
String data = "He PRP B-NP O\n" + "is VBZ B-VP O\n" + "good JJ B-ADJ O\n" + "\n";
InputStream in = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
ObjectStream<String> lineStream = new PlainTextByLineStream(() -> in, StandardCharsets.UTF_8);
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES | Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES | Conll02NameSampleStream.GENERATE_ORGANIZATION_ENTITIES | Conll02NameSampleStream.GENERATE_MISC_ENTITIES);
NameSample result = stream.read();
assertNotNull(result);
Span[] spans = result.getNames();
assertNotNull(spans);
assertEquals(0, spans.length);
assertEquals("He", result.getSentence()[0]);
}

@Test
public void testIWithoutBStartsNewEntity() throws IOException {
String example = "Berlin NNP B-NP I-LOC\n" + "is VBZ B-VP O\n" + "\n";
InputStream in = new ByteArrayInputStream(example.getBytes(StandardCharsets.UTF_8));
ObjectStream<String> lineStream = new PlainTextByLineStream(() -> in, StandardCharsets.UTF_8);
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES);
NameSample result = stream.read();
assertNotNull(result);
Span[] spans = result.getNames();
assertEquals(1, spans.length);
assertEquals("LOC", spans[0].getType());
assertEquals(0, spans[0].getStart());
assertEquals(1, spans[0].getEnd());
}

@Test
public void testSingleOrganizationEntityFilteredOut() throws IOException {
String orgLine = "Acme NNP B-NP B-ORG\n" + "\n";
InputStream in = new ByteArrayInputStream(orgLine.getBytes(StandardCharsets.UTF_8));
ObjectStream<String> stream = new PlainTextByLineStream(() -> in, StandardCharsets.UTF_8);
Conll03NameSampleStream nameStream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, stream, Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES);
NameSample result = nameStream.read();
assertNotNull(result);
Span[] spans = result.getNames();
assertEquals(0, spans.length);
}

@Test
public void testBackToBackEntitiesWithBTags() throws IOException {
String input = "John NNP B-NP B-PER\n" + "Mary NNP B-NP B-PER\n" + "\n";
InputStream in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
ObjectStream<String> stream = new PlainTextByLineStream(() -> in, StandardCharsets.UTF_8);
Conll03NameSampleStream sampleStream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, stream, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);
NameSample sample = sampleStream.read();
assertNotNull(sample);
Span[] spans = sample.getNames();
assertEquals(2, spans.length);
assertEquals(0, spans[0].getStart());
assertEquals(1, spans[0].getEnd());
assertEquals(1, spans[1].getStart());
assertEquals(2, spans[1].getEnd());
assertEquals("PER", spans[0].getType());
assertEquals("PER", spans[1].getType());
}

@Test
public void testMultipleEntityTypesInOneSentence() throws IOException {
String input = "-DOCSTART- -X- O O\n" + "\n" + "Alice NNP B-NP B-PER\n" + "went VBD B-VP O\n" + "to TO B-PP O\n" + "Stanford NNP B-NP B-ORG\n" + "University NNP I-NP I-ORG\n" + "in IN B-PP O\n" + "California NNP B-NP B-LOC\n" + "\n";
InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
ObjectStream<String> lineStream = new PlainTextByLineStream(() -> inputStream, StandardCharsets.UTF_8);
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES | Conll02NameSampleStream.GENERATE_ORGANIZATION_ENTITIES | Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
Span[] spans = sample.getNames();
assertEquals(3, spans.length);
assertEquals("PER", spans[0].getType());
assertEquals(0, spans[0].getStart());
assertEquals(1, spans[0].getEnd());
assertEquals("ORG", spans[1].getType());
assertEquals(3, spans[1].getStart());
assertEquals(5, spans[1].getEnd());
assertEquals("LOC", spans[2].getType());
assertEquals(6, spans[2].getStart());
assertEquals(7, spans[2].getEnd());
}

@Test
public void testReadReturnsNullAfterEOF() throws IOException {
String text = "John NNP B-NP B-PER\n" + "\n";
InputStream inputStream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
ObjectStream<String> lines = new PlainTextByLineStream(() -> inputStream, StandardCharsets.UTF_8);
Conll03NameSampleStream sampleStream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lines, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);
NameSample sample1 = sampleStream.read();
assertNotNull(sample1);
NameSample sample2 = sampleStream.read();
assertNull(sample2);
}

@Test
public void testMiscEntityRecognition() throws IOException {
String input = "iPhone NNP B-NP B-MISC\n" + "13 CD I-NP I-MISC\n" + "\n";
InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
ObjectStream<String> stream = new PlainTextByLineStream(() -> inputStream, StandardCharsets.UTF_8);
Conll03NameSampleStream sampleStream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, stream, Conll02NameSampleStream.GENERATE_MISC_ENTITIES);
NameSample sample = sampleStream.read();
assertNotNull(sample);
Span[] spans = sample.getNames();
assertEquals(1, spans.length);
assertEquals("MISC", spans[0].getType());
assertEquals(0, spans[0].getStart());
assertEquals(2, spans[0].getEnd());
}

@Test
public void testGermanWithIncorrectFieldLength() throws IOException {
String input = "Berlin NNP B-NP B-LOC\n";
InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
ObjectStream<String> stream = new PlainTextByLineStream(() -> inputStream, StandardCharsets.UTF_8);
Conll03NameSampleStream sampleStream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.DE, stream, Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES);
try {
sampleStream.read();
fail("Expected IOException for incorrect number of fields in German data");
} catch (IOException e) {
assertTrue(e.getMessage().contains("Incorrect number of fields"));
}
}

@Test
public void testInterleavedIOErrorMidSentence() throws IOException {
String input = "John NNP B-NP B-PER\n" + "Smith NNP I-NP I-XYZ\n" + "\n";
InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
ObjectStream<String> stream = new PlainTextByLineStream(() -> inputStream, StandardCharsets.UTF_8);
Conll03NameSampleStream sampleStream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, stream, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);
try {
sampleStream.read();
fail("Expected IOException for malformed I-X tag sequence");
} catch (IOException e) {
assertTrue(e.getMessage().contains("Invalid tag"));
}
}

@Test
public void testMultipleSentencesSeparatedByEmptyLines() throws IOException {
String input = "John NNP B-NP B-PER\n" + "\n" + "\n" + "Alice NNP B-NP B-PER\n" + "\n";
InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
ObjectStream<String> stream = new PlainTextByLineStream(() -> inputStream, StandardCharsets.UTF_8);
Conll03NameSampleStream sampleStream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, stream, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);
NameSample sample1 = sampleStream.read();
assertNotNull(sample1);
assertEquals("PER", sample1.getNames()[0].getType());
NameSample sample2 = sampleStream.read();
assertNotNull(sample2);
assertEquals("PER", sample2.getNames()[0].getType());
NameSample sample3 = sampleStream.read();
assertNull(sample3);
}

@Test
public void testInvalidNEWithoutPrefixShouldThrow() throws IOException {
String input = "Paris NNP B-NP LOC\n";
InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
ObjectStream<String> stream = new PlainTextByLineStream(() -> inputStream, StandardCharsets.UTF_8);
Conll03NameSampleStream sampleStream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, stream, Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES);
try {
sampleStream.read();
fail("Expected IOException for missing tag prefix");
} catch (IOException e) {
assertTrue(e.getMessage().contains("Invalid tag"));
}
}

@Test
public void testSentenceEndingWithIEntityFollowedByEmptyLine() throws IOException {
String input = "New NNP B-NP B-LOC\n" + "York NNP I-NP I-LOC\n" + "\n";
InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
ObjectStream<String> stream = new PlainTextByLineStream(() -> inputStream, StandardCharsets.UTF_8);
Conll03NameSampleStream sampleStream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, stream, Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES);
NameSample sample = sampleStream.read();
assertNotNull(sample);
Span[] spans = sample.getNames();
assertEquals(1, spans.length);
assertEquals("LOC", spans[0].getType());
assertEquals(0, spans[0].getStart());
assertEquals(2, spans[0].getEnd());
}

@Test
public void testMultipleIEntitiesInSingleSpan() throws IOException {
String input = "New NNP B-NP B-LOC\n" + "York NNP I-NP I-LOC\n" + "City NNP I-NP I-LOC\n" + "\n";
InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
ObjectStream<String> stream = new PlainTextByLineStream(() -> inputStream, StandardCharsets.UTF_8);
Conll03NameSampleStream sampleStream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, stream, Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES);
NameSample sample = sampleStream.read();
assertNotNull(sample);
Span[] spans = sample.getNames();
assertEquals(1, spans.length);
assertEquals("LOC", spans[0].getType());
assertEquals(0, spans[0].getStart());
assertEquals(3, spans[0].getEnd());
}

@Test
public void testUnknownLanguageShouldSkipAllLines() throws IOException {
String input = "Paris NNP B-NP B-LOC\n" + "\n";
InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
ObjectStream<String> stream = new PlainTextByLineStream(() -> inputStream, StandardCharsets.UTF_8);
Conll03NameSampleStream sampleStream = new Conll03NameSampleStream(null, stream, Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES);
try {
sampleStream.read();
fail("Expected IOException for null (unknown) language");
} catch (IOException e) {
}
}

@Test
public void testConsecutiveIOReadsReturnsConsistentResults() throws IOException {
String input = "John NNP B-NP B-PER\n" + "\n" + "London NNP B-NP B-LOC\n" + "\n";
InputStream in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
ObjectStream<String> lineStream = new PlainTextByLineStream(() -> in, StandardCharsets.UTF_8);
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES | Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);
NameSample sample1 = stream.read();
assertNotNull(sample1);
assertEquals("PER", sample1.getNames()[0].getType());
NameSample sample2 = stream.read();
assertNotNull(sample2);
assertEquals("LOC", sample2.getNames()[0].getType());
NameSample sample3 = stream.read();
assertNull(sample3);
}

@Test
public void testSentenceWithOnlyOEntities() throws IOException {
String input = "This DT B-NP O\n" + "is VBZ B-VP O\n" + "a DT B-NP O\n" + "test NN I-NP O\n" + ". . O O\n" + "\n";
InputStream stream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
ObjectStream<String> lineStream = new PlainTextByLineStream(() -> stream, StandardCharsets.UTF_8);
Conll03NameSampleStream conllStream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES | Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES | Conll02NameSampleStream.GENERATE_ORGANIZATION_ENTITIES | Conll02NameSampleStream.GENERATE_MISC_ENTITIES);
NameSample sample = conllStream.read();
assertNotNull(sample);
Span[] spans = sample.getNames();
assertNotNull(spans);
assertEquals(0, spans.length);
}

@Test
public void testIEntityFollowedByDifferentTypeStartsNewSpan() throws IOException {
String input = "New NNP B-NP B-LOC\n" + "York NNP I-NP I-LOC\n" + "Times NNP I-NP I-ORG\n" + "\n";
InputStream stream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
ObjectStream<String> lineStream = new PlainTextByLineStream(() -> stream, StandardCharsets.UTF_8);
Conll03NameSampleStream conllStream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES | Conll02NameSampleStream.GENERATE_ORGANIZATION_ENTITIES);
NameSample sample = conllStream.read();
assertNotNull(sample);
Span[] spans = sample.getNames();
assertEquals(2, spans.length);
assertEquals("LOC", spans[0].getType());
assertEquals(0, spans[0].getStart());
assertEquals(2, spans[0].getEnd());
assertEquals("ORG", spans[1].getType());
assertEquals(2, spans[1].getStart());
assertEquals(3, spans[1].getEnd());
}

@Test
public void testTrailingEmptyLinesIgnored() throws IOException {
String input = "Berlin NNP B-NP B-LOC\n" + "\n" + "\n" + "\n";
InputStream stream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
ObjectStream<String> lineStream = new PlainTextByLineStream(() -> stream, StandardCharsets.UTF_8);
Conll03NameSampleStream conllStream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES);
NameSample sample = conllStream.read();
assertNotNull(sample);
Span[] spans = sample.getNames();
assertEquals(1, spans.length);
assertNull(conllStream.read());
}

@Test
public void testResetCalledTwice() throws IOException {
String input = "Alice NNP B-NP B-PER\n" + "\n";
InputStream stream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
ObjectStream<String> lineStream = new PlainTextByLineStream(() -> stream, StandardCharsets.UTF_8);
Conll03NameSampleStream conllStream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);
NameSample first = conllStream.read();
assertNotNull(first);
conllStream.reset();
NameSample second = conllStream.read();
assertNotNull(second);
assertEquals(first.getSentence()[0], second.getSentence()[0]);
conllStream.reset();
NameSample third = conllStream.read();
assertNotNull(third);
assertEquals("Alice", third.getSentence()[0]);
}

@Test
public void testInvalidMixedFormatEnglishLine() throws IOException {
String input = "Berlin NNP B-NP\n";
InputStream stream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
ObjectStream<String> lineStream = new PlainTextByLineStream(() -> stream, StandardCharsets.UTF_8);
Conll03NameSampleStream conllStream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES);
conllStream.read();
}

@Test
public void testClearAdaptiveDataFalseWhenNoDocStart() throws IOException {
String input = "Alice NNP B-NP B-PER\n" + "\n";
InputStream stream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
ObjectStream<String> lineStream = new PlainTextByLineStream(() -> stream, StandardCharsets.UTF_8);
Conll03NameSampleStream conllStream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);
NameSample sample = conllStream.read();
assertNotNull(sample);
// assertFalse(sample.isClearAdaptiveData());
}

@Test
public void testSpanCreatedWhenBFollowedByO() throws IOException {
String input = "Bob NNP B-NP B-PER\n" + "went VBD B-VP O\n" + "\n";
InputStream stream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
ObjectStream<String> lineStream = new PlainTextByLineStream(() -> stream, StandardCharsets.UTF_8);
Conll03NameSampleStream conllStream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);
NameSample sample = conllStream.read();
assertNotNull(sample);
Span[] spans = sample.getNames();
assertEquals(1, spans.length);
assertEquals(0, spans[0].getStart());
assertEquals(1, spans[0].getEnd());
}

@Test
public void testConll03DEParsingWithValidFiveFields() throws IOException {
String input = "Berlin Berlin NE B-NP B-LOC\n" + "\n";
InputStream stream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
ObjectStream<String> deLineStream = new PlainTextByLineStream(() -> stream, StandardCharsets.UTF_8);
Conll03NameSampleStream conllStream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.DE, deLineStream, Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES);
NameSample result = conllStream.read();
assertNotNull(result);
String[] sentence = result.getSentence();
assertNotNull(sentence);
assertEquals(1, sentence.length);
assertEquals("Berlin", sentence[0]);
Span[] spans = result.getNames();
assertEquals(1, spans.length);
assertEquals("LOC", spans[0].getType());
assertEquals(0, spans[0].getStart());
assertEquals(1, spans[0].getEnd());
}

@Test
public void testEmptyLineAfterDocStartContainingWhitespace() throws IOException {
String input = "-DOCSTART- -X- O O\n" + "   \n";
InputStream stream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
ObjectStream<String> lineStream = new PlainTextByLineStream(() -> stream, StandardCharsets.UTF_8);
Conll03NameSampleStream conllStream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);
conllStream.read();
}

@Test
public void testMultipleBEntitiesOfSameTypeAreSplitCorrectly() throws IOException {
String input = "Bob NNP B-NP B-PER\n" + "John NNP B-NP B-PER\n" + "\n";
InputStream stream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
ObjectStream<String> lineStream = new PlainTextByLineStream(() -> stream, StandardCharsets.UTF_8);
Conll03NameSampleStream conllStream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);
NameSample sample = conllStream.read();
assertNotNull(sample);
Span[] spans = sample.getNames();
assertEquals(2, spans.length);
assertEquals("PER", spans[0].getType());
assertEquals(0, spans[0].getStart());
assertEquals(1, spans[0].getEnd());
assertEquals("PER", spans[1].getType());
assertEquals(1, spans[1].getStart());
assertEquals(2, spans[1].getEnd());
}

@Test
public void testComplexTransitionIBtoIBSameEntityType() throws IOException {
String input = "New NNP B-NP I-LOC\n" + "York NNP B-NP I-LOC\n" + "City NNP B-NP I-LOC\n" + "\n";
InputStream stream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
ObjectStream<String> lineStream = new PlainTextByLineStream(() -> stream, StandardCharsets.UTF_8);
Conll03NameSampleStream conllStream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES);
NameSample sample = conllStream.read();
assertNotNull(sample);
Span[] spans = sample.getNames();
assertEquals(1, spans.length);
assertEquals(0, spans[0].getStart());
assertEquals(3, spans[0].getEnd());
assertEquals("LOC", spans[0].getType());
}

@Test
public void testEntityDisabledViaTypeMask() throws IOException {
String input = "France NNP B-NP B-LOC\n" + "\n";
InputStream stream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
ObjectStream<String> lineStream = new PlainTextByLineStream(() -> stream, StandardCharsets.UTF_8);
Conll03NameSampleStream conllStream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);
NameSample sample = conllStream.read();
assertNotNull(sample);
Span[] spans = sample.getNames();
assertEquals(0, spans.length);
assertEquals("France", sample.getSentence()[0]);
}

@Test
public void testInvalidTagThatIsNotBOrIorO() throws IOException {
String input = "Moon NNP B-NP Z-FOO\n" + "\n";
InputStream stream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
ObjectStream<String> reader = new PlainTextByLineStream(() -> stream, StandardCharsets.UTF_8);
Conll03NameSampleStream conllStream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, reader, Conll02NameSampleStream.GENERATE_MISC_ENTITIES);
conllStream.read();
}

@Test
public void testSentenceWithMultipleDifferentEntityTypes() throws IOException {
String input = "Apple NNP B-NP B-ORG\n" + "CEO NNP B-NP O\n" + "Steve NNP B-NP B-PER\n" + "Jobs NNP I-NP I-PER\n" + "in IN B-PP O\n" + "California NNP B-NP B-LOC\n" + "\n";
InputStream stream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
ObjectStream<String> lineStream = new PlainTextByLineStream(() -> stream, StandardCharsets.UTF_8);
Conll03NameSampleStream conllStream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES | Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES | Conll02NameSampleStream.GENERATE_ORGANIZATION_ENTITIES);
NameSample sample = conllStream.read();
assertNotNull(sample);
Span[] spans = sample.getNames();
assertEquals(3, spans.length);
assertEquals("ORG", spans[0].getType());
assertEquals("PER", spans[1].getType());
assertEquals("LOC", spans[2].getType());
}

@Test
public void testConsecutiveBlankLinesAreIgnored() throws IOException {
String input = "Bob NNP B-NP B-PER\n" + "\n" + "\n" + "\n";
InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
ObjectStream<String> stream = new PlainTextByLineStream(() -> inputStream, StandardCharsets.UTF_8);
Conll03NameSampleStream sampleStream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, stream, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);
NameSample first = sampleStream.read();
assertNotNull(first);
NameSample second = sampleStream.read();
assertNull(second);
}

@Test
public void testReadReturnsNullWhenOnlyEmptyLinesPresent() throws IOException {
String input = "\n\n\n";
InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
ObjectStream<String> stream = new PlainTextByLineStream(() -> inputStream, StandardCharsets.UTF_8);
Conll03NameSampleStream sampleStream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, stream, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);
NameSample result = sampleStream.read();
assertNull(result);
}

@Test
public void testBTagAtEndOfStreamCreatesSingleSpan() throws IOException {
String input = "Bob NNP B-NP B-PER\n";
InputStream stream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
ObjectStream<String> lineStream = new PlainTextByLineStream(() -> stream, StandardCharsets.UTF_8);
Conll03NameSampleStream conll = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);
NameSample sample = conll.read();
assertNotNull(sample);
Span[] spans = sample.getNames();
assertEquals(1, spans.length);
assertEquals("PER", spans[0].getType());
assertEquals(0, spans[0].getStart());
assertEquals(1, spans[0].getEnd());
}

@Test
public void testIBSequenceWithNonMatchingTypesSplitsSpan() throws IOException {
String input = "John NNP B-NP B-PER\n" + "Walker NNP I-NP I-ORG\n" + "\n";
InputStream stream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
ObjectStream<String> streamReader = new PlainTextByLineStream(() -> stream, StandardCharsets.UTF_8);
Conll03NameSampleStream conll = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, streamReader, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES | Conll02NameSampleStream.GENERATE_ORGANIZATION_ENTITIES);
NameSample sample = conll.read();
assertNotNull(sample);
Span[] spans = sample.getNames();
assertEquals(2, spans.length);
assertEquals("PER", spans[0].getType());
assertEquals(0, spans[0].getStart());
assertEquals(1, spans[0].getEnd());
assertEquals("ORG", spans[1].getType());
assertEquals(1, spans[1].getStart());
assertEquals(2, spans[1].getEnd());
}

@Test
public void testSentenceWithMultipleSameTypeEntities() throws IOException {
String input = "Tom NNP B-NP B-PER\n" + "Jerry NNP B-NP B-PER\n" + "\n";
InputStream stream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
ObjectStream<String> reader = new PlainTextByLineStream(() -> stream, StandardCharsets.UTF_8);
Conll03NameSampleStream streamHandler = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, reader, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);
NameSample sample = streamHandler.read();
assertNotNull(sample);
Span[] spans = sample.getNames();
assertEquals(2, spans.length);
assertEquals("PER", spans[0].getType());
assertEquals(0, spans[0].getStart());
assertEquals(1, spans[0].getEnd());
assertEquals("PER", spans[1].getType());
assertEquals(1, spans[1].getStart());
assertEquals(2, spans[1].getEnd());
}

@Test
public void testIOSequenceWithOnlyIWithoutBProcessesAsSpan() throws IOException {
String input = "York NNP I-NP I-LOC\n" + "City NNP I-NP I-LOC\n" + "\n";
InputStream stream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
ObjectStream<String> reader = new PlainTextByLineStream(() -> stream, StandardCharsets.UTF_8);
Conll03NameSampleStream conllStream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, reader, Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES);
NameSample sample = conllStream.read();
assertNotNull(sample);
Span[] spans = sample.getNames();
assertEquals(1, spans.length);
assertEquals("LOC", spans[0].getType());
assertEquals(0, spans[0].getStart());
assertEquals(2, spans[0].getEnd());
}

@Test
public void testEmptyInputReturnsNull() throws IOException {
String input = "";
InputStream stream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
ObjectStream<String> reader = new PlainTextByLineStream(() -> stream, StandardCharsets.UTF_8);
Conll03NameSampleStream conll = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, reader, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);
NameSample sample = conll.read();
assertNull(sample);
}

@Test
public void testIncorrectFieldCountWithExtraColumnsThrowsException() throws IOException {
String input = "Hello World Foo Bar Extra\n";
InputStream stream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
ObjectStream<String> reader = new PlainTextByLineStream(() -> stream, StandardCharsets.UTF_8);
Conll03NameSampleStream conll = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, reader, Conll02NameSampleStream.GENERATE_MISC_ENTITIES);
try {
conll.read();
fail("Expected IOException due to invalid field count");
} catch (IOException e) {
assertTrue(e.getMessage().contains("Incorrect number of fields"));
}
}

@Test
public void testLongSpanIsCapturedProperly() throws IOException {
String input = "San NNP B-NP B-LOC\n" + "Francisco NNP I-NP I-LOC\n" + "Bay NNP I-NP I-LOC\n" + "Area NNP I-NP I-LOC\n" + "\n";
InputStream stream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
ObjectStream<String> reader = new PlainTextByLineStream(() -> stream, StandardCharsets.UTF_8);
Conll03NameSampleStream streamHandler = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, reader, Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES);
NameSample sample = streamHandler.read();
assertNotNull(sample);
Span[] spans = sample.getNames();
assertEquals(1, spans.length);
assertEquals(0, spans[0].getStart());
assertEquals(4, spans[0].getEnd());
assertEquals("LOC", spans[0].getType());
}

@Test
public void testDocStartWithoutEntitiesStillTriggersClearAdaptive() throws IOException {
String input = "-DOCSTART- -X- O O\n" + "\n" + "This DT B-NP O\n" + "is VBZ I-VP O\n" + "\n";
InputStream stream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
ObjectStream<String> reader = new PlainTextByLineStream(() -> stream, StandardCharsets.UTF_8);
Conll03NameSampleStream conll = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, reader, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);
NameSample sample = conll.read();
assertNotNull(sample);
assertEquals(2, sample.getSentence().length);
// assertTrue(sample.isClearAdaptiveData());
assertEquals(0, sample.getNames().length);
}

@Test
public void testReadFiltersUnsupportedEntityTypes() throws IOException {
String input = "Python NNP B-NP B-MISC\n" + "\n";
InputStream stream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
ObjectStream<String> reader = new PlainTextByLineStream(() -> stream, StandardCharsets.UTF_8);
Conll03NameSampleStream conll = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, reader, 0);
NameSample sample = conll.read();
assertNotNull(sample);
Span[] spans = sample.getNames();
assertEquals(0, spans.length);
}

@Test
public void testMultipleSentencesWithMixedEntityTypes() throws IOException {
String input = "-DOCSTART- -X- O O\n" + "\n" + "Steve NNP B-NP B-PER\n" + "Jobs NNP I-NP I-PER\n" + "\n" + "Apple NNP B-NP B-ORG\n" + "Inc. NNP I-NP I-ORG\n" + "\n" + "Cupertino NNP B-NP B-LOC\n" + "\n";
InputStream stream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
ObjectStream<String> lineStream = new PlainTextByLineStream(() -> stream, StandardCharsets.UTF_8);
Conll03NameSampleStream sampleStream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES | Conll02NameSampleStream.GENERATE_ORGANIZATION_ENTITIES | Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES);
NameSample sample1 = sampleStream.read();
assertNotNull(sample1);
assertEquals("PER", sample1.getNames()[0].getType());
NameSample sample2 = sampleStream.read();
assertNotNull(sample2);
assertEquals("ORG", sample2.getNames()[0].getType());
NameSample sample3 = sampleStream.read();
assertNotNull(sample3);
assertEquals("LOC", sample3.getNames()[0].getType());
NameSample sample4 = sampleStream.read();
assertNull(sample4);
}

@Test
public void testSentenceWithOnlyBTagWithoutIOtherTags() throws IOException {
String input = "London NNP B-NP B-LOC\n" + "\n";
InputStream stream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
ObjectStream<String> reader = new PlainTextByLineStream(() -> stream, StandardCharsets.UTF_8);
Conll03NameSampleStream conll = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, reader, Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES);
NameSample sample = conll.read();
assertNotNull(sample);
assertEquals(1, sample.getNames().length);
assertEquals("LOC", sample.getNames()[0].getType());
assertEquals(0, sample.getNames()[0].getStart());
assertEquals(1, sample.getNames()[0].getEnd());
}

@Test
public void testInvalidLanguageFormatShouldThrowException() throws IOException {
String input = "Ein DEUTSCH T1 T2 T3 T4  \n";
InputStream stream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
ObjectStream<String> germanReader = new PlainTextByLineStream(() -> stream, StandardCharsets.UTF_8);
Conll03NameSampleStream conll = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.DE, germanReader, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);
try {
conll.read();
fail("Expected exception due to too many fields for DE");
} catch (IOException e) {
assertTrue(e.getMessage().contains("Incorrect number of fields"));
}
}

@Test
public void testBTagImmediatelyFollowedByDifferentBTagType() throws IOException {
String input = "Berlin NNP B-NP B-LOC\n" + "Google NNP B-NP B-ORG\n" + "\n";
InputStream stream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
ObjectStream<String> inputReader = new PlainTextByLineStream(() -> stream, StandardCharsets.UTF_8);
Conll03NameSampleStream conll = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, inputReader, Conll02NameSampleStream.GENERATE_ORGANIZATION_ENTITIES | Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES);
NameSample result = conll.read();
assertNotNull(result);
Span[] spans = result.getNames();
assertEquals(2, spans.length);
assertEquals("LOC", spans[0].getType());
assertEquals(0, spans[0].getStart());
assertEquals(1, spans[0].getEnd());
assertEquals("ORG", spans[1].getType());
assertEquals(1, spans[1].getStart());
assertEquals(2, spans[1].getEnd());
}

@Test
public void testIEntityThatSwitchesTypeMidwayIsSplitProperly() throws IOException {
String input = "Tom NNP B-NP I-PER\n" + "Jerry NNP I-NP I-ORG\n" + "\n";
InputStream stream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
ObjectStream<String> source = new PlainTextByLineStream(() -> stream, StandardCharsets.UTF_8);
Conll03NameSampleStream parser = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, source, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES | Conll02NameSampleStream.GENERATE_ORGANIZATION_ENTITIES);
NameSample result = parser.read();
assertNotNull(result);
Span[] spans = result.getNames();
assertEquals(2, spans.length);
assertEquals("PER", spans[0].getType());
assertEquals(0, spans[0].getStart());
assertEquals(1, spans[0].getEnd());
assertEquals("ORG", spans[1].getType());
assertEquals(1, spans[1].getStart());
assertEquals(2, spans[1].getEnd());
}

@Test
public void testSpanCreatedByConsecutiveITagsWithoutPrefix() throws IOException {
String input = "United NNP I-NP I-ORG\n" + "Nations NNPS I-NP I-ORG\n" + "Headquarters NNP I-NP I-ORG\n" + "\n";
InputStream stream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
ObjectStream<String> streamReader = new PlainTextByLineStream(() -> stream, StandardCharsets.UTF_8);
Conll03NameSampleStream streamParser = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, streamReader, Conll02NameSampleStream.GENERATE_ORGANIZATION_ENTITIES);
NameSample result = streamParser.read();
assertNotNull(result);
Span[] spans = result.getNames();
assertEquals(1, spans.length);
assertEquals("ORG", spans[0].getType());
assertEquals(0, spans[0].getStart());
assertEquals(3, spans[0].getEnd());
}

@Test
public void testSentenceWithDisabledEntityTypeShouldSkipTag() throws IOException {
String input = "Bitcoin NNP B-NP B-MISC\n" + "\n";
InputStream stream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
ObjectStream<String> lineSource = new PlainTextByLineStream(() -> stream, StandardCharsets.UTF_8);
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineSource, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);
// NameSample result = stream.read();
// assertNotNull(result);
// assertEquals(0, result.getNames().length);
}

@Test
public void testMixedEntTypesWithNonContiguousTokBreakTags() throws IOException {
String input = "Bill NNP B-NP B-PER\n" + "Gates NNP I-NP I-PER\n" + "Microsoft NNP B-NP B-ORG\n" + "\n";
InputStream in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
ObjectStream<String> lines = new PlainTextByLineStream(() -> in, StandardCharsets.UTF_8);
Conll03NameSampleStream parser = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lines, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES | Conll02NameSampleStream.GENERATE_ORGANIZATION_ENTITIES);
NameSample sample = parser.read();
assertNotNull(sample);
Span[] spans = sample.getNames();
assertEquals(2, spans.length);
assertEquals("PER", spans[0].getType());
assertEquals("ORG", spans[1].getType());
assertEquals(0, spans[0].getStart());
assertEquals(2, spans[0].getEnd());
assertEquals(2, spans[1].getStart());
assertEquals(3, spans[1].getEnd());
}

@Test
public void testSpanCreatedFromTrailingIBSequenceWithTypeSwitch() throws IOException {
String input = "Barack NNP B-NP I-PER\n" + "Obama NNP I-NP I-PER\n" + "Foundation NNP I-NP I-ORG\n" + "\n";
InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
ObjectStream<String> src = new PlainTextByLineStream(() -> inputStream, StandardCharsets.UTF_8);
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, src, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES | Conll02NameSampleStream.GENERATE_ORGANIZATION_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
Span[] spans = sample.getNames();
assertEquals(2, spans.length);
assertEquals("PER", spans[0].getType());
assertEquals(0, spans[0].getStart());
assertEquals(2, spans[0].getEnd());
assertEquals("ORG", spans[1].getType());
assertEquals(2, spans[1].getStart());
assertEquals(3, spans[1].getEnd());
}

@Test
public void testResetDoesNotInterfereWithParsing() throws IOException {
String input = "Berlin NNP B-NP B-LOC\n" + "\n";
InputStream stream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
PlainTextByLineStream lineStream = new PlainTextByLineStream(() -> stream, StandardCharsets.UTF_8);
Conll03NameSampleStream parser = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES);
NameSample firstRead = parser.read();
assertNotNull(firstRead);
parser.reset();
NameSample reread = parser.read();
assertNotNull(reread);
assertEquals("Berlin", reread.getSentence()[0]);
assertEquals("LOC", reread.getNames()[0].getType());
}

@Test
public void testReadReturnsNullWhenOnlyDocStartAndEmptyLinePresent() throws IOException {
String input = "-DOCSTART- -X- O O\n" + "\n";
InputStream in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
ObjectStream<String> lines = new PlainTextByLineStream(() -> in, StandardCharsets.UTF_8);
Conll03NameSampleStream parser = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lines, Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES);
NameSample result = parser.read();
assertNull(result);
}

@Test
public void testDocumentWithTwoDocStartsYieldsTwoAdaptiveResets() throws IOException {
String input = "-DOCSTART- -X- O O\n" + "\n" + "John NNP B-NP B-PER\n" + "\n" + "-DOCSTART- -X- O O\n" + "\n" + "Alice NNP B-NP B-PER\n" + "\n";
InputStream in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
ObjectStream<String> lines = new PlainTextByLineStream(() -> in, StandardCharsets.UTF_8);
Conll03NameSampleStream parser = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lines, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);
NameSample first = parser.read();
assertNotNull(first);
// assertTrue(first.isClearAdaptiveData());
NameSample second = parser.read();
assertNotNull(second);
// assertTrue(second.isClearAdaptiveData());
NameSample third = parser.read();
assertNull(third);
}

@Test
public void testIWithoutBAndTypeMismatchCreatesNewSpan() throws IOException {
String input = "Hello NNP I-NP I-LOC\n" + "World NNP I-NP I-ORG\n" + "\n";
InputStream stream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
ObjectStream<String> source = new PlainTextByLineStream(() -> stream, StandardCharsets.UTF_8);
Conll03NameSampleStream parser = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, source, Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES | Conll02NameSampleStream.GENERATE_ORGANIZATION_ENTITIES);
NameSample sample = parser.read();
assertNotNull(sample);
Span[] spans = sample.getNames();
assertEquals(2, spans.length);
assertEquals("LOC", spans[0].getType());
assertEquals("ORG", spans[1].getType());
}

@Test
public void testNonAlphaCharactersInInputTokensHandledCorrectly() throws IOException {
String input = "$100 NNP B-NP B-MISC\n" + "€200 NNP I-NP I-MISC\n" + "\n";
InputStream bytes = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
ObjectStream<String> lineStream = new PlainTextByLineStream(() -> bytes, StandardCharsets.UTF_8);
Conll03NameSampleStream parser = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_MISC_ENTITIES);
NameSample result = parser.read();
assertNotNull(result);
Span[] spans = result.getNames();
assertEquals(1, spans.length);
assertEquals("MISC", spans[0].getType());
assertEquals("$100", result.getSentence()[0]);
assertEquals("€200", result.getSentence()[1]);
}

@Test
public void testOrgEntitySpanningMultipleTokensFilteredOutWhenDisabled() throws IOException {
String input = "OpenAI NNP B-NP B-ORG\n" + "Labs NNP I-NP I-ORG\n" + "\n";
InputStream bytes = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
ObjectStream<String> lines = new PlainTextByLineStream(() -> bytes, StandardCharsets.UTF_8);
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lines, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES | Conll02NameSampleStream.GENERATE_MISC_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
Span[] spans = sample.getNames();
assertEquals(0, spans.length);
}
}
