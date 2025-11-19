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

public class Conll03NameSampleStream_llmsuite_1_GPTLLMTest {

@Test
public void testEnglishSinglePersonEntity() throws IOException {
ObjectStream<String> lineStream = new ObjectStream<String>() {

BufferedReader reader = new BufferedReader(new StringReader("-DOCSTART- -X- -X- O\n" + "\n" + "John NNP B-NP B-PER\n" + "Smith NNP I-NP I-PER\n" + "\n"));

public String read() throws IOException {
return reader.readLine();
}

public void reset() throws IOException {
reader = new BufferedReader(new StringReader("-DOCSTART- -X- -X- O\n" + "\n" + "John NNP B-NP B-PER\n" + "Smith NNP I-NP I-PER\n" + "\n"));
}

public void close() throws IOException {
reader.close();
}
};
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES | Conll02NameSampleStream.GENERATE_ORGANIZATION_ENTITIES | Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES | Conll02NameSampleStream.GENERATE_MISC_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
assertArrayEquals(new String[] { "John", "Smith" }, sample.getSentence());
Span[] names = sample.getNames();
assertEquals(1, names.length);
assertEquals(0, names[0].getStart());
assertEquals(2, names[0].getEnd());
assertEquals("PER", names[0].getType());
// assertTrue(sample.isClearAdaptiveData());
}

@Test
public void testGermanSingleLocationEntity() throws IOException {
ObjectStream<String> lineStream = new ObjectStream<String>() {

BufferedReader reader = new BufferedReader(new StringReader("-DOCSTART- -X- -X- -X- O\n" + "\n" + "Berlin lemma1 NN B-NP B-LOC\n" + "\n"));

public String read() throws IOException {
return reader.readLine();
}

public void reset() throws IOException {
reader = new BufferedReader(new StringReader("-DOCSTART- -X- -X- -X- O\n" + "\n" + "Berlin lemma1 NN B-NP B-LOC\n" + "\n"));
}

public void close() throws IOException {
reader.close();
}
};
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.DE, lineStream, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES | Conll02NameSampleStream.GENERATE_ORGANIZATION_ENTITIES | Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES | Conll02NameSampleStream.GENERATE_MISC_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
assertArrayEquals(new String[] { "Berlin" }, sample.getSentence());
Span[] spans = sample.getNames();
assertEquals(1, spans.length);
assertEquals(0, spans[0].getStart());
assertEquals(1, spans[0].getEnd());
assertEquals("LOC", spans[0].getType());
// assertTrue(sample.isClearAdaptiveData());
}

@Test
public void testHandlesEmptySentence() throws IOException {
ObjectStream<String> lineStream = new ObjectStream<String>() {

BufferedReader reader = new BufferedReader(new StringReader("\n" + "\n" + "John NNP B-NP B-PER\n" + "\n"));

public String read() throws IOException {
return reader.readLine();
}

public void reset() throws IOException {
reader = new BufferedReader(new StringReader("\n" + "\n" + "John NNP B-NP B-PER\n" + "\n"));
}

public void close() throws IOException {
reader.close();
}
};
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES | Conll02NameSampleStream.GENERATE_ORGANIZATION_ENTITIES | Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES | Conll02NameSampleStream.GENERATE_MISC_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
assertEquals("John", sample.getSentence()[0]);
assertEquals(1, sample.getSentence().length);
assertEquals(1, sample.getNames().length);
assertEquals("PER", sample.getNames()[0].getType());
}

@Test
public void testInvalidFieldCountEnglish() throws IOException {
ObjectStream<String> lineStream = new ObjectStream<String>() {

BufferedReader reader = new BufferedReader(new StringReader("BrokenLineWithoutEnoughFields\n" + "\n"));

public String read() throws IOException {
return reader.readLine();
}

public void reset() throws IOException {
reader = new BufferedReader(new StringReader("BrokenLineWithoutEnoughFields\n" + "\n"));
}

public void close() throws IOException {
reader.close();
}
};
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);
stream.read();
}

@Test
public void testDocStartWithoutEmptyLineThrows() throws IOException {
ObjectStream<String> lineStream = new ObjectStream<String>() {

BufferedReader reader = new BufferedReader(new StringReader("-DOCSTART- -X- -X- O\n" + "NotEmpty\n"));

public String read() throws IOException {
return reader.readLine();
}

public void reset() throws IOException {
reader = new BufferedReader(new StringReader("-DOCSTART- -X- -X- O\n" + "NotEmpty\n"));
}

public void close() throws IOException {
reader.close();
}
};
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);
stream.read();
}

@Test
public void testResetAndReRead() throws IOException {
String input = "-DOCSTART- -X- -X- O\n" + "\n" + "John NNP B-NP B-PER\n" + "Smith NNP I-NP I-PER\n" + "\n";
final byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);
InputStreamFactory factory = new InputStreamFactory() {

public InputStream createInputStream() {
return new ByteArrayInputStream(inputBytes);
}
};
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, factory, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);
NameSample firstSample = stream.read();
stream.reset();
NameSample secondSample = stream.read();
assertNotNull(firstSample);
assertNotNull(secondSample);
assertArrayEquals(firstSample.getSentence(), secondSample.getSentence());
Span[] firstSpans = firstSample.getNames();
Span[] secondSpans = secondSample.getNames();
assertEquals(firstSpans.length, secondSpans.length);
assertEquals(firstSpans[0].getStart(), secondSpans[0].getStart());
assertEquals(firstSpans[0].getEnd(), secondSpans[0].getEnd());
assertEquals(firstSpans[0].getType(), secondSpans[0].getType());
}

@Test
public void testSingleOtagTokenSentence() throws IOException {
ObjectStream<String> lineStream = new ObjectStream<String>() {

BufferedReader reader = new BufferedReader(new StringReader("Token NN B-NP O\n\n"));

public String read() throws IOException {
return reader.readLine();
}

public void reset() throws IOException {
}

public void close() throws IOException {
reader.close();
}
};
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
assertArrayEquals(new String[] { "Token" }, sample.getSentence());
assertEquals(0, sample.getNames().length);
}

@Test
public void testAllOtagSentenceNoEntitiesReturned() throws IOException {
ObjectStream<String> lineStream = new ObjectStream<String>() {

BufferedReader reader = new BufferedReader(new StringReader("This DT B-NP O\n" + "is VBZ B-VP O\n" + "a DT B-NP O\n" + "test NN I-NP O\n" + "\n"));

public String read() throws IOException {
return reader.readLine();
}

public void reset() throws IOException {
}

public void close() throws IOException {
reader.close();
}
};
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
assertArrayEquals(new String[] { "This", "is", "a", "test" }, sample.getSentence());
assertEquals(0, sample.getNames().length);
}

@Test
public void testStartsWithIWithoutB() throws IOException {
ObjectStream<String> lineStream = new ObjectStream<String>() {

BufferedReader reader = new BufferedReader(new StringReader("Paris NNP B-NP I-LOC\n" + "France NNP I-NP I-LOC\n" + "\n"));

public String read() throws IOException {
return reader.readLine();
}

public void reset() throws IOException {
}

public void close() throws IOException {
reader.close();
}
};
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
assertArrayEquals(new String[] { "Paris", "France" }, sample.getSentence());
Span[] names = sample.getNames();
assertEquals(1, names.length);
assertEquals("LOC", names[0].getType());
assertEquals(0, names[0].getStart());
assertEquals(2, names[0].getEnd());
}

@Test
public void testMismatchedIEntityBreaksAndStartsNew() throws IOException {
ObjectStream<String> lineStream = new ObjectStream<String>() {

BufferedReader reader = new BufferedReader(new StringReader("Angela NNP B-NP B-PER\n" + "Merkel NNP I-NP I-LOC\n" + "Berlin NNP I-NP I-LOC\n" + "\n"));

public String read() throws IOException {
return reader.readLine();
}

public void reset() throws IOException {
}

public void close() throws IOException {
reader.close();
}
};
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES | Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
assertArrayEquals(new String[] { "Angela", "Merkel", "Berlin" }, sample.getSentence());
Span[] names = sample.getNames();
assertEquals(2, names.length);
assertEquals("PER", names[0].getType());
assertEquals(0, names[0].getStart());
assertEquals(1, names[0].getEnd());
assertEquals("LOC", names[1].getType());
assertEquals(1, names[1].getStart());
assertEquals(3, names[1].getEnd());
}

@Test
public void testInvalidTagWithoutTypeThrowsIOException() throws IOException {
ObjectStream<String> lineStream = new ObjectStream<String>() {

BufferedReader reader = new BufferedReader(new StringReader("Invalid NNP B-NP B-\n" + "\n"));

public String read() throws IOException {
return reader.readLine();
}

public void reset() throws IOException {
}

public void close() throws IOException {
reader.close();
}
};
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);
stream.read();
}

@Test
public void testTagFilteringExcludesEntity() throws IOException {
ObjectStream<String> lineStream = new ObjectStream<String>() {

BufferedReader reader = new BufferedReader(new StringReader("OpenAI NNP B-NP B-ORG\n" + "\n"));

public String read() throws IOException {
return reader.readLine();
}

public void reset() throws IOException {
}

public void close() throws IOException {
reader.close();
}
};
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_MISC_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
assertArrayEquals(new String[] { "OpenAI" }, sample.getSentence());
assertEquals(0, sample.getNames().length);
}

@Test
public void testSentenceWithExtraSpacesBetweenFields() throws IOException {
ObjectStream<String> lineStream = new ObjectStream<String>() {

BufferedReader reader = new BufferedReader(new StringReader("OpenAI   NNP     B-NP     B-ORG\n" + "\n"));

public String read() throws IOException {
return reader.readLine();
}

public void reset() throws IOException {
}

public void close() throws IOException {
reader.close();
}
};
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_ORGANIZATION_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
assertArrayEquals(new String[] { "OpenAI" }, sample.getSentence());
Span[] spans = sample.getNames();
assertEquals(1, spans.length);
assertEquals("ORG", spans[0].getType());
assertEquals(0, spans[0].getStart());
assertEquals(1, spans[0].getEnd());
}

@Test
public void testEndOfStreamReturnsNull() throws IOException {
ObjectStream<String> lineStream = new ObjectStream<String>() {

boolean hasBeenRead = false;

public String read() {
if (!hasBeenRead) {
hasBeenRead = true;
return "John NNP B-NP B-PER";
}
return null;
}

public void reset() {
}

public void close() {
}
};
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);
NameSample first = stream.read();
NameSample second = stream.read();
assertNotNull(first);
assertNull(second);
}

@Test
public void testConsecutiveSameTypeEntities() throws IOException {
ObjectStream<String> lineStream = new ObjectStream<String>() {

BufferedReader reader = new BufferedReader(new StringReader("Alice NNP B-NP B-PER\n" + "works VBZ B-VP O\n" + "Bob NNP B-NP B-PER\n" + "\n"));

public String read() throws IOException {
return reader.readLine();
}

public void reset() {
}

public void close() {
}
};
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
assertArrayEquals(new String[] { "Alice", "works", "Bob" }, sample.getSentence());
Span[] names = sample.getNames();
assertEquals(2, names.length);
assertEquals(0, names[0].getStart());
assertEquals(1, names[0].getEnd());
assertEquals("PER", names[0].getType());
assertEquals(2, names[1].getStart());
assertEquals(3, names[1].getEnd());
assertEquals("PER", names[1].getType());
}

@Test
public void testConsecutiveDifferentTypeEntities() throws IOException {
ObjectStream<String> lineStream = new ObjectStream<String>() {

BufferedReader reader = new BufferedReader(new StringReader("Alice NNP B-NP B-PER\n" + "OpenAI NNP B-NP B-ORG\n" + "\n"));

public String read() throws IOException {
return reader.readLine();
}

public void reset() {
}

public void close() {
}
};
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES | Conll02NameSampleStream.GENERATE_ORGANIZATION_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
assertArrayEquals(new String[] { "Alice", "OpenAI" }, sample.getSentence());
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
public void testSentenceWithoutFinalNewline() throws IOException {
ObjectStream<String> lineStream = new ObjectStream<String>() {

BufferedReader reader = new BufferedReader(new StringReader("Alice NNP B-NP B-PER"));

public String read() throws IOException {
return reader.readLine();
}

public void reset() {
}

public void close() {
}
};
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
assertEquals(1, sample.getNames().length);
assertEquals("Alice", sample.getSentence()[0]);
}

@Test
public void testOFollowedByIIllegalTransitionCreatesNewSpan() throws IOException {
ObjectStream<String> lineStream = new ObjectStream<String>() {

BufferedReader reader = new BufferedReader(new StringReader("token1 NN B-NP O\n" + "token2 NN I-NP I-LOC\n" + "\n"));

public String read() throws IOException {
return reader.readLine();
}

public void reset() {
}

public void close() {
}
};
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
assertArrayEquals(new String[] { "token1", "token2" }, sample.getSentence());
Span[] spans = sample.getNames();
assertEquals(1, spans.length);
assertEquals("LOC", spans[0].getType());
assertEquals(1, spans[0].getStart());
assertEquals(2, spans[0].getEnd());
}

@Test
public void testOnlyIEntityTags() throws IOException {
ObjectStream<String> lineStream = new ObjectStream<String>() {

BufferedReader reader = new BufferedReader(new StringReader("Paris NNP B-NP I-LOC\n" + "France NNP I-NP I-LOC\n" + "\n"));

public String read() throws IOException {
return reader.readLine();
}

public void reset() {
}

public void close() {
}
};
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
assertArrayEquals(new String[] { "Paris", "France" }, sample.getSentence());
Span[] names = sample.getNames();
assertEquals(1, names.length);
assertEquals("LOC", names[0].getType());
assertEquals(0, names[0].getStart());
assertEquals(2, names[0].getEnd());
}

@Test
public void testEntityContinuesTillEndWithoutNewline() throws IOException {
ObjectStream<String> lineStream = new ObjectStream<String>() {

BufferedReader reader = new BufferedReader(new StringReader("Angela NNP B-NP B-PER\n" + "Merkel NNP I-NP I-PER"));

public String read() throws IOException {
return reader.readLine();
}

public void reset() {
}

public void close() {
}
};
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
assertArrayEquals(new String[] { "Angela", "Merkel" }, sample.getSentence());
Span[] spans = sample.getNames();
assertEquals(1, spans.length);
assertEquals(0, spans[0].getStart());
assertEquals(2, spans[0].getEnd());
assertEquals("PER", spans[0].getType());
}

@Test
public void testEmptyInputStreamReturnsNull() throws IOException {
ObjectStream<String> lineStream = new ObjectStream<String>() {

public String read() {
return null;
}

public void reset() {
}

public void close() {
}
};
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.DE, lineStream, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);
NameSample sample = stream.read();
assertNull(sample);
}

@Test
public void testValidDOCSTARTHasNoSentenceReturnsNullThenNextIsValid() throws IOException {
ObjectStream<String> lineStream = new ObjectStream<String>() {

BufferedReader reader = new BufferedReader(new StringReader("-DOCSTART- -X- -X- -X- O\n" + "\n" + "\n" + "Angela NNP B-NP B-PER\n" + "\n"));

public String read() throws IOException {
return reader.readLine();
}

public void reset() {
}

public void close() {
}
};
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.DE, lineStream, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);
NameSample first = stream.read();
assertNotNull(first);
assertEquals("Angela", first.getSentence()[0]);
// assertTrue(first.isClearAdaptiveData());
}

@Test
public void testMixedValidAndInvalidTagsSkipsInvalidSpan() throws IOException {
ObjectStream<String> lineStream = new ObjectStream<String>() {

BufferedReader reader = new BufferedReader(new StringReader("Berlin NNP B-NP B-LOC\n" + "Headquarters NNP I-NP I-XXX\n" + "\n"));

public String read() throws IOException {
return reader.readLine();
}

public void reset() {
}

public void close() {
}
};
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES | Conll02NameSampleStream.GENERATE_PERSON_ENTITIES | Conll02NameSampleStream.GENERATE_ORGANIZATION_ENTITIES);
try {
stream.read();
fail("Expected IOException for invalid tag");
} catch (IOException e) {
assertTrue(e.getMessage().contains("Invalid tag"));
}
}

@Test
public void testTagEntityOutsideAllowedFilterIsDropped() throws IOException {
ObjectStream<String> lineStream = new ObjectStream<String>() {

BufferedReader reader = new BufferedReader(new StringReader("Tesla NNP B-NP B-ORG\n" + "\n"));

public String read() throws IOException {
return reader.readLine();
}

public void reset() {
}

public void close() {
}
};
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
assertArrayEquals(new String[] { "Tesla" }, sample.getSentence());
assertEquals(0, sample.getNames().length);
}

@Test
public void testTwoEmptyLinesSkipBlankEvents() throws IOException {
ObjectStream<String> lineStream = new ObjectStream<String>() {

int count = 0;

public String read() {
if (count == 0) {
count++;
return "";
} else if (count == 1) {
count++;
return "";
} else if (count == 2) {
count++;
return "John NNP B-NP B-PER";
} else if (count == 3) {
count++;
return "";
}
return null;
}

public void reset() {
}

public void close() {
}
};
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
assertEquals("John", sample.getSentence()[0]);
assertEquals(1, sample.getNames().length);
assertEquals("PER", sample.getNames()[0].getType());
}

@Test
public void testIncorrectFieldCountForGermanThrows() throws IOException {
ObjectStream<String> lineStream = new ObjectStream<String>() {

BufferedReader reader = new BufferedReader(new StringReader("Berlin lemma1 NN B-NP\n" + "\n"));

public String read() throws IOException {
return reader.readLine();
}

public void reset() {
}

public void close() {
}
};
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.DE, lineStream, Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES);
stream.read();
}

@Test
public void testIEntityTypeDoesNotMatchPriorSpanStartsNewEntity() throws IOException {
ObjectStream<String> lineStream = new ObjectStream<String>() {

BufferedReader reader = new BufferedReader(new StringReader("John NNP B-NP B-PER\n" + "University NNP I-NP I-ORG\n" + "Berlin NNP I-NP I-ORG\n" + "\n"));

public String read() throws IOException {
return reader.readLine();
}

public void reset() {
}

public void close() {
}
};
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES | Conll02NameSampleStream.GENERATE_ORGANIZATION_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
assertArrayEquals(new String[] { "John", "University", "Berlin" }, sample.getSentence());
Span[] names = sample.getNames();
assertEquals(2, names.length);
assertEquals("PER", names[0].getType());
assertEquals(0, names[0].getStart());
assertEquals(1, names[0].getEnd());
assertEquals("ORG", names[1].getType());
assertEquals(1, names[1].getStart());
assertEquals(3, names[1].getEnd());
}

@Test
public void testBTypeDirectlyAfterBTypeCreatesSeparateEntity() throws IOException {
ObjectStream<String> lineStream = new ObjectStream<String>() {

BufferedReader reader = new BufferedReader(new StringReader("Alice NNP B-NP B-PER\n" + "Bob NNP B-NP B-PER\n" + "City NNP B-NP B-LOC\n" + "\n"));

public String read() throws IOException {
return reader.readLine();
}

public void reset() {
}

public void close() {
}
};
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES | Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
assertArrayEquals(new String[] { "Alice", "Bob", "City" }, sample.getSentence());
Span[] spans = sample.getNames();
assertEquals(3, spans.length);
assertEquals("PER", spans[0].getType());
assertEquals(0, spans[0].getStart());
assertEquals(1, spans[0].getEnd());
assertEquals("PER", spans[1].getType());
assertEquals(1, spans[1].getStart());
assertEquals(2, spans[1].getEnd());
assertEquals("LOC", spans[2].getType());
assertEquals(2, spans[2].getStart());
assertEquals(3, spans[2].getEnd());
}

@Test
public void testTaggedSpanAtEndIsCapturedCorrectly() throws IOException {
ObjectStream<String> lineStream = new ObjectStream<String>() {

BufferedReader reader = new BufferedReader(new StringReader("Angela NNP B-NP B-PER\n" + "Merkel NNP I-NP I-PER\n"));

public String read() throws IOException {
return reader.readLine();
}

public void reset() {
}

public void close() {
}
};
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
assertArrayEquals(new String[] { "Angela", "Merkel" }, sample.getSentence());
Span[] spans = sample.getNames();
assertEquals(1, spans.length);
assertEquals("PER", spans[0].getType());
assertEquals(0, spans[0].getStart());
assertEquals(2, spans[0].getEnd());
}

@Test
public void testLineWithWhitespaceOnlyIsConsideredEmpty() throws IOException {
ObjectStream<String> lineStream = new ObjectStream<String>() {

int callCount = 0;

public String read() {
callCount++;
if (callCount == 1)
return "   ";
if (callCount == 2)
return "John NNP B-NP B-PER";
if (callCount == 3)
return "";
return null;
}

public void reset() {
}

public void close() {
}
};
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
assertEquals("John", sample.getSentence()[0]);
assertEquals(1, sample.getNames().length);
}

@Test
public void testSentenceWithSingleBEntityEndsCorrectly() throws IOException {
ObjectStream<String> lineStream = new ObjectStream<String>() {

BufferedReader reader = new BufferedReader(new StringReader("Steve NNP B-NP B-PER\n" + "\n"));

public String read() throws IOException {
return reader.readLine();
}

public void reset() throws IOException {
}

public void close() throws IOException {
}
};
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
assertArrayEquals(new String[] { "Steve" }, sample.getSentence());
Span[] spans = sample.getNames();
assertEquals(1, spans.length);
assertEquals(0, spans[0].getStart());
assertEquals(1, spans[0].getEnd());
assertEquals("PER", spans[0].getType());
}

@Test
public void testSentenceWithMultipleIEntitiesWithoutBStillProducesEntities() throws IOException {
ObjectStream<String> lineStream = new ObjectStream<String>() {

BufferedReader reader = new BufferedReader(new StringReader("Google NNP B-NP I-ORG\n" + "Cloud NNP I-NP I-ORG\n" + "\n"));

public String read() throws IOException {
return reader.readLine();
}

public void reset() throws IOException {
}

public void close() throws IOException {
}
};
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_ORGANIZATION_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
assertEquals("Google", sample.getSentence()[0]);
assertEquals("Cloud", sample.getSentence()[1]);
Span[] spans = sample.getNames();
assertEquals(1, spans.length);
assertEquals(0, spans[0].getStart());
assertEquals(2, spans[0].getEnd());
assertEquals("ORG", spans[0].getType());
}

@Test
public void testSentenceWithIEntitySwitchingTypesCausesSplit() throws IOException {
ObjectStream<String> lineStream = new ObjectStream<String>() {

BufferedReader reader = new BufferedReader(new StringReader("Alice NNP B-NP B-PER\n" + "University NNP I-NP I-ORG\n" + "California NNP I-NP I-ORG\n" + "\n"));

public String read() throws IOException {
return reader.readLine();
}

public void reset() throws IOException {
}

public void close() throws IOException {
}
};
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_ORGANIZATION_ENTITIES | Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
assertArrayEquals(new String[] { "Alice", "University", "California" }, sample.getSentence());
Span[] names = sample.getNames();
assertEquals(2, names.length);
assertEquals("PER", names[0].getType());
assertEquals(0, names[0].getStart());
assertEquals(1, names[0].getEnd());
assertEquals("ORG", names[1].getType());
assertEquals(1, names[1].getStart());
assertEquals(3, names[1].getEnd());
}

@Test
public void testSentenceLineWithTooManyFieldsThrowsIOException_EN() throws IOException {
ObjectStream<String> lineStream = new ObjectStream<String>() {

BufferedReader reader = new BufferedReader(new StringReader("Token MORE THAN FOUR FIELDS EXCEPTION\n" + "\n"));

public String read() throws IOException {
return reader.readLine();
}

public void reset() throws IOException {
}

public void close() throws IOException {
}
};
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);
stream.read();
}

@Test
public void testUnlistedEntityTypeFilteredByFlags() throws IOException {
ObjectStream<String> lineStream = new ObjectStream<String>() {

BufferedReader reader = new BufferedReader(new StringReader("Soccer NNP B-NP B-MISC\n" + "\n"));

public String read() throws IOException {
return reader.readLine();
}

public void reset() throws IOException {
}

public void close() throws IOException {
}
};
int types = Conll02NameSampleStream.GENERATE_PERSON_ENTITIES | Conll02NameSampleStream.GENERATE_ORGANIZATION_ENTITIES;
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, types);
NameSample sample = stream.read();
assertNotNull(sample);
assertEquals(1, sample.getSentence().length);
assertArrayEquals(new String[] { "Soccer" }, sample.getSentence());
Span[] spans = sample.getNames();
assertEquals(0, spans.length);
}

@Test
public void testUnexpectedDocStartInMiddleOfFileClearsAdaptiveData() throws IOException {
ObjectStream<String> lineStream = new ObjectStream<String>() {

int call = 0;

public String read() {
if (call == 0) {
call++;
return "Steve NNP B-NP B-PER";
} else if (call == 1) {
call++;
return "";
} else if (call == 2) {
call++;
return "-DOCSTART- -X- -X- O";
} else if (call == 3) {
call++;
return "";
} else if (call == 4) {
call++;
return "Apple NNP B-NP B-ORG";
} else if (call == 5) {
return "";
}
return null;
}

public void reset() {
}

public void close() {
}
};
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES | Conll02NameSampleStream.GENERATE_ORGANIZATION_ENTITIES);
NameSample first = stream.read();
assertNotNull(first);
assertEquals("Steve", first.getSentence()[0]);
// assertFalse(first.isClearAdaptiveData());
NameSample second = stream.read();
assertNotNull(second);
assertEquals("Apple", second.getSentence()[0]);
// assertTrue(second.isClearAdaptiveData());
}

@Test
public void testBInvalidTypeSuffixThrowsIOException() throws IOException {
ObjectStream<String> lineStream = new ObjectStream<String>() {

public String read() {
return "Name NNP B-NP B-";
}

public void reset() {
}

public void close() {
}
};
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);
try {
stream.read();
fail("Expected IOException");
} catch (IOException e) {
assertTrue(e.getMessage().contains("Invalid tag"));
}
}

@Test
public void testEmptyDocstartFollowedByAnotherDocstartThrows() throws IOException {
ObjectStream<String> lineStream = new ObjectStream<String>() {

int call = 0;

public String read() {
if (call == 0) {
call++;
return "-DOCSTART- -X- -X- -X- O";
} else if (call == 1) {
call++;
return "";
} else if (call == 2) {
return "-DOCSTART- -X- -X- -X- O not-empty";
}
return null;
}

public void reset() {
}

public void close() {
}
};
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.DE, lineStream, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);
try {
stream.read();
stream.read();
fail("Expected IOException");
} catch (IOException e) {
assertTrue(e.getMessage().contains("Empty line after -DOCSTART- not empty"));
}
}

@Test
public void testReadReturnsNullWhenStreamDepleted() throws IOException {
ObjectStream<String> lineStream = new ObjectStream<String>() {

int readCount = 0;

public String read() {
if (readCount == 0) {
readCount++;
return "John NNP B-NP B-PER";
}
return null;
}

public void reset() {
}

public void close() {
}
};
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);
NameSample sample1 = stream.read();
NameSample sample2 = stream.read();
assertNotNull(sample1);
assertNull(sample2);
}

@Test
public void testTagWithoutEntityCodeThrowsException() throws IOException {
ObjectStream<String> lineStream = new ObjectStream<String>() {

public String read() {
return "Token NNP B-NP I-";
}

public void reset() {
}

public void close() {
}
};
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES);
stream.read();
}

@Test
public void testTagBeginningWithIWithoutBStillCreatesSpan() throws IOException {
ObjectStream<String> lineStream = new ObjectStream<String>() {

int call = 0;

public String read() {
if (call == 0) {
call++;
return "OpenAI NNP B-NP I-ORG";
} else if (call == 1) {
call++;
return "Labs NNP I-NP I-ORG";
} else if (call == 2) {
return "";
}
return null;
}

public void reset() {
}

public void close() {
}
};
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_ORGANIZATION_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
assertArrayEquals(new String[] { "OpenAI", "Labs" }, sample.getSentence());
Span[] spans = sample.getNames();
assertEquals(1, spans.length);
assertEquals("ORG", spans[0].getType());
assertEquals(0, spans[0].getStart());
assertEquals(2, spans[0].getEnd());
}

@Test
public void testEntitySpanIsFlushedIfLineEndsWithActiveEntity() throws IOException {
ObjectStream<String> lineStream = new ObjectStream<String>() {

int count = 0;

public String read() {
if (count == 0) {
count++;
return "John NNP B-NP B-PER";
}
if (count == 1) {
count++;
return "Smith NNP I-NP I-PER";
}
return null;
}

public void reset() {
}

public void close() {
}
};
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
assertArrayEquals(new String[] { "John", "Smith" }, sample.getSentence());
Span[] spans = sample.getNames();
assertEquals(1, spans.length);
assertEquals("PER", spans[0].getType());
assertEquals(0, spans[0].getStart());
assertEquals(2, spans[0].getEnd());
}

@Test
public void testTransitionFromIPERToIORGCreatesNewSpan() throws IOException {
ObjectStream<String> lineStream = new ObjectStream<String>() {

int call = 0;

public String read() {
if (call == 0)
return "John NNP B-NP B-PER";
if (call == 1)
return "CEO NNP I-NP I-ORG";
if (call == 2)
return "Company NNP I-NP I-ORG";
if (call == 3)
return "";
return null;
}

public void reset() {
}

public void close() {
}
};
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES | Conll02NameSampleStream.GENERATE_ORGANIZATION_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
assertArrayEquals(new String[] { "John", "CEO", "Company" }, sample.getSentence());
Span[] spans = sample.getNames();
assertEquals(2, spans.length);
assertEquals("PER", spans[0].getType());
assertEquals(0, spans[0].getStart());
assertEquals(1, spans[0].getEnd());
assertEquals("ORG", spans[1].getType());
assertEquals(1, spans[1].getStart());
assertEquals(3, spans[1].getEnd());
}

@Test
public void testHandlesWhitespaceLinesGracefully() throws IOException {
ObjectStream<String> lineStream = new ObjectStream<String>() {

int callCount = 0;

public String read() {
if (callCount == 0) {
callCount++;
return "   ";
}
if (callCount == 1) {
callCount++;
return "John NNP B-NP B-PER";
}
if (callCount == 2)
return "";
return null;
}

public void reset() {
}

public void close() {
}
};
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
assertEquals("John", sample.getSentence()[0]);
assertEquals("PER", sample.getNames()[0].getType());
}

@Test
public void testEntityAtStartIsCapturedAndThenIgnoredWithTagFilter() throws IOException {
ObjectStream<String> lineStream = new ObjectStream<String>() {

public String read() {
return "Bob NNP B-NP B-ORG";
}

public void reset() {
}

public void close() {
}
};
int flags = Conll02NameSampleStream.GENERATE_PERSON_ENTITIES;
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, flags);
NameSample sample = stream.read();
assertNotNull(sample);
assertEquals(1, sample.getSentence().length);
assertEquals("Bob", sample.getSentence()[0]);
assertEquals(0, sample.getNames().length);
}

@Test
public void testMultipleConsecutiveEmptyEventsSkipSuccessfully() throws IOException {
ObjectStream<String> lineStream = new ObjectStream<String>() {

int index = 0;

public String read() {
String[] lines = { "", "", "Alice NNP B-NP B-PER", "", "", null };
return lines[index++];
}

public void reset() {
}

public void close() {
}
};
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
assertArrayEquals(new String[] { "Alice" }, sample.getSentence());
assertEquals(1, sample.getNames().length);
}

@Test
public void testSentenceWithEscapedCharactersParsedProperly() throws IOException {
ObjectStream<String> lineStream = new ObjectStream<String>() {

StringReader stringReader = new StringReader("O\\'Connor NNP B-NP B-PER\n" + "\n");

BufferedReader reader = new BufferedReader(stringReader);

public String read() throws IOException {
return reader.readLine();
}

public void reset() throws IOException {
}

public void close() throws IOException {
reader.close();
}
};
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
assertArrayEquals(new String[] { "O\\'Connor" }, sample.getSentence());
assertEquals(1, sample.getNames().length);
assertEquals(0, sample.getNames()[0].getStart());
assertEquals(1, sample.getNames()[0].getEnd());
assertEquals("PER", sample.getNames()[0].getType());
}

@Test
public void testEntityWithNumericToken() throws IOException {
ObjectStream<String> lineStream = new ObjectStream<String>() {

StringReader r = new StringReader("2024 NNP B-NP B-MISC\n" + "\n");

BufferedReader reader = new BufferedReader(r);

public String read() throws IOException {
return reader.readLine();
}

public void reset() throws IOException {
}

public void close() throws IOException {
reader.close();
}
};
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_MISC_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
assertArrayEquals(new String[] { "2024" }, sample.getSentence());
assertEquals(1, sample.getNames().length);
assertEquals("MISC", sample.getNames()[0].getType());
}

@Test
public void testBackToBackSentenceEntitiesHandledIndependently() throws IOException {
ObjectStream<String> lineStream = new ObjectStream<String>() {

StringReader r = new StringReader("London NNP B-NP B-LOC\n" + "\n" + "John NNP B-NP B-PER\n" + "\n");

BufferedReader reader = new BufferedReader(r);

public String read() throws IOException {
return reader.readLine();
}

public void reset() throws IOException {
}

public void close() throws IOException {
reader.close();
}
};
int types = Conll02NameSampleStream.GENERATE_PERSON_ENTITIES | Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES;
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, types);
NameSample sample1 = stream.read();
NameSample sample2 = stream.read();
assertNotNull(sample1);
assertEquals("London", sample1.getSentence()[0]);
assertEquals("LOC", sample1.getNames()[0].getType());
assertNotNull(sample2);
assertEquals("John", sample2.getSentence()[0]);
assertEquals("PER", sample2.getNames()[0].getType());
}

@Test
public void testInvalidSpanInterruptedByMisalignedTagType() throws IOException {
ObjectStream<String> lineStream = new ObjectStream<String>() {

BufferedReader reader = new BufferedReader(new StringReader("Barack NNP B-NP B-PER\n" + "Obama NNP I-NP I-ORG\n" + "\n"));

public String read() throws IOException {
return reader.readLine();
}

public void reset() throws IOException {
}

public void close() throws IOException {
reader.close();
}
};
int types = Conll02NameSampleStream.GENERATE_PERSON_ENTITIES | Conll02NameSampleStream.GENERATE_ORGANIZATION_ENTITIES;
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, types);
NameSample sample = stream.read();
assertNotNull(sample);
assertArrayEquals(new String[] { "Barack", "Obama" }, sample.getSentence());
Span[] spans = sample.getNames();
assertEquals(2, spans.length);
assertEquals("PER", spans[0].getType());
assertEquals("ORG", spans[1].getType());
}

@Test
public void testFinalSentenceWithoutTrailingNewline() throws IOException {
ObjectStream<String> lineStream = new ObjectStream<String>() {

String input = "Einstein NNP B-NP B-PER\n" + "";

BufferedReader reader = new BufferedReader(new StringReader(input));

public String read() throws IOException {
return reader.readLine();
}

public void reset() throws IOException {
}

public void close() throws IOException {
reader.close();
}
};
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
assertArrayEquals(new String[] { "Einstein" }, sample.getSentence());
assertEquals(1, sample.getNames().length);
assertEquals("PER", sample.getNames()[0].getType());
}

@Test
public void testBTagNotFollowedByINullifiesPendingSpan() throws IOException {
ObjectStream<String> lineStream = new ObjectStream<String>() {

BufferedReader reader = new BufferedReader(new StringReader("Berlin NNP B-NP B-LOC\n" + "visits NNP B-VP O\n" + "\n"));

public String read() throws IOException {
return reader.readLine();
}

public void reset() throws IOException {
}

public void close() throws IOException {
reader.close();
}
};
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES);
NameSample sample = stream.read();
assertNotNull(sample);
assertArrayEquals(new String[] { "Berlin", "visits" }, sample.getSentence());
Span[] spans = sample.getNames();
assertEquals(1, spans.length);
assertEquals("LOC", spans[0].getType());
assertEquals(0, spans[0].getStart());
assertEquals(1, spans[0].getEnd());
}

@Test
public void testGermanLanguageWithTooFewFieldsThrowsException() throws IOException {
ObjectStream<String> lineStream = new ObjectStream<String>() {

String line = "Berlin LEM POS SC";

BufferedReader reader = new BufferedReader(new StringReader(line));

public String read() throws IOException {
return reader.readLine();
}

public void reset() throws IOException {
}

public void close() throws IOException {
}
};
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.DE, lineStream, Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES);
stream.read();
}

@Test
public void testEnglishLanguageWithFiveFieldsThrowsException() throws IOException {
ObjectStream<String> lineStream = new ObjectStream<String>() {

String line = "Steve NN B-NP B-PER EXTRA";

BufferedReader reader = new BufferedReader(new StringReader(line));

public String read() throws IOException {
return reader.readLine();
}

public void reset() throws IOException {
}

public void close() throws IOException {
}
};
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);
stream.read();
}
}
